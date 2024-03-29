////// GenericHandler.java: Node Handler generic implementation
//	$Id: GenericHandler.java,v 1.15 2001-04-03 00:04:18 steve Exp $

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


package org.risource.dps.handle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.namespace.BasicEntityTable;

import org.risource.dps.output.DiscardOutput;
import org.risource.dps.input.FromParseTree;
import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.tree.TreeNodeList;


/**
 * Generic implementation for an Element Handler. <p>
 *
 *	This is a Handler that contains enough additional state to be
 *	customized, via its attributes and content, to handle any syntax and
 *	semantics that can be specified without the use of primitives.  It is
 *	specialized for Elements.  Specialized subclasses should be based 
 *	on TypicalHandler. <p>
 *
 * @version $Id: GenericHandler.java,v 1.15 2001-04-03 00:04:18 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.handle.TypicalHandler
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.tagset.BasicTagset
 * @see org.risource.dps.Input 
 */

public class GenericHandler extends BasicHandler {

  /************************************************************************
  ** State Used for Syntax:
  ************************************************************************/

  /** If <code>true</code>, only Text in the content is retained. */
  protected boolean textContent = false;

  /** If <code>true</code>, only Text in the content is retained. */
  public boolean textContent() { return textContent; }

  /** If <code>true</code>, only Text in the content is retained. */
  public void setTextContent(boolean value) { textContent = value; }


  /************************************************************************
  ** State Used for Semantics:
  ************************************************************************/

  /** The class name of the nodes to construct. */
  protected String nodeClassName = null;

  /** The parse tree of the action to perform at expansion time. */
  protected ActiveNode expandAction = null;

  protected boolean hideExpansion = false;
  protected boolean passTag = false;
  protected boolean passContent = false;
  protected boolean deferContent = false;
  protected boolean beforeContent = false;

  /** If <code>true</code>, the tag is passed through. */
  public boolean passTag() { return passTag; }
  /** If <code>true</code>, the tag is passed through. */
  public void setPassTag(boolean value) { passTag = value; }
 
  /** If <code>true</code>, the content is passed through. */
  public boolean passContent() { return passContent; }
  /** If <code>true</code>, the content is passed through. */
  public void setPassContent(boolean value) { passContent = value; }
 
  /** If <code>true</code>, the content is expanded lazily. */
  public boolean deferContent() { return deferContent; }
  /** If <code>true</code>, the content is expanded lazily. */
  public void setDeferContent(boolean value) { deferContent = value; }
 
  /** If <code>true</code>, the content is processed after expansion. */
  public boolean beforeContent() { return beforeContent; }
  /** If <code>true</code>, the content is processed after expansion. */
  public void setBeforeContent(boolean value) { beforeContent = value; }
 
  /** If <code>true</code>, the expansion is hidden. */
  public boolean hideExpansion() { return hideExpansion; }
  /** If <code>true</code>, the expansion is hidden. */
  public void setHideExpansion(boolean value) { hideExpansion = value; }
 

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** We're assuming that this is an <em>active</em> node, so call
   *	the three-argument <code>action</code> routine to do the work.
   */
  public int getActionCode() {
    return Action.ACTIVE_NODE;
  }

