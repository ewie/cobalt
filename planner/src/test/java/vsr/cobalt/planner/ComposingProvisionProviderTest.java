/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.planner.graph.Provision;

import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.aMinimalAction;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.models.makers.WidgetMaker.aWidget;
import static vsr.cobalt.testing.Assert.assertContainsAll;
import static vsr.cobalt.testing.Utilities.MOCKS_ABSTRACT_CLASS;
import static vsr.cobalt.testing.Utilities.make;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class ComposingProvisionProviderTest {

  @Test
  public static class GetProvisionsFor {

    @Test
    public void includeBasicCompatibleProvisions() {
      final Subject s1 = new Subject();
      final Subject s2 = new Subject();

      final DummyProvision dp1 = provision(s1);
      final DummyProvision dp2 = provision(s2);

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.getProvisionsFor(anySetOf(Subject.class))).thenCallRealMethod();
      when(cpp.findProvisionsFor(s1)).thenReturn(setOf(dp1));
      when(cpp.findProvisionsFor(s2)).thenReturn(setOf(dp2));

      final Set<DummyProvision> tps = cpp.getProvisionsFor(setOf(s1, s2));

      assertContainsAll(tps, setOf(dp1, dp2));
    }

    @Test
    public void combineProvidingActionsWhenPossible() {
      final Subject s1 = new Subject();
      final Subject s2 = new Subject();

      final DummyProvision dp1 = provision(s1, actionWithTask("t1"));
      final DummyProvision dp2 = provision(s2, actionWithTask("t2"));

      final Action a1 = Action.compose(dp1.getProvidingAction(), dp2.getProvidingAction());

      final DummyProvision dp3 = provision(s1, a1);
      final DummyProvision dp4 = provision(s2, a1);

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.findProvisionsFor(s1)).thenReturn(setOf(dp1));
      when(cpp.findProvisionsFor(s2)).thenReturn(setOf(dp2));

      final Set<DummyProvision> tps = cpp.getProvisionsFor(setOf(s1, s2));

      assertEquals(tps, setOf(dp1, dp2, dp3, dp4));
    }

    @Test
    public void doNotCombineActionsWhichCannotBeCombined() {
      final Subject s1 = new Subject();
      final Subject s2 = new Subject();

      final DummyProvision dp1 = provision(s1, actionWithWidget("w1"));
      final DummyProvision dp2 = provision(s1, actionWithWidget("w2"));

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.findProvisionsFor(s1)).thenReturn(setOf(dp1));
      when(cpp.findProvisionsFor(s2)).thenReturn(setOf(dp2));

      final Set<DummyProvision> tps = cpp.getProvisionsFor(setOf(s1, s2));

      assertEquals(tps, setOf(dp1, dp2));
    }

    private static Action actionWithWidget(final String widgetId) {
      return make(aMinimalAction()
          .withWidget(aWidget()
              .withIdentifier(widgetId)));
    }

    private static Action actionWithTask(final String taskId) {
      return make(aMinimalAction()
          .withTask(aTask()
              .withIdentifier(taskId)));
    }

    private static DummyProvision provision(final Subject subject) {
      return new DummyProvision(subject, make(aMinimalAction()));
    }

    private static DummyProvision provision(final Subject subject, final Action action) {
      return new DummyProvision(subject, action);
    }

  }

  /**
   * The subjects of {@link DummyProvision}.
   */
  private static class Subject {
  }

  private static class DummyProvision extends Provision<Subject> {

    /**
     * Associates actions with their offered subjects.
     * This mapping is necessary as we cannot extend {@link Action} and aggregate its offered subjects.
     */
    private static final SetMultimap<Action, Subject> offers = HashMultimap.create();

    public DummyProvision(final Subject request, final Subject offer, final Action action) {
      super(request, offer, action);
      // remember the offer for this action
      offers.put(action, offer);
    }

    public DummyProvision(final Subject subject, final Action action) {
      this(subject, subject, action);
    }

    public static Set<Subject> getOffers(final Action action) {
      return offers.get(action);
    }

    @Override
    protected boolean canEqual(final Object other) {
      return true;
    }

  }

  private static abstract class DummyComposingProvisionProvider
      extends ComposingProvisionProvider<Subject, DummyProvision> {

    @Override
    protected DummyProvision createProvision(final Subject request, final Subject offer, final Action action) {
      return new DummyProvision(request, offer, action);
    }

    @Override
    protected Set<Subject> getOffers(final Action action) {
      return DummyProvision.getOffers(action);
    }

  }

}
