////// NodeType.java: Document Processor basic implementation
//	$Id: NodeType.java,v 1.1 1999-04-07 23:21:01 steve Exp $

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
 * @version $Id: NodeType.java,v 1.1 1999-04-07 23:21:01 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.w3c.dom.Node
 */

public class NodeType {
  public static final short           ELEMENT_NODE         = 1;
  public static final short           ATTRIBUTE_NODE       = 2;
  public static final short           TEXT_NODE            = 3;
  public static final short           CDATA_SECTION_NODE   = 4;
  public static final short           ENTITY_REFERENCE_NODE = 5;
  public static final short           ENTITY_NODE          = 6;
  public static final short           PROCESSING_INSTRUCTION_NODE = 7;
  public static final short           COMMENT_NODE         = 8;
  public static final short           DOCUMENT_NODE        = 9;
  public static final short           DOCUMENT_TYPE_NODE   = 10;
  public static final short           DOCUMENT_FRAGMENT_NODE = 11;
  public static final short           NOTATION_NODE        = 12;

  public static final short	NONE		=  0;
  public static final short	ENDTAG 		= -1;
  public static final short	NODELIST 	= -2;
  public static final short	DECLARATION 	= -3;
  public static final short	ALL 		= -4;
  public static final short	UNDEFINED 	= -5;

  public static final short	MIN_TYPE 	= -4;
  public static final short	MAX_TYPE 	= NOTATION_NODE;

  /** This maps types into names.  They are only used for debugging,
   *	so they needn't match the names defined by either the DOM
   *	or XPTR standards, which are different anyway.
   */
  public static final String names[] = {
    "ALL",	"DECLARATION", 	"NODELIST",	"ENDTAG",  	"ERROR",
    /* 1... */ 	"ELEMENT",	"ATTRIBUTE",	"TEXT",	  	"CDATA", 
    "REFERENCE","ENTITY",	"PI",		"COMMENT",	"DOCUMENT",
    "DOCTYPE",	"FRAGMENT",	"NOTATION",
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
    case ATTRIBUTE_NODE: 
    case ENTITY_REFERENCE_NODE:
    case ENTITY_NODE:
      return true;
    default:
      return false;
    }
  }

  /** Test for the node containing text.  
   *	
   * <p> Note that although DOM comment nodes inherit from CharacterData, 
   *	 are not considered text in the DPS.
   */
  public static final boolean isText(Node n) {
    switch (n.getNodeType()) {
    case TEXT_NODE:
    case CDATA_SECTION_NODE:
      return true;
    default:
      return false;
    }
  }
}
