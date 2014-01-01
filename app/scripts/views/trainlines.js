/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates',
	'../collections/trainlines'
], function ($, _, Backbone, JST, TrainLineCollection) {
	'use strict';

	var TrainLinesView = Backbone.View.extend({
		template: JST['app/scripts/templates/trainlines.hbs'],
		initialize: function(){
			var self = this;
			self.trainStatus = new TrainLineCollection();

			self.trainStatus.fetch({
				dataType: 'jsonp xml',
				success: function(data){
					console.log('success!!' + data);
				},
				error: function(){
					console.log('Failed to get CTA status');
				}
			});
		}
	});

	return TrainLinesView;
});
