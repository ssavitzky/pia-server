////// Filter.java: the Document Processing System used stand-alone as a filter
//	$Id: Filter.java,v 1.8 1999-05-07 23:32:58 steve Exp $

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


package org.risource.dps;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.ObjectOutputStream;

import org.risource.dps.Parser;
import org.risource.dps.Input;
import org.risource.dps.Processor;
import org.risource.dps.Tagset;

import org.risource.dps.process.TopProcessor;
import org.risource.dps.tagset.TagsetProcessor;

import org.risource.dps.output.*;
import org.risource.dps.active.*;
import org.risource.dps.handle.GenericHandler;
import org.risource.dps.tree.TreeElement;

/**
 * Filter an input stream or file with the DPS.
 */
public class Filter {
  static String infile = null;
  static String outfile = null;
  static String propfile = null;
  static String tsname = "xhtml";
  static boolean tree = false;
  static boolean entities = true;
  static boolean parsing = false;
  static int verbosity = 0;
  static boolean noaction = false;
  static boolean loadTagset = false;
  static boolean writeTagset = false;

  /** Main program.
   * 	Interpret the given arguments, then run the interpretor over
   *	standard input or the specified input file.
   */
  public static void main(String[] args) {
    if (!options(args)) {
      usage();
      System.exit(-1);		// return an error
    }

    boolean verbose = verbosity > 0;
    boolean debug   = verbosity > 1;
    InputStream in = System.in;
    OutputStream outs = System.out;
    OutputStreamWriter out = null;

    /* Open the input and output files. */

    try {
      if (infile != null) in = new FileInputStream(infile);
    } catch (Exception e) {
      System.err.println("Cannot open input file " + infile);
      System.exit(-1);
      in = null;
    }
      
    try {
      if (outfile != null) outs = new FileOutputStream(outfile);
    } catch (Exception e) {
      System.err.println("Cannot open output file " + outfile);
      System.exit(-1);
      outs = null;
    }
    if (outs != null) out = new OutputStreamWriter(outs);

    if (verbosity > 2) {
      java.util.Properties env = System.getProperties();
      java.util.Enumeration names = env.propertyNames();
      while (names.hasMoreElements()) {
	String name = names.nextElement().toString();
	System.err.println(name+" = " + env.getProperty(name));
      }
    }

    if (verbose) {
      System.err.println("infile = " + infile );
      System.err.println("outfile= " + outfile);
      System.err.println("tagset= " + tsname);
    }

    /* Initialize and run the interpretor */

    /* Start by getting a Tagset. */

    org.risource.dps.tagset.Loader.setVerbosity(verbosity);
    if (tsname.equals("tagset")) loadTagset = true;
    if (tsname.equals("BOOT")) loadTagset = true;

    Tagset ts = org.risource.dps.tagset.Loader.getTagset(tsname);
    if (ts == null) {
      System.err.println("Unable to load Tagset " + tsname);
      System.exit(-1);
    }

    if (writeTagset) {
      // === Crock!
      if (debug) dumpTagset(ts);
      try {
	ObjectOutputStream destination = new ObjectOutputStream(outs);
	destination.writeObject( ts );
	destination.flush();
	destination.close();
      } catch (java.io.IOException e) { 
	e.printStackTrace(System.err);
      }
      System.exit(0);
    }

    if (verbose && loadTagset) {
      System.err.println("We appear to be defining a tagset. ");
    }

    if (debug) {
      dumpTagset(ts);
    } else if (verbose) {
      System.err.println("================================");
      System.err.println(ts.toString());
    }

    if (in == null || out == null) System.exit(-1);

    /* Ask the Tagset for an appropriate parser, and set its Reader. */
    Parser p = ts.createParser();
    p.setReader(new InputStreamReader(in));

    /* Finally, create a Processor and set it up. */
    TopContext ii = loadTagset? new TagsetProcessor() : new TopProcessor();
    ii.setInput(p);
    ii.setTagset(ts);

    if (debug && (ii.getNamespace(null) != null)) {
      System.err.println("Entities defined: ");
      java.util.Enumeration names = ii.getNamespace(null).getNames();
      while (names.hasMoreElements()) {
	String name = names.nextElement().toString();
	ActiveNodeList v = ii.getValueNodes(name, false);
	System.err.println(" " + name + "=" + v);
      }
      System.err.print("\n");
    } else if (verbose &&  (ii.getNamespace(null) != null)) {
      System.err.println(ii.getNamespace(null).toString());
    }
     
    ToParseTree outputTree = null;
    Output output = null;
    ToWriter writeout = null;
    if (loadTagset) {
      output = new org.risource.dps.output.DiscardOutput();
    } else if (parsing) {
      outputTree = new org.risource.dps.output.ToParseTree();
      // === root should be an ActiveDocument ===
      outputTree.setRoot(new TreeElement("Document", (ActiveAttrList)null));
      output = outputTree;
    } else {
      output = writeout = new ToWriter(out);
    }

    if (debug) output = new OutputTrace(output);
    ii.setOutput(output);
    ii.setVerbosity(verbosity);

    /* Run the Processor. */
    if (noaction) ii.copy();
    else ii.run();

    if (loadTagset && verbose) {
      dumpTagset(((TagsetProcessor)ii).getNewTagset());
    }

    if (parsing) { 
      if (debug) {
	System.err.println("\n\n========= parse tree: ==========\n");
	if (outputTree != null) System.err.println(outputTree.getRoot());
      } 
      try {
	Object oo = loadTagset
	  ? ((TagsetProcessor)ii).getNewTagset()
	  : outputTree.getRoot();
	if (loadTagset && debug) dumpTagset((Tagset)oo);
	ObjectOutputStream destination = new ObjectOutputStream(outs);
	destination.writeObject( oo );
	destination.flush();
	destination.close();
      } catch (java.io.IOException e) { 
	e.printStackTrace(System.err);
      }
    } 

    if (writeout != null) writeout.close();
    else if (out != null) try {
      out.close();
    } catch (java.io.IOException e){}
  }

