////// FilterText: text-only filter for an Output
//	$Id: FilterText.java,v 1.7 2001-04-03 00:04:40 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package org.risource.dps.output;

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.active.NodeType;

import java.io.PrintStream;

/**
 * An Output filter that passes only Text (and Entity) nodes <p>
 *
 * @version $Id: FilterText.java,v 1.7 2001-04-03 00:04:40 steve Exp $
 * @author steve@rii.ricoh.com 
 */

public class FilterText extends Proxy {

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (target != null && (aNode.getNodeType() == Node.TEXT_NODE
			   || aNode.getNodeType() == Node.ENTITY_REFERENCE_NODE
			   || aNode.getNodeType() == Node.ENTITY_NODE))
      target.putNode(aNode);
    else if (aNode.hasChildNodes()) {
      startNode(aNode);
      for (Node n = aNode.getFirstChild(); n != null; n = n.getNextSibling())
	putNode(n);
      endNode();
    }
  }
  public void startNode(Node aNode) { 
    depth++;
  }
  public boolean endNode() { 
    depth --;
    return depth >= 0;
  }
  public void startElement(String tagname, NamedNodeMap attrs) {
    depth++;
  }
  public boolean endElement(boolean optional) {
    depth --;
    return depth >= 0;
  }

  public void putNewNode(short nodeType, String nodeName, String value) {
    if (target != null && NodeType.isTextType(nodeType))
      target.putNewNode(nodeType, nodeName, value);
  }
  public void startNewNode(short nodeType, String nodeName) {
    depth++;
  }
  public void putCharData(short nodeType, String nodeName,
			  char[] buffer, int start, int length) {
    if (target != null && NodeType.isTextType(nodeType))
      target.putCharData(nodeType, nodeName, buffer, start, length);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FilterText() {
  }

  public FilterText(Output theTarget) {
    super(theTarget);
  }

}
