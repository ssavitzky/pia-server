////// prettyHandler.java: <pretty> Handler implementation
//	$Id: prettyHandler.java,v 1.5 2001-01-11 23:37:22 steve Exp $

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

import org.w3c.dom.*;

import org.risource.ds.List;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.*;
import org.risource.dps.tree.TreeElement;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeFragment;
import org.risource.dps.tree.TreeCharData;

import java.util.Enumeration;
import java.lang.String;
import java.lang.StringBuffer;


/**
 * Handler for &lt;pretty&gt; &lt;/&gt;  This tag prints
 * the node, document fragment, or document, that is between
 * pretty begin and end tags as a tree.
 * <br>	
 *
 * @version $Id: prettyHandler.java,v 1.5 2001-01-11 23:37:22 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class prettyHandler extends GenericHandler {

    List printList = null;

	
    int indentIncrement = 4;
  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;pretty&gt; node.  Recursively
    * prints the content NodeList as a tree.
   */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {

    String whitenTag = "defaultMeansNoWhiten"; 
    String yellowTag = "defaultMeansNoYellow"; 
    String hideBelowTag = "defaultMeansNoHideBelow";
    int hideBelowDepth = -1;
    int hideAboveDepth = -1;
    boolean colorize = true;

    // Actually do the work. 
    OutputTrace trOut = new OutputTrace(out);

    // look at attributes for prettyting
    if (atts.hasTrueAttribute("white-tag") ){
	whitenTag = atts.getAttribute("white-tag");
    }
    if (atts.hasTrueAttribute("yellow-tag") ){
	yellowTag = atts.getAttribute("yellow-tag");
    }
    if (atts.hasTrueAttribute("hide-below-tag") ){
	hideBelowTag = atts.getAttribute("hide-below-tag");
    }
    if (atts.hasTrueAttribute("hide-below-depth") ){
	hideBelowDepth = MathUtil.getInt(atts, "hide-below-depth", -1);
    }
    if (atts.hasTrueAttribute("hide-above-depth") ){
	hideAboveDepth = MathUtil.getInt(atts, "hide-above-depth", -1);
    }
    if (atts.hasTrueAttribute("nomarkup") ){
	colorize = false;
    }
    	       
    // Create a "pre" element as a parent node to
    // preserve indented prettyting, but not if the output 
    // is piped colorless directly to an editor or document
    ActiveNode preNode;
    if (colorize)
	preNode = new TreeElement("pre");
    else
	preNode = new TreeFragment();
	//        preNode = new TreeElement("");
    for (int i = 0; i < content.getLength(); ++i) {
      // Print the node tree
      printTree(content.activeItem(i), 0, preNode, hideBelowDepth,
		hideAboveDepth,	hideBelowTag, whitenTag, yellowTag, colorize);
    }
    out.putNode(preNode);
    // Output the pre node
  }

  /** Extracts the node type and string and indents appropriately.
   *  Next, creates a text node to represent each string in the output
   *  tree and adds the text node as a child of the parent <pre> node,
   *  which preserves indented prettyting.  This function is called
   *  recursively.
   *  @param node:  the node to be expanded.
   *  @param depth: the depth, to give the number of spaces by which each node
   *  is indented
   *  @param parent:  the <pre> element parent node that preserves tree
   *         prettyting.  */
  protected void printTree(ActiveNode node, int depth, ActiveNode parent,
			   int hideBelowDepth, int hideAboveDepth,
			   String hideBelowTag, 
			   String whitenTag, String yellowTag, 
			   boolean colorize) {
    // System.out.println("printTree Node: " + node.toString());
    if(node == null)
      return;

    String displayColor = colorFromIndent(depth);
    boolean hideBelowNow = false;
    boolean hideAboveNow = false;

    String nType = NodeType.getName(node.getNodeType());
    String nContent = null;
    String endContent = null;
    ActiveNodeList nl = node.getContent();

    switch(node.getNodeType()) {
    case Node.COMMENT_NODE:
      nContent = node.getNodeValue();
      break;
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
	if(Test.isWhitespace(node.getNodeValue())){
	    //	    nContent = "<ws>";
	    return; // don't show whitespace
	}
      else
	  // trim both sides so whitespace doesn't accumulate when re-inserted
	  // into original document
	nContent = (node.getNodeValue()).trim() ;
      break;

      // clipped below
      //case NodeType.DECLARATION:
      //nContent = ((Declaration)node).getName();
      //break;
      // clipped above

    case Node.PROCESSING_INSTRUCTION_NODE:
      nContent = node.getNodeName();
      break;
    case Node.ENTITY_NODE:
      nContent = node.getNodeName();
      break;
    case Node.ENTITY_REFERENCE_NODE:
	//      nContent = "&" + node.getNodeName() + ";" ;
      nContent = node.toString();
      break;
    case Node.ELEMENT_NODE:
	// user-selected tag-name to color white or yellow
	if ( whitenTag.equals(node.getNodeName()) )
	    displayColor = "white";
	if ( yellowTag.equals(node.getNodeName()) )
	    displayColor = "yellow";

	// decide whether to hideBelow content
	if ( hideBelowTag.equals(node.getNodeName()) ||
	       hideBelowDepth == depth )
	    hideBelowNow = true;

	// decide whether to hide above this point (but don't hide
	// protect tags!)
	if ( depth < hideAboveDepth && !node.getNodeName().equals("protect") ){
	    hideAboveNow = true;
	}

	// decide whether tag is empty
	if(nl == null){
	    nContent = node.startString();	
	}
	else{
	    endContent = "</" + node.getNodeName() + ">";
	    nContent = node.startString();
	}

	break;
    case Node.ATTRIBUTE_NODE:
      nContent = node.getNodeName();
      break;
      // case NodeType.ENDTAG:
      // nContent = ((Attribute)node).getName();
      // break;
    default:
	// DOCUMENT falls here
	// System.out.println("default: " + node.toString());
	nContent = node.toString();
      break;
    }

    int newDepth = depth + 1;
    // only print out if not hidden
    if (!hideAboveNow){
	//    String printStr = nType + " " + nContent;
	String printStr = nContent;
	String indStr = indentString(printStr, depth*indentIncrement);
	TreeElement fontNode = new TreeElement("font");
	fontNode.setAttribute("color", displayColor );
	ActiveNode newNode = new TreeText(indStr);
	fontNode.addChild(newNode);
	// Add each node as a child of the parent <pre> element
	// colorize if desired; otherwise use raw text string
	if (colorize)
	    parent.addChild(fontNode);	
	else{
	    ActiveNode rawNode = new TreeCharData(NodeType.STRING,indStr);
	    parent.addChild(rawNode);	
	}
    }    

    //    ActiveNodeList nl = node.getContent();
    if(nl == null)
	return;

    // don't print content if user says to hideBelow it instead
    if (!hideBelowNow){
	for (int i = 0; i < nl.getLength(); i++)
	    printTree(nl.activeItem(i), newDepth, parent, hideBelowDepth,
		      hideAboveDepth, hideBelowTag, whitenTag, yellowTag,
		      colorize);
    }

    if (!hideAboveNow){
	// adding end-tags
	String printStr2 = endContent;
	String indStr2 = indentString(printStr2, depth*indentIncrement);
	ActiveNode newNode2 = new TreeText(indStr2);
	TreeElement fontNode2 = new TreeElement("font");
	fontNode2.setAttribute("color", displayColor );
	fontNode2.addChild(newNode2);

	// colorize if desired; otherwise use raw text string
	if (colorize)
	    parent.addChild(fontNode2);	
	else{
	    ActiveNode rawNode2 = new TreeCharData(NodeType.STRING, indStr2);
	    parent.addChild(rawNode2);	
	}
	// end end-tags
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


  /** Return a color-name string, given a depth */
  protected String colorFromIndent(int whichColor) {

    if (whichColor > 5)
	whichColor = whichColor%6;

    switch(whichColor){
    case 0:
	return ("black");
    case 1:
	return ("peru");
    case 2:
	return ("purple");
    case 3:
	return ("green");
    case 4:
	return ("blue");
    case 5:
	return ("crimson");
    default:
	return ("white");
    }
  }


    

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public prettyHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
    printList = new List();
  }

  prettyHandler(ActiveElement e) {
     this();
     // customize for element.
     expandContent = true;
     printList = new List();
  }
}

