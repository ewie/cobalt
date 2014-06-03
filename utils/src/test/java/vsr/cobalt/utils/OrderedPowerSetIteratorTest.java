/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class OrderedPowerSetIteratorTest {

  @Test
  public void extendsBufferedIterator() {
    assertSubClass(OrderedPowerSetIterator.class, ProbingIterator.class);
  }

  @Test
  public static class New {

    @Test
    public void acceptEmptySet() {
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(emptySet(Integer.class));
      assertFalse(it.hasNext());
    }

    @Test
    public void preventModificationFromOutside() {
      final Set<Integer> s = setOf(0);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      s.add(1);
      assertEquals(Sets.newHashSet(it), setOf(setOf(0)));
    }

  }

  @Test
  public static class ProbeNextValue {

    @Test
    public void returnNullWhenDepleted() {
      final Set<Integer> s = setOf(0);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);

      it.probeNextValue();
      assertNull(it.probeNextValue());
    }

    @Test
    public void returnEverySubsetOnSuccessiveCalls() {
      final Set<Integer> s = setOf(0, 1);
      final Set<Set<Integer>> xps = setOf(setOf(0), setOf(1), setOf(0, 1));

      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      final Set<Set<Integer>> ps = setOf();

      ps.add(it.probeNextValue());
      ps.add(it.probeNextValue());
      ps.add(it.probeNextValue());

      assertNull(it.probeNextValue());
      assertEquals(ps, xps);
    }

    @Test
    public void returnSetsOrderedByCardinality() {
      final Set<Integer> s = setOf(0, 1);
      final List<Integer> xc = asList(1, 1, 2);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      final List<Integer> c = new ArrayList<>(4);

      c.add(it.probeNextValue().size());
      c.add(it.probeNextValue().size());
      c.add(it.probeNextValue().size());

      assertNull(it.probeNextValue());
      assertEquals(c, xc);
    }

  }

  @Test
  public static class ExcludeSuperSetsOf {

    @Test
    public void superSetExclusionBeforeIteration() {
      final Set<Integer> s = setOf(0, 1);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      final Set<Set<Integer>> xps = setOf(setOf(1));
      final Set<Set<Integer>> ps = setOf();

      it.excludeSuperSetsOf(setOf(0));

      while (it.hasNext()) {
        ps.add(it.next());
      }

      assertEquals(ps, xps);
    }

    @Test
    public void superSetExclusionDuringIteration() {
      final Set<Integer> s = setOf(0, 1);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      final Set<Set<Integer>> xps = setOf(setOf(0), setOf(1));
      final Set<Set<Integer>> ps = setOf();

      while (it.hasNext()) {
        final Set<Integer> t = it.next();
        ps.add(t);
        // exclude any further sets containing a zero
        if (t.contains(0)) {
          it.excludeSuperSetsOf(t);
        }
      }

      assertEquals(ps, xps);
    }

    @Test
    public void preventModificationFromOutside() {
      final Set<Integer> s = setOf(0, 1);
      final OrderedPowerSetIterator<Integer> it = new OrderedPowerSetIterator<>(s);
      final Set<Set<Integer>> xps = setOf(setOf(1));
      final Set<Set<Integer>> ps = setOf();

      // exclude 0 so {1} should be the only set the iterator produces
      final Set<Integer> x = setOf(0);

      it.excludeSuperSetsOf(x);

      // add 1 would only exclude supersets of {0, 1}
      x.add(1);

      while (it.hasNext()) {
        ps.add(it.next());
      }

      assertEquals(ps, xps);
    }

  }

}