  /** The default action for active elements is to obtain the expanded
   *	attribute list and content, then call the ``5-argument'' action
   *	method. 
   */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /** This routine does the setup for the ``5-argument'' action routine. 
   *
   * <p> It is provided as a separate method in case the normal three-argument
   *	 action routine is overridden, and we need the normal functionality in
   *	 a subclass.  <code>extractHandler</code> is a good example.
   *
   * @see org.risource.dps.handle.extractHandler
   */
  protected final void defaultAction(Input in, Context aContext, Output out) {
    ActiveNode node = in.getActive();
    if (node.getNodeType() != NodeType.ELEMENT) {
      nonElementAction(node, aContext, out, Expand.getContent(in, aContext));
      return;
    }
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    if (atts == null) atts = new TreeAttrList();
    if (passTag) {
      ActiveElement e = node.asElement();
      ActiveElement element = e.editedCopy(atts, null);
      out.startNode(element);
    }
    ActiveNodeList content = null;
    boolean empty = false;
    if (!in.hasChildren()) {
      empty = true;
      // aContext.debug("   no children...\n");
    } else if (beforeContent || deferContent) {
      // content processing is deferred, so we do nothing here
    } else if (textContent) {
	content = expandContent
	  ? Expand.getProcessedText(in, aContext)
	  : Expand.getText(in, aContext);
    } else {
	content = expandContent
	  ? Expand.getProcessedContent(in, aContext)
	  : Expand.getContent(in, aContext);
    }
    if (passContent) {
      Copy.copyNodes(content, out);
    }
    action(in, aContext,
	   (hideExpansion? new DiscardOutput() : out),
	   atts, content);
    if (passTag) {
      out.endElement(empty);
    }
  }

