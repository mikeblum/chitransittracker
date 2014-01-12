/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtaAlertsCollection = Backbone.Collection.extend({
        url: 'alerts',
        parse: function(data){
        	return data.CTAAlerts.Alert;
        }
    });

    return CtaAlertsCollection;
});
