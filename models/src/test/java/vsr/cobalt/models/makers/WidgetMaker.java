/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Widget;

/**
 * @author Erik Wienhold
 */
public class WidgetMaker extends IdentifiableMaker<Widget> {

  public static WidgetMaker aWidget() {
    return new WidgetMaker();
  }

  public static WidgetMaker aMinimalWidget() {
    return (WidgetMaker) aWidget().withIdentifier("");
  }

  @Override
  public Widget make() {
    return new Widget(identifier.get());
  }

}
