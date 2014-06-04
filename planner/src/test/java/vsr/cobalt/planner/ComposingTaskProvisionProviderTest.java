/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

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
import static vsr.cobalt.models.makers.TaskMaker.aMinimalTask;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.planner.graph.makers.TaskProvisionMaker.aTaskProvision;
import static vsr.cobalt.testing.Assert.assertSubClass;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingTaskProvisionProviderTest {

  @Test
  public void extendsComposingProvisionProvider() {
    assertSubClass(ComposingTaskProvisionProvider.class, ComposingProvisionProvider.class);
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

  @Test
  public static class FindProvisionsFor {

    @Test
    public void delegateToRepository() {
      final Task t = make(aMinimalTask());

      final Action a = make(aMinimalAction().withTask(t));

      final Set<RealizedTask> rts = setOf(realizedTask(t, a));

      final Set<TaskProvision> tps = setOf(taskProvision(t, a));

      final Repository r = mock(Repository.class);
      when(r.findCompatibleTasks(t)).thenReturn(rts);

      final ComposingTaskProvisionProvider tpp = new ComposingTaskProvisionProvider(r);
      assertEquals(tpp.findProvisionsFor(t), tps);
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
