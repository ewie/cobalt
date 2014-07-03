/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var rave = require('./rave');



/**
 * Get a widget from Wookie for a given widget instance.
 *
 * @param {object} instance a widget instance
 *
 * @return {object} a promise resolving to the given widget instance after
 *                  assigning the wookie widget
 */
function getWookieWidget(instance) {
  return rave.getWookieWidget(instance.widget.uri)
    .then(function (wookieWidget) {
      instance.wookie = wookieWidget;
      return instance;
    });
}



/**
 * Get all wookie widgets for an execution model.
 *
 * @param {Backbone.Model} execution an execution model
 *
 * @return {object} a promise resolving to an array of distinct widget
 *                  instances with assigned wookie widgets
 */
function getWookieWidgets(execution) {
  var dfds = [];

  execution.get('steps').forEach(function (step) {
    step.get('actions').forEach(function (action) {
      dfds.push(getWookieWidget(action.get('instance')));
      var comms = action.get('communications');
      if (comms) {
        comms.forEach(function (comm) {
          dfds.push(getWookieWidget(comm.instance));
        });
      }
    });
  });

  return $.Deferred.join(dfds)
    .then(function (instances) {
      // Instances may occur multiple times, because we may issued multiple
      // deferreds for an instances. Index each instance by it's unique ID to
      // get a collection of distinct instances.
      return _.chain(instances)
        .indexBy(function (instance) { return instance.id })
        .toArray()
        .value();
    });
}



/**
 * Load all widgets for an execution model.
 *
 * @param {Backbone.Model} execution an execution model
 *
 * @return {object} a promise resolving to an array of distinct widget
 *                  instances with assigned wookie widgets and region widgets
 */
function loadWidgets(execution) {
  return getWookieWidgets(execution)
    .then(function (instances) {

      var fns = instances.map(function (instance) {
        return function () {
          return rave.addWookieWidgetToPage(instance.wookie)
            .then(function (regionWidget) {
              instance.region = regionWidget;
              return instance;
            });
        };
      });

      // add the widgets one after another with a delay in between, otherwise
      // the request may happen to quickly for Rave to handle
      return $.Deferred.sequence(fns, 200);

    });
}



/**
 * Visually highlight a region widget.
 *
 * @param {object} regionWidget the region widget to highlight
 */
function highlightRegionWidget(regionWidget) {
  $(rave.getRegionWidgetElement(regionWidget))
    .flash(3000, 300, { boxShadow: '0 0 50px #ed600d' });
}



/**
 * Find a widget instance via its associated region widget's ID.
 *
 * @param {array}  instances      an array of widget instances
 * @param {string} regionWidgetId a region widget ID
 *
 * @return {object}    a widget instance with matching region widget
 * @return {undefined} when no widget instances has a matching region widget
 */
function findWidgetInstanceByRegionWidgetId(instances, regionWidgetId) {
  return _.find(instances, function (instance) {
    // compare the regionWidgetId as strings
    return instance.region.regionWidgetId.toString() === regionWidgetId.toString();
  });
}



/**
 * Find the action in an execution step, which corresponds to a given widget
 * instance.
 *
 * @param {Backbone.Model} step     an execution step
 * @param {object}         instance a widget instance
 *
 * @return {Backbone.Model} the corresponding action when present
 * @return {undefined}      when the step contains no corresponding action
 */
function findActionByWidgetInstance(step, instance) {
  return step.get('actions').find(function (action) {
    return action.get('instance') === instance;
  });
}



/**
 * Load an execution model in the current Rave page.
 *
 * @param {Backbone.Model} execution the execution model to load
 *
 * @return {object} a promise resolving the execution is loaded
 */
function load(execution) {
  return rave.clearRegionWidgets()
    .then(function () {
      loadWidgets(execution)
        .then(function (instances) {

          execution.get('steps').forEach(function (step) {
            step.get('actions').forEach(function (action) {
              action.on('highlight', function () {
                var instance = action.get('instance');
                var inst = _.find(instances, function (inst) {
                  return inst.id === instance.id;
                });
                if (inst) {
                  highlightRegionWidget(inst.region);
                }
              });
            });
          });

          rave.setPublishFilter(function (topic, data, pcont, scont) {
            var step = execution.getCurrentStep();

            var source = findWidgetInstanceByRegionWidgetId(instances, pcont.getClientID());
            var target = findWidgetInstanceByRegionWidgetId(instances, scont.getClientID());

            var action = findActionByWidgetInstance(step, source);
            var comms = action.get('communications');

            // do not allow the communication when the action has no
            // communications at all
            if (comms.length === 0) {
              return false;
            }

            // get the remaining communications, i.e. communications on a
            // different topic or with a different target
            var remainingComms = comms.filter(function (comm) {
              return comm.topic !== topic || comm.instance !== target;
            });

            action.set({
              communications: remainingComms,
              // we're done with the action when no communication remains
              done: remainingComms.length === 0
            });

            return true;
          });

        });
    });
}

module.exports = {

  /**
   * Check if the mashup is empty, i.e. no execution is loaded.
   *
   * @return {true}  when empty
   * @return {false} otherwise
   */
  get isEmpty() { return rave.isEmpty },

  load: load

};
