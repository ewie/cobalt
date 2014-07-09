/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class PlannerTest {

  /**
   * A list of graphs, each being an extension of the previous graph.
   */
  private static final List<Graph> GRAPHS;

  static {
    final Functionality f = make(aMinimalFunctionality());

    final Property p1 = make(aMinimalProperty().withName("p1"));
    final Property p2 = make(aMinimalProperty().withName("p2"));

    final Action a1 = make(aMinimalAction()
        .withFunctionality(f)
        .withPre(aPropositionSet()
            .withCleared(p1)));

    final Action a2 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withCleared(p1))
        .withPre(aPropositionSet()
            .withCleared(p2)));

    final Action a3 = make(aMinimalAction()
        .withEffects(aPropositionSet()
            .withCleared(p2)));

    final Graph g = make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(f)
                .withOffer(f)
                .withProvidingAction(a1)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a1)
                .withPrecursor(a2)))
        .withExtensionLevel(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(a2)
                .withPrecursor(a3))));

    GRAPHS = getExtensionGraphs(g);
  }

  private static List<Graph> getExtensionGraphs(final Graph graph) {
    final List<Graph> graphs = new ArrayList<>();

    final InitialLevel il = graph.getInitialLevel();
    graphs.add(Graph.create(il));

    final List<ExtensionLevel> xls = Lists.reverse(Lists.newArrayList(graph.getExtensionLevelsReversed()));
    final int depth = graph.getDepth();
    for (int i = 1; i < depth; i += 1) {
      graphs.add(Graph.create(il, xls.subList(0, i)));
    }

    return graphs;
  }

  /**
   * Create a minimal graph identified only by the given functionality.
   *
   * @param functionality a functionality realized by the graph
   *
   * @return a minimal graph realizing the given functionality
   */
  private static Graph minimalGraph(final Functionality functionality) {
    return make(aGraph()
        .withInitialLevel(anInitialLevel()
            .withFunctionalityProvision(aFunctionalityProvision()
                .withRequest(functionality)
                .withOffer(functionality)
                .withProvidingAction(aMinimalAction()
                    .withFunctionality(functionality)))));
  }

  @Test
  public static class New {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting positive minimum depth")
    public void rejectNonPositiveMinDepth() {
      new Planner(null, null, null, null, 0, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting minimum depth to be less than or equal to maximum depth")
    public void rejectMinDepthGreaterThanMaxDepth() {
      new Planner(null, null, null, null, 2, 1);
    }

  }

  @Test
  public static class GetGraph {

    @Test
    public void returnInitialGraphWhenNotYetExtended() {
      final Planner p = new Planner(GRAPHS.get(0), null, null, null, 1, 1);
      assertSame(p.getGraph(), GRAPHS.get(0));
    }

    @Test
    public void returnGraphFromLastExtension() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 2, 2);
      p.advance();

      assertSame(p.getGraph(), GRAPHS.get(1));
    }

  }

  @Test
  public static class IsDone {

    @Test
    public void returnFalseWhenPlansOfMaxDepthHaveNotYetBeenExtracted() {
      final Planner p = new Planner(GRAPHS.get(0), null, null, null, 1, 1);
      assertFalse(p.isDone());
    }

    @Test
    public void returnTrueWhenPlansOfMaxDepthHaveBeenExtracted() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 1, 1);
      p.advance();

      assertTrue(p.isDone());
    }

    @Test
    public void returnFalseWhenGraphIsSatisfiedBeforeMaxDepthIsReachedAndNoPlansHaveBeenExtracted() throws Exception {
      final Graph g = make(aMinimalGraph());
      final Planner p = new Planner(g, null, null, null, 1, 2);
      assertFalse(p.isDone());
    }

    @Test
    public void returnTrueWhenGraphIsSatisfiedBeforeMaxDepthIsReachedAndPlansHaveBeenExtracted() throws Exception {
      final Graph g = make(aMinimalGraph());

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(g, null, px, pc, 1, 2);
      p.advance();

      assertTrue(p.isDone());
    }

  }

  @Test
  public static class Advance {

    @Test
    public void extendGivenGraphToReachMinDepthBeforeExtractingAnyPlans() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));
      when(gx.extendGraph(GRAPHS.get(1))).thenReturn(GRAPHS.get(2));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 3, 3);

      p.advance();

      verify(gx).extendGraph(GRAPHS.get(0));
      verify(gx).extendGraph(GRAPHS.get(1));
    }

    @Test
    public void extractPlansOfMinDepthOnFirstExtraction() throws Exception {
      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(2), null, px, pc, 1, 3);

      p.advance();

      verify(px).extractPlans(GRAPHS.get(2), 1);
    }

    @Test
    public void extendGraphByOneLevelAfterPlansHaveBeenExtracted() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 1, 3);

      p.advance();
      p.advance();

      verify(px).extractPlans(GRAPHS.get(0), 1);
      verify(px).extractPlans(GRAPHS.get(1), 2);
    }

    @Test
    public void extractPlansOfCurrentDepthOnFurtherExtractions() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 1, 3);

      p.advance();
      p.advance();

      verify(px).extractPlans(GRAPHS.get(0), 1);
      verify(px).extractPlans(GRAPHS.get(1), 2);
    }

    @Test
    public void collectExtractedPlans() throws Exception {
      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final Graph g1 = minimalGraph(make(aMinimalFunctionality().withIdentifier("f1")));
      final Graph g2 = minimalGraph(make(aMinimalFunctionality().withIdentifier("f2")));

      final Plan p1 = new Plan(g1);
      final Plan p2 = new Plan(g2);

      final Iterator<Plan> it = asList(p1, p2).iterator();

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(GRAPHS.get(0), 1)).thenReturn(it);

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(GRAPHS.get(0), gx, px, pc, 1, 3);

      p.advance();

      verify(pc).collect(p1);
      verify(pc).collect(p2);
    }

    @Test
    public void doNotExtendSatisfiedGraph() throws Exception {
      final Graph g = make(aMinimalGraph());

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(g)).thenThrow(new PlanningException());

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final Planner p = new Planner(g, gx, px, pc, 1, 2);

      p.advance();

      assertSame(p.getGraph(), g);
    }

  }

}
