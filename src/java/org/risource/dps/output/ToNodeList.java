////// ToNodeList.java: Token output Stream to node list
//	$Id: ToNodeList.java,v 1.3 1999-03-12 19:27:09 steve Exp $

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

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.Attribute;
import org.risource.dom.Element;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to an (active) NodeList.<p>
 *
 * @version $Id: ToNodeList.java,v 1.3 1999-03-12 19:27:09 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Token
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class ToNodeList extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ParseNodeList list = new ParseNodeList();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ParseNodeList getList() { return list; }
  public void clearList() { list = new ParseNodeList(); }

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
    if (p != null || aNode.hasChildren()) {
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

  public Element toParentElement() {
    if (depth != 1) return super.toParentElement();
    setNode((Node)null);
    depth--;
    atFirst = false;
    return element;
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
