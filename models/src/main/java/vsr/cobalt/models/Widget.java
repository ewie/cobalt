/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

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
  public Widget(final Identifier identifier) {
    super(identifier);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Widget;
  }

}
