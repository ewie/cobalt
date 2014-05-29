/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
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
      new ProductSetIterator<>(setOf(emptySet()));
    }

  }

  @Test
  public static class ProbeNextValue {

    @Test
    public void returnNullWhenDepleted() {
      final Set<ImmutableSet<Object>> s = emptySet();
      final ProductSetIterator<Object> psi = new ProductSetIterator<>(s);
      assertNull(psi.probeNextValue());
    }

    @Test
    public void returnEveryCombinationOnSuccessiveCalls() {
      final Set<Set<Integer>> s = setOf(setOf(0, 1), setOf(2, 3));

      final Set<Set<Integer>> xps = setOf(
          setOf(0, 2), setOf(0, 3),
          setOf(1, 2), setOf(1, 3));

      final ProductSetIterator<Integer> psi = new ProductSetIterator<>(s);
      final Set<Set<Integer>> ps = setOf();

      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());
      ps.add(psi.probeNextValue());

      assertNull(psi.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void preventModificationFromOutside() {
      final Set<Integer> t = setOf(1);
      final Set<Set<Integer>> s = setOf(setOf(0), t);

      final Set<Set<Integer>> xps = setOf(setOf(0, 1));

      final ProductSetIterator<Integer> psi = new ProductSetIterator<>(s);
      final Set<Set<Integer>> ps = setOf();

      // this should not affect the iterator
      t.add(2);

      ps.add(psi.probeNextValue());

      assertNull(psi.probeNextValue());
      assertEquals(ps, xps);
    }

  }

}
