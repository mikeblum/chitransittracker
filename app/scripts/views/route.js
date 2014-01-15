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

			self.saveRoute = function(){
				if(typeof(Storage)!=="undefined"){
					var favorites = localStorage.getItem('favorites');
					if(favorites === null){
						favorites = [];
					}else{
						favorites = JSON.parse(favorites);
					}
					favorites.push(self.route.serviceId);
					console.log(favorites);
					localStorage.setItem('favorites', JSON.stringify(favorites));
				}
			};
			
			self.removeRoute = function(){
				if(typeof(Storage)!=="undefined"){
					var favorites = localStorage.getItem('favorites');
					if(favorites === null && favorites !== undefined && favorites.length > 0){
						favorites = JSON.parse(favorites);
						favorites = _.without(favorites, self.route.serviceId);
						console.log(favorites);
						localStorage.setItem('favorites', JSON.stringify(favorites));
					}
				}
			};

			Handlebars.registerHelper('saveFavorite', function(){
				if(event.type == "mousedown"){
					if(event.target.getAttribute('fill') === 'none'){
						self.saveRoute();
						event.target.setAttribute("fill", "#ffd400");
					}else{
						self.removeRoute();
						event.target.setAttribute("fill", "none");
					}
				}
			});
		},
		setRoute: function(route){
			var self = this;
			self.route = route;

			$('#arrivalsSpinner').show();
			self.arrivalsCollection = new CtaArrivalsCollection([], {
				url: 'arrivals?stop=' + self.route.serviceId
			}).fetch({
				success: function(data){
					self.arrivals = data.toJSON();
					self.render();
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
			if(typeof(Storage)!=="undefined"){
				var favorites = localStorage.getItem('favorites');
				if(favorites === null){
					self.route.favorite = "none";
				}else{
					favorites = JSON.parse(favorites);
					if(_.indexOf(favorites, self.route.serviceId) > -1){
						self.route.favorite = "#ffd400";
					}else{
						self.route.favorite = "none";
					}
				}
			}
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

				setTimeout(function() {
					$('#arrivalsSpinner').fadeOut('fast');
			}, 1000);
			}
			return self;
		}
	});

	return RouteView;
});