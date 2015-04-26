/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
/*
 * IndexNameConflictException.java
 *
 * Created on February 15, 2005, 10:20 AM
 */

package com.gemstone.gemfire.cache.query;

/**
 * Thrown while creating the new index if there exists an Index with
 * the same definition as new index.
 *
 * @author vaibhav
 * @since 4.0
 */

public class IndexExistsException extends QueryException{
private static final long serialVersionUID = -168312863985932144L;
  
  /**
   * Constructs instance of IndexNameConflictException with error message
   * @param msg the error message
   */
  public IndexExistsException(String msg) {
    super(msg);
  }
  
  /**
   * Constructs instance of IndexNameConflictException with error message and cause
   * @param msg the error message
   * @param cause a Throwable that is a cause of this exception
   */
  public IndexExistsException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
