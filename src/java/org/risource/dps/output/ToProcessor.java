////// ToProcessor: markup-only filter for an Output
//	$Id: ToProcessor.java,v 1.1 1999-07-14 20:20:49 steve Exp $

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

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.input.FromParseTree;

import java.io.PrintStream;

/** Output to a Processor.
 *
 * <p>	This class is used as a ``shim'' to make a Processor look like
 *	an Output.  The basic idea is simply to copy all nodes to the
 *	Processor's own output until we hit an active one, at which point
 *	we collect its parse tree and fire off the Processor on it.
 *
 * <p> 	There is a complication, of course: some tags are quoted, and so
 *	have to be copied without processing.  The decision: collect, proxy,
 *	or quote, is made on the basis of the <code>state</code> variable. 
 *
 * @version $Id: ToProcessor.java,v 1.1 1999-07-14 20:20:49 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public class ToProcessor extends Proxy {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Processor   processor;
  protected Tagset      tagset = null;
  protected ToParseTree	collect = null;

  /** The current processing state.  
   * <dl>
   *	<dt> &lt; 0
   *	<dd> Collect an active tag and its content.
   *	<dt> = 0
   *	<dd> Pass passive tags, process active ones. 
   *	<dt> &gt; 0
   *	<dd> Copy <em>all</em> tags.
   * </dl>
   */
  protected int		state = 0;

  protected void initialize() {
    target = processor.getOutput();
    tagset = processor.getTopContext().getTagset();
  }

  protected Action getAction(Node aNode) {
    return (aNode instanceof ActiveNode)
      ? ((ActiveNode)aNode).getAction()
      : getAction(aNode.getNodeType(), aNode.getNodeName());
  }

  protected Action getAction(short nodeType, String nodeName) {
    return (nodeType == Node.ELEMENT_NODE)
      ? getAction(nodeType, null)
      : tagset.getHandlerForType(nodeType);
  }

  protected Action getAction(String tagname, NamedNodeMap attrs) {
    return tagset.getHandlerForTag(tagname);
  }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.putNode(aNode);
    } else if (state > 0) {	// Passing.
      target.putNode(aNode);
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(aNode);
      if (action == null) target.putNode(aNode);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE: target.putNode(aNode); return;
      case Action.EXPAND_NODE: target.putNode(aNode); return;
      case Action.PUT_NODE: target.putNode(aNode); return;
      case Action.ACTIVE_NODE: 
	// Since we have the whole node, just run the Processor on it.
	collect=new ToParseTree(null, tagset);
	collect.putNode(aNode);
 	processor.setInput(new FromParseTree(collect.getRoot()));
	processor.processNode();
      }
    }
  }
  public void startNode(Node aNode) { 
    depth++;
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.startNode(aNode);
      state--;
    } else if (state > 0) {	// Passing.
      target.startNode(aNode);
      state++;
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(aNode);
      if (action == null) target.startNode(aNode);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE:
      case Action.PUT_NODE:
	state++;		// Positive state: keep copying until zero.
	target.startNode(aNode);
	return;
      case Action.EXPAND_NODE:
	state--;		// Negative state: collect until zero
	collect.startNode(aNode);
	return;
      case Action.ACTIVE_NODE: 
	state--;		// Set state negative and start collecting.
	collect = new ToParseTree(null, tagset);
	collect.putNode(aNode);
      }
    }
  }
  public boolean endNode() { 
    depth--;
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.endNode();
      state++;
      if (state == 0) {		// Done collecting.  Process.
 	processor.setInput(new FromParseTree(collect.getRoot()));
	processor.processNode();
      }
    } else if (state > 0) {	// Passing.
      target.endNode();
      state--;
    } else { /* state == 0 */	// Passing. 
      target.endNode();
    }
    return depth >= 0;
  }
  public void startElement(String tagname, NamedNodeMap attrs) {
    depth++;
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.startElement(tagname, attrs);
      state--;
    } else if (state > 0) {	// Passing.
      target.startElement(tagname, attrs);
      state++;
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(tagname, attrs);
      if (action == null) target.startElement(tagname, attrs);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE:
      case Action.PUT_NODE:
	state++;		// Positive state: keep copying until zero.
	target.startElement(tagname, attrs);
	return;
      case Action.EXPAND_NODE:
	state--;		// Negative state: collect until zero
	collect.startElement(tagname, attrs);
	return;
      case Action.ACTIVE_NODE: 
	state--;		// Set state negative and start collecting.
	collect = new ToParseTree(null, tagset);
	collect.startElement(tagname, attrs);
      }
    }
  }
  public boolean endElement(boolean optional) {
    depth --;
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.endNode();
      state++;
      if (state == 0) {		// Done collecting.  Process.
 	processor.setInput(new FromParseTree(collect.getRoot()));
	processor.processNode();
      }
    } else if (state > 0) {	// Passing.
      target.endElement(optional);
      state--;
    } else { /* state == 0 */	// Passing. 
      target.endElement(optional);
    }
    return depth >= 0;
  }

  public void putNewNode(short nodeType, String nodeName, String value) {
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.putNewNode(nodeType, nodeName, value);
    } else if (state > 0) {	// Passing.
      target.putNewNode(nodeType, nodeName, value);
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(nodeType, nodeName);
      if (action == null) target.putNewNode(nodeType, nodeName, value);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE: 
	target.putNewNode(nodeType, nodeName, value); return;
      case Action.EXPAND_NODE:
	target.putNewNode(nodeType, nodeName, value); return;
      case Action.PUT_NODE:
	target.putNewNode(nodeType, nodeName, value); return;
      case Action.ACTIVE_NODE: 
	// Since we have the whole node, just run the Processor on it.
	ActiveNode aNode = tagset.createActiveNode(nodeType, nodeName, value);
 	processor.setInput(new FromParseTree(aNode));
	processor.processNode();
      }
    }
  }
  public void startNewNode(short nodeType, String nodeName) {
    if (nodeType == Node.ELEMENT_NODE) {
      startElement(nodeName, null);
      return;
    }
    depth++;
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.startNewNode(nodeType, nodeName);
      state--;
    } else if (state > 0) {	// Passing.
      target.startNewNode(nodeType, nodeName);
      state++;
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(nodeType, nodeName);
      if (action == null) target.startNewNode(nodeType, nodeName);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE:
      case Action.PUT_NODE:
	state++;		// Positive state: keep copying until zero.
	target.startNewNode(nodeType, nodeName);
	return;
      case Action.EXPAND_NODE:
	state--;		// Negative state: collect until zero
	collect.startNewNode(nodeType, nodeName);
	return;
      case Action.ACTIVE_NODE: 
	state--;		// Set state negative and start collecting.
	collect = new ToParseTree(null, tagset);
	collect.startNewNode(nodeType, nodeName);
      }
    }
  }
  public void putCharData(short nodeType, String nodeName,
			  char[] buffer, int start, int length) {
    if (target == null) initialize();
    if (state < 0) {		// Collecting.
      collect.putCharData(nodeType, nodeName, buffer, start, length);
    } else if (state > 0) {	// Passing.
      target.putCharData(nodeType, nodeName, buffer, start, length);
    } else { /* state == 0 */	// Have to think about it.
      Action action = getAction(nodeType, nodeName);
      if (action == null) target.putCharData(nodeType, nodeName,
					     buffer, start, length);
      else switch (action.getActionCode()) {
      case Action.COPY_NODE: 
	target.putCharData(nodeType, nodeName, buffer, start, length); return;
      case Action.EXPAND_NODE:
	target.putCharData(nodeType, nodeName, buffer, start, length); return;
      case Action.PUT_NODE:
	target.putCharData(nodeType, nodeName, buffer, start, length); return;
      case Action.ACTIVE_NODE: 
	// Since we have the whole node, just run the Processor on it.
	ActiveNode aNode =
	  tagset.createActiveNode(nodeType, nodeName,
				  new String(buffer, start, length));
 	processor.setInput(new FromParseTree(aNode));
	processor.processNode();
      }
    }
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ToProcessor(Processor aProcessor) {
    super(null);
    processor = aProcessor;
  }

}
