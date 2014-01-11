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
	'./route'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Typeahead, CtaRoutesCollection, RouteView) {
	'use strict';

	var SearchView = Backbone.View.extend({
		collection: CtaRoutesCollection,
		initialize: function(){
			var self = this;
			self.context = {};
			self.routeView = new RouteView();

		    self.railRoutes = new self.collection([], { 
		            url: 'routes?type=rail',
		            icon: 'images/cta_train.png'
		    }).fetch({
		            success: function(data){
		            	self.context.railRoutes = data.toJSON();
		            	console.log(self.context.railRoutes);
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
		               	console.log(self.context.busRoutes);
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
		                console.log(self.context.stations);
		            },
		            error: function(collection, response, options){
		                console.log('error: ' + response);
		            }
		    });
		},
		render: function(){
			var self = this;

			var source = '<div style="background-color:#{{routeColorCode}};">'+
			'<table class="table routeTable">' + 
			'<tr><td><div class="line">{{ busNumber }}' + 
						'<img class="transit_logo" src={{routeIcon}}></img></div></td>' +
						'<td><div class="line">{{route}}</div></td></tr>' +
						'<table class="table routeTable"><tr><td class="line">{{ address }}</td></tr></table></table></div>';
			var hbs = Handlebars.compile(source);
			$('.routesTypeahead.typeahead').typeahead({
				remote: {
					url: 'search?query=%QUERY',
					filter: function(data) {
						console.log(data);
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
				template: hbs
			}).on('typeahead:selected', function (obj, datum) {
			   self.routeView.setRoute(datum);
			   self.routeView.render(); 
			});
			return this;
		}
	});

	return SearchView;
});