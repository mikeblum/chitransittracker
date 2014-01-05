/*global define*/

define([
    'underscore',
    'backbone',
    'models/ctaRoute'
], function (_, Backbone, CtaRouteModel) {
    'use strict';

    var CtaRoutesCollection = Backbone.Collection.extend({
        model: CtaRouteModel,
        url: 'routes',
        parse: function(data){
        	return data.CTARoutes.RouteInfo;
        }
    });

    return CtaRoutesCollection;
});
