////// ToDocument.java:  Output to Document
//	$Id: ToDocument.java,v 1.2 2000-06-09 04:06:57 steve Exp $

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


package org.risource.dom;

import org.risource.dps.Output;

import org.w3c.dom.*;

/**
 * Output to a DOM Document.
 *
 * <p>	ToDocument can make no assumptions about the implementation of
 *	the Document being constructed -- in general it is <em>not</em>
 *	compatible with the PIA's parse tree implementation.  Hence, we
 *	must use only the DOM interfaces in the construction. 
 *
 * @version $Id: ToDocument.java,v 1.2 2000-06-09 04:06:57 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Input
 * @see org.w3c.dom
 */

public class ToDocument implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Node current = null;
  protected Document root = null;

  /************************************************************************
  ** Methods:
  ************************************************************************/

  protected Element newElement(String tagname, NamedNodeMap attrs) {
    Element e = root.createElement(tagname);
    if (attrs != null) {
      for (int i = 0; i < attrs.getLength(); ++i) {
	Attr a = (Attr) attrs.item(i);
	Attr n = root.createAttribute(a.getNodeName());
	if (a.hasChildNodes()) {
	  ToDocument subtree = new ToDocument(root, n);
	  for (Node child = a.getFirstChild();
	       child != null;
	       child = child.getNextSibling()) {
	    subtree.putNode(child);
	  }
	}    
	e.setAttributeNode(n);
      }
    }
    return e;
  }

  protected Node appendCopy(Node aNode) {
    Node n = null;
    String nodeName = aNode.getNodeName();
    short nodeType = aNode.getNodeType();
    switch (nodeType) {
    case Node.ATTRIBUTE_NODE:
      n = root.createAttribute(nodeName);
      break;
    case Node.CDATA_SECTION_NODE:
      n = root.createCDATASection(aNode.getNodeValue());
      break;
    case Node.COMMENT_NODE:
      n = root.createComment(aNode.getNodeValue());
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      n = root.createDocumentFragment();
      break;
    case Node.ELEMENT_NODE:
      n = newElement(nodeName, aNode.getAttributes());
      break;
    case Node.ENTITY_REFERENCE_NODE:
      n = root.createEntityReference(nodeName);
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      n = root.createProcessingInstruction(nodeName, aNode.getNodeValue());
      break;
    case Node.TEXT_NODE:
      n = root.createTextNode(aNode.getNodeValue());
      break;

    case Node.ENTITY_NODE:
    case Node.NOTATION_NODE:
    case Node.DOCUMENT_NODE:
    case Node.DOCUMENT_TYPE_NODE:
    default:
      n = root.createComment("ERROR: cannot create node of type " 
			     + nodeType + " and name " + nodeName);
    }
    current.insertBefore(n, null);

    if (aNode.hasChildNodes()) {
      ToDocument subtree = new ToDocument(root, n);
      for (Node child = aNode.getFirstChild();
	   child != null;
	   child = child.getNextSibling()) {
	subtree.putNode(child);
      }
    }    
    return n;
  }

  public void putNode(Node aNode) {
    appendCopy(aNode);
  }
  public void startNode(Node aNode) { 
    if (aNode.getNodeType() == Node.ELEMENT_NODE) {
      startElement(aNode.getNodeName(), aNode.getAttributes());
    } else {
      current = appendCopy(aNode);
    }
  }
  public boolean endNode() {
    current = current.getParentNode();
    return current != null;
  }

  public void startElement(String tagname, NamedNodeMap attrs) {
    Element e = newElement(tagname, attrs);
    current.insertBefore(e, null);
    current = e;
  }
  public boolean endElement(boolean optional) {
    return endNode();
  }


  public void putNewNode(short nodeType, String nodeName, String value) {
    Node n = null;
    switch (nodeType) {
    case Node.ATTRIBUTE_NODE:
      Attr a = root.createAttribute(nodeName);
      a.setNodeValue(value);
      n = a;
      break;
    case Node.CDATA_SECTION_NODE:
      n = root.createCDATASection(value);
      break;
    case Node.COMMENT_NODE:
      n = root.createComment(value);
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      n = root.createDocumentFragment();
      break;
    case Node.ELEMENT_NODE:
      n = root.createElement(nodeName);
      break;
    case Node.ENTITY_REFERENCE_NODE:
      n = root.createEntityReference(nodeName);
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      n = root.createProcessingInstruction(nodeName, value);
      break;
    case Node.TEXT_NODE:
      n = root.createTextNode(value);
      break;

    case Node.ENTITY_NODE:
    case Node.NOTATION_NODE:
    case Node.DOCUMENT_NODE:
    case Node.DOCUMENT_TYPE_NODE:
    default:
      n = root.createComment("ERROR: cannot create node of type " 
			     + nodeType + " and name " + nodeName);
    }
    current.insertBefore(n, null);
  }
  public void startNewNode(short nodeType, String nodeName) {
    Node n = null;
    switch (nodeType) {
    case Node.ATTRIBUTE_NODE:
      n = root.createAttribute(nodeName);
      break;
    case Node.CDATA_SECTION_NODE:
      n = root.createCDATASection(null);
      break;
    case Node.COMMENT_NODE:
      n = root.createComment(null);
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      n = root.createDocumentFragment();
      break;
    case Node.ELEMENT_NODE:
      n = root.createElement(nodeName);
      break;
    case Node.ENTITY_REFERENCE_NODE:
      n = root.createEntityReference(nodeName);
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      n = root.createProcessingInstruction(nodeName, null);
      break;
    case Node.TEXT_NODE:
      n = root.createTextNode(null);
      break;

    case Node.ENTITY_NODE:
    case Node.NOTATION_NODE:
    case Node.DOCUMENT_NODE:
    case Node.DOCUMENT_TYPE_NODE:
    default:
      n = root.createComment("ERROR: cannot create node of type " 
			     + nodeType + " and name " + nodeName);
    }
    current.insertBefore(n, null);
    current = n;
  }

  public void putCharData(short nodeType, String nodeName,
			  char[] buffer, int start, int length) {
    putNewNode(nodeType, nodeName, new String(buffer, start, length));
  }
  public void close() {}

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Output to a given DOM Document. */
  public ToDocument(Document doc) {
    root = doc;
    current = doc;
  }

  /** Output to a subtree of a given DOM Document. */
  public ToDocument(Document doc, Node subtree) {
    root = doc;
    current = subtree;
  }

}
