/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var CollectionView = require('./collection');

module.exports = CollectionView.extend({

  tagName: 'ul',

  className: 'interactions',

  itemView: require('./interaction'),


  initialize: function () {
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
      s = "One step to go.";
    } else {
      s = "There are " + count + " steps to go.";
    }
    this.$count.text(s);
  }



});
