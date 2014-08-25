/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import java.util.Set;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonActionProvisionSerializer extends JsonSerializer<ActionProvision> {

  private static final String requestedAction = "requestedAction";
  private static final String precursorAction = "precursorAction";
  private static final String propertyProvisions = "propertyProvisions";

  private final JsonSerializer<Action> actionSerializer;
  private final JsonSerializer<PropertyProvision> propertyProvisionSerializer;

  public JsonActionProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                       final JsonSerializer<PropertyProvision> propertyProvisionSerializer) {
    this.actionSerializer = actionSerializer;
    this.propertyProvisionSerializer = propertyProvisionSerializer;
  }

  @Override
  public JsonObjectBuilder build(final ActionProvision provision) {
    final JsonObjectBuilder obj = Json.createObjectBuilder();
    obj.add(requestedAction, actionSerializer.serialize(provision.getRequestedAction()));
    if (provision.getPrecursorAction() != null) {
      obj.add(precursorAction, actionSerializer.serialize(provision.getPrecursorAction()));
    }
    final Set<PropertyProvision> pps = provision.getPropertyProvisions();
    if (!pps.isEmpty()) {
      obj.add(propertyProvisions, propertyProvisionSerializer.serializeAll(pps));
    }
    return obj;
  }

}
