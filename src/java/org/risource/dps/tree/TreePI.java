////// TreePI.java -- implementation of ActivePI
//	$Id: TreePI.java,v 1.2 1999-06-04 22:40:46 steve Exp $

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

/**
 * An implementation of the ActivePI interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreePI.java,v 1.2 1999-06-04 22:40:46 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.w3c.dom.Node
 */
public class TreePI extends TreeNode implements ActivePI {

  /************************************************************************
  ** PI interface:
  ************************************************************************/

  String data = "";

  public void   setData(String v) 	{ data = v; }
  public String getData() 	   	{ return data; }

  public String getNodeValue() 		{ return getData(); }
  public void   setNodeValue(String v)	{ data = v; }

  public void   setTarget(String v) 	{ nodeName = v; }
  public String getTarget() 	   	{ return nodeName; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreePI() {
    super(Node.PROCESSING_INSTRUCTION_NODE, "");
  }

  public TreePI(TreePI e, boolean copyChildren) {
    super(e, copyChildren);
    setData(e.getData());
  }

  /** Construct a node with given data. */
  public TreePI(String name, String data) {
    super(Node.PROCESSING_INSTRUCTION_NODE, name);
    setData(data);
  }

  /** Construct a node with given data and handler. */
  public TreePI(String name, String data, Handler handler) {
    super(Node.PROCESSING_INSTRUCTION_NODE, name, handler);
    setData(data);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<?" + getNodeName() + " ";
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return (!hasChildNodes())? getData() : getChildNodes().toString();
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ">";
  }


  /** Convert the Node to a String.
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
    return new TreePI(this, false);
  }

}
