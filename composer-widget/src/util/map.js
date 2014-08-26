/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('./value');


function findEntry(entries, key) {
  for (var i = 0; i < entries.length; i += 1) {
    var entry = entries[i];
    if (value.equals(entry[0], key)) {
      return entry;
    }
  }
}



/**
 * A mapping from keys of any type to values of any type.
 *
 * Key equality is based on object identity (===) or, when applicable, the
 * result of `key.equals(other)`.
 */
var _Map = module.exports = function _Map() {
  this._entries = [];
};



_Map.prototype = {


  /**
   * @return {array} an array containing all mapped values
   */
  values: function () {
    return this._entries.map(function (entry) { return entry[1] });
  },


  /**
   * Check if a mapping with a given key exists.
   *
   * @param {any} key
   *
   * @return {true}  when the mapping contains the key `key`
   * @return {false} otherwise
   */
  has: function (key) {
    return !!findEntry(this._entries, key);
  },


  /**
   * Get the value associated with a given key.
   *
   * @param {any} key
   *
   * @return {any}       the value associated with `key`
   * @return {undefined} when no value is associated with `key`
   */
  get: function (key) {
    var entry = findEntry(this._entries, key);
    if (entry) {
      return entry[1];
    }
  },


  /**
   * Get the value associated with a given key, or create a new mapping when no
   * value is associated with the key.
   *
   * @param {any}      key
   * @param {function} valueFn a function creating a value, only invoked when
   *                           necessary
   *
   * @return {any} the value associated with key (possible created by `valueFn`)
   */
  getOrPut: function (key, valueFn) {
    var entry = findEntry(this._entries, key);
    if (!entry) {
      entry = [ key, valueFn() ];
      this._entries.push(entry);
    }
    return entry[1];
  },


  /**
   * Associate a key with a value. Any existing mapping with that key will be
   * replaced.
   *
   * @param {any} key
   * @param {any} value
   */
  put: function (key, value) {
    var entry = findEntry(this._entries, key);
    if (entry) {
      entry[1] = value;
    } else {
      this._entries.push([ key, value ]);
    }
  },


  /**
   * Create a mapping for a given key, when no such mapping yet exists.
   *
   * @param {any}      key
   * @param {function} valueFn a function creating a value, only invoked when
   *                           necessary
   */
  putWhenMissing: function (key, valueFn) {
    var entry = findEntry(this._entries, key);
    if (!entry) {
      this._entries.push([ key, valueFn() ]);
    }
  }


};
