/*global define*/

define([
	'jquery',
	'backbone',
	'../views/search'
], function ($, Backbone, SearchView) {
	'use strict';

	var AppRouter = Backbone.Router.extend({
		initialize: function() {
		},
		routes: {
			'' : 'root'
		},
		root: function(){
	       	this.SearchView = new SearchView();
	       	this.SearchView.render();
		}
	});

	return AppRouter;
});