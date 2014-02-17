/*global define*/

define([
	'underscore',
	'backbone',
	'../models/ctaStop'
], function (_, Backbone, Stop) {
	'use strict';

	var CtaNearbyCollection = Backbone.Collection.extend({
		model: Stop,
		initialize: function(attributes){
			if(attributes && attributes.lat && attributes.long){
				this.latitude = attributes.lat;
				this.longitude = attributes.long;
			}
		},
		url: function(){
			return 'nearby?lat=' + this.latitude + '&long=' + this.longitude;
		}
	});

	return CtaNearbyCollection;
});
