////// EntityIndirect.java -- implementation of ActiveEntity
//	$Id: EntityIndirect.java,v 1.1 1999-04-23 00:17:10 steve Exp $

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
 * @version $Id: EntityIndirect.java,v 1.1 1999-04-23 00:17:10 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Namespace
 * @see org.risource.dps.namespace.NamespaceWrap
 * @see org.risource.dps.namespace.EntityIndirect
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

  /** Retrieve the wrapped object. */
  public Object getWrappedObject() { return table.get(getName()); }


  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value. 
   */
  public ActiveNodeList getValueNodes() {
    if (table == null) return namespace.getValueNodes(null, getName());
    Object o = getWrappedObject();
    if (o instanceof ActiveNodeList) return (ActiveNodeList) o;
    if (o instanceof ActiveNode) return new TreeNodeList((ActiveNode)o);
    return new TreeNodeList(new TreeText(o.toString()));
  }

  public void setValueNodes(ActiveNodeList newValue) {
    if (table != null) table.put(getName(), newValue);
    else namespace.setValueNodes(null, getName(), newValue);
  }

  public String startString() {
    return "  <ENTITY name='" + getName() + "' indirect='true'>";
  }

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
  }

  /** Construct a node with given data. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl) {
    super(name);
    namespace = ns;
    table = tbl;
  }

  /** Construct a node with given handler. */
  public EntityIndirect(String name, Namespace ns, Tabular tbl, Handler handler) {
    super(name, handler);
    namespace = ns;
    table = tbl;
  }


  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new EntityIndirect(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new EntityIndirect(this, false);
  }
}
