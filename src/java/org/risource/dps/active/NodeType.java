////// NodeType.java: Document Processor basic implementation
//	$Id: NodeType.java,v 1.5 1999-07-14 20:12:32 steve Exp $

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


package org.risource.dps.active;

import org.w3c.dom.Node;

/** Node types and their names.
 *	
 * <p> This class mainly exists to compensate for the lack of enumerated
 *	types in Java.  In addition to the types defined in the DOM, we
 *	define a few additional ones (with negative numbers), and the
 *	corresponding names.
 *
 * <p> This type also performs type-related tests on nodes, partially
 *	compensating for the lack of inheritance in the DOM types.
 *
 * @version $Id: NodeType.java,v 1.5 1999-07-14 20:12:32 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.w3c.dom.Node
 */

public class NodeType {
  public static final short	UNDEFINED 	= -5;
  public static final short	ALL 		= -4;
  public static final short	DECLARATION 	= -3;
  public static final short	NODELIST 	= -2;
  public static final short	ENDTAG 		= -1;

  public static final short	NONE		=  0;

  public static final short 	ELEMENT		= Node.ELEMENT_NODE;
  public static final short 	ATTRIBUTE	= Node.ATTRIBUTE_NODE;
  public static final short 	TEXT		= Node.TEXT_NODE;
  public static final short 	CDATA 		= Node.CDATA_SECTION_NODE;
  public static final short 	REFERENCE	= Node.ENTITY_REFERENCE_NODE;
  public static final short 	ENTITY		= Node.ENTITY_NODE;
  public static final short 	PI	    = Node.PROCESSING_INSTRUCTION_NODE;
  public static final short 	COMMENT		= Node.COMMENT_NODE;
  public static final short 	DOCUMENT	= Node.DOCUMENT_NODE;
  public static final short 	DOCTYPE		= Node.DOCUMENT_TYPE_NODE;
  public static final short 	FRAGMENT	= Node.DOCUMENT_FRAGMENT_NODE;
  public static final short 	NOTATION	= Node.NOTATION_NODE;

  public static final short	STRING		= (short)(((int)NOTATION)+1);

  public static final short	MIN_TYPE 	= ALL;
  public static final short	MAX_TYPE 	= STRING;

  /** This maps types into names.  They are only used for debugging,
   *	so they needn't match the names defined by either the DOM
   *	or XPTR standards, which are different anyway.
   */
  public static final String names[] = {
    "ALL",	"DECLARATION", 	"NODELIST",	"ENDTAG",  	"NONE",
    /* .. 1: */	"ELEMENT",	"ATTRIBUTE",	"TEXT",	  	"CDATA", 
    "REFERENCE","ENTITY",	"PI",		"COMMENT",	"DOCUMENT",
    "DOCTYPE",	"FRAGMENT",	"NOTATION",	/* added */	"STRING",
  };

  public static String getName(int type) {
    return names[type-MIN_TYPE];
  }
  public static int getType(String name) {
    for (int i = 0; i < names.length; ++i) 
      if (name.equalsIgnoreCase(names[i])) return (i + MIN_TYPE);
    return UNDEFINED;
  }

  public static final boolean hasValueNodes(Node n) {
    switch (n.getNodeType()) {
    case Node.ATTRIBUTE_NODE: 
    case Node.ENTITY_REFERENCE_NODE:
    case Node.ENTITY_NODE:
      return true;
    default:
      return false;
    }
  }

  public static final boolean hasContent(Node n) {
    switch (n.getNodeType()) {
    case Node.ATTRIBUTE_NODE: 
    case Node.ENTITY_NODE:
    case Node.ELEMENT_NODE:
      return true;
    default:
      return false;
    }
  }

  /** Test for the node containing text.  
   *	
   * <p> Note that although DOM comment nodes inherit from CharacterData, 
   *	 they are not considered text in the DPS.
   */
  public static final boolean isText(Node n) {
    return isTextType(n.getNodeType());
  }

  /** Test for the node containing text.  
   *	
   * <p> Note that although DOM comment nodes inherit from CharacterData, 
   *	 they are not considered text in the DPS.
   */
  public static final boolean isTextType(short n) {
    switch (n) {
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
    case STRING:
      return true;
    default:
      return false;
    }
  }
}
