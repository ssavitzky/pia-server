////// CursorStack.java: A linked-list stack of current nodes.
//	$Id: CursorStack.java,v 1.8 2001-04-03 00:04:59 steve Exp $

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


package org.risource.dps.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.risource.dps.*;
import org.risource.dps.active.*;

/**
 * A stack frame for a linked-list stack of current nodes. 
 *	It is designed to be used for saving state in a Cursor that is
 *	not operating on a real parse tree.
 *
 * @version $Id: CursorStack.java,v 1.8 2001-04-03 00:04:59 steve Exp $
 * @author steve@rii.ricoh.com
 * 
 * @see org.risource.dps.Cursor
 */

public class CursorStack implements Cursor {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int depth = 0;

  /** The current Node. */
  protected Node node;

  /** The Action handler associated with the current Node. */
  protected Action action;

  /** If <code>node</code> is an element, this is its tagname
   *	Otherwise it's <code>null</code>. 
   */
  protected String tagName;

  /** If <code>node</code> is an active node, this is equal to it. 
   *	Otherwise it's <code>null</code>. 
   */ 
  protected ActiveNode active;

  protected boolean retainTree 	 = false;
  protected boolean atFirst	 = false;
  protected boolean atLast	 = false;
  protected boolean sawChildren  = false;

  protected CursorStack stack = null;

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void initialize() {	/* override if necessary */ }

  protected void copy(CursorStack old) {
    depth 	= old.depth;
    node 	= old.node;
    action 	= old.action;
    tagName 	= old.tagName;
    active 	= old.active;
    retainTree 	= old.retainTree;
    atFirst 	= old.atFirst;
    atLast 	= old.atLast;
    stack 	= old.stack;
  }

  public CursorStack() {}

  public CursorStack(CursorStack prev) {
    stack = prev;
    depth = prev.depth + 1;
  }

  public CursorStack(CursorStack old, boolean bumpOldDepth) {
    copy(old);
    old.sawChildren = true;
    if (bumpOldDepth) old.depth++;
  }
    

  public CursorStack(int d, Node n, Action act, String tag,
		     ActiveNode an, boolean retain, boolean first, 
		     boolean last, CursorStack old) {
    depth 	= d;
    node 	= n;
    action 	= act;
    tagName 	= tag;
    active 	= an;
    retainTree 	= retain;
    atFirst 	= first;
    atLast	= last;
    stack 	= old;
  }

  /** Return the previous stack frame. 
   *	There are two ways to pop:  one is simply:<br>
   *	<code>stack = stack.getPrevious();</code><br>
   *	which works if the object executing it is <em>not</em> itself a
   *	CursorStack. 
   */
  public CursorStack getPrevious() 	{ return stack; }

  /** Replace with the contents of the previous stack frame. 
   *	There are two ways to pop; the other one uses <code>getPrevious</code>.
   *	Using <code>popInPlace</code> is only useful in subclasses of
   *	CursorStack, so it's protected.
   *
   * @see #getPrevious
   */
  protected boolean popInPlace() {
    if (stack == null) return false;
    copy(stack);
    sawChildren = true;
    return true;
  }

  /** Save the current state in a new stack frame. 
   *
   *<p>	Pushing in place is marginally faster than the alternative,
   *	<code>stack = new CursorStack(this, true);</code> --
   *	the alternative generalizes better to subclasses, but there are
   *	also some cases where one simply <em>must</em> push in place, 
   *	for example when the CursorStack is being used as an Output.
   */
  protected void pushInPlace() { 
    stack = new CursorStack(depth, node, action, tagName, active, 
			    retainTree, atFirst, atLast, stack);
    sawChildren = false;
    depth++;
  }


  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  public final Node       getNode() 	{ 
    if (node == null) initialize();
    return node;
  }
  public final ActiveAttrList getAttributes() {
    if (node == null) initialize();
    return active.getAttrList();
  }
  public final String	  getNodeName() {
    if (node == null) initialize();
    return active.getNodeName();
  }
  public final ActiveNode getActive() 	{  
    if (node == null) initialize();
    return active;
  }
  public final int	  getDepth() 	{ return depth; }
  public final String 	  getTagName() 	{ return tagName; }

  /** Set the current node.  Set <code>active</code> and <code>element</code>
   *	if applicable. */
  protected void  setNode(Node aNode) {
    node   = aNode;
    active = (node instanceof ActiveNode)? (ActiveNode)node : null;
    action = (active == null)? null : active.getAction();

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      tagName = node.getNodeName();
    } else {
      tagName = null;
    }
  }

  protected void setNode(ActiveNode aNode) {
    node   = aNode;
    active = aNode;
    if (active != null) action = active.getAction();

    if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
      tagName = ((ActiveElement)active).getTagName();
    } else {
      tagName = null;
    }
  }

  protected void setNode(ActiveElement aNode, String aTagName) {
    node   = aNode;
    active = aNode;
    action = active.getAction();
    tagName = aTagName;
  }



  /************************************************************************
  ** Information:
  ************************************************************************/

  public Syntax getSyntax() {
    return (active == null)? null : active.getSyntax();
  }

  public Action getAction() 	{ return action; }

  public boolean atTop() 	{ return depth == 0; }
  public boolean atFirst() 	{ return atFirst; }

  /** This will have to be overridden if the tree is being built on the fly. */
  public boolean hasAttributes() {
    NamedNodeMap atts = node.getAttributes();
    return (atts != null) && (atts.getLength() > 0);
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveAttributes() {
    return hasAttributes();
  }

  /** This will have to be overridden if the tree is being built on the fly. */
  protected boolean hasChildren() {
    return node.hasChildNodes();
  }

  /** This should be overridden to if more information is available. */
  public boolean hasActiveChildren() {
    return hasChildren();
  }

  public String getTagName(int level) {
    if (level > depth) return null;
    else if (level == depth) return tagName;
    else if (stack == null) return null;
    else return stack.getTagName(level);
  }

  protected Node getNode(int level) {
    if (level > depth) return null;
    else if (level == depth) return node;
    else if (stack == null) return null;
    else return stack.getNode(level);
  }

  public boolean insideElement(String tag, boolean ignoreCase) {
    for (CursorStack frame = this; frame != null; frame = frame.stack) {
      if (frame.tagName == null) continue;
      if (ignoreCase && tag.equalsIgnoreCase(frame.tagName)) return true;
      else if (!ignoreCase && tag.equals(frame.tagName)) return true;
    }
    return false;
  }


  /************************************************************************
  ** Navigation Operations:
  ************************************************************************/

  /** Moves to the parent of the current Node.
   *	After calling <code>toParent</code>, <code>toNext</code> will
   *	move the Cursor to the parent's next sibling.
   */
  protected boolean toParent() {
    if (atTop()) return false;
    popInPlace();
    atFirst = false;
    atLast = false;
    sawChildren = true;
    return node != null;
  }

}
