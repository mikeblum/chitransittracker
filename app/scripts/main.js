/*global require*/
'use strict';

require.config({
    shim: {
        underscore: {
            exports: '_'
        },
        backbone: {
            deps: [
                'underscore',
                'jquery'
            ],
            exports: 'Backbone'
        },
        bootstrap: {
            deps: ['jquery'],
            exports: 'jquery'
        },
        handlebars: {
            exports: 'Handlebars'
        },
        typeahead:{
            exports: 'Typeahead'
        },
        moment:{
            exports: 'Moment'
        }
    },
    paths: {
        jquery: '../bower_components/jquery/jquery',
        backbone: '../bower_components/backbone/backbone',
        underscore: '../bower_components/underscore/underscore',
        bootstrap: '../bower_components/sass-bootstrap/dist/js/bootstrap',
        handlebars: '../bower_components/handlebars/handlebars',
        typeahead: '../bower_components/typeahead.js/dist/typeahead',
        moment: '../bower_components/moment/min/moment.min'
    }
});

require([
    'jquery',
    'backbone',
    'routes/approuter'
], function ($, Backbone, AppRouter) {
    var app = new AppRouter();
    Backbone.history.start();
});
