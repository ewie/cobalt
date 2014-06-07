/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.Graph;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class BackwardChainingPlanExtractorTest {

  @Test
  public static class ExtractPlans {

    @Test
    public void createExtractingIterator() {
      final Graph g = make(aMinimalGraph());
      final int d = 1;
      final BackwardChainingPlanExtractor px = new BackwardChainingPlanExtractor();
      final BackwardChainingPlanIterator it = px.extractPlans(g, d);
      assertSame(it.getGraph(), g);
      assertEquals(it.getMinDepth(), d);
      assertEquals(it.getMaxDepth(), d);
    }

  }

}
