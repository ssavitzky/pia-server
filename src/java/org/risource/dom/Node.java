// Node.java
// $Id: Node.java,v 1.3 1999-03-12 19:24:34 steve Exp $

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

package org.risource.dom;

/** 
 * The Node object is the primary datatype for the entire Document Object
 * Model. It represents a single node in the document tree. Nodes may have,
 * but are not required to have, an arbitrary number of child nodes.
 *
 * === [steve] Node should have methods for <code>shallowCopy</code> and 
 * === <code>deepCopy</code>.  Commented out for the present.
 */
public interface Node {

  /** 
   * Returns an indication of the underlying Node object's type. The actual
   * type of the returned data is language binding dependent; the IDL
   * specification uses an enum, and it is expected that most language
   * bindings will represent this runtime-queryable Node type using an
   * integral data type. The names of the node type enumeration literals are
   * straightforwardly derived from the names of the actual Node subtypes, and
   * are fully specified in the IDL definition of Node in the IDL definition
   * in Appendix A.
   */
  int getNodeType();

  /**
   * Returns the parent of the given Node instance. If this node is the root
   * of the document object tree, null is returned. [Note: because in
   * ECMAScript get/set method pairs are surfaced as properties, Parent would
   * conflict with the pre-defined Parent property, so we disambiguate this
   * with "ParentNode" even though it is inconsistent with the naming
   * convention of the other methods that do not include "Node"].
   */
  Node     getParentNode();
  
  /**
   * Returns a NodeList object containing the children of this node. If there
   * are no children, null is returned. The content of the returned NodeList
   * is "live" in the sense that changes to the children of the Node object
   * that it was created from will be immediately reflected in the set of
   * Nodes the NodeList contains; it is not a static snapshot of the content
   * of the Node. Similarly, changes made to the NodeList will be immediately
   * reflected in the set of children of the Node that the NodeList was
   * created from.
   */
  NodeList getChildren();

  /**
   * Returns true if the node has any children, false if the node has no
   * children at all. This method exists both for convenience as well as to
   * allow implementations to be able to bypass object allocation, which may
   * be required for implementing getChildren().
   */
  boolean  hasChildren();

  /**
   * Returns the first child of a node. If there is no such node, null is 
   * returned.
   */
  Node     getFirstChild();

  /**
   * Returns the node immediately preceding the current node in a breadth-first
   * traversal of the tree. If there is no such node, null is returned. 
   */
  Node     getPreviousSibling();

  /**
   * Returns the node immediately following the current node in a breadth-first
   * traversal of the tree. If there is no such node, null is returned. 
   */
  Node     getNextSibling();
  
  /**
   * Inserts a child node (newChildbefore the existing child node refChild. If
   * refChild is null, insert newChild at the end of the list of children. If
   * refChild is not a child of the Node that insertBefore is being invoked
   * on, a NotMyChildException is thrown. 
   */
  void insertBefore(Node newChild, Node refChild)
       throws NotMyChildException;
 /**
  * Replaces the child node oldChild with newChild in the set of children of
  * the given node, and return the oldChild node. If oldChild was not already
  * a child of the node that the replaceChild method is being invoked on, a
  * NotMyChildException is thrown.
  */
  Node replaceChild(Node oldChild, Node newChild)
    throws NotMyChildException;

 /**
  *  Removes the child node indicated by oldChild from the list of children
  *  and returns it. If oldChild was not a child of the given node, a
  *  NotMyChildException is thrown.
  */
  Node removeChild(Node oldChild)
    throws NotMyChildException;

  /** 
   * Copies a Node and its attributes, but not the children of an Element.
   *	(Some Node implementations, for example Attribute and Entity, may
   *	use children for the value.  These are copied.)
   */
    // Node shallowCopy();

  /** 
   * Copies a Node and all of its descendents.
   */
    // Node deepCopy();

};



