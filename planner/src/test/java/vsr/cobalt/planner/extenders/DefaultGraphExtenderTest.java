/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Functionality;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Widget;
import vsr.cobalt.planner.PlanningException;
import vsr.cobalt.planner.extenders.providers.PrecursorActionProvider;
import vsr.cobalt.planner.extenders.providers.PropertyProvisionProvider;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.PropertyProvision;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.FunctionalityMaker.aMinimalFunctionality;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.WidgetMaker.aMinimalWidget;
import static vsr.cobalt.planner.graph.makers.ActionProvisionMaker.anActionProvision;
import static vsr.cobalt.planner.graph.makers.ExtensionLevelMaker.anExtensionLevel;
import static vsr.cobalt.planner.graph.makers.FunctionalityProvisionMaker.aFunctionalityProvision;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aGraph;
import static vsr.cobalt.planner.graph.makers.GraphMaker.aMinimalGraph;
import static vsr.cobalt.planner.graph.makers.InitialLevelMaker.anInitialLevel;
import static vsr.cobalt.planner.graph.makers.PropertyProvisionMaker.aPropertyProvision;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class DefaultGraphExtenderTest {

  private static final PrecursorActionProvider NO_PRECURSORS = emptyPrecursorActionProvider();

  private static final PropertyProvisionProvider NO_PROPERTIES = emptyPropertyProvisionProvider();

  private static final CyclicDependencyDetector NO_CYCLES = defaultCyclicDependencyDetector();

  private static PrecursorActionProvider emptyPrecursorActionProvider() {
    final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
    when(pap.getPrecursorActionsFor(any(Action.class))).thenReturn(emptySet(Action.class));
    return pap;
  }

  private static PropertyProvisionProvider emptyPropertyProvisionProvider() {
    final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
    when(ppr.getProvisionsFor(anySetOf(Property.class))).thenReturn(emptySet(PropertyProvision.class));
    return ppr;
  }

  private static CyclicDependencyDetector defaultCyclicDependencyDetector() {
    final CyclicDependencyDetector cdd = mock(CyclicDependencyDetector.class);
    when(cdd.createsCyclicDependencyVia(any(Action.class), any(Action.class), any(Graph.class))).thenReturn(false);
    return cdd;
  }

  @Test
  public static class ExtendGraph {

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "cannot extend satisfied graph")
    public void rejectSatisfiedGraph() throws Exception {
      final Graph g = make(aMinimalGraph());
      final DefaultGraphExtender gx = new DefaultGraphExtender(NO_PRECURSORS, NO_PROPERTIES, NO_CYCLES);
      gx.extendGraph(g);
    }

    @Test
    public void ignoreSatisfiedActions() {
      final Functionality f = make(aMinimalFunctionality());
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction().withFunctionality(f));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a2))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a2)).thenReturn(emptySet(Action.class));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);

      try {
        gx.extendGraph(g);
      } catch (final PlanningException ignored) {
      }

      verify(pap, never()).getPrecursorActionsFor(a1);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot satisfy any action")
    public void throwWhenNoActionCanBeEnabledWithPrecursor() throws Exception {
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

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a)).thenReturn(emptySet(Action.class));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);

      gx.extendGraph(g);
    }

    @Test(expectedExceptions = PlanningException.class,
        expectedExceptionsMessageRegExp = "cannot satisfy any action")
    public void throwWhenNoActionCanBeEnabledWithProviders() throws Exception {
      final Functionality f = make(aMinimalFunctionality());
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a)).thenReturn(emptySet(Action.class));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);

      gx.extendGraph(g);
    }

    @Test
    public void extendWithPrecursorActions() throws Exception {
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
                  .withProvidingAction(a1))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(setOf(a2));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);
      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void extendWithoutPrecursorActions() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Widget w = make(aMinimalWidget().withPublic(p));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p)));

      // a property provider for a1
      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      final PropertyProvision pp = make(aPropertyProvision()
          .withRequest(p)
          .withOffer(p)
          .withProvidingAction(a2));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(emptySet(Action.class));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p))).thenReturn(setOf(pp));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, ppr, NO_CYCLES);
      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void provideFilledPropertiesNotSatisfiedByPrecursor() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      // a precursor for a1
      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      // a property provider for a1
      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final PropertyProvision pp = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a3));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(setOf(a2));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p2))).thenReturn(setOf(pp));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, ppr, NO_CYCLES);
      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)
              .withProvision(pp)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void createActionProvisionForEachPropertyProvisionCombination() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1, p2)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a2));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a3));

      final PropertyProvision pp3 = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a3));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(emptySet(Action.class));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p1, p2))).thenReturn(setOf(pp1, pp2, pp3));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, ppr, NO_CYCLES);
      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp1, pp3))
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp2, pp3)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void collectAllRequiredProperties() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p1)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet().withFilled(p2)));

      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a4 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final Action a5 = Action.compose(a3, a4);

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a2))));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a3));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a4));

      final PropertyProvision pp3 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a5));

      final PropertyProvision pp4 = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a5));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(emptySet(Action.class));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p1, p2))).thenReturn(setOf(pp1, pp2, pp3, pp4));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, ppr, NO_CYCLES);
      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp1))
          .withProvision(anActionProvision()
              .withRequest(a2)
              .withProvision(pp2))
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp3))
          .withProvision(anActionProvision()
              .withRequest(a2)
              .withProvision(pp4)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void ignoreActionsWithoutPrecursor() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet().withCleared(p2)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a2))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(emptySet(Action.class));
      when(pap.getPrecursorActionsFor(a2)).thenReturn(setOf(a3));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a2)
              .withPrecursor(a3)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void ignoreActionProvisionWhenAnyRequiredPropertyCannotBeProvided() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      // a precursor action fully satisfying a1
      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      // a precursor action not fully satisfying a1
      // because p2 cannot be provided otherwise, this precursor should be ignored
      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(setOf(a2, a3));

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, NO_CYCLES);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void ignorePrecursorActionCausingCyclicDependency() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withCleared(p1)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet()
              .withCleared(p1)));

      // will cause cyclic dependency with a1
      final Action a3 = Action.compose(a1, make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(p2))));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(setOf(a2, a3));

      final CyclicDependencyDetector cdd = mock(CyclicDependencyDetector.class);
      when(cdd.createsCyclicDependencyVia(a3, a1, g)).thenReturn(true);

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, NO_PROPERTIES, cdd);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withPrecursor(a2)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void filterPrecursorActionsCausingCyclicDependencies() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p1)));

      // will cause cyclic dependency with a1, therefore cannot be a precursor
      final Action a2 = Action.compose(a1, make(aMinimalAction()
          .withWidget(w)
          .withPre(aPropositionSet()
              .withCleared(p2))));

      // an action supporting a1 via a property provision, will be used instead of a2
      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PropertyProvision pp = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a3));

      final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
      when(pap.getPrecursorActionsFor(a1)).thenReturn(setOf(a2));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p1))).thenReturn(setOf(pp));

      final CyclicDependencyDetector cdd = mock(CyclicDependencyDetector.class);
      when(cdd.createsCyclicDependencyVia(a2, a1, g)).thenReturn(true);

      final DefaultGraphExtender gx = new DefaultGraphExtender(pap, ppr, cdd);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void ignoreProvidingActionCausingCyclicDependency() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p = make(aMinimalProperty());

      final Widget w = make(aMinimalWidget().withPublic(p));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p)));

      // will cause cyclic dependency with a1
      final Action a3 = Action.compose(a1, make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p))));

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withRequest(p)
          .withOffer(p)
          .withProvidingAction(a2));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withRequest(p)
          .withOffer(p)
          .withProvidingAction(a3));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p))).thenReturn(setOf(pp1, pp2));

      final CyclicDependencyDetector cdd = mock(CyclicDependencyDetector.class);
      when(cdd.createsCyclicDependencyVia(a3, a1, g)).thenReturn(true);

      final DefaultGraphExtender gx = new DefaultGraphExtender(NO_PRECURSORS, ppr, cdd);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp1)));

      assertEquals(xg.getLastLevel(), xl);
    }

    @Test
    public void doNotCreateActionProvisionWithProvidingActionRepresentedByAnotherProvidingAction() throws Exception {
      final Functionality f = make(aMinimalFunctionality());

      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Widget w = make(aMinimalWidget().withPublic(p1, p2));

      final Action a1 = make(aMinimalAction()
          .withWidget(w)
          .withFunctionality(f)
          .withPre(aPropositionSet()
              .withFilled(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p1)));

      final Action a3 = make(aMinimalAction()
          .withWidget(w)
          .withEffects(aPropositionSet()
              .withFilled(p2)));

      final Action a4 = Action.compose(a2, a3);

      final PropertyProvision pp1 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a2));

      final PropertyProvision pp2 = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a3));

      final PropertyProvision pp3 = make(aPropertyProvision()
          .withRequest(p1)
          .withOffer(p1)
          .withProvidingAction(a4));

      final PropertyProvision pp4 = make(aPropertyProvision()
          .withRequest(p2)
          .withOffer(p2)
          .withProvidingAction(a4));

      final Graph g = make(aGraph()
          .withInitialLevel(anInitialLevel()
              .withProvision(aFunctionalityProvision()
                  .withRequest(f)
                  .withOffer(f)
                  .withProvidingAction(a1))));

      final PropertyProvisionProvider ppr = mock(PropertyProvisionProvider.class);
      when(ppr.getProvisionsFor(setOf(p1, p2))).thenReturn(setOf(pp1, pp2, pp3, pp4));

      final DefaultGraphExtender gx = new DefaultGraphExtender(NO_PRECURSORS, ppr, NO_CYCLES);

      final Graph xg = gx.extendGraph(g);

      final ExtensionLevel xl = make(anExtensionLevel()
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp1, pp2))
          .withProvision(anActionProvision()
              .withRequest(a1)
              .withProvision(pp3, pp4)));

      assertEquals(xg.getLastLevel(), xl);
    }

  }

}
