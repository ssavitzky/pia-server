// TreeNodeMap.java
// $Id: TreeNodeMap.java,v 1.3 2001-04-03 00:04:57 steve Exp $

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

import java.io.*;
import org.w3c.dom.*;
import org.risource.dps.active.*;
import org.risource.dps.util.Test;

/**
 * Implementing NamedNodeMap, which associates <em>named</em> nodes with
 * their own names.
 */
public class TreeNodeMap extends TreeNodeTable
  implements ActiveNodeMap, ActiveNodeList {

  public TreeNodeMap() {
  }

  /**
   * Copy from another list.
   */
  public TreeNodeMap(TreeNodeTable l){
    if( l != null )
      initialize( l );
  }


  public ActiveNodeList asNodeList() { return this; }

  public Node item(int index) { return activeItem(index); }

  public boolean hasTrueItem(String name) {
    return Test.trueValue(getBinding(name));
  }

  public Node setNamedItem(String name, Node o) {
    if (! (o instanceof ActiveNode))
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR);
    return setBinding(name, (ActiveNode)o);
  }

  public Node setNamedItem(Node o) {
    return setNamedItem(o.getNodeName(), o);
  }

  public Node removeNamedItem(String name) {
    return removeActiveItem(name);
  }

  public String getItemValue(String name) {
    ActiveNode n = getBinding(name);
    return (n == null) ? null : n.getNodeValue();
  }

  public void append(ActiveNode o) {
    setNamedItem(o);
  }

  /**
   * Remove node specified by name.
   */
  public Node remove(String  name) { return removeNamedItem(name); }

}







