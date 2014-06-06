/*
* Copyright (c) 2014, Erik Wienhold
* All rights reserved.
*
* Licensed under the BSD 3-Clause License.
*/

package vsr.cobalt.planner;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vsr.cobalt.models.Action;
import vsr.cobalt.models.Task;
import vsr.cobalt.planner.graph.ActionProvision;
import vsr.cobalt.planner.graph.ExtensionLevel;
import vsr.cobalt.planner.graph.Graph;
import vsr.cobalt.planner.graph.InitialLevel;
import vsr.cobalt.planner.graph.Level;
import vsr.cobalt.planner.graph.TaskProvision;
import vsr.cobalt.utils.ProbingIterator;
import vsr.cobalt.utils.ProductSet;
import vsr.cobalt.utils.ProductSetIterator;

/**
 * Iterates over all plans available in a graph.
 *
 * @author Erik Wienhold
 */
public class PlanIterator extends ProbingIterator<Plan> {

  /**
   * The graph potentially containing plans.
   */
  private final Graph graph;

  /**
   * The minimum graph depth for a plan to be considered.
   */
  private final int minDepth;

  /**
   * The maximum graph depth for a plan to be considered.
   */
  private final int maxDepth;

  private final InitialFrame initialFrame;

  private final Deque<ExtensionFrame> extensionFrames;

  private final ActionReachabilityIndex reachabilityIndex;

  /**
   * Create a new plan iterator using a graph and depth range.
   * <p/>
   * Because the plan extraction usually happens after each graph extension, the constraining of the graph depth allows
   * to ignore plans which have been extracted before, when the plan iterator had been used with a less extended
   * version of the given graph.
   *
   * @param graph    a graph to examine
   * @param minDepth the minimum graph depth for a plan
   * @param maxDepth the maximum graph depth for a plan
   */
  public PlanIterator(final Graph graph, final int minDepth, final int maxDepth) {
    if (minDepth < 1) {
      throw new IllegalArgumentException("expecting minDepth >= 1");
    }
    if (minDepth > maxDepth) {
      throw new IllegalArgumentException("expecting minDepth <= maxDepth");
    }
    this.graph = graph;
    this.minDepth = minDepth;
    this.maxDepth = maxDepth;
    initialFrame = new InitialFrame(graph.getInitialLevel());
    extensionFrames = new ArrayDeque<>(graph.getExtensionDepth());
    reachabilityIndex = new ActionReachabilityIndex(graph);
  }

  /**
   * Create a new plan iterator without constraining the maximum graph depth.
   *
   * @param graph    a graph to examine
   * @param minDepth the minimum graph depth for a plan
   */
  public PlanIterator(final Graph graph, final int minDepth) {
    this(graph, minDepth, graph.getDepth());
  }

  /**
   * Create a new plan iterator without any constrains on the graph depth.
   *
   * @param graph a graph to examine
   */
  public PlanIterator(final Graph graph) {
    this(graph, 1);
  }

  /**
   * @return the minimum graph depth
   */
  public int getMinDepth() {
    return minDepth;
  }

  /**
   * @return the maximum graph depth
   */
  public int getMaxDepth() {
    return maxDepth;
  }

  @Override
  protected Plan probeNextValue() {
    while (true) {
      evolve();
      // the stack may be empty after evolving it
      if (isEmpty()) {
        break;
      }
      // create and return a plan when possible
      final Plan p = createPlan();
      if (p != null) {
        return p;
      }
      // grow the stack when there is no plan
      grow();
    }
    return null;
  }

  private boolean isEmpty() {
    return !initialFrame.hasLevel();
  }

  private int getDepth() {
    return 1 + extensionFrames.size();
  }

  /**
   * Evolve the stack, i.e. consider the next possible level of the current stack frame or, when no such level exists,
   * pop the stack. The stack may be empty afterwards.
   */
  private void evolve() {
    while (!extensionFrames.isEmpty()) {
      final ExtensionFrame xf = extensionFrames.peek();
      xf.createNextLevel();
      if (xf.hasLevel()) {
        return;
      } else {
        extensionFrames.pop();
      }
    }
    // the extension stack is empty, so the initial frame remains
    initialFrame.createNextLevel();
  }

  /**
   * Create a plan when possible using the levels currently on the stack.
   *
   * @return a new plan when possible, null otherwise
   */
  private Plan createPlan() {
    // create a plan when the current level is enabled and the minimum depth is satisfied
    if (isEnabled() && getDepth() >= minDepth) {
      return new Plan(Graph.create(initialFrame.getLevel(), getExtensionLevels()));
    }
    return null;
  }

  /**
   * Grow the stack, when possible, by pushing an extension frame, thus considering plans with an additional graph
   * level.
   */
  private void grow() {
    if (canGrow()) {
      final ExtensionLevel xl = graph.getExtensionLevel(extensionFrames.size());
      extensionFrames.push(new ExtensionFrame(xl, getCurrentLevel().getRequiredActions()));
    }
  }

