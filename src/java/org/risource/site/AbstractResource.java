////// AbstractResource.java -- Minimal implementation of Resource
//	$Id: AbstractResource.java,v 1.9 1999-12-14 18:44:00 steve Exp $

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
import org.risource.ds.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.*;

import org.risource.dps.namespace.BasicNamespace;

import java.io.File;
import java.net.URL;

/**
 * Abstract base class for implementations of the Resource interface. 
 *
 * <p> AbstractResource implements all those methods of Resource that
 *	can be implemented in terms of other methods, as well as those
 *	that require only configuration information.  
 *
 * <p> There is a fundamental design decision to be made here: whether
 *	to make AbstractResource <em>inherit from</em> something like
 *	<code>Namespace</code>, which would simplify access to configuration
 *	information, or whether it should just have a pointer to an
 *	<code>Element</code> that represents its configuration, which would
 *	be more general.  It's too bad that Java doesn't have mixins. 
 *
 * <p> One factor that may help tip the balance is the fact that some
 *	resources, e.g. those representing real files, do not need explicit
 *	configuration information.  Another is that, without the links from
 *	parent to child that characterize DOM nodes, it should be possible to
 *	garbage-collect resources that are no longer in use.
 *
 * <p> On the other hand, making AbstractResource descend from Namespace
 *	would (at least somewhat) simplify loading, and would make it 
 *	significantly easier to change the configuration using XML.
 *
 * <p> The last point is really the deciding factor:  making it possible to
 *	change the configuration without going through specialized handlers
 *	would represent a major security hole.  
 *
 * <p> <strong>Therefore, configuration information is separate.</strong>
 *
 * @version $Id: AbstractResource.java,v 1.9 1999-12-14 18:44:00 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.risource.dps.Namespace
 * @see org.w3c.dom */
public abstract class AbstractResource implements Resource {
  
  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  /** Returns the <code>ActiveElement</code> node representing this 
   *	Resource's configuration.  
   * 
   *<p>	By convention, the tagname of this ActiveElement is
   *	<code>Resource</code>.  All configuration information with boolean 
   *	or String values is in the configuration's attributes.  The content
   *	consists of 
   *<ul>
   *	<li> <code>Resource</code> elements for children that have
   *	     no configuration information of their own.
   *	<li> <code>ext</code> elements for extension mapping.
   *	<li> at most one <code>Namespace</code> element with the name
   *	     <code>.properties</code>
   *	<li> at most one <code>Namespace</code> element with the name 
   *	     <code>.entities</code>
   *</ul>
   */
  ActiveElement getConfig() {
    return new TreeElement("Resource", reportConfigAttrs());
  }

  /** Initialize configuration information from the current configuration 
   *	element.
   */
  void configure() { }

  /** Modify the configuration from a given ActiveElement. 
   *
   *<p> The attributes and contents of the given element are merged into the
   *	current configuration.
   */
  boolean reconfigure(ActiveElement config) { return false; }

  /** Reports whether this resource has an explicit configuration.
   *	If not, it is assumed that the configuration can be derived
   *	entirely from other considerations. 
   */
  public boolean hasExplicitConfig() { return false; }

  /** Report the current configuration in a new ActiveElement.
   *
   *<p> === probably this should report to an Output.
   *
   * @param depth If zero, report the configuration of this node, otherwise
   *	report the configuration of this node with 
   *	<code>reportConfig(depth-1)</code> appended to its contents.  A 
   *	negative value of <code>depth</code> is meaningful and results in
   *	a report representing the configuration of the complete resource tree.
   *	To a first approximation, the only useful values of <code>depth</code>
   *	are <code>-1</code>, <code>0</code>, and <code>1</code>.
   * @param total If true, report the configuration of all children even 
   *	if they have no explicit configuration information to report. 
   */
  public void reportConfig(int depth, boolean total, Output out) {
    out.startElement("Resource", reportConfigAttrs());
    reportConfigNodes(out);
    if (depth != 0) {
      Resource[] children = listResources();
      if (children != null) {
	for (int i = 0; i < children.length; ++i) {
	  if (total || children[i].hasExplicitConfig())
	    children[i].reportConfig(depth-1, total, out);
	}
      }
    }	  
    out.endNode();
  }

  /** Report the current configuration attributes of this Resource. 
   *
   *<p> This should be extended by subclasses that add attributes that
   *	need to be reported.  The default is to report all attributes
   *	defined by the Resource interface.
   */
  protected ActiveAttrList reportConfigAttrs() {
    // === should use a tagset for this, or return an attr list.  ===
    ActiveAttrList cfg = new TreeAttrList();
    cfg.setAttribute("name", getName());
    cfg.setAttribute("path", getPath());
    if (exists()) cfg.setAttribute("exists", "yes");
    if (isHidden()) cfg.setAttribute("hidden", "yes");
    if (isLocal()) cfg.setAttribute("local", "yes");
    if (isContainer()) cfg.setAttribute("container", "yes");
    if (isPassive()) cfg.setAttribute("passive", "yes");
    if (isSuspect()) cfg.setAttribute("suspect", "yes");
    if (hasExplicitConfig()) cfg.setAttribute("config", "explicit");
    return cfg;
  }

