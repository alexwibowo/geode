package com.gemstone.gemfire.management.internal;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.gemstone.gemfire.internal.lang.StringUtils;
import com.gemstone.gemfire.internal.logging.LogService;

/**
 * Hosts common utility methods needed by the management package
 * @author wwilliams
 * @since Geode 1.0.0.0
 *
 */
public class AgentUtil {

	  private static final Logger logger = LogService.getLogger();
	  
	  private String gemfireVersion = null;

	  public AgentUtil(String gemfireVersion) {
		  this.gemfireVersion = gemfireVersion;
	  }
	  
	  String getGemFireWebApiWarLocation() {
		String gemfireHome = getGemFireHome();
	    assert !StringUtils.isBlank(gemfireHome) : "The GEMFIRE environment variable must be set!";
	    logger.warn(gemfireHome + "/tools/Extensions/gemfire-web-api-" + gemfireVersion + ".war");
	    if (new File(gemfireHome + "/tools/Extensions/gemfire-web-api-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/tools/Extensions/gemfire-web-api-" + gemfireVersion + ".war";
	    }
	     else if (new File(gemfireHome + "/lib/gemfire-web-api-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/lib/gemfire-web-api-" + gemfireVersion + ".war";
	    }
	    else {
	      return null;
	    }
	  }
	  

	  /*
	   * Use the GEMFIRE environment variable to find the GemFire product tree.
	   * First, look in the $GEMFIRE/tools/Management directory
	   * Second, look in the $GEMFIRE/lib directory
	   * Finally, if we cannot find Management WAR file then return null...
	   */
	  String getGemFireWebWarLocation() {
		String gemfireHome = getGemFireHome();
	    assert !StringUtils.isBlank(gemfireHome) : "The GEMFIRE environment variable must be set!";

	    if (new File(gemfireHome + "/tools/Extensions/gemfire-web-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/tools/Extensions/gemfire-web-" + gemfireVersion + ".war";
	    }
	    else if (new File(gemfireHome + "/lib/gemfire-web-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/lib/gemfire-web-" + gemfireVersion + ".war";
	    }
	    else {
	      return null;
	    }
	  }

	  String getGemfireApiWarLocation() {
		String gemfireHome = getGemFireHome();
	    assert !StringUtils.isBlank(gemfireHome) : "The GEMFIRE environment variable must be set!";
	    if (new File(gemfireHome + "/tools/Extensions/gemfire-api-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/tools/Extensions/gemfire-api-" + gemfireVersion + ".war";
	    }
	    else if (new File(gemfireHome + "/lib/gemfire-api-" + gemfireVersion + ".war").isFile()) {
	      return gemfireHome + "/lib/gemfire-api-" + gemfireVersion + ".war";
	    }
	    else {
	      return null;
	    }
	  }

	  // Use the GEMFIRE environment variable to find the GemFire product tree.
	  // First, look in the $GEMFIRE/tools/Pulse directory
	  // Second, look in the $GEMFIRE/lib directory
	  // Finally, if we cannot find the Management WAR file then return null...
	  String getPulseWarLocation() {
		String gemfireHome = getGemFireHome();
	    assert !StringUtils.isBlank(gemfireHome) : "The GEMFIRE environment variable must be set!";

	    if (new File(gemfireHome + "/tools/Pulse/pulse.war").isFile()) {
	      return gemfireHome + "/tools/Pulse/pulse.war";
	    }
	    else if (new File(gemfireHome + "/lib/pulse.war").isFile()) {
	      return gemfireHome + "/lib/pulse.war";
	    }
	    else {
	      return null;
	    }
	  }


	  boolean isWebApplicationAvailable(final String warFileLocation) {
	    return !StringUtils.isBlank(warFileLocation);
	  }

	  boolean isWebApplicationAvailable(final String... warFileLocations) {
	    for (String warFileLocation : warFileLocations) {
	      if (isWebApplicationAvailable(warFileLocation)) {
	        return true;
	      }
	    }

	    return false;
	  }

	  String getGemFireHome() {

	    String gemFireHome = System.getenv("GEMFIRE");
	    
	    // Check for empty variable. if empty, then log message and exit HTTP server startup
	    if (StringUtils.isBlank(gemFireHome)) {
	      gemFireHome = System.getProperty("gemfire.home");
	      logger.info("Reading gemfire.home System Property -> {}", gemFireHome);
	      if (StringUtils.isBlank(gemFireHome)) {
	        logger.info("GEMFIRE environment variable not set; HTTP service will not start.");
	        gemFireHome = null;
	      }
	    }
	    
	    return gemFireHome;
	  }
	  
	  boolean isGemfireHomeDefined() {
		String gemfireHome = getGemFireHome();
		return !StringUtils.isBlank(gemfireHome);
	  }
}