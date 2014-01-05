/*global define*/

define([
  'jquery',
  'bootstrap',
  'underscore',
  'backbone',
  'templates'
], function ($, Bootstrap, _, Backbone, JST) {
  'use strict';

  var NotificationsView = Backbone.View.extend({
    template: JST['app/scripts/templates/notifications.hbs'],
    initialize: function(){
      var self = this;

    },
    render: function(){
      $('#notifications').html(this.template());
        return this;
      }
  });

  return NotificationsView;
});