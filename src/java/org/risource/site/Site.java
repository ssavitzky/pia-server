////// Site.java -- implementation of Root
//	$Id: Site.java,v 1.8 1999-12-14 18:44:00 steve Exp $

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


package org.risource.site;

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.process.TopProcessor;
import org.risource.dps.namespace.PropertyTable;

import org.risource.ds.*;

import org.risource.site.util.*;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;

/**
 * Implementation of Root, the root of a resource tree.
 *
 * <p> All real container resources that descend from a Site can be 
 *	assumed to be Subsite objects. 
 *
 * @version $Id: Site.java,v 1.8 1999-12-14 18:44:00 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public class Site extends Subsite implements Root {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int verbosity = 0;
  protected PrintStream log = System.err;
  protected URL serverURL = null;
  protected String configFileName = "_subsite.xcf";
  protected String configTagsetName = "xxml";
  protected Tagset configTagset = null;

  protected File siteConfigFile = null;

  protected Table agentHomes = null;
  protected int agentCount = 0;

  /************************************************************************
  ** Root interface:
  ************************************************************************/

  /** Returns the URL by which this resource tree can be accessed. */
  public URL getServerURL() { return serverURL; }

  /** Report an exception. */
  public void reportException(Exception e, String explanation) {
    String message = "Exception " + e + " " + explanation + "\n";
    report(-2, message, 0, true);
  }

  /** Report a message. */
  public void report(int severity, String message,
		     int indent, boolean noNewline) {
    if (getVerbosity() < severity) return;
    for (int i = 0; i < indent; ++i) log.print(" ");
    if (noNewline) log.print(message);
    else	   log.println(message);
  }

  /** Obtain the current verbosity level for error reporting. */
  public int getVerbosity() { return verbosity; }

  public Resource agentHome(String name) {
    return (agentHomes == null)? null : (Resource)agentHomes.at(name);
  }

  public void registerAgentHome(String name, Resource home) {
    if (agentHomes == null) agentHomes = new Table();
    if (home == null) {
      agentHomes.remove(name);
      agentCount --;
    } else {
      agentHomes.at(name, home);
      agentCount ++;
    }
  }
  
  /** List the registered agents. */
  public String[] listAgents() {
    if (agentHomes == null) return null;
    String list[] = new String[agentCount];
    Enumeration e = agentHomes.keys();
    for (int i = 0; i < agentCount && e.hasMoreElements(); ++i) {
      list[i] = e.nextElement().toString();
    }
    return list;
  }

  /** Construct a suitable TopContext for processing a Document. 
   *
   *<p>	Note that this operation is used for processing configuration 
   *	documents.  Therefore, it should not be assumed that the container
   *	of the document to be processed is fully configured yet.
   *
   * @param doc the Document to be processed.  If null, an unconfigured
   *	TopContext of the appropriate type is constructed.
   * @param ts the tagset with which to process the document.  If omitted, 
   *	the document provides its own Input.
   * @return a suitable TopContext.
   */
  public TopContext makeTopContext(Document doc, Tagset ts) {
    Input in = null;
    if (doc == null) {
    } else if (ts == null) {
      in = doc.documentInput();
      String tsname = doc.getTagsetName();
      ts = doc.loadTagset(tsname);
    } else {
      /* Ask the Tagset for an appropriate parser, and set its Reader. */
      Parser p = ts.createParser();
      p.setReader(doc.documentReader());
      in = p;
    }
    /* Finally, create a Processor and set it up. */
    TopContext ii = makeTopContext();
    if (doc != null) ii.setDocument(doc);
    if (in != null) ii.setInput(in);
    if (ts != null) ii.setTagset(ts);

    return ii;
  }

  /** Construct a TopContext of the correct type. 
   *	This method may be overridden in a subclass in order to provide
   *	a suitable environment in which to run documents. 
   */
  protected TopContext makeTopContext() {
    return new TopProcessor();
  }

  /************************************************************************
  ** Resource interface:
  ************************************************************************/

  public Root getRoot() { return this; }

  /** <code>getChild</code> extended to handle agent names starting 
   *	with tilde.
   */
  public Resource getChild(String name) {
    if (name.startsWith("~") && agentHomes != null) 
      return (Resource) agentHomes.at(name.substring(1));
    else return super.getChild(name);
  }

  protected Resource locateChild(String name) {
    if (name.startsWith("~") && agentHomes != null) 
      return (Resource) agentHomes.at(name.substring(1));
    else return super.locateChild(name);
  }

  /** Get a resource that has a path starting with a colon-delimited
   *	prefix.
   *
   * @param path the path to the resource, with the prefix stripped off.
   * @param prefix the prefix, including its final colon.
   */
  protected Resource getPrefixedResource(String path, String prefix) {
    if (prefix == null || prefix.indexOf('/') >= 0) return null;
    if (prefix.equalsIgnoreCase("file:")) {
      File f = new File(path);
      return f.exists()? new FileDocument(f.getName(), null, f) : null;
    } else if (prefix.equalsIgnoreCase("root:") 
	       || prefix.equalsIgnoreCase("r:")) {
      if (file == null || !file.exists()) return null;
      while (path.startsWith("/")) { path = path.substring(1); }
      return new FileDocument("root:", null, file).getRelative(path);
    } else if( prefix.equalsIgnoreCase("vroot:") 
	       || prefix.equalsIgnoreCase("v:")) {
      if (virtualSearchPath == null || virtualSearchPath.length < 1)
	return null;
      File v = virtualSearchPath[0];
      if (v == null || !v.exists()) return null;
      return new FileDocument("vroot:", null, v).getRelative(path);
    } else {
      return null;
    }
  }

  /** Get a resource that has a path starting with a colon-delimited
   *	prefix.
   *
   * @param path the path to the resource, with the prefix stripped off.
   * @param prefix the prefix, including its final colon.
   */
  protected Resource locatePrefixedResource(String path, String prefix,
					    boolean create, List extensions) {
    if (prefix == null || prefix.indexOf('/') >= 0) return null;
    if (prefix.equalsIgnoreCase("file:")) {
      File f = new File(path);
      return f.exists()? new FileDocument(f.getName(), null, f) : null;
    } else if (prefix.equalsIgnoreCase("root:") 
	       || prefix.equalsIgnoreCase("r:")) {
      if (file == null || !file.exists()) return null;
      while (path.startsWith("/")) { path = path.substring(1); }
      return new FileDocument("root:", this, file).locate(path, create,
							  extensions);
    } else if( prefix.equalsIgnoreCase("vroot:") 
	       || prefix.equalsIgnoreCase("v:")) {
      if (virtualSearchPath == null || virtualSearchPath.length < 1)
	return null;
      File v = virtualSearchPath[0];
      if (v == null || !v.exists()) return null;
      return new FileDocument("vroot:", this, v).locate(path, create,
							extensions);
    } else {
      return null;
    }
  }


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  public void setVerbosity(int v) { verbosity = v; }
  public void setReporting(PrintStream s) { log = s; }
  public void setServerURL(URL u) { serverURL = u; }

  public String getConfigFileName() { return configFileName; }
  public String getConfigTagsetName() { return configTagsetName; }

  /** Load and return the tagset for loading configuration files. */
  protected Tagset getConfigTagset() {
    if (configTagset == null) {
      configTagset = loadTagset(getConfigTagsetName());
    }
    if (configTagset == null) {
      report(-2, "*** Failed to load specified config-file tagset "
	     + getConfigTagsetName(), 0, false);
      configTagset = loadTagset("xxml");
    }
    return configTagset;
  }

  /** Set configuration file name. */
  public void setConfigFileName(String configFileName) {
    if (configFileName != null) this.configFileName = configFileName;
  }

  protected boolean configAttrs(ActiveElement config) {
    String v;

    // Handle any attributes not handled by the parent. 

    v = config.getAttribute("configfile");
    if (v != null) {
      setConfigFileName(v);
    }

    // Call on the parent to finish the job
    return super.configAttrs(config);
  }

  protected void configItem(String tag, ActiveElement item) {
    // <ConfigFileName>
    if (tag.equals("ConfigFileName")) {
      setConfigFileName(item.contentString());
    }    

    // let ConfiguredResource (super) handle it
    else super.configItem(tag, item);
  }
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a Site from a location, configuration, and properties. */
  public Site(File location, ActiveElement config) {
    super("/", null, null, location, config);
    // === merge properties. 
  }

  /** Construct a Site from a location and configuration file.
   *	
   * @param location the pathname of the real location of the Site. 
   * @param siteConfigPath the pathname of the site configuration file. 
   *	The configuration file need not be accessible as part of the Site. 
   */
  public Site(String location, String siteConfigPath) {
    this(location, null, null, null, null, null);
    loadConfigFile(new File(siteConfigPath));
  }

  /** Construct a Site with an explicit virtual path.
   *
   *<p> This constructor allows both the real and virtual locations to be
   *	specified before attempting to load the configuration file.  This
   *	is necessary in case the configuration file is virtual.  The actual
   *	loading is deferred in case the caller needs to perform additional
   *	setup.
   *
   * @param realLoc the pathname of the real location of the Site
   * @param virtualLoc the pathname of the virtual location of the Site
   * @param defaultDir the pathname of the directory of default documents
   * @param configFileName the default configuration file name
   * @param configTagsetName the tagset to use for loading configuration files.
   */
  public Site(String realLoc, String virtualLoc, String defaultDir,
	      String configFileName, String configTagsetName,
	      PropertyTable props) {
    super(realLoc, virtualLoc, defaultDir, props);
    if (configFileName != null)   setConfigFileName(configFileName);
    if (configTagsetName != null) this.configTagsetName = configTagsetName;
  }

}
