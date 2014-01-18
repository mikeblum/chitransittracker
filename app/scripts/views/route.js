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
		manage: true,
		template: JST['app/scripts/templates/route.hbs'],
		initialize: function(route){
			var self = this;
			self.arrivals = {};
			self.route = route;
		},
		setRoute: function(route){
			var self = this;
			self.route = route;

			$('#arrivalsSpinner').show();
			self.arrivalsCollection = new CtaArrivalsCollection();
			self.arrivalsCollection.url = 'arrivals?stop=' + self.route.serviceId;
			self.arrivalsCollection.fetch({
				success: function(data){
					self.arrivals = data.toJSON();
					self.render();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});
		},
		saveFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				var favorites = localStorage.getItem('chitransittracker-favorites');
				if(favorites === null){
					favorites = [];
				}else{
					favorites = JSON.parse(favorites);
				}
				var fav = {
					route: '{{ route }}',
					serviceId: '{{ serviceId }}'
				};
				favorites.push(fav);
				localStorage.setItem('chitransittracker-favorites', JSON.stringify(favorites));
			}
		},
		removeFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				var favorites = localStorage.getItem('chitransittracker-favorites');
				if(favorites === null){
					return;
				}else{
					favorites = JSON.parse(favorites);
					_.each(favorites, function(fav, index){
						if(fav.serviceId === '{{ serviceId }}'){
							favorites.splice(index, 1);
						}
					});
					localStorage.setItem('chitransittracker-favorites', JSON.stringify(favorites));
				}
			}
		},
		favoriteRoute: function(evt)
		{
			if(evt.target.getAttribute('fill') === 'none'){
				saveFavorite();
				evt.target.setAttribute("fill", "#ffd400");
			}else{
				removeFavorite();
				evt.target.setAttribute("fill", "none");
			}
		},
		convertDate: function(ctaDate){
			return Moment(ctaDate, 'YYYYMMDD hh:mm:ss');
		},
		beforeRender: function(){
			var self = this;
			if(self.route){
				if(typeof(Storage)!=="undefined"){
					var favorites = localStorage.getItem('chitransittracker-favorites');
					if(favorites === null){
						self.route.favorite = "none";
					}else{
						var favorited = false;
						favorites = JSON.parse(favorites);
						_.map(favorites, function(fav){
							if(fav.serviceId === self.route.serviceId){
								self.route.favorite = "#ffd400";
								favorited = true;
							}
						});
						if(!favorited){
							self.route.favorite = "none";
						}
					}
				}
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
					$('.arrivals').html(source(arrivalsTemplate));

					setTimeout(function() {
						$('#arrivalsSpinner').fadeOut('fast');
					}, 1000);
				}
			}
			$(".route").empty().append(self.el);
			return self;
		},
		afterRender: function(){
			var self = this;
			$( ".favorite" ).click(function() {
			  self.favoriteRoute();
			});
		},
		serialize: function(){
			var self = this;
			return self.route;
		}
	});

	return RouteView;
});