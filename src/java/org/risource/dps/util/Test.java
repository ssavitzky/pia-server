////// Test.java: Utilities for testing nodes and strings
//	$Id: Test.java,v 1.5 1999-04-07 23:22:18 steve Exp $

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

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.output.*;


/**
 * Test utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for computing tests
 *	(booleans) on nodes and strings.
 *
 * @version $Id: Test.java,v 1.5 1999-04-07 23:22:18 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class Test {

  /************************************************************************
  ** Tests on Strings:
  ************************************************************************/

  /** Determine whether a string consists entirely of whitespace.
   */
  public static boolean isWhitespace(String s) {
    if (s == null) return true;
    for (int i = 0; i < s.length(); ++i) 
      if (! Character.isWhitespace(s.charAt(i))) return false;
    return true;
  }

  /************************************************************************
  ** Tests on Nodes:
  ************************************************************************/

  /** Determine whether a Node should be considered <code>true</code> as
   *	a boolean.  Essentially whitespace, comments, and unbound entities
   *	are considered false; everything else is true.
   */
  public static boolean trueValue(ActiveNode aNode) {
    if (aNode == null) return false;
    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case Node.ELEMENT_NODE: 
      return true;

    case Node.ENTITY_NODE: 
    case Node.ENTITY_REFERENCE_NODE: 
    case Node.TEXT_NODE:
      return ! isWhitespace(aNode.getNodeValue());

    case Node.COMMENT_NODE: 
      return false;

    case Node.PROCESSING_INSTRUCTION_NODE:
      return true;

    case Node.ATTRIBUTE_NODE: 
      ActiveAttr attr = (ActiveAttr)aNode;
      if (! attr.getSpecified()) return true;
      return orValues(attr.getValueNodes(null));

    case Node.DOCUMENT_FRAGMENT_NODE: 
      return orValues(aNode.getContent());

    default: 
      return true;
    }
  }

  /** Determine whether a Node has a true value in a given context. 
   *	If it does, return that value, otherwise return <code>null</code>
   */
  public static ActiveNodeList getTrueValue(ActiveNode aNode,
					    Context aContext) {
    if (aNode == null) return null;
    ActiveNodeList v = new TreeNodeList(aNode);

    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case Node.TEXT_NODE:
      return trueValue(aNode)? v : null;  

    case Node.COMMENT_NODE: 
      return null;

    case Node.ATTRIBUTE_NODE: 
      ActiveAttr attr = (ActiveAttr)aNode;
      if (! attr.getSpecified()) return v;
      return orValues(attr.getValueNodes(null))
	? attr.getValueNodes(null) : null;
    }

    v = Expand.processNodes(v, aContext);
    return (orValues(v))? v : null;
  }

  /** Determine whether a Node has a true value in a given context. 
   */
  public static boolean trueValue(ActiveNode aNode, Context aContext) {
    return getTrueValue(aNode, aContext) != null;
  }

  /** Determine whether <em>all</em> the items in a nodeList are true.
   *	An empty list is considered <em>true</em>, because all of
   *	its elements are true.
   */
  public static boolean andValues(ActiveNodeList aNodeList) {
    if (aNodeList == null) return true;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; ++i) {
      /** === Ignore whitespace === */
      if (! trueValue(aNodeList.activeItem(i))) return false;
    }
    return true;
  }

  /** Determine whether <em>all</em> the items in a nodeList are true in a
   *	given context.  */
  public static boolean andValues(ActiveNodeList aNodeList, Context aContext) {
    if (aNodeList == null) return true;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; ++i) {
      /** === Ignore whitespace === */
      if (! trueValue(aNodeList.activeItem(i), aContext)) return false;
    }
    return true;
  }

  /** Determine whether <em>any</em> of the items in a nodeList are true. 
   *	An empty list is considered <em>false</em> because it contains
   *	no true elements.
   */
  public static boolean orValues(ActiveNodeList aNodeList) {
    if (aNodeList == null) return false;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; ++i) {
      if (trueValue(aNodeList.activeItem(i))) return true;
    }
    return false;
  }

  /** Determine whether <em>any</em> of the items in a nodeList are true
   *	in a given context.
   */
  public static boolean orValues(ActiveNodeList aNodeList, Context aContext) {
    if (aNodeList == null) return false;
    int len = aNodeList.getLength();
    for (int i = 0; i < len; ++i) {
      if (trueValue((ActiveNode)aNodeList.item(i), aContext)) return true;
    }
    return false;
  }

}



