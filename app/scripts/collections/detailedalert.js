/*global define*/

define([
    'underscore',
    'backbone',
    'models/detailedalert'
], function (_, Backbone, DetailedAlertModel) {
    'use strict';

    var DetailedAlertCollection = Backbone.Collection.extend({
        model: DetailedAlertModel,
        url: 'http://www.transitchicago.com/api/1.0/alerts.aspx',
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

    return DetailedAlertCollection;
});
