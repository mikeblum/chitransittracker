/*global define*/

define([
	'jquery',
	'backbone',
	'../views/trainstatus',
	'../views/detailedalert'
], function ($, Backbone, TrainStatusView, DetailedAlertView) {
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
		detailedAlerts: function(){
	        this.detailedAlertView = new DetailedAlertView();
	       	this.detailedAlertView.render();
		},
		root: function(){
			this.trainStatus();
			this.detailedAlerts();
		}
	});

	return AppRouter;
});