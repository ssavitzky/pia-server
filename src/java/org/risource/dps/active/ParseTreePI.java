////// ParseTreePI.java -- implementation of ActivePI
//	$Id: ParseTreePI.java,v 1.4 1999-03-27 01:28:33 steve Exp $

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
import org.risource.dom.PI;

import org.risource.dps.*;
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActivePI interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: ParseTreePI.java,v 1.4 1999-03-27 01:28:33 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class ParseTreePI extends ParseTreeNamed implements ActivePI {

  /************************************************************************
  ** PI interface:
  ************************************************************************/

  String data = "";

  public void setData(String data) { this.data = data; }
  public String getData() 	   { return data; }

  public int getNodeType(){ return NodeType.PI; }
  
  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // At most one of the following will return <code>this</code>:

  public ActivePI	 asPI() { return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreePI() {
  }

  public ParseTreePI(ParseTreePI e, boolean copyChildren) {
    super(e, copyChildren);
    setData(e.getData());
  }

  /** Construct a node with given data. */
  public ParseTreePI(String name, String data) {
    super(name);
    setData(data);
  }

  /** Construct a node with given data and handler. */
  public ParseTreePI(String name, String data, Handler handler) {
    super(name, handler);
    setData(data);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<?" + getName() + " ";
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ">";
  }


  /** Convert the Token to a String.
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
    return new ParseTreePI(this, false);
  }

}
