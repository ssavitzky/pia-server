////// Create.java: Utilities for Creating nodes.
//	$Id: Create.java,v 1.3 1999-03-12 19:28:10 steve Exp $

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

import org.risource.dom.Node;
import org.risource.dom.Element;
import org.risource.dom.NodeList;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;
import org.risource.dom.Entity;

import org.risource.dps.NodeType;
import org.risource.dps.active.*;
import org.risource.dps.output.*;

import org.risource.ds.Table;

import java.util.Enumeration;

/**
 * Node Creation utilities (static methods) for a Document Processor. 
 *
 * @version $Id: Create.java,v 1.3 1999-03-12 19:28:10 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 */

public class Create {

  /************************************************************************
  ** NodeList Construction:
  ************************************************************************/

  /** Create a singleton NodeList containing a given node. */
  public static NodeList createNodeList(Node aNode) {
    return new ParseNodeArray(aNode);
  }

  /** Create a singleton NodeList containing a given String. */
  public static NodeList createNodeList(String aString) {
    return new ParseNodeArray(new ParseTreeText(aString));
  }

  /** Create a NodeList by splitting a string on whitespace. */
  public static NodeList split(String aString) {
    return createNodeList(new java.util.StringTokenizer(aString), " ");
  }

  /** Create a NodeList from an enumeration of elements, with an optional 
   *	separator.  The separator is made ignorable if it is whitespace.
   */
  public static NodeList createNodeList(Enumeration enum, String sep) {
    boolean iws = (sep != null) && Test.isWhitespace(sep);
    ParseNodeArray nl = new ParseNodeArray();
    while (enum.hasMoreElements()) {
      Object o = enum.nextElement();
      if (o instanceof Node) { nl.append((Node)o); }
      else { nl.append(new ParseTreeText(o.toString())); }
      if (sep != null && enum.hasMoreElements()) {
	nl.append(new ParseTreeText(sep, iws));
      }
    }
    return nl;
  }

  /************************************************************************
  ** Node Construction:
  ************************************************************************/

  /** Create an arbitrary ActiveNode with optional name and data. */
  public static ActiveNode createActiveNode(int nodeType,
					    String name, String data) {
    switch (nodeType) {
    case NodeType.COMMENT:
      return new ParseTreeComment(data);
    case NodeType.PI:
      return new ParseTreePI(name, data);
    case NodeType.ATTRIBUTE:
      return new ParseTreeAttribute(name, (NodeList)null);
    case NodeType.ENTITY:
      return new ParseTreeEntity(name, (NodeList)null);
    case NodeType.ELEMENT:
      return new ParseTreeElement(name, null);
    case NodeType.DECLARATION:
      return new ParseTreeDecl(name, null, data);
    default:
      return new ParseTreeComment("Undefined type " + nodeType
				  + " name=" + name + " data=" + data);
      //return null;
    }
  }

  /** Create an arbitrary ActiveNode with optional name and data. */
  public static ActiveNode createActiveNode(int nodeType,
					    String name, NodeList value) {
    switch (nodeType) {
    case NodeType.COMMENT:
      return new ParseTreeComment("");
    case NodeType.PI:
      return new ParseTreePI(name, "");
    case NodeType.ATTRIBUTE:
      return new ParseTreeAttribute(name, value);
    case NodeType.ENTITY:
      return new ParseTreeEntity(name, value);
    case NodeType.ELEMENT:
      return new ParseTreeElement(name, null);
    case NodeType.DECLARATION:
      return new ParseTreeDecl(name, null, null);
    default:
      return new ParseTreeComment("Undefined type " + nodeType
				  + " name=" + name + " value=" + value);
      //return null;
    }
  }

}
