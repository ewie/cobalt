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
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonPropertyProvisionSerializer extends JsonSerializer<PropertyProvision> {

  private static final String offer = "offer";
  private static final String request = "request";
  private static final String provider = "provider";

  private final JsonSerializer<Action> actionSerializer;
  private final JsonSerializer<Property> propertySerializer;

  public JsonPropertyProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                         final JsonSerializer<Property> propertySerializer) {
    this.actionSerializer = actionSerializer;
    this.propertySerializer = propertySerializer;
  }

  @Override
  public JsonObjectBuilder build(final PropertyProvision provision) {
    return Json.createObjectBuilder()
        .add(request, propertySerializer.serialize(provision.getRequest()))
        .add(offer, propertySerializer.serialize(provision.getOffer().getSubject()))
        .add(provider, actionSerializer.serialize(provision.getProvidingAction()));
  }

}
