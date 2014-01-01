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
			$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
			    options.crossDomain ={
			        crossDomain: true
			    };
			    options.xhrFields = {
			        withCredentials: true
			    };
			});

			var allowCrossDomain = function(req, res, next) {
			    res.header('Access-Control-Allow-Origin', config.allowedDomains);
			    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
			    res.header('Access-Control-Allow-Headers', 'Content-Type');

			    next();
			}
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