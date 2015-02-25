/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtaRoutesCollection = Backbone.Collection.extend({
        url: 'routes',
        parse: function(data){
        	return data;
        }
    });

    return CtaRoutesCollection;
});
