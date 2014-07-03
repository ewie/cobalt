/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');

var Action = require('./action');
var Task = require('./task');


module.exports = value.define({

  request: { type: Task },
  offer: { type: Task },
  provider: { type: Action }

});
