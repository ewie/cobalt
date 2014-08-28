/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var _Set = require('../util/set');
var value = require('../util/value');

var ActionProvision = require('./actionProvision');



module.exports = value.define({

  actionProvisions: {
    intern: function (v) { return new _Set(ActionProvision.fromArray(v)) },
    extern: function (s) { return ActionProvision.toArray(s) }
  },

  requiredActions: {
    lazy: function () {
      return this.actionProvisions.reduce(function (actions, ap) {
        actions.addAll(ap.requiredActions);
        return actions;
      }, new _Set());
    }
  }

});