  public static void dumpTagset(Tagset ts) {
    System.err.print("Tags defined in Tagset(" + ts.getName() + "): ");
    java.util.Enumeration names = ts.allHandlerNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      Handler h = ts.getHandlerForTag(name);
      GenericHandler gh =
	(h instanceof GenericHandler)? (GenericHandler)h : null;
      if (gh != null) {
	name += gh.getSyntaxCode() < 0? "E" : h.expandContent()? "X" : "Q";
	String cname = gh.getClass().getName();
	if (cname.equals("org.risource.dps.handle.GenericHandler")) name += "U";
      }
      System.err.print(" " + name + " " + ((gh != null)? gh.toString() : ""));
    }
    System.err.print("\n");
  }

  /** Print a usage string.
   */
  public static void usage() {
    PrintStream o = System.err ;

    o.println("Usage: java org.risource.dps.Filter [option]... [infile]");
    o.println("    options:");
    o.println("        -e	no entities");
    o.println("        -h	print help string");
    o.println("        -n       no action (for building parse tree)");
    o.println("        -o file	specify output file");
    o.println("	       -p	build parse tree");
    o.println("        -t ts	specify tagset name");
    o.println("        -w	write tagset");
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
      } else if (args[i].equals("-tree")) {
	tree = true;
      } else if (args[i].equals("-d")) {
	verbosity += 2;
      } else if (args[i].equals("-e")) {
	entities = false;
      } else if (args[i].equals("-n")) {
	noaction = true;;
      } else if (args[i].equals("-o")) {
	if (i == args.length - 1) return false;
	outfile = args[++i];
      } else if (args[i].equals("-p")) {
	parsing = true;
      } else if (args[i].equals("-q")) {
	verbosity = -1;
      } else if (args[i].equals("-s")) {
	verbosity = -2;
      } else if (args[i].equals("-t")) {
	if (i == args.length - 1) return false;
	tsname = args[++i];
      } else if (args[i].equals("-v")) {
	verbosity += 1;
      } else if (args[i].equals("-w")) {
	writeTagset = true;
      } else if (args[i].charAt(0) != '-') {
	if (infile != null) return false;
	infile = args[i];
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
