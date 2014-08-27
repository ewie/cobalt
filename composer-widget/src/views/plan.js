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
    this.$rating = this.$el.byName('rating');
    this.$ratingBar = this.$el.byName('rating-bar');

    this.listenTo(this.model, 'change', this.render);
  },


  render: function () {
    var attrs = this.model.attributes;
    var relativeRating = Math.floor(attrs.relativeRating * 100);
    this.$rating.text(relativeRating);
    this.$ratingBar.css({ width: relativeRating + '%' });
    this.$execute.attr('href', '#execution/' + this.model.id);
    return this;
  }


});
