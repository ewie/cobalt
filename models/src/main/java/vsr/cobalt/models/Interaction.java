/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

/**
 * Describes a user interaction required by an {@link Action}.
 *
 * @author Erik Wienhold
 */
public class Interaction {

  private final String instructionText;

  /**
   * @param instructionText the instruction text
   */
  public Interaction(final String instructionText) {
    this.instructionText = instructionText;
  }

  /**
   * @return the instruction text
   */
  public String getInstructionText() {
    return instructionText;
  }

  @Override
  public int hashCode() {
    return Objects.hash(instructionText);
  }

  @Override
  public boolean equals(final Object other) {
    return super.equals(other)
        || other instanceof Interaction
        && equals((Interaction) other);
  }

  private boolean equals(final Interaction other) {
    return Objects.equals(instructionText, other.instructionText);
  }

}
