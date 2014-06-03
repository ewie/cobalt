/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Property;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vsr.cobalt.testing.Assert.assertContains;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.EffectSetMaker.anEffectSet;
import static vsr.cobalt.testing.makers.PropertyMaker.aMinimalProperty;
import static vsr.cobalt.testing.makers.PropositionSetMaker.aPropositionSet;

@Test
public class BasicPrecursorActionProviderTest {

  @Test
  public static class GetPrecursorActionsFor {

    private Action a1;

    private Action a2;

    private BasicPrecursorActionProvider pap;

    @BeforeMethod
    public void setUp() {
      final Property p = make(aMinimalProperty());

      a1 = make(aMinimalAction()
          .withPre(aPropositionSet().withCleared(p)));

      a2 = make(aMinimalAction()
          .withEffects(anEffectSet().withToClear(p)));

      final Repository r = mock(Repository.class);
      when(r.getWidgetActions(a1.getWidget())).thenReturn(setOf(a1, a2));

      pap = new BasicPrecursorActionProvider(r);
    }

    @Test
    public void selectPrecursorActions() {
      assertContains(pap.getPrecursorActionsFor(a1), a2);
    }

    @Test
    public void filterNonPrecursorActions() {
      assertContains(pap.getPrecursorActionsFor(a1), a1);
    }

  }

}
