/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');

var Action = require('./action');
var Functionality = require('./functionality');


module.exports = value.define({

  request: { type: Functionality },
  offer: { type: Functionality },
  providingAction: { type: Action }

});
