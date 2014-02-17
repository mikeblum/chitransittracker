/*global define*/

define([
	'jquery',
	'underscore',
	'backbone',
	'templates',
	'../collections/ctaNearby',
	'layoutmanager'
], function ($, _, Backbone, JST, NearbyStops) {
	'use strict';

	var Nearby = Backbone.Layout.extend({
		template: JST['app/scripts/templates/nearby.hbs'],
		initialize: function(){
			this.nearbyStops = new NearbyStops();
			this.getLocation();
		},
		getLocation: function() {
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

					}
				});
			}.bind(this));
		},
		serialize: function(){
			return {
				stops: this.nearbyStops.toJSON()
			};
		}
	});

	return Nearby;
});
