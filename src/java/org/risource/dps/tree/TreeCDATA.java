////// TreeCDATA.java -- implementation of ActiveCDATA
//	$Id: TreeCDATA.java,v 1.3 2001-04-03 00:04:52 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
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
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActiveCDATA interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeCDATA.java,v 1.3 2001-04-03 00:04:52 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeCDATA extends TreeText implements ActiveCDATA {


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeCDATA() {
    super(Node.CDATA_SECTION_NODE, null);
  }

  public TreeCDATA(TreeCDATA n, boolean copyChildren) {
    super(n, copyChildren);
  }

  /** Construct a node with given data. */
  public TreeCDATA(String data) {
    super(Node.CDATA_SECTION_NODE, data);
  }

  /** Construct a node with given data and handler. */
  public TreeCDATA(String data, Handler handler) {
    super(Node.CDATA_SECTION_NODE, data);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<![CDATA[ ";
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getData();
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "]]>";
  }


  /** Convert the Node to a String, in external form.
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
    return new TreeCDATA(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeCDATA(this, true);
  }
 
}
