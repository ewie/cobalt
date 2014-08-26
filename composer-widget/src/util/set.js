/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('./value');



/**
 * A collection of unique values.
 *
 * Value equality is based on object identity (===) or, when applicable, the
 * result of `value.equals(other)`.
 */
var _Set = module.exports = function _Set(items) {
  this._items = [];
  items && this.addAll(items);
};



_Set.prototype = {


  /**
   * @return {number} the number of contained values
   */
  get size() { return this._items.length },


  /**
   * Check if the set contains an item.
   *
   * @param {any} item
   *
   * @return {true}  when the set contains `item`
   * @return {false} otherwise
   */
  has: function (item) {
    for (var i = 0; i < this._items.length; i += 1) {
      if (value.equals(item, this._items[i])) {
        return true;
      }
    }
    return false;
  },


  /**
   * Add an item to this set.
   *
   * @param {any} item
   */
  add: function (item) {
    this.has(item) || this._items.push(item);
  },


  /**
   * Add a collection of items to this set.
   *
   * @param {_Set|array} items
   */
  addAll: function (items) {
    items.forEach(this.add.bind(this));
  },


  /**
   * Test this set for equality with another value.
   *
   * A set is equal to another value, when the other value is also a set and
   * contains the same items as this set and no more.
   *
   * @return {true}  when equal
   * @return {false} otherwise
   */
  equals: function (other) {
    return other instanceof _Set &&
           this.size === other.size &&
           this.every(function (item) { return other.has(item) });
  },


  /**
   * @return {array} an array containing all items
   */
  toArray: function () {
    return [].concat(this._items);
  },


  /**
   * @see Array.prototype.forEach()
   */
  forEach: function (fn) {
    this._items.forEach(function (item) { fn(item) });
  },


  /**
   * @see Array.prototype.some()
   */
  some: function (fn) {
    return this._items.some(function (item) { return fn(item) });
  },


  /**
   * @see Array.prototype.every()
   */
  every: function (fn) {
    return this._items.every(function (item) { return fn(item) });
  },


  /**
   * @see Array.prototype.reduce()
   */
  reduce: function (fn, initial) {
    return this._items.reduce(function (previous, item) {
      return fn(previous, item);
    }, initial);
  },


  /**
   * @see Array.prototype.map()
   */
  map: function (fn) {
    return this._items.map(function (item) { return fn(item) });
  }


};
