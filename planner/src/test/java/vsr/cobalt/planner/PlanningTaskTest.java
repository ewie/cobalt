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
import vsr.cobalt.models.Mashup;
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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.MashupMaker.aMinimalMashup;
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
public class PlanningTaskTest {

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
            .withProvision(aFunctionalityProvision()
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
            .withProvision(aFunctionalityProvision()
                .withRequest(functionality)
                .withOffer(functionality)
                .withProvidingAction(aMinimalAction()
                    .withFunctionality(functionality)))));
  }

  @Test
  public static class GetGraph {

    @Test
    public void returnNullWhenNew() {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 1);
      final PlanningTask pt = new PlanningTask(pp, (GraphFactory) null, null, null, null);
      assertNull(pt.getGraph());
    }

    @Test
    public void returnInitialGraphAfterFirstAdvance() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 2);

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(GRAPHS.get(0));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, null, px, pc);
      pt.advance();

      assertSame(pt.getGraph(), GRAPHS.get(0));
    }

    @Test
    public void returnGraphFromLastExtension() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 2);

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(GRAPHS.get(0));

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, gx, px, pc);
      pt.advance();
      pt.advance();

      assertSame(pt.getGraph(), GRAPHS.get(1));
    }

  }

  @Test
  public static class IsDone {

    @Test
    public void returnFalseWhenNew() {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 1);
      final PlanningTask pt = new PlanningTask(pp, (GraphFactory) null, null, null, null);
      assertFalse(pt.isDone());
    }

    @Test
    public void returnFalseWhenPlansOfMaxDepthHaveNotYetBeenSearched() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 2);

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(GRAPHS.get(0));

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, gx, px, pc);
      pt.advance();

      assertFalse(pt.isDone());
    }

    @Test
    public void returnTrueWhenPlansOfMaxDepthHaveBeenSearched() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 1);

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(GRAPHS.get(0));

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, gx, px, pc);
      pt.advance();

      assertTrue(pt.isDone());
    }

    @Test
    public void returnFalseWhenGraphIsSatisfiedBeforeMaxDepthIsReached() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 2);

      final Graph g = make(aMinimalGraph());

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(g);

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, null, px, pc);
      pt.advance();

      assertTrue(pt.isDone());
    }

  }

  @Test
  public static class Advance {

    @Test
    public void createGraphOnFirstCallWhenFactoryIsGiven() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 1);

      final Graph g = make(aMinimalGraph());

      final GraphFactory gf = mock(GraphFactory.class);
      when(gf.createGraph(m)).thenReturn(g);

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, gf, null, px, pc);
      pt.advance();

      verify(gf).createGraph(m);
    }

    @Test
    public void extendGraphToReachMinDepthBeforeSearchingPlans() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 3, 3);

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));
      when(gx.extendGraph(GRAPHS.get(1))).thenReturn(GRAPHS.get(2));

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, GRAPHS.get(0), gx, px, pc);
      pt.advance();

      verify(gx).extendGraph(GRAPHS.get(0));
      verify(gx).extendGraph(GRAPHS.get(1));
    }

    @Test
    public void searchPlansOfMinDepthOnFirstSearch() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 3);

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, GRAPHS.get(2), null, px, pc);
      pt.advance();

      verify(px).extractPlans(GRAPHS.get(2), 1);
    }

    @Test
    public void collectExtractedPlans() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 3);

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(GRAPHS.get(0))).thenReturn(GRAPHS.get(1));

      // not the actual plans to be found in the planning graph
      final Graph g1 = minimalGraph(make(aMinimalFunctionality().withIdentifier("f1")));
      final Graph g2 = minimalGraph(make(aMinimalFunctionality().withIdentifier("f2")));

      final Plan p1 = new Plan(g1);
      final Plan p2 = new Plan(g2);

      final Iterator<Plan> it = asList(p1, p2).iterator();

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(GRAPHS.get(0), 1)).thenReturn(it);

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, GRAPHS.get(0), gx, px, pc);
      pt.advance();

      verify(pc).collect(p1);
      verify(pc).collect(p2);
    }

    @Test
    public void doNotExtendSatisfiedGraph() throws Exception {
      final Mashup m = make(aMinimalMashup());
      final PlanningProblem pp = new PlanningProblem(m, 1, 2);

      final Graph g = make(aMinimalGraph());

      final GraphExtender gx = mock(GraphExtender.class);
      when(gx.extendGraph(g)).thenThrow(new PlanningException());

      final PlanExtractor px = mock(PlanExtractor.class);
      when(px.extractPlans(any(Graph.class), anyInt())).thenReturn(Iterators.<Plan>emptyIterator());

      final PlanCollector pc = mock(PlanCollector.class);

      final PlanningTask pt = new PlanningTask(pp, g, gx, px, pc);
      pt.advance();

      assertSame(pt.getGraph(), g);
    }

  }

}
