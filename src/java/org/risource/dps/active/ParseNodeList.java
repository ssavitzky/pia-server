////// ParseNodeList.java: ActiveNodeList implementation
//	$Id: ParseNodeList.java,v 1.3 1999-03-12 19:25:29 steve Exp $

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

import org.risource.dom.NodeList;
import org.risource.dom.Node;
import org.risource.dom.NodeEnumerator;

import java.util.Enumeration;

/**
 * A list or sequence of ActiveNode objects.  
 *
 *	The contents need not be children of the same Node.
 *
 * @version $Id: ParseNodeList.java,v 1.3 1999-03-12 19:25:29 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Token
 * @see org.risource.dom.Node
 */
public class ParseNodeList extends ParseNodeArray
  implements ActiveNodeList, java.io.Serializable {

  /**
   * Returns the indexth item in the collection, as a ActiveNode.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a ActiveNode at index position.
   * @param index Position to get node.
   */
  public ActiveNode activeNodeAt(long index) { 
    try {
      return (ActiveNode)item(index);
    } catch (org.risource.dom.NoSuchNodeException e) {
      return null;
    }
  }

  /** Append a new ActiveNode.
   */
  public void append(ActiveNode newChild) { append((Node)newChild); }

  public ParseNodeList() {}

  public ParseNodeList(ActiveNode initialChild) {
    super((Node)initialChild);
  }
  public ParseNodeList(Node initialChild) {
    super((Node)initialChild);
  }
  public ParseNodeList(NodeList initialChildren) {
    super(initialChildren);
  }

  public ParseNodeList(Enumeration elements) {
    while (elements.hasMoreElements()) {
      Object o = elements.nextElement();
      if (o instanceof ActiveNode) append((ActiveNode) o);
      else append(new ParseTreeText(o.toString()));
    }
  }

  public ParseNodeList(NodeEnumerator elements) {
    for (Node n = elements.getFirst(); n != null; n = elements.getNext()) {
      if (n instanceof ActiveNode) append((ActiveNode) n);
    }
  }
}
