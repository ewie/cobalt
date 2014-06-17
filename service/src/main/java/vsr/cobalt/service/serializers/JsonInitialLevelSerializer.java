/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonInitialLevelSerializer extends JsonSerializer<InitialLevel> {

  private static final String taskProvisions = "taskProvisions";

  private final JsonSerializer<TaskProvision> taskProvisionSerializer;

  public JsonInitialLevelSerializer(final JsonSerializer<TaskProvision> taskProvisionSerializer) {
    this.taskProvisionSerializer = taskProvisionSerializer;
  }

  @Override
  public JsonObjectBuilder build(final InitialLevel level) {
    return Json.createObjectBuilder()
        .add(taskProvisions, taskProvisionSerializer.serializeAll(level.getTaskProvisions()));
  }

}
