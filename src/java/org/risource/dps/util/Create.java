////// Create.java: Utilities for Creating nodes.
//	Create.java,v 1.7 1999/03/01 23:46:54 pgage Exp

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
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Entity;

import crc.dps.NodeType;
import crc.dps.active.*;
import crc.dps.output.*;

import crc.ds.Table;

import java.util.Enumeration;

/**
 * Node Creation utilities (static methods) for a Document Processor. 
 *
 * @version Create.java,v 1.7 1999/03/01 23:46:54 pgage Exp
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
