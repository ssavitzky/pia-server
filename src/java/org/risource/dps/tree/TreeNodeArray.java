// TreeNodeArray.java
// $Id: TreeNodeArray.java,v 1.2 2001-04-03 00:04:56 steve Exp $

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

import org.w3c.dom.*;
import org.risource.dps.active.*;

import java.io.*;
import java.util.Vector;

/**
 * Mutable node collection.  Basically an extensible array of Nodes.
 */
public class TreeNodeArray implements ActiveNodeList, Serializable {

  /** The actual elements.  Should be protected, but the enumerator needs it. */
  Vector elements = new Vector();
  protected int size() { return elements.size(); }

  protected String sep = null;
  public String getSep() { return sep; }
  public void setSep(String v) { sep = v; }

  public TreeNodeArray(){
  }

  public TreeNodeArray(Node aNode){
    if (aNode != null) append(aNode);
  }

  /**
   * Deep copy of another list.
   */
  public TreeNodeArray(NodeList list) {
    if (list != null) append(list);
  }

  /**
   * Replace the indexth item the list with replacedNode, and return the old
   *	node object at that index (null is returned if the index is equal to
   *	the previous number of nodes in the list). If index is greater than
   *	the number of nodes in the list, a NoSuchNodeException is thrown.
   *
   * @param index Ranges from 0 to size - 1
   * @param replaceNode new node to be put at index
   * @return previous node at index
   * @exception NoSuchNodeException when indexed out of bound.
   */
  public Node replace(long index,Node replacedNode)  {
    if( index >= elements.size() || index < 0){
      return null;
    }
    Node old = (Node)elements.elementAt( (int)index );
    elements.setElementAt(replacedNode, (int)index);
    return old;
  }

  /**
   * Inserts a child node into the list BEFORE zero-based location
   * index. Nodes from index to the end of list are moved up by one. If index
   * is 0, the node is added at the beginning of the list; if index is
   * self.getLength(), the node is added at the end of the list.
   *
   * @param index If 0, the node is added at the beginning of the list; if
   * index is self.getLength(), the node is added at the end of the list.
   * @param newNode new node to be put at index 
   * @exception NoSuchNodeException when index out of bounds.
   */
  public void insert(long index,Node newNode) {
    if( index == 0 ){
      elements.insertElementAt( newNode, 0 );
      return;
    }
    if( index == size() ){
      elements.addElement( newNode );
      return;
    }

    if( index >= 0 && index <= size() )
      elements.insertElementAt( newNode, (int)index );
    
  }

  /**
   * Removes the node at index from the list and returns it. 
   * 	The indices of the members of the list which followed this node are
   * 	decremented by one following the removal. If the index is provided is
   * 	larger than the number of nodes in the list, NoSuchNodeException is
   * 	thrown.
   *
   * @param index Ranges from 0 to size - 1
   * @return node at index
   * @exception NoSuchNodeException when indexed out of bound.  */
  public Node remove(long index) {
    if( index >= size() || index < 0){
      return null;
    }

    Node n = (Node)elements.elementAt( (int)index );
    elements.removeElementAt( (int)index );
    return n;
  }

  public Node remove(Node node) {
    int index = elements.indexOf(node);
    if (index < 0) return null;
    elements.removeElementAt(index);
    return node;
  }

  public long indexOf(Node node) {
    return elements.indexOf(node);
  }


  /**
   * Returns the indexth item in the collection. 
   *	If index is greater than or equal to the number of nodes in the list, 
   *	a NoSuchNodeException is thrown. 
   *@param index Ranges from 0 to size - 1
   *@return node at index
   *@exception NoSuchNodeException when indexed out of bound.
   */
  public Node item(int index) {
    if( index >= size() || index < 0){
      return null;
    }
    return  (Node)elements.elementAt( (int)index );
  }

  public ActiveNode activeItem(int index) {
    if( index >= size() || index < 0){
      return null;
    }
    return  (ActiveNode)elements.elementAt( (int)index );
  }

  /**
   * Returns the number of nodes in the NodeList instance. The range of valid
   * child node indices is 0 to getLength()-1 inclusive.
   */
  public int getLength(){ return size(); }
  
  /** Append a new element.
   */
  public void append(Node newChild) { elements.addElement(newChild); }

  /** Append a new element.
   */
  public void append(ActiveNode newChild) { elements.addElement(newChild); }

  /** Append a list of elements.
   */
  public void append(NodeList aNodeList) {
    if (aNodeList == null) return;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; ++i) {
      append(aNodeList.item(i));
    }
  }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    String result = "";
    int length = getLength();
    for (int i = 0; i < length; ++i) {
      Node n = item(i);
      result += n.toString();
      if (sep != null && i != length - 1) result += sep;
    }
    return result;
  }
}






