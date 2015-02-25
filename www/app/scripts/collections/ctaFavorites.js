define([
    'underscore',
    'backbone',
    'localstorage',
    '../models/CtaFavorite'
], function (_, Backbone, localstorage, CtaFavorite) {
    'use strict';

    var CtaFavoritesCollection = Backbone.Collection.extend({
        model: CtaFavorite,
        localStorage: new Backbone.LocalStorage("chitransittracker-favorites")
    });
    return new CtaFavoritesCollection();
});