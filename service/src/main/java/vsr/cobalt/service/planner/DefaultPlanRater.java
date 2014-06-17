/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.service.planner;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Repository;
import vsr.cobalt.planner.Plan;
import vsr.cobalt.planner.collectors.rating.PlanRater;
import vsr.cobalt.planner.collectors.rating.Rating;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;
import vsr.cobalt.planner.graph.PropertyProvision;
import vsr.cobalt.planner.graph.TaskProvision;

/**
 * The default plan rater using a metric of 3 properties:
 * <ul>
 * <li>favor less interactions</li>
 * <li>favor more concrete published properties (minimal type distance)</li>
 * <li>favor more concrete task realizations (minimal task distance)</li>
 * </ul>
 * <p/>
 * In addition, the rater identifies plans not executable by the target environment, i.e.
 * <ul>
 * <li>compatible properties must have the same name</li>
 * <li>each action must have at least one interaction</li>
 * </ul>
 *
 * @author Erik Wienhold
 */
public class DefaultPlanRater implements PlanRater {

  private final Repository repository;

  public DefaultPlanRater(final Repository repository) {
    this.repository = repository;
  }

  @Override
  public Rating rate(final Plan plan) {
    final Graph g = plan.getGraph();

    if (isSupported(g)) {
      return rate(g);
    }

    return null;
  }

  private Rating rate(final Graph graph) {
    int value = rateInitialLevel(graph.getInitialLevel());

    for (final ExtensionLevel xl : graph.getExtensionLevelsReversed()) {
      value += rateLevel(xl);
      value += rateActionProvisions(xl.getActionProvisions());
    }

    return new Rating(value);
  }

  private int rateInitialLevel(final InitialLevel level) {
    return rateLevel(level) + rateTaskProvisions(level.getTaskProvisions());
  }

  private int rateLevel(final Level level) {
    return rateActions(level.getRequiredActions());
  }

  private int rateActions(final Iterable<Action> actions) {
    int value = 0;
    for (final Action a : actions) {
      value += a.getInteractions().size();
    }
    return value;
  }

  private int rateTaskProvisions(final Iterable<TaskProvision> provisions) {
    int value = 0;
    for (final TaskProvision tp : provisions) {
      value += rateTaskProvision(tp);
    }
    return value;
  }

  private int rateTaskProvision(final TaskProvision provision) {
    return repository.getDistance(provision.getRequest(), provision.getOffer().getSubject());
  }

  private int rateActionProvisions(final Iterable<ActionProvision> provisions) {
    int value = 0;
    for (final ActionProvision ap : provisions) {
      value += rateActionProvision(ap);
    }
    return value;
  }

  private int rateActionProvision(final ActionProvision provision) {
    int value = 0;
    for (final PropertyProvision pp : provision.getPropertyProvisions()) {
      value += ratePropertyProvision(pp);
    }
    return value;
  }

  private int ratePropertyProvision(final PropertyProvision provision) {
    return repository.getDistance(provision.getRequest().getType(),
        provision.getOffer().getSubject().getType());
  }

  /**
   * Test if a plan with the given graph is supported, i.e. it can be executed by the target environment.
   *
   * @param graph a graph of a plan
   *
   * @return true when the graph is supported, false otherwise
   */
  private boolean isSupported(final Graph graph) {
    if (!isSupported(graph.getInitialLevel())) {
      return false;
    }
    for (final ExtensionLevel xl : graph.getExtensionLevelsReversed()) {
      if (!isSupported(xl)) {
        return false;
      }
    }
    return true;
  }

  private boolean isSupported(final Level level) {
    for (final Action a : level.getRequiredActions()) {
      if (!isSupported(a)) {
        return false;
      }
    }
    return true;
  }

  private boolean isSupported(final ExtensionLevel level) {
    if (!isSupported((Level) level)) {
      return false;
    }
    for (final ActionProvision ap : level.getActionProvisions()) {
      for (final PropertyProvision pp : ap.getPropertyProvisions()) {
        if (!isSupported(pp)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isSupported(final Action action) {
    return !action.getInteractions().isEmpty();
  }

  /**
   * Test if a property provision is supported, i.e. the requested and offered properties have the same name.
   *
   * @param provision a property provision
   *
   * @return true when supported, false otherwise
   */
  private boolean isSupported(final PropertyProvision provision) {
    final Property r = provision.getRequest();
    final Property o = provision.getOffer().getSubject();
    return r.getName().equals(o.getName());
  }

}
