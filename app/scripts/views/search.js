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
			self.context = {};

			self.searchResultTemplate = JST['app/scripts/templates/searchResult.hbs'];
			self.routeView = RouteView;
		},
		afterRender: function(){
			var self = this;
			$('.routesTypeahead.typeahead').typeahead({
				limit: 10,
				remote: {
					url: 'search?query=%QUERY',
					beforeSend: function(xhr){
						$('#searchSpinner').show();
					},
					filter: function(data) {
						setTimeout(function() {
							$('#searchSpinner').fadeOut('fast');
						}, 1000);
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
								busNumber: '#' + el.serviceId,
								type: 'bus'
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
								routeIcon: 'images/cta_train.svg',
								type: 'rail'
							});
						});
						return retval;
					}
				},
				template: self.searchResultTemplate
			}).on('typeahead:selected ', function (obj, datum) {
				//clear typeahead
				$('.routesTypeahead.typeahead').typeahead('setQuery', '');
				self.routeView.setRoute(datum);
				self.routeView.refresh(datum.serviceId);
				$('.routesTypeahead.typeahead').trigger('blur');
			});
		}
	});

	return SearchView;
});