  /** Report the current configuration metadata of this Resource. 
   *
   *<p> This should be extended by subclasses that add variables that
   *	need to be reported.  The default is to report the properties, 
   *	if any.
   */
  protected void reportConfigNodes(Output out) {
    PropertyMap props = getProperties();
    if (props != null) out.putNode((Node)props);
  }

  /** Save the current configuration in a file. 
   *
   *<p> Climbs up the tree if necessary to find the Resource that owns
   *	the root of the configuration document.
   */
  boolean saveConfig() {
    return false;
  }

  protected abstract boolean basicSaveConfig();

  /************************************************************************
  ** Tree Navigation:
  ************************************************************************/

  /** Returns the name of the resource. */
  public abstract String getName();

  /** Returns a Resource that refers to a named child. */
  public abstract Resource getChild(String name);

  /** Returns a Resource that refers to the resource's parent. */
  public abstract Resource getContainer();

  /** Returns the root of this Resource's tree. */
  public Root getRoot() {
    if (getContainer() != null) return getContainer().getRoot();
    else if (this instanceof Root) return (Root)this;
    else return null;
  }

  /************************************************************************
  ** Metadata:
  ************************************************************************/

  /** Return the entire collection of properties as a Namespace. */
  public abstract PropertyMap getProperties();

  /** Write out any properties that have been changed. */
  public abstract boolean synchronize();

  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** Return <code>true</code> if the associated resource exists. 
   *
   *<p>	Like <code>File</code>, a <code>Resource</code> object represents
   *	a virtual pathname; the associated resource may or may not actually
   *	exist in the site's resource tree. 
   *
   * @return <code>true</code> if the associated resource exists.
   */
  public abstract boolean exists();

  /** @return <code>true</code> if the Resource is a container. */
  public abstract boolean isContainer();

  /** @return <code>true</code> if the associated document is accessible as 
   *	a local file */
  public abstract boolean isLocal();

  /** Returns <code>true</code> if the associated document is a local 
   *	file or directory in direct descent from a real root directory.
   *
   *<p>	Note that <code>isReal</code> differs from <code>isLocal</code>: 
   *	a resource can be local without being real, e.g. if it is an alias.
   */
  public abstract boolean isReal();

  /** Returns <code>true</code> if the resource is hidden. 
   *
   *<p>	Hidden documents can still be accessed through their Resource,
   *	but should not be exposed to clients which request the Resource
   *	by its URL.
   *
   * @return <code>true</code> if the associated document is hidden. */
  public abstract boolean isHidden();

  /** Returns <code>true</code> if the resource is passive, i.e. is not 
   *	processed before being returned from a server.  All configuration
   *	information in a passive container is inherited from the last
   *	active ancestor.
   *
   *<p>	Passivity is dominantly inherited -- all of the descendents of an
   *	passive container are necessarily passive.
   *
   * @return <code>true</code> if the associated document is passive. */
  public abstract boolean isPassive();

  /** Returns <code>true</code> if the resource is ``suspect'', i.e. must 
   *	only be processed by ``safe'' tagsets.
   *
   *<p>	Suspicion is dominantly inherited -- all of the descendents of an
   *	suspect container are necessarily suspect.
   *
   * @return <code>true</code> if the associated document is suspect. */
  public abstract boolean isSuspect();

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. */ 
  public abstract Document getDocument();

  /** Returns the File associated with a local document.
   *
   *<p> Although this is not part of the Resource interface, it turns
   *	out to be very convenient to have it defined for local directories
   *	as well as for documents.
   *
   * @return a <code>File</code> for accessing a local document, or
   *      <code>null</code> if the resource is non-local. */
  protected abstract File documentFile();

  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Returns an array of strings naming the contents. 
   *
   * @return An array of strings naming the contents of this container.  The
   *	array will be empty if the container is empty. Returns
   *	<code>null</code> if this resource is not a container.
   */
  public abstract String[] listNames();

  /** Returns an array of resources corresponding to the contents.  
   *
   * @return An array of Resource objects.  The array will be empty if the
   *	 container is empty.  Returns <code>null</code> if this resource is
   *	 not a container.
   */
  public Resource[] listResources() {
    String[] names = listNames();
    if (names == null) return null;
    Resource[] result = new Resource[names.length];
    for (int i = 0; i < names.length; ++i) 
      result[i] = getChild(names[i]);
    return result;
  }

