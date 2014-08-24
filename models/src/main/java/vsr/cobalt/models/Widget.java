/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Widget are the building blocks of an UI mashup.
 *
 * @author Erik Wienhold
 */
public final class Widget extends Identifiable {

  /**
   * The set of publicly exposed properties.
   * Not included properties are private per default.
   */
  private final Set<Property> publicProperties;

  /**
   * Create a new widget with a set of public properties.
   *
   * @param identifier       a widget identifier
   * @param publicProperties a set of publicly exposed properties
   */
  public Widget(final Identifier identifier, final Set<Property> publicProperties) {
    super(identifier);
    this.publicProperties = publicProperties;
  }

  /**
   * Create a new widget without any public properties.
   *
   * @param identifier a widget identifier
   */
  public Widget(final Identifier identifier) {
    this(identifier, ImmutableSet.<Property>of());
  }

  /**
   * @return the properties publicly exposed to a widget's environment
   */
  public Set<Property> getPublicProperties() {
    return publicProperties;
  }

  /**
   * Test if a property is publicly exposed.
   *
   * @param property the property to test for visibility
   *
   * @return true when public, false otherwise
   */
  public boolean isPublicProperty(final Property property) {
    return publicProperties.contains(property);
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof Widget;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), publicProperties);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        && other instanceof Widget
        && equals((Widget) other);
  }

  private boolean equals(final Widget other) {
    return other.canEqual(this)
        && publicProperties.equals(other.publicProperties);
  }

}
