/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.internal.admin.remote;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gemstone.gemfire.CancelException;
import com.gemstone.gemfire.cache.persistence.PersistentID;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.distributed.internal.DM;
import com.gemstone.gemfire.distributed.internal.DistributionManager;
import com.gemstone.gemfire.distributed.internal.DistributionMessage;
import com.gemstone.gemfire.distributed.internal.ReplyException;
import com.gemstone.gemfire.internal.cache.DiskStoreImpl;
import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;
import com.gemstone.gemfire.internal.util.ArrayUtils;

/**
 * An instruction to all members with cache that they should 
 * compact their disk stores.
 * @author dsmith
 *
 */
public class CompactRequest extends CliLegacyMessage {
  public CompactRequest() {
  }
  
  public static Map<DistributedMember, Set<PersistentID>> send(DM dm) {
    Set recipients = dm.getOtherDistributionManagerIds();
    CompactRequest request = new CompactRequest();
    request.setRecipients(recipients);
    
    CompactReplyProcessor replyProcessor = new CompactReplyProcessor(dm, recipients);
    request.msgId = replyProcessor.getProcessorId();
    dm.putOutgoing(request);
    
    request.setSender(dm.getDistributionManagerId());
    request.process((DistributionManager)dm);
    
    try {
      replyProcessor.waitForReplies();
    } catch (ReplyException e) {
      if(!(e.getCause() instanceof CancelException)) {
        throw e;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    return replyProcessor.results;
  }
  
  @Override
  protected void process(DistributionManager dm) {
    super.process(dm);
  }

  @Override
  protected AdminResponse createResponse(DistributionManager dm) {
    GemFireCacheImpl cache = GemFireCacheImpl.getInstance();
    HashSet<PersistentID> compactedStores = new HashSet<PersistentID>();
    if(cache != null && !cache.isClosed()) {
      for(DiskStoreImpl store : cache.listDiskStoresIncludingRegionOwned()) {
        if(store.forceCompaction()) {
          compactedStores.add(store.getPersistentID());
        }
      }
    }

    return new CompactResponse(this.getSender(), compactedStores);
  }

  public int getDSFID() {
    return COMPACT_REQUEST;
  }
  
  @Override
  public void fromData(DataInput in) throws IOException,ClassNotFoundException {
    super.fromData(in);
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    super.toData(out);
  }

  @Override  
  public String toString() {
    return "Compact request sent to " + ArrayUtils.toString((Object[])this.getRecipients()) +
      " from " + this.getSender();
  }

  private static class CompactReplyProcessor extends AdminMultipleReplyProcessor {
    Map<DistributedMember, Set<PersistentID>> results = Collections.synchronizedMap(new HashMap<DistributedMember, Set<PersistentID>>());
    
    public CompactReplyProcessor(DM dm, Collection initMembers) {
      super(dm, initMembers);
    }
    
    @Override
    protected boolean stopBecauseOfExceptions() {
      return false;
    }

    @Override
    protected boolean allowReplyFromSender() {
      return true;
    }

    @Override
    protected void process(DistributionMessage msg, boolean warn) {
      if(msg instanceof CompactResponse) {
        final HashSet<PersistentID> persistentIds = ((CompactResponse) msg).getPersistentIds();
        if(persistentIds != null && !persistentIds.isEmpty()) {
          results.put(msg.getSender(), persistentIds);
        }
      }
      super.process(msg, warn);
    }
    
    
  }
}
