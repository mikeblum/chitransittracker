/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'typeahead',
	'./route'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Typeahead, RouteView) {
	'use strict';

	var SearchView = Backbone.View.extend({
		initialize: function(){
			var self = this;
			self.routeView = new RouteView();
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
								value: el.Route,
								tokens: [ el.Route[0], el.ServiceId ],
								route: el.Route[0],
								routeColorCode: el.RouteColorCode,
								routeTextColor: el.RouteTextColor,
								serviceId: el.ServiceId,
								routeURL: el.RouteURL,
								routeStatus: el.RouteStatus,
								routeStatusColor: el.RouteStatusColor,
								routeIcon: 'images/cta_bus.svg',
								busNumber: '#' + el.ServiceId
							});
						});
						_.each(data.railRoutes, function(el){
							retval.push({
								value: el.Route,
								tokens: [ el.Route[0] ],
								route: el.Route[0],
								routeColorCode: el.RouteColorCode,
								routeTextColor: el.RouteTextColor,
								serviceId: el.ServiceId,
								routeURL: el.RouteURL,
								routeStatus: el.RouteStatus,
								routeStatusColor: el.RouteStatusColor,
								routeIcon: 'images/cta_train.svg'
							});
						});
						_.each(data.stations, function(el){
							retval.push({
								value: el.Route,
								tokens: [ el.Route[0] ],
								route: el.Route[0],
								address: el.Route[1].split(',')[0],
								routeColorCode: el.RouteColorCode,
								routeTextColor: el.RouteTextColor,
								serviceId: el.ServiceId,
								routeURL: el.RouteURL,
								routeStatus: el.RouteStatus,
								routeStatusColor: el.RouteStatusColor,
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