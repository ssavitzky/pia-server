////// ParseTreeAttribute.java -- implementation of ActiveAttribute
//	$Id: ParseTreeAttribute.java,v 1.4 1999-03-31 23:08:16 steve Exp $

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

import org.risource.dom.Attribute;

import org.risource.dps.*;

/**
 * An implementation of the ActiveAttribute interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id: ParseTreeAttribute.java,v 1.4 1999-03-31 23:08:16 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class ParseTreeAttribute extends ParseTreeNamed
	implements ActiveAttribute
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected boolean specified = false;

  /************************************************************************
  ** Attribute interface:
  ************************************************************************/

  public int getNodeType() { return NodeType.ATTRIBUTE; }

  public void setValueNodes(NodeList value){
    setIsAssigned( true );
    setSpecified(value != null);
    super.setValueNodes(value);
  }

  public void setSpecified(boolean specified){ this.specified = specified; }
  public boolean getSpecified(){return specified;}


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public ActiveAttribute asAttribute() 	{ return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeAttribute() {
    super();
  }

  public ParseTreeAttribute(ParseTreeAttribute e, boolean copyChildren) {
    super(e, copyChildren);
    specified = e.specified;
  }

  /** Construct a node with given data. */
  public ParseTreeAttribute(String name, NodeList value) {
    super(name, value);
    
    // explicitly assigned value
    setIsAssigned( true );
    setSpecified( value != null );
  }

  /** Construct a node with given data and handler. */
  public ParseTreeAttribute(String name, NodeList value, Handler handler) {
    super(name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  public String startString() {
    return getName() + ((! getSpecified() || getValue() == null)? "" : "=");
  }

  public String contentString() {
    return (! getSpecified() || getValue() == null)
      ? ""
      : "'" + getValue() + "'";
  }

  public String endString() {
    return "";
  }


  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token. 
   *	Since an attribute's value is kept in its children, we actually
   *	need to do a deep copy.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeAttribute(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeAttribute(this, true);
  }

}
