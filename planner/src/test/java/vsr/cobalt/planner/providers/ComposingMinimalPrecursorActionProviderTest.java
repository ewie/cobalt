/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.providers;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.Assert.assertContains;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Assert.assertNotContains;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingMinimalPrecursorActionProviderTest {

  private static Action maintenance(final Property p) {
    return make(aMinimalAction().withPre(aPropositionSet().withCleared(p)));
  }

  private static Repository repository(final Action... actions) {
    final Repository r = mock(Repository.class);
    when(r.getWidgetActions(actions[0].getWidget())).thenReturn(setOf(actions));
    return r;
  }

  @Test
  public static class GetPrecursorActionsFor {

    @Test
    public void returnEmptySetWhenNoActionMatches() {
      final Property p = make(aMinimalProperty());

      final Action a = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Repository r = repository(a);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      assertEmpty(pap.getPrecursorActionsFor(a));
    }

    @Test
    public void returnMatchingActions() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p)));

      final Repository r = repository(a1, a2);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      assertContains(pap.getPrecursorActionsFor(a1), a2);
    }

    @Test
    public void ignoreActionsNotMatching() {
      final Property p = make(aMinimalProperty());

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      final Action a2 = make(aMinimalAction());

      final Repository r = repository(a1, a2);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      assertNotContains(pap.getPrecursorActionsFor(a1), setOf(a2));
    }

    @Test
    public void combineActionsToCreateMatchingPrecursorAction() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2)));

      final Repository r = repository(a1, a2, a3);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      final Action a4 = Action.compose(a2, a3);

      assertContains(pap.getPrecursorActionsFor(a1), a4);
    }

    @Test
    public void combineWithMaintenanceActions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Repository r = repository(a1, a2);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      final Action a3 = Action.compose(a2, maintenance(p2));

      assertContains(pap.getPrecursorActionsFor(a1), a3);
    }

    @Test
    public void doNotCombineOnlyMaintenanceActions() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withTask(aMinimalTask())
          .withPre(aPropositionSet().withCleared(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Repository r = repository(a1, a2);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      final Action a3 = Action.compose(maintenance(p1), maintenance(p2));

      assertNotContains(pap.getPrecursorActionsFor(a1), a3);
    }

    @Test
    public void doNotCombineActionsBeingMutex() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1, p2)));

      // mutex with a3 because of pre-condition
      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1))
          .withPre(aPropositionSet().withCleared(p2)));

      // mutex with a2 because of pre-condition
      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2))
          .withPre(aPropositionSet().withFilled(p2)));

      final Repository r = repository(a1, a2, a3);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      // combining actions being mutex would throw an exception, because the combined propositions would have
      // intersecting sets of cleared and filled properties
      pap.getPrecursorActionsFor(a1);
    }

    @Test
    public void allowExcessivePrecursors() {
      final Property p1 = make(aMinimalProperty().withName("p1"));
      final Property p2 = make(aMinimalProperty().withName("p2"));
      final Property p3 = make(aMinimalProperty().withName("p3"));

      final Action a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p1, p2)));

      final Action a2 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p1)));

      final Action a3 = make(aMinimalAction()
          .withEffects(aPropositionSet().withCleared(p2, p3)));

      final Repository r = repository(a1, a2, a3);

      final ComposingMinimalPrecursorActionProvider pap = new ComposingMinimalPrecursorActionProvider(r);

      final Action a4 = Action.compose(a2, a3);

      assertContains(pap.getPrecursorActionsFor(a1), a4);
    }

  }

}
