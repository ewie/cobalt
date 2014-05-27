/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;
import vsr.cobalt.planner.ProbingIterator;
import vsr.cobalt.planner.ProductSetIterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.testing.Utilities.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.immutableSetOf;
import static vsr.cobalt.testing.Utilities.setOf;

/**
 * @author Erik Wienhold
 */
@Test
public class ProductSetIteratorTest {

  @Test
  public void extendsBufferedIterator() {
    assertSubClass(ProductSetIterator.class, ProbingIterator.class);
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a set of non-empty sets")
    public void rejectEmptySets() {
      new ProductSetIterator<>(immutableSetOf(emptySet()));
    }

  }

  @Test
  public static class ProbeNextValue {

    @Test
    public void returnNullWhenDepleted() {
      final ImmutableSet<ImmutableSet<Object>> s = emptySet();
      final ProductSetIterator<Object> psi = new ProductSetIterator<>(s);
      assertNull(psi.probeNextValue());
    }

    @Test
    public void returnEveryCombinationOnSuccessiveCalls() throws Exception {
      final ImmutableSet<ImmutableSet<Integer>> s = immutableSetOf(
          immutableSetOf(0, 1), immutableSetOf(2, 3));

      final ImmutableSet<ImmutableSet<Integer>> xps = immutableSetOf(
          immutableSetOf(0, 2), immutableSetOf(0, 3),
          immutableSetOf(1, 2), immutableSetOf(1, 3));

      final ProductSetIterator<Integer> psi = new ProductSetIterator<>(s);
      final Set<Set<Integer>> ps = setOf();

      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());

      assertNull(psi.probeNextValue());
      assertEquals(ps, xps);
    }

  }

}