  private boolean canGrow() {
    return getDepth() < maxDepth // ensure we do not exceed the maximum depth
        && extensionFrames.size() < graph.getExtensionDepth() // ensure the graph has enough levels
        && !isEnabled() // grow only when the current level is not enabled by itself
        && isReachable() // ensure the current level is reachable, otherwise there would be no plans with more levels
        ;
  }

  private Frame getCurrentFrame() {
    return extensionFrames.isEmpty()
        ? initialFrame
        : extensionFrames.peek();
  }

  private Level getCurrentLevel() {
    return getCurrentFrame().getLevel();
  }

  /**
   * Check if the current level is enabled, i.e. all its actions have empty pre-conditions.
   *
   * @return true when enabled, false otherwise
   */
  private boolean isEnabled() {
    for (final Action a : getCurrentLevel().getRequiredActions()) {
      if (!a.getPreConditions().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if all actions in the current level are reachable, i.e. their pre-conditions can be satisfied via some later
   * extension level.
   * <p/>
   * This allows the pruning of graph levels which can never be satisfied within the current graph and thus avoids
   * unnecessary plan searches.
   *
   * @return true when reachable, false otherwise
   */
  private boolean isReachable() {
    // use the original graph level (the graph level from which the current level is derived) as it was used to built
    // the reachability index
    final Level l = getCurrentFrame().getOriginalLevel();
    for (final Action a : getCurrentLevel().getRequiredActions()) {
      if (!reachabilityIndex.isReachable(a, l)) {
        return false;
      }
    }
    return true;
  }

  private List<ExtensionLevel> getExtensionLevels() {
    // the number of extension levels is known, because there is one level per extension frame
    final ExtensionLevel[] xls = new ExtensionLevel[extensionFrames.size()];

    // populate the array in reverse as the stack is iterated from top to bottom
    int i = xls.length;
    for (final ExtensionFrame xf : extensionFrames) {
      xls[--i] = xf.getLevel();
    }

    return Arrays.asList(xls);
  }

  private static abstract class Frame<L extends Level, P> {

    private final ProductSetIterator<P> provisionCombinations;

    private final L originalLevel;

    private L level;

    public Frame(final L originalLevel, final ProductSet<P> provisionCombinations) {
      this.originalLevel = originalLevel;
      this.provisionCombinations = provisionCombinations.iterator();
    }

    protected abstract L createLevel(Set<P> provisions);

    public final boolean hasLevel() {
      return level != null;
    }

    /**
     * @return the graph level this frame corresponds to
     */
    public final L getOriginalLevel() {
      return originalLevel;
    }

    /**
     * @return the level created from the current provision combination
     */
    public final L getLevel() {
      return level;
    }

    /**
     * Create a level from the next provision combination.
     */
    public void createNextLevel() {
      if (provisionCombinations.hasNext()) {
        level = createLevel(provisionCombinations.next());
      } else {
        level = null;
      }
    }

  }

  private static class InitialFrame extends Frame<InitialLevel, TaskProvision> {

    public InitialFrame(final InitialLevel level) {
      super(level, createCombinations(level));
    }

    @Override
    protected InitialLevel createLevel(final Set<TaskProvision> provisions) {
      return new InitialLevel(provisions);
    }

    private static ProductSet<TaskProvision> createCombinations(final InitialLevel level) {
      final Set<Task> ts = level.getRequestedTasks();
      final Set<Set<TaskProvision>> tps = new HashSet<>();
      for (final Task t : ts) {
        tps.add(level.getTaskProvisionsByRequestedTask(t));
      }
      return new ProductSet<>(tps);
    }

  }

  private static class ExtensionFrame extends Frame<ExtensionLevel, ActionProvision> {

    /**
     * Create an extension frame from an extension level which may contain multiple provisions to satisfy the same
     * requested action. By using a set of actions required by the previous level we can filter out provisions
     * unnecessary for the current phase of the plan search.
     *
     * @param level   the original extension level
     * @param actions actions required by the previous level
     */
    public ExtensionFrame(final ExtensionLevel level, final Set<Action> actions) {
      super(level, createCombinations(level, actions));
    }

    @Override
    protected ExtensionLevel createLevel(final Set<ActionProvision> provisions) {
      return new ExtensionLevel(provisions);
    }

    private static ProductSet<ActionProvision> createCombinations(final ExtensionLevel level,
                                                                  final Set<Action> actions) {
      final Set<Set<ActionProvision>> apss = new HashSet<>();
      for (final Action a : actions) {
        final Set<ActionProvision> aps = level.getActionProvisionsByRequestedAction(a);
        if (!aps.isEmpty()) {
          apss.add(aps);
        }
      }
      return new ProductSet<>(apss);
    }

  }

}
