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
			this.trainStatus = new TrainStatusCollection();
	        this.trainStatusView = new TrainStatusView({model:this.trainStatus});
	       	this.trainStatusView.render();
		}
	});

	return AppRouter;
});