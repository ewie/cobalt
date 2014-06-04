/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Repository;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.TaskProvision;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class BasicTaskProvisionProviderTest {

  @Test
  public void returnAllTaskProvisionsReturnedByRepository() {
    final Task t1 = make(aTask().withIdentifier("t1"));
    final Task t2 = make(aTask().withIdentifier("t2"));

    final Action a1 = make(aMinimalAction().withTask(t1));
    final Action a2 = make(aMinimalAction().withTask(t2));

    final Repository r = mock(Repository.class);
    when(r.findCompatibleTasks(t1)).thenReturn(setOf(realizedTask(t1, a1)));
    when(r.findCompatibleTasks(t2)).thenReturn(setOf(realizedTask(t2, a2)));

    final BasicTaskProvisionProvider tpp = new BasicTaskProvisionProvider(r);

    final TaskProvision tp1 = taskProvision(t1, a1);
    final TaskProvision tp2 = taskProvision(t2, a2);

    assertEquals(tpp.getProvisionsFor(setOf(t1, t2)), setOf(tp1, tp2));
  }

  private static TaskProvision taskProvision(final Task t, final Action a) {
    return make(aTaskProvision()
        .withRequest(t)
        .withOffer(t)
        .withProvidingAction(a));
  }

  private static RealizedTask realizedTask(final Task t, final Action a) {
    return new RealizedTask(t, a);
  }

}
