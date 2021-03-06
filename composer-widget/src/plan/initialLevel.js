/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var _Set = require('../util/set');
var value = require('../util/value');

var FunctionalityProvision = require('./functionalityProvision');



module.exports = value.define({

  functionalityProvisions: {
    intern: function (v) { return new _Set(FunctionalityProvision.fromArray(v)) },
    extern: function (s) { return FunctionalityProvision.toArray(s) }
  },

  requiredActions: {
    lazy: function () {
      return this.functionalityProvisions.reduce(function (actions, fp) {
        actions.add(fp.providingAction);
        return actions;
      }, new _Set());
    }
  }

});
