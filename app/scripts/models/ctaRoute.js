/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtaRouteModel = Backbone.Model.extend({
        defaults:{
			'Route': '',
			'RouteColorCode': '',
			'RouteTextColor': '',
			'ServiceId': '',
			'RouteURL': '',
			'RouteStatus': '',
			'RouteStatusColor': ''
        },
    });

    return CtaRouteModel;
});
