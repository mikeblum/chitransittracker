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
		getRailColor: function(route){
			var rt = route.toLowerCase();
			if(rt === 'red'){
				return 'c60c30';
			}else if(rt === 'blue'){
				return '00a1de';
			}else if(rt === 'brn'){
				return '62361b';
			}else if(rt === 'g'){
				return '009b3a';
			}else if(rt === 'org'){
				return 'f9461c';
			}else if(rt === 'pexp' || rt === 'p'){
				return '522398';
			}else if(rt === 'pink'){
				return 'e27ea6';
			}else if(rt === 'y'){
				return 'f9e300';
			}
		},
		setRoute: function(route){
			this.route = route;
		},
		refresh: function(route, isBusRoute){
			var self = this;
			$('#arrivalsSpinner').show();
			this.$('#arrivalsTable').hide();
			this.$('.arrivalsFooter').hide();

			var serviceId;

			if(isBusRoute){
				serviceId = route.stpid;
			}else{
				serviceId = route.serviceId;
			}

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
										computedTime: self.convertDate(arrival.prdtm).diff(self.convertDate(arrival.tmstmp), 'minutes'),
										routeColorCode: '059',
										routeDir: arrival.rtdir,
										busArrivals: true
									});
								}else{
									_.each(self.arrivalsTable[0].prd, function(arrival){
										self.arrivals.push({
											destination: arrival.des,
											computedTime: self.convertDate(arrival.prdtm).diff(self.convertDate(arrival.tmstmp), 'minutes'),
											routeColorCode: '059',
											routeDir: arrival.rtdir,
											busArrivals: true
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
									approaching: arrival.isApp === '1' ? '1' : '',
									routeColorCode: self.getRailColor(arrival.rt)
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
				self.$('#arrivalsTable').show();
				self.$('.arrivalsFooter').show();
			});
		},
		beforeRender: function(){
			$("#arrivals").empty().append(this.el);
		},
		afterRender: function(){
			var self = this;
			this.$('.arrivalsFooter').click(function(){
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
