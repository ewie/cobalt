/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.planner.graph.FunctionalityProvision;
import vsr.cobalt.service.JsonSerializer;
import vsr.cobalt.service.distance.ProvisionDistanceMeter;

/**
 * @author Erik Wienhold
 */
public class JsonFunctionalityProvisionSerializer
    extends JsonProvisionSerializer<Functionality, FunctionalityProvision> {

  public JsonFunctionalityProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                              final JsonSerializer<Functionality> functionalitySerializer,
                                              final ProvisionDistanceMeter<FunctionalityProvision>
                                                  provisionDistanceMeter) {
    super(actionSerializer, functionalitySerializer, provisionDistanceMeter);
  }

}
