/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonTaskProvisionSerializer extends JsonSerializer<TaskProvision> {

  private static final String offer = "offer";
  private static final String request = "request";
  private static final String provider = "provider";

  private final JsonSerializer<Action> actionSerializer;
  private final JsonSerializer<Task> taskSerializer;

  public JsonTaskProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                     final JsonSerializer<Task> taskSerializer) {
    this.actionSerializer = actionSerializer;
    this.taskSerializer = taskSerializer;
  }

  @Override
  public JsonObjectBuilder build(final TaskProvision provision) {
    return Json.createObjectBuilder()
        .add(request, taskSerializer.serialize(provision.getRequest()))
        .add(offer, taskSerializer.serialize(provision.getOffer().getSubject()))
        .add(provider, actionSerializer.serialize(provision.getProvidingAction()));
  }

}
