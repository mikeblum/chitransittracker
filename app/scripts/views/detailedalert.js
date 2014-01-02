/*global define*/

define([
    'jquery',
    'underscore',
    'backbone',
    'templates',
    '../collections/detailedalert'
], function ($, _, Backbone, JST,　DetailedAlertCollection) {
    'use strict';

    var DetailedAlertView = Backbone.View.extend({
        template: JST['app/scripts/templates/detailedalert.hbs'],
        initialize: function(){
			var self = this;

			self.detailedAlert = new DetailedAlertCollection();

			// self.trainStatus.fetch({
			// 	dataType: 'jsonp xml',
			// 	success: function(data){
			// 		console.log('success!!' + data);
			// 	},
			// 	error: function(){
			// 		console.log('Failed to get CTA status');
			// 	}
			// });

			$.ajax({
		        url: 'http://www.transitchicago.com/api/1.0/alerts.aspx',
		        type: 'get',
		        dataType: 'xml',
		        async: false,
		        success: function(data) {
		            console.log(data);
		        },
		        error: function(){
		        	console.log("Failed to load CTA detailed alerts");
		        }
		    });
		},
		render: function(){
	      $('#detailed-alert').html(this.template());
	      return this;
	    }
    });

    return DetailedAlertView;
});
