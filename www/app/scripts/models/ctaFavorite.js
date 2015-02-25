/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtafavoriteModel = Backbone.Model.extend({
        defaults: {
        	route: '',
        	serviceId: ''
        }
    });

    return CtafavoriteModel;
});
