/*global define*/

define([
	'jquery',
	'backbone',
	'../views/routes',
	'../views/notifications'
], function ($, Backbone, RoutesView, NotificationsView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		root: function(){
			this.notificationsView = new NotificationsView();
	       	this.notificationsView.render();

	       	this.routesView = new RoutesView();
	       	this.routesView.render();
		}
	});

	return AppRouter;
});