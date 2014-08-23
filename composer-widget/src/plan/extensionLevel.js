/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var Set = require('../util/set');
var value = require('../util/value');

var ActionProvision = require('./actionProvision');



module.exports = value.define({

  actionProvisions: {
    intern: function (v) { return new Set(ActionProvision.fromArray(v)) },
    extern: function (s) { return ActionProvision.toArray(s) }
  }

}, {

  get requiredActions() {
    return this.actionProvisions.reduce(function (actions, ap) {
      actions.addAll(ap.requiredActions);
      return actions;
    }, new Set());
  }

});
