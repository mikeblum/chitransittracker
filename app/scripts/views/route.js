/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars'
], function ($, Bootstrap, _, Backbone, JST, Handlebars) {
	'use strict';

	var RouteView = Backbone.View.extend({
		template: JST['app/scripts/templates/route.hbs'],
		initialize: function(route){
			var self = this;
			self.route = route;
		},
		setRoute: function(route){
			this.route = route;
		},
		render: function(){
			var self = this;
			$('#route').html(self.template(self.route));
			return self;
		}
	});

	return RouteView;
});