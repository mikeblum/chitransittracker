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
			var search = new SearchView();
			$("#search").empty().append(search.el);
			search.render();

			var status = new StatusView();
			$(".routeStatus").empty().append(status.el);
			// Render the layout.
			status.render();

			var route = RouteView;
			$("#route").empty().append(route.el);

			var favorites = new FavoriteView();
		}
	});

	return AppRouter;
});