/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');
var _Set = require('../util/set');

var Action = require('./action');
var PropertyProvision = require('./propertyProvision');



module.exports = value.define({

  requestedAction: { type: Action },

  precursorAction: { type: Action },

  propertyProvisions: {
    intern: function (v) { return new _Set(PropertyProvision.fromArray(v)) },
    extern: function (s) { return PropertyProvision.toArray(s) }
  },

  requiredActions: {
    lazy: function () {
      var actions = new _Set();
      this.precursorAction && actions.add(this.precursorAction);
      return this.propertyProvisions.reduce(function (actions, pp) {
        actions.add(pp.providingAction);
        return actions;
      }, actions);
    }
  }

});
