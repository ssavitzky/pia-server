////// ToNodeList.java:  Output to node list
//	$Id: ToNodeList.java,v 1.6 1999-06-04 22:40:14 steve Exp $

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


package org.risource.dps.output;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeNodeList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to an (active) NodeList.<p>
 *
 * @version $Id: ToNodeList.java,v 1.6 1999-06-04 22:40:14 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.w3c.dom.NodeList
 */
public class ToNodeList extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected TreeNodeList list = new TreeNodeList();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveNodeList getList() { return list; }
  public void clearList() { list = new TreeNodeList(); }

  public void putNode(Node aNode) {
    if (depth == 0) {
      list.append(aNode);
    } else {
      super.putNode(aNode);
    }
  }

  public void startNode(Node aNode) {
    Node p = aNode.getParentNode();
    if (active == p && active != null) {	// already a child.  descend.
      if (p != null) descend();
      setNode(aNode);
      return;
    }
    if (p != null || aNode.hasChildNodes()) {
      aNode = Copy.copyNodeAsActive(aNode);
    }
    appendNode(aNode, active);
    descend();
    setNode(aNode);
  }

  public Node toParent() {
    if (depth != 1) return super.toParent();
    setNode((Node)null);
    depth--;
    atFirst = false;
    return active;
  }

  protected void appendNode(Node aNode, Node aParent) {
    if (depth == 0)  	list.append(aNode); 
    else 		Copy.appendNode(aNode, aParent);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToNodeList() {
  }

}
