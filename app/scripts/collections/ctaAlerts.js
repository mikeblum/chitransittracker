/*global define*/

define([
    'underscore',
    'backbone',
    'models/ctaAlert'
], function (_, Backbone, CtaAlertModel) {
    'use strict';

    var CtaAlertsCollection = Backbone.Collection.extend({
        model: CtaAlertModel,
        url: 'localhost:9000/alerts'
    });

    return CtaAlertsCollection;
});
