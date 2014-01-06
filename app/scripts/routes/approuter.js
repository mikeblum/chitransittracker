/*global define*/

define([
	'jquery',
	'backbone',
	'../views/routes'
], function ($, Backbone, RoutesView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		root: function(){
	       	this.routesView = new RoutesView();
	       	this.routesView.render();
		}
	});

	return AppRouter;
});