/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.internal.cache.partitioned;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.distributed.internal.DistributionManager;
import com.gemstone.gemfire.distributed.internal.ReplyProcessor21;
import com.gemstone.gemfire.distributed.internal.membership.InternalDistributedMember;
import com.gemstone.gemfire.internal.Assert;
import com.gemstone.gemfire.internal.InternalDataSerializer;
import com.gemstone.gemfire.internal.cache.ForceReattemptException;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;

/**
 * This message is sent to a member to make it attempt to become
 * primary. This message is sent at the end of PRHARedundancyProvider
 * .createBucketAtomically, because the buckets created during that
 * time do not volunteer for primary until receiving this message.
 * 
 * @author Dan Smith
 */
public class EndBucketCreationMessage extends PartitionMessage {

  private int bucketId;
  private InternalDistributedMember newPrimary;
  
  /**
   * Empty constructor to satisfy {@link DataSerializer} requirements
   */
  public EndBucketCreationMessage() {
  }

  private EndBucketCreationMessage(Collection<InternalDistributedMember> recipients, 
                                     int regionId, 
                                     ReplyProcessor21 processor,
                                     int bucketId,
                                     InternalDistributedMember newPrimary) {
    super(recipients, regionId, processor);
    this.bucketId = bucketId;
    this.newPrimary = newPrimary;
  }

  /**
   * Sends a message to make the recipient primary for the bucket.
   * @param acceptedMembers 
   * 
   * @param newPrimary the member to to become primary
   * @param pr the PartitionedRegion of the bucket
   * @param bid the bucket to become primary for
   */
  public static void send(Collection<InternalDistributedMember> acceptedMembers, 
      InternalDistributedMember newPrimary, 
      PartitionedRegion pr, 
      int bid)  {
    
    Assert.assertTrue(newPrimary != null, 
        "VolunteerPrimaryBucketMessage NULL recipient");
    
    ReplyProcessor21 response = new ReplyProcessor21(
        pr.getSystem(), acceptedMembers);
    EndBucketCreationMessage msg = new EndBucketCreationMessage(
        acceptedMembers, pr.getPRId(), response, bid, newPrimary);

    pr.getDistributionManager().putOutgoing(msg);
  }

  public EndBucketCreationMessage(DataInput in) 
  throws IOException, ClassNotFoundException {
    fromData(in);
  }

  @Override
  public int getProcessorType() {
    // use the waiting pool because operateOnPartitionedRegion will 
    // try to get a dlock
    return DistributionManager.WAITING_POOL_EXECUTOR;
  }
  
  @Override
  public boolean isSevereAlertCompatible() {
    // allow forced-disconnect processing for all cache op messages
    return true;
  }

  @Override
  protected final boolean operateOnPartitionedRegion(DistributionManager dm,
                                                     PartitionedRegion region, 
                                                     long startTime) 
                                              throws ForceReattemptException {
    
    // this is executing in the WAITING_POOL_EXECUTOR
    
    try {
      region.getRedundancyProvider().endBucketCreationLocally(bucketId, newPrimary);

    } finally {
      region.getPrStats().endPartitionMessagesProcessing(startTime);
    }
    
    return false;
    
  }

  @Override
  protected void appendFields(StringBuffer buff) {
    super.appendFields(buff);
    buff.append("; bucketId=").append(this.bucketId);
    buff.append("; newPrimary=").append(this.newPrimary);
  }

  public int getDSFID() {
    return END_BUCKET_CREATION_MESSAGE;
  }

  @Override
  public void fromData(DataInput in) 
  throws IOException, ClassNotFoundException {
    super.fromData(in);
    this.bucketId = in.readInt();
    newPrimary = new InternalDistributedMember();
    InternalDataSerializer.invokeFromData(newPrimary, in);
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    super.toData(out);
    out.writeInt(this.bucketId);
    InternalDataSerializer.invokeToData(newPrimary, out);
  }
}
