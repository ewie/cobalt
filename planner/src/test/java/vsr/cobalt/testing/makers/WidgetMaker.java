/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.testing.makers;

import vsr.cobalt.planner.models.Widget;
import vsr.cobalt.testing.maker.Maker;
import vsr.cobalt.testing.maker.AtomicValue;

/**
 * @author Erik Wienhold
 */
public class WidgetMaker implements Maker<Widget> {

  private final AtomicValue<String> identifier = new AtomicValue<>();

  public static WidgetMaker aWidget() {
    return new WidgetMaker();
  }

  public static WidgetMaker aMinimalWidget() {
    return aWidget().withIdentifier("");
  }

  @Override
  public Widget make() {
    return new Widget(identifier.get());
  }

  public WidgetMaker withIdentifier(final String identifier) {
    this.identifier.set(identifier);
    return this;
  }

}
