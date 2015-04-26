/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *========================================================================
 */
   
package com.gemstone.gemfire.admin;

/**
 * An <code>CacheDoesNotExistException</code> is thrown when an attempt
 * is made to get a cache and one does not exist.
 *
 * @author    Darrel Schneider
 * @since     3.5
 * @deprecated as of 7.0 use the {@link com.gemstone.gemfire.management} package instead
 */
public class CacheDoesNotExistException extends AdminException {
private static final long serialVersionUID = -1639933911265729978L;

  /**
   * Constructs a new exception with <code>null</code> as its detail message.
   * The cause is not initialized, and may subsequently be initialized by a
   * call to {@link Throwable#initCause}.
   */
  public CacheDoesNotExistException() {
    super();
  }

  /**
   * Constructs a new exception with the specified detail message.  The
   * cause is not initialized, and may subsequently be initialized by
   * a call to {@link Throwable#initCause}.
   *
   * @param   message   the detail message. The detail message is saved for 
   *          later retrieval by the {@link #getMessage()} method.
   */
  public CacheDoesNotExistException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and
   * cause.  <p>Note that the detail message associated with
   * <code>cause</code> is <i>not</i> automatically incorporated in
   * this exception's detail message.
   *
   * @param  message the detail message (which is saved for later retrieval
   *         by the {@link #getMessage()} method).
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link #getCause()} method).  (A <tt>null</tt> value is
   *         permitted, and indicates that the cause is nonexistent or
   *         unknown.)
   */
  public CacheDoesNotExistException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified cause and a detail
   * message of <tt>(cause==null ? null : cause.toString())</tt> (which
   * typically contains the class and detail message of <tt>cause</tt>).
   * This constructor is useful for exceptions that are little more than
   * wrappers for other throwables (for example, {@link
   * java.security.PrivilegedActionException}).
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link #getCause()} method).  (A <tt>null</tt> value is
   *         permitted, and indicates that the cause is nonexistent or
   *         unknown.)
   */
  public CacheDoesNotExistException(Throwable cause) {
    super(cause);
  }
    
}

