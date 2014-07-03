/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');
var uuid = require('uuid');

var InitialLevel = require('./initialLevel');
var ExtensionLevel = require('./extensionLevel');



module.exports = value.define({

  id: {
    intern: function (id) {
      return id || uuid.v4();
    }
  },
  rating: undefined,
  initialLevel: {
    type: InitialLevel
  },
  extensionLevels: {
    type: ExtensionLevel,
    array: true
  }

}, {

  get depth() {
    return 1 + this.extensionLevels.length;
  },

  get levels() {
    return [this.initialLevel].concat(this.extensionLevels);
  },

  level: function (index) {
    if (index === 0) {
      return this.initialLevel;
    }
    return this.extensionLevels[index - 1];
  }

});
