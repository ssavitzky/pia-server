////// ParseTreeEntity.java -- implementation of ActiveEntity
//	$Id: ParseTreeEntity.java,v 1.4 1999-03-27 01:28:31 steve Exp $

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


package org.risource.dps.active;

import org.risource.dom.Node;
import org.risource.dom.NodeList;

import org.risource.dom.Entity;

import org.risource.dps.*;
import org.risource.dps.input.FromParseNodes;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActiveEntity interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: ParseTreeEntity.java,v 1.4 1999-03-27 01:28:31 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class ParseTreeEntity extends ParseTreeNamed implements ActiveEntity {

  // At most one of the following will return <code>this</code>:

  public ActiveEntity 	 asEntity() 	{ return this; }

  /************************************************************************
  ** Entity Interface:
  ************************************************************************/

  public int getNodeType() { return NodeType.ENTITY; }

  protected boolean isParameterEntity = false;
  public void setIsParameterEntity(boolean value) { isParameterEntity = value; }
  public boolean getIsParameterEntity() { return isParameterEntity; }

  public Input getValueInput(Context cxt) {
    return new FromParseNodes(getValueNodes(cxt));
  }

  public Output getValueOutput(Context cxt) {
    // === changes when value becomes children ===
    ToNodeList out = new ToNodeList();
    setValue(out.getList());
    return out;
  }

  public NodeList getValueNodes(Context cxt) {
    return getValue();
  }

  public void setValueNodes(Context cxt, NodeList v) {
    // === changes when value becomes children ===
    setValue(v);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeEntity() {
    super("");
  }

  /** Note that this has to do a shallow copy */
  public ParseTreeEntity(ParseTreeEntity e, boolean copyChildren) {
    super(e, copyChildren);
    isParameterEntity = e.isParameterEntity;
  }

  /** Construct a node with given name. */
  public ParseTreeEntity(String name) {
    super(name);
  }

  /** Construct a character entity. */
  public ParseTreeEntity(String name, char c) {
    super(name, new ParseNodeList(new ParseTreeText(c)));
    // === really needs additional flag ===
  }

  /** Construct a node with given data. */
  public ParseTreeEntity(String name, NodeList value) {
    super(name, value);
  }

  /** Construct a node with given handler. */
  public ParseTreeEntity(String name, Handler handler) {
    super(name);
    setHandler(handler);
  }

  /** Construct a node with given data and handler. */
  public ParseTreeEntity(String name, NodeList value, Handler handler) {
    super(name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "&";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getName();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ";";
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
    return new ParseTreeEntity(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeEntity(this, true);
  }

}
