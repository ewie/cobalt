/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var mediaTypes = require('../mediaTypes');



function isEmpty(x) {
  return !x || x.length === 0 || /^\s*$/.test(x);
}



function isUrl(s) {
  return !isEmpty(s) && /^\s*https?:\/\/[^\/]+(?:\/.*)?\s*$/.test(s);
}



module.exports = Backbone.Model.extend({


  initialize: function () {
    this.on('change:goalMashup', this._guessMediaType, this);
  },


  validate: function (attrs) {
    var err = {};
    this._validateGoalMashup(attrs, err);
    this._validateServiceUrl(attrs, err);
    this._validatePlanDepth(attrs, err);
    if (!_.isEmpty(err)) {
      return err;
    }
  },


  _validateGoalMashup: function (attrs, err) {
    if (isEmpty(attrs.goalMashup)) {
      err.goalMashup = "must not be empty";
    }
  },


  _validateServiceUrl: function (attrs, err) {
    if (!isUrl(attrs.serviceUrl)) {
      err.serviceUrl = "must be a valid URL";
    }
  },


  _validatePlanDepth: function (attrs, err) {
    var min = attrs.minPlanDepth;
    var max = attrs.maxPlanDepth;

    var minIsNum = _.isNumber(min);
    var maxIsNum = _.isNumber(max);

    if (minIsNum && min < 1) {
      err.minPlanDepth = "must be a positive integer";
    }

    if (maxIsNum && max < 1) {
      err.maxPlanDepth = "must be a positive integer";
    }

    if (minIsNum && maxIsNum && min > max) {
      err.minPlanDepth = "must be less then or equal to max plan depth";
      err.maxPlanDepth = "must be greater then or equal to min plan depth";
    }
  },


  _guessMediaType: function () {
    var m = this.get('goalMashup');
    var mt = mediaTypes.guess(m);
    mt && this.set({ mediaType: mt });
  }


});
