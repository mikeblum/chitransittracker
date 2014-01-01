/*global define*/

define([
    'underscore',
    'backbone',
    'models/trainline'
], function (_, Backbone, TrainLineModel) {
    'use strict';

    var TrainLineCollection = Backbone.Collection.extend({
        model: TrainLineModel,
        url: 'http://www.transitchicago.com/api/1.0/routes.aspx',
        parse: function(response) {
			return response;
		}
    });

    return TrainLineCollection;
});
