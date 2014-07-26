/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterables;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aFunctionality;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.aMinimalActionProvision;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.aMinimalExtensionLevel;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aMinimalFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.aMinimalInitialLevel;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class GraphTest {

  @Test
  public static class Create {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a sufficient extension")
    public void rejectInvalidExtensionLevelsWhenGiven() {
      final InitialLevel il = make(aMinimalInitialLevel());
      final ExtensionLevel xl = make(aMinimalExtensionLevel());
      Graph.create(il, xl);
    }

    @Test
    public void returnAnInitialGraphWhenCalledWithoutExtensionLevels() {
      final InitialLevel il = make(aMinimalInitialLevel());
      final Graph g = Graph.create(il);
      assertSame(g.getInitialLevel(), il);
      assertSame(g.getLastLevel(), il);
    }

    public void returnAnExtendedGraphWhenCalledWithValidExtensionLevels() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final InitialLevel il = make(anInitialLevel()
          .withProvision(aMinimalFunctionalityProvision()
              .withProvidingAction(a1)
              .withOffer(f)
              .withRequest(f)));

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)));

      final Graph g = Graph.create(il, xl);

      assertSame(g.getInitialLevel(), il);
      assertSame(g.getLastLevel(), xl);
    }

  }

  @Test
  public static class ExtendWith {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a sufficient extension")
    public void rejectInsufficientExtensionWhenExtendingSeededGraph() {
      final Widget w = make(aWidget().withIdentifier("w"));

      final Functionality f = make(aMinimalFunctionality());

      final Action a = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f));

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(aMinimalActionProvision()));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aMinimalFunctionalityProvision()
                  .withProvidingAction(a)
                  .withOffer(f)
                  .withRequest(f))));

      g.extendWith(xl);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "expecting a sufficient extension")
    public void rejectInsufficientExtensionWhenExtendingAnExtendedGraph() {
      final Widget w = make(aWidget().withIdentifier("w"));

      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet().withCleared(p)));

      final ExtensionLevel xl1 = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)));

      final ExtensionLevel xl2 = make(anExtensionLevel()
          .withProvision(aMinimalActionProvision()));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aMinimalFunctionalityProvision()
                  .withProvidingAction(a1)
                  .withOffer(f)
                  .withRequest(f)))
          .withExtensionLevel(xl1));

      g.extendWith(xl2);
    }

    @Test
    public void returnExtendedGraph() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final ActionProvision ap = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      final ExtensionLevel xl = make(anExtensionLevel().withProvision(ap));

      final Graph ig = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aMinimalFunctionalityProvision()
                  .withProvidingAction(a1)
                  .withOffer(f)
                  .withRequest(f))));

      final Graph xg = ig.extendWith(xl);

      assertEquals(xg.getExtensionDepth(), 1);
    }

  }

  @Test
  public static class GetDepth {

    @Test
    public void returnExtensionDepthPlusOne() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))));

      assertEquals(g.getDepth(), g.getExtensionDepth() + 1);
    }

  }

  @Test
  public static class IsSatisfied {

    @Test
    public void returnFalseWhenAnyActionOfTheLastLevelIsNotEnabled() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a))));

      assertFalse(g.isSatisfied());
    }

    @Test
    public void returnTrueWhenAllActionsOfTheLastLevelAreEnabled() {
      final Graph g = make(aMinimalGraph());
      assertTrue(g.isSatisfied());
    }

  }

  @Test
  public static class GetLevels {

    private Graph graph;

    @BeforeMethod
    public void setUp() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p)));

      graph = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))));
    }

    @Test
    public void includeExtensionLevels() {
      final Iterator<Level> ls = graph.getLevels().iterator();
      // consume the initial level
      ls.next();
      for (final ExtensionLevel xl : graph.getExtensionLevels()) {
        assertEquals(ls.next(), xl);
      }
    }

    @Test
    public void includeInitialLevel() {
      final Iterator<Level> ls = graph.getLevels().iterator();
      assertEquals(ls.next(), graph.getInitialLevel());
    }

  }

  @Test
  public static class GetLevelsReversed {

    private Graph graph;

    @BeforeMethod
    public void setUp() {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p)));

      graph = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1)))
          .withExtensionLevel(anExtensionLevel()
              .withProvision(anActionProvision()
                  .withRequest(a1)
                  .withPrecursor(a2))));
    }

    @Test
    public void includeExtensionLevels() {
      final Iterator<Level> ls = graph.getLevelsReversed().iterator();
      for (final ExtensionLevel xl : graph.getExtensionLevelsReversed()) {
        assertEquals(ls.next(), xl);
      }
    }

    @Test
    public void includeInitialLevel() {
      final Iterator<Level> ls = graph.getLevelsReversed().iterator();
      // consume the extension level
      ls.next();
      assertEquals(ls.next(), graph.getInitialLevel());
    }

  }

  @Test
  public static class AnInitialGraph {

    private static final InitialLevel initialLevel = make(aMinimalInitialLevel());

    private static final Graph graph = make(aGraph().withInitialLevel(initialLevel));

    @Test
    public static class IsExtended {

      @Test
      public void returnFalse() {
        assertFalse(graph.isExtended());
      }

    }

    @Test
    public static class GetInitialLevel {

      @Test
      public void returnTheInitialLevel() {
        assertSame(graph.getInitialLevel(), initialLevel);
      }

    }

    @Test
    public static class GetBaseGraph {

      @Test(expectedExceptions = UnsupportedOperationException.class)
      public void isUnsupportedOperation() {
        graph.getBaseGraph();
      }

    }

    @Test
    public static class GetExtensionDepth {

      @Test
      public void returnZero() {
        assertEquals(graph.getExtensionDepth(), 0);
      }

    }

    @Test
    public static class GetLastLevel {

      @Test
      public void returnInitialLevel() {
        assertSame(graph.getLastLevel(), graph.getInitialLevel());
      }

    }

    @Test
    public static class GetLastExtensionLevel {

      @Test(expectedExceptions = UnsupportedOperationException.class)
      public void isUnsupportedOperation() {
        graph.getLastExtensionLevel();
      }

    }

    @Test
    public static class GetExtensionLevel {

      @Test(expectedExceptions = UnsupportedOperationException.class)
      public void reject() {
        graph.getExtensionLevel(0);
      }

    }

    @Test
    public static class GetExtensionLevels {

      @Test
      public void returnEmptyIterable() {
        assertTrue(Iterables.isEmpty(graph.getExtensionLevels()));
      }

    }

    @Test
    public static class GetExtensionLevelsReversed {

      @Test
      public void returnEmptyIterable() {
        assertTrue(Iterables.isEmpty(graph.getExtensionLevelsReversed()));
      }

    }

    @Test
    public static class Equality {

      @Test
      public void calculateHashCodeFromInitialLevel() {
        assertEquals(graph.hashCode(), initialLevel.hashCode());
      }

      @Test
      public void returnFalseWhenComparedWithNonGraph() {
        final Object x = new Object();
        assertNotEquals(graph, x);
      }

      @Test
      public void returnFalseWhenInitialLevelDiffers() {
        final Functionality f = make(aFunctionality().withIdentifier("f"));
        final Action a = make(aMinimalAction().withFunctionality(f));

        final Graph g2 = make(aGraph()
            .withInitialLevel(anInitialLevel()
                .withProvision(aFunctionalityProvision()
                    .withRequest(f)
                    .withOffer(f)
                    .withProvidingAction(a))));

        assertNotEquals(graph, g2);
      }

      @Test
      public void returnTrueWhenInitialLevelsEqual() {
        // simply create a duplicate graph
        final Graph g2 = make(aGraph().withInitialLevel(initialLevel));
        assertEquals(graph, g2);
      }

    }

  }

  @Test
  public static class AnExtendedGraph {

    private static final Graph graph;

    private static final Graph initialGraph;

    private static final Graph baseGraph;

    private static final ExtensionLevel xl1;

    private static final ExtensionLevel xl2;

    private static final ActionProvision ap2;

    static {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2)));

      final ActionProvision ap1 = make(anActionProvision()
          .withRequest(a1)
          .withPrecursor(a2));

      ap2 = make(anActionProvision()
          .withRequest(a2)
          .withPrecursor(a3));

      xl1 = make(anExtensionLevel().withProvision(ap1));
      xl2 = make(anExtensionLevel().withProvision(ap2));

      initialGraph = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aMinimalFunctionalityProvision()
                  .withProvidingAction(a1)
                  .withOffer(f)
                  .withRequest(f))));

      baseGraph = initialGraph.extendWith(xl1);
      graph = baseGraph.extendWith(xl2);
    }

    @Test
    public static class IsExtended {

      @Test
      public void returnTrue() {
        assertTrue(graph.isExtended());
      }

    }

    @Test
    public static class GetInitialLevel {

      @Test
      public void returnTheInitialLevel() {
        assertSame(graph.getInitialLevel(), initialGraph.getInitialLevel());
      }

    }

    @Test
    public static class GetBaseGraph {

      @Test
      public void returnBaseGraph() {
        assertSame(graph.getBaseGraph(), baseGraph);
      }

    }

    @Test
    public static class GetExtensionDepth {

      @Test
      public void returnNumberOfExtensionLevels() {
        assertEquals(graph.getExtensionDepth(), 2);
      }

    }

    @Test
    public static class GetExtensionLevel {

      @Test
      public void returnExtensionLevelWithGivenIndex() {
        assertSame(graph.getExtensionLevel(0), xl1);
        assertSame(graph.getExtensionLevel(1), xl2);
      }

      @Test(expectedExceptions = IndexOutOfBoundsException.class)
      public void rejectNegativeIndex() {
        graph.getExtensionLevel(-1);
      }

      @Test(expectedExceptions = IndexOutOfBoundsException.class)
      public void rejectExceedingIndex() {
        graph.getExtensionLevel(1 + graph.getExtensionDepth());
      }

    }

    @Test
    public static class GetLastExtensionLevel {

      @Test
      public void returnTheMostRecentExtensionWhenExtended() {
        assertSame(graph.getLastExtensionLevel(), xl2);
      }

    }

    @Test
    public static class GetLastLevel {

      @Test
      public void returnLastExtensionLevel() {
        assertSame(graph.getLastLevel(), graph.getLastExtensionLevel());
      }

    }

    @Test
    public static class GetExtensionLevels {

      @Test
      public void returnIterableOfAllExtensionLevelsIExtensionOrder() {
        final List<ExtensionLevel> xls = newArrayList(graph.getExtensionLevels());

        assertEquals(xls.size(), 2);

        assertSame(xls.get(0), xl1);
        assertSame(xls.get(1), xl2);
      }

    }

    @Test
    public static class GetExtensionLevelsReversed {

      @Test
      public void returnIterableOfAllExtensionLevelsInReverseExtensionOrder() {
        final List<ExtensionLevel> xls = newArrayList(graph.getExtensionLevelsReversed());

        assertEquals(xls.size(), 2);

        assertSame(xls.get(0), xl2);
        assertSame(xls.get(1), xl1);
      }

    }

    @Test
    public static class Equality {

      @Test
      public void calculateHashCodeFromBaseGraphAndLastExtensionLevel() {
        assertEquals(graph.hashCode(), Objects.hash(baseGraph, xl2));
      }

      @Test
      public void returnFalseWhenComparedWithNonGraph() {
        final Object x = new Object();
        assertNotEquals(graph, x);
      }

      @Test
      public void returnFalseWhenAnyLevelDiffers() {
        final Property p2 = make(aMinimalProperty().withName("p2"));
        final Property p3 = make(aMinimalProperty().withName("p3"));

        final Action a3 = make(aMinimalAction()
            .withEffects(aPropositionSet().withCleared(p2, p3)));

        final ExtensionLevel xl = make(anExtensionLevel()
            .withProvision(anActionProvision()
                .withRequest(ap2.getRequestedAction())
                .withPrecursor(a3)));

        final Graph g2 = baseGraph.extendWith(xl);

        assertNotEquals(graph, g2);
      }

      @Test
      public void returnTrueWhenAllLevelsEqual() {
        // simply create a duplicate graph
        final Graph g2 = baseGraph.extendWith(xl2);
        assertEquals(graph, g2);
      }

    }

  }

}