  /************************************************************************
  ** Path Operations:
  ************************************************************************/

  /** Strip a colon-delimited prefix off the given path. */
  protected final static String stripPrefix(String path) {
    return path.substring(path.indexOf(":") + 1);
  }

  /** Return the initial colon-delimited prefix of the given path. */
  protected final static String getPrefix(String path) {
    return path.substring(0, path.indexOf(":"));
  }

  /** Get a resource that has a path starting with a colon-delimited
   *	prefix.  The default is to simply send this to the root. 
   */
  protected Resource getPrefixedResource(String path, String prefix) {
    if (prefix == null || prefix.indexOf('/') >= 0) return null;
    return (getContainer() != null)
      ? ((AbstractResource)getContainer()).getPrefixedResource(path, prefix)
      : null;
  }

  /** Get a resource that has a path starting with a colon-delimited
   *	prefix.  The default is to simply send this to the root. 
   */
  protected Resource locatePrefixedResource(String path, String prefix,
					    boolean create, List extensions) {
    if (prefix == null || prefix.indexOf('/') >= 0) return null;
    return (getContainer() != null)
      ? ((AbstractResource)getContainer()).locatePrefixedResource(path,
								  prefix,
								  create,
								  extensions)
      : null;
  }

  /** Returns the path to this resource from the root of the resource tree. 
   *
   *<p> Note that, because a Resource corresponds to a URL, the names in the
   *	path are always separated by slash (<code>/</code>) characters.
   */
  public String getPath() {
    return (getContainer() == null)? "/" : getBasePath() + getName();
  }

  /** Returns the absolute path to this resource's real location. */
  public abstract String getRealPath();

  /** Returns the path of the resource's parent. */
  public String getBasePath() {
    Resource parent = getContainer();
    if (parent == null) return "/";
    String ppath = parent.getPath();
    return (ppath.endsWith("/"))? ppath : ppath + "/";
  }

  /** Returns a Resource that corresponds to a (typically relative) path.
   *
   *<p>	Paths that start with <code>/</code> or a colon-delimited prefix
   *	are sent directly to the root.  Paths that start with <code>/</code>
   *	<em>followed by</em> a prefix are invalid.  Prefixes are only used
   *	<em>inside</em> the PIA, and must not appear in URL's.
   *
   * @param path a path, relative to either this Resource or (if it starts 
   *	with a slash) the root of the resource tree.
   */
  public Resource getRelative(String path) {
    if (path == null || path.length() == 0) return null;

    if (! isContainer()) { return getContainer().getRelative(path); }
    if (path == null) return this;
    if (path.startsWith("./")) {
      path = path.substring(2);
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.indexOf(':') > 0) return null;
    }
    if (path.length() == 0) {      // Null or empty path: it's this. 
      return this;
    }
    int si = path.indexOf('/');	// Look for a slash.
    int ci = path.indexOf(':');	// look for a colon (delimiting a prefix)

