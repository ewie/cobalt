/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var TemplateView = require('./template');
var InteractionsView = require('./interactions');

module.exports = TemplateView.extend({

  tagName: 'li',

  className: 'action',

  templateId: 'action',


  events: {
    'click [name=highlight]': function () { this.model.trigger('highlight') },
    'change [name=done-check]': '_checkDone'
  },


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);
    this.$count = this.$el.byName('count');
    this.$done = this.$el.byName('done');
    this.$doneCheck = this.$el.byName('done-check');
    this.$interactions = this.$el.byName('interactions');

    this.listenTo(this.model, 'change:done', this._done);

    var comms = this.model.get('communications');
    if (comms.length > 0) {
      this.$done.hide();
    }
  },


  render: function () {
    var attrs = this.model.attributes;

    var interactions = attrs.interactions;
    var s;
    switch (interactions.length) {
    case 0:
      s = "No interactions to perform.";
      break;
    case 1:
      s = "One interaction to perform.";
      break;
    default:
      s = "There are " + interactions.length + " interactions to perform in any sequence.";
      break;
    }
    this.$count.text(s);

    this.$doneCheck.prop('checked', attrs.done);

    var v = new InteractionsView({
      collection: interactions,
      el: this.$interactions
    });
    v.render();

    return this;
  },


  _done: function () {
    this.$el.addClass('done');
  },


  _checkDone: function () {
    this.model.set({ done: this.$doneCheck.prop('checked') });
  }


});
