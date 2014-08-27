/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var ExecutionModel = require('./models/execution');
var StepModel = require('./models/step');
var StepCollection = require('./models/steps');
var InteractionCollection = require('./models/interactions');
var ActionCollection = require('./models/actions');
var _Map = require('./util/map');



/**
 * An index for widget instances created during execution model creation.
 */
var instances = (function () {

  var nextId = 0;

  var index;

  return {

    /**
     * Clear the widget instances, important to avoid memory leaks, as widget
     * instances are held via strong references.
     */
    clear: function () {
      index = new _Map();
    },

    /**
     * Get the widget instance for an action in a specific level.
     *
     * @param {object} action
     * @param {object} level
     *
     * @return {object} the widget instance
     */
    get: function (action, level) {
      level || (level = null);
      return index
        .getOrPut(level, function () { return new _Map() })
        .getOrPut(action, function () {
          return {
            id: nextId++,
            widget: action.widget
          };
        });
    }

  };

}());



/**
 * Get the interactions for an action.
 *
 * @param {object} action
 * @param {object} level
 *
 * @return {Backbone.Collection} a collection of interactions
 */
function getInteractions(action, level) {
  var instance = instances.get(action, level);

  return action.interactions.reduce(function (interactions, interaction) {
    interactions.add({
      instructionText: interaction.instructionText,
      widget: instance
    });
    return interactions;
  }, new InteractionCollection());
}



/**
 * Get the communications for an action.
 *
 * @param {object} action
 * @param {object} level
 * @param {object} prevLevel the previous level, i.e. the level which `level`
 *                           extends on
 *
 * @return {array} an array of communications
 */
function getCommunications(action, level, prevLevel) {
  return (level.actionProvisions || []).reduce(function (comms, ap) {
    return ap.propertyProvisions.reduce(function (comms, pp) {
      if (pp.providingAction.equals(action)) {
        if (pp.offer.name !== pp.request.name) {
          throw new Error("cannot realize communication for property provision with offer and request of differing names");
        }
        comms.push({
          topic: pp.offer.name,
          instance: instances.get(ap.requestedAction, prevLevel)
        });
      }
      return comms;
    }, comms);
  }, []);
}



/**
 * Extract action models from a level.
 *
 * @param {object} level
 * @param {object} prevLevel the previous level, i.e. the level which `level`
 *                           extends on
 *
 * @return {Backbone.Collection} a collection of action models
 */
function getActions(level, prevLevel) {
  var index = new _Map();

  // Get the widget instance for each precursor action, by using the widget
  // instance of requested action in the previous level. This applies only to
  // extension levels.
  if (level.actionProvisions) {
    level.actionProvisions.forEach(function (ap) {
      var pa = ap.precursorAction;
      if (pa) {
        index.putWhenMissing(pa, function () {
          return {
            done:           false,
            interactions:   getInteractions(pa),
            communications: getCommunications(pa, level, prevLevel),
            instance:       instances.get(ap.requestedAction, prevLevel)
          };
        });
      }
    });
  }

  level.requiredActions.forEach(function (action) {
    index.putWhenMissing(action, function () {
      return {
        done:           false,
        interactions:   getInteractions(action),
        communications: getCommunications(action, level, prevLevel),
        instance:       instances.get(action, level)
      };
    });
  });

  return new ActionCollection(index.values());
}



/**
 * Create a step model for a level.
 *
 * @param {number} number    the step number
 * @param {object} level
 * @param {object} prevLevel the previous level, i.e. the level which `level`
 *                           extends on
 *
 * @return {Backbone.Model} a step model
 */
function createStep(number, level, prevLevel) {
  return new StepModel({
    number: number,
    actions: getActions(level, prevLevel),
    state: number === 1 ? 'current' : 'open',
    expanded: number === 1
  });
}



/**
 * Create an execution model from a plan.
 *
 * @param {object} plan a plan as returned by the planner service
 *
 * @return {Backbone.Model} an execution model
 */
function createExecution(plan) {
  // clear the widget instances, important to avoid memory leaks
  instances.clear();

  // Create a step per level by iterating in extension order, i.e. start with
  // the initial level.
  var steps = plan.levels.reduce(function (steps, level, index) {
    var step = createStep(plan.depth - index, level, plan.level(index - 1));
    if (!step.isEmpty()) {
      // Collect steps in reverse order as they are constructed in extension
      // order, i.e. converse to execution order.
      steps.unshift(step);
    }
    return steps;
  }, []);

  return new ExecutionModel({
    steps: new StepCollection(steps)
  });
}



/**
 * Check if a plan can be executed by the underlying execution model.
 *
 * @param {object} plan a plan to check
 *
 * @return {true}  when executable
 * @return {false} when not executable
 */
function isExecutable(plan) {
  return plan.extensionLevels.every(function (xl) {
    return xl.requiredActions.every(function (a) {
      // Every non-terminal action must have at least one interaction.
      // Terminal actions (initial level) need no interactions.
      return !a.interactions.isEmpty;
    }) || xl.actionProvisions.every(function (ap) {
      return ap.propertyProvisions.every(function (pp) {
        // Offered properties must have the same name as their respective
        // requested properties to make communication possible.
        return pp.offer.name === pp.request.name;
      });
    });
  });
}




module.exports = {

  createExecution: createExecution,

  isExecutable: isExecutable

};
