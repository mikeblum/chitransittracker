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
			var self = this;
			self.route = route;
			self.favorites = CtaFavoritesCollection;
			self.arrivals = new Arrivals();
		},
		setRoute: function(route){
			var self = this;
			$('#arrivalsSpinner').show();
			self.route = route;
			self.arrivals.setRoute(self.route);
			self.arrivals.refresh(self.route.serviceId);
			self.render();
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
			console.log('route rendered');
			$("#route").empty().append(this.el);
			return self;
		},
		afterRender: function(){
			var self = this;
			$( ".favorite" ).click(function(){
			 	self.favoriteRoute();
			});
		},
		serialize: function(){
			var self = this;
			return self.route;
		},
		// render: function(){
		// 	$("#route").html(this.template(this.route));
		// }
	});

	return new RouteView();
});