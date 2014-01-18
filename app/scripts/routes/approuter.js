/*global define*/

define([
	'jquery',
	'backbone',
	'../views/search',
	'../views/favorite',
	'../views/status',
	'../views/route',
	'layoutmanager'
], function ($, Backbone, SearchView, FavoriteView, StatusView, RouteView){
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		root: function(){
			this.SearchView = new SearchView();
			var status = new StatusView();
			$(".routeStatus").empty().append(status.el);
			// Render the layout.
			status.render();

			var route = new RouteView();
			$(".route").empty().append(route.el);

			var favorites = new FavoriteView();
			$(".favorites").empty().append(favorites.el);
			favorites.render();
		}
	});

	return AppRouter;
});