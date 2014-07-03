/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var Set = require('../util/set');
var value = require('../util/value');

var TaskProvision = require('./taskProvision');



module.exports = value.define({

  taskProvisions: {
    intern: function (v) { return new Set(TaskProvision.fromArray(v)) },
    extern: function (s) { return TaskProvision.toArray(s) }
  }

}, {

  get requiredActions() {
    return this.taskProvisions.reduce(function (actions, tp) {
      actions.add(tp.provider);
      return actions;
    }, new Set());
  }

});
