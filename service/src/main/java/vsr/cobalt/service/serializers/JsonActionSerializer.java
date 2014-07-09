/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.serializers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.service.JsonSerializer;

/**
 * @author Erik Wienhold
 */
public class JsonActionSerializer extends JsonSerializer<Action> {

  private static final String widget = "widget";
  private static final String realizedFunctionalities = "realizedFunctionalities";
  private static final String publishedProperties = "publishedProperties";
  private static final String interactions = "interactions";

  private final JsonSerializer<Widget> widgetSerializer;

  private final JsonSerializer<Property> propertySerializer;

  private final JsonSerializer<Interaction> interactionSerializer;

  private final JsonSerializer<Functionality> functionalitySerializer;

  public JsonActionSerializer(final JsonSerializer<Widget> widgetSerializer,
                              final JsonSerializer<Functionality> functionalitySerializer,
                              final JsonSerializer<Property> propertySerializer,
                              final JsonSerializer<Interaction> interactionSerializer) {
    this.widgetSerializer = widgetSerializer;
    this.functionalitySerializer = functionalitySerializer;
    this.propertySerializer = propertySerializer;
    this.interactionSerializer = interactionSerializer;
  }

  @Override
  public JsonObjectBuilder build(final Action action) {
    return Json.createObjectBuilder()
        .add(widget, serializeWidget(action))
        .add(realizedFunctionalities, serializeFunctionalities(action))
        .add(publishedProperties, serializeProperties(action))
        .add(interactions, serializeInteractions(action));
  }

  private JsonObject serializeWidget(final Action action) {
    return widgetSerializer.serialize(action.getWidget());
  }

  private JsonArray serializeFunctionalities(final Action action) {
    return functionalitySerializer.serializeAll(action.getRealizedFunctionalities());
  }

  private JsonArray serializeProperties(final Action action) {
    return propertySerializer.serializeAll(action.getPublishedProperties());
  }

  private JsonArray serializeInteractions(final Action action) {
    return interactionSerializer.serializeAll(action.getInteractions());
  }

}
