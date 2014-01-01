/*global define*/

define([
	'underscore',
	'backbone'
], function (_, Backbone) {
	'use strict';

	var TrainStatusModel = Backbone.Model.extend({
		defaults: {
			status: 'Unavalible'
		}
	});

	return TrainStatusModel;
});
