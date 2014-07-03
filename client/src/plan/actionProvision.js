/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');
var Set = require('../util/set');

var Action = require('./action');
var PropertyProvision = require('./propertyProvision');



module.exports = value.define({

  request: { type: Action },
  precursor: { type: Action },
  propertyProvisions: {
    intern: function (v) { return new Set(PropertyProvision.fromArray(v)) },
    extern: function (s) { return PropertyProvision.toArray(s) }
  }

}, {

  get requiredActions() {
    var actions = new Set();
    this.precursor && actions.add(this.precursor);
    return this.propertyProvisions.reduce(function (actions, pp) {
      actions.add(pp.provider);
      return actions;
    }, actions);
  }

});
