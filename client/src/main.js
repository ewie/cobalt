/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var app = require('./app');

var escapeCssString = (function () {

  var C = {
    '"': '\\"',
    "'": "\\'",
    '\n': '\\A'
  };

  return function escapeCssString(s) {
    // http://www.w3.org/TR/CSS21/syndata.html#strings
    return s.replace(/['"\n]/g, function (c) { return C[c] });
  };

}());



$.fn.extend({

  byName: function (name) {
    return this.find('[name="' + escapeCssString(name) + '"]');
  },

  setValid: function () {
    return this.setValidity("");
  },

  setError: function (message) {
    return this.setValidity(message);
  },

  setValidity: function (message) {
    this[0].setCustomValidity(message || '');
    return this;
  },

  /**
   * Flash an element by setting some CSS styling and reseting it after a
   * certain delay.
   *
   * @param {number} duration the duration in milliseconds
   * @param {number} delay    the delay in milliseconds between updates to the
   *                          element style
   * @param {object} style    an object with CSS properties
   */
  flash: function (duration, delay, style) {
    var offStyle = Object.getOwnPropertyNames(style)
      .reduce(function (offStyle, property) {
        offStyle[property] = '';
        return offStyle;
      }, {});

    if (this.data('fx.flash')) {
      return;
    }

    this.data('fx.flash', true);
    var count = Math.floor(duration / delay) || 1;

    (function next(e, i) {
      if (i) {
        e.css(style);
        setTimeout(function () {
          e.css(offStyle);
          setTimeout(function () {
            next(e, i -= 1);
          }, delay);
        }, delay);
      } else {
        e.data('fx.flash', false);
      }
    }(this, count));
  }

});



/**
 * Create a deferred and return its promise.
 *
 * @param {function} fn the function passed to $.Deferred()
 *
 * @return {object} a promise
 */
$.Deferred.promise = function (fn) {
  return $.Deferred(fn).promise();
};



/**
 * @param {array} deferreds an array of deferreds
 *
 * @return {object} a promise resolving to an array containing each deferreds
 *                  resolved value (in the same order)
 */
$.Deferred.join = function (deferreds) {
  return $.when.apply($, deferreds)
    .then(function () {
      return _.toArray(arguments);
    });
};



/**
 * Resolve a sequence of deferreds, by creating each deferred as soon as the
 * previous deferred resolved.
 *
 * @param {array}  fns   an array of functions, each returning a deferred
 * @param {number} delay the delay in milliseconds before creating the next
 *                       deferred
 *
 * @return {object} a promise resolving when the last deferred resolves.
 */
$.Deferred.sequence = function (fns, delay) {
  if (!fns || !fns.length) {
    return $.Deferred().resolve().promise();
  }

  delay || (delay = 0);

  var results = [];

  var lastIndex = fns.length - 1;
  var dfd = $.Deferred();

  (function next(index, promise) {
    // create the next promise and push its result when available
    var p = fns[index]()
      .then(function (r) {
        results.push(r);
        if (index === lastIndex) {
          dfd.resolve(results);
        }
      });

    // set up the promise for the next step, either chain on the promise from
    // the previous step or use the promise from the current step (the very
    // first step)
    promise = promise ? promise.then(p) : p;

    if (index < lastIndex) {
      // delay the next step
      setTimeout(function () {
        next(index + 1, promise);
      }, delay);
    }
  }(0));

  return dfd.promise();
};




function removeFragment(url) {
  return url.replace(/#.*$/, '');
}



function getFragment(url) {
  var match = /#(.*)$/.exec(url);
  return match && match[1];
}



function getLocalFragment(url) {
  var thisUrl = location.href;
  var _thisUrl = removeFragment(thisUrl);
  var _url = removeFragment(url);
  if (_url === _thisUrl) {
    return getFragment(url);
  }
}



$(function () {

  app.init();

  $('body').on('click', 'a', function (ev) {
    var fragment = getLocalFragment(ev.target.href);
    if (fragment) {
      app.router.navigate(fragment, { trigger: true, replace: true });
      ev.preventDefault();
    }
  });

});
