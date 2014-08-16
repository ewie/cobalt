/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.models;

import java.util.Set;

/**
 * A repository serves as the knowledge base for the planning algorithm.
 *
 * @author Erik Wienhold
 */
public interface Repository {

  /**
   * Get all actions which belong to a given widget.
   *
   * @param widget a widget
   *
   * @return a set of all actions belonging to the given widget
   */
  Set<Action> getWidgetActions(Widget widget);

  /**
   * Get realized functionalities compatible to a requested functionality.
   *
   * @param request the requested functionality
   *
   * @return a set of zero or more realized functionalities
   */
  Set<RealizedFunctionality> findCompatibleOffers(Functionality request);

  /**
   * Get published properties compatible to a requested property.
   *
   * @param request the requested property
   *
   * @return a set of zero or more published properties
   */
  Set<PublishedProperty> findCompatibleOffers(Property request);

  /**
   * Get the distance between two functionalities.
   * <p/>
   * The distance is measured as the number of inheritance steps from the offered functionality O up to the requested
   * functionality R.
   * For identical functionalities the distance is 0. When O subsumes R the distance is undefined.
   *
   * @param request a requested functionality
   * @param offer   an offered functionality
   *
   * @return the distance between the functionalities, -1 when not compatible
   */
  int getDistance(Functionality request, Functionality offer);

  /**
   * Get the distance between two types.
   * <p/>
   * The distance is measured as the number of inheritance steps from the offered type O up to the requested type R.
   * For identical types the distance is 0. When O subsumes R the distance is undefined.
   *
   * @param request a requested type
   * @param offer   an offered type
   *
   * @return the distance between the types, -1 when not compatible
   */
  int getDistance(Type request, Type offer);

}
