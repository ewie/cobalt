/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 *   <http://directory.fsf.org/wiki/License:BSD_3Clause>
 */


var mediaTypes = require('./mediaTypes');
var jsonresolve = require('./util/jsonresolve');

var Plan = require('./plan/plan');



function requestPlans(model) {

  var attrs = model.attributes;

  var mashup;

  if (attrs.mediaType === mediaTypes.RDFJSON) {
    mashup = JSON.parse(attrs.goalMashup);
  } else {
    mashup = {
      mediaType: attrs.mediaType,
      content:   attrs.goalMashup
    };
  }

  var data = {
    mashup:                   mashup,
    minDepth:                 attrs.minPlanDepth,
    maxDepth:                 attrs.maxPlanDepth,
    actionComposition: {
      precursorActions:       attrs.precursorComposition,
      functionalityProviders: attrs.functionalityComposition,
      propertyProviders:      attrs.propertyComposition,
    }
  };

  var request = $.ajax({
    type:     'POST',
    url:      attrs.serviceUrl,
    data:     JSON.stringify(data),
    mimeType: mediaTypes.JSON,
    dataType: 'json',
    headers:  {
      // override the default header jQuery would use for JSON
      'accept': 'application/json; charset=utf-8; x-json-pointer=true'
    }
  });

  var deferred = $.Deferred();

  request.done(function (data) {
    if (data.plans.length === 0) {
      deferred.reject({ message: "no plans found" });
    } else {
      var result = jsonresolve.resolve(data);
      var plans = result.plans.map(function (plan) {
        return new Plan(plan);
      });
      deferred.resolve(plans);
    }
  });

  request.fail(function (xhr) {
    if (xhr.status) {
      deferred.reject(xhr.responseText && JSON.parse(xhr.responseText));
    } else {
      deferred.reject({ message: "could not reach service at given URL" });
    }
  });

  return deferred;

}



module.exports = {

  requestPlans: requestPlans

};
