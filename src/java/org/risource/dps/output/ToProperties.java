////// ToProperties.java:  Output to Properties

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


package org.risource.dps.output;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeGeneric;
import org.risource.dps.namespace.PropertyTable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Output to a Properties.<p>
 *
 * @version $Id: ToProperties.java,v 1.2 1999-11-17 18:33:54 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Namespace
 */
public class ToProperties extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Namespace namespace = null;
  protected Context context = null;
  protected Output bypass = null;

  /************************************************************************
  ** Access Methods:
  ************************************************************************/

  /** Get the Namespace under construction. */
  public Namespace getNamespace() 	{ return namespace; }

  /** Get the context in which bindings are made. */
  public Context getContext() 		{ return context; }

  /** Set the context in which bindings are made. */
  public void setContext(Context cxt) 	{ context = cxt; }

  /** Get the output to which to send non-bindings. */
  public Output getBypass() 		{ return bypass; }
  public void setBypass(Output v) 	{ bypass = v; }

  /************************************************************************
  ** Output Methods:
  ************************************************************************/

  public void putNode(Node aNode) {
    if (depth == 0) {
      bind(aNode);
    } else {
      super.putNode(aNode);
    }
  }

  /** Start a node.  
   *
   */
  public void startNode(Node aNode) {
    Node p = aNode.getParentNode();
    if (active == p && active != null) {	// already a child.  descend.
      if (p != null) descend();
      setNode(aNode);
      return;
    }
    if (p != null || aNode.hasChildNodes()) {
      aNode = Copy.copyNodeAsActive(aNode, tagset);
    }
    if (depth > 0) appendNode(aNode, active);
    descend();
    setNode(aNode);
  }

  public boolean toParent() {
    if (depth == 0) return false;
    if (depth != 1) return super.toParent();
    Node aNode = getNode();
    setNode((Node)null);
    depth--;
    if (depth == 0) appendNode(aNode, active);
    atFirst = false;
    return active != null;
  }

  protected void appendNode(Node aNode, Node aParent) {
    if (depth == 0)  	bind(aNode);
    else 		Copy.appendNode(aNode, aParent);
  }

  protected void bind(Node aNode) {
    ActiveNode binding = (ActiveNode) aNode;
    switch (binding.getNodeType()) {
    case Node.ENTITY_NODE: 
      namespace.setValueNodes(context, binding.getNodeName(),
			      binding.getValueNodes(context));
      break;
    case Node.ELEMENT_NODE:
      String name = (aNode instanceof TreeGeneric)
	? ((TreeGeneric)aNode).getName()
	: aNode.getNodeName();

      namespace.setBinding(name, binding);
      break;
    default: 
      if (bypass != null) bypass.putNode(aNode);
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToProperties(Tagset ts) {
    this(new PropertyTable(), ts);
  }

  public ToProperties(Namespace ns, Tagset ts) {
    super(ts);
    namespace = ns;
  }

}
