////// ConfiguredResource.java -- Minimal implementation of Resource
//	$Id: ConfiguredResource.java,v 1.1 1999-08-07 00:29:46 steve Exp $

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
 * Abstract base class for implementations of the Resource interface
 *	that have an explicit configuration. 
 *
 * <p> This class contains all of the state necessary for a Resource that
 *	has an explicit configuration element.  We assume that all parents
 *	of a ConfiguredResource are also configured. 
 *
 * @version $Id: ConfiguredResource.java,v 1.1 1999-08-07 00:29:46 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.risource.dps.Namespace
 * @see org.w3c.dom */
public abstract class ConfiguredResource extends AbstractResource {
  
  /************************************************************************
  ** Configuration State:
  ************************************************************************/

  /** The current configuration information. */
  protected ActiveElement config = null;

  // Variables corresponding to the configuration attributes.
  protected String name = null;
  protected boolean hidden = false;
  protected boolean passive = false;
  protected boolean suspect = false;
  protected boolean real = false;

  /** The parent, which we assume is also a ConfiguredResource. */
  protected ConfiguredResource base = null; // === Subsite? ===

  /** The associated real file or directory, if any. */
  protected File file;

  /** The associated metadata. */
  protected Namespace properties = null;

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
    if (config == null) 
      config = new TreeElement("Resource", reportConfigAttrs());
    return config;
  }

  /** Set the configuration from a given ActiveElement. 
   *
   *<p> The configuration element is retained, and any changes made to the
   *	Resource will be reflected in the configuration element.
   */
  void setConfig(ActiveElement config) {
    this.config = config;
    configure();
  }

  /** Initialize configuration information from the current configuration 
   *	element.
   */
  void configure() {
    if (name != null) name = config.getAttribute("name");
    if (config.hasTrueAttribute("hidden")) hidden = true;
    
    // === have to hit the other items here.
  }

  /** Modify the configuration from a given ActiveElement. 
   *
   *<p> The attributes and contents of the given element are merged into the
   *	current configuration.
   */
  boolean reconfigure(ActiveElement config) {
    return false; // === unimplemented ===
  }

  /** Reports whether this resource has an explicit configuration.
   *	In this case, it does. 
   *
   * @return <code>true</code>.
   */
  public boolean hasExplicitConfig() { return true; }

  /** Save the current configuration in a file. 
   *
   *<p> Climbs up the tree if necessary to find the Resource that owns
   *	the root of the configuration document.
   */
  boolean saveConfig() {
    if (config == null) return true;
    else if (config.getParentNode() != null) return base.saveConfig();
    else return basicSaveConfig();
  }

  protected abstract boolean basicSaveConfig();


  /************************************************************************
  ** Tree Navigation:
  ************************************************************************/

  /** Returns the name of the resource. */
  public String getName() { return name; }

  /** Returns a Resource that refers to a named child. */
  public abstract Resource getChild(String name);

  /** Returns a Resource that refers to the resource's parent. */
  public Resource getContainer() { return base; }

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
  public Namespace getProperties() {
    return properties;
  }

  /** Write out any properties that have been changed. */
  public boolean synchronize() {
    return saveConfig();
  }

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
  public boolean exists() { return file != null && file.exists(); }

  /** @return <code>true</code> if the Resource is a container. */
  public abstract boolean isContainer();

  /** @return <code>true</code> if the associated document is accessible as 
   *	a local file */
  public boolean isLocal() { return file != null && file.exists(); }

  /** Returns <code>true</code> if the associated document is a local 
   *	file or directory in direct descent from a real root directory.
   *
   *<p>	Note that <code>isReal</code> differs from <code>isLocal</code>: 
   *	a resource can be local without being real, e.g. if it is an alias.
   */
  public boolean isReal() { return real; }

  /** Returns <code>true</code> if the resource is hidden. 
   *
   *<p>	Hidden documents can still be accessed through their Resource,
   *	but should not be exposed to clients which request the Resource
   *	by its URL.
   *
   * @return <code>true</code> if the associated document is hidden. */
  public boolean isHidden() { return hidden; }

  /** Returns <code>true</code> if the resource is passive, i.e. is not 
   *	processed before being returned from a server.  All configuration
   *	information in a passive container is inherited from the last
   *	active ancestor.
   *
   *<p>	Passivity is dominantly inherited -- all of the descendents of an
   *	passive container are necessarily passive.
   *
   * @return <code>true</code> if the associated document is passive. */
  public boolean isPassive() { return passive; }

  /** Returns <code>true</code> if the resource is ``suspect'', i.e. must 
   *	only be processed by ``safe'' tagsets.
   *
   *<p>	Suspicion is dominantly inherited -- all of the descendents of an
   *	suspect container are necessarily suspect.
   *
   * @return <code>true</code> if the associated document is suspect. */
  public boolean isSuspect() { return suspect; }

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. */ 
  public abstract Document getDocument();

  /** @return a <code>File</code> for accessing a local document, or
   *      <code>null</code> if the resource is non-local. */
  protected File documentFile() { return file; }

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


  /************************************************************************
  ** Path Operations:
  ************************************************************************/

  /** Returns the absolute path to this resource's real location. */
  public String getRealPath() {
    return (isReal())? file.getAbsolutePath() : null;
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public abstract boolean delete();

  boolean renameChild(String fromName, String toName) {
    return false;		// === rename unimplemented
  }

  public boolean rename(String name) {
    return (base != null) && base.renameChild(getName(), name);
  }

  public abstract Resource create(String name, boolean container,
				  boolean virtual, Element config);

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public abstract boolean realize();


  /************************************************************************
  ** Construction and Initialization:
  ************************************************************************/

  public ConfiguredResource(String name, ConfiguredResource parent, 
			    boolean container, File file, 
			    ActiveElement config, Namespace props) {
    this.base = parent;
    this.name = name;
    this.file = file;
    this.properties = props;
    if (config != null) setConfig(config);
  }

  public ConfiguredResource(ConfiguredResource parent, ActiveElement config) {
    this.base = parent;
    setConfig(config);
  }
}
