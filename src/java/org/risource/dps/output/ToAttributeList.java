////// ToAttributeList.java: Output to attribute list
//	$Id: ToAttributeList.java,v 1.9 2001-04-03 00:04:41 steve Exp $

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


package org.risource.dps.output;

import org.w3c.dom.*;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeAttrList;


/**
 * Output to an AttributeList.<p>
 *
 * @version $Id: ToAttributeList.java,v 1.9 2001-04-03 00:04:41 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.active.ActiveAttrList
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class ToAttributeList extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ActiveAttrList list = new TreeAttrList();

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveAttrList getList() { return list; }

  public void putNode(Node aNode) {
    if (aNode.getNodeType() == Node.ATTRIBUTE_NODE && depth == 0) {
      list.setNamedItem(aNode);
    } else {
      super.putNode(aNode);
    }
  }

  public void startNode(Node aNode) {
    if (depth == 0) {
      putNode(aNode);
      descend();
      setNode(aNode);
      return;
    }
    Node p = aNode.getParentNode();
    if (active == p && active != null) {	// already a child.  descend.
      if (p != null) descend();
      setNode(aNode);
      return;
    }
    if (p != null || aNode.hasChildNodes()) {
      aNode = Copy.copyNodeAsActive(aNode, tagset);
    }
    appendNode(aNode, active);
    descend();
    setNode(aNode);
  }

  public boolean toParent() {
    if (depth == 0) return false;
    if (depth != 1) return super.toParent();
    setNode((Node)null);
    depth--;
    atFirst = false;
    return active != null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToAttributeList(Tagset ts) {
    super(ts);
  }
}
