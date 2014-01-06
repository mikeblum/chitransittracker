/*global define*/

define([
    'underscore',
    'backbone',
    'models/ctaAlert'
], function (_, Backbone, CtaAlertModel) {
    'use strict';

    var CtaAlertsCollection = Backbone.Collection.extend({
        model: CtaAlertModel,
        url: 'alerts',
        parse: function(data){
        	return data.CTAAlerts.Alert;
        }
    });

    return CtaAlertsCollection;
});
