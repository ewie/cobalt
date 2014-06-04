/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Type;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class TypeMaker implements Maker<Type> {

  private final AtomicValue<String> identifier = new AtomicValue<>();

  public static TypeMaker aType() {
    return new TypeMaker();
  }

  public static TypeMaker aMinimalType() {
    return aType().withIdentifier("");
  }

  public Type make() {
    return new Type(identifier.get());
  }

  public TypeMaker withIdentifier(final String identifier) {
    this.identifier.set(identifier);
    return this;
  }

}
