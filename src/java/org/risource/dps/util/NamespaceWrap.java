////// NamespaceWrap.java: Wrap a Tabular as a Namespace
//	$Id: NamespaceWrap.java,v 1.7 1999-04-17 01:20:00 steve Exp $

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

package org.risource.dps.util;

import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.*;
import org.risource.dps.Namespace;
import org.risource.dps.input.FromNodeList;

import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

/**
 * Make a Tabular implementation look like a Namespace.
 *
 * ===	This could be simplified considerably if we just permitted strings,
 *	Tabular, and nodelists as values, and faked everything else.
 *
 * ===	The implementation is crude, and will probably want to be revisited. ===
 * ===	We may want to insist that NamespaceWrap implement Entity.
 *
 * @version $Id: NamespaceWrap.java,v 1.7 1999-04-17 01:20:00 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Namespace
 * @see org.risource.ds.Tabular
 */

public class NamespaceWrap extends TreeGeneric implements Namespace {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Tabular itemsByName	   = null;
  protected boolean caseSensitive  = true;
  protected boolean lowerCase	   = true; // ignored if caseSensitive
  protected int     namespaceItems = 0;

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Wrap an object as a binding.  This would work better if we had
   *	some kind of EntityWrap to go with NamespaceWrap.
   */
  public ActiveNode wrap(String name, Object o) {
    if (o == null) return null;
    if (o instanceof ActiveNode) return (ActiveNode)o;
    if (o instanceof Tabular) return new NamespaceWrap(name, (Tabular)o);
    if (o instanceof ActiveNodeList)
      return new TreeEntity(name, (ActiveNodeList)o);

    // Have to make a wrapper. === should be EntityWrap
    ActiveNode n = new TreeText(o.toString());
    return new TreeEntity(name, new TreeNodeList(n));
  }

  /** Unwrap a binding as an Object. */
  public Object unwrap(ActiveNode binding) {
    if (binding == null) return null;
    if (binding instanceof TreeEntity) 
      return ((TreeEntity)binding).getValueNodes(null);
    return binding;
  }

  /** Look up a name and get a (local) binding. */
  public ActiveNode getBinding(String name) {
    if (!caseSensitive) name = cannonizeName(name);
    return wrap(name, itemsByName.get(name));
  }

  /** Add a new local binding or replace an existing one. 
   *	=== In the absence of entity wrappers this is almost certainly wrong
   */
  public ActiveNode setBinding(String name, ActiveNode binding) {
    if (!caseSensitive) name = cannonizeName(name);
    ActiveNode old = getBinding(name);
    if (binding == null) {	// We are removing an old binding
      if (old == null) {	// ... but there wasn't one.  Nothing to do.
      } else try {		// ... so remove from:
	itemsByName.put(name, null); // ... hash table
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      } 
    } else if (old == null) {	// We are adding a new binding.  Easy.
      addBinding(name, binding);
    } else try {		// We are replacing an old binding.
      itemsByName.put(name, unwrap(binding));

      // adjust namespaceItems if necessary:
      if (old instanceof Namespace) {
	if (binding == null || !(binding instanceof Namespace))
	  namespaceItems --;
      } else {
	if (binding != null && binding instanceof Namespace) 
	  namespaceItems ++;
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return old;
  }


  /** Add a new local binding.  Assumes that the name has already been
   *	cannonized if necessary.   Can be useful for initialization.
   */
  protected final void addBinding(String name, ActiveNode binding) {
    if (binding instanceof Namespace) namespaceItems ++;
    itemsByName.put(name, unwrap(binding));
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns the bindings defined in this table.
   */
  public Input getBindings() {
    // === should implement this with a NodeEnumerator that wraps each value.
    Enumeration enum = getNames();
    TreeNodeArray list = new TreeNodeArray();
    while(enum.hasMoreElements()) {
      String name = enum.nextElement().toString();
      list.append(wrap(name, getBinding(name)));
    }
    return new FromNodeList(list);
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames() { 
    return itemsByName.keys();
  }

  /** Returns <code>true</code> if names are case-sensitive. */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /** Convert a name to cannonical case. */
  public String cannonizeName(String name) {
    return (!caseSensitive)? name
      : lowerCase? name.toLowerCase() : name.toUpperCase();
  }

  /** Returns <code>true</code> if any of the bindings in the Namespace 
   *	implement the Namespace interface themselves. */
  public boolean containsNamespaces() {
    return namespaceItems > 0;
  }

  public Namespace asNamespace() { return this; }

  public String getName()		{ return name; }
  public void setName(String n) 	{ name = n; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public NamespaceWrap() { this("Namespace", new Table()); }
  public NamespaceWrap(String name, Tabular t) {
    setName(name);
    itemsByName = t;
  }

}
