////// Expand.java: Utilities for Expanding nodes.
//	Expand.java,v 1.13 1999/03/01 23:46:57 pgage Exp

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
import org.risource.dom.NodeEnumerator;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;
import org.risource.dom.Entity;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.input.FromParseNodes;
import org.risource.dps.input.FromParseTree;

/**
 * Node-expansion utilities (static methods) for the Document Processor. 
 *
 * <p>	These utilities are primarily used in handlers for obtaining processed
 *	content, expanding entities, and so on.  They essentially duplicate
 *	the functionality available in a Processor without requiring an actual
 *	Processor to be instantiated.
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.process.BasicProcessor
 *
 * @version Expand.java,v 1.13 1999/03/01 23:46:57 pgage Exp
 * @author steve@rsv.ricoh.com 
 */

public class Expand {

  /************************************************************************
  ** Input Processing:
  ************************************************************************/

  /** Get the processed content of the current node. */
  public static ParseNodeList getProcessedContent(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    c.subProcess(in, out).processChildren();
    return out.getList();
  }

  /** Get the processed content of the current node as a string. */
  public static String getProcessedContentString(Input in, Context c) {
    ToString out = new ToString();
    c.subProcess(in, out).processChildren();
    return out.getString();
  }

  /** Get the unprocessed content of the current node. */
  public static ParseNodeList getContent(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    Copy.copyChildren(in, out);
    return out.getList();
  }

  /** Get the unprocessed content of the current node as a string. */
  public static String getContentString(Input in, Context c) {
    ToString out = new ToString();
    Copy.copyChildren(in, out);
    return out.getString();
  }

  /** Extract text from the processed content of the current node. */
  public static ParseNodeList getProcessedText(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    c.subProcess(in, new FilterText(out)).processChildren();
    return out.getList();
  }

  /** Extract text from the processed content of the current node
   *	and return the result as a string. */
  public static String getProcessedTextString(Input in, Context c) {
    ToString out = new ToString();
    c.subProcess(in, new FilterText(out)).processChildren();
    return out.getString();
  }

  /** Extract text from the unprocessed content of the current node. */
  public static ParseNodeList getText(Input in, Context c) {
    ToNodeList out = new ToNodeList();
    Copy.copyChildren(in, new FilterText(out));
    return out.getList();
  }

  /** Extract text from the unprocessed content of the current node 
   *	and return the result as a string. */
  public static String getTextString(Input in, Context c) {
    ToString out = new ToString();
    Copy.copyChildren(in, new FilterText(out));
    return out.getString();
  }

  /************************************************************************
  ** NodeList Processing:
  ************************************************************************/

  /** Process a node list and return the result. */
  public static ParseNodeList processNodes(NodeList nl, Context c) {
    Input in = new FromParseNodes(nl);
    ToNodeList out = new ToNodeList();
    if (nl != null) c.subProcess(in, out).run();
    return out.getList();
  }

  /** Process a node list and return the result. */
  public static void processNodes(NodeList nl, Context c, Output out) {
    if (nl == null || nl.getLength() == 0) return;
    Input in = new FromParseNodes(nl);
    c.subProcess(in, out).run();
  }

  /** Process the children of a Node and return the result. */
  public static ParseNodeList processChildren(ActiveNode aNode, Context c) {
    Input in = new FromParseTree(aNode);
    ToNodeList out = new ToNodeList();
    c.subProcess(in, out).processChildren();
    return out.getList();
  }

  /** Process the children of a Node and return the result. */
  public static void processChildren(ActiveNode aNode, Context c, Output out) {
    Input in = new org.risource.dps.input.FromParseTree(aNode);
    c.subProcess(in, out).processChildren();
  }


  /************************************************************************
  ** Expansion from an Input:
  ************************************************************************/

  /** Get the expanded attribute list of the current node. 
   *	The list is not expanded if it doesn't have to be. 
   */
  public static ActiveAttrList getExpandedAttrs(Input in, Context c) {
    if (in.hasActiveAttributes()) {
      return expandAttrs(c, in.getElement().getAttributes());
    } else if (in.hasAttributes()) {
      return Copy.copyAttrs(in.getElement().getAttributes());
    } else {
      return null;
    }
  }

  /************************************************************************
  ** Expansion:
  ************************************************************************/

  /** Expand entities in an AttributeList, getting values from a Context. */
  public static ActiveAttrList expandAttrs(Context c, AttributeList atts) {
    ToAttributeList dst = new ToAttributeList();
    expandAttrs(c, atts, dst);
    return dst.getList();
  }

  /** Copy an attribute list without expansion. */
  public static ActiveAttrList copyAttrs(AttributeList atts) {
    ToAttributeList dst = new ToAttributeList();
    Copy.copyNodes(atts, dst);
    return dst.getList();
  }

  /** Expand entities in an AttributeList with results going to a given
   *	Output. */ 
  public static void expandAttrs(Context c, AttributeList atts, Output dst) {
    for (int i = 0; i < atts.getLength(); i++) { 
      try {
	expandAttribute(c, (Attribute) atts.item(i), dst);
      } catch (org.risource.dom.NoSuchNodeException ex) {}
    }
  }

  /** Expand entities in an Attribute and put the result to an Output. */
  public static void expandAttribute(Context c, Attribute att,  Output dst) {
    dst.putNode(new ParseTreeAttribute(att.getName(),
				       expandNodes(c, att.getValue())));
  }

  /** Expand entities in a NodeList. */
  public static NodeList expandNodes(Context c, NodeList nl) {
    if (nl == null) return null;
    ToNodeList dst = new ToNodeList();
    expandNodes(c, nl, dst);
    return dst.getList();
  }

  /** Expand entities in a NodeList and put the result to an Output. */
  public static void expandNodes(Context c, NodeList nl, Output dst) {
    NodeEnumerator e = nl.getEnumerator();
    for (Node n = e.getFirst(); n != null; n = e.getNext()) {
      if (n.getNodeType() == NodeType.ENTITY) {
	expandEntity(c, (Entity) n, dst);
      } else {
	dst.putNode(n);
      }
    }
  }

  /** Expand a single entity, putting the expansion to an Output. */
  public static void expandEntity(Context c, Entity n, Output dst) {
    String name = n.getName();
    NodeList value = Index.getIndexValue(c, name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }


}
