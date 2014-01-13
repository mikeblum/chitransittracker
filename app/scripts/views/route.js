/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'moment',
	'../collections/ctaArrivals'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Moment, CtaArrivalsCollection) {
	'use strict';

	var RouteView = Backbone.View.extend({
		template: JST['app/scripts/templates/route.hbs'],
		initialize: function(route){
			var self = this;
			self.arrivals = {};
			self.route = route;
		},
		setRoute: function(route){
			var self = this;
			self.route = route;
			$('.spinner').show();
			self.arrivalsCollection = new CtaArrivalsCollection([], {
				url: 'arrivals?stop=' + self.route.serviceId
			}).fetch({
				success: function(data){
					self.arrivals = data.toJSON();
					self.render();
					$('.spinner').hide();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});
		},
		convertDate: function(ctaDate){
			return Moment(ctaDate, 'YYYYMMDD hh:mm:ss');
		},
		render: function(){
			var self = this;
			$('#route').html(self.template(self.route));
			if(self.arrivals){
				var source = JST['app/scripts/templates/arrivals.hbs'];
				var arrivalsTemplate = {
					routeColor: self.route.routeColorCode,
					arrivals: []
				};
				_.each(self.arrivals, function(arrival){
					arrivalsTemplate.arrivals.push({
						destination: arrival.destNm,
						computedTime: self.convertDate(arrival.arrT).diff(self.convertDate(arrival.prdt), 'minutes'),
						approaching: arrival.isApp === '1' ? '1' : ''
					});
				});
				arrivalsTemplate.arrivals = arrivalsTemplate.arrivals.sort(function(a,b){
					if(a.destination < b.destination) return -1;
				    if(a.destination > b.destination) return 1;
				    return 0;
				});
				$('#arrivals').html(source(arrivalsTemplate));
			}
			return self;
		}
	});

	return RouteView;
});