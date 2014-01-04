/*global define*/

define([
  'jquery',
  'underscore',
  'backbone',
  'templates'
], function ($, _, Backbone, JST) {
  'use strict';

  var NotificationView = Backbone.View.extend({
    template: JST['app/scripts/templates/notification.hbs'],
    initialize: function(){
      var self = this;
    },
    render: function(){
        $('#notifications').html(this.template());
        return this;
      }
  });

  return NotificationView;
});