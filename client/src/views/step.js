/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var TemplateView = require('./template');
var ActionsView = require('./actions');

module.exports = TemplateView.extend({

  tagName: 'li',

  className: 'step',

  templateId: 'step',


  events: {
    'click [name=number]': '_toggle'
  },


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);
    this.$number = this.$el.byName('number');
    this.$count = this.$el.byName('count');
    this.$actions = this.$el.byName('actions');

    this.listenTo(this.model, 'change:state', this._state);
    this.listenTo(this.model, 'change:expanded', this._expanded);

    this._state();
    this._expanded();
  },


  render: function () {
    var attrs = this.model.attributes;

    this.$number.text(attrs.number);

    var v = new ActionsView({
      collection: attrs.actions,
      el: this.$actions
    });
    v.render();

    return this;
  },


  _state: function () {
    var state = this.model.attributes.state;
    switch (state) {
    case 'open':
      this.$el.addClass('open');
      break;
    case 'current':
      this.$el.removeClass('open');
      this.$el.addClass('current');
      break;
    case 'done':
      this.$el.removeClass('current');
      this.$el.addClass('done');
      break;
    }
  },


  _expanded: function () {
    var expanded = this.model.attributes.expanded;
    if (typeof expanded === 'undefined') {
      expanded = true;
    }
    this.$el.toggleClass('collapsed', !expanded);
  },

  _toggle: function () {
    var expanded = this.model.attributes.expanded;
    if (typeof expanded === 'undefined') {
      expanded = true;
    }
    this.model.set({ expanded: !expanded });
  }


});
