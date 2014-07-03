/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var resolveIds = (function () {
  var counter = 0;

  var proto = {
    value: function (id) {
      if (!this.hasOwnProperty(id)) {
        this[id] = counter++;
      }
      return this[id];
    },
    id: function (id) {
      return '-' + id + '-' + this.value(id);
    }
  };

  return function (html) {
    var index = Object.create(proto);
    return html.replace(/\{([^}]+)\}/g, function (_, id) {
      return index.id(id);
    });
  };
}());



module.exports = {

  get: function (name) {
    var e = $('template#' + name + '-template');
    if (!e.length) {
      throw new Error("unknown template: " + name);
    }
    return resolveIds(e.html());
  }

};
