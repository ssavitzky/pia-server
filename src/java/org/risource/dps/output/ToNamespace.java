////// ToNamespace.java:  Output to Namespace

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
import org.risource.dps.namespace.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Output to a Namespace.<p>
 *
 * @version $Id: ToNamespace.java,v 1.5 1999-11-17 18:33:54 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Namespace
 */
public class ToNamespace extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Namespace namespace = null;
  protected String nameAttr = "name";
  protected String bindingTag = "BIND";
  protected Context context = null;
  protected Output bypass = null;

  /************************************************************************
  ** Access Methods:
  ************************************************************************/

  /** Get the Namespace under construction. */
  public Namespace getNamespace() 	{ return namespace; }

  /** Get the name of the attribute that contains an element's name.
   *	This is used instead of the NodeName.
   */
  public String getNameAttr() 		{ return nameAttr; }

  /** Set the name of the attribute that contains an element's name. */
  public void setNameAttr(String v) 	{ nameAttr = v; }

  /** Get the tagname of elements used to represent bindings.
   */
  public String getBindingTag()		{ return bindingTag; }

  /** Set the tagname we expect for bindings. */
  public void setBindingTag(String v) 	{ bindingTag = v; }

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
   * <p> NOTE: If the node is an element that matches bindingTag and has a
   *	 "name" attribute, it would be best to collect its contents and bind
   *	 it with setValueNodes.  This will avoid cloning the entire contents.
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
      namespace.setBinding(binding.getNodeName(), binding);
      break;
    case Node.ELEMENT_NODE:
      if (binding instanceof Namespace) {
	Namespace ns = (Namespace) binding;
	namespace.setBinding(ns.getName(), new EntityWrap(ns.getName(), ns));
      } else
      if (bindingTag != null && !bindingTag.equals(binding.getNodeName())) {
	if (bypass != null) bypass.putNode(aNode);
      } else {
	namespace.setValueNodes(context,
				binding.asElement().getAttribute(nameAttr),
				binding.getValueNodes(context));
      }
      break;
    default: 
      if (bypass != null) bypass.putNode(aNode);
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToNamespace(Tagset ts) {
    this(new BasicNamespace(), ts);
  }

  public ToNamespace(Namespace ns, Tagset ts) {
    super(ts);
    namespace = ns;
  }

}
