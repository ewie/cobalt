/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.service.JsonSerializer;
import vsr.cobalt.service.serializers.CachingJsonSerializers;

/**
 * @author Erik Wienhold
 */
public class JsonPlannerResponseSerializer extends JsonSerializer<PlannerResponse> {

  private static final String message = "message";
  private static final String plans = "plans";

  @Override
  public JsonObjectBuilder build(final PlannerResponse response) {
    final JsonObjectBuilder obj = Json.createObjectBuilder();
    if (response.isSuccess()) {
      obj.add(plans, CachingJsonSerializers.plans.serializeAll(response.getPlans()));
    } else {
      obj.add(message, response.getCause().getMessage());
    }
    return obj;
  }

}
