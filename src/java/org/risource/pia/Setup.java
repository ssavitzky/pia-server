// Setup.java
// $Id: Setup.java,v 1.7 1999-09-22 00:28:57 steve Exp $

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


/**
 * Setup is used to isolate the PIA's setup process: reading properties and
 *	environment variables, checking for the existance of the various
 *	directories, and adding the things we need to the system properties.
 *
 * <p>	There is some hope that a sufficiently-clever runtime could
 *	unload the code after it is needed.  Even if that proves
 *	impossible, the only class that depends on Setup is Pia, so
 *	changes in the setup code will not cause massive recompilation.
 *	In contrast, almost <em>everything</em> depends on Pia.
 */
package org.risource.pia;

import java.io.File;

import java.util.Properties;

import org.risource.pia.Pia;
import org.risource.pia.Configuration; 

import org.risource.ds.List;

class Setup extends Configuration {

  /** Reference to the Pia instance. */
  protected Pia pia;

  /** PIA environment table: */
  protected String[] piaEnvTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
    "PIA_DIR",	"pia.piaroot",
    "USR_DIR",	"pia.usrroot",
    "PIA_PORT",	"pia.port",
    "REAL_PORT", "pia.realport",
  };

  /** PIA option table: */
  protected String[] piaOptTable = {
    "-version", "pia.print-version",  "bool", 	null,
    "-d",	"pia.debug",	"bool",		null,
    "-v",	"pia.verbose",	"bool",		null,
    "-u",	"pia.usrroot",	"dir",		null,
    "-p",	"pia.port",	"number",	"8888",
    "-port",	"pia.port",	"number",	"8888",
    "-real",	"pia.realport", "number",	"8888",
    "-root",	"pia.piaroot",	"dir",		null,
    "-profile",	"pia.profile",	"file",		null,
    "-host",	"pia.host", 	"name",		null,
    "-config",	"pia.cfgfile",	"file",		"_subsite.xcf",
    "-site",	"pia.sitecfg",	"file",		null,
  };

  /* options from old version: ============================================
	-l logfile
	-c command
	-v		verbose
	-q		quiet
	-d[N]		debugging
	-e		print out setenv commands for proxying
	-f		proxy ftp as well (optional because flaky)
	-x		exit after printing info, starting command (if any)
  */


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** 
   * Construct a Setup object and initialize the tables.
   */
  public Setup() {
    super();

    pia = Pia.instance();
    properties = pia.properties();

    envTable = piaEnvTable;
    optTable = piaOptTable;
  }
  
  /************************************************************************
  ** Configuration:
  ************************************************************************/

  /**
   * Perform special configuration for the PIA
   */
  public boolean configure(String[] commandLineArgs) {
    List path;

    /* Call super to parse the command line and system props. */
    boolean results = super.configure(commandLineArgs);

    /* Merge properties from the profile, if specified. */

    String profile = properties.getProperty("pia.profile");
    if (profile != null) {
      if (mergeProperties(profile)) {
	System.err.println("Loaded properties from "+profile);
      } else {
	System.err.println("Warning! Profile (properties) file " + profile
			   + " does not exist. \nProceeding anyway.");
      }
    }

    /* Make sure we have a PIA directory */

    String piaRoot = properties.getProperty("pia.piaroot");
    
    if (piaRoot == null) {
      System.err.println("Cannot locate PIA root (install) directory.\n"
			 + "  Please put the PIA's binary directory in your "
			 + "shell's search path, \n  or specify the -root "
			 + "option on the command line." );
      return true;
    }
    piaRoot = fixFileName(piaRoot);
    if (!fileExists(piaRoot)) {
      System.err.println("Error: "+piaRoot+" does not exist.\n");
      return true;
    }
    if (!dirExists(piaRoot)) {
      System.err.println("Error: "+piaRoot+" is not a directory.\n");
      return true;
    }
    properties.put("pia.piaroot", piaRoot);

    /* Check to see if we have a user directory.  Warn the user if we don't,
     *	but proceed (to possible disaster). */

    String usrRoot = properties.getProperty("pia.usrroot");
    if (usrRoot == null) {
      if (dirExists(home + filesep + ".pia")) {
	usrRoot = home + filesep + ".pia";
      }
    }
    if (usrRoot == null) {
      /*
      System.err.println("Warning! "
			 + "  You do not have a personal '.pia' directory.\n"
			 + "  This is the directory where agents will put their data.\n"
			 + "  Please create one, or specify an alternative\n "
			 + "  with the -u option on the command line." );
      */
      System.err.println("Creating .pia for agents.\n");

      // We could get more elaborate and use /tmp or try some alternatives.
      boolean result = false;
      File userRoot = new File(home, ".pia") ;
      if ( userRoot != null )
	  result = userRoot.mkdir();

      if( result ){
	usrRoot = userRoot.getAbsolutePath();
	properties.put("pia.usrroot", fixFileName(usrRoot));
      }
      else
	return true;

    } else {
      properties.put("pia.usrroot", fixFileName(usrRoot));
    }

    /* Load the user's default profile if there is one and it hasn't already
     *	been loaded. */
    if (profile == null) {
      profile = usrRoot+filesep+"Config" + filesep + "pia.props";
      if (fileExists(profile) && mergeProperties(profile)) {
	System.err.println("Loaded properties from "+profile);
      }
    }

    return results;
  }
  
}

