/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'typeahead',
	 '../collections/ctaRoutes',
	 '../collections/ctaAlerts',
	'layoutmanager'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Typeahead, CtaRoutesCollection, CtaAlertsCollection) {
	'use strict';

	var routes;

	var StatusView = Backbone.View.extend({
		manage: true,
		template: JST['app/scripts/templates/status.hbs'],
		initialize: function(){
			var self = this;
			$('#routeSpinner').show();
			self.context = {};

			self.railLines = [];

		    self.railRoutes = new CtaRoutesCollection();
		    self.railRoutes.url = 'routes?type=rail';
		    self.railRoutes.fetch({
		            success: function(data){
		            	self.context.railRoutes = data.toJSON();
		            	self.railLines = self.context.railRoutes;
		            	self.render();
		            },
		            error: function(collection, response, options){
		            	console.log('error: ' + response);
		            	$('#error').show();
		            }
		    });

		    self.alerts = new CtaAlertsCollection();

		    self.alerts.fetch({
	            success: function(data){
		            self.context.alerts = data.toJSON();
		            self.render();
                },
                error: function(error){
                        console.log('error');
                        $('#error').show();
                }
	        });
		},
		beforeRender: function(){
			var self = this;
			routes = [];
		    _.each(self.railLines, function(route){
		        route.description = "";
		        route.chevron = '<td class="statusChevron"></td>';
		         _.find(self.context.alerts, function(alert){
		                if(alert.ImpactedService.Service.ServiceId === route.ServiceId){
		                    route.description = alert.FullDescription;
		                    route.chevron = '<td class="statusChevron"><span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></td>';
		                }
		        });
		        routes.push(route);
            });
		},
		serialize: function(){
			return { routes: routes };
		}
	});

	return StatusView;
});