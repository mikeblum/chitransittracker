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

	var getRailLines = function(railLines){
		var lines = [];
		_.each(railLines, function(line){
			if(line.ServiceId == 'Red' || line.ServiceId === 'Blue' || line.ServiceId == 'Brn' ||
                line.ServiceId == 'G' || line.ServiceId == 'Org' || line.ServiceId == 'P' ||
                line.ServiceId == 'Pexp' || line.ServiceId == 'Pink' || line.ServiceId == "Y"){
                lines.push(line);
        	}
		});
        return lines;
    };

	var SearchView = Backbone.Layout.extend({
		template: JST['app/scripts/templates/search.hbs'],
		initialize: function(){
			var self = this;
			$('#routeSpinner').show();
			self.context = {};
			self.routeView = RouteView;

			self.railLines = [];

		    self.railRoutes = new CtaRoutesCollection();
			self.railRoutes.url = 'routes?type=rail';
		    self.railRoutes.fetch({
		            success: function(data){
		            	self.context.railRoutes = data.toJSON();
		            	self.railLines = getRailLines(self.context.railRoutes);
		            	self.render();
		            },
		            error: function(collection, response, options){
		            	console.log('error: ' + response);
		            	$('#error').show();
		            }
		    });

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
						_.each(data.railRoutes, function(el){
							retval.push({
								value: el.route,
								tokens: [ el.route[0] ],
								route: el.route[0],
								routeColorCode: el.routeColorCode,
								routeTextColor: el.routeTextColor,
								serviceId: el.serviceId,
								routeURL: el.routeURL,
								routeStatus: el.routeStatus,
								routeStatusColor: el.routeStatusColor,
								routeIcon: 'images/cta_train.svg'
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
			}).on('typeahead:selected typeahead:autocompleted', function (obj, datum) {
			   self.routeView.setRoute(datum);
			   $('.routesTypeahead.typeahead').trigger('blur');
			   return datum;
			});
			setTimeout(function() {
			    $('#routeSpinner').fadeOut('fast');
			}, 1000);
		}
	});

	return SearchView;
});