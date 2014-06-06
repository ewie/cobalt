/*
 * Copyright (c) 2014, Erik Wienhold
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License.
 */

package vsr.cobalt.planner;

/**
 * @author Erik Wienhold
 */
public class PlanningException extends Exception {

  public PlanningException() {
  }

  public PlanningException(final String message) {
    super(message);
  }

  public PlanningException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PlanningException(final Throwable cause) {
    super(cause);
  }

  public PlanningException(final String message, final Throwable cause, final boolean enableSuppression,
                           final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
