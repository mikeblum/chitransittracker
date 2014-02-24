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
					self.railLines = data.toJSON();
					var temp = ['Red', 'Blue', 'Brn', 'G', 'Org', 'P', 'Pexp', 'Pink', 'Y'];
					//sort rail lines into cta order
					_.each(temp, function(serviceId, index){
						var route = _.findWhere(self.railLines, {serviceId: serviceId});
						if(serviceId === 'Y'){
							route.routeTextColor = "blackLine";
							route.transitLogo = "images/cta_train_black.svg";
						}else{
							route.routeTextColor = "line";
							route.transitLogo = "images/cta_train.svg";
						}
						temp[index] = route;
					});
					self.railLines = temp;
					self.render();
				},
				error: function(collection, response, options){
					
				},
			}).complete(function(){
				setTimeout(function() {
				    $('#routeSpinner').fadeOut('fast');
				}, 1000);
			});

			self.alerts = new CtaAlertsCollection();

			self.alerts.fetch({
				success: function(data){
					self.context.alerts = data.toJSON();
					self.render();
				},
				error: function(error){
					
				}
			});
		},
		beforeRender: function(){
			var self = this;
			routes = [];
			_.each(self.railLines, function(route){
				route.description = "";
				route.chevron = '<td class="statusChevron"></td>';
				var impactedServices = [];
				var alerts =  _.filter(self.context.alerts, function(alert){
					if(alert.impactedService.Service.ServiceId === route.serviceId){
						if(impactedServices.length > 0 && _.contains(impactedServices, alert.shortDescription)){
							return false;
						}else{
							impactedServices.push(alert.shortDescription);
							return true;
						}
					}
					return false;
				});
				route.alerts = alerts;
				if(alerts && alerts.length > 0){
					route.chevron = '<td class="statusChevron"><span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></td>';
				}
				routes.push(route);
			});
		},
		serialize: function(){
			return { routes: routes };
		}
	});

	return StatusView;
});