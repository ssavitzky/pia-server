// TreeChildList.java
// $Id: TreeChildList.java,v 1.1 1999-04-07 23:22:04 steve Exp $

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

import java.io.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.w3c.dom.*;

/**
 * Implementation of ActiveNodeList for the common special case where
 *	the contents are the children of a single Node. 
 */
public class TreeChildList implements ActiveNodeList {

  public TreeChildList(TreeNode parentNode){
    parent = parentNode;
  }

  /**
   * Returns the indexth item in the collection. If index is greater than or equal
   * to the number of nodes in the list, a NoSuchNodeException is thrown. 
   * @return a node at index position.
   * @param index Position to get node.
   * @exception NoSuchNodeException is thrown if index out of bound.
   */
  public ActiveNode activeItem(int index)
  {
    int i = 0;
    if (parent == null || index < 0) return null;

    ActiveNode n = parent.getFirstActive();
    while( i < index && n != null){
	i++;
	n = n.getNextActive();
    }
    return n;
  }

  public Node item(int index) { return activeItem(index); }

  public void append(ActiveNode node) {
    parent.addChild(node);
  } 

  /**
   * Returns the number of nodes in the NodeList instance. 
   *	The range of valid child node indices is 0 to getLength()-1 inclusive. 
   * @return length of list.
   */
  public int getLength()
  {
    Node ptr = parent.getFirstChild();
    int count = 0;
    while( ptr != null ){
      count++;
      ptr = ptr.getNextSibling();
    }
    return count;
  }

  /**
   * For copying foreign node list.
   */
  protected void initialize(NodeList list)
  {
    //punt
  }

  /**
   * return parent so that this list can be copied.
   */
  protected TreeNode getParent(){ return parent; }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    Node n = parent.getFirstChild();
    if (n == null) return "";

    String result = "";
    for (; n != null; n = n.getNextSibling()) result += n.toString();
    return result;
  }

  /**
   * This ParseChildList is expanded through parent's
   * first node.
   * 
   */
  protected TreeNode parent;
}



