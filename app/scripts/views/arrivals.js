/*global define*/

define([
    'jquery',
    'underscore',
    'backbone',
   	'moment',
    'templates',
    '../collections/ctaArrivals',
   	'layoutmanager'
], function ($, _, Backbone, Moment, JST, ctaArrivals) {
    'use strict';

    var ArrivalsView = Backbone.Layout.extend({
        template: JST['app/scripts/templates/arrivals.hbs'],
        initialize: function(){
        	var self = this;
        	self.arrivalsCollection = new ctaArrivals();
        	self.arrivals = [];
        },
        convertDate: function(ctaDate){
			return Moment(ctaDate, 'YYYYMMDD hh:mm:ss');
		},
		setRoute: function(route){
			this.route = route;
		},
		refresh: function(serviceId, isBusRoute){
			var self = this;
			$('#arrivalsSpinner').show();

			self.arrivalsCollection.url = 'arrivals?stop=' + serviceId + '&type=' + this.route.type;
			self.arrivalsCollection.type = this.route.type;
			self.arrivalsCollection.fetch({
				success: function(data){
					self.arrivalsTable = data.toJSON();
					self.arrivals = [];
					if(self.route.type === 'bus' && isBusRoute){
						self.isBusRoute = isBusRoute;
						//update the service id to be the bus stop
						self.route.serviceId = serviceId;
						if(self.arrivalsTable[0].error){
							self.error = self.arrivalsTable[0].error.msg;
						}else{ //get arrival times
							self.error = false;
							if(self.arrivalsTable[0].prd){
								if(self.arrivalsTable[0].prd.stpnm){ //only one arrival - .hack
									var arrival = self.arrivalsTable[0].prd;
									self.arrivals.push({
										destination: arrival.des,
										computedTime: self.convertDate(arrival.prdtm).diff(self.convertDate(arrival.tmstmp), 'minutes')
									});
								}else{
									_.each(self.arrivalsTable[0].prd, function(arrival){
										self.arrivals.push({
											destination: arrival.des,
											computedTime: self.convertDate(arrival.prdtm).diff(self.convertDate(arrival.tmstmp), 'minutes')
										});
									});
								}
							}
						}
					}else if(self.route.type === 'rail'){
						if(!self.arrivalsTable || self.arrivalsTable.length === 0){
							self.error = 'Arrival times not available';
						}else{
							self.error = false;
							_.each(self.arrivalsTable, function(arrival){
								self.arrivals.push({
									destination: arrival.destNm,
									computedTime: self.convertDate(arrival.arrT).diff(self.convertDate(arrival.prdt), 'minutes'),
									approaching: arrival.isApp === '1' ? '1' : ''
								});
							});
						}
					}
					self.arrivals = self.arrivals.sort(function(a,b){
						if(a.destination < b.destination) return -1;
						if(a.destination > b.destination) return 1;
						return 0;
					});
					self.render();
				},
				error: function(collection, response, options){
					self.error = 'Arrival times not available';
				}
			}).complete(function(){
				setTimeout(function() {
				    $('#arrivalsSpinner').fadeOut('fast');
				}, 1000);
			});
		},
		beforeRender: function(){
			$("#arrivals").empty().append(this.el);
		},
		afterRender: function(){
			var self = this;
			$('.arrivalsFooter').click(function(){
				self.refresh(self.route.serviceId, self.isBusRoute);
			});
		},
		serialize: function(){
			return {
				route: this.route,
				arrivals: this.arrivals,
				error: this.error
			};
		}
    });

    return ArrivalsView;
});
