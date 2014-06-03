/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import org.testng.annotations.Test;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.planner.models.Action;
import vsr.cobalt.planner.models.Task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.emptySet;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;
import static vsr.cobalt.testing.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.testing.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.testing.makers.TaskMaker.aTask;
import static vsr.cobalt.testing.makers.TaskProvisionMaker.aTaskProvision;

@Test
public class ComposingTaskProvisionProviderTest {

  @Test
  public void extendsComposingProvisionProvider() {
    assertSubClass(ComposingTaskProvisionProvider.class, ComposingProvisionProvider.class);
  }

  @Test
  public static class FindProvisionsFor {

    @Test
    public void delegateToRepository() {
      final Task t = make(aMinimalTask());
      final Set<TaskProvision> tps = emptySet();

      final Repository r = mock(Repository.class);
      when(r.findCompatibleTasks(t)).thenReturn(tps);

      final ComposingTaskProvisionProvider tpp = new ComposingTaskProvisionProvider(r);
      assertSame(tpp.findProvisionsFor(t), tps);
    }

  }

  @Test
  public static class CreateProvision {

    @Test
    public void createTaskProvision() {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));
      final Action a = make(aMinimalAction().withTask(t2));
      final TaskProvision tp = make(aTaskProvision()
          .withRequest(t1)
          .withOffer(t2)
          .withProvidingAction(a));
      final ComposingTaskProvisionProvider tpp = new ComposingTaskProvisionProvider(null);
      assertEquals(tpp.createProvision(t1, t2, a), tp);
    }

  }

  @Test
  public static class GetOffers {

    @Test
    public void returnRealizedTasks() {
      final Task t1 = make(aTask().withIdentifier("t1"));
      final Task t2 = make(aTask().withIdentifier("t2"));
      final Action a = make(aMinimalAction().withTask(t1, t2));
      final ComposingTaskProvisionProvider tpp = new ComposingTaskProvisionProvider(null);
      assertEquals(tpp.getOffers(a), setOf(t1, t2));
    }

  }

}
