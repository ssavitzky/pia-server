////// subsite.java -- standard implementation of Resource
//	$Id: Subsite.java,v 1.2 1999-08-20 00:03:26 steve Exp $

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

import org.risource.site.util.*;

import org.w3c.dom.*;
import org.risource.ds.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tagset.Loader;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;

/**
 * Standard implementation of the Resource interface for containers with
 *	configuration information. 
 *
 * <p>	Because configuring a Subsite is fairly expensive, the system 
 *	caches each Subsite in a hash table attached to its parent.  
 *	Eventually there will be a mechanism for blowing away the caches
 *	to reclaim space. 
 *
 * <p>	For similar reasons, a Subsite goes through its list of default
 *	directories and any child elements in its configuration, in order
 *	to locate all of the children.  This makes listing a Subsite 
 *	very efficient -- the second time around.  There <em>is</em> a
 *	need to check timestamps, which is not addressed at the moment.
 *
 * @version $Id: Subsite.java,v 1.2 1999-08-20 00:03:26 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 */
public class Subsite extends ConfiguredResource implements Resource {

  /************************************************************************
  ** State:
  ************************************************************************/

  /** Table that maps filename suffix into tagset.  
   *
   *<p>	A ``tagset'' name that starts with ``<code>!</code>'' indicates an
   *	external command to be run as a CGI script interperter.  For example,
   *	<code>.pl</code> might be mapped into <code>!/usr/bin/perl</code>.
   *	A simple ``<code>!</code>'' indicates a file that is directly 
   *	executable.
   */
  protected Table tagsetMap   = null;

  /** Table that maps filename suffix into <em>result</em> MIME type. */
  protected Table mimeTypeMap = null;

  /** A ``search path'' of filename suffixes. */
  protected List extSearch;

  /** Table that maps tagset names into the corresponding tagsets. 
   */
  protected Table tagsetCache = null;

  /** Context to be used for loading tagsets. */
  protected SiteContext tsLoader = null;

  /** A table that maps resource names into child Subsite objects.
   */
  protected Table subsiteCache = new Table();

  /** A table that maps resource names into configuration elements. 
   */
  protected Table childConfigCache = null;

  /** The File containing the configuration information, if any. */
  protected File configFile = null;

  /** A table that maps resource names into locations or child resources.
   *	A location is either the File for the directory <em>containing</em> 
   *	the child, (<em>not</em> the child itself) or a configuration element.
   *    If the child turns out to have associated configuration information
   *	the location will be replaced by the corresponding resource the
   *	first time the child is accessed.
   */
  protected Table childLocationCache = null;
  protected int childLocationCount = 0;

  protected Namespace entityBindings = null;

  protected File virtualSearchPath[] = null;

  /** The most recent time the underlying directory or a directory in the
   *	<code>virtualSearchPath</code> was modified.
   *	This is compared to the timestamps of the underlying real directory 
   *	and every directory in the <code>virtualSearchPath</code>. 
   */
  protected long locationTimestamp = 0;

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  protected boolean basicSaveConfig() {
    // If there's nothing to save, we're done.
    if (config == null) return true;

    // If there's no real directory to save in, we fail.
    if (!realize()) return false;

    // Locate the file. 
    configFile = new File(file, getConfigFileName());

    // === write into config file ===
    return true; // === unimplemented
  }

  protected ActiveElement loadConfig() {
    configFile = locateChildDocument(getConfigFileName());
    if (configFile == null) return null;

    // === really want to load the tagset here as well ===

    config = XMLUtil.load(configFile, null);
    return config;
  }

  protected void configure() {
    if (config == null) return;
    super.configure();

    // Handle any attributes not handled by the parent. 

    // Go through the children.
  }

  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** @return <code>true</code>, because a Subsite necessarily exists.
   */
  public boolean exists() { return true; }

