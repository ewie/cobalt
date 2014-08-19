/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.extenders.providers;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.testing.Assert.assertContains;
import static vsr.cobalt.testing.Assert.assertContainsAll;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingExtendedPrecursorActionProviderTest {

  private static Repository repository(final Action... actions) {
    final Repository r = mock(Repository.class);
    when(r.getWidgetActions(actions[0].getWidget())).thenReturn(setOf(actions));
    return r;
  }

  private static PrecursorActionProvider precursorActionProvider(final Action action, final Set<Action> precursors) {
    final PrecursorActionProvider pap = mock(PrecursorActionProvider.class);
    when(pap.getPrecursorActionsFor(action)).thenReturn(precursors);
    return pap;
  }

  private static PrecursorActionProvider precursorActionProvider(final Action action, final Action... precursors) {
    return precursorActionProvider(action, setOf(precursors));
  }

  private static Action maintenance(final Property p) {
    return make(aMinimalAction().withPre(aPropositionSet().withFilled(p)));
  }

  @Test
  public static class GetPrecursorActionsFor {

    @Test
    public void includeMinimalPrecursors() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final Repository r = repository(a1, a2);

      final Set<Action> as = setOf(a2);

      final PrecursorActionProvider pap = precursorActionProvider(a1, as);
      final ComposingExtendedPrecursorActionProvider epap = new ComposingExtendedPrecursorActionProvider(r, pap);

      assertContainsAll(epap.getPrecursorActionsFor(a1), as);
    }

    @Test
    public void combineActionsToSatisfyPropertiesRequiredFilled() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withFilled(p2)));

      final Repository r = repository(a1, a2, a3);

      final PrecursorActionProvider pap = precursorActionProvider(a1, a2);
      final ComposingExtendedPrecursorActionProvider epap = new ComposingExtendedPrecursorActionProvider(r, pap);

      final Action a4 = Action.compose(a2, a3);

      assertContains(epap.getPrecursorActionsFor(a1), a4);
    }

    @Test
    public void combineWithMaintenanceActionsToSatisfyPropertiesRequiredFilled() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet()
              .withCleared(p1)
              .withFilled(p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Repository r = repository(a1, a2);

      final PrecursorActionProvider pap = precursorActionProvider(a1, a2);
      final ComposingExtendedPrecursorActionProvider epap = new ComposingExtendedPrecursorActionProvider(r, pap);

      final Action a4 = Action.compose(a2, maintenance(p2));

      assertContains(epap.getPrecursorActionsFor(a1), a4);
    }

  }

}
