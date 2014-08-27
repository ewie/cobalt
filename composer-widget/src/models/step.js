/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


module.exports = Backbone.Model.extend({


  initialize: function () {
    this.on('change:actions', this._updateActions, this);
    this._updateActions();
  },


  isEmpty: function () {
    return this.get('actions').every(function (action) {
      return action.isEmpty();
    });
  },


  _updateActions: function () {
    var _actions = this.previous('actions');
    var actions = this.get('actions');
    _actions && _actions.off('change:done', this._done, this);
    actions && actions.on('change:done', this._done, this);
  },


  _done: function () {
    var done = this.get('actions').every(function (action) {
      return action.get('done');
    });
    done && this.set({ state: 'done' });
  }


});
