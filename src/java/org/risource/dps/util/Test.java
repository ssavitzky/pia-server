////// Test.java: Utilities for testing nodes and strings
//	Test.java,v 1.9 1999/03/01 23:47:02 pgage Exp

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


package crc.dps.util;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Entity;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.*;


/**
 * Test utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for computing tests
 *	(booleans) on nodes and strings.
 *
 * @version Test.java,v 1.9 1999/03/01 23:47:02 pgage Exp
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
  public static boolean trueValue(Node aNode) {
    if (aNode == null) return false;
    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case NodeType.ELEMENT: 
      return true;

    case NodeType.ENTITY: 
      return aNode.hasChildren();

    case NodeType.TEXT:
      crc.dom.Text t = (crc.dom.Text)aNode;
      if (t.getIsIgnorableWhitespace()) return false;
      return ! isWhitespace(t.getData());

    case NodeType.COMMENT: 
      return false;

    case NodeType.PI:
      return true;

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)aNode;
      if (! attr.getSpecified()) return true;
      return orValues(attr.getValue());

    case NodeType.NODELIST: 
      return orValues(aNode.getChildren());

    default: 
      return true;
    }
  }

  /** Determine whether a Node has a true value in a given context. 
   *	If it does, return that value, otherwise return <code>null</code>
   */
  public static NodeList getTrueValue(ActiveNode aNode, Context aContext) {
    if (aNode == null) return null;
    ParseNodeList v = new ParseNodeList(aNode);

    int nodeType = aNode.getNodeType();
    switch (nodeType) {
    case NodeType.TEXT:
      return trueValue(aNode)? v : null;  

    case NodeType.COMMENT: 
      return null;

    case NodeType.ATTRIBUTE: 
      crc.dom.Attribute attr = (crc.dom.Attribute)aNode;
      if (! attr.getSpecified()) return v;
      return orValues(attr.getValue())? attr.getValue() : null;
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
  public static boolean andValues(NodeList aNodeList) {
    if (aNodeList == null) return true;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (! trueValue(node)) return false;
    }
    return true;
  }

  /** Determine whether <em>all</em> the items in a nodeList are true in a
   *	given context.  */
  public static boolean andValues(NodeList aNodeList, Context aContext) {
    if (aNodeList == null) return true;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (! trueValue((ActiveNode)node, aContext)) return false;
    }
    return true;
  }

  /** Determine whether <em>any</em> of the items in a nodeList are true. 
   *	An empty list is considered <em>false</em> because it contains
   *	no true elements.
   */
  public static boolean orValues(NodeList aNodeList) {
    if (aNodeList == null) return false;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (trueValue(node)) return true;
    }
    return false;
  }

  /** Determine whether <em>any</em> of the items in a nodeList are true
   *	in a given context.
   */
  public static boolean orValues(NodeList aNodeList, Context aContext) {
    if (aNodeList == null) return false;
    NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      if (trueValue((ActiveNode)node, aContext)) return true;
    }
    return false;
  }


}
