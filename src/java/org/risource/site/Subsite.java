////// subsite.java -- standard implementation of Resource
//	$Id: Subsite.java,v 1.6 1999-09-11 00:26:18 steve Exp $

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
 * @version $Id: Subsite.java,v 1.6 1999-09-11 00:26:18 steve Exp $
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
  protected List extSearch = null;

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

  /** An array of File objects to search for children, in addition to
   *	the real location.
   *
   *<p>	Although in theory we could have an arbitrary number of these,
   *	at the moment the implementation only supports two: the ``virtual
   *	location'' and the directory that contains default documents.
   */
  protected File virtualSearchPath[] = null;

  /** The most recent time the underlying directory or a directory in the
   *	<code>virtualSearchPath</code> was modified.
   *	This is compared to the timestamps of the underlying real directory 
   *	and every directory in the <code>virtualSearchPath</code>. 
   */
  protected long locationTimestamp = 0;

  /************************************************************************
  ** Access to State:
  ************************************************************************/

  /** Returns the directory from which virtual documents are obtained.
   *	By convention, this is the first File in the virtual search path.
   */
  protected File getVirtualLoc() { 
    return (virtualSearchPath == null || virtualSearchPath.length < 1)
      ? null : virtualSearchPath[0];
  }

  /** Returns the directory from which default documents are obtained.
   *	By convention, this is the last File in the virtual search path.
   */
  protected File getDefaultDir() {
    return (virtualSearchPath == null || virtualSearchPath.length < 2)
      ? null : virtualSearchPath[virtualSearchPath.length - 1];
  }

  protected File[] getVirtualSearchPath() { return virtualSearchPath; }

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  protected ActiveAttrList reportConfigAttrs() {
    ActiveAttrList cfg = super.reportConfigAttrs();
    if (getVirtualLoc() != null) 
      cfg.setAttribute("virtual", getVirtualLoc().getPath());
    if (getDefaultDir() != null) 
      cfg.setAttribute("default", getDefaultDir().getPath());

    // Everything else is handled by AbstractResource (super)
    return cfg;
  }

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
    configFile = locateChildFile(getConfigFileName());
    if (configFile == null) return null;

    // === really want to load the tagset here as well ===

    config = XMLUtil.load(configFile, null);
    return config;
  }

  protected boolean configAttrs(ActiveElement config) {
    String v;

    // Handle any attributes not handled by the parent. 

    v = config.getAttribute("virtual");
    if (v != null) {
      if (virtualSearchPath == null) virtualSearchPath = new File[2];
      virtualSearchPath[0] = new File(v);
    }

    v = config.getAttribute("default");
    if (v != null) {
      if (virtualSearchPath == null) virtualSearchPath = new File[2];
      virtualSearchPath[1] = new File(v);
    }

    // Call on the parent to finish the job
    return super.configAttrs(config);
  }

  protected void configItem(String tag, ActiveElement item) {

    // Resource, Document, Container
    if (tag.equals("Resource") ||
	tag.equals("Document") || tag.equals("Container")) {
      configChildItem(tag, item);
    }    

    // <ext>
    else if (tag.equals("Map")) configMapItem(tag, item);

    // let ConfiguredResource (super) handle it
    else super.configItem(tag, item);
  }

  protected void configNamespaceItem(Namespace ns) {
    String name = ns.getName();

    
  }

  protected void configChildItem(String tag, ActiveElement e) {
    if (tag.equals("Container")) {
      e.setAttribute("container", "yes");
    }
    String name = e.getAttribute("name");
    if (childConfigCache == null) childConfigCache = new Table();
    childConfigCache.at(name, e);
  }

  protected void configMapItem(String tag, ActiveElement e) {
    String ext = e.getAttribute("extension");
    if (ext == null) ext = e.getAttribute("ext");
    if (ext == null) ext = "";
    String type = e.getAttribute("type");
    if (type == null) type = "";
    String ts = e.getAttribute("tagset");
    if (ts == null) ts = "";

    if (tagsetMap == null) tagsetMap = new Table();
    tagsetMap.put(ext, ts);
    if (mimeTypeMap == null) mimeTypeMap = new Table();
    mimeTypeMap.put(ext, type);
    if (! ts.startsWith("#")) {
      if (extSearch == null) extSearch = new List();
      extSearch.push(ext);
    }
  }


  /************************************************************************
  ** Metadata:
  ************************************************************************/

  /** Returns the time that the resource was last modified. */
  public long getLastModified() {
    long mod = (file != null && file.exists())? file.lastModified() : 0;
    
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j] != null && virtualSearchPath[j].exists()) {
	  long vmod = virtualSearchPath[j].lastModified();
	  if (vmod > mod) mod = vmod;
	}
      }
    }

    return mod;
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
    return (doc != null)
      ? doc.getDocument()
      : new Listing(getName(), this, file);
  }

  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Locate a (local) child document without using the location cache. */
  protected File locateChildFile(String name) {
    File f = null;
    if (file != null && file.exists()) {
      f = new File(file, name);
      if (f.exists()) return f;
    }
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j] == null 
	    || !virtualSearchPath[j].isDirectory()) continue;
	f = new File(virtualSearchPath[j], name);
	if (f.exists()) return f;
      }
    }
    return null;
  }

  /** Locate a child document without using the location cache. */
  protected Resource locateChild(String name) {
    File f = null;
    if (file != null && file.exists()) {
      f = new File(file, name);
      if (f.exists()) return configureChild(name, f);
    }
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j] == null 
	    || !virtualSearchPath[j].isDirectory()) continue;
	f = new File(virtualSearchPath[j], name);
	if (f.exists()) return configureChild(name, f);
      }
    }
    if (childConfigCache != null) {
      ActiveElement cfg = (ActiveElement) childConfigCache.at(name);
      return (cfg == null)? null :  configureChild(name, cfg);
    }
    return null;
  }

  /** Return the resource corresponding to a child file. */
  protected Resource configureChild(String name, File f) {
    ActiveElement cfg = null;
    Resource result = null;
    if (childConfigCache != null) 
      cfg = (ActiveElement) childConfigCache.at(name);
    if (f.isDirectory()) {
      result = new Subsite(name, this, f, cfg, null);
      subsiteCache.at(name, result);
    } else {
      result = new SiteDocument(name, this, f, cfg);
    }
    return result;
  }

  /** Return the resource corresponding to a configuration element. */
  protected Resource configureChild(String name, ActiveElement cfg) {
    Resource result = null;
    if (cfg.hasTrueAttribute("container") 
	|| cfg.getTagName().equals("Container")) {
      result = new Subsite(name, this, null, cfg, null);
      subsiteCache.at(name, result);
    } else {
      result = new SiteDocument(name, this, null, cfg);
    }
    return result;
  }

  /** Construct a new childLocationCache */
  protected void locateChildren() {
    Table t = new Table();
    int count = 0;

    String list[];
    String name;
    Enumeration enum;

    // === look in subsiteCache to see if any subsites have already been seen
    // === make sure they're still valid. 

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
	if (virtualSearchPath[j] == null 
	    || !virtualSearchPath[j].isDirectory()) continue;
	list = virtualSearchPath[j].list();
	for (int i = 0; i < list.length; ++i) {
	  if (t.at(list[i]) == null) {
	    t.at(list[i], virtualSearchPath[j]);
	    count ++;
	  }
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
    if (childLocationCache == null) return false;
    if (file != null && file.isDirectory()
        && file.lastModified() > locationTimestamp) return false;
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j] != null 
	    && virtualSearchPath[j].isDirectory()
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
    // If we haven't yet located all the children, just find this one.
    if (childLocationCache == null) return locateChild(name);

    // Update the location cache if necessary
    if (! locationCacheValid()) locateChildren();

    ActiveElement cfg = null;
    Resource result = null;
    Object loc = childLocationCache.at(name);
    if (loc == null) {			  	// No location -- it's a dud.
      return null;
    } else if (loc instanceof Resource) { 	// location is a resource.
      return (Resource) loc;
    } else if (loc instanceof File) { 	  	// Location is a file
      File f = new File((File)loc, name);
      // Check for any associated configuration information
      result = configureChild(name, f);
    } else if (loc instanceof ActiveElement) { 	// Location is a config.
      // no file, so the location must be a configuration.
      result = configureChild(name, (ActiveElement)loc);
    } else {
      getRoot().report(-2, "unknown type for child location " + loc, 0, false);
      return null;
    }
    childLocationCache.at(name, result);
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
    if (extSearch != null) return extSearch;
    else if (base != null) extSearch = base.getDefaultExtensions();
    else {
      List l = new List();
      l.push("xh"); l.push("html"); l.push("htm"); // === bogus
      extSearch = l;
    }
    return extSearch;
  }

  protected String getExtension(String name) {
    int i = name.lastIndexOf(".");
    return (i < 0)? "" : name.substring(i+1);
  }

  /** Map a document name to a corresponding file type. */
  public String getContentTypeFor(String name) {
    if (mimeTypeMap != null) {
      Object s = mimeTypeMap.at(getExtension(name));
      if (s != null) return s.toString();
    }
    return (getContainer() != null)
      ? getContainer().getContentTypeFor(name)
      : null;
  }

  /** Map a document name to a corresponding tagset name. */
  public String getTagsetNameFor(String name) {
    if (tagsetMap != null) {
      Object s = tagsetMap.at(getExtension(name));
      if (s != null) return s.toString();
    }
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
    File tsfile = locateChildFile(name + ".ts");
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
      if (!base.realize()) return false;
      // replace file with a new one under its (realized) parent. 
      file = new File(new File(base.getRealPath()), getName());
      real = true;
    }
    return isReal();
  }

  /************************************************************************
  ** Construction and Initialization:
  ************************************************************************/

  /** Construct a new Subsite with the given name, parent, and so on. 
   *
   *<p>	If the virtual search path is not set up by the configuration,
   *	the default directory is inherited from the parent, and the
   *	virtual location is inherited from the parent by appending
   *	this resource's name to the parent's virtual location. 
   *
   *<p> The current implementation checks to see whether the virtual
   *	location exists.  This means that the PIA has to be restarted
   *	if a directory is added to its virtual (prototype) location.
   *
   *<p> There is a sequencing problem in initialization: we need the
   *	virtual search path in order to load the configuration file,
   *	but the configuration might override the virtual search path.
   */
  public Subsite(String name, ConfiguredResource parent,
		 File file, ActiveElement config, Namespace props) {
    super(name, parent, true, file, config, props);
    if (file != null && file.exists()) real = true;
    if (virtualSearchPath == null && base != null) {
      if (virtualSearchPath == null) virtualSearchPath = new File[2];
      if (virtualSearchPath[1] == null)
	virtualSearchPath[1] = base.getDefaultDir();
      if (virtualSearchPath[0] == null) {
	File f = base.getVirtualLoc();
	if (f != null && f.exists()) {
	  f = new File(f, name);
	  if (f.exists() && f.isDirectory()) virtualSearchPath[0] = f;
	}
      }
    }
    if (config == null) {
      setConfig(loadConfig());
    }
  }

  /** Create a root Subsite.
   *	Actually loading the configuration is left to the caller, presumably
   *	a constructor for Site.
   */
  protected Subsite(String realLoc, String virtualLoc, String defaultDir) {
    super("/", null, true, new File(realLoc), null, null);
    if (file != null && file.exists()) real = true;
    virtualSearchPath = new File[2];
    if (virtualLoc != null) virtualSearchPath[0] = new File(virtualLoc);
    if (defaultDir != null) virtualSearchPath[1] = new File(defaultDir);    
    for (int i = 0; i < virtualSearchPath.length; ++i) {
      if (virtualSearchPath[i] != null && !virtualSearchPath[i].exists())
	virtualSearchPath[i] = null;
    }
  }
}
