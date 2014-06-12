/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.repository.semantic;

import java.util.Set;

import com.hp.hpl.jena.query.Dataset;
import org.testng.annotations.Test;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.PublishedProperty;
import vsr.cobalt.models.RealizedTask;
import vsr.cobalt.models.Task;
import vsr.cobalt.models.Type;
import vsr.cobalt.repository.semantic.finders.CompatibleResourceFinder;

import static org.testng.Assert.assertEquals;
import static vsr.cobalt.repository.semantic.Datasets.loadDataset;
import static vsr.cobalt.repository.semantic.Models.action;
import static vsr.cobalt.repository.semantic.Models.property;
import static vsr.cobalt.repository.semantic.Models.task;
import static vsr.cobalt.repository.semantic.Models.type;
import static vsr.cobalt.repository.semantic.Models.widget;
import static vsr.cobalt.testing.Utilities.setOf;

@Test
public class CompatibleResourceFinderTest {

  @Test
  public void findCompatibleProperties() {
    final Dataset ds = loadDataset("compatibility/properties.n3");
    final CompatibleResourceFinder finder = new CompatibleResourceFinder(ds);

    final Type y0 = type(0);
    final Type y1 = type(1);
    final Type y2 = type(2);

    final Property p0 = property(0, y0);
    final Property p1 = property(1, y1);
    final Property p2 = property(2, y2);

    final Action a1 = action(widget(1), p1);
    final Action a2 = action(widget(2), p2);

    final PublishedProperty pp1 = new PublishedProperty(p1, a1);
    final PublishedProperty pp2 = new PublishedProperty(p2, a2);

    final Set<PublishedProperty> xpps = setOf(pp1, pp2);
    final Set<PublishedProperty> pps = finder.findCompatibleProperties(p0);

    assertEquals(pps, xpps);
  }

  @Test
  public void findCompatibleTasks() {
    final Dataset ds = loadDataset("compatibility/tasks.n3");
    final CompatibleResourceFinder finder = new CompatibleResourceFinder(ds);

    final Task t0 = task(0);
    final Task t1 = task(1);
    final Task t2 = task(2);

    final Action a1 = action(widget(1), t1);
    final Action a2 = action(widget(2), t2);

    final RealizedTask rt1 = new RealizedTask(t1, a1);
    final RealizedTask rt2 = new RealizedTask(t2, a2);

    final Set<RealizedTask> xrts = setOf(rt1, rt2);
    final Set<RealizedTask> rts = finder.findCompatibleTasks(t0);

    assertEquals(rts, xrts);
  }

}
