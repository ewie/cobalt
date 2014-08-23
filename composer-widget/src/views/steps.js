/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var CollectionView = require('./collection');
var StepView = require('./step');
var TemplateView = require('./template');

module.exports = CollectionView.extend({

  listElement: '[name=steps]',

  itemView: StepView,

  templateId: 'steps',


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);
    CollectionView.prototype.initialize.apply(this, arguments);

    this.$count = this.$el.byName('count');

    this.listenTo(this.collection, 'reset', this._resize);
    this.listenTo(this.collection, 'add', this._reisze);
    this.listenTo(this.collection, 'remove', this._resize);

    this._resize();
  },


  _resize: function () {
    var count = this.collection.size();
    var s;
    if (count === 1) {
      s = "You\u2019re lucky, only step to go.";
    } else {
      s = "There are " + count + " steps to go in the given order.";
    }
    this.$count.text(s);
  }


});
