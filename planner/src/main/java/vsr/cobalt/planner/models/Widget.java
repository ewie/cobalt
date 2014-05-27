/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.models;

/**
 * A widget is the building of an UI mashup.
 *
 * @author Erik Wienhold
 */
public final class Widget extends Identifiable {

  /**
   * Create a new widget.
   *
   * @param identifier a widget identifier
   */
  public Widget(final String identifier) {
    super(identifier);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Widget;
  }

}
