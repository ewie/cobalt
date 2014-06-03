/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;

@Test
public class BasicTaskProvisionProviderTest {

  @Test
  public void returnAllTaskProvisionsReturnedByRepository() {
    final Task t1 = make(aTask().withIdentifier("t1"));
    final Task t2 = make(aTask().withIdentifier("t2"));

    final TaskProvision tp1 = make(aTaskProvision()
        .withRequest(t1)
        .withOffer(t1)
        .withProvidingAction(aMinimalAction().withTask(t1)));

    final TaskProvision tp2 = make(aTaskProvision()
        .withRequest(t2)
        .withOffer(t2)
        .withProvidingAction(aMinimalAction().withTask(t2)));

    final Repository r = mock(Repository.class);
    when(r.findCompatibleTasks(t1)).thenReturn(setOf(tp1));
    when(r.findCompatibleTasks(t2)).thenReturn(setOf(tp2));

    final BasicTaskProvisionProvider tpp = new BasicTaskProvisionProvider(r);

    assertEquals(tpp.getProvisionsFor(setOf(t1, t2)), setOf(tp1, tp2));
  }

}
