/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serialization.serializers;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.service.planner.distance.ProvisionDistanceMeter;
import vsr.cobalt.service.serialization.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonPropertyProvisionSerializer extends JsonProvisionSerializer<Property, PropertyProvision> {

  public JsonPropertyProvisionSerializer(final JsonSerializer<Action> actionSerializer,
                                         final JsonSerializer<Property> propertySerializer,
                                         final ProvisionDistanceMeter<PropertyProvision> provisionDistanceMeter) {
    super(actionSerializer, propertySerializer, provisionDistanceMeter);
  }

}
