////// Resource.java -- interface for a resource in a site
//	$Id: Resource.java,v 1.9 2001-01-11 23:37:51 steve Exp $

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

import java.io.File;
import java.net.URL;

/**
 * Generic interface for something accessible through a URL.
 *
 * <p> The Resource interface essentially generalizes File and URL.  A
 *	Resource may be either document-like (a piece of data, typically
 *	marked-up text) or directory-like (a container for sub-resources that
 *	can be listed); a container resource may also have an associated
 *	document.  A Resource may have an arbitrary amount of XML metadata
 *	associated with it, called its <em>properties</em>.
 *
 * <p> A Resource also has a more limited, and separate, set of metadata
 *	called its <em>configuration</em>, which is needed for constructing
 *	and maintaining the Resource tree.  This is conveniently represented
 *	by means of an XML document for each container Resource, with a child
 *	element for each child resource.  <em>It is important to note that
 *	every container may have its own configuration document.</em> Note
 *	also that, unlike the properties, the configuration information is
 *	maintained by the implementation and methods for modifying it need not
 *	be exposed in the Resource interface.
 *
 * <p> One might ask why a site does not have a single, unified 
 *	configuration document, with each container Resource element 
 *	containing its own children, and so on.  This is primarily an
 *	efficiency decision; it allows configuration files to be loaded
 *	incrementally as needed.  It also allows subsite directories to
 *	be shared among multiple sites.  Note that the configuration of a
 *	virtual subsite probably <em>will</em> be incorporated into its 
 *	parent's configuration, simply because there is no real location 
 *	for it. 
 *
 * <p> One may also ask why even document resources have methods for accessing
 *	their children, and so on -- methods only defined for containers. 
 *	The answer is simple: it makes operations on hierarchies (such as
 *	following paths) significantly simpler and more efficient.  See the 
 *	<strong>Composite</strong> pattern in the Gang-of-Four book for 
 *	details.
 *
 * <p> On the other hand, it makes sense to make the <em>document associated
 *	with a resource</em> a separate object with its own interface; this
 *	neatly solves the ambiguity between a container and its associated
 *	document, and enormously simplifies the common situation where the
 *	associated document is actually stored in an <code>index.html</code>
 *	or <code>home.xh</code> file.
 *
 * <p> One may also ask why properties and configuration information are kept
 *	separate.  (They are, at least, <em>conceptually</em> separate;
 *	nothing actually prevents configuration information from being
 *	accessible as properties in some implementations.)  This is because
 *	not all applications need metadata, and because the same underlying
 *	real resource may need different metadata when used in different
 *	applications.
 *
 * @version $Id: Resource.java,v 1.9 2001-01-11 23:37:51 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 * @see org.risource.site.Document

 */
public interface Resource {

  /************************************************************************
  ** Tree Navigation:
  ************************************************************************/

  /** Returns the name of the resource. */
  public String getName();

  /** Returns a Resource that refers to a named child. */
  public Resource getChild(String name);

  /** Returns a Resource that refers to the resource's parent. */
  public Resource getContainer();

  /** Returns the root of this Resource's tree. */
  public Root getRoot();

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  /** Reports whether this resource has an explicit configuration.
   *	If not, it is assumed that the configuration can be derived
   *	entirely from other considerations. 
   */
  public boolean hasExplicitConfig();

  /** Output an XML representation of the current configuration.
   *
   *<p>	The configuration is represented by a <code>Resource</code>
   *	element.  All configuration information with boolean 
   *	or String values is in the configuration's attributes.  The content
   *	consists of 
   *
   *<ul>
   *	<li> <code>Resource</code> elements for children that have
   *	     no configuration information of their own.  These may also 
   *	     have the tagnames <code>Document</code> or <code>Container</code>.
   *	<li> <code>ext</code> elements for extension mapping.
   *	<li> at most one <code>Namespace</code> element with the name
   *	     <code>DAV</code> for WebDAV properties
   *	<li> at most one <code>Namespace</code> element with the name 
   *	     <code>DOC</code> for document-specific entities
   *	<li> at most one <code>Namespace</code> element with the name 
   *	     <code>LOC</code> for location-specific entities shared by
   *	     all children of a container.
   *</ul>
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
   * @param out the destination for the report. 
   */
  public void reportConfig(int depth, boolean total, Output out);

  /************************************************************************
  ** Metadata:
  ************************************************************************/

  /** Return any associated XML metadata (properties) as a Namespace.
   *	The <code>name</code> of the Namespace will be the same as
   *	the name of the Resource.
   */
  public PropertyMap getProperties();

  /** Write out any properties that have been changed, and check for 
   *	changes in the state of any underlying real resources. */
  public boolean synchronize();

  /** Returns the time that the resource was last modified. */
  public long getLastModified();

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
  public boolean exists();

  /** @return <code>true</code> if the Resource is a container. */
  public boolean isContainer();

  /** @return <code>true</code> if the resource is accessible as a local file
   *	or directory.
   */
  public boolean isLocal();

  /** Returns <code>true</code> if the resource is hidden. 
   *
   *<p>	Hidden documents can still be accessed through their Resource,
   *	but should not be exposed to clients which request the Resource
   *	by its URL.
   *
   * @return <code>true</code> if the associated document is hidden. */
  public boolean isHidden();

