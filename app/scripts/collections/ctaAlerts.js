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
        icon: 'images/signal.png',
        parse: function(data){
        	return data.CTAAlerts.Alert;
        }
    });

    return CtaAlertsCollection;
});
