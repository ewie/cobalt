/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import vsr.cobalt.models.Action;
import vsr.cobalt.models.Property;
import vsr.cobalt.models.Proposition;
import vsr.cobalt.models.PropositionSet;

/**
 * Identifies mutually exclusive actions within a graph.
 * <p/>
 * The information about mutex relations is kept separate from the actual graph structure as it isn't essential to the
 * graph construction but mainly only applies to plan extraction.
 *
 * @author Erik Wienhold
 */
public class ActionMutexIndex {

  private final Map<Level, MutexSet<Action>> index;

  public ActionMutexIndex(final Graph graph) {
    index = buildIndex(graph);
  }

  /**
   * Check if any level contains mutex actions.
   *
   * @return true when any level contains mutually exclusive actions, false otherwise
   */
  public boolean hasAnyMutexes() {
    for (final Level l : index.keySet()) {
      if (hasMutexActions(l)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if any actions in a given level are mutex.
   *
   * @param level the level to check
   *
   * @return true when the level contains mutually exclusive actions, false otherwise
   */
  public boolean hasMutexActions(final Level level) {
    final MutexSet<Action> mutexes = index.get(level);
    return mutexes != null && !mutexes.isEmpty();
  }

  /**
   * Check if two actions are mutual exclusive in a given level.
   *
   * @param action1 an action to check
   * @param action2 an action to check
   * @param level   a level containing the given actions
   *
   * @return true when mutually exclusive, false otherwise
   */
  public boolean isMutex(final Level level, final Action action1, final Action action2) {
    final MutexSet<Action> mutexes = index.get(level);
    return mutexes != null
        && (mutexes.contains(action1, action2)
        || mutexes.contains(action2, action1));
  }

  private static Map<Level, MutexSet<Action>> buildIndex(final Graph graph) {
    final Map<Level, MutexSet<Action>> index = new HashMap<>();

    MutexSet<Proposition> preMutexes = new MutexSet<>();

    // Iterate over all levels in reverse, i.e. start with the very last extension level.
    for (final Level level : graph.getLevelsReversed()) {
      final Set<Action> actions = level.getRequiredActions();
      final SetMultimap<Proposition, Action> propositionIndex = indexActionsByPostConditions(actions);

      // Determine mutex actions given a set of mutexes among the pre-conditions.
      final MutexSet<Action> actionMutexes = determineMutexActions(actions, preMutexes);

      // Propagate mutexes to the current level's post-conditions (the pre-conditions of the next level).
      preMutexes = propagateMutexes(actionMutexes, propositionIndex);

      index.put(level, actionMutexes);
    }

    return index;
  }

  private static MutexSet<Proposition> propagateMutexes(final MutexSet<Action> actionMutexes,
                                                        final SetMultimap<Proposition, Action> post2action) {
    final MutexSet<Proposition> preMutexes = new MutexSet<>();

    for (final Proposition p : post2action.keySet()) {
      for (final Proposition q : post2action.keySet()) {
        // a proposition cannot be mutex with itself,
        // it's sufficient to check object identity, as both objects come from the same set
        if (p == q) {
          continue;
        }

        // make propositions mutex when all pairs of achieving actions are mutex
        if (!canAchievePostConditions(p, q, post2action, actionMutexes)) {
          preMutexes.add(p, q);
        }
      }
    }

    return preMutexes;
  }

  private static boolean canAchievePostConditions(final Proposition p, final Proposition q,
                                                  final SetMultimap<Proposition, Action> post2action,
                                                  final MutexSet<Action> actionMutexes) {
    final Set<Action> pas = post2action.get(p);
    final Set<Action> qas = post2action.get(q);
    for (final Action ai : pas) {
      for (final Action aj : qas) {
        if (!actionMutexes.contains(ai, aj)) {
          return true;
        }
      }
    }
    return false;
  }

  private static MutexSet<Action> determineMutexActions(final Set<Action> actions,
                                                        final MutexSet<Proposition> preMutexes) {
    final SetMultimap<Action, Action> mutexes = HashMultimap.create();
    for (final Action ai : actions) {
      for (final Action aj : actions) {
        // an action cannot be mutex with itself,
        // it's sufficient to check object identity, as both objects come from the same set
        if (ai == aj) {
          continue;
        }

        if (isMashupMutex(ai, aj) || haveCompetingNeeds(ai, aj, preMutexes)) {
          mutexes.put(ai, aj);
        }
      }
    }
    return new MutexSet<>(mutexes);
  }

  private static SetMultimap<Proposition, Action> indexActionsByPostConditions(final Set<Action> actions) {
    final SetMultimap<Proposition, Action> index = HashMultimap.create();
    for (final Action a : actions) {
      for (final Proposition p : a.getPostConditions()) {
        index.put(p, a);
      }
    }
    return index;
  }

  private static boolean haveCompetingNeeds(final Action x, final Action y,
                                            final MutexSet<Proposition> preMutexes) {
    for (final Proposition p : x.getPreConditions()) {
      for (final Proposition q : y.getPreConditions()) {
        if (preMutexes.contains(p, q)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isMashupMutex(final Action x, final Action y) {
    final PropositionSet pre = y.getPreConditions();
    for (final Property p : x.getPublishedProperties()) {
      if (pre.isCleared(p)) {
        return true;
      }
    }
    return false;
  }

  private static class MutexSet<T> {

    private final SetMultimap<T, T> mutexes;

    public MutexSet(final SetMultimap<T, T> mutexes) {
      this.mutexes = mutexes;
    }

    public MutexSet() {
      this(HashMultimap.<T, T>create());
    }

    public boolean isEmpty() {
      return mutexes.isEmpty();
    }

    public void add(final T x, final T y) {
      mutexes.put(x, y);
    }

    public boolean contains(final T x, final T y) {
      return mutexes.containsEntry(x, y);
    }

  }

}
