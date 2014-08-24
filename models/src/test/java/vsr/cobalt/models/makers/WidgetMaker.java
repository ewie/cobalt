/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import java.util.Collection;

import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class WidgetMaker extends IdentifiableMaker<Widget, WidgetMaker> {

  private final CollectionValue<Property> publicProperties = new CollectionValue<>();

  public static WidgetMaker aWidget() {
    return new WidgetMaker();
  }

  public static WidgetMaker aMinimalWidget() {
    return aWidget().withIdentifier("");
  }

  @Override
  public Widget make() {
    return new Widget(identifier.get(), publicProperties.asSet());
  }

  public WidgetMaker withPublish(final Collection<Property> properties) {
    publicProperties.set(properties);
    return this;
  }

  public WidgetMaker withPublic(final Property... properties) {
    publicProperties.addValues(properties);
    return this;
  }

  @SafeVarargs
  public final WidgetMaker withPublish(final Maker<Property>... makers) {
    publicProperties.add(makers);
    return this;
  }

}
