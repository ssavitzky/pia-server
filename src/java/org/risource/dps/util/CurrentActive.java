////// CurrentActive.java: current node in a parse tree
//	$Id: CurrentActive.java,v 1.5 1999-06-17 01:03:21 steve Exp $

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


package org.risource.dps.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

import org.risource.dps.*;
import org.risource.dps.active.*;

import org.risource.dps.util.Copy;

/**
 * The base class for objects with a current node in a parse tree.
 *
 *	This is a basic implementation of Cursor in the special case
 *	where all instances of Node are known to implement ActiveNode. <p>
 *
 * ===	For best results, ActiveNode should have alternatives to
 *	getFirstActive, etc. that return ActiveNode.  Easiest if we
 *	completely replace (shadow) AbstractNode with our own parent.
 *
 *	A sufficient selection of protected navigation functions is provided
 *	to implement most subclasses (including implementations of Input, 
 *	Output, TreeIterator, and so on) efficiently, provided they operate
 *	on complete trees.
 *
 * @version $Id: CurrentActive.java,v 1.5 1999-06-17 01:03:21 steve Exp $
 * @author steve@rsv.ricoh.com
 * 
 * @see org.risource.dps.Cursor
 */
public class CurrentActive implements Cursor {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int depth = 0;

  /** This is the current Node, since we're assuming everything is active.
   */ 
  protected ActiveNode active;

  /** The Action handler associated with the current Node. 
   *	We still want this because there might be subclasses which
   *	need to change it, e.g. as a continuation.
   */
  protected Action action;

  /** If the current <code>node</code> is an element, this is equal to it. 
   *	Otherwise it's <code>null</code>. 
   */
  protected String tagName;

  protected boolean retainTree = false;
  protected boolean atFirst = false;

  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  public final Node       getNode() 	{ return active; }
  public final ActiveAttrList getAttributes() {
    return active.getAttrList();
  }
  public final String	  getNodeName() { return active.getNodeName(); }

  public final ActiveNode getActive() 	{ return active; }
  public final int	  getDepth() 	{ return depth; }
  public final String 	  getTagName() 	{ return tagName; }

  /** Set the current node.  Set <code>active</code>. */
  protected final void  setNode(Node aNode) {
    if (aNode == null) {
      action = null;
      active = null;
      tagName = null;
      return;
    } else if (! (aNode instanceof ActiveNode)) {
      throw(new DPSException(DPSException.NOT_ACTIVE_NODE_ERR));
    }
    active = (ActiveNode)aNode;
    action = active.getAction();
    if (aNode.getNodeType() == Node.ELEMENT_NODE) {
      tagName = aNode.getNodeName();
    } else {
      tagName = null;
    }
  }

  /** Set the current node to an element */
  protected final void setNode(ActiveElement anElement, String aTagName) {
    active = anElement;

    if (active == null)
      throw(new DPSException(DPSException.NOT_ACTIVE_NODE_ERR,
			     "ActiveElement expected"));

    action = active.getAction();
    tagName = (aTagName == null)? anElement.getTagName() : aTagName;
  }

  protected final void setNode(ActiveNode aNode) {
    active = aNode;
    action = active.getAction();
    if (active.getNodeType() == Node.ELEMENT_NODE) {
      tagName = active.getNodeName();
    } else {
      tagName = null;
    }
  }

  protected final void setNode(ActiveNode aNode, String aTagName) {
    active = aNode;
    tagName = aTagName;
    action = active.getAction();
  }



  /************************************************************************
  ** Information:
  ************************************************************************/

  public Syntax getSyntax() 	{ return active.getSyntax(); }

