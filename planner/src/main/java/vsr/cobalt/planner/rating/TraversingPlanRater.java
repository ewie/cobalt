/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.rating;

import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.Lists;
import vsr.cobalt.models.Action;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * Rates a plan by traversing its graph.
 * <p/>
 * The traversal starts with the entire graph followed by each level in extension order (i.e. start at the initial
 * level). After the initial level has been visited, its task provisions and finally its required actions are visited.
 * For each extension level, its action provisions followed by its required actions are visited. For each action
 * provision its property provisions are visited.
 *
 * @author Erik Wienhold
 */
public class TraversingPlanRater implements PlanRater {

  private final PlanRaterVisitor visitor;

  /**
   * @param visitor a visitor to rate each graph element
   */
  public TraversingPlanRater(final PlanRaterVisitor visitor) {
    this.visitor = visitor;
  }

  @Override
  public Rating rate(final Plan plan) {
    final Traversal t = new Traversal(plan.getGraph(), visitor);
    final Integer value = t.getValue();
    if (value == null) {
      return null;
    }
    return new Rating(value);
  }

  private static class Traversal {

    private final Graph graph;

    private final PlanRaterVisitor visitor;

    private int value;

    public Traversal(final Graph graph, final PlanRaterVisitor visitor) {
      this.graph = graph;
      this.visitor = visitor;
    }

    public Integer getValue() {
      if (!rateGraph(graph)) {
        return null;
      }

      final InitialLevel il = graph.getInitialLevel();
      if (!rateLevel(il)) {
        return null;
      }

      for (final TaskProvision tp : il.getTaskProvisions()) {
        if (!rateTaskProvision(tp)) {
          return null;
        }
      }

      for (final Action a : il.getRequiredActions()) {
        if (!rateAction(a)) {
          return null;
        }
      }

      final ListIterator<ExtensionLevel> xls = getExtensionLevels();
      while (xls.hasPrevious()) {
        final ExtensionLevel xl = xls.previous();
        if (!rateLevel(xl)) {
          return null;
        }
        for (final ActionProvision ap : xl.getActionProvisions()) {
          if (!rateActionProvision(ap)) {
            return null;
          }
          for (final PropertyProvision pp : ap.getPropertyProvisions()) {
            if (!ratePropertyProvision(pp)) {
              return null;
            }
          }
        }
        for (final Action a : xl.getRequiredActions()) {
          if (!rateAction(a)) {
            return null;
          }
        }
      }

      return value;
    }

    private boolean rateGraph(final Graph graph) {
      return add(visitor.rateGraph(graph));
    }

    private boolean rateLevel(final Level level) {
      return add(visitor.rateLevel(level));
    }

    private boolean rateTaskProvision(final TaskProvision provision) {
      return add(visitor.rateTaskProvision(provision));
    }

    private boolean rateActionProvision(final ActionProvision provision) {
      return add(visitor.rateActionProvision(provision));
    }

    private boolean ratePropertyProvision(final PropertyProvision provision) {
      return add(visitor.ratePropertyProvision(provision));
    }

    private boolean rateAction(final Action action) {
      return add(visitor.rateAction(action));
    }

    private boolean add(final Integer value) {
      if (value == null) {
        return false;
      }
      this.value += value;
      return true;
    }

    private ListIterator<ExtensionLevel> getExtensionLevels() {
      final List<ExtensionLevel> xls = Lists.newArrayList(graph.getExtensionLevels());
      return xls.listIterator(xls.size());
    }

  }

}
