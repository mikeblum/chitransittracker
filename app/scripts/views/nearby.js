/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates',
	'../collections/ctaNearby',
	'./route',
	'../models/ctaRoute',
	'layoutmanager'
], function ($, _, Backbone, JST, NearbyStops, RouteView, CtaRoute) {
	'use strict';

	var Nearby = Backbone.Layout.extend({
		template: JST['app/scripts/templates/nearby.hbs'],
		initialize: function(){
			this.error = false;
			this.nearbyStops = new NearbyStops();
			this.getLocation();
		},
		getLocation: function() {
			$('#nearbySpinner').show();
			navigator.geolocation.getCurrentPosition(function(position){
				var self = this;
				var latitude = position.coords.latitude;
				var longitude = position.coords.longitude;
				this.nearbyStops = new NearbyStops({
					lat: latitude,
					long: longitude
				});
				this.nearbyStops.fetch({
					reset: true,
					success: function(collection){
						self.error = false;
						setTimeout(function() {
						    $('#nearbySpinner').fadeOut('fast');
						}, 1000);
						collection.each(function(stop){
							if(stop.get('type') === 'train'){
								stop.set('routeIcon', 'images/cta_train.svg');
							}else{
								stop.set('routeIcon', 'images/cta_bus.svg');
								stop.set('routeColorCode', '059');
							}
						});

						self.nearbyStops = collection;

						self.render();
					},
					error: function(){
						setTimeout(function() {
						    $('#nearbySpinner').fadeOut('fast');
						}, 1000);
						self.error = true;
						self.render();
					}
				});
			}.bind(this), function(){
				setTimeout(function() {
				    $('#nearbySpinner').fadeOut('fast');
				}, 1000);
				this.error = true;
				this.render();
			}.bind(this));
		},
		afterRender: function(){
			this.$(".nearbyRoute").on('click', function(event){
				var serviceId = event.currentTarget.classList[1];
				var routeData = this.nearbyStops.find(function(model){
					return model.get('stpid') === serviceId;
				}, this);
				RouteView.setRoute(routeData.attributes);
			}.bind(this));
		},
		serialize: function(){
			return {
				stops: this.nearbyStops.toJSON(),
				error: this.error
			};
		}
	});

	return Nearby;
});
