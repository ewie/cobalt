/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Objects;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.InteractionMaker.aMinimalInteraction;
import static vsr.cobalt.models.makers.InteractionMaker.anInteraction;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class InteractionTest {

  @Test
  public static class Getters {

    @Test
    public void getInstructionText() {
      final String s = "foo";
      final Interaction i = new Interaction(s);
      assertEquals(i.getInstructionText(), s);
    }

  }

  @Test
  public static class Equals {

    @Test
    public void returnFalseWhenNotInstanceOfSameClass() {
      final Interaction i = make(aMinimalInteraction());
      final Object x = new Object();
      assertFalse(i.equals(x));
    }

    @Test
    public void returnFalseWhenInstructionTextDiffers() {
      final Interaction i1 = make(anInteraction().withInstruction("i1"));
      final Interaction i2 = make(anInteraction().withInstruction("i2"));
      assertFalse(i1.equals(i2));
    }

    @Test
    public void returnTrueWhenEqual() {
      final Interaction i1 = make(aMinimalInteraction());
      final Interaction i2 = make(aMinimalInteraction());
      assertTrue(i1.equals(i2));
    }

  }

  @Test
  public static class HashCode {

    @Test
    public void useInteractionText() {
      final String instruction = "foo";
      final Interaction interaction = make(anInteraction()
          .withInstruction(instruction));
      assertEquals(interaction.hashCode(), Objects.hash(instruction));
    }

  }

}
