/*global define*/

define([
  'jquery',
  'underscore',
  'backbone',
  'templates'
], function ($, _, Backbone, JST) {
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