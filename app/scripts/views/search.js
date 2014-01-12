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
	'./route'
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

	var SearchView = Backbone.View.extend({
		collection: CtaRoutesCollection,
		initialize: function(){
			var self = this;
			self.context = {};
			self.routeView = new RouteView();

			self.railLines = [];

		    self.railRoutes = new self.collection([], { 
		            url: 'routes?type=rail'
		    }).fetch({
		            success: function(data){
		            	self.context.railRoutes = data.toJSON();
		            	self.railLines = getRailLines(self.context.railRoutes);
		            	self.render();
		            },
		            error: function(collection, response, options){
		            	console.log('error: ' + response);

		            }
		    });

		    self.busRoutes = new self.collection([], { 
		            url: 'routes?type=bus'
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

		    self.alerts = new CtaAlertsCollection().fetch({
	            success: function(data){
		            self.context.alerts = data.toJSON();
		            self.render();
                },
                error: function(error){
                        console.log('error');
                }
	        });
		},
		render: function(){
			var self = this;

			var routes = '<div id="routeStatus" class="accordion">';
           	var icon = 'images/cta_train.png';
		    _.each(self.railLines, function(route){
		        var description = "";
		        var chevron = '<td class="statusChevron"></td>';
		         _.find(self.context.alerts, function(alert){
		                if(alert.ImpactedService.Service.ServiceId === route.ServiceId){
		                    description = alert.FullDescription;
		                    chevron = '<td class="statusChevron"><span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></td>';
		                }
		        });

		        routes = routes + '<div class="accordion-group line routeAlert" style="background-color:#' + route.RouteColorCode + '; data-parent="#routeStatus" data-toggle="collapse" data-target="#' + route.ServiceId + '" class="accordion-toggle">' + 
                        '<div class="accordion-heading">' +
                                '<table class="table routeTable"><tr>' +
                                '<td>' + '<img class="transit_logo" src=' + icon + '></img></td>' +
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
            routes += '</div>';

            $('#routeStatus').html(routes);

			var source = '<div style="background-color:#{{routeColorCode}};">'+
			'<table class="table routeTable">' + 
			'<tr><td class="line">{{ busNumber }} <img class="transit_logo" src={{routeIcon}}></img></td>' +
				'<td class="line">{{route}}</td>' +
				'<td class="line">{{ routeStatus }}</td></tr>' +
				'<table class="table routeTable"><tr><td class="line">{{ address }}</td></tr></table></table></div>';
			var hbs = Handlebars.compile(source);
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