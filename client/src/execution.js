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
var Map = require('./util/map');



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
      index = new Map();
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
        .getOrPut(level, function () { return new Map() })
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
      instruction: interaction.instruction,
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
 *
 * @return {array} an array communications
 */
function getCommunications(action, level, prevLevel) {
  return (level.actionProvisions || []).reduce(function (comms, ap) {
    return ap.propertyProvisions.reduce(function (comms, pp) {
      if (pp.provider.equals(action)) {
        if (pp.offer.name !== pp.request.name) {
          throw new Error("cannot realize communication for property provision with offer and request of differing names");
        }
        comms.push({
          topic: pp.offer.name,
          instance: instances.get(ap.request, prevLevel)
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
  var index = new Map();

  // Get the widget instance for each precursor action, by using the widget
  // instance of requested action in the previous level. This applies only to
  // extension levels.
  if (level.actionProvisions) {
    level.actionProvisions.forEach(function (actionProvision) {
      var precursor = actionProvision.precursor;
      if (precursor) {
        index.putWhenMissing(precursor, function () {
          return {
            done:           false,
            interactions:   getInteractions(precursor),
            communications: getCommunications(precursor, level, prevLevel),
            instance:       instances.get(actionProvision.request, prevLevel)
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
  var steps = plan.levels.map(function (level, index) {
    return createStep(plan.depth - index, level, plan.level(index - 1));
  });

  // Reverse the steps as they were constructed in extension order, i.e.
  // backwards in time.
  steps.reverse();

  return new ExecutionModel({
    steps: new StepCollection(steps)
  });
}



module.exports = {

  createExecution: createExecution

};
