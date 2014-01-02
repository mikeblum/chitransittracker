/*global define*/

define([
    'underscore',
    'backbone',
    'models/trainstatus'
], function (_, Backbone, TrainStatusModel) {
    'use strict';

    var TrainStatusCollection = Backbone.Collection.extend({
        model: TrainStatusModel,
        url: 'http://www.transitchicago.com/api/1.0/routes.aspx',
        parse: function(response) {
			return response;
		}
    });

    return TrainStatusCollection;
});