  public boolean atTop() 	{ return depth == 0; }
  public boolean atFirst() 	{ return atFirst; }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean atLast() {
    return active.getNextActive() == null;
  }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean hasChildren() {
    return active != null &&  active.hasChildNodes();
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveChildren() {
    return hasChildren();
  }

  /** This will have to be overridden if the tree is being built on the fly. */
  public boolean hasAttributes() {
    NamedNodeMap atts = active.getAttributes();
    return (atts != null) && (atts.getLength() > 0);
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveAttributes() {
    return hasAttributes();
  }

  public String getTagName(int level) {
    Node n = getNode(level);
    return (n == null || !(n.getNodeType() == Node.ELEMENT_NODE))
      ? null : n.getNodeName();
  }

  public Node getNode(int level) {
    if (level > depth || level < 0) return null;
    ActiveNode n = active;
    int d = depth;
    for ( ; n != null ; n = n.getActiveParent(), d--) if (d == level) return n;
    return null;
  }

  public boolean insideElement(String tag, boolean ignoreCase) {
    ActiveNode n = active;
    int d = depth;
    String tn = null;
    for ( ; n != null && d >= 0 ; n = n.getActiveParent(), d--) {
      if (n instanceof Element) {
	tn = ((Element)n).getTagName();
	if (ignoreCase && tag.equalsIgnoreCase(tn)) return true;
	else if (!ignoreCase && tag.equals(tn)) return true;
      }
    }
    return false;
  }

  /************************************************************************
  ** Navigation Operations:
  ************************************************************************/

  /** Returns the parent of the current Node.
   *	After calling <code>toParent</code>, <code>toNextNode</code> will
   *	return the parent's next sibling.
   */
  public Node toParent() {
    if (atTop()) return null;
    ActiveNode p = active.getActiveParent();
    if (p == null) return null;
    setNode(p);
    depth--;
    atFirst = false;
    return active;
  }

  /************************************************************************
  ** Input Operations:
  ************************************************************************/

  protected Node toFirstNode() {
    while (!atTop()) toParent();
    atFirst = true;
    return active;
  }

  /** Returns the first child of the current Node. 
   *	A subsequent call on <code>toNextNode</code> will return the 
   *	second child, if any.
   *
   *	Must be overridden if the tree is being built on the fly.
   */
  protected Node toFirstChild() {
    Node n = active.getFirstActive();
    if (n == null) return null;
    descend();
    setNode(n);
    atFirst = true;
    return active;
  }

  /** Returns the next node from this source and makes it current.  
   *	May descend or ascend levels.  This can be detected by tracking
   *	the depth with <code>getDepth()</code>. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available from this source. 
   */
  public Node toNextNode() {
    Node n = active.getNextActive(); // toNextNode bogus at this point ===
    if (n == null) return null;
    setNode(n);
    atFirst = false;
    return active;
  }

  /** Returns the next node at this level and makes it current.  
   *	May require traversing all of the (old) current node if its
   *	children have not yet been seen. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available at this level. 
   */
  protected Node toNextSibling() {
    Node n = active.getNextActive();
    if (n == null) return null;
    setNode(n);
    atFirst = false;
    return active;
  }



  /************************************************************************
  ** Processing Operations:
  ************************************************************************/

  /** Returns the action, if known, for the current node. 
   */
  public Action getAction() { return action; }

  /** Ensures that all descendents of the current node will be appended to
   *	it as they are traversed.  
   */
  public void retainTree() { retainTree = true; }
  
  /** Ensures that all descendents of the current node have been seen
   *	and appended to it.  May be expensive.  
   */
  protected Node getTree() { return active; }


  /************************************************************************
  ** Output Operations:
  **
  **	These may also be used in an Input to build a parse tree as a
  **	side effect while parsing.
  **
  ************************************************************************/

  /** Adds <code>aNode</code> and its children to the document under 
   *	construction as a new child of the current node.  The new node
   *	is copied unless it has no parent and has a type compatible with
   *	the document under construction.  <p>
   *
   *	If the current node is an Element and <code>aNode</code> is an
   *	Attribute, it is added to the attribute list of the curren node.
   */
  protected void putNode(Node aNode) {
    Node p = aNode.getParentNode();
    if (p != null) {	// someone else's child: deep copy.
      if (p == active) System.err.println(Log.node(aNode)+ " in " + p);
      ActiveNode n = (ActiveNode)aNode;
      appendNode(n.deepCopy(), active);
      /* === this will fail for attributes and entities with values:
      startNode(aNode);
      for (ActiveNode m = n.getFirstActive();
	   m != null;
	   m = m.getNextActive()) 
	putNode(m);
      endNode();
      */
    } else {
      appendNode(aNode, active);
    }
  }

  /** Adds <code>aNode</code> to the document under construction, and
   *	makes it the current node.
   */
  protected void startNode(Node aNode) {
    Node p = aNode.getParentNode();
    if (p != null || aNode.hasChildNodes()) {
      // === The following chokes on an attribute with an unspecified value!
      // System.err.println("about to copy " + aNode.getClass().getName());
      aNode = shallowCopy(aNode);
    }
    appendNode(aNode, active);
    descend();
    setNode(aNode);
  }

  /** Ends the current Node and makes its parent current.
   * @return <code>false</code> if the current Node has no parent.
   */
  protected boolean endNode() {
    return toParent() != null;
  }

  /** Adds <code>anElement</code> to the document under construction,
   *	and makes it the current node.  The attribute list is not
   *	copied; instead, putAttribute, etc. are used.  An element
   *	may be ended with either <code>endElement</code> or
   *	<code>endNode</code>. 
   */
  protected void startElement(Element anElement) {
    Node e = shallowCopy(anElement);
    appendNode(e, active);
    descend();
    setNode(e);
  }

  /** Ends the current Element.  The end tag may be optional.  
   *	<code>endElement(true)</code> may be used to end an empty element. 
   */
  public boolean endElement(boolean optional) {
    // === set optional end-tag flag if the element has one. ===
    return toParent() != null;
  }

  /** Perform any necessary actions before descending a level. 
   *	Normally this just increments <code>depth</code>
   */
  protected void descend() {
    depth++;
  }


  /** === Subclasses that need it MUST override shallowCopy === */
  protected ActiveNode shallowCopy(Node aNode) {
    return ((ActiveNode)aNode).shallowCopy();
  }

  protected void appendNode(Node aNode, Node aParent) {
    Copy.appendNode(aNode, aParent);
  }

}
