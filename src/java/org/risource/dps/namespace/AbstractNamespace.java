////// AbstractNamespace.java: Node Lookup Table
//	$Id: AbstractNamespace.java,v 1.2 1999-04-30 23:37:05 steve Exp $

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
 * The abstract base class for a Namespace -- a lookup table for Nodes 
 *
 * <p> AbstractNamespace descends from TreeElement, so it can be saved and
 *	restored as an XML data stream.
 *
 *
 * @version $Id: AbstractNamespace.java,v 1.2 1999-04-30 23:37:05 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.tree.TreeElement
 * @see org.risource.dps.Input 
 */

public abstract class AbstractNamespace extends TreeGeneric
  implements Namespace
{
  /************************************************************************
  ** Data:
  ************************************************************************/

  protected boolean 		caseSensitive  	= true;
  protected boolean 		lowerCase	= true;
  protected int     		namespaceItems	= 0;

  /************************************************************************
  ** Tabular Operations:
  ************************************************************************/

  // === not clear we want these! ===

  /** Access an individual item by name. */
  public Object get(String key) { return getBinding(key); }

  /** Replace an individual named item with value <em>v</em>. */
  public void put(String key, Object v) { 
    setBinding(key, (ActiveNode)v);
  }

  /** Return an enumeration of all the  keys. */
  public Enumeration keys() { return getNames(); }


  /************************************************************************
  ** Basic Operations:
  ************************************************************************/

  /** Look up a name and get a (local) binding. */
  public abstract ActiveNode getBinding(String name);

  /** Add a new local binding.  Assumes that the name has already been
   *	cannonized if necessary.   Can be useful for initialization.
   */
  protected abstract void addBinding(String name, ActiveNode binding);

  /** Remove a binding. */
  protected abstract void removeBinding(String name, ActiveNode binding);

  /** Replace an existing binding. */
  protected abstract void replaceBinding(String name, ActiveNode old,
					 ActiveNode binding);

  /** Update an existing binding of which the value may have changed. */
  protected void updateBinding(String name, ActiveNode binding) {
    // nothing to do, but subclasses may override.
  }

  /** Update the value of an existing binding. */
  protected void updateValue(String name, ActiveNode binding,
			     Context cxt, ActiveNodeList value) {
      binding.setValueNodes(cxt, value);
      updateBinding(name, binding);
  }

  /** Create a new binding with a given value. */
  protected void newBinding(Context cxt, String name, ActiveNodeList value) {
      ActiveNode binding
	= cxt.getTopContext().getTagset().createActiveEntity(name, value);
      addBinding(name, binding);
  }

  /************************************************************************
  ** Derived Operations:
  ************************************************************************/

  /** Add a new local binding or replace an existing one. */
  public ActiveNode setBinding(String name, ActiveNode binding) {
    if (!caseSensitive) name = cannonizeName(name);
    ActiveNode old = getBinding(name);
    if (binding == null) {	// We are removing an old binding
      if (old == null) {	// ... but there wasn't one.  Nothing to do.
      } else {			// remove from:
	removeBinding(name, old);
      } 
    } else if (old == null) {	// We are adding a new binding.  Easy.
      addBinding(name, binding);
    } else if (old == binding) { // We are replacing a binding by itself
      updateBinding(name, binding);
    } else {			// We are replacing an old binding.
      replaceBinding(name, old, binding);

      // adjust namespaceItems if necessary:
      if (old instanceof Namespace) {
	if (binding == null || !(binding instanceof Namespace))
	  namespaceItems --;
      } else {
	if (binding != null && binding instanceof Namespace) 
	  namespaceItems ++;
      }
    }
    return old;
  }

  /** Set the value for a given name. */
  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value) {
    ActiveNode binding = getBinding(name);
    if (binding != null) {
      updateValue(name, binding, cxt, value);
    } else {
      if (!caseSensitive) name = cannonizeName(name);
      newBinding(cxt, name, value);
    } 
    return binding;
  }

  /** Obtain the <em>value of</em> an existing binding. 
   *
   * <p> This function allows a specialized Namespace to construct a value 
   *	 rather than simply retrieving it from an existing binding.
   *
   * @return the value.
   */
  public ActiveNodeList getValueNodes(Context cxt, String name) {
    ActiveNode binding = getBinding(name);
    return (binding == null)? null : binding.getValueNodes(cxt);
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns this object as a Namespace. */
  public Namespace asNamespace() { return this; }

  /** Returns the bindings defined in this table. */
  public Input getBindings() {
    return new FromNamespace(this);
  }

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public abstract Enumeration getNames();

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

  /************************************************************************
  ** ActiveNode Operations:
  ************************************************************************/

  public ActiveNodeList getValueNodes(Context cxt) {
    return new TreeNodeList((ActiveNode)this);
  }

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.  Special
   *	hackery for wrapping excessively-long attribute lists. 
   */
  public String startString() {
    String s = "<" + (nodeName == null ? "" : nodeName);
    ActiveAttrList attrs = getAttrList();
    int margin = 3 + nodeName.length();
    int col = margin; 
    if (attrs != null && attrs.getLength() > 0) {
      for (int i = 0; i < attrs.getLength(); ++i) {
	ActiveAttr at = (ActiveAttr)attrs.asNodeList().activeItem(i);
	if (!at.getSpecified() || at.getValueNodes() == null) continue;
	String it = at.toString();
	if (col + 1 + it.length() < 72) {
	  s += " " + it;
	  col += 1 + it.length();
	} else {
	  s += "\n";
	  for (col = 0; col < margin; col++) s += " ";
	  s += it;
	  col += it.length();
	}	    
      }
    }
    if (getName() != null && getAttribute("name") == null) 
      s += " name='" + getName() + "'";
    if (hasEmptyDelimiter()) s += " /";
    s += ">";
    // if (!mixedContent && hasChildNodes()) s += "\n";
    return s;
  }

  public String contentString() {
    String s = "\n";
    for (ActiveNode n = getFirstActive(); n != null; n = n.getNextActive()) {
      EntityIndirect e =
	(n instanceof EntityIndirect)? (EntityIndirect)n : null;
      if (e != null && e.getValueNodes() == null) continue;
      if (e != null && e.getAttrName() != null)      continue;
      s += "  " + n.toString() + "\n";
    }
    return s;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public AbstractNamespace() { this("NAMESPACE", null); }
  public AbstractNamespace(String tag, String name) {
    super(tag); 
    setName(name);
    setAttribute("name", name);
    setMixedContent(false);
  }

}
