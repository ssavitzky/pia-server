////// FileResource.java -- Minimal implementation of Resource
//	$Id: FileResource.java,v 1.6 2001-04-03 00:05:23 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
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

import org.risource.dps.namespace.BasicNamespace;

import java.io.*;
import java.net.URL;

/**
 * Minimal implementation of the Resource interface. 
 *
 * <p> FileResource implements a Resource as a local File or Directory with no
 *	explicit configuration information.  It is little more than a wrapper
 *	for <code>File</code> that adds the Resource metadata and methods.
 *
 * <p> As much of a FileDocument's configuration information as possible
 *	is derived from its corresponding File. 
 *
 * @version $Id: FileResource.java,v 1.6 2001-04-03 00:05:23 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom */
public abstract class FileResource extends AbstractResource {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected File file = null;
  protected AbstractResource base = null;

  protected PropertyMap propertyBindings = null;
  protected boolean hidden = false;
  protected boolean real = false;
  protected boolean passive = false;
  protected boolean suspect = false;

  /************************************************************************
  ** Metadata:
  ************************************************************************/

  /** Return the entire collection of properties as a PropertyMap. */
  public PropertyMap getProperties() { return propertyBindings; }

  /** Write out any properties that have been changed. */
  public boolean synchronize() {
    return false;		// === unimplemented
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
  public boolean exists() { return file.exists(); }

  /** @return <code>true</code> if the Resource is a container. */
  public boolean isContainer() { return file.isDirectory(); }

  /** @return <code>true</code> if the associated document is accessible as 
   *	a local file */
  public boolean isLocal() { return true; }

  /** Returns <code>true</code> if the associated document is a local 
   *	file or directory in direct descent from a real root directory.
   *
   *<p>	Note that <code>isReal</code> differs from <code>isLocal</code>: 
   *	a resource can be local without being real, e.g. if it is an alias.
   */
  public boolean isReal() { return real; }

  /** Returns <code>true</code>  if the associated document is hidden. 
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
  ** Tree Navigation:
  ************************************************************************/

  /** Returns the name of the resource. */
  public String getName() { return file.getName(); }

  /** Returns a Resource that refers to a named child. */
  public abstract Resource getChild(String name);

  /** Returns a Resource that refers to the resource's parent. */
  public Resource getContainer() { return base; }

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. */ 
  public abstract Document getDocument();

  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Returns an array of strings naming the contents. 
   *
   * @return An array of strings naming the contents of this container.  The
   *	array will be empty if the container is empty. Returns
   *	<code>null</code> if this resource is not a container.
   */
  public String[] listNames() { 
    return isContainer()? file.list() : null;
  }

  /************************************************************************
  ** Path Operations:
  ************************************************************************/

  /** Returns the absolute path to this resource's real location. */
  public String getRealPath() {
    if (! isReal()) return null;
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      return null;
    }
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete() { 
    return file.delete();
  }

  boolean renameChild(String fromName, String toName) {
    return false;		// === rename unimplemented
  }

  public boolean rename(String name) {
    return (base != null) && base.renameChild(getName(), name);
  }

  public Resource create(String name, boolean container, boolean virtual, 
			 Element config) {
    if (container) return null; // === punt on containers for now ===
    return new FileDocument(name, this, config);
  }

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize() { return isReal(); }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Configure a new resource. 
   *
   * @param name the name of the new Resource
   * @param parent its parent resource
   * @param container whether this resource is a container
   * @param config its configuration (ignored)
   * @return the new resource.  This need not be the resource handling the
   *	call; in some cases it may be necessary to return an instance of
   *`	a different class.  
   */
  public Resource configure(String name, Resource parent, Element config) {
    this.base = (AbstractResource) parent;
    file = new File(base.documentFile(), name);
    return this;
  }

  public FileResource(String name, AbstractResource parent) {
    base = parent;
    file = new File(parent.documentFile(), name);
  }

  public FileResource(String name, AbstractResource parent, File f) {
    base = parent;
    file = f;
  }

  public FileResource(String name, AbstractResource parent, boolean container, 
		       Element config) {
    this(name, parent);
    real = parent.isReal();
    if (container) file.mkdir();
  }

}
