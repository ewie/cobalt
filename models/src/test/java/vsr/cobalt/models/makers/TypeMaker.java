/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Type;

/**
 * @author Erik Wienhold
 */
public class TypeMaker extends IdentifiableMaker<Type, TypeMaker> {

  public static TypeMaker aType() {
    return new TypeMaker();
  }

  public static TypeMaker aMinimalType() {
    return aType().withIdentifier("");
  }

  @Override
  public Type make() {
    return new Type(identifier.get());
  }

}
