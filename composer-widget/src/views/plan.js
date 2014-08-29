/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var templates = require('../templates');

module.exports = Backbone.View.extend({

  tagName: 'li',

  className: 'plan',


  initialize: function () {
    this.$el.html(templates.get('plan'));

    this.$execute = this.$el.byName('execute');
    this.$relativeRating = this.$el.byName('relative-rating');
    this.$ratingBar = this.$el.byName('rating-bar');
    this.$widgetCount = this.$el.byName('widget-count');
    this.$stepCount = this.$el.byName('step-count');
    this.$interactionCount = this.$el.byName('interaction-count');

    this.listenTo(this.model, 'change', this.render);
  },


  render: function () {
    var attrs = this.model.attributes;
    var relativeRating = Math.floor(attrs.relativeRating * 100);
    this.$execute.attr('href', '#execution/' + this.model.id);
    this.$relativeRating.text(relativeRating);
    this.$ratingBar.css({ width: relativeRating + '%' });
    this.$widgetCount.text(attrs.widgetCount);
    this.$stepCount.text(attrs.stepCount);
    this.$interactionCount.text(attrs.interactionCount);
    return this;
  }


});
