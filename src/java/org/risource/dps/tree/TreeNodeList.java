////// TreeNodeList.java: ActiveNodeList implementation
//	$Id: TreeNodeList.java,v 1.3 1999-06-25 00:18:03 steve Exp $

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

import java.util.Enumeration;
import org.risource.dps.Input;

/**
 * A list or sequence of ActiveNode objects.  
 *
 *	The contents need not be children of the same Node.
 *
 * @version $Id: TreeNodeList.java,v 1.3 1999-06-25 00:18:03 steve Exp $
 * @author steve@rsv.ricoh.com 
 */
public class TreeNodeList extends TreeNodeArray {

  /**
   * Returns the indexth item in the collection, as a ActiveNode.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a ActiveNode at index position.
   * @param index Position to get node.
   */
  public ActiveNode activeItem(int index) { 
    return (ActiveNode)item(index);
  }

  public TreeNodeList() {}

  public TreeNodeList(ActiveNode initialChild) {
    super((Node)initialChild);
  }
  public TreeNodeList(Node initialChild) {
    super((Node)initialChild);
  }
  public TreeNodeList(NodeList initialChildren) {
    super(initialChildren);
  }

  public TreeNodeList(Enumeration elements) {
    while (elements.hasMoreElements()) {
      Object o = elements.nextElement();
      if (o instanceof ActiveNode) append((ActiveNode) o);
      else append(new TreeText(o.toString()));
    }
  }

  public TreeNodeList(Input elements) {
    for ( ; elements.getActive() != null; elements.toNext() ) {
      append(elements.getActive());
    }
  }

}