  /** @return <code>true</code> because a Subsite is a container by
   *	definition.
   */
  public boolean isContainer() { return true; }

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. */ 
  public Document getDocument() {
    Resource doc = locate("home", false, null);	// === getDocument bogus
    if (doc == null)  doc = locate("index", false, null); // === getDocument
    return (doc != null)? doc.getDocument() : null;
  }

  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Locate a child document without using the location cache. */
  protected File locateChildDocument(String name) {
    File f = null;
    if (file != null && file.exists()) {
      f = new File(file, name);
      if (f.exists()) return f;
    }
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (!virtualSearchPath[j].isDirectory()) continue;
	f = new File(virtualSearchPath[j], name);
	if (f.exists()) return f;
      }
    }
    return null;
  }

  /** Construct a new childLocationCache */
  protected void locateChildren() {
    Table t = new Table();
    int count = 0;

    String list[];
    String name;
    Enumeration enum;

    // First look for the real contents
    if (file != null && file.isDirectory()) {
      list = file.list();
      for (int i = 0; i < list.length; ++i) {
	t.at(list[i], file);
	count ++;
      }
      if (file.lastModified() > locationTimestamp) 
	locationTimestamp = file.lastModified();
    }
    // Finally look at every directory in the virtual search path.
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (!virtualSearchPath[j].isDirectory()) continue;
	list = virtualSearchPath[j].list();
	for (int i = 0; i < list.length; ++i) 
	  if (t.at(list[i]) == null) {
	    t.at(list[i], virtualSearchPath[i]);
	    count ++;
	  }
	if (virtualSearchPath[j].lastModified() > locationTimestamp) 
	  locationTimestamp = virtualSearchPath[j].lastModified();
      }
    }
    // Finally, check for pure virtual resources
    if (childConfigCache != null) {
      enum = childConfigCache.keys();
      while (enum.hasMoreElements()) {
	name = enum.nextElement().toString();
	if (t.at(name) == null) {
	  t.at(name, childConfigCache.at(name));
	  count ++; 
	}
      } 
    }
    childLocationCache = t;
    childLocationCount = count;
  }

  /** Compare the timestamps on all directories in the virtualSearchPath
   *	with the time the last locateChildren was done.
   *
   * @return <code>true</code> if the location cache is still valid.
   */
  protected boolean locationCacheValid() {
    if (childConfigCache == null) return false;
    if (file != null && file.isDirectory()
        && file.lastModified() > locationTimestamp) return false;
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j].isDirectory()
	    && virtualSearchPath[j].lastModified() > locationTimestamp) 
	  return false;
      }
    }

    return true;
  }

  /** Returns an array of strings naming the contents. 
   *
   * @return An array of strings naming the contents of this container.  The
   *	array will be empty if the container is empty. Returns
   *	<code>null</code> if this resource is not a container.
   */
  public String[] listNames() {
    if (!locationCacheValid()) locateChildren();

    String list[] = new String[childLocationCount];
    Enumeration enum = childLocationCache.keys();
    for (int i = 0; enum.hasMoreElements(); ++i) 
      list[i] = enum.nextElement().toString();
    return list;
  }

  /** Returns a Resource that refers to a named child. */
  public Resource getChild(String name) {
    ActiveElement cfg = null;
    Resource result = null;
    if (! locationCacheValid()) locateChildren();

    Object loc = childLocationCache.at(name);
    if (loc == null) {			  	// No location -- it's a dud.
      return null;
    } else if (loc instanceof Resource) { 	// location is a resource.
      return (Resource) loc;
    } else if (loc instanceof File) { 	  	// Location is a file
      File f = new File((File)loc, name);
      // Check for any associated configuration information
      if (childConfigCache != null) 
	cfg = (ActiveElement) childConfigCache.at(name);
      if (f.isDirectory()) {
	result = new Subsite(name, this, f, config, null);
	subsiteCache.at(name, result);
	childLocationCache.at(name, result);
      } else {
	result = new LocalDocument(name, this, f, config);
	if (cfg != null) childLocationCache.at(name, result);
      }
    } else if (loc instanceof ActiveElement) { 	// Location is a config.
      // no file, so the location must be a configuration.
      cfg = (ActiveElement)loc;
      if (cfg.hasTrueAttribute("collection")) {
	result = new Subsite(name, this, null, config, null);
	subsiteCache.at(name, result);
	childLocationCache.at(name, result);
      } else {
	result = new LocalDocument(name, this, null, config);
	childLocationCache.at(name, result);
      }
    } else {
      getRoot().report(-2, "unknown type for child location " + loc, 0, false);
      return null;
    }
    return result;
  }
  /************************************************************************
  ** Name and Type Mapping:
  ************************************************************************/

  /** Get the default extension list used by <code>locate</code>. 
   *
   * <p> The default is to pass the buck to the container.  It is assumed 
   *	that somewhere up the resource tree we will encounter a node with a
   *	default list. 
   *
   * @return an empty List if there is no parent.
   */
  public List getDefaultExtensions() {
    List l = new List();
    l.push("xh"); l.push("html"); l.push("htm"); // === bogus
    return l;
  }

  /** Map a document name to a corresponding file type. */
  public String getContentTypeFor(String name) {
    return (getContainer() != null)
      ? getContainer().getContentTypeFor(name)
      : null;
  }

  /** Map a document name to a corresponding tagset name. */
  public String getTagsetNameFor(String name) {
    return (getContainer() != null)
      ? getContainer().getTagsetNameFor(name)
      : null;
  }

  /** Load a tagset. */
  public Tagset loadTagset(String name) {
    // === Probably should check the tagset name for signs of being a path 

    // First look in the cache
    Tagset ts = (tagsetCache == null)? null : (Tagset) tagsetCache.at(name);
    if (ts != null && ts.upToDate()) {
      return ts;
    }

    // Then look for it as a local ".ts" file.
    File tsfile = locateChildDocument(name + ".ts");
    if (tsfile != null) {
      if (tsLoader == null) tsLoader = new SiteContext(this, null);
      ts = Loader.loadTagset(tsfile, tsLoader);
      if (tagsetCache == null) tagsetCache = new Table();
      tagsetCache.at(name, ts);
      return ts;
    }

    // Finally, fall back on the parent
    //	If there isn't a parent, hope that the tagset loader can find it.
    return (getContainer() != null)
      ? getContainer().loadTagset(name)
      : Loader.loadTagset(name);
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete() {
    if (file != null && file.exists()) {
      if (!file.delete()) return false;
    }
    // === delete config from parent
    return true;
  }

  boolean renameChild(String fromName, String toName) {
    return false;		// === rename unimplemented
  }


  public Resource create(String name, boolean container,
			 boolean virtual, Element config) {
    return null;		// === create unimplemented
  }

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize() {
    if (!isReal()) {
      if (!getContainer().realize()) return false;
      // replace file with a new one under its (realized) parent. 
      file = new File(new File(getContainer().getRealPath()), getName());
      real = true;
    }
    return isReal();
  }

  /************************************************************************
  ** Construction and Initialization:
  ************************************************************************/

  public Subsite(String name, ConfiguredResource parent,
		 File file, ActiveElement config, Namespace props) {
    super(name, parent, true, file, config, props);
  }
  public Subsite(ConfiguredResource parent, ActiveElement config) {
    super(parent, config);
  }
}
