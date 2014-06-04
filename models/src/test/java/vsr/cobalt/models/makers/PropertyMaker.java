/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Property;
import vsr.cobalt.models.Type;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.TypeMaker.aMinimalType;

/**
 * @author Erik Wienhold
 */
public class PropertyMaker implements Maker<Property> {

  public final AtomicValue<String> name = new AtomicValue<>();

  public final AtomicValue<Type> type = new AtomicValue<>();

  public static PropertyMaker aProperty() {
    return new PropertyMaker();
  }

  public static PropertyMaker aMinimalProperty() {
    return aProperty()
        .withName("")
        .withType(aMinimalType());
  }

  @Override
  public Property make() {
    return new Property(name.get(), type.get());
  }

  public PropertyMaker withName(final String name) {
    this.name.set(name);
    return this;
  }

  public PropertyMaker withType(final Type type) {
    this.type.set(type);
    return this;
  }

  public PropertyMaker withType(final Maker<Type> type) {
    this.type.set(type);
    return this;
  }

}
