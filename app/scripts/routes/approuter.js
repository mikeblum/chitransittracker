/*global define*/

define([
	'jquery',
	'backbone',
	'../collections/trainlines',
	'../views/trainlines'
], function ($, Backbone, TrainLineCollection, TrainLinesView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'trainStatus'
		},
		trainStatus: function(){
			this.trainStatus = new TrainLineCollection();
	        this.trainLinesView = new TrainLinesView({model:this.trainStatus});
	        $('#train-status').html(this.trainLinesView.render());
		}
	});

	return AppRouter;
});