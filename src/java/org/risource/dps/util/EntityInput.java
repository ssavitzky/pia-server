////// EntityInput.java -- Wrapper for arbitrary input.
//	$Id: EntityInput.java,v 1.7 1999-07-14 20:21:22 steve Exp $

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

import java.io.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Entity;

import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.*;
import org.risource.dps.util.Copy;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.output.ToNodeList;

import org.risource.ds.Tabular;

/**
 * An implementation of the ActiveEntity interface that wraps an arbitrary 
 *	Input object.  Sometimes it will be a Parser looking at a file, but
 *	it is more likely to be used to lazily-evaluate the content of an
 *	active node (i.e. as the value of <code>&amp;content;</code>).
 *
 * @version $Id: EntityInput.java,v 1.7 1999-07-14 20:21:22 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class EntityInput extends TreeEntity {

  /************************************************************************
  ** The wrapped Object:
  ************************************************************************/

  /** The Input being wrapped. */
  protected Input wrappedInput = null;

  /** Retrieve the wrapped object. */
  public Input getWrappedInput() { return wrappedInput; }

  /** Set the wrapped object.  Wrap it, if possible. */
  public void setWrappedInput(Input in) {
    wrappedInput = in;
  }

  /************************************************************************
  ** Access to Value:
  ************************************************************************/

  /** Get the node's value as an Input. 
   */
  public Input fromValue(Context cxt) { 
    if (nodeValue != null) return new FromNodeList(getValueNodes(cxt));
    return getWrappedInput();
  }

  /** Get the node's value. 
   *
   * <p> There will be problems if this is called while reading the value.
   */
  public ActiveNodeList getValueNodes(Context cxt) {
    if (nodeValue != null || wrappedInput == null) return nodeValue;
    ToNodeList out = new ToNodeList(null); // === possibly bogus
    Input in = fromValue(cxt);
    Copy.copyNodes(in, out);
    nodeValue = out.getList();
    wrappedInput = null;
    return nodeValue;
  }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValueNodes(Context cxt, ActiveNodeList newValue) {
    super.setValueNodes(cxt, newValue);
    wrappedInput = null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public EntityInput() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public EntityInput(EntityInput e, boolean copyChildren) {
    super(e, copyChildren);
    setValueNodes(e.getValueNodes(null));
  }

  /** Construct a node with given name. */
  public EntityInput(String name) {
    super(name);
  }

  /** Construct a node with given data. */
  public EntityInput(String name, Input in) {
    super(name);
    setWrappedInput(in);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new EntityInput(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new EntityInput(this, true);
  }
}
