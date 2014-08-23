/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var nextId = (function () {

  var value = 0;

  return function () {
    return value += 1;
  };

}());



module.exports = {

  nextId: nextId,

};
