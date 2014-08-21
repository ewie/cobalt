/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.collectors;

import org.testng.annotations.Test;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.PlanCollector;

import static org.testng.Assert.assertEquals;
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
  public void returnContinue() {
    final SequentialPlanCollector pc = new SequentialPlanCollector();
    final Plan p = new Plan(make(aMinimalGraph()));
    assertEquals(pc.collect(p), PlanCollector.Result.CONTINUE);
  }

  @Test
  public void offerCollectedPlan() {
    final SequentialPlanCollector pc = new SequentialPlanCollector();
    final Plan p = new Plan(make(aMinimalGraph()));
    pc.collect(p);
    assertSame(pc.peek(), p);
  }

}