  /** This routine does the work; it should be overridden in specialized
   *	subclasses.  The generic action is to expand the handler's children.
   *
   *	<p>Note that the element we construct (in order to bind &amp;element;)
   *	is empty, and the expanded content is kept in a separate NodeList.
   *	This means that unexpanded nodes don't have to be reparented in the
   *	usual case.
   *
   *	<p>If the handler has no children, we simply copy the new Element to
   *	the Output.  This should be equivalent to the default action obtained
   *	by returning Action.EXPAND_NODE as an action code.
   *
   * @param the Input, with the current node being the one to be processed.
   * @param aContext the context in which to look up entity bindings
   * @param out the Output to which to send results
   * @param atts the (processed) attribute list.
   * @param content the (possibly-processed) content.  */
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    //aContext.debug("in action for " + in.getNode());
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);
    BasicEntityTable ents = null;
    ActiveNodeList wrapper = new TreeNodeList();

    // === We shouldn't ever have to copy the children here.
    // === Instead, make a special EntityTable that can construct the element
    // === if a value of ELEMENT is requested, or (better) construct a 
    // === pseudo-Element that behaves like one but doesn't have up-links.
    // === Supporting pseudo-Elements requires special hackery in the
    // === ParseListIterator, with a potential nodelist at each level.

    if (hasChildNodes()) {	
      // Children exists, so this is a defined action (macro). 

      // aContext.debug("expanding<"+in.getTagName()+"> "+getChildNodes()+"\n");

      // Create a suitable sub-context for the expansion:
      //    === not clear if entities should still be lowercase.  For now...
      //    === This may want to go into defaultAction ===

      Tagset ts = aContext.getTopContext().getTagset();
      ents = new BasicEntityTable(e.getTagName());
      if (deferContent) {
	in.toFirstChild();
	ents.setBinding("content", new EntityInput("content", in));
      } else {
	ents.setValueNodes(aContext, "content", content, ts);
      }
      if (beforeContent) ents.setValueNodes(aContext, "wrapper", wrapper, ts);
      ents.setValueNodes(aContext, "tagname",
			 Create.createNodeList(e.getTagName()), ts);
      ents.setValueNodes(aContext, "element", new TreeNodeList(element), ts);
      ents.setValueNodes(aContext, "attributes", atts.asNodeList(), ts);
      // ... in which to expand this Actor's definition
      Input def = new FromParseTree(this);
      Processor p = aContext.subProcess(def, out, ents);
      // ... Expand the definition in the sub-context
      p.processChildren();
    } else if (content == null) {
      // No content: just put the new element. 
      out.putNode(element);
    } else {
      // Content: output an expanded copy of the original element.
      out.startNode(element);
      Copy.copyNodes(content, out);
      out.endElement(e.isEmptyElement() || e.implicitEnd());
    }
    if (beforeContent) {
      wrapper = ents.getValueNodes(aContext, "wrapper");
      String wrapTag = null;
      if (wrapper != null && wrapper.getLength() != 0)
	wrapTag = wrapper.toString();

      if (wrapTag != null && "#skip".equalsIgnoreCase(wrapTag)) {
	if (in.hasChildren()) {
	  Copy.copyChildren(in, new DiscardOutput());
	}
      } else if (wrapTag != null && "#hide".equalsIgnoreCase(wrapTag)) {
	if (in.hasChildren()) {
	  if (expandContent) {
	    Processor p = aContext.subProcess(in, new DiscardOutput(), ents);
	    p.processChildren();
	  } else {
	    Copy.copyChildren(in, new DiscardOutput());
	  }
	}
      } else {
	if (wrapTag != null) out.startElement(wrapTag, null);
	if (in.hasChildren()) {
	  Processor p = aContext.subProcess(in, out, ents);
	  if (expandContent) {
	    p.processChildren();
	  } else {
	    ((org.risource.dps.process.BasicProcessor)p).copyChildren();
	  }
	}
	if (wrapTag != null) out.endNode();
      }

      content = ents.getValueNodes(aContext, "content");
      if (content != null) Copy.copyNodes(content, out);
    }
  }

  /** Perform default action for a non-Element node. 
   *
   *<p> The action is run with <code>name</code> and <code>value</code> 
   *	bound to the <code>nodeName</code> and <code>nodeValue</code> 
   *	respectively.  
   *	=== fails when a node has element content, e.g. for attributes 
   */
  public void nonElementAction(ActiveNode aNode, Context aContext, Output out,
			       ActiveNodeList content) {
    ActiveNodeList value = Create.createNodeList(aNode.getNodeValue());
    
    if (hasChildNodes()) {	
      // Children exists, so this is a defined action (macro). 
      // Create a suitable sub-context for the expansion:

      Tagset ts = aContext.getTopContext().getTagset();
      BasicEntityTable ents = new BasicEntityTable(aNode.getNodeName());
      if (value != null) 
	ents.setValueNodes(aContext, "value", value, ts);
      ents.setValueNodes(aContext, "content", content, ts);
      if (aNode.getNodeName() != null) 
	ents.setValueNodes(aContext, "name",
			   Create.createNodeList(aNode.getNodeName()), ts);
      // ... in which to expand this Actor's definition
      Input def = new FromParseTree(this);
      Processor p = aContext.subProcess(def, out, ents);
      // ... Expand the definition in the sub-context
      p.processChildren();
    } else if (content == null) {
      // No content: just put the new element. 
      out.putNode(aNode);
    } 
  }

  public ActiveNodeList getValue(Node aNode, Context aContext) {
    return null;
  }

  public ActiveNodeList getValue(String aName, ActiveNode aNode,
				 Context aContext) {
    return null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public GenericHandler() {}

  /** Construct a GenericHandler for a passive element. 
   *
   * @param syntax see codes in <a href="org.risource.dps.Syntax.html">Syntax</a>
   * @see org.risource.dps.handle.AbstractHandler#getSyntaxCode
   * @see org.risource.dps.Syntax
   */
  public GenericHandler(int syntax) {
    super(syntax);
    if (syntax != 0) {
      expandContent = (syntax & Syntax.NO_EXPAND) == 0;
    }
  }

  /** Construct a GenericHandler for a passive element. 
   *
   * @param empty     if <code>true</code>, the element has no content
   *	and expects no end tag.  If <code>false</code>, the element
   *	<em>must</em> have an end tag.
   * @param parseElts if <code>true</code> (default), recognize elements in
   *	the content.
   * @param parseEnts if <code>true</code> (default), recognize entities in
   *	the content.
   * @see org.risource.dps.handle.AbstractHandler#getSyntaxCode
   * @see org.risource.dps.Syntax
   */
  public GenericHandler(boolean empty, boolean parseElts, boolean parseEnts) {
    super(empty, parseElts, parseEnts);
  }

}
