////// EntityIndirect.java -- implementation of ActiveEntity
//	$Id: EntityIndirect.java,v 1.4 2001-04-03 00:04:37 steve Exp $

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

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.util.Copy;

import org.risource.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that <em>refers to</em>
 *	an item in a NamespaceWrap or other extended namespace.
 *
 * <p> Any attempt to change the entity's value results in a corresponding
 *	change in the associated Namespace.  EntityIndirect may be subclassed
 *	as needed to allow entity-like access to arbitrary data on an
 *	associated (extended) Namespace.
 *
 * @version $Id: EntityIndirect.java,v 1.4 2001-04-03 00:04:37 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Namespace
 * @see org.risource.dps.namespace.NamespaceWrap
 * @see org.risource.dps.namespace.AttrIndirect
 * @see org.risource.dps.active.ActiveEntity
 */
public class EntityIndirect extends TreeEntity {

  /************************************************************************
  ** The wrapped Object:
  ************************************************************************/

  /** The namespace in which the entity resides. */
  protected Namespace namespace;

  /** The underlying Tabular object in which the unwrapped value resides. */
  protected Tabular table;

  /** The name of an underlying Attribute. */
  protected String attr;

  /** The namespace cast as an ActiveElement. */
  protected ActiveElement element; 

  /** Retrieve the wrapped object. */
  public Object getWrappedObject() { return table.get(getName()); }

  /************************************************************************
  ** Access to Fields:
  ************************************************************************/

  public String getAttrName() { return attr; }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value. 
   */
  public ActiveNodeList getValueNodes() {
    if (table == null) {
      if (attr == null) return namespace.getValueNodes(null, getName());
      else 		return element.getAttributeValue(attr);
    }
    Object o = getWrappedObject();
    if (o == null) return null;
    if (o instanceof ActiveNodeList) return (ActiveNodeList) o;
    if (o instanceof ActiveNode) return new TreeNodeList((ActiveNode)o);
    return new TreeNodeList(new TreeText(o.toString()));
  }

  public void setValueNodes(ActiveNodeList newValue) {
    if (table != null) table.put(getName(), newValue);
    else if (attr != null) element.setAttributeValue(attr, newValue);
    else namespace.setValueNodes(null, getName(), newValue);
  }

  /* === only needed during debugging.
  public String startString() {
    return "<BIND name='" + getName() + "' indirect='true'>";
  }
  */
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public EntityIndirect() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public EntityIndirect(EntityIndirect e, boolean copyChildren) {
    super(e, false);
    namespace = e.namespace;
    table = e.table;
    attr = e.attr;
    element = e.element;
  }

  /** Construct a node with given data. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl) {
    super(name);
    namespace = ns;
    table = tbl;
    attr = null;
    element = null;
  }

  /** Construct a node with given data. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl, String aname) {
    super(name);
    namespace = ns;
    table = tbl;
    attr = aname;
    element = (ActiveElement)ns;
  }

  /** Construct a node with given data and handler. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl, String aname,
			Handler handler) {
    super(name, handler);
    namespace = ns;
    table = tbl;
    attr = aname;
    element = (ActiveElement)ns;
  }

  /** Construct a node with given data and handler. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl,
			Handler handler) {
    super(name, handler);
    namespace = ns;
    table = tbl;
    attr = null;
    element = null;
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.
   */
  public ActiveNode shallowCopy() {
    return new EntityIndirect(this, false);
  }

  /** Return a deep copy of this Node.  Don't copy children because they
   *	aren't really there: the value is obtained specially.
   */
  public ActiveNode deepCopy() {
    return new EntityIndirect(this, false);
  }
}
