////// TreeComment.java -- implementation of ActiveComment
//	$Id: TreeComment.java,v 1.2 1999-06-04 22:40:34 steve Exp $

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
import org.risource.dps.util.Copy;

/**
 * An implementation of the ActiveComment interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id: TreeComment.java,v 1.2 1999-06-04 22:40:34 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeComment extends TreeCharData implements ActiveComment {


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeComment() {
    super(Node.COMMENT_NODE, null);
  }

  public TreeComment(TreeComment n, boolean copyChildren) {
    super(n, false);
  }

  /** === Let's not construct TreeComment from Comment, shall we?
  public TreeComment(Comment n, boolean copyChildren) {
    super((ActiveComment)n, false);
  }
  */

  /** Construct a node with given data. */
  public TreeComment(String data) {
    super(Node.COMMENT_NODE, data);
  }

  /** Construct a node with given data and handler. */
  public TreeComment(String data, Handler handler) {
    super(Node.COMMENT_NODE, data, handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<!-- ";
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

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeComment(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeComment(this, true);
  }
 
}
