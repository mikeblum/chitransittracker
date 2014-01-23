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
	'./route',
	'layoutmanager'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Typeahead, CtaRoutesCollection, CtaAlertsCollection, RouteView) {
	'use strict';

	var SearchView = Backbone.Layout.extend({
		template: JST['app/scripts/templates/search.hbs'],
		initialize: function(){
			var self = this;
			$('#routeSpinner').show();
			self.context = {};
			self.routeView = RouteView;

		    self.busRoutes = new CtaRoutesCollection();
		    self.busRoutes.url = 'routes?type=bus';
		    self.busRoutes.fetch({
		            success: function(data){
		               	self.context.busRoutes = data.toJSON();
		            },
		            error: function(collection, response, options){
		                console.log('error: ' + response);
		                $('#error').show();
		            }
		    });

		    self.stations = new CtaRoutesCollection();
		    self.stations.url = 'routes?type=station';
		    self.stations.fetch({
		            success: function(data){
		                self.context.stations = data.toJSON();
		            },
		            error: function(collection, response, options){
		                console.log('error: ' + response);
		                $('#error').show();
		            }
		    });
		},
		beforeRender: function(){
			var self = this;
			$('.routesTypeahead.typeahead').typeahead({
				limit: 10,
				remote: {
					url: 'search?query=%QUERY',
					filter: function(data) {
						var query = false;
						if(data){
							_.each(data, function(type){
								if(type.length > 0){
									query = true;
								}
							});
						}
						if(!query){
							return '';
						}
						var retval = [];
						_.each(data.busRoutes, function(el){
							retval.push({
								value: el.route,
								tokens: [ el.route[0], el.serviceId ],
								route: el.route[0],
								routeColorCode: el.routeColorCode,
								routeTextColor: el.routeTextColor,
								serviceId: el.serviceId,
								routeURL: el.routeURL,
								routeStatus: el.routeStatus,
								routeStatusColor: el.routeStatusColor,
								routeIcon: 'images/cta_bus.svg',
								busNumber: '#' + el.serviceId
							});
						});
						_.each(data.stations, function(el){
							retval.push({
								value: el.route,
								tokens: [ el.route[0] ],
								route: el.route[0],
								address: el.route[1].split(',')[0],
								routeColorCode: el.routeColorCode,
								routeTextColor: el.routeTextColor,
								serviceId: el.serviceId,
								routeURL: el.routeURL,
								routeStatus: el.routeStatus,
								routeStatusColor: el.routeStatusColor,
								routeIcon: 'images/cta_train.svg'
							});
						});
						return retval;
					}
				},
				template: self.template
			}).on('typeahead:selected ', function (obj, datum) {
				//clear typeahead
				$('.typeahead').typeahead('setQuery', '');
				console.log(JSON.stringify(datum))
			   	self.routeView.setRoute(datum);
			   	$('.routesTypeahead.typeahead').trigger('blur');
			});
			setTimeout(function() {
			    $('#routeSpinner').fadeOut('fast');
			}, 1000);
		}
	});

	return SearchView;
});