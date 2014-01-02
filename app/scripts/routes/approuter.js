/*global define*/

define([
	'jquery',
	'backbone',
	'../collections/trainstatus',
	'../views/trainstatus'
], function ($, Backbone, TrainStatusCollection, TrainStatusView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'trainStatus'
		},
		trainStatus: function(){
			this.trainStatus = new TrainStatusCollection();
	        this.trainStatusView = new TrainStatusView({model:this.trainStatus});
	       	this.trainStatusView.render();
		}
	});

	return AppRouter;
});