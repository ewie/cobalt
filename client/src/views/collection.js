/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


module.exports = Backbone.View.extend({


  initialize: function () {
    this.listenTo(this.collection, 'reset', this._reset);
    this.listenTo(this.collection, 'add', this._add);
    this.listenTo(this.collection, 'remove', this._remove);

    this._index = {};
    this._reset(this.collection);
  },


  render: function () {
    var index = this._index;
    this.collection.forEach(function (m) { index[m.cid].render() });
    return this;
  },


  _reset: function () {
    var self = this;
    var el = this._getListElement().empty();
    this.collection.forEach(function (m) {
      var v = self._index[m.cid] = self._createItemView({ model: m });
      el.append(v.$el);
    });
    this.render();
  },


  _add: function (model) {
    var v = this._index[model.cid] = this._createItemView({ model: model });
    this._getListElement().append(v.$el);
    v.render();
  },


  _remove: function (plan) {
    var v = this._index[plan.cid];
    if (v) {
      v.remove();
      delete this._index[plan.cid];
    }
  },


  _createItemView: function (options) {
    return new this.itemView(options);
  },


  _getListElement: function () {
    return this.listElement ? this.$(this.listElement) : this.$el;
  }


});
