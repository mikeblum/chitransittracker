/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'../collections/ctaFavorites',
	'../models/CtaFavorite',
	'../models/ctaRoute',
	'./arrivals',
	'layoutmanager'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, CtaFavoritesCollection, CtaFavorite, CtaRoute, Arrivals){
	'use strict';

	var RouteView = Backbone.Layout.extend({
		template: JST['app/scripts/templates/route.hbs'],
		initialize: function(route){
			this.route = route;
			this.favorites = CtaFavoritesCollection;
			this.arrivals = new Arrivals();
		},
		setRoute: function(route){
			this.route = route;
			this.arrivals.setRoute(this.route);
			if(this.route.type === 'bus'){
				this.route.busRoute = true;
			}else{
				this.route.busRoute = false;
			}
			this.arrivals.refresh(this.route, this.route.type === 'bus');
			this.render();
		},
		saveFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				var favorite = new CtaFavorite(
					this.route
				);
				this.favorites.fetch();
				this.favorites.add(favorite);
				favorite.save();
			}
		},
		removeFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				this.favorites.fetch();
				var favorite = this.favorites.findWhere({
					serviceId: this.route.serviceId
				});
				favorite.destroy();
				this.favorites.remove(favorite);
			}
		},
		favoriteRoute: function()
		{
			if($( ".favorite" ).css('fill') === 'none'){
				this.saveFavorite();
				$( ".favorite" ).css({fill: "#ffd400"});
			}else{
				this.removeFavorite();
				$( ".favorite" ).css({fill: "none"});
			}
		},
		beforeRender: function(){
			var self = this;
			if(self.route){
				if(typeof(Storage)!=="undefined"){
					this.favorites.fetch();
					var favRoute = this.favorites.findWhere({
						route: this.route.route,
						serviceId: this.route.serviceId
					});
					if(!favRoute){
						self.route.favorite = "none";
					}else{
						self.route.favorite = "#ffd400";
					}
				}
			}
			$("#route").empty().append(this.el);
			return self;
		},
		afterRender: function(){
			var self = this;

			var busStopTemplate = JST['app/scripts/templates/busRoute.hbs'];

			this.$('.favorite').click(function(){
				self.favoriteRoute();
			});

			this.$('.busStopsTypeahead.typeahead').typeahead({
				limit: 10,
				remote: {
					url: 'busStops?query=%QUERY&serviceId=' + this.route.serviceId,
					filter: function(data){
						var results = [];
						_.each(data, function(stop){
							results.push({
								value: stop.stpnm,
								tokens: [ stop.stpnm, stop.stpid ],
								latitude: stop.lat,
								longitude: stop.lon,
								stopNumber: stop.stpid
							});
						});
						return results;
					}
				},
				template: busStopTemplate
			}).on('typeahead:selected ', function (obj, datum){
				datum.type = 'bus';
				self.route.stopName = datum.value;
				self.route.busRoute = true;
				datum.routeColorCode = '059';
				datum.serviceId = self.route.serviceId;
				self.arrivals.refresh(datum, true);
				self.render();
			   	$('.busStopsTypeahead.typeahead').trigger('blur');
			});
		},
		serialize: function(){
			return this.route;
		}
	});

	return new RouteView();
});