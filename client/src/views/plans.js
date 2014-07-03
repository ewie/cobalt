/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var PlanView = require('./plan');
var TemplateView = require('./template');
var CollectionView = require('./collection');

module.exports = CollectionView.extend({

  listElement: '[name=plans]',

  itemView: PlanView,

  templateId: 'plans',


  initialize: function () {
    TemplateView.prototype.initialize.apply(this, arguments);
    CollectionView.prototype.initialize.apply(this, arguments);

    this.$count = this.$el.byName('count');

    this.listenTo(this.collection, 'reset', this._resize);
    this.listenTo(this.collection, 'add', this._resize);
    this.listenTo(this.collection, 'remove', this._resize);

    this._resize();
  },


  _resize: function () {
    var count = this.collection.size();
    var s;
    if (count === 1) {
      s = "There is only one plan to choose.";
    } else {
      s = "There are " + count + " plans to choose from.";
    }
    this.$count.text(s);
  }


});
