/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'../collections/ctaFavorites',
	'./route',
	'layoutmanager'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, CtaFavoritesCollection, RouteView) {
	'use strict';
	
	var FavoriteView = Backbone.View.extend({
		manage: true,
		template: JST['app/scripts/templates/favorite.hbs'],
		initialize: function(){
			var self = this;
			self.favoritesCollection = CtaFavoritesCollection;
			self.favoritesCollection.fetch({
				success: function(data){
					self.render();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});
			self.favoritesCollection.on("change reset add remove", function() {
		        self.render();
		    });
		},
		removeFavorite: function(serviceId){
			if(typeof(Storage)!=="undefined"){
				var favorite = this.favoritesCollection.findWhere({
					serviceId: serviceId
				});
				favorite.destroy();
				this.favoritesCollection.remove(favorite);
				this.render();
				RouteView.render();
			}
		},
		beforeRender: function(){
			$(".favorites").empty().append(this.el);
		},
		afterRender: function(){
			var self = this;
			$(".trash").click(function(event){
				self.removeFavorite(event.currentTarget.id);
			});

			$(".favoriteRoute").click(function(event){
				var serviceId = event.currentTarget.classList[1];
				console.log(serviceId);
				RouteView.setRoute(serviceId);
			});
		},
		serialize: function(){
			return {
				favorites: this.favoritesCollection.toJSON()
			}
		}
	});

	return FavoriteView;
});