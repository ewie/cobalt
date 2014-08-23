/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


function mediaType(name, pattern) {
  return {
    name:    name,
    pattern: pattern
  };
}


var RDFJSON = mediaType('application/rdf+json', /^\s*\{/);
var RDFXML  = mediaType('application/rdf+xml',  /^\s*</);
var TURTLE  = mediaType('text/turtle',          /^\s*@prefix/i);

var MEDIA_TYPES = [ RDFJSON, RDFXML, TURTLE ];


/**
 * Guess the media type of some textual data.
 *
 * @param {string} s
 *
 * @return {string}    the determined media type
 * @return {undefined} when the media type cannot be determined
 */
function guess(s) {
  for (var i = 0; i < MEDIA_TYPES.length; i += 1) {
    var mt = MEDIA_TYPES[i];
    if (mt.pattern.test(s)) {
      return mt.name;
    }
  }
}


module.exports = {

  JSON:    'application/json',

  RDFJSON: RDFJSON.name,
  RDFXML:  RDFXML.name,
  TURTLE:  TURTLE.name,

  guess: guess

};
