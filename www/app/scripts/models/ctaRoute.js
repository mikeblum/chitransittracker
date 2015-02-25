/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtarouteModel = Backbone.Model.extend({
        url: 'routes'
    });

    return CtarouteModel;
});
