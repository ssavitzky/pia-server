////// BasicNamespace.java: Node Lookup Table
//	$Id: BasicNamespace.java,v 1.3 1999-10-11 21:42:13 steve Exp $

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

package org.risource.dps.namespace;

import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.*;
import org.risource.dps.input.FromNamespace;

import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;

/**
 * The basic implementation for a Namespace -- a lookup table for Nodes 
 *
 * <p> BasicNamespace descends from TreeElement, so it can be saved and
 *	restored as an XML data stream.
 *
 *
 * @version $Id: BasicNamespace.java,v 1.3 1999-10-11 21:42:13 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.tree.TreeElement
 * @see org.risource.dps.Input 
 */

public class BasicNamespace extends AbstractNamespace {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected ActiveNodeMap	bindings	= new TreeNodeMap();
  protected List    		itemNames 	= new List();


  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and get a (local) binding. */
  public ActiveNode getBinding(String name) {
    if (!caseSensitive) name = cannonizeName(name);
    return bindings.getBinding(name);
  }

  /** Add a new local binding.  Assumes that the name has already been
   *	cannonized if necessary.   Can be useful for initialization.
   */
  protected void addBinding(String name, ActiveNode binding) {
    bindings.setBinding(name, binding);
    itemNames.push(name);
    if (binding.getParentNode() != null) addChild(binding);
    if (binding.asNamespace() != null) namespaceItems ++;
  }

  /** Remove a binding. */
  protected void removeBinding(String name, ActiveNode binding) {
    bindings.removeNamedItem(name); // ... hash table
    itemNames.remove(name);	// ... name list
    if (binding.getParentNode() == this) removeChild(binding);	// ... children
  }

  /** Replace an existing binding. */
  protected void replaceBinding(String name, ActiveNode old,
				ActiveNode binding) {
    bindings.setBinding(name, binding); // ... in hash table.  Name stays.
    if (old.getParentNode() == this) {  // ... in children
      if (binding.getParentNode() != null) {
	removeChild(binding);
      } else {
	replaceChild(old, binding); 
      }
    } else if (binding.getParentNode() != null) {
      addChild(binding);
    }    
    // adjust namespaceItems if necessary:
    if (old.asNamespace() != null) {
      if (binding == null || !(binding.asNamespace() != null))
	namespaceItems --;
    } else {
      if (binding != null && binding.asNamespace() != null) 
	namespaceItems ++;
    }
  }

  /** Update an existing binding of which the value may have changed. 
   */
  protected void updateBinding(String name, ActiveNode binding) {
    // nothing to do, but subclasses may override.
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames() { 
    return itemNames.elements();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicNamespace() 		{ this("namespace", null); }
  public BasicNamespace(String name) 	{ this("namespace", name); }
  public BasicNamespace(String tag, String name) { super(tag, name); }

}
