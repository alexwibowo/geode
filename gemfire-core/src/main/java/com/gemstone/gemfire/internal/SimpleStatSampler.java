/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *========================================================================
 */
package com.gemstone.gemfire.internal;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gemstone.gemfire.CancelCriterion;
import com.gemstone.gemfire.internal.i18n.LocalizedStrings;
import com.gemstone.gemfire.internal.logging.LogService;
import com.gemstone.gemfire.internal.logging.log4j.LocalizedMessage;
import com.gemstone.gemfire.internal.logging.log4j.LogMarker;

/**
 * SimpleStatSampler is a functional implementation of HostStatSampler
 * that samples statistics stored in local java memory and does not
 * require any native code or additional GemFire features.
 * <p>
 * The StatisticsManager may be implemented by LocalStatisticsFactory and does
 * not require a GemFire connection.

 * @author Darrel Schneider
 * @author Kirk Lund
 */
public class SimpleStatSampler extends HostStatSampler  {

  private static final Logger logger = LogService.getLogger();
  
  public static final String ARCHIVE_FILE_NAME_PROPERTY = "stats.archive-file";
  public static final String FILE_SIZE_LIMIT_PROPERTY = "stats.file-size-limit";
  public static final String DISK_SPACE_LIMIT_PROPERTY = "stats.disk-space-limit";
  public static final String SAMPLE_RATE_PROPERTY = "stats.sample-rate";
  
  public static final String DEFAULT_ARCHIVE_FILE_NAME = "stats.gfs";
  public static final long DEFAULT_FILE_SIZE_LIMIT = 0;
  public static final long DEFAULT_DISK_SPACE_LIMIT = 0;
  public static final int DEFAULT_SAMPLE_RATE = 1000;
  
  private final File archiveFileName = new File(System.getProperty(ARCHIVE_FILE_NAME_PROPERTY, DEFAULT_ARCHIVE_FILE_NAME));
  private final long archiveFileSizeLimit = Long.getLong(FILE_SIZE_LIMIT_PROPERTY, DEFAULT_FILE_SIZE_LIMIT).longValue() * (1024*1024);
  private final long archiveDiskSpaceLimit = Long.getLong(DISK_SPACE_LIMIT_PROPERTY, DEFAULT_DISK_SPACE_LIMIT).longValue() * (1024*1024);
  private final int sampleRate = Integer.getInteger(SAMPLE_RATE_PROPERTY, DEFAULT_SAMPLE_RATE).intValue();

  private final StatisticsManager sm;

  public SimpleStatSampler(CancelCriterion stopper, StatisticsManager sm) {
    super(stopper, new StatSamplerStats(sm, sm.getId()));
    this.sm = sm;
    logger.info(LogMarker.STATISTICS, LocalizedMessage.create(LocalizedStrings.SimpleStatSampler_STATSSAMPLERATE_0, getSampleRate()));
  }

  @Override
  protected void checkListeners() {
    // do nothing
  }
  
  @Override
  public File getArchiveFileName() {
    return this.archiveFileName;
  }
  
  @Override
  public long getArchiveFileSizeLimit() {
    if (fileSizeLimitInKB()) {
      return this.archiveFileSizeLimit / 1024;
    } else {
      return this.archiveFileSizeLimit;
    }
  }
  
  @Override
  public long getArchiveDiskSpaceLimit() {
    if (fileSizeLimitInKB()) {
      return this.archiveDiskSpaceLimit / 1024;
    } else {
      return this.archiveDiskSpaceLimit;
    }
  }
  
  @Override
  public String getProductDescription() {
    return "Unknown product";
  }

  @Override
  protected StatisticsManager getStatisticsManager() {
    return this.sm;
  }
  
  @Override
  protected int getSampleRate() {
    return this.sampleRate;
  }
  
  @Override
  public boolean isSamplingEnabled() {
    return true;
  }
}
