////// TreeEntity.java -- implementation of ActiveEntity
//	$Id: TreeEntity.java,v 1.2 1999-04-23 00:22:23 steve Exp $

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
 * @version $Id: TreeEntity.java,v 1.2 1999-04-23 00:22:23 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeEntity extends TreeValue implements ActiveEntity {

  // At most one of the following will return <code>this</code>:

  public ActiveEntity 	 asEntity() 	{ return this; }

  /************************************************************************
  ** Entity Interface:
  ************************************************************************/

  protected String publicId;
  protected String systemId;
  protected String notationName;

  /**
   * The public identifier associated with the entity, if specified. If the 
   * public identifier was not specified, this is <code>null</code>.
   */
  public String             getPublicId() { return publicId; }
  /**
   * The system identifier associated with the entity, if specified. If the 
   * system identifier was not specified, this is <code>null</code>.
   */
  public String             getSystemId() { return systemId; }
  /**
   * For unparsed entities, the name of the notation for the entity. For 
   * parsed entities, this is <code>null</code>. 
   */
  public String             getNotationName() { return notationName; }

  /************************************************************************
  ** ActiveEntity Interface:
  ************************************************************************/

  protected boolean isParameterEntity = false;
  public void setIsParameterEntity(boolean value) { isParameterEntity = value; }
  public boolean getIsParameterEntity() { return isParameterEntity; }

  public Output getValueOutput(Context cxt) {
    // === changes when value becomes children ===
    ToNodeList out = new ToNodeList();
    setValueNodes(out.getList());
    return out;
  }

  public void setValueNodes(Context cxt, ActiveNodeList v) {
    setValueNodes(v);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeEntity() {
    super(Node.ENTITY_NODE, "");
  }

  /** Note that this has to do a shallow copy */
  public TreeEntity(TreeEntity e, boolean copyChildren) {
    super(e, copyChildren);
    isParameterEntity = e.isParameterEntity;
  }

  /** Construct a node with given name. */
  public TreeEntity(String name) {
    super(Node.ENTITY_NODE, name);
  }

  /** Construct a character entity. */
  public TreeEntity(String name, char c) {
    super(Node.ENTITY_NODE, name, new TreeNodeList(new TreeText(c)));
    // === really needs additional flag ===
  }

  /** Construct a node with given data. */
  public TreeEntity(String name, ActiveNodeList value) {
    super(Node.ENTITY_NODE, name, value);
  }

  /** Construct a node with given handler. */
  public TreeEntity(String name, Handler handler) {
    super(Node.ENTITY_NODE, name, handler);
  }

  /** Construct a node with given data and handler. */
  public TreeEntity(String name, ActiveNodeList value, Handler handler) {
    super(Node.ENTITY_NODE, name, value, handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "  <ENTITY name='" + getName() + "'>";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getValueNodes().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "</ENTITY>\n";
  }


  /** Convert the Token to a String.
   *
   * === It's an interesting question whether to return name or value ===
   */
  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeEntity(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeEntity(this, true);
  }

}