    if (si < 0) {		// No slash: it's a child, .,  or ..
      if (path.equals("..")) return getContainer();
      //if (path.equals(".")) return this;
      return getChild(path);
    } else if (si == 0) {	// Starts with slash: relative to root
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.length() == 0) return getRoot();
      else if (ci > 0) return null;
      else return getRoot().getRelative(path);
    } else if (ci >= 0 && (si < 0 || si > ci)) {
      return getPrefixedResource(stripPrefix(path), getPrefix(path));
    } else if (path.startsWith("../")) {
      path = path.substring(3);
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.length() == 0) return getContainer();
      return getContainer().getRelative(path);
    } else {			// Somewhere underneath.
      Resource child = getChild(path.substring(0, si-1));
      if (child == null) return null;
      path = path.substring(si);
      while (path.startsWith("/")) { path = path.substring(1); }
      return child.getRelative(path);
    }
  }

  /** Locate a Resource that corresponds to a (typically relative) path.
   *
   *<p>	Unlike <code>getRelative</code>, which it resembles, 
   *	<code>locate</code> will search through a list of optional extensions,
   *	and will create a resource for output if necessary.
   *
   * @param path a path, relative to either this Resource or (if it starts 
   *	with a slash) the root of the resource tree.
   * @param create if <code>true</code>, a missing resource will be created 
   *	even if it does not exist.  
   * @param extensions a list of extensions to try.  If <em>empty</em>, no
   *	extension processing will be done.  If <code>null</code>, the default
   *	list will be used. 
   */
  public Resource locate(String path, boolean create, List extensions) {
    if (path == null || path.length() == 0) return null;

    if (! isContainer()) { 
      return getContainer().locate(path, create, extensions); }

    if (extensions == null) extensions = getDefaultExtensions();

    // Do all the same path manipulation as getRelative.

    if (path == null) return this;
    if (path.startsWith("./")) {
      path = path.substring(2);
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.indexOf(':') > 0) return null;
    }
    if (path.length() == 0) {	// Null or empty path: it's this. 
      return this;
    }
    int si = path.indexOf('/');	// Look for a slash.
    int ci = path.indexOf(':');	// look for a colon (delimiting a prefix)

    if (si < 0) {		// No slash: it's a child, .,  or ..
      if (path.equals("..")) return getContainer();
      //if (path.equals(".")) return this;
      Resource child = locateChild(path, extensions);
      if (child == null && create) {
	// Try to create a virtual resource for the child. 
	child = create(path, false, true, null);
	if (child == null)
	  getRoot().report(-2, getPath() + " failed to create child " + path,
			   0, false);
      }
      return child;
    } else if (si == 0) {	// Starts with slash: relative to root
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.length() == 0) return getRoot();
      else if (ci > 0) return null;
      else return getRoot().locate(path, create, extensions);
    } else if (ci >= 0 && (si < 0 || si > ci)) {
      return locatePrefixedResource(stripPrefix(path), getPrefix(path),
				    create, extensions);
    } else if (path.startsWith("../")) {
      path = path.substring(3);
      while (path.startsWith("/")) { path = path.substring(1); }
      if (path.length() == 0) return getContainer();
      return getContainer().locate(path, create, extensions);
    } else {			// Somewhere underneath.
      Resource child = getChild(path.substring(0, si));
      if (child == null && create) {
	// Try to create a virtual container for the child. 
	child = create(path.substring(0, si), true, true, null);
	if (child == null)
	  getRoot().report(-2, getPath() + " failed to create directory " 
			   + path.substring(0, si), 0, false);
      }
      if (child == null) return null;
      path = path.substring(si+1);
      while (path.startsWith("/")) { path = path.substring(1); }
      // If the path is now empty, return the directory we just found
      if (path.length() == 0) return child;
      return child.locate(path, create, extensions);
    }
  }

  /** Locate a child using an optional list of extensions. 
   *
   *<p>	Subclasses that allow directory search paths may need to 
   *	override this in order to get the desired behavior.  Those
   *	that do not may still want to override it for efficiency.
   *
   * @param name the name to look for
   * @param extensions a list of extensions to try.  If <em>empty</em>, no
   *	extension processing will be done.  If <code>null</code>, the default
   *	list will be used. 
   */
  Resource locateChild(String name, List extensions) {
    Resource child = getChild(name);
    if (child != null) return child;
    if (child == null && extensions != null && extensions.nItems() != 0) {
      // No child by that name: try the extension list
      // Get the default extension list if necessary
      if (extensions == null) extensions = getDefaultExtensions();
      if (extensions == null) return null;
      for (int i = 0; i < extensions.nItems(); ++i) {
	child = getChild(name + "." + extensions.at(i));
	if (child != null) return child;
      }
    }
    return null;
  }

  /** Locate one of a list of children using an optional list of extensions. 
   *
   *<p>	Subclasses that allow directory search paths may need to 
   *	override this in order to get the desired behavior.  Those
   *	that do not may still want to override it for efficiency.
   *
   * @param names the list of names to look for
   * @param extensions a list of extensions to try.  If <em>empty</em>, no
   *	extension processing will be done.  If <code>null</code>, the default
   *	list will be used. 
   */
  Resource locateChild(List names, List extensions) {
    for (int j = 0; j < names.nItems(); ++j) {
      String name = names.at(j).toString();
      Resource child = getChild(name);
      if (child != null) return child;
      if (child == null && extensions != null && extensions.nItems() != 0) {
	// No child by that name: try the extension list
	// Get the default extension list if necessary
	if (extensions == null) extensions = getDefaultExtensions();
	if (extensions == null) return null;
	for (int i = 0; i < extensions.nItems(); ++i) {
	  child = getChild(name + "." + extensions.at(i));
	  if (child != null) return child;
	}
      }
    }
    return null;
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
    return (getContainer() != null)
      ? getContainer().getDefaultExtensions()
      : new List();
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
    return (getContainer() != null)
      ? getContainer().loadTagset(name)
      : null;
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public abstract boolean delete();

  abstract boolean renameChild(String fromName, String toName);

  public abstract Resource create(String name, boolean container,
				  boolean virtual, Element config);

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public abstract boolean realize();


  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Return the current running time in milliseconds. */
  protected static long time() {
    return System.currentTimeMillis();
  }

  /** Return a string representing the number of seconds since a given
   *	start time.  Useful for reporting timing information.
   */
  protected static String timing(long start) {
    long t = time();
    String s = "" + (t - start)/1000 + "." + ((t - start)%1000 + 5)/10;
    return s;
  }


  /************************************************************************
  ** Construction and Initialization:
  ************************************************************************/

}
