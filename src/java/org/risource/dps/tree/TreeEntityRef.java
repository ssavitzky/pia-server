////// TreeEntityRef.java -- implementation of ActiveEntity
//	$Id: TreeEntityRef.java,v 1.3 1999-06-04 22:40:42 steve Exp $

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


package org.risource.dps.tree;

import org.w3c.dom.*;

import org.risource.dps.active.*;

import org.risource.dps.*;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActiveEntity interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeEntityRef.java,v 1.3 1999-06-04 22:40:42 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeEntityRef extends TreeNode implements ActiveEntityRef {

  /************************************************************************
  ** Entity Interface:
  ************************************************************************/

  protected boolean isParameterEntity = false;
  public void setIsParameterEntity(boolean value) { isParameterEntity = value; }
  public boolean getIsParameterEntity() { return isParameterEntity; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeEntityRef() {
    super(Node.ENTITY_REFERENCE_NODE, "");
  }

  /** Note that this has to do a shallow copy */
  public TreeEntityRef(TreeEntityRef e, boolean copyChildren) {
    super(e, copyChildren);
    isParameterEntity = e.isParameterEntity;
  }

  /** Construct a node with given name. */
  public TreeEntityRef(String name) {
    super(Node.ENTITY_REFERENCE_NODE, name);
  }

  /** Construct a node with given handler. */
  public TreeEntityRef(String name, Handler handler) {
    super(Node.ENTITY_REFERENCE_NODE, name);
    setHandler(handler);
  }

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "&";
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getNodeName();
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ";";
  }


  /** Convert the Node to a String.
   *
   * === It's an interesting question whether to return name or value ===
   */
  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeEntityRef(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeEntityRef(this, true);
  }

}
