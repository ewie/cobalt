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
import vsr.cobalt.planner.graph.Provision;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonProvisionSerializer<T, P extends Provision<T>> extends JsonSerializer<P> {

  private static final String offer = "offer";
  private static final String request = "request";
  private static final String providingAction = "providingAction";

  private final JsonSerializer<Action> actionSerializer;
  private final JsonSerializer<T> subjectSerializer;

  public JsonProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                 final JsonSerializer<T> subjectSerializer) {
    this.actionSerializer = actionSerializer;
    this.subjectSerializer = subjectSerializer;
  }

  @Override
  public JsonObjectBuilder build(final P provision) {
    return Json.createObjectBuilder()
        .add(request, subjectSerializer.serialize(provision.getRequest()))
        .add(offer, subjectSerializer.serialize(provision.getOffer().getSubject()))
        .add(providingAction, actionSerializer.serialize(provision.getProvidingAction()));
  }

}
