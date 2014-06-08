/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import org.testng.annotations.Test;
import vsr.cobalt.planner.Plan;

import static org.testng.Assert.assertSame;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class SequentialPlanCollectorTest {

  @Test
  public void extendsQueueingPlanCollector() {
    assertSubClass(SequentialPlanCollector.class, QueueingPlanCollector.class);
  }

  @Test
  public void offerCollectedPlan() {
    final SequentialPlanCollector sc = new SequentialPlanCollector();
    final Plan p = new Plan(make(aMinimalGraph()));
    sc.collect(p);
    assertSame(sc.peek(), p);
  }

}
