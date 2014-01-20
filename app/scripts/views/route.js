/*global define*/

define([
	'jquery',
	'bootstrap',
	'underscore',
	'backbone',
	'templates',
	'handlebars',
	'moment',
	'../collections/ctaArrivals',
	'../collections/ctaFavorites',
	'../models/CtaFavorite',
	'../models/ctaRoute'
], function ($, Bootstrap, _, Backbone, JST, Handlebars, Moment, CtaArrivalsCollection, CtaFavoritesCollection, CtaFavorite, CtaRoute){
	'use strict';

	var RouteView = Backbone.View.extend({
		manage: true,
		template: JST['app/scripts/templates/route.hbs'],
		initialize: function(route){
			var self = this;
			self.arrivals = {};
			self.route = route;
			self.favorites = CtaFavoritesCollection;
		},
		setRoute: function(route){
			var self = this;
			$('#arrivalsSpinner').show();
			self.route = route;
			self.arrivalsCollection = new CtaArrivalsCollection();
			self.arrivalsCollection.url = 'arrivals?stop=' + self.route.serviceId;
			self.arrivalsCollection.fetch({
				success: function(data){
					self.arrivals = data.toJSON();
					self.render();
				},
				error: function(collection, response, options){
					console.log('error: ' + JSON.stringify(response));
				}
			});
		},
		saveFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				var favorite = new CtaFavorite(
					this.route
				);
				this.favorites.fetch();
				this.favorites.add(favorite);
				favorite.save();
			}
		},
		removeFavorite: function(){
			if(typeof(Storage)!=="undefined"){
				this.favorites.fetch();
				var favorite = this.favorites.findWhere({
					serviceId: this.route.serviceId
				});
				favorite.destroy();
				this.favorites.remove(favorite);
			}
		},
		favoriteRoute: function()
		{
			if($( ".favorite" ).css('fill') === 'none'){
				this.saveFavorite();
				$( ".favorite" ).css({fill: "#ffd400"});
			}else{
				this.removeFavorite();
				$( ".favorite" ).css({fill: "none"});
			}
		},
		convertDate: function(ctaDate){
			return Moment(ctaDate, 'YYYYMMDD hh:mm:ss');
		},
		beforeRender: function(){
			var self = this;
			if(self.route){
				if(typeof(Storage)!=="undefined"){
					this.favorites.fetch();
					var favRoute = this.favorites.findWhere({
						route: this.route.route,
						serviceId: this.route.serviceId
					});
					if(!favRoute){
						self.route.favorite = "none";
					}else{
						self.route.favorite = "#ffd400";
					}
				}
				if(self.arrivals){
					var source = JST['app/scripts/templates/arrivals.hbs'];
					var arrivalsTemplate = {
						routeColor: self.route.routeColorCode,
						arrivals: []
					};
					_.each(self.arrivals, function(arrival){
						arrivalsTemplate.arrivals.push({
							destination: arrival.destNm,
							computedTime: self.convertDate(arrival.arrT).diff(self.convertDate(arrival.prdt), 'minutes'),
							approaching: arrival.isApp === '1' ? '1' : ''
						});
					});
					arrivalsTemplate.arrivals = arrivalsTemplate.arrivals.sort(function(a,b){
						if(a.destination < b.destination) return -1;
						if(a.destination > b.destination) return 1;
						return 0;
					});
					$('.arrivals').html(source(arrivalsTemplate));

					setTimeout(function() {
						$('#arrivalsSpinner').fadeOut('fast');
					}, 1000);
				}
			}
			$(".route").empty().append(self.el);
			return self;
		},
		afterRender: function(){
			var self = this;
			$( ".favorite" ).click(function(){
			  self.favoriteRoute();
			});
		},
		serialize: function(){
			var self = this;
			return self.route;
		}
	});

	return new RouteView();
});