////// BasicProcessor.java: Document Processor basic implementation
//	$Id: BasicProcessor.java,v 1.16 2001-04-03 00:04:47 steve Exp $

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
 * @version $Id: BasicProcessor.java,v 1.16 2001-04-03 00:04:47 steve Exp $
 * @author steve@rii.ricoh.com
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
    do {
      copyCurrentNode(input.getNode());
    } while (input.toNext());
  }

  /** Run the Processor, constructing a new (possibly virtual) tree via its
   *	registered Output, until we either run out of input or the 
   *	<code>isRunning</code> flag is turned off.
   */
  public boolean run() {
    running = true;
    input.getNode();		// initialize the input if necessary
    processNode();
    while (running && input.toNext()) processNode();
    return running;
  }

  /** Process the current Node */
  public final void processNode() {
    Action handler = input.getAction();
    if (handler != null) {
      doAction(handler.getActionCode(), handler);
      // Equivalent to calling handler.action(input, this, output)
    } else {
      //System.err.println("No handler! " + logNode(input.getActive()));
      expandCurrentNode();
      // MUST BE equivalent to the default action for a node.
    }
  }

  /** Process the children of the current Node */
  public final boolean processChildren() {
    if (! input.toFirstChild()) return true;
    do {
      processNode();
    } while (running && input.toNext());
    input.toParent();
    return running;
  }

  /** Perform any additional action requested by the action routine. */
  protected final void doAction(int flag, Action handler) {
    //debug("   -> " + Action.actionNames[flag+1] + " (" + flag + ")\n");
    switch (flag) {
    case Action.COPY_NODE: copyCurrentNode(input.getNode()); return;
    case Action.ACTIVE_NODE: handler.action(input, this, output); return;
    case Action.EXPAND_NODE: expandCurrentNode(); return;
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

    // Crock for PI comments:
    if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE
	&& node.getNodeName().equals("--"))
      return;

    // No need to check for an entity; active ones use EntityHandler.
    if (input.hasActiveAttributes()) {
      ActiveElement oe = node.asElement();
      ActiveElement e = oe.editedCopy(expandAttrs(oe.getAttrList()), null);
      output.startNode(e);
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
      output.startNode(e);
      if (input.hasChildren()) { copyChildren(); }
      output.endElement(e.isEmptyElement() || e.implicitEnd());
    } else if (input.hasChildren() && ! node.hasChildNodes()) {
      copyCurrentNode(node);
    } else {
      output.putNode(node);
    }
  }

  public final void copyCurrentNode() {
    copyCurrentNode(input.getNode());
  }

  /** Copy the current node to the output.  
   *
   *	This is done by recursively traversing its children.
   */
  protected final void copyCurrentNode(Node n) {
    if (n == null) return;
    // Crock for PI comments:
    if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE
	&& n.getNodeName() != null && n.getNodeName().equals("--"))
      return;

    if (input.hasChildren() && ! n.hasChildNodes()) {
      // Copy recursively only if the node hasn't been fully parsed yet.
      if (!input.toFirstChild()) output.putNode(n);
      else {
	output.startNode(n);
	do {
	  copyCurrentNode(input.getNode());
	} while (input.toNext());
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
    if (! input.toFirstChild()) return;
    do { copyCurrentNode(input.getNode()); } while (input.toNext());
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
    ToNodeList dst = new ToNodeList(getTopContext().getTagset());
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
