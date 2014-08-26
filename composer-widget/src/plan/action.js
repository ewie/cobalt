/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');
var _Set = require('../util/set');

var Interaction = require('./interaction');
var Property = require('./property');
var Functionality = require('./functionality');
var Widget = require('./widget');



function asSet(type) {
  return function (ary) {
    return new _Set(type.fromArray(ary));
  };
}



function conv(type) {
  return {
    intern: asSet(type),
    extern: type.toArray.bind(type)
  };
}



module.exports = value.define({

  widget: { type: Widget },
  publishedProperties: conv(Property),
  realizedFunctionalities: conv(Functionality),
  interactions: conv(Interaction)

});
