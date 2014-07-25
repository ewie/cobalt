/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models.makers;

import vsr.cobalt.models.Interaction;
import vsr.cobalt.testing.maker.AtomicValue;
import vsr.cobalt.testing.maker.Maker;

/**
 * @author Erik Wienhold
 */
public class InteractionMaker implements Maker<Interaction> {

  private final AtomicValue<String> instruction = new AtomicValue<>();

  public static InteractionMaker anInteraction() {
    return new InteractionMaker();
  }

  public static InteractionMaker aMinimalInteraction() {
    return anInteraction().withInstruction("");
  }

  @Override
  public Interaction make() {
    return new Interaction(instruction.get());
  }

  public InteractionMaker withInstruction(final String instruction) {
    this.instruction.set(instruction);
    return this;
  }

}
