////// Log.java: log utilities
//	Log.java,v 1.3 1999/03/01 23:47:00 pgage Exp

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

import java.io.PrintStream;

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.Element;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;
import org.risource.dom.Text;

import org.risource.dps.*;
import org.risource.dps.active.*;

/**
 * Logging utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for maintaining and 
 *	formatting a message log.
 *
 * @version Log.java,v 1.3 1999/03/01 23:47:00 pgage Exp
 * @author steve@rsv.ricoh.com
 * 
 * @see org.risource.dps.Context
 */
public class Log {
  public static String node(Node aNode) {
    switch (aNode.getNodeType()) {
    case org.risource.dom.NodeType.ELEMENT:
      Element e = (Element)aNode;
      AttributeList atts = e.getAttributes();
      return "<" + e.getTagName()
	+ ((atts != null && atts.getLength() > 0)? " " + atts.toString() : "")
	+ ">" + (e.hasChildren()? "..." : "");

    case org.risource.dom.NodeType.TEXT: 
      Text t = (Text)aNode;
      return t.getIsIgnorableWhitespace()
	? "ignorable" : Test.isWhitespace(t.getData()) ? "whitespace"
	: ("text: '" + string(t.getData()) + "'");

    default: 
      return aNode.toString();      
    }
  }

  public static String string(String s, int length) {
    if (s == null) return "null";
    String o = "";
    int i = 0;
    for ( ; i < s.length() && i < length; ++i) {
      char c = s.charAt(i);
      switch (c) {
      case '\n': o += "\\n"; break;
      default: o += c;
      }
    }
    if (i < s.length()) o += "..."; 
    return o;
  }

  public static String string(String s) {
    return string(s, 15);
  }
}
