/*global define*/

define([
	'jquery',
	'backbone',
	'../views/trainstatus',
	'../views/notification'
], function ($, Backbone, TrainStatusView, NotificationView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		trainStatus: function(){
	        this.trainStatusView = new TrainStatusView();
	       	this.trainStatusView.render();
		},
		notifications: function(){
	        this.notificationView = new NotificationView();
	       	this.notificationView.render();
		},
		root: function(){
			this.trainStatus();
			this.notifications();
		}
	});

	return AppRouter;
});