  /** Returns <code>true</code> if the resource is passive, i.e. is not 
   *	processed before being returned from a server.  All configuration
   *	information in a passive container is inherited from the last
   *	active ancestor.
   *
   *<p>	Passivity is dominantly inherited -- all of the descendents of an
   *	passive container are necessarily passive.
   *
   * @return <code>true</code> if the associated document is passive. */
  public boolean isPassive();

  /** Returns <code>true</code> if the resource is ``suspect'', i.e. must 
   *	only be processed by ``safe'' tagsets.
   *
   *<p>	Suspicion is dominantly inherited -- all of the descendents of an
   *	suspect container are necessarily suspect.
   *
   * @return <code>true</code> if the associated document is suspect. */
  public boolean isSuspect();

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. 
   *
   *<p>	A Resource that <em>is</em> a Document (i.e. implements the Document 
   *	interface) simply returns itself.  A container Resource will normally
   *	return, for example, its <code>index.html</code> document or the 
   *	equivalent. 
   */ 
  public Document getDocument();

  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Returns an array of strings naming the contents. 
   *
   * @return An array of strings naming the contents of this container.  The
   *	array will be empty if the container is empty. Returns
   *	<code>null</code> if this resource is not a container.
   */
  public String[] listNames();

  /** Returns an array of resources corresponding to the contents.  
   *
   * @return An array of Resource objects.  The array will be empty if the
   *	 container is empty.  Returns <code>null</code> if this resource is
   *	 not a container.
   */
  public Resource[] listResources();

  /************************************************************************
  ** Path Operations:
  ************************************************************************/

  /** Returns the path to this resource from the root of the resource tree. 
   *
   *<p> Note that, because a Resource corresponds to a URL, the names in the
   *	path are always separated by slash (<code>/</code>) characters.
   *
   * @return the path to this resource.  The Root's path is the only one that
   *	ends with a slash; all other resources have a path ending in their
   *	name.
   */
  public String getPath();

  /** Returns the path of the resource's parent, with a slash appended 
   *	if necessary.
   */
  public String getBasePath();

  /** Returns a Resource that corresponds to a (typically relative) path.
   *
   *<p>	If this resource is not a container, a relative path will be 
   *	taken relative to the container of this resource. 
   *
   * @param path a path, relative to either this Resource or (if it starts 
   *	with a slash) the root of the resource tree.
   */
  public Resource getRelative(String path);

  /** Locate a Resource that corresponds to a (typically relative) path.
   *
   *<p>	Unlike <code>getRelative</code>, which it resembles, 
   *	<code>locate</code> will search through a list of optional extensions,
   *	and will create a resource for output if necessary.
   *
   * @param path a path, relative to either this Resource or (if it starts 
   *	with a slash) the root of the resource tree.  If the path ends with
   *	a slash, a new child directory is created if necessary.
   * @param create if <code>true</code>, a missing resource will be created 
   *	even if it does not exist.  
   * @param extensions a list of extensions to try.  If <em>empty</em>, no
   *	extension processing will be done.  If <code>null</code>, the default
   *	list will be used. 
   */
  public Resource locate(String path, boolean create, List extensions);

  /** Locate a Resource that corresponds to a (typically relative) path.
   *
   *<p>	This version of <code>locate</code> gives the option of returning
   *	an ancestor of the requested document.
   *
   * @param path a path, relative to either this Resource or (if it starts 
   *	with a slash) the root of the resource tree.
   * @param code if positive, a missing resource will be created 
   *	even if it does not exist; if negative, an ancestor will be returned.
   * @param extensions a list of extensions to try.  If <em>empty</em>, no
   *	extension processing will be done.  If <code>null</code>, the default
   *	list will be used. 
   * @param tail a StringBuffer in which to return the remaining path segment
   *	if <code>code</code> is negative.
   */
  public Resource locate(String path, short code, List extensions,
			 StringBuffer tail);

  /************************************************************************
  ** Name and Type Mapping:
  ************************************************************************/

  /** Get the default extension list used by <code>locate</code>. */
  public List getDefaultExtensions(); 

  /** Map a document name to a corresponding file type. 
   *	The extension map uses the following ``pseudo-extensions'' for
   *	defaults:
   *
   *<ul> 
   *	<li> <code>"/"</code> for containers
   *	<li> <code>""</code> for documents with <em>no</em> extension
   *	<li> <code>"*"</code> for documents with an <em>unknown</em> extension.
   *</ul>
   */
  public String getContentTypeFor(String name);

  /** Map a document name to a corresponding tagset name. */
  public String getTagsetNameFor(String name);

  /** Load a tagset.  
   * @param name The tagset name.  If <code>null</code>, the Resource's
   *	default tagset is loaded.
   */
  public Tagset loadTagset(String name);

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete();

  public boolean rename(String name);

  /** Create a new child resource.
   *
   * @return the new Resource, or null if the given child cannot be created. 
   */
  public Resource create(String name, boolean container, boolean virtual, 
			 Element config);

  /** Configure a new resource. 
   *
   * @param name the name of the new Resource
   * @param parent its parent resource
   * @param config its configuration
   * @return the new resource.  This need not be the resource handling the
   *	call; in some cases it may be necessary to return an instance of
   *`	a different class.  
   */
  public Resource configure(String name, Resource parent, Element config);

}
