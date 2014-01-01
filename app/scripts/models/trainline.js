/*global define*/

define([
	'underscore',
	'backbone'
], function (_, Backbone) {
	'use strict';

	var TrainLineModel = Backbone.Model.extend({
		defaults: {
			status: 'Unavalible'
		}
	});

	return TrainLineModel;
});
