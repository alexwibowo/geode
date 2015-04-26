/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */

package com.gemstone.gemfire.cache.util;

/**
 * Utility class that implements all methods in 
 * <code>BridgeMembershipListener</code> with empty implementations.
 * Applications can subclass this class and only override the methods for
 * the events of interest.
 *
 * @author Kirk Lund
 * @since 4.2.1
 * @deprecated see com.gemstone.gemfire.management.membership.ClientMembershipListenerAdapter
 */
public abstract class BridgeMembershipListenerAdapter 
implements BridgeMembershipListener {
    
  /**
   * Invoked when a client has connected to this process or when this
   * process has connected to a BridgeServer.
   */
  public void memberJoined(BridgeMembershipEvent event) {}

  /**
   * Invoked when a client has gracefully disconnected from this process
   * or when this process has gracefully disconnected from a BridgeServer.
   */
  public void memberLeft(BridgeMembershipEvent event) {}

  /**
   * Invoked when a client has unexpectedly disconnected from this process
   * or when this process has unexpectedly disconnected from a BridgeServer.
   */
   public void memberCrashed(BridgeMembershipEvent event) {}
  
}

