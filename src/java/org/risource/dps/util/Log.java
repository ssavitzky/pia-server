////// Log.java: log utilities
//	$Id: Log.java,v 1.6 2001-01-11 23:37:43 steve Exp $

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

import org.w3c.dom.*;

import org.risource.dps.*;
import org.risource.dps.active.*;

/**
 * Logging utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for maintaining and 
 *	formatting a message log.
 *
 * @version $Id: Log.java,v 1.6 2001-01-11 23:37:43 steve Exp $
 * @author steve@rii.ricoh.com
 * 
 * @see org.risource.dps.Context
 */
public class Log {
  public static String node(Node aNode) {
    switch (aNode.getNodeType()) {
    case Node.ELEMENT_NODE:
      Element e = (Element)aNode;
      NamedNodeMap atts = e.getAttributes();
      return "<" + e.getNodeName()
	+ ((atts != null && atts.getLength() > 0)? " " + atts.toString() : "")
	+ ">" + (e.hasChildNodes()? "..." : "");

    case Node.TEXT_NODE: 
      ActiveText t = (ActiveText)aNode;

      if (true)
	  throw new java.lang.RuntimeException();

      return t.getIsIgnorable()
	? "ignorable" : Test.isWhitespace(t.getData()) ? "whitespace"
	: ("Text: '" + string(t.getData()) + "'");

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
