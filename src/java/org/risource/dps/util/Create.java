////// Create.java: Utilities for Creating nodes.
//	$Id: Create.java,v 1.5 1999-07-15 17:17:29 steve Exp $

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
import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.tree.*;

import org.risource.ds.Table;

import java.util.Enumeration;

/**
 * Node Creation utilities (static methods) for a Document Processor. 
 *
 * @version $Id: Create.java,v 1.5 1999-07-15 17:17:29 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 */

public class Create {

  /************************************************************************
  ** NodeList Construction:
  ************************************************************************/

  /** Create a singleton NodeList containing a given node. */
  public static ActiveNodeList createNodeList(Node aNode) {
    return new TreeNodeArray(aNode);
  }

  /** Create a singleton NodeList containing a given String. */
  public static ActiveNodeList createNodeList(String aString) {
    return new TreeNodeArray(new TreeText(aString));
  }

  /** Create a NodeList by splitting a string on whitespace. */
  public static ActiveNodeList split(String aString) {
    return createNodeList(new java.util.StringTokenizer(aString), " ");
  }

  /** Create a NodeList from an enumeration of elements, with an optional 
   *	separator.  The separator is made ignorable if it is whitespace.
   */
  public static ActiveNodeList createNodeList(Enumeration enum, String sep) {
    boolean iws = (sep != null) && Test.isWhitespace(sep);
    TreeNodeArray nl = new TreeNodeArray();
    while (enum.hasMoreElements()) {
      Object o = enum.nextElement();
      if (o instanceof Node) { nl.append((Node)o); }
      else { nl.append(new TreeText(o.toString())); }
      if (sep != null && enum.hasMoreElements()) {
	nl.append(new TreeText(sep, iws));
      }
    }
    return nl;
  }

  /************************************************************************
  ** Node Construction:
  ************************************************************************/

  /** Create an arbitrary ActiveNode with optional name and data. */
  public static ActiveNode createActiveNode(short nodeType,
					    String name, String data) {
    switch (nodeType) {
    case Node.COMMENT_NODE:
      return new TreeComment(data);
    case Node.PROCESSING_INSTRUCTION_NODE:
      return new TreePI(name, data);
    case Node.ATTRIBUTE_NODE:
      return new TreeAttr(name, (ActiveNodeList)null);
    case Node.ENTITY_NODE:
      return new TreeEntity(name, (ActiveNodeList)null);
    case Node.ENTITY_REFERENCE_NODE:
      return new TreeEntityRef(name);
    case Node.ELEMENT_NODE:
      return new TreeElement(name);
    case Node.DOCUMENT_FRAGMENT_NODE:
      return new TreeFragment();
    case Node.DOCUMENT_TYPE_NODE:
      return new TreeDocType(name, data);
      //    case Node.DECLARATION:
      //return new TreeDecl(name, null, null);
    default:
      return new TreeComment("Undefined type " + nodeType
			     + " name=" + name + " data=" + data);
      //return null;
    }
  }

  /** Create an arbitrary ActiveNode with optional name and data. */
  public static ActiveNode createActiveNode(short nodeType, String name,
					    ActiveNodeList value) {
    switch (nodeType) {
    case Node.COMMENT_NODE:
      return new TreeComment("");
    case Node.PROCESSING_INSTRUCTION_NODE:
      return new TreePI(name, "");
    case Node.ATTRIBUTE_NODE:
      return new TreeAttr(name, value);
    case Node.ENTITY_NODE:
      return new TreeEntity(name, value);
    case Node.ENTITY_REFERENCE_NODE:
      return new TreeEntityRef(name);
    case Node.ELEMENT_NODE:
      return new TreeElement(name);
    case Node.DOCUMENT_FRAGMENT_NODE:
      return new TreeFragment();
    case Node.DOCUMENT_TYPE_NODE:
      return new TreeDocType(name, null); // really an error.
      //    case Node.DECLARATION:
      //return new TreeDecl(name, null, null);
    default:
      return new TreeComment("Undefined type " + nodeType
				  + " name=" + name + " value=" + value);
      //return null;
    }
  }

}
