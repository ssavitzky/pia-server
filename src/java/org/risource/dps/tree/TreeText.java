////// TreeText.java -- implementation of ActiveText
//	$Id: TreeText.java,v 1.2 1999-06-04 22:40:46 steve Exp $

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
import org.risource.dps.util.*;

/**
 * An implementation of the ActiveText interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id: TreeText.java,v 1.2 1999-06-04 22:40:46 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Context
 * @see org.risource.dps.Processor
 */
public class TreeText extends TreeCharData implements ActiveText {

  public Text splitText(int offset) {
    throw new DPSException(DOMException.NOT_SUPPORTED_ERR,
			   "splitText unimplemented");
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeText() {
    super(Node.TEXT_NODE, null);
  }

  public TreeText(TreeText e, boolean copyChildren) {
    super(e, copyChildren);
    handler = e.handler;
    action = e.action;
    data = e.data;
    ignorable = e.ignorable;
    isWhitespace = e.isWhitespace;
  }

  /** Construct a node with given data. */
  public TreeText(String data) {
    this();
    this.data = data;
    setIsWhitespace(Test.isWhitespace(data));
  }

  /** Construct a node with a single character as data. */
  public TreeText(char data) {
    this(String.valueOf(data));
  }

  /** Construct a node with an integer value. */
  public TreeText(long data) {
    this(String.valueOf(data), false, false);
  }

  /** Construct a node with a floating-point value. */
  public TreeText(double data) {
    this(String.valueOf(data), false, false);
  }

  /** Construct a node with given data and handler. */
  public TreeText(String data, Handler handler) {
    this(data);
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeText(String data, boolean isIgnorable,
		       boolean isWhitespace, Handler handler) {
    this();
    this.data = data;
    ignorable = isIgnorable;
    this.isWhitespace = isWhitespace;
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeText(String data, boolean isIgnorable,
		       boolean isWhitespace) {
    this(data, isIgnorable, isWhitespace, null);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeText(String data, boolean isIgnorable) {
    this(data);
    setIsIgnorable(isIgnorable);
  }



  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "";		// insert character entities ===
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return TextUtil.protectMarkup(getData()); // insert character entities
    //return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "";
  }


  /** Convert the Node to a String.
   */
  public String toString() {
    return contentString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeText(this, false);
  }

}
