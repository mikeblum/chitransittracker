/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates',
	'../collections/trainstatus'
], function ($, _, Backbone, JST, TrainStatusCollection) {
	'use strict';

	var TrainStatusView = Backbone.View.extend({
		template: JST['app/scripts/templates/trainstatus.hbs'],
		initialize: function(){
			var self = this;

			self.trainStatus = new TrainStatusCollection();

			// self.trainStatus.fetch({
			// 	dataType: 'jsonp xml',
			// 	success: function(data){
			// 		console.log('success!!' + data);
			// 	},
			// 	error: function(){
			// 		console.log('Failed to get CTA status');
			// 	}
			// });

			$.ajax({
		        url: 'http://www.transitchicago.com/api/1.0/routes.aspx',
		        type: 'get',
		        dataType: 'xml',
		        async: false,
		        success: function(data) {
		            console.log(data);
		        },
		        error: function(){
		        	console.log("Failed to load CTA route status");
		        }
		    });
		},
		render: function(){
	      $('#train-status').html(this.template());
	      return this;
	    }
	});

	return TrainStatusView;
});