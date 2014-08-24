/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Functionality;

/**
 * @author Erik Wienhold
 */
public class FunctionalityMaker extends IdentifiableMaker<Functionality, FunctionalityMaker> {

  public static FunctionalityMaker aFunctionality() {
    return new FunctionalityMaker();
  }

  public static FunctionalityMaker aMinimalFunctionality() {
    return aFunctionality().withIdentifier("");
  }

  @Override
  public Functionality make() {
    return new Functionality(identifier.get());
  }

}
