////// Inspect.java: inspect a Site object
//	$Id: Inspect.java,v 1.1 1999-08-18 18:06:30 steve Exp $

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


package org.risource.site.util;

import java.io.*;

import org.risource.site.*;

import org.risource.dps.*;
import org.risource.dps.output.*;
import org.risource.dps.active.*;

/**
 * Inspect a Site. 
 *
 * <p>	A Site object is constructed, then queried according to the 
 *	command line options.  The command-line arguments are meant to be
 *	similar to those of the <code>ls</code> command.
 */
public class Inspect {
  static String location = null;
  static String outfile = null;
  static String cfgfile = null;

  static Site mysite = null;

  static int verbosity = 0;
  static boolean recursive = false;
  static boolean total = true;

  /** Main program.
   * 	Interpret the given arguments, then report.
   */
  public static void main(String[] args) {
    if (!options(args)) {
      usage();
      System.exit(-1);		// return an error
    }

    boolean verbose = verbosity > 0;
    boolean debug   = verbosity > 1;
    OutputStream outs = System.out;
    Writer out = null;
    ToWriter output = null;

    int depth = recursive? -1 : 1;

    try {
      if (outfile != null) outs = new FileOutputStream(outfile);
    } catch (Exception e) {
      System.err.println("Cannot open output file " + outfile);
      System.exit(-1);
    }
    out = new PrintWriter(new OutputStreamWriter(outs));
    output = new ToWriter(out);

    if (verbosity > 2) {
      java.util.Properties env = System.getProperties();
      java.util.Enumeration names = env.propertyNames();
      while (names.hasMoreElements()) {
	String name = names.nextElement().toString();
	System.err.println(name+" = " + env.getProperty(name));
      }
    }

    if (verbose) {
      System.err.println("location = " + location );
      System.err.println("outfile  = " + outfile);
    }

    /* Create the Site */

    mysite = new Site(location, cfgfile);
    
    /* Report */

    String names[] = mysite.listNames();
    if (names != null) {
      for (int i = 0; i < names.length; ++i) System.err.print(" " + names[i]);
      System.err.println(" ");
    }

    mysite.reportConfig(depth, total, output);
    output.close();

    if (out != null) try {
      out.close();
    } catch (java.io.IOException e){}
    System.exit(0);
  }


  /** Print a usage string.
   */
  public static void usage() {
    PrintStream o = System.err ;

    o.println("Usage: java org.risource.site.Inspect [option]... [location]");
    o.println("    options:");
    o.println("        -h	print help string");
    o.println("        -o file	specify output file");
    o.println("        -t ts	specify tagset name");
    o.println("        -f file	specify config file name");
    o.println("	       -r	recursive");
    o.println("	       -s	silent");
    o.println("	       -q	quiet");
    o.println("	       -v	verbose");
    o.println("        -d	debug");
  }

  /** Decode the arguments.
   *	Returns false if the -h option or an invalid option is present.
   */
  public static boolean options(String[] args) {
    suckEnvironment(args);
    for (int i = 0 ; i < args.length ; i++) {
      if (args[i].indexOf("=") >= 0) {
	continue;
      } else if (args[i].equals("-h")) {
	return false;
      } else if (args[i].equals("-r")) {
	recursive = true;
      } else if (args[i].equals("-d")) {
	verbosity += 2;
      } else if (args[i].equals("-o")) {
	if (i == args.length - 1) return false;
	outfile = args[++i];
      } else if (args[i].equals("-f")) {
	if (i == args.length - 1) return false;
	cfgfile = args[++i];
      } else if (args[i].equals("-q")) {
	verbosity = -1;
      } else if (args[i].equals("-r")) {
	recursive = true;
      } else if (args[i].equals("-s")) {
	verbosity = -2;
      } else if (args[i].equals("-v")) {
	verbosity += 1;
      } else if (args[i].charAt(0) != '-') {
	if (location != null) return false;
	location = args[i];
      } else {
	System.err.println("bad arg: "+args[i]);
	return false;
      }
    }
    return true;
  }

  /** Stolen from Configuration.  Set system props from name=value pairs. */
  protected static void suckEnvironment(String[] args) {
    for (int i = 0; i < args.length; ++i) {
      int pos = args[i].indexOf("=");
      if (pos >= 0) {
	String key = args[i].substring(0, pos);
	String value = "";
	if (pos < (args[i].length() - 1)) {
	  value = args[i].substring( pos+1 );
	}
	System.getProperties().put(key.trim(), value.trim());
      }
    }
  }
}
