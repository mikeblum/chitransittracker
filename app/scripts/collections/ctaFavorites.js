define([
    'underscore',
    'backbone',
    'localstorage'
], function (_, Backbone, localstorage) {
    'use strict';

    var CtaFavoritesCollection = Backbone.Collection.extend({
        model: new Backbone.Model.extend({
            default: {
                route: '',
                serviceId: ''
            }
        }),
        localStorage: new Backbone.LocalStorage("chitransittracker-favorites")
    });
    return CtaFavoritesCollection;
});