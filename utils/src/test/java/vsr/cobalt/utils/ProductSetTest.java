/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ProductSetTest {

  @Test
  public static class Empty {

    @Test
    public void returnEmptyProductSet() {
      final ProductSet<?> ps = ProductSet.empty();
      assertTrue(Iterables.isEmpty(ps));
    }

  }

  @Test
  public static class Iterator {

    @Test
    public void returnIterator() {
      final Set<Set<Integer>> s = setOf(setOf(0, 1), setOf(2, 3));

      final ProductSet<Integer> ps = new ProductSet<>(s);

      final ProductSetIterator<Integer> psi = ps.iterator();
      final ProductSetIterator<Integer> xpsi = new ProductSetIterator<>(s);

      assertTrue(Iterators.elementsEqual(psi, xpsi));
    }

  }

}
