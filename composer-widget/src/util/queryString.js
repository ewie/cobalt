/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */



/**
 * Get a value from a query string using a key.
 *
 * @param {string} qs  a query string
 * @param {string} key
 *
 * @return {string}    the value associated with the given key
 * @return {undefined} when the query string does not contain `key`
 */
function get(qs, key) {
  var params = parse(qs);
  return params[key];
}



/**
 * Set a value in a query string based on a key. Any existing value under the
 * same key will be replaced.
 *
 * @param {string} qs a query string
 * @param {string} key
 * @param {.toString()} value
 *
 * @return {string} a query string containing the new value
 */
function set(qs, key, value) {
  var params = parse(qs);
  if (params[key] === value) {
    return qs;
  }
  params[key] = value;
  return stringify(params);
}



/**
 * Parse a query string. Treats each key as a plain string, i.e. it does not
 * resolve `"key[0]=1&key[1]=2"` to `{ key: [0, 1] }`. When the query string
 * contains the same key multiple times, the last occurence takes precedence.
 *
 * @param {string} qs a query string
 *
 * @return {object} an object containing each key/value pair
 */
function parse(qs) {
  var pairs = qs.split('&');
  return pairs.reduce(function (params, pair) {
    var ps = pair.split('=');
    var name = decodeURIComponent(ps[0]);
    var value = decodeURIComponent(ps[1]);
    params[name] = value;
    return params;
  }, {});
}



/**
 * Serialize an object as a query string.
 *
 * @param {object} params an object to serialize as query string
 *
 * @return {string} the query string encoding `object`
 */
function stringify(params) {
  return Object.getOwnPropertyNames(params).map(function (name) {
    return encodeURIComponent(name) + '=' + encodeURIComponent(params[name]);
  }).join('&');
}



module.exports = {

  get: get,

  set: set,

  parse: parse,

  stringify: stringify

};


