////// NamespaceWrap.java: Wrap a Tabular as a Namespace
//	$Id: NamespaceWrap.java,v 1.8 2001-04-03 00:04:38 steve Exp $

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

package org.risource.dps.namespace;

import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.*;
import org.risource.dps.Namespace;
import org.risource.dps.input.FromNamespace;
import org.risource.dps.output.ToString;
import org.risource.dps.util.Copy;

import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

/**
 * Make a Tabular implementation look like a Namespace.
 *
 * <p> Note that a NamespaceWrap <em>implements</em> Tabular as well;
 *	it simply <em>delegates</em> the Tabular operations to a Tabular
 *	called <code>itemsByName</code>.  The Namespace operations, in turn,
 *	use only these delegated operations.
 *
 * <p> It is easy to extend an existing Tabular implementation with no parent
 *	class into a NamespaceWrap by simply making it extend NamespaceWrap.  
 *	A Tabular implementation with a parent can be wrapped in the usual
 *	way by assigning it to <code>itemsByName</code> on initialization.
 *
 * ===	The implementation is crude, and will probably want to be revisited. ===
 * ===	We may want to insist that NamespaceWrap implement Entity.
 *
 * @version $Id: NamespaceWrap.java,v 1.8 2001-04-03 00:04:38 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.Namespace
 * @see org.risource.ds.Tabular */

public class NamespaceWrap extends AbstractNamespace implements Tabular {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Tabular itemsByName	   = null;

  /************************************************************************
  ** Tabular Operations:
  ************************************************************************/

  /** Access an individual item by name. */
  public Object get(String key) {return itemsByName.get(key); }

  /** Replace an individual named item with value <em>v</em>. */
  public void put(String key, Object v) { itemsByName.put(key, v); }

  /** Return an enumeration of all the  keys. */
  public Enumeration keys() { return itemsByName.keys(); }


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

  /** Wrap an object as a NodeList.  
   */
  public ActiveNodeList wrapValue(Object o) {
    if (o == null) return null;
    if (o instanceof ActiveNodeList) return (ActiveNodeList)o;
    if (o instanceof ActiveNode) return new TreeNodeList((ActiveNode)o);
    if (o instanceof Tabular)
      return new TreeNodeList(new NamespaceWrap(name, (Tabular)o));
    ActiveNode n = new TreeText(o.toString());
    return new TreeNodeList(n);
  }

  /** Unwrap a binding as an Object. */
  public Object unwrap(ActiveNode binding) {
    if (binding == null) return null;
    if (binding instanceof TreeExternal) return binding; //preserve external refs
    if (binding instanceof ActiveValue) 
      return ((ActiveValue)binding).getValueNodes();
    return binding;
  }

  /** Look up a name and get a (local) binding. */
  public ActiveNode getBinding(String name) {
    if (!caseSensitive) name = cannonizeName(name);
    return wrap(name, get(name));
  }

  /** Remove a binding. */
  protected void removeBinding(String name, ActiveNode binding) {
    put(name, null);		 // ... hash table
  }

  /** Replace an existing binding. */
  protected void replaceBinding(String name, ActiveNode old,
				ActiveNode binding) {
    put(name, unwrap(binding));
  }


  /** Add a new binding or replace the <em>value of</em> an existing one. 
   *
   * <p> The new value will be copied if necessary.  This function allows a
   *	 specialized Namespace to construct a binding of the proper type, and
   *	 (in the present case) perform any associated updating that may be 
   *	 needed.
   *
   * @return the new or updated binding.
   */
  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value) {
    ActiveNode binding = getBinding(name);
    if (binding != null) {
      binding.setValueNodes(cxt, value); 
      put(name, unwrap(binding));
    } else {
      binding = bind(cxt, name, value);
      setBinding(name, binding);
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
    return wrapValue(get(name));
  }

  /** Add a new local binding.  Assumes that the name has already been
   *	cannonized if necessary.   Can be useful for initialization.
   */
  protected void addBinding(String name, ActiveNode binding) {
    if (binding instanceof Namespace) namespaceItems ++;
    put(name, unwrap(binding));
  }

  /** Create a new binding node for a value. */
  protected ActiveNode bind(Context cxt, String name, ActiveNodeList value) {
    return cxt.getTopContext().getTagset().createActiveEntity(name, value);
  }

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration getNames() { 
    return keys();
  }


  /************************************************************************
  ** ActiveNode operations:
  ************************************************************************/

  /** contentString has to explicitly iterate over the bindings because
   *	they are generated on-the-fly rather than being the children of
   *	the NamespaceWrap.
   */
  public String contentString() {
    String s = "\n";
    Input in = getBindings();
    do {
      org.w3c.dom.Node n = in.getNode();
      if (n == null) break;
      s += " " + n.toString() + "\n"; 
    } while(in.toNext());
    return s;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public NamespaceWrap() { this("#Table", new Table()); }
  public NamespaceWrap(String name, Tabular t) { 
    this("#Table", name, t);
  }
  public NamespaceWrap(String tag, String name, Tabular t) {
    super(tag, name);
    itemsByName = t;
  }

}
