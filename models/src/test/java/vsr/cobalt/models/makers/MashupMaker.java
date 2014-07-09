/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Mashup;
import vsr.cobalt.testing.maker.CollectionValue;
import vsr.cobalt.testing.maker.Maker;

import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;

/**
 * @author Erik Wienhold
 */
public class MashupMaker implements Maker<Mashup> {

  private final CollectionValue<Functionality> functionalities = new CollectionValue<>();

  public static MashupMaker aMashup() {
    return new MashupMaker();
  }

  public static MashupMaker aMinimalMashup() {
    return aMashup().withFunctionality(aMinimalFunctionality());
  }

  @Override
  public Mashup make() {
    return new Mashup(functionalities.asSet());
  }

  public MashupMaker withFunctionality(final Maker<Functionality> maker) {
    functionalities.add(maker);
    return this;
  }

  public MashupMaker withFunctionality(final Functionality... functionalities) {
    this.functionalities.addValues(functionalities);
    return this;
  }

}
