////// BasicProcessor.java: Document Processor basic implementation
//	$Id: BasicProcessor.java,v 1.9 1999-06-17 01:05:27 steve Exp $

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


package org.risource.dps.process;

import org.w3c.dom.*;		// This needs to be revisited

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.tree.TreeElement;

/**
 * A minimal implementation for a document Processor. <p>
 *
 * @version $Id: BasicProcessor.java,v 1.9 1999-06-17 01:05:27 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Output
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Action
 */

public class BasicProcessor extends ContextStack implements Processor {

 /************************************************************************
  ** Accessors:
  ************************************************************************/

  public Processor getProcessor() { return this; }

  /************************************************************************
  ** Processing:
  ************************************************************************/

  protected boolean running = true;

  public boolean isRunning() { return running; }
  public void stop() { running = false; }

  /** Copy nodes from the input to the output. */
  public void copy() {
    Node n = input.getNode();
    while (n != null) {
      copyCurrentNode(n);
      n = input.toNextSibling();
    }
  }

  /** Run the Processor, constructing a new (possibly virtual) tree via its
   *	registered Output, until we either run out of input or the 
   *	<code>isRunning</code> flag is turned off.
   */
  public boolean run() {
    running = true;
    input.getNode();		// initialize the input if necessary
    processNode();
    while (running && input.toNextSibling() != null) processNode();
    return running;
  }

  /** Process the current Node */
  public final void processNode() {
    Action handler = input.getAction();
    if (handler != null) {
      additionalAction(handler.actionCode(input, this));
      // MUST BE equivalent to: handler.action(input, this, output);
    } else {
      //System.err.println("No handler! " + logNode(input.getActive()));
      expandCurrentNode();
      // MUST BE equivalent to the default action for a node.
    }
  }

  /** Process the children of the current Node */
  public final boolean processChildren() {
    Node node = input.toFirstChild();
    if (node == null) return true;
    for (;
	 node != null && running;
	 node = input.toNextSibling()) {
      processNode();
    }
    input.toParent();
    return running;
  }

  /** Perform any additional action requested by the action routine. */
  protected final void additionalAction(int flag) {
    //debug("   -> " + Action.actionNames[flag+1] + " (" + flag + ")\n");
    switch (flag) {
    case Action.COPY_NODE: copyCurrentNode(input.getNode()); return;
    case Action.COMPLETED: return;
    case Action.EXPAND_NODE: expandCurrentNode(); return;
    case Action.EXPAND_ATTS: expandCurrentAttrs(); return;
    case Action.PUT_NODE: putCurrentNode(); return;
    }
  }

  /************************************************************************
  ** Convenience and Utility Methods:
  ************************************************************************/

  /** Process the current node in the default manner, expanding entities
   *	in its attributes and processing its children re-entrantly.
   */
  public final void expandCurrentNode() {
    ActiveNode node = input.getActive();
    if (node == null) return;

    // No need to check for an entity; active ones use EntityHandler.
    if (input.hasActiveAttributes()) {
      ActiveElement oe = node.asElement();
      ActiveElement e = oe.editedCopy(expandAttrs(oe.getAttrList()), null);
      output.startElement(e);
      if (input.hasChildren()) { processChildren(); }
      output.endElement(e.isEmptyElement() || e.implicitEnd());
    } else if (input.hasChildren()) {
      output.startNode(node);
      if (input.hasChildren()) processChildren();
      output.endNode();
    } else {
      output.putNode(node);
    }
  }

  /** Process the current node by expanding entities in its attributes, but
   *	blindly copying its children (content).
   */
  public final void expandCurrentAttrs() {
    ActiveNode node = input.getActive();
    if (input.hasActiveAttributes()) {
      ActiveElement oe = node.asElement();
      ActiveElement e =
	new TreeElement(oe, expandAttrs(oe.getAttrList()));
      output.startElement(e);
      if (input.hasChildren()) { copyChildren(); }
      output.endElement(e.isEmptyElement() || e.implicitEnd());
    } else if (input.hasChildren() && ! node.hasChildNodes()) {
      copyCurrentNode(node);
    } else {
      output.putNode(node);
    }
  }

  /** Copy the current node to the output.  
   *
   *	This is done by recursively traversing its children.
   */
  protected final void copyCurrentNode(Node n) {
    if (input.hasChildren() && ! n.hasChildNodes()) {
      // Copy recursively only if the node hasn't been fully parsed yet.
      Node c = input.toFirstChild();
      if (c == null) output.putNode(n);
      else {
	output.startNode(n);
	for ( ; c != null; c = input.toNextSibling()) {
	  copyCurrentNode(c);
	}
	input.toParent();
	output.endNode();
      }
    } else {
      output.putNode(n);
    }
  }

  /** Copy the current node to the output.  Recursion is not needed.
   *
   *	This is commonly used for, e.g., Text nodes.
   */
  public final void putCurrentNode() {
    Node n = input.getNode();
    output.putNode(n);
  }

  /** Copy the children of the current Node */
  public final void copyChildren() {
    Node node = input.toFirstChild();
    if (node == null) return;
    for (;
	 node != null;
	 node = input.toNextSibling()) {
      copyCurrentNode(node);
    }
    input.toParent();
  }

  /* === Many of the following could be done using a suitable Input. === */

  /** Copy nodes in a nodelist. */
  public void copyNodes(NodeList nl) {
    Copy.copyNodes(nl, output);
  }

  /** Expand entities in the attributes of the current Node.
   */
  public ActiveAttrList expandAttrs(ActiveAttrList attrs) {
    return Expand.expandAttrs(this, attrs);
  }

  /** Expand entities in the value of a given attribute. */
  public void expandAttribute(ActiveAttr att,  ActiveElement e) {
    e.setAttributeValue(att.getName(), expandNodes(att.getValueNodes(null)));
  }

  /** Expand nodes in a nodelist. */
  public ActiveNodeList expandNodes(ActiveNodeList nl) {
    if (nl == null) return null;
    ToNodeList dst = new ToNodeList();
    expandNodes(nl, dst);
    return dst.getList();
  }

  public void expandNodes(ActiveNodeList nl, Output dst) {
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode n = nl.activeItem(i);
      if (n.getNodeType() == Node.ENTITY_NODE) {
	expandEntity((ActiveEntity) n, dst);
      } else if (n.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
	expandEntityRef((EntityReference) n, dst);
      } else {
	dst.putNode(n);
      }
    }
  }

  /** Expand a single entity. */
  public void expandEntity(ActiveEntity n, Output dst) {
    NodeList value = n.getValueNodes(this);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }

  /** Expand an entity reference. */
  public void expandEntityRef(EntityReference n, Output dst) {
    String name = n.getNodeName();
    NodeList value = Index.getIndexValue(this, name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicProcessor() {}

  public BasicProcessor(Input in, Context prev, Output out, Namespace ents) {
    super(in, prev, out, ents);
  }

  public BasicProcessor(Input in, Context prev, Output out) {
    super(in, prev, out);
  }

  /** Return a new BasicProcessor copied from an old one. */
  public BasicProcessor(BasicProcessor p) {
    super(p);
    stack = p;
  }

}
