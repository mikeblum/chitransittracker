/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates'
], function ($, _, Backbone, JST) {
	'use strict';

	var TrainStatusView = Backbone.View.extend({
		template: JST['app/scripts/templates/trainstatus.hbs'],
		initialize: function(){
			var self = this;
		},
		render: function(){
	      $('#train-status').html(this.template());
	      return this;
	    }
	});

	return TrainStatusView;
});