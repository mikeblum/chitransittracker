/*global define*/

define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    'use strict';

    var CtaArrivalsCollection = Backbone.Collection.extend({
        url: 'arrivals',
        type: 'rail',
        parse: function(data){
            if(this.type === 'rail'){
        	   return data.ctatt.eta;
            }else{
                return data['bustime-response'];
            }
        }
    });

    return CtaArrivalsCollection;
});
