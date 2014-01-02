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
        parse: function (data) {
            var parsed = [];
            // $(data).find('book').each(function (index) {
            //     var bookTitle = $(this).find('name').text();
            //     parsed.push({title: bookTitle});
            // });

            return parsed;
        },

        fetch: function (options) {
            options = options || {};
            options.dataType = "xml";
            return Backbone.Collection.prototype.fetch.call(this, options);
        }
    });

    return TrainStatusCollection;
});
