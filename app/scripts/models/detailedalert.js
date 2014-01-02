/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var DetailedAlertModel = Backbone.Model.extend({
        defaults: {
        	status: 'Unavalible'
        }
    });

    return DetailedAlertModel;
});
