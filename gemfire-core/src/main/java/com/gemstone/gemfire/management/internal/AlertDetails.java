/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.management.internal;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import com.gemstone.gemfire.distributed.internal.membership.InternalDistributedMember;
import com.gemstone.gemfire.internal.admin.Alert;
import com.gemstone.gemfire.internal.logging.DateFormatter;
import com.gemstone.gemfire.internal.logging.LogWriterImpl;

public class AlertDetails {

  private int alertLevel;

  private String connectionName;
  private String threadName;
  private long tid;
  private String msg;
  private String exceptionText;
  private Date msgDate;
  private final String sourceId;
  private final String message;

  private InternalDistributedMember sender;

  public AlertDetails(int alertLevel, Date msgDate, String connectionName,
      String threadName, long tid, String msg, String exceptionText,
      InternalDistributedMember sender) {

    this.alertLevel = alertLevel;
    this.connectionName = connectionName;
    this.threadName = threadName;
    this.tid = tid;
    this.msg = msg;
    this.exceptionText = exceptionText;
    this.msgDate = msgDate;
    this.sender = sender;

    {
      StringBuffer tmpSourceId = new StringBuffer();

      tmpSourceId.append(threadName);
      if (tmpSourceId.length() > 0) {
        tmpSourceId.append(' ');
      }
      tmpSourceId.append("tid=0x");
      tmpSourceId.append(Long.toHexString(tid));
      this.sourceId = tmpSourceId.toString();
    }
    {
      StringBuffer tmpMessage = new StringBuffer();
      tmpMessage.append(msg);
      if (tmpMessage.length() > 0) {
        tmpMessage.append('\n');
      }
      tmpMessage.append(exceptionText);
      this.message = tmpMessage.toString();
    }
  }

  public int getAlertLevel() {
    return alertLevel;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public String getThreadName() {
    return threadName;
  }

  public long getTid() {
    return tid;
  }

  public String getMsg() {
    return msg;
  }

  public String getExceptionText() {
    return exceptionText;
  }

  public Date getMsgTime() {
    return msgDate;
  }
  
  public String getSource() {
    return sourceId;
  }

  /**
   * Returns the sender of this message. Note that this value is not set until
   * this message is received by a distribution manager.
   */
  public InternalDistributedMember getSender() {
    return this.sender;
  }

  public String toString() {
    final DateFormat timeFormatter = DateFormatter.createDateFormat();
    java.io.StringWriter sw = new java.io.StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.print('[');
    pw.print(LogWriterImpl.levelToString(alertLevel));
    pw.print(' ');
    pw.print(timeFormatter.format(msgDate));
    pw.print(' ');
    pw.print(connectionName);
    pw.print(' ');
    pw.print(sourceId);
    pw.print("] ");
    pw.print(message);

    pw.close();
    try {
      sw.close();
    } catch (java.io.IOException ignore) {
    }
    return sw.toString();
  }
  
  /**
   * Converts the int alert level to a string representation.
   * 
   * @param intLevel
   *          int alert level to convert
   * @return A string representation of the alert level
   */
  public static final String getAlertLevelAsString(final int intLevel) {
    if (intLevel == Alert.SEVERE) {
      return "severe";
    } else if (intLevel == Alert.ERROR) {
      return "error";
    } else if (intLevel == Alert.WARNING) {
      return "warning";
    } else if (intLevel == Alert.OFF) {
      return "none";
    }

    throw new IllegalArgumentException("Unable to find an alert level with int value: " + intLevel);
  }
}
