/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.immutableSetOf;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ProductSetTest {

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a set of non-empty sets")
    public void rejectEmptySets() {
      new ProductSet<>(setOf(emptySet()));
    }

  }

  @Test
  public static class Iterator {

    @Test
    public void returnIterator() {
      final ImmutableSet<ImmutableSet<Integer>> s = immutableSetOf(
          immutableSetOf(0, 1), immutableSetOf(2, 3));

      final ProductSet<Integer> ps = new ProductSet<>(s);

      final ProductSetIterator<Integer> psi = ps.iterator();
      final ProductSetIterator<Integer> xpsi = new ProductSetIterator<>(s);

      while (psi.hasNext() && xpsi.hasNext()) {
        assertEquals(psi.next(), xpsi.next());
      }

      assertFalse(psi.hasNext());
      assertFalse(psi.hasNext());
    }

  }

}
