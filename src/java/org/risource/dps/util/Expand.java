////// Expand.java: Utilities for Expanding nodes.
//	$Id: Expand.java,v 1.5 1999-04-07 23:22:16 steve Exp $

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
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.DOMException;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.input.FromParseTree;
import org.risource.dps.tree.TreeAttr;

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
 * @version $Id: Expand.java,v 1.5 1999-04-07 23:22:16 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public class Expand {

  /************************************************************************
  ** Input Processing:
  ************************************************************************/

  /** Get the processed content of the current node. */
  public static ActiveNodeList getProcessedContent(Input in, Context c) {
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
  public static ActiveNodeList getContent(Input in, Context c) {
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
  public static ActiveNodeList getProcessedText(Input in, Context c) {
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
  public static ActiveNodeList getText(Input in, Context c) {
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
  public static ActiveNodeList processNodes(NodeList nl, Context c) {
    Input in = new FromNodeList(nl);
    ToNodeList out = new ToNodeList();
    if (nl != null) c.subProcess(in, out).run();
    return out.getList();
  }

  /** Process a node list and return the result. */
  public static void processNodes(NodeList nl, Context c, Output out) {
    if (nl == null || nl.getLength() == 0) return;
    Input in = new FromNodeList(nl);
    c.subProcess(in, out).run();
  }

  /** Process the children of a Node and return the result. */
  public static ActiveNodeList processChildren(ActiveNode aNode, Context c) {
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
      return expandAttrs(c, in.getAttributes());
    } else if (in.hasAttributes()) {
      return Copy.copyAttrs(in.getAttributes());
    } else {
      return null;
    }
  }

  /************************************************************************
  ** Expansion:
  ************************************************************************/

  /** Expand entities in an ActiveAttrList, getting values from a Context. */
  public static ActiveAttrList expandAttrs(Context c, ActiveAttrList atts) {
    ToAttributeList dst = new ToAttributeList();
    expandAttrs(c, atts, dst);
    return dst.getList();
  }

  /** Copy an attribute list without expansion. */
  public static ActiveAttrList copyAttrs(ActiveAttrList atts) {
    ToAttributeList dst = new ToAttributeList();
    Copy.copyNodes(atts, dst);
    return dst.getList();
  }

  /** Expand entities in an ActiveAttrList with results going to a given
   *	Output. */ 
  public static void expandAttrs(Context c, ActiveAttrList atts, Output dst) {
    for (int i = 0; i < atts.getLength(); i++) { 
      try {
	expandAttribute(c, (ActiveAttr) atts.item(i), dst);
      } catch (DOMException ex) {}
    }
  }

  /** Expand entities in an Attribute and put the result to an Output. */
  public static void expandAttribute(Context c, ActiveAttr att,  Output dst) {
    dst.putNode(new TreeAttr(att.getName(),
			     expandNodes(c, att.getValueNodes(c))));
  }

  /** Expand entities in a NodeList. */
  public static ActiveNodeList expandNodes(Context c, ActiveNodeList nl) {
    if (nl == null) return null;
    ToNodeList dst = new ToNodeList();
    expandNodes(c, nl, dst);
    return dst.getList();
  }

  /** Expand entities in a NodeList and put the result to an Output. */
  public static void expandNodes(Context c, ActiveNodeList nl, Output dst) {
    for (int i = 0; i < nl.getLength(); i++) { 
      Node n = nl.item(i);
      if (n == null) return;
      if (n.getNodeType() == Node.ENTITY_NODE) {
	expandEntity(c, (Entity) n, dst);
      } else if (n.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
	expandEntityRef(c, (EntityReference) n, dst);
      } else {
	dst.putNode(n);
      }
    }
  }

  /** Expand a single entity, putting the expansion to an Output. */
  public static void expandEntity(Context c, Entity n, Output dst) {
    String name = n.getNodeName();
    // === expandEntity should just get the node's value ===
    NodeList value = Index.getIndexValue(c, name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }

  /** Expand an entity reference, putting the expansion to an Output. */
  public static void expandEntityRef(Context c, EntityReference n, Output dst) {
    String name = n.getNodeName();
    NodeList value = Index.getIndexValue(c, name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }


}
