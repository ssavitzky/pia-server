////// PropertiesWrap.java: Wrap a Properties or Tabular as a Namespace
//	$Id: PropertiesWrap.java,v 1.3 2001-01-11 23:37:29 steve Exp $

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
import org.risource.dps.Namespace;
import org.risource.dps.input.FromNamespace;
import org.risource.dps.output.ToString;
import org.risource.dps.util.Copy;

import java.util.Enumeration;
import java.util.Properties;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

/**
 * Make a Properties or Tabular implementation look like a Namespace.
 *
 * <p> A PropertiesWrap is a NamespaceWrap that always maps names to Strings. 
 *	It is essentially a wrapper for the Java class java.util.Properties,
 *	and in fact can be used to map one. 
 *
 * <p> PropertiesWrap must not be confused with PropertyTable: the keys
 *	of a PropertiesWrap are not required to be valid tagnames.
 *
 * @version $Id: PropertiesWrap.java,v 1.3 2001-01-11 23:37:29 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.Namespace
 * @see org.risource.dps.NamespaceWrap
 * @see org.risource.dps.PropertyTable
 * @see org.risource.ds.Tabular
 * @see java.util.Properties
 */

public class PropertiesWrap extends NamespaceWrap {

  /************************************************************************
  ** Data:
  ************************************************************************/

  protected Properties propsByName = null;

  /************************************************************************
  ** Tabular Operations:
  ************************************************************************/

  /** Access an individual string item by name. */
  public String getProperty(String key) {
    return (propsByName != null)? propsByName.getProperty(key)
      : itemsByName.get(key).toString();
  }

  /** Access an individual item by name. */
  public Object get(String key) { return getProperty(key); }

  /** Replace an individual named item with value <em>v</em>. */
  public void setProperty(String key, String v) { 
    if (propsByName != null) propsByName.put(key, v);
    else		     itemsByName.put(key, v); }

  /** Replace an individual named item with value <em>v</em>. */
  public void put(String key, Object v) { setProperty(key, v.toString()); }

  /** Return an enumeration of all the  keys. */
  public Enumeration keys() { 
    return (propsByName != null)? propsByName.keys() : itemsByName.keys();
  }


  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Wrap an object as a binding.  
   *	We know that the object is a String, which makes it easy.
   */
  public ActiveNode wrap(String name, Object o) {
    if (o == null) return null;
    ActiveNode n = new TreeText(o.toString());
    return new TreeEntity(name, new TreeNodeList(n));
  }

  /** Wrap an object as a NodeList.  
   */
  public ActiveNodeList wrapValue(Object o) {
    if (o == null) return null;
    ActiveNode n = new TreeText(o.toString());
    return new TreeNodeList(n);
  }

  /** Unwrap a binding as an Object (and in particular, a String). */
  public Object unwrap(ActiveNode binding) {
    if (binding == null) return null;
    if (binding instanceof ActiveElement) 
      return binding.getContent().toString();
    String s = binding.getNodeValue();
    return (s != null)? s : binding.toString();
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
   * <p> In our particular case we insist that the value be a string, 
   *	 so the "binding" we return is a complete fabrication.
   *
   * @return the new or updated binding.
   */
  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value) {
    setProperty(name, value.toString());
    return getBinding(name);
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
    put(name, unwrap(binding));
  }

  /************************************************************************
  ** ActiveNode operations:
  ************************************************************************/

  /** contentString has to explicitly iterate over the bindings because
   *	they are generated on-the-fly rather than being the children of
   *	the PropertiesWrap.
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

  public PropertiesWrap() { this("#Properties", new Properties()); }
  public PropertiesWrap(String name, Tabular t) { 
    this("#Table", name, t);
  }
  public PropertiesWrap(String name, Properties t) { 
    this("#Properties", name, t);
  }
  public PropertiesWrap(String tag, String name, Properties t) {
    super(tag, name, (Tabular)null);
    propsByName = t;
  }
  public PropertiesWrap(String tag, String name, Tabular t) {
    super(tag, name, t);
    itemsByName = t;
  }

}
