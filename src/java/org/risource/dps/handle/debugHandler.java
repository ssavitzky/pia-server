////// debugHandler.java: <debug> Handler implementation
//	$Id: debugHandler.java,v 1.4 1999-03-24 01:58:58 pgage Exp $

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


package org.risource.dps.handle;

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;
import org.risource.dom.Comment;
import org.risource.dom.Text;
import org.risource.dom.Attribute;
import org.risource.dom.Declaration;
import org.risource.dom.PI;
import org.risource.dom.Entity;
import org.risource.dom.Element;

import org.risource.ds.List;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.*;
import org.risource.dps.NodeType;

import java.util.Enumeration;
import java.lang.String;
import java.lang.StringBuffer;


/**
 * Handler for &lt;debug&gt; &lt;/&gt;  This tag prints
 * the node, document fragment, or document, that is between
 * debug begin and end tags as a tree.
 * <br>	
 *
 * @version $Id: debugHandler.java,v 1.4 1999-03-24 01:58:58 pgage Exp $
 * @author steve@rsv.ricoh.com
 */

public class debugHandler extends GenericHandler {

    List printList = null;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;debug&gt; node.  Recursively
    * prints the content NodeList as a tree.
   */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {

    // Actually do the work. 
    OutputTrace trOut = new OutputTrace(out);
    
    NodeEnumerator enum = content.getEnumerator();
    // Create a "pre" element as a parent node to
    // preserve indented formatting
    ActiveNode preNode = new ParseTreeElement("pre");
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {

      // Print the node tree
      printTree(n, 0, preNode);
    }
    // Output the pre node
    out.putNode(preNode);

  }

  /** Extracts the node type and string and indents appropriately.
   *  Next, creates a text node to represent each string in the output
   *  tree and adds the text node as a child of the parent <pre> node,
   *  which preserves indented formatting.  This function is called
   *  recursively.
   *  @param node:  the node to be expanded.
   *  @param indentNum:  the number of spaces by which each node is indented
   *  @param parent:  the <pre> element parent node that preserves tree
   *         formatting.
    */
  protected void printTree(Node node, int indentNum, ActiveNode parent) {
    // System.out.println("printTree Node: " + node.toString());
    if(node == null)
      return;

    String nType = NodeType.getName(node.getNodeType());
    String nContent = null;

    switch(node.getNodeType()) {
    case NodeType.COMMENT:
      nContent = ((Comment)node).getData();
      break;
    case NodeType.TEXT:
      Text tn = ((Text)node);
      if(Test.isWhitespace(tn.getData()))
	nContent = "<whitespace>";
      else
	nContent = tn.getData();
      break;
    case NodeType.DECLARATION:
      nContent = ((Declaration)node).getName();
      break;
    case NodeType.PI:
      nContent = ((PI)node).getName();
      break;
    case NodeType.ENTITY:
      nContent = ((Entity)node).getName();
      break;
    case NodeType.ELEMENT:
      nContent = "<" + ((Element)node).getTagName() + ">";
      break;
    case NodeType.ATTRIBUTE:
      nContent = ((Attribute)node).getName();
      break;
    case NodeType.ENDTAG:
      nContent = ((Attribute)node).getName();
      break;
    default:
	// DOCUMENT falls here
	// System.out.println("default: " + node.toString());
	nContent = node.toString();
      break;
    }

    String printStr = nType + " " + nContent;

    String indStr = indentString(printStr, indentNum);
    // System.out.println(indStr);
    ActiveNode newNode = new ParseTreeText(indStr);
    
    // Add each node as a child of the parent <pre> element
    parent.addChild(newNode);
    int newIndent = indentNum + 3;
    NodeList nl = node.getChildren();
    if(nl == null)
      return;
    NodeEnumerator childEnum = nl.getEnumerator();
    for (Node n = childEnum.getFirst(); n != null; n = childEnum.getNext()) {
      printTree(n, newIndent, parent);
    }
  }

  /** Return an indented string */
  protected String indentString(String indentStr, int num) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < num; i++) {
      sb.append(" ");
    }
    sb.append(indentStr+"\n");
    return(sb.toString());
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public debugHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
    printList = new List();
  }

  debugHandler(ActiveElement e) {
     this();
     // customize for element.
     expandContent = true;
     printList = new List();
  }
}

