/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'../collections/ctaFavorites'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, CtaFavoritesCollection) {
	'use strict';
	
	var FavoriteView = Backbone.View.extend({
		template: JST['app/scripts/templates/favorite.hbs'],
		initialize: function(){
			var self = this;
			self.favoritesCollection = new CtaFavoritesCollection().fetch({
				success: function(data){
					self.favorites = data.toJSON();
					console.log(self.favorites);
					self.render();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});
			
		},
		render: function(){
			var self = this;
			
			return self;
		}
	});

	return FavoriteView;
});