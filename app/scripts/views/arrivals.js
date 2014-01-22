/*global define*/

define([
    'jquery',
    'underscore',
    'backbone',
   	'moment',
    'templates',
    '../collections/ctaArrivals'
], function ($, _, Backbone, Moment, JST, ctaArrivals) {
    'use strict';

    var ArrivalsView = Backbone.View.extend({
    	manage: true,
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
		refresh: function(serviceId){
			$('#arrivalsSpinner').show();
			var self = this;
			self.arrivalsCollection.url = 'arrivals?stop=' + serviceId;
			self.arrivalsCollection.fetch({
				success: function(data){
					self.arrivalsTable = data.toJSON();
					self.arrivals = [];
					_.each(self.arrivalsTable, function(arrival){
						self.arrivals.push({
							destination: arrival.destNm,
							computedTime: self.convertDate(arrival.arrT).diff(self.convertDate(arrival.prdt), 'minutes'),
							approaching: arrival.isApp === '1' ? '1' : ''
						});
					});
					self.arrivals = self.arrivals.sort(function(a,b){
						if(a.destination < b.destination) return -1;
						if(a.destination > b.destination) return 1;
						return 0;
					});
					console.log(self.arrivals);
					self.render();
				},
				error: function(collection, response, options){
					console.log('error: ' + JSON.stringify(response));
				},
				complete: function(){
					setTimeout(function() {
                    	$('#arrivalsSpinner').fadeOut('fast');
                    }, 1000);
				}
			});
		},
		beforeRender: function(){
			$("#arrivals").empty().append(this.el);
		},
		afterRender: function(){
			var self = this;
			$('.arrivalsFooter').click(function(){
				self.refresh(self.route.serviceId);
			});
		},
		serialize: function(){
			return {
				route: this.route,
				arrivals: this.arrivals
			};
		}
    });

    return ArrivalsView;
});
