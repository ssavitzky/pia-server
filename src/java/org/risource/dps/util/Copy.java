////// Copy.java: Utilities for Copying nodes.
//	$Id: Copy.java,v 1.6 1999-06-04 22:40:50 steve Exp $

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

import org.w3c.dom.*;

import org.risource.dps.active.*;

import org.risource.dps.tree.*;
import org.risource.dps.output.*;
import org.risource.dps.*;

/**
 * Node-copying utilities (static methods) for a Document Processor. 
 *
 * @version $Id: Copy.java,v 1.6 1999-06-04 22:40:50 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.util.Expand
 */

public class Copy {

  /************************************************************************
  ** Copying to an Output
  ************************************************************************/

  /** Copy the current Node from the given Input to the given Output 
   *
   * === It should be possible to do this non-recursively by keeping track
   * === of depth.  Note that we're using the Input's stack for state.
   */
  public static final void copyNode(Node n, Input in, Output out) {
    if (n == null) n = in.getNode();
    if (in.hasChildren() && ! n.hasChildNodes()) {
      // Copy recursively only if the node hasn't been fully parsed yet.
      out.startNode(n);
      copyChildren(in, out);
      out.endNode();
    } else {
      out.putNode(n);
    }
  }

  /** Copy the children of the input's current Node
   *
   * === It should be possible to do this non-recursively by keeping track
   * === of depth.  Note that we're using the Input's stack for state.
   */
  public static final void copyChildren(Input in, Output out) {
    for (Node n = in.toFirstChild(); n != null; n = in.toNextSibling()) {
      copyNode(n, in, out);
    }
    in.toParent();
  }

  /** Copy nodes from an Input to an Output. */
  public static final void copyNodes(Input in, Output out) {
    for (Node n = in.getNode(); n != null; n = in.toNextSibling()) {
      copyNode(n, in, out);
    }
  }

  /** Copy the content of a NodeList to an Output.
   */
  public static void copyNodes(NodeList aNodeList, Output out) {
    if (aNodeList == null) return;
    int n = aNodeList.getLength();
    for (int i = 0; i < n ; ++i) {
      out.putNode(aNodeList.item(i));
    }
  }

  /** Copy the content of a NamedNodeMap to an Output.
   */
  public static void copyNodes(NamedNodeMap aNodeList, Output out) {
    if (aNodeList == null) return;
    int n = aNodeList.getLength();
    for (int i = 0; i < n ; ++i) {
      out.putNode(aNodeList.item(i));
    }
  }

  /************************************************************************
  ** Copying from a Node to the return value:
  ************************************************************************/

  public static ActiveAttrList copyAttrs(ActiveAttrList atts) {
    ToAttributeList dst = new ToAttributeList();
    copyNodes(atts, dst);
    return dst.getList();
  }

  public static ActiveNode copyNodeAsActive(Node node) {
    if (node instanceof ActiveNode) 
      return ((ActiveNode)node).shallowCopy();
    int nodeType = node.getNodeType();
    switch (nodeType) {
    case Node.ELEMENT_NODE: 
      return new TreeElement(node.getNodeName(), node.getAttributes());

    case Node.TEXT_NODE:
      if (node instanceof ActiveText) {
	ActiveText t = (ActiveText) node;
	return new TreeText(t.getData(), t.getIsIgnorable());
      } else {
	return new TreeText(node.getNodeValue());
      }

    case Node.COMMENT_NODE: 
      return new TreeComment(node.getNodeValue());

    case Node.PROCESSING_INSTRUCTION_NODE:
      return new TreePI(node.getNodeName(), node.getNodeValue());

    case Node.ATTRIBUTE_NODE: 
      ActiveAttr attr = (ActiveAttr)node;
      return new TreeAttr(attr.getName(), attr.getValueNodes(null));
      // there are missing cases and botched conversions here.

    default: 
      return null;		// node.shallowCopy();
    }
  }

  /************************************************************************
  ** Appending (copying into children):
  ************************************************************************/


  /** Append a Node to a given parent.
   *	If the new Node is already a child of <code>parentNode</code>, nothing
   *	happens.  If its parent is non-<code>null</code> and
   *	<em>different</em> from the <code>parentNode</code>, it will be
   *	reparented.  If the Node's type is NODELIST, its children will be
   *	appended (spliced in). <p>
   *
   * @param aNode the node to be appended.
   * @param parentNode the node to be appended to.
   * @return <code>parentNode</code>.
   */
  public static Node appendNode(Node aNode, Node parentNode) {
    // No node to append to: do nothing.
    if (parentNode == null) return null;
    // Current node is the parent: already taken care of.
    if (aNode.getParentNode() == parentNode) return parentNode;
    // A NodeList in disguise -- append its children:
    if (aNode.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
      if (aNode.hasChildNodes())
	appendNodes(aNode.getChildNodes(), parentNode);
      return parentNode;
    }
    try {
      // Different parent: deep-copy it.
      if (aNode.getParentNode() != null) { 
	aNode = ((ActiveNode)aNode).deepCopy();
      }
      parentNode.insertBefore(aNode, null);
    } catch (DOMException e) {
      System.err.println("Cloning failed: ");
      e.printStackTrace(System.err);
      // === not clear what to do here...  shouldn't happen. ===
    }
    return parentNode;
  }

  /** Append the nodes in a NodeList to a given parent.
   *
   * @return parentNode.
   * @see #appendNode
   */
  public static Node appendNodes(NodeList aNodeList, Node parentNode) {
    if (aNodeList == null) return parentNode;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; i++) {
      parentNode.appendChild(aNodeList.item(i));
    }
    return parentNode;
  }

  /** Append the nodes in a NodeList to a given Active parent.
   *
   * @return parentNode.
   * @see #appendNode
   */
  public static ActiveNode appendNodes(NodeList aNodeList,
				       ActiveNode parentNode) {
    if (aNodeList == null) return parentNode;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; i++) {
      parentNode.addChild((ActiveNode)aNodeList.item(i));
    }
    return parentNode;
  }

}
