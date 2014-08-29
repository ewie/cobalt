/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var value = require('../util/value');
var seqid = require('../util/seqid');

var InitialLevel = require('./initialLevel');
var ExtensionLevel = require('./extensionLevel');



function ratePlan(plan) {
  return rateInitialLevel(plan.initialLevel) +
    rateExtensionLevels(plan.extensionLevels);
}

function rateInitialLevel(level) {
  return rateLevel(level) +
    level.functionalityProvisions.reduce(function (rating, fp) {
      return rating + fp.distance;
    }, 0);
}

function rateExtensionLevels(levels) {
  return levels.reduce(function (rating, level) {
    return rating + rateExtensionLevel(level);
  }, 0);
}

function rateExtensionLevel(level) {
  return rateLevel(level) +
    level.actionProvisions.reduce(function (rating, ap) {
      return rating + rateActionProvision(ap);
    }, 0);
}

function rateActionProvision(ap) {
  return ap.propertyProvisions.reduce(function (rating, pp) {
    return rating + pp.distance;
  }, 0);
}

function rateLevel(level) {
  var actions = level.requiredActions;
  return actions.reduce(function (rating, action) {
    return rating + rateAction(action);
  }, actions.size);
}

function rateAction(action) {
  return action.interactions.size;
}



module.exports = value.define({

  id: {
    intern: function (id) {
      return id || seqid.nextId();
    }
  },

  initialLevel: {
    type: InitialLevel
  },

  extensionLevels: {
    type: ExtensionLevel,
    array: true
  },

  rating: {
    lazy: function () { return ratePlan(this) }
  },

  depth: {
    lazy: function () {
      return 1 + this.extensionLevels.length;
    }
  },

  levels: {
    lazy: function () {
      return [this.initialLevel].concat(this.extensionLevels);
    }
  },

  widgetCount: {
    lazy: function () {
      return this.levels.reduce(function (r, level) {
        return level.requiredActions.reduce(function (r, action) {
          var widget = action.widget;
          var key = widget.uri || widget.id;
          if (!r.index[key]) {
            r.count += 1;
            r.index[key] = true;
          }
          return r;
        }, r);
      }, { count: 0, index: {} }).count;
    }
  },

  interactionCount: {
    lazy: function () {
      return this.levels.reduce(function (count, level) {
        return level.requiredActions.reduce(function (count, action) {
          return count + action.interactions.size;
        }, count);
      }, 0);
    }
  }

}, {

  get stepCount() {
    return this.depth;
  },

  level: function (index) {
    if (index === 0) {
      return this.initialLevel;
    }
    return this.extensionLevels[index - 1];
  }

});
