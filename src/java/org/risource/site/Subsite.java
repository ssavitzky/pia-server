////// subsite.java -- standard implementation of Resource
//	$Id: Subsite.java,v 1.16 1999-10-15 17:15:42 steve Exp $

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

import org.risource.dps.namespace.PropertyTable;

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
 * @version $Id: Subsite.java,v 1.16 1999-10-15 17:15:42 steve Exp $
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
  
  /** Name of the directory's home document. */
  protected String homeDocumentName = null;

  /** Name of listing document. */
  protected String listingName = "-";

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

  public String getHomeDocumentName() {
    return (homeDocumentName != null)? homeDocumentName
      : (base != null)? base.getHomeDocumentName() : "home";
  }

  public String getListingName() {
    return listingName;
  }

  public boolean isListingName(String name) {
    return name.equals(listingName) || name.equals(".");
  }

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  protected ActiveAttrList reportConfigAttrs() {
    ActiveAttrList cfg = super.reportConfigAttrs();
    if (file != null) 
      cfg.setAttribute("file", file.getPath());
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

  public void loadConfigFile(File f) {
    if (!f.exists()) {
      RuntimeException e = new RuntimeException("Cannot open config file");
      getRoot().reportException(e, "  in loadConfigFile");
      throw e;
    }
    setConfig(XMLUtil.load(new FileDocument("[config]", this, f),
			   getConfigTagset()));
  }

  public void loadConfig() {
    String fn = getConfigFileName();
    if (fn == null || fn.length() == 0) return;
    configFile = locateChildFile(fn);
    if (configFile != null) loadConfigFile(configFile);
  }

  protected boolean configAttrs(ActiveElement config) {
    String v;

    // Handle any attributes not handled by the parent. 

    v = config.getAttribute("file");
    if (v != null) {
      file = (v.length() == 0)? null : new File(v);
    }

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

  /** Handle one of the standard configuration items for a container.
   *
   *<p> The following items are handled:
   *<ul>
   *	<li> <code>Resource</code>, <code>Document</code>, 
   *	     <code>Container</code>: configure a child resource
   *	<li> <code>Ext</code>: configure an extension map item.
   *	<li> <code>Home</code>: register this Subsite as an agent's home
   *	     Resource.  The content is the name to register, defaulting if 
   *	     empty to the name of this Resource.
   *</ul>
   */
  protected void configItem(String tag, ActiveElement item) {

    // Resource, Document, Container
    if (tag.equals("Resource") ||
	tag.equals("Document") || tag.equals("Container")) {
      configChildItem(tag, item);
    }    

    // <HomeDocumentName>
    else if (tag.equals("HomeDocumentName")) {
      homeDocumentName = item.contentString();
    }

    // <Ext>
    else if (tag.equals("Ext")) configExtItem(tag, item);

    // <Home>
    else if (tag.equals("Home")) configHomeItem(tag, item);

    // let ConfiguredResource (super) handle it
    else super.configItem(tag, item);
  }

  protected void configHomeItem(String tag, ActiveElement item) {
    String name = getName();
    if (item.hasChildNodes()) {
      name = item.getChildNodes().toString().trim();
    }
    getRoot().registerAgentHome(name, this);
  }

  protected void configChildItem(String tag, ActiveElement e) {
    if (tag.equals("Container")) {
      e.setAttribute("container", "yes");
    }
    String name = e.getAttribute("name");
    if (childConfigCache == null) childConfigCache = new Table();
    childConfigCache.at(name, e);
  }

  /** Handle an &lt;Ext&gt; element. 
   *
   * <p> === has problems dealing with items that are already on the 
   *	 === extSearch list.  Ideally need separate hidden flag, too.
   */
  protected void configExtItem(String tag, ActiveElement e) {
    String ext = e.getAttribute("name");
    if (ext == null) ext = "";
    String type = e.getAttribute("type");
    if (type == null) type = "";
    String ts = e.getAttribute("tagset");
    if (ts == null) ts = "";

    if (tagsetMap == null) tagsetMap = new Table();
    if (mimeTypeMap == null) mimeTypeMap = new Table();
    mimeTypeMap.put(ext, type);
    if (! ts.startsWith("#")) {
      tagsetMap.put(ext, ts);
      if (extSearch == null) extSearch = new List();
      extSearch.push(ext);
    } else {
      // tagset name starting with # means don't search for this ext. 
      tagsetMap.put(ext, ts.substring(1));
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

  /** Returns the Document associated with the Resource.
   *
   *<p> The home document name can be set as the value of the 
   *	<code>HomeDocumentName</code> property.  If not set in the 
   *	configuration file it is inherited from the parent; if never
   *	set it defaults to <code>home</code>.
   *
   *<p> Sensibly check to make sure the home ``document'' isn't a container. 
   *	This can happen if there is a subdirectory called <code>home</code>,
   *	for example.
   *
   * @return document matching <code>homeDocumentName</code>, or a listing.
   */ 
  public Document getDocument() {
    Resource doc = locate(getHomeDocumentName(), false, null);
    return (doc != null && ! doc.isContainer())
      ? doc.getDocument()
      : new Listing(getListingName(), this, file);
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
    if (childConfigCache != null) {
      ActiveElement cfg = (ActiveElement) childConfigCache.at(name);
      if (cfg == null) return null; 
      Resource child = configureChild(name, cfg);
      if (child != null) f = child.getDocument().documentFile();
      if (f != null && f.exists()) return f;
      else System.err.println("Bad documentFile from virtual " + getPath() 
			      + "/" + name);
    }
    return null;
  }

  /** Locate a child document without using the location cache. */
  protected Resource locateChild(String name) {
    Resource child = (Resource)subsiteCache.at(name);
    if (child != null) return child;

    if (isListingName(name)) {
      if (childConfigCache != null) {
	ActiveElement cfg = (ActiveElement) childConfigCache.at(name);
	if (cfg != null) return configureChild(name, cfg);
      } 
      return new Listing(getListingName(), this, file);
    }

    File f = null;
    if (file != null && file.exists()) {
      f = new File(file, name);
      if (f.exists()) return configureChild(name, f, false);
    }
    if (virtualSearchPath != null) {
      for (int j = 0; j < virtualSearchPath.length; ++j) {
	if (virtualSearchPath[j] == null 
	    || !virtualSearchPath[j].isDirectory()) continue;
	f = new File(virtualSearchPath[j], name);
	if (f.exists()) return configureChild(name, f, true);
      }
    }
    if (childConfigCache != null) {
      ActiveElement cfg = (ActiveElement) childConfigCache.at(name);
      return (cfg == null)? null :  configureChild(name, cfg);
    }
    return null;
  }

  /** Return the resource corresponding to a child file. */
  protected Resource configureChild(String name, File f, boolean virtual) {
    ActiveElement cfg = null;
    Resource result = null;
    File v = null;
    boolean container = f.isDirectory();
    if (childConfigCache != null) 
      cfg = (ActiveElement) childConfigCache.at(name);
    if (container) {
      result = (Resource)subsiteCache.at(name);
      // === may have to reconfigure if timestamps changed!
      if (result != null) return result; // subsite already configured
      if (virtual) { v = f; f = null; }
      result = new Subsite(name, this, f, v, cfg);
      subsiteCache.at(name, result);
    } else {
      result = new SiteDocument(name, this, f, !virtual, cfg);
    }
    return result;
  }


  /** Locate a path used in a <code>virtual</code> configuration 
   *	attribute. 
   */
  protected File locateVirtual(String path) {
    // === for now just go relative ===
    // === needs lots of configuration stuff.
    return new File(getVirtualLoc(), path);
  }


  /** Return the resource corresponding to a configuration element. */
  protected Resource configureChild(String name, ActiveElement cfg) {
    Resource result = null;
    File loc = null;
    String vpath = cfg.getAttribute("virtual");
    if (vpath != null) {
      loc = locateVirtual(vpath);
    }
    if (cfg.hasTrueAttribute("container") 
	|| cfg.getTagName().equals("Container")) {
      result = (Resource)subsiteCache.at(name);
      if (result != null) return result; // subsite already configured
      result = new Subsite(name, this, null, loc,  cfg);
      subsiteCache.at(name, result);
    } else {
      result = new SiteDocument(name, this, loc, false, cfg);
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
	if (list[i].equals(".") || list[i].equals("..")) continue;
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
	for (int i = 0; list != null && i < list.length; ++i) {
	  if (list[i].equals(".") || list[i].equals("..")) continue;
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
    // Return null if the name is null.
    if (name == null || name.length() == 0) return null;

    // If we haven't yet located all the children, just find this one.
    if (childLocationCache == null) return locateChild(name);

    // Update the location cache if necessary
    if (! locationCacheValid()) locateChildren();

    ActiveElement cfg = null;
    Resource result = null;
    Object loc = childLocationCache.at(name);
    if (isListingName(name)
	&& (loc == null || !(loc instanceof ActiveElement))) {
      return new Listing(getListingName(), this, file);
    } else if (loc == null) {			// No location -- it's a dud.
      return null;
    } else if (loc instanceof Resource) { 	// location is a resource.
      return (Resource) loc;
    } else if (loc instanceof File) { 	  	// Location is a file
      File f = new File((File)loc, name);
      // Check for any associated configuration information
      result = configureChild(name, f, (loc != file));
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
    if (name == null) {
      throw new RuntimeException("Null name passed to loadTagset");
    }

    // First look in the cache
    Tagset ts = (tagsetCache == null)? null : (Tagset) tagsetCache.at(name);
    if (ts != null && ts.upToDate()) {
      return ts;
    }

    // Then look for it as a local ".ts" file.
    long start = 0;
    File tsfile = locateChildFile(name + ".ts");
    if (tsfile != null) {
      start = time();
      if (tsLoader == null) tsLoader = new SiteContext(this, null);
      ts = Loader.loadTagset(tsfile, tsLoader);
      if (ts == null) {
	getRoot().report(-2, "*** " + getPath() + " failed to load tagset '"
			 + name, 0, false);
      } else {
	getRoot().report(0, getPath() + " Loaded tagset '" + name 
			 + "' in " + timing(start) + " seconds.", 2, false);
	if (tagsetCache == null) tagsetCache = new Table();
	tagsetCache.at(name, ts);
	return ts;
      }
    }

    // Finally, fall back on the parent
    if (getContainer() != null) return getContainer().loadTagset(name);

    //	If there isn't a parent, hope that the tagset loader can find it.
    start = time();
    ts = Loader.loadTagset(name);
    if (ts == null) {
      getRoot().report(-2, "*** " + getPath() + " failed to load tagset '"
		       + name, 0, false);
    } else {
      if (tagsetCache == null) tagsetCache = new Table();
      tagsetCache.at(name, ts);
      getRoot().report(0, getPath() + " Loaded system tagset '" + name 
		       + "' in " + timing(start) + " seconds.", 4, false);
    }
    return ts;
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete() {
    // === need writable checks.  All resources need isWritable 
    if (file != null && file.exists() ) {
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
    if (!isReal() && !realize()) {
      getRoot().report(-2, "Cannot realize " + getPath(), 0, false);
      return null;
    } 
    File rfile = new File(file, name);
    File vfile = (getVirtualLoc() == null)
      ? null : new File(getVirtualLoc(), name);
    if (container) {
      // === should probably just make the Subsite and tell _it_ to realize.
      rfile.mkdirs();
      if (!rfile.exists() && !rfile.isDirectory()) {
	getRoot().report(-2, "Cannot create directory " + name 
			 + " in " + getPath(), 0, false);
	return null;
      } 
      return new Subsite(name, this, rfile, vfile, null);
    } else {
      if (rfile.exists() && rfile.isDirectory()) {
	getRoot().report(-2, "Creating " + name + " in " + getPath()
			 + " would overlay existing directory", 0, false);
	return null;
      } 
      return new SiteDocument(name, this, rfile, true, null);
    }
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
      file.mkdirs();
      if (! file.exists() || ! file.isDirectory()) {
	file = null;
	return false;
      }
      prepareRealDirectory();
      real = true;
    }
    return isReal();
  }

  /** Put into a newly-created real directory whatever it is we need to 
   *	put there.
   */
  public void prepareRealDirectory() {

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
  public Subsite(String name, Subsite parent,
		 File file, File vfile, ActiveElement config) {
    super(name, parent, true, (file == null), file, config, null);
    if (file != null && file.exists()) real = true;
    if (virtualSearchPath == null && (base != null || vfile != null)) 
      virtualSearchPath = new File[2];
    if (virtualSearchPath[1] == null)	
      virtualSearchPath[1] = base.getDefaultDir();
    if (virtualSearchPath[0] == null || vfile != null) {
      if (vfile != null) virtualSearchPath[0] = vfile;
      else {
	File f = base.getVirtualLoc();
	if (f != null && f.exists()) {
	  f = new File(f, name);
	  if (f.exists() && f.isDirectory()) virtualSearchPath[0] = f;
	}
      }
    }

    // === probably need to load config. unconditionally!
    loadConfig();
  }

  /** Create a root Subsite.
   *	Actually loading the configuration is left to the caller, presumably
   *	a constructor for Site.
   */
  protected Subsite(String realLoc, String virtualLoc, String defaultDir,
		    PropertyTable props) {
    super("/", null, true, (realLoc != null), new File(realLoc), null, props);
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
