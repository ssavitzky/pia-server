////// ParseTreeComment.java -- implementation of ActiveComment
//	ParseTreeComment.java,v 1.9 1999/03/01 23:45:50 pgage Exp

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

import org.risource.dom.Comment;

import org.risource.dps.*;
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActiveComment interface, suitable for use in 
 *	DPS parse.
 *
 * @version ParseTreeComment.java,v 1.9 1999/03/01 23:45:50 pgage Exp
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.active.ActiveNode
 */
public class ParseTreeComment extends ParseTreeNode implements ActiveComment {

  /************************************************************************
  ** Comment Interface:
  ************************************************************************/

  public int getNodeType(){ return NodeType.COMMENT; }
  public void setData(String data){ this.data = data; }
  public String getData(){ return data; }


  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  String data;

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeComment() {
  }

  public ParseTreeComment(ParseTreeComment n, boolean copyChildren) {
    super(n, copyChildren);
    data = n.getData();
  }

  public ParseTreeComment(Comment n, boolean copyChildren) {
    super((ActiveComment)n, copyChildren);
    data = n.getData();
  }

  /** Construct a node with given data. */
  public ParseTreeComment(String data) {
    this.data = data;
  }

  /** Construct a node with given data and handler. */
  public ParseTreeComment(String data, Handler handler) {
    super(handler);
    this.data = data;
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<!-- ";
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
    return " -->";
  }


  /** Convert the Node to a String, in external form.
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
    return new ParseTreeComment(this, false);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeComment(this, true);
  }
 
}
