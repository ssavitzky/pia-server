// TreeNodeMap.java
// $Id: TreeNodeMap.java,v 1.1 1999-04-07 23:22:10 steve Exp $

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

  
  public Node item(int index) { return activeItem(index); }

  public boolean hasTrueItem(String name) {
    return Test.trueValue(getActiveItem(name));
  }

  public Node setNamedItem(String name, Node o) {
    if (! (o instanceof ActiveNode))
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR);
    return setActiveItem(name, (ActiveNode)o);
  }

  public Node setNamedItem(Node o) {
    return setNamedItem(o.getNodeName(), o);
  }

  public Node removeNamedItem(String name) {
    return removeActiveItem(name);
  }

  public String getItemValue(String name) {
    ActiveNode n = getActiveItem(name);
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







