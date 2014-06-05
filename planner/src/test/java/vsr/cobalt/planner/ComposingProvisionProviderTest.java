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
import vsr.cobalt.models.Offer;
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

      final DummyOffer o1 = offer(s1);
      final DummyOffer o2 = offer(s2);

      final DummyProvision p1 = provision(o1);
      final DummyProvision p2 = provision(o2);

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.getProvisionsFor(anySetOf(Subject.class))).thenCallRealMethod();
      when(cpp.getOffersFor(s1)).thenReturn(setOf(o1));
      when(cpp.getOffersFor(s2)).thenReturn(setOf(o2));

      final Set<DummyProvision> ps = cpp.getProvisionsFor(setOf(s1, s2));

      assertContainsAll(ps, setOf(p1, p2));
    }

    @Test
    public void combineProvidingActionsWhenPossible() {
      final Subject s1 = new Subject();
      final Subject s2 = new Subject();

      final DummyOffer o1 = offer(s1, actionWithTask("t1"));
      final DummyOffer o2 = offer(s2, actionWithTask("t2"));

      final Action a = Action.compose(o1.getAction(), o2.getAction());

      final DummyProvision p1 = provision(o1);
      final DummyProvision p2 = provision(o2);
      final DummyProvision p3 = provision(s1, a);
      final DummyProvision p4 = provision(s2, a);

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.getOffersFor(s1)).thenReturn(setOf(o1));
      when(cpp.getOffersFor(s2)).thenReturn(setOf(o2));

      final Set<DummyProvision> ps = cpp.getProvisionsFor(setOf(s1, s2));

      assertEquals(ps, setOf(p1, p2, p3, p4));
    }

    @Test
    public void doNotCombineActionsWhichCannotBeCombined() {
      final Subject s1 = new Subject();
      final Subject s2 = new Subject();

      final DummyOffer o1 = offer(s1, actionWithWidget("w1"));
      final DummyOffer o2 = offer(s2, actionWithWidget("w2"));

      final DummyProvision p1 = provision(o1);
      final DummyProvision p2 = provision(o2);

      final DummyComposingProvisionProvider cpp = mock(DummyComposingProvisionProvider.class, MOCKS_ABSTRACT_CLASS);
      when(cpp.getOffersFor(s1)).thenReturn(setOf(o1));
      when(cpp.getOffersFor(s2)).thenReturn(setOf(o2));

      final Set<DummyProvision> ps = cpp.getProvisionsFor(setOf(s1, s2));

      assertEquals(ps, setOf(p1, p2));
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

    private static DummyOffer offer(final Subject subject, final Action action) {
      return new DummyOffer(subject, action);
    }

    private static DummyOffer offer(final Subject subject) {
      return offer(subject, make(aMinimalAction()));
    }

    private static DummyProvision provision(final DummyOffer offer) {
      return new DummyProvision(offer);
    }

    private static DummyProvision provision(final Subject subject, final Action action) {
      return provision(offer(subject, action));
    }

  }

  /**
   * The subjects of {@link DummyProvision}.
   */
  private static class Subject {

    private static int ID = 0;

    private final int id;

    public Subject() {
      id = ++ID;
    }

  }

  private static class DummyOffer extends Offer<Subject> {

    /**
     * Associates actions with their offered subjects.
     * This mapping is necessary as we cannot extend {@link Action} and aggregate its offered subjects.
     */
    private static final SetMultimap<Action, Subject> offers = HashMultimap.create();

    public DummyOffer(final Subject subject, final Action action) {
      super(subject, action);
      // remember the offered subject for the given action
      offers.put(action, subject);
    }

    public static Set<Subject> getOffers(final Action action) {
      return offers.get(action);
    }

    @Override
    protected boolean canEqual(final Object other) {
      return true;
    }

  }

  private static class DummyProvision extends Provision<Subject> {

    public DummyProvision(final Subject request, final DummyOffer offer) {
      super(request, offer);
    }

    public DummyProvision(final DummyOffer offer) {
      super(offer);
    }

    @Override
    protected boolean canEqual(final Object other) {
      return true;
    }

  }

  private static abstract class DummyComposingProvisionProvider
      extends ComposingProvisionProvider<Subject, DummyOffer, DummyProvision> {

    @Override
    protected DummyProvision createProvision(final Subject request, final Subject subject, final Action action) {
      return new DummyProvision(request, new DummyOffer(subject, action));
    }

    @Override
    protected Set<Subject> getOfferedSubjects(final Action action) {
      return DummyOffer.getOffers(action);
    }

  }

}
