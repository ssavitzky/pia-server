// Configuration.java
// $Id: Configuration.java,v 1.4 1999-03-12 19:28:59 steve Exp $

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
 * A Configuration is an object that inspects the runtime environment
 *	and attempts to load properties and locate any other configuration
 *	files that may be necessary.  The end result is a Properties
 *	object that has system properties, command-line arguments, and
 *	the contents of configuration files merged in.<p>
 *
 *	There are several reasons for doing initialization this way:
 *	<ol>
 *	  <li> It keeps it out of the main program (i.e. Pia).
 *	  <li> We don't have to keep updating Pia (which everything
 *		depends on) whenever we add a new option.
 *	  <li> We can do initialization in a standardized way.
 *	</ol>
 */

package org.risource.pia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;
import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;

class Configuration {

  /************************************************************************
  ** Tables:
  ************************************************************************/

  /** The properties under construction. */
  public Properties properties;

  /** The original command line arguments. */
  public String[] commandLine;

  /** Any unprocessed command-line arguments. */
  public List commandLineTail = new List();

  /** Any unrecognized command-line arguments. */
  public List unrecOptions = new List();

  /** A list of environment variables and corresponding property names. */
  public String[] envTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
  };

  /** A list of command-line options and corresponding property name,
   *	type, and default value.  Type is either "string" if a string
   *	follows the option, "bool" if only the option's presence is
   *	significant, "tail" if the option (usually a filename) is to
   *	be added to commandLineTail, and "rest" if the rest of the
   *	command line is to be collected and put into commandLineTail.
   *	Anything else is treated as "string".<p>
   *
   *	The null string as the option name matches any option that
   *	does not start with "-".  
   */
  public String[] optTable = {
    "", "", "tail", null,	// default is to save only filenames.
  };

  /************************************************************************
  ** Loading:
  ************************************************************************/

  /** Load a Configuration subclass and return an instance of it. */
  public static Configuration loadConfig(String className) {
    try {
      Object o = Class.forName(className).newInstance() ;
      return (Configuration) o;
    } catch (Exception ex) {
      System.err.println("Cannot load Configuration class "+className
			 + "because of:\n" + ex.getMessage());
      return null;
    }
  }
  
  /************************************************************************
  ** Configuration:
  ************************************************************************/

  /** Configure.  Effectively merge the system properties, environment 
   *	variables, and command line.
   *	@return true if any command-line options were unrecognized.
   */
  public boolean configure(String[] commandLineArgs) {
    if (properties == null)
      properties = new Properties(System.getProperties());

    commandLine = commandLineArgs;
    suckEnvironment();
    return parseCommandLine(commandLine);
  }
  

  /** Suck in the environment variables specified in envTable.
   *	Actually we can't access the real environment, so we fake it by
   *	assuming that any "name=value" pairs on the command line are
   *	environment variables.  Also put them in properties, and assume
   *	that system properties may have been defined using the "-D" flag
   *	to the java command.
   */
  protected void suckEnvironment() {

    /* First suck key=value pairs out of the command line. */

    String[] args = commandLine;
    for (int i = 0; i < args.length; ++i) {
      int pos = args[i].indexOf("=");
      if (pos >= 0) {
	String key = args[i].substring(0, pos);
	String value = "";
	if (pos < (args[i].length() - 1)) {
	  value = args[i].substring( pos+1 );
	}
	properties.put(key.trim(), value.trim());
      }
    }

    /* Then go through the table for the ones we're interested in. */

    if (envTable != null) {
      for (int i = 0; i < envTable.length; i += 2) {
	String v = properties.getProperty(envTable[i]);
	if (v != null) properties.put(envTable[i+1], v);
      }
    }
  }


  /** Print a usage table. */
  public void usage () {
    PrintStream o = System.out ;
    o.println("options:");

    for (int i = 0; i < optTable.length; i += 4) {
      String msg = "\t"+optTable[i]+"\t";

      if (!optTable[i+2].equals("bool")) {
	msg += optTable[i+2];
      }

      msg += "\t"+optTable[i+1];

      if (optTable[i+3] != null) {
	msg += "\t"+optTable[i+3];
      }
      o.println(msg);
    }
  }


  /** Parse the command line.  Any options not specified in optTable 
   *	are collected in unrecOptions.
   */
  protected boolean parseCommandLine(String[] args) {
    Table props = new Table();
    Table types = new Table();
    
    if (optTable != null)
      for (int i = 0; i < optTable.length; i += 4) {
	props.at(optTable[i], optTable[i+1]);
	types.at(optTable[i], optTable[i+2]);

	if (optTable[i+3] != null) {
	  if (properties.getProperty(optTable[i]) != null)
	    properties.put(optTable[i], optTable[i+3]);
	}
      }
    for (int i = 0; i < args.length; ++i) {
      String opt = args[i];
      if (opt.indexOf("=") >= 0) continue; // FOO=bar already handled.
      String type = (String)types.at(opt);
      String prop = (String)props.at(opt);

      if (type == null) {
	if (! opt.startsWith("-")) {
	  type = (String)types.at("");
	  prop = (String)props.at("");
	}
      }

      if (type == null) {
	unrecOptions.push(opt);
      } else if (type.equals("string")) {
	++i;
	String value = (i < args.length)? args[i] : "";
	properties.put(prop, value);
      } else if (type.equals("tail")) {
	commandLineTail.push(opt);
      } else if (type.equals("rest")) {
	for ( ; i < args.length; ++i) 
	  commandLineTail.push(args[i]);
      } else if (type.equals("bool")) {
	properties.put(prop, "true");
      } else {
	++i;
	String value = (i < args.length)? args[i] : "";
	properties.put(prop, value);
      } 
      
    }

    return unrecOptions.nItems() > 0;
  }

  /************************************************************************
  ** Configuration Utilities:
  ************************************************************************/

  // file separator and other information
  protected String filesep  = System.getProperty("file.separator");
  protected String home     = System.getProperty("user.home");
  protected String cwd	    = System.getProperty("user.dir");
  protected String userName = System.getProperty("user.name");

  /** Locate a file given an enumeration of the names of directories
   *	it might be in.  Given a null filename, returns the first
   *	directory in the path that actually exists.
   */
  public String findFileInPath(String filename, Enumeration path) {
    while (path.hasMoreElements()) {
      String dir = fixFileName(path.nextElement().toString());
      File d = new File(dir);
      if (d.exists()) {
	if (filename == null) return d.getAbsolutePath();
	File f = new File(d, filename);
	if (f.exists()) return f.getAbsolutePath();
      }
    }
    return null;
  }

  /** Fix up a filename starting with "~" or "." */
  public String fixFileName(String filename) {
    if (filename.startsWith("~"))
      return home+filename.substring(1);
    if (filename.startsWith("."+filesep))
      return cwd+filename.substring(1);
    if (filename.equals(".")) return cwd;
    return filename;
  }

  /** Check to see whether a file exists. */
  public boolean fileExists(String filename) {
    File f = new File(filename);
    return f.exists();
  }

  /** Check to see whether a directory exists. */
  public boolean dirExists(String filename) {
    File f = new File(filename);
    return f.isDirectory();
  }

  /** Merge a set of properties from a file with those already defined. 
   *	@return false if the file does not exist or an error occurs.
   */
  public boolean mergeProperties(String filename) {
    File pfile = new File(filename);
    if (! pfile.exists()) return false;

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(filename));
    } catch (FileNotFoundException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }
    Enumeration e = props.propertyNames();
    while (e.hasMoreElements()) {
      String prop = e.nextElement().toString();
      if (! properties.containsKey(prop))
	properties.put(prop, props.getProperty(prop));
    }
    return true;
  }

  /** Load a set of properties from a file.
   *	@return null if the file does not exist or an error occurs.
   */
  public Properties loadProperties(String filename) {
    File pfile = new File(filename);
    if (! pfile.exists()) return null;

    Properties props = new Properties();
    try {
      props.load(new FileInputStream(filename));
    } catch (FileNotFoundException ex) {
      return null;
    } catch (IOException ex) {
      return null;
    }
    return null;
  }

}






