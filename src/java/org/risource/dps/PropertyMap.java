////// PropertyMap.java: Property Lookup Table interface
//	$Id: PropertyMap.java,v 1.2 2001-01-11 23:37:07 steve Exp $

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

import org.risource.ds.Tabular;
import org.risource.dps.active.*;

/**
 * The interface for a PropertyMap -- a lookup table for properties. 
 *
 * <p> This interface is used to store ``properties'' as defined in, for 
 *	example, the Site Resource Package or WebDAV.  Properties can be
 *	obtained as either nodelists, nodes, objects, or strings. 
 *
 * <p>	A PropertyMap uses Element nodes for its bindings.  Elements
 *	with <code>name</code> or <code>ID</code> attributes are stored
 *	in a child Namespace, while others are used directly as bindings.
 *
 * @version $Id: PropertyMap.java,v 1.2 2001-01-11 23:37:07 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.site
 */

public interface PropertyMap extends Namespace, Tabular {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return its value.
   *
   * @param name the name to look up
   * @return the list of nodes associated with the name.  If the binding
   *	is an Element (the typical case) its children are returned.
   */
  public ActiveNodeList getValueNodes(String name);

  /** Set the value of a name.
   *
   *<p>	If the name is not present, a new Element is created with the
   *	given name as its tagname, and the given nodelist becomes its content.
   *
   * @param name the name to bind
   * @param value the value to associate with it.
   */
  public void setValueNodes(String name, ActiveNodeList value);

  /** Bind an Element that represents a property.
   *
   *<p> The exact behavior depends on the element's attributes.  If it has a
   *	<code>name</code> or <code>ID</code> attribute, it is associated with
   *	that name in a Namespace binding, which in turn is associated with
   *	the Element's tagname.  Otherwise, it is simply associated with the 
   *	Element's tagname, just like a NamedNodeMap.
   *
   * @param binding the element to store.
   * @see org.w3c.dom.NamedNodeMap
   */
  public void setPropertyBinding(ActiveElement binding);

  /** Bind a named Element within a map that represents a property.
   *
   * @param binding the element to store.
   * @see org.w3c.dom.NamedNodeMap
   */
  public void setNamedBinding(String mapName, String name,
			      ActiveElement binding);

  /** Look up a name and return its value as a String. 
   *
   * @param name the name to look up
   */
  public String	getProperty(String name);

  /** Look up a name and return its value as a String, with default.
   *
   * @param name the name to look up
   * @param dflt the default value if the name is not found.
   */
  public String getProperty(String name, String dflt);

}
