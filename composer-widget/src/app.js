/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var RequestModel = require('./models/request');
var Plan = require('./plan/plan');
var PlanCollection = require('./models/plans');
var ExecutionModel = require('./models/execution');

var ServiceView = require('./views/service');
var PlansView = require('./views/plans');
var ExecutionView = require('./views/execution');

var Router = require('./router');

var store = require('./store');
var execution = require('./execution');
var mashup = require('./mashup');



function createRouter() {
  return new Router({
    key: 'cobalt.view',
    routes: {
      '':                'root',
      'service':         'service',
      'plans':           'plans',
      'execution(/:id)': 'execution'
    }
  });
}



function createRequestModel() {
  var m = new RequestModel(store.getRequest());
  m.on('change', function () {
    store.putRequest(m.attributes);
  });
  return m;
}



function createPlansCollection() {
  var plans = store.getPlans().map(function (plan) {
    return {
      id: plan.id,
      rating: plan.rating,
      plan: plan
    };
  });
  return new PlanCollection(plans);
}



function createExecutionModel() {
  return new ExecutionModel();
}



function createServiceView() {
  return new ServiceView({
    model: createRequestModel(),
    el: $('#service-section')
  }).render();
}



function createPlansView() {
  return new PlansView({
    collection: createPlansCollection(),
    el: $('#plans-section')
  }).render();
}



function createExecutionView() {
  return new ExecutionView({
    model: createExecutionModel(),
    el: $('#execution-section')
  }).render();
}



function focusSection(name) {
  $('body').attr('data-section', name);
}



function loadExecution(options) {
  var currentId = store.getCurrentPlanId();
  var id = options.planId || currentId;

  var force = options.force;
  var plans = options.plans;
  var exec = options.execution;

  var plan = plans.get(id);

  // TODO how to handle an ID for which no plan exists

  var shouldLoad = force || id !== currentId || mashup.isEmpty;

  if (plan && shouldLoad) {
    var x = execution.createExecution(plan.get('plan'));

    exec.set(x.attributes);
    mashup.load(exec);

    store.putCurrentPlanId(plan.id);
    $('menu').attr('data-has-execution', true);
  }
}



function clearExecution() {
  mashup.clear();
  store.putCurrentPlanId(null);
  $('menu').attr('data-has-execution', false);
}



// guards against multiple initializations
var initialized;



module.exports = {

  init: function () {
    if (initialized) {
      throw new Error("application already initialized");
    }

    initialized = true;

    var router = this.router = createRouter();

    function redirect(hash) {
      router.navigate(hash, { trigger: true, replace: true });
    }

    var serviceView = this.service = createServiceView();
    var plansView = this.plans = createPlansView();
    var executionView = this.execution = createExecutionView();

    executionView.model.on('clear', function () {
      clearExecution();
      redirect('plans');
    });

    executionView.model.on('reset', function () {
      loadExecution({
        execution: executionView.model,
        plans:     plansView.collection,
        force:     true
      });
    });

    if (store.getCurrentPlanId()) {
      $('menu').attr('data-has-execution', true);
    }

    serviceView.on('plans', function (plans) {
      redirect('plans');

      // store ALL plans returned by the service
      store.putPlans(Plan.toArray(plans));

      // filter out non-executable plans
      var supportedPlans = plans.filter(execution.isExecutable);
      plansView.collection.reset(supportedPlans.map(function (plan) {
        return {
          id:     plan.id,
          rating: plan.rating,
          plan:   plan
        };
      }));
    });

    router.on('route:root', function () {
      redirect('service');
    });

    router.on('route:service', function () {
      focusSection('service');
    });

    router.on('route:plans', function () {
      focusSection('plans');
    });

    router.on('route:execution', function (id) {
      if (!id) {
        id = store.getCurrentPlanId();

        if (id) {
          // redirect to canonical path
          redirect('execution/' + id);
        } else {
          // redirect to the root when there's no current plan
          redirect('');
        }

        return;
      }

      focusSection('execution');

      loadExecution({
        planId:    id,
        execution: executionView.model,
        plans:     plansView.collection
      });
    });

    Backbone.history.start();
  }

};
