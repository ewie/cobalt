/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.hp.hpl.jena.query.Dataset;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Interaction;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.models.Widget;
import vsr.cobalt.repository.semantic.finders.WidgetActionFinder;

import static org.testng.Assert.assertEquals;
import static vsr.cobalt.models.makers.ActionMaker.anAction;
import static vsr.cobalt.models.makers.InteractionMaker.anInteraction;
import static vsr.cobalt.models.makers.PropositionSetMaker.aPropositionSet;
import static vsr.cobalt.models.makers.TaskMaker.aTask;
import static vsr.cobalt.repository.semantic.Datasets.loadDataset;
import static vsr.cobalt.repository.semantic.Models.property;
import static vsr.cobalt.repository.semantic.Models.type;
import static vsr.cobalt.testing.Assert.assertEmpty;
import static vsr.cobalt.testing.Utilities.make;

@Test
public class WidgetActionFinderTest {

  private WidgetActionFinder finder;

  @BeforeMethod
  public void setUp() {
    final Dataset ds = loadDataset("widget-actions.n3");
    finder = new WidgetActionFinder(ds);
  }

  @Test
  public void ignoreActionsOfDifferentWidgets() {
    final Widget widget = new Widget("urn:example:widget:1");
    final Set<Action> actions = finder.findWidgetActions(widget);
    for (final Action a : actions) {
      assertEquals(a.getWidget(), widget);
    }
  }

  @Test
  public void internalizeAction() {
    final Widget widget = new Widget("urn:example:widget:1");
    final Set<Action> actions = finder.findWidgetActions(widget);

    final Type y1 = type(1);
    final Type y2 = type(2);
    final Type y3 = type(3);
    final Type y4 = type(4);
    final Type y5 = type(5);

    final Property p1 = property(1, y1);
    final Property p2 = property(2, y2);
    final Property p3 = property(3, y3);
    final Property p4 = property(4, y4);
    final Property p5 = property(5, y5);

    final Task t1 = make(aTask().withIdentifier("urn:example:task:1"));

    final Interaction i1 = make(anInteraction().withInstruction("i1"));

    final Action x = make(anAction()
        .withWidget(widget)
        .withTask(t1)
        .withInteraction(i1)
        .withPub(p5)
        .withPre(aPropositionSet()
            .withCleared(p1)
            .withFilled(p2))
        .withEffects(aPropositionSet()
            .withCleared(p3)
            .withFilled(p4)));

    final Action a = Iterables.get(actions, 0);

    assertEquals(a, x);
  }

  @Test
  public void defaultToEmptyPreConditions() {
    final Widget widget = new Widget("urn:example:widget:2");
    final Set<Action> actions = finder.findWidgetActions(widget);

    final Action a = Iterables.get(actions, 0);

    assertEmpty(a.getPreConditions());
  }

  @Test
  public void defaultToEmptyEffects() {
    final Widget widget = new Widget("urn:example:widget:2");
    final Set<Action> actions = finder.findWidgetActions(widget);

    final Action a = Iterables.get(actions, 0);

    assertEmpty(a.getEffects());
  }

}
