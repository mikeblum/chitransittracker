/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtaArrivalsCollection = Backbone.Collection.extend({
        url: 'arrivals',
        parse: function(data){
        	return data.ctatt.eta;
        }
    });

    return CtaArrivalsCollection;
});
