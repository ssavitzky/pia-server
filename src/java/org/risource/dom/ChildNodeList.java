// ChildNodeList.java
// ChildNodeList.java,v 1.10 1999/03/01 23:45:11 pgage Exp

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


package crc.dom;

import java.io.*;

/**
 * Given a node, this list is expanded through the node's
 * first child.  This list also supports deep copy operation 
 * of another node.
 */

public class ChildNodeList implements NodeList{

  public ChildNodeList(AbstractNode parentNode){
    parent = parentNode;
  }

  /**
   * Given a node list, perform deep copy of list
   * only if the parent node is an AbstractNode.
   */
  public ChildNodeList(NodeList list)
  {
    if( list == null ) return;
    if( list instanceof ChildNodeList ){
      AbstractNode p = ((ChildNodeList)list).getParent();
        if( p != null )
      	parent = (AbstractNode)p.clone();
      else
      	parent = null;
    }else initialize( list );
  }

  /**
   * Returns a NodeEnumerator.
   */
  public NodeEnumerator getEnumerator(){ return new ChildNodeListEnumerator( this ); }

  /**
   * Returns the indexth item in the collection. If index is greater than or equal
   * to the number of nodes in the list, a NoSuchNodeException is thrown. 
   * @return a node at index position.
   * @param index Position to get node.
   * @exception NoSuchNodeException is thrown if index out of bound.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    long i = 0;
    if (parent == null)
      throw new NoSuchNodeException("ChildNodeList is empty.");

    Node n = parent.getFirstChild();
    long howmany = getLength();

    //Report.debug(this, "index is-->"+Integer.toString( (int)index ));
    if( index <= howmany && index >= 0 ){
      while( i != index ){
	i++;
	n = n.getNextSibling();
      }
      //Report.debug(this, "i is-->"+Integer.toString( (int)i ));
      return n;
    }
    else{
      String err = ("Index out of bound.");
      throw new NoSuchNodeException(err);
    }
   
  }

  /**
   * Returns the number of nodes in the NodeList instance. The range of valid child
   * node indices is 0 to getLength()-1 inclusive. 
   * @return length of list.
   */
  public long getLength()
  {
    Node ptr = parent.getFirstChild();
    long count = 0;
    while( ptr != null ){
      count++;
      ptr = ptr.getNextSibling();
    }
    return count;
    /*
    if( parent == null ) return 0;
    return parent.getChildCount(); // === getChildCount is buggy ===
    */
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
  protected AbstractNode getParent(){ return parent; }

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
   * This ChildNodeList is expanded through parent's
   * first node.
   * 
   */
  protected AbstractNode parent;
}



