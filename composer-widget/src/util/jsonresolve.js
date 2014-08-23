/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var jsonpointer = require('json-pointer');

function callable(fn) { return fn.call.bind(fn) }

var hasOwn = callable(Object.prototype.hasOwnProperty);
var toString = callable(Object.prototype.toString);



function isObject(x) {
  return toString(x) === '[object Object]';
}



function isArray(x) {
  return toString(x) === '[object Array]';
}



var REF = '$ref';



/**
 * Check if an object is a JSON Pointer reference, i.e. it contains only the
 * property "$ref".
 *
 * @param {object} obj the object to check
 *
 * @return {true}  when `obj` is a reference object
 * @return {false} otherwise
 */
function isReference(obj) {
  return isObject(obj) &&
         hasOwn(obj, REF) &&
         Object.getOwnPropertyNames(obj).length === 1;
}



/**
 * Extract the JSON Pointer from a reference object.
 *
 * @param {object} obj an object containing only the property "$ref"
 *
 * @return {string} a JSON Pointer
 */
function getPointer(obj) {
  return obj[REF];
}



/**
 * Resolve references found in an object.
 *
 * @param {object}       obj  an object to resolve
 * @param {object|array} root a root object or array to resolve references
 *
 * @return {object} a copy of `obj` with resolved references
 */
function resolveObject(obj, root) {
  var copy = {};
  for (var name in obj) {
    if (!hasOwn(obj, name)) {
      continue;
    }
    copy[name] = resolve(obj[name], root);
  }
  return copy;
}



/**
 * Resolve references found in an array.
 *
 * @param {array}        ary  an array to resolve
 * @param {object|array} root a root object or array to resolve references
 *
 * @return {array} a copy of `ary` with resolved references
 */
function resolveArray(ary, root) {
  return ary.map(function (obj) {
    return resolve(obj, root);
  });
}



/**
 * Resolve JSON Pointer references found in a value against a root object or
 * array.
 *
 * @param {any}          value the value search for references
 * @param {object|array} root  a root object or array to resolve references
 *
 * @return {any} `value` with JSON Pointer references replaced with the
 *               referenced values
 */
function resolve(value, root) {
  if (isReference(value)) {
    // TODO detect cycle when a reference points to itself
    return resolve(jsonpointer.get(root, getPointer(value)), root);
  } else if (isArray(value)) {
    return resolveArray(value, root);
  } else if (isObject(value)) {
    return resolveObject(value, root);
  } else {
    return value;
  }
}



module.exports = {

  /**
   * Resolve JSON Pointer references in an object or array.
   *
   * @param {object|array} struct
   *
   * @return {object|array} `struct` with JSON Pointer references replaced with
   *                        the references values
   */
  resolve: function (struct) {
    return resolve(struct, struct);
  }

};
