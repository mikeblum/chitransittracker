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

	var RoutesView = Backbone.View.extend({
		template: JST['app/scripts/templates/routes.hbs'],
		collection: CtaRoutesCollection,
		initialize: function(){
			var self = this;
			self.context = {};
			self.railRoutes = new self.collection([], { 
				url: 'routes?type=rail',
				icon: 'images/cta_train.png'
			}).fetch({
				success: function(data){
					self.context.railRoutes = data.toJSON();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});

			self.busRoutes = new self.collection([], { 
				url: 'routes?type=bus',
				icon: 'images/bus.png'
			}).fetch({
				success: function(data){
					self.context.busRoutes = data.toJSON();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});

			self.stations = new self.collection([], { 
				url: 'routes?type=station',
			}).fetch({
				success: function(data){
					self.context.stations = data.toJSON();
				},
				error: function(collection, response, options){
					console.log('error: ' + response);
				}
			});

			// self.alerts = new CtaAlertsCollection().fetch({
			// 	success: function(data){
			// 		self.context.alerts = data.toJSON();
			// 		console.log(self.context.alerts);
			// 	},
			// 	error: function(collection, response, options){
			// 		console.log('error: ' + response);
			// 	}
			// });

			Handlebars.registerHelper('train-routes', function(routes) {
				var out = '<div id="routeStatus" class="accordion">';
				var icon;
				
				_.each(routes, function(route){
					var routeNum = true;
					if(isTrain(route.ServiceId)){
						routeNum = false;
						
					}else{
						icon = 'images/cta_bus.png';
					}

					//catch Systemwise and All Routes edge cases
					if(route.ServiceId === "Bus" ||
						route.ServiceId === "Systemwide"){
						routeNum = false;
					}
					var description = "";
					var chevron = "";
					var busNumber = routeNum ? '<span class="line">#' + route.ServiceId + '</span>' : "";
					 _.find(self.context.alerts, function(alert){
						if(alert.ImpactedService.Service.ServiceId === route.ServiceId){
							description = alert.FullDescription;
							chevron = '<td class="statusChevron"><span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></td>';
						}
					});

					 out = out + '<div class="accordion-group line routeAlert" style="background-color:#' + route.RouteColorCode + '; data-parent="#routeStatus" data-toggle="collapse" data-target="#' + route.ServiceId + '" class="accordion-toggle">' + 
							'<div class="accordion-heading">' +
								'<table class="table routeTable"><tr>' +
								'<td>' + busNumber + '<img class="transit_logo" src=' + icon + '></img></td>' +
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

			var source = '<div style="background-color:#{{routeColorCode}};">'+
			'<table class="table routeTable"><tr><td><img class="transit_logo" src={{routeIcon}}></img></td>' +
						'<td><div class="line">{{route}}</div></td></tr></table>';
			var hbs = Handlebars.compile(source);
			$('.routesTypeahead.typeahead').typeahead({
				remote: {
		        	url: 'search?query=%QUERY',
			        filter: function(data) {
			            var retval = [];
			            _.each(data, function(el){
			            	var route = el.Route.split('|')[0].trim(); //trim mailing address from station name
			            	retval.push({
			                    value: route,
			                    tokens: [ el.Route, el.ServiceId ],
			                    route: route,
								routeColorCode: el.RouteColorCode || '0f0f0f',
								routeTextColor: el.RouteTextColor,
								serviceId: el.ServiceId,
								routeURL: el.RouteURL,
								routeStatus: el.RouteStatus,
								routeStatusColor: el.RouteStatusColor,
								routeIcon: el.RouteIcon
			                });
			            });
			            console.log(retval);
			            return retval;
			        }
		    	},
		    	template: hbs
			});
			return this;
		}
	});

	return RoutesView;
});