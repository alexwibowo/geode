/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
/**
 * File comment
 */
package com.gemstone.gemfire.internal.cache;

import java.util.Set;

import com.gemstone.gemfire.cache.EntryNotFoundException;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.Region.Entry;
import com.gemstone.gemfire.internal.cache.tier.sockets.ClientProxyMembershipID;


/**
 * @author mthomas
 * @since 6.0tx
 */
public class PartitionedRegionDataView extends LocalRegionDataView {

  
  @Override
  public void updateEntryVersion(EntryEventImpl event)
      throws EntryNotFoundException {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    pr.updateEntryVersionInBucket(event);
  }

  @Override
  public void invalidateExistingEntry(EntryEventImpl event,
      boolean invokeCallbacks, boolean forceNewEntry) {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    pr.invalidateInBucket(event);
  }

  @Override
  public void destroyExistingEntry(EntryEventImpl event, boolean cacheWrite, Object expectedOldValue) {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    pr.destroyInBucket(event, expectedOldValue);
  }

  @Override
  public Entry getEntry(KeyInfo keyInfo, LocalRegion localRegion, boolean allowTombstones) {
    TXStateProxy tx = localRegion.cache.getTXMgr().internalSuspend();
    try {
      PartitionedRegion pr = (PartitionedRegion)localRegion;
      return pr.nonTXGetEntry(keyInfo, false, allowTombstones);
    } finally {
      localRegion.cache.getTXMgr().resume(tx);
    }
  }

  @Override
  public Object findObject(KeyInfo key, LocalRegion r, boolean isCreate,
      boolean generateCallbacks, Object value, boolean disableCopyOnRead,
      boolean preferCD, ClientProxyMembershipID requestingClient, EntryEventImpl clientEvent, boolean returnTombstones) {
    TXStateProxy tx = r.cache.getTXMgr().internalSuspend();
    try {
      return r.findObjectInSystem(key, isCreate, tx, generateCallbacks, value, disableCopyOnRead, preferCD, requestingClient, clientEvent, returnTombstones);
    } finally {
      r.cache.getTXMgr().resume(tx);
    }
  }

  @Override
  public boolean containsKey(KeyInfo keyInfo, LocalRegion localRegion) {
    PartitionedRegion pr = (PartitionedRegion)localRegion;
    return pr.nonTXContainsKey(keyInfo);
  }
  @Override
  public Object getSerializedValue(LocalRegion localRegion, KeyInfo keyInfo, boolean doNotLockEntry, ClientProxyMembershipID requestingClient,
  EntryEventImpl clientEvent, boolean returnTombstones) throws DataLocationException {
    PartitionedRegion pr = (PartitionedRegion)localRegion;
    return pr.getDataStore().getSerializedLocally(keyInfo, doNotLockEntry, clientEvent, returnTombstones);
  }
  @Override
  public boolean putEntryOnRemote(EntryEventImpl event, boolean ifNew,
      boolean ifOld, Object expectedOldValue, boolean requireOldValue,
      long lastModified, boolean overwriteDestroyed)
      throws DataLocationException {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    return pr.getDataStore().putLocally(event.getKeyInfo().getBucketId(), event, ifNew, ifOld, expectedOldValue, requireOldValue, lastModified);
  }
  @Override
  public void destroyOnRemote(EntryEventImpl event, boolean cacheWrite,
      Object expectedOldValue) throws DataLocationException {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    pr.getDataStore().destroyLocally(event.getKeyInfo().getBucketId(), event, expectedOldValue);
    return;
  }
  @Override
  public void invalidateOnRemote(EntryEventImpl event, boolean invokeCallbacks,
      boolean forceNewEntry) throws DataLocationException {
    PartitionedRegion pr = (PartitionedRegion)event.getLocalRegion();
    pr.getDataStore().invalidateLocally(event.getKeyInfo().getBucketId(), event);
  }
  @Override
  public Set getBucketKeys(LocalRegion localRegion, int bucketId, boolean allowTombstones) {
    PartitionedRegion pr = (PartitionedRegion)localRegion;
    return pr.getBucketKeys(bucketId, allowTombstones);
  }
  @Override
  public Entry getEntryOnRemote(KeyInfo keyInfo, LocalRegion localRegion,
      boolean allowTombstones) throws DataLocationException {
    PartitionedRegion pr = (PartitionedRegion)localRegion;
    return pr.getDataStore().getEntryLocally(keyInfo.getBucketId(),
        keyInfo.getKey(), false, allowTombstones);
  }

  @Override
  public Object getKeyForIterator(KeyInfo curr, LocalRegion currRgn,
      boolean rememberReads, boolean allowTombstones) {
    // do not perform a value check here, it will send out an
    // extra message. Also BucketRegion will check to see if
    // the value for this key is a removed token
    return curr.getKey();
  }

  /**
   * @see InternalDataView#getEntryForIterator(KeyInfo, LocalRegion, boolean, boolean)
   */
  @Override
  public Region.Entry<?, ?> getEntryForIterator(final KeyInfo keyInfo,
      final LocalRegion currRgn, boolean rememberRead, boolean allowTombstones) {
    return currRgn.nonTXGetEntry(keyInfo, false, allowTombstones);
  }
}
