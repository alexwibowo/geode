/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.management.internal.configuration.functions;

import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.distributed.internal.InternalLocator;
import com.gemstone.gemfire.distributed.internal.SharedConfiguration;
import com.gemstone.gemfire.internal.InternalEntity;
import com.gemstone.gemfire.management.internal.cli.CliUtil;
import com.gemstone.gemfire.management.internal.configuration.domain.ConfigurationChangeResult;

public class DeleteJarFunction extends FunctionAdapter implements
InternalEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DeleteJarFunction() {
  }

  @Override
  public void execute(FunctionContext context) {

    InternalLocator locator = InternalLocator.getLocator();
    ConfigurationChangeResult configChangeResult = new ConfigurationChangeResult();

    try {
      if (locator.isSharedConfigurationRunning()) {
        final Object[] args = (Object[]) context.getArguments();
        final String[] jarFilenames = (String[]) args[0];
        final String[] groups = (String[])args[1];

        SharedConfiguration sharedConfiguration = locator.getSharedConfiguration();
        sharedConfiguration.removeJars(jarFilenames, groups);
      } else {
        configChangeResult.setErrorMessage("Shared Configuration has not been started in locator : " + locator);
      }
    } catch (Exception e) {
      configChangeResult.setException(e);
      configChangeResult.setErrorMessage(CliUtil.stackTraceAsString(e));
    } finally {
      context.getResultSender().lastResult(configChangeResult);
    }
  }

  @Override
  public String getId() {
    return DeleteJarFunction.class.getName();
  }

}
