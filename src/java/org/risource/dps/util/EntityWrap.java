////// EntityWrap.java -- implementation of ActiveEntity
//	$Id: EntityWrap.java,v 1.4 1999-03-31 23:08:42 steve Exp $

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

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.Entity;

import org.risource.dps.active.*;
import org.risource.dps.*;
import org.risource.dps.util.Copy;

import org.risource.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that wraps an arbitrary 
 *	object.  This is intended for use in NamespaceWrap.
 *
 * @version $Id: EntityWrap.java,v 1.4 1999-03-31 23:08:42 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class EntityWrap extends ParseTreeEntity {

  /************************************************************************
  ** The wrapped Object:
  ************************************************************************/

  /** The object being wrapped. */
  protected Object wrappedObject = null;

  /** Retrieve the wrapped object. */
  public Object getWrappedObject() { return wrappedObject; }

  /** Set the wrapped object.  Wrap it, if possible. */
  public void setWrappedObject(Object o) {
    wrappedObject = o;
    if (o instanceof NodeList) {
      value = (NodeList)o;
      names = (o instanceof Namespace)? (Namespace) o : null;
    } else if (o instanceof ActiveNode) {
      value = new ParseNodeList((ActiveNode)o);
      names = (o instanceof Namespace)? (Namespace) o : null;
    } else if (o instanceof Tabular) {
      names = new NamespaceWrap(getName(), (Tabular)o);
      value = null;
    } else {
      value = null;
    }
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value. 
   *
   *	Eventually we may want a way to distinguish values stored in
   *	the children from values stored in a separate nodelist.
   */
  public NodeList getValueNodes() {
    if (value != null || wrappedObject == null) return value;
    // Have to wrap the object. 
    if (names != null) return new ParseNodeList(names.getBindings());
    return new ParseNodeList(new ParseTreeText(wrappedObject.toString()));
  }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValueNodes(NodeList newValue) {
    wrappedObject = newValue;
    super.setValueNodes(newValue);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public EntityWrap() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public EntityWrap(EntityWrap e, boolean copyChildren) {
    super(e, copyChildren);
    wrappedObject = e.getWrappedObject();
  }

  /** Construct a node with given name. */
  public EntityWrap(String name) {
    super(name);
  }

  /** Construct a node with given data. */
  public EntityWrap(String name, Object o) {
    super(name);
    setWrappedObject(o);
  }

  /** Construct a node with given handler. */
  public EntityWrap(String name, Handler handler) {
    super(name, handler);
  }

  /** Construct a node with given data and handler. */
  public EntityWrap(String name, Object o, Handler handler) {
    super(name, handler);
    setWrappedObject(o);
  }


  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new EntityWrap(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new EntityWrap(this, true);
  }
}
