/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var jsonpointer = require('json-pointer');
var rave = require('./rave');
var Plan = require('./plan/plan');


var KEY = rave.pageId;


function read() {
  return JSON.parse(window.localStorage.getItem(KEY)) || {};
}



function write(data) {
  return window.localStorage.setItem(KEY, JSON.stringify(data));
}



function get(pointer, defaultValue) {
  try {
    return jsonpointer.get(read(), pointer);
  } catch (e) {
    return defaultValue;
  }
}



function put(pointer, value) {
  var data = read();
  jsonpointer.set(data, pointer, value);
  write(data);
}



function remove(pointer) {
  var data = read();
  jsonpointer.remove(data, pointer);
  write(data);
}



function getRequest() {
  return get('/request');
}



function putRequest(request) {
  put('/request', request);
}



function getPlans() {
  return get('/plans', []).map(function (plan) {
    return new Plan(plan);
  });
}



function putPlans(plans) {
  put('/plans', plans);
}



function getPlan(id) {
  var plans = get('/plans', []);
  for (var i = 0; i < plans.length; i += 1) {
    var plan = plans[i];
    if (plan.id === id) {
      return new Plan(plan);
    }
  }
}



function getCurrentPlanId() {
  return get('/currentPlanId');
}



function putCurrentPlanId(id) {
  put('/currentPlanId', id);
}



function removeCurrentPlanId() {
  remove('/currentPlanId');
}



module.exports = {

  get: get,

  put: put,

  getRequest: getRequest,

  putRequest: putRequest,

  getPlans: getPlans,

  putPlans: putPlans,

  getPlan: getPlan,

  getCurrentPlanId: getCurrentPlanId,

  putCurrentPlanId: putCurrentPlanId,

  removeCurrentPlanId: removeCurrentPlanId

};
