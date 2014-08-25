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
import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonFunctionalityProvisionSerializer extends JsonSerializer<FunctionalityProvision> {

  private static final String offer = "offer";
  private static final String request = "request";
  private static final String providingAction = "providingAction";

  private final JsonSerializer<Action> actionSerializer;
  private final JsonSerializer<Functionality> functionalitySerializer;

  public JsonFunctionalityProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                              final JsonSerializer<Functionality> functionalitySerializer) {
    this.actionSerializer = actionSerializer;
    this.functionalitySerializer = functionalitySerializer;
  }

  @Override
  public JsonObjectBuilder build(final FunctionalityProvision provision) {
    return Json.createObjectBuilder()
        .add(request, functionalitySerializer.serialize(provision.getRequest()))
        .add(offer, functionalitySerializer.serialize(provision.getOffer().getSubject()))
        .add(providingAction, actionSerializer.serialize(provision.getProvidingAction()));
  }

}
