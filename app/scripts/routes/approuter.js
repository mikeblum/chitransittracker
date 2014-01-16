/*global define*/

define([
	'jquery',
	'backbone',
	'../views/search',
	'../views/favorite'
], function ($, Backbone, SearchView, FavoriteView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		root: function(){
			this.favoriteView = new FavoriteView();
			this.favoriteView.render();
	       	this.SearchView = new SearchView();
	       	this.SearchView.render();
		}
	});

	return AppRouter;
});