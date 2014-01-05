/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'../collections/ctaRoutes'
], function ($, _, Backbone, JST, Handlebars, CtaRoutesCollection) {
	'use strict';

	var isTrain = function(input){
		if(input == 'Red' || input === 'Blue' || input == 'Brn' ||
			input == 'G' || input == 'Org' || input == 'P' ||
			input == 'Pexp' || input == 'Pink' || input == "Y"){
			return true;
		}
		return false;
	};

	Handlebars.registerHelper('train-routes', function(routes) {
		var out = '<table class="table borderless">';
		var icon;
		_.each(routes, function(route){
			console.log(route.Route + " " + route.ServiceId);
			if(isTrain(route.ServiceId[0])){
				icon = 'images/cta_train.png';
			}else{
				icon = 'images/cta_bus.png';
			}

			out = out + '<tr class="line" style="background-color:#' + route.RouteColorCode + '";><td><p><img class="transit_logo" src=' + icon + '></img>'+
				 route.Route + '</p></td><td><p>' + route.RouteStatus + '  <span class="glyphicon glyphicon-chevron-down glyphicon-inverse"></span></p></td></tr>';
		});
		return out + '</table>';
	});

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
		},
		render: function(){
		  $('#train-status').html(this.template(this.context));
		  return this;
		}
	});

	return RoutesView;
});