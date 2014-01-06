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
	'../collections/ctaAlerts'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Typeahead, CtaRoutesCollection, CtaAlertsCollection) {
	'use strict';

	var isTrain = function(input){
		if(input == 'Red' || input === 'Blue' || input == 'Brn' ||
			input == 'G' || input == 'Org' || input == 'P' ||
			input == 'Pexp' || input == 'Pink' || input == "Y"){
			return true;
		}
		return false;
	};

	var isSystemWide = function(input){

	};

	var RoutesView = Backbone.View.extend({
		template: JST['app/scripts/templates/routes.hbs'],
		collection: CtaRoutesCollection,
		initialize: function(){
			var self = this;
			self.context = {};
			self.routes = new self.collection().fetch({
				success: function(data){
					self.context.routes = data.toJSON();
					self.render();
				},
				error: function(error){
					console.log('error');
				}
			});

			self.alerts = new CtaAlertsCollection().fetch({
				success: function(data){
					self.context.alerts = data.toJSON();
					self.render();
				},
				error: function(error){
					console.log('error');
				}
			});

			Handlebars.registerHelper('train-routes', function(routes) {
				var out = '<div id="routeStatus" class="accordion">';
				var icon;
				
				_.each(routes, function(route){
					if(isTrain(route.ServiceId[0])){
						icon = 'images/cta_train.png';
					}else{
						icon = 'images/cta_bus.png';
					}
					var description = "";
					var chevron = "";
					 _.find(self.context.alerts, function(alert){
						if(alert.ImpactedService[0].Service[0].ServiceId[0] === route.ServiceId[0]){
							description = alert.FullDescription[0];
							chevron = '<td class="statusChevron"><span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></td>';
						}
					});

					 out = out + '<div class="accordion-group line routeAlert" style="background-color:#' + route.RouteColorCode + '; data-parent="#routeStatus" data-toggle="collapse" data-target="#' + route.ServiceId + '" class="accordion-toggle">' + 
							'<div class="accordion-heading">' +
								'<table class="table routeTable"><tr><td><img class="transit_logo" src=' + icon + '></img></td>' +
								'<td><div class="line">' + route.Route + '</div></td>' +
								'<td><div class="line">' + route.RouteStatus + '</div></td>' +  
								chevron + '</tr></table>' + 
							'</div>' + 
							'<div class="accordion-body collapse" id=' + route.ServiceId +'>' + 
								'<div class="accordion-inner line routeAlertText">' + 
									description +	
								'</div>' +
							'</div>' +
						'</div>';
				});
				return out + '</div>';
			});

		},
		render: function(){
			var self = this;
			$('#train-status').html(this.template(self.context));
			var routes = [];
			_.each(self.context.routes, function(route){
				var tokens = route.Route[0].split('/');
					tokens.push(route.ServiceId[0]);
				routes.push({
					'value': route.Route[0],
					'tokens': tokens,
					'route': route.Route[0],
					'routeColorCode': route.RouteColorCode[0],
					'icon': isTrain(route.ServiceId[0]) ? 'images/cta_train.png' : 'images/cta_bus.png'
				});
			});

			var source   = '<div style="background-color:#{{routeColorCode}};">'+
			'<table class="table routeTable"><tr><td><img class="transit_logo" src={{icon}}></img></td>' +
						'<td><div class="line">{{route}}</div></td></tr>';
			var hbs = Handlebars.compile(source);
			$('input.routesTypeahead').typeahead({
				prefetch: 'cta.json',
				template: hbs
			});
			return this;
		}
	});

	return RoutesView;
});