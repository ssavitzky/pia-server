// TreeDocument.java
// $Id: TreeDocument.java,v 1.1 1999-04-07 23:22:06 steve Exp $

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


package org.risource.dps.tree;

import java.io.*;
import org.risource.dps.active.*;
import org.w3c.dom.*;

import org.risource.dps.Handler;
import org.risource.dps.Namespace;
import org.risource.dps.util.Copy;

/** 
 * Class for Document nodes. <p>
 *
 *	Document nodes can also be used as Element nodes.
 */
public class TreeDocument extends TreeNode implements ActiveDocument
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  DocumentType doctype;
  DOMImplementation implementation;
  ActiveElement	documentElement;

  /************************************************************************
  ** Accessors:
  ************************************************************************/

  public DocumentType getDoctype() 		{ return doctype; }
  public DOMImplementation getImplementation()	{ return implementation; }
  public Element getDocumentElement() 		{ return documentElement; }


  /** In some cases it may be necessary to make the node type more specific. */
  public void setNodeType(short value)		{ nodeType = value; }
  
  public void setTagName(String name) { 
    nodeName = name;
    setNodeType(name == null? Node.DOCUMENT_NODE : Node.ELEMENT_NODE);
  }

  /**
   * Returns a <code>NodeList</code> of all the <code>Element</code>s with a 
   * given tag name in the order in which they would be encountered in a 
   * preorder traversal of the <code>Document</code> tree. 
   * @param tagname The name of the tag to match on. The special value "*" 
   *   matches all tags.
   * @return A new <code>NodeList</code> object containing all the matched 
   *   <code>Element</code>s.
   */
  public NodeList           getElementsByTagName(String tagname) {
    return (documentElement == null)? null
      : documentElement.getElementsByTagName(tagname);
  }

  /************************************************************************
  ** Node Construction:
  ************************************************************************/

  /**
   * Creates an element of the type specified. Note that the instance returned 
   * implements the Element interface, so attributes can be specified 
   * directly  on the returned object.
   * @param tagName The name of the element type to instantiate. For XML, this 
   *   is case-sensitive. For HTML, the  <code>tagName</code> parameter may 
   *   be provided in any case,  but it must be mapped to the canonical 
   *   uppercase form by  the DOM implementation. 
   * @return A new <code>Element</code> object.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
   *   invalid character.
   */
  public Element            createElement(String tagName)
                                          throws DOMException {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates an empty <code>DocumentFragment</code> object. 
   * @return A new <code>DocumentFragment</code>.
   */
  public DocumentFragment   createDocumentFragment() {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates a <code>Text</code> node given the specified string.
   * @param data The data for the node.
   * @return The new <code>Text</code> object.
   */
  public Text               createTextNode(String data) {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates a <code>Comment</code> node given the specified string.
   * @param data The data for the node.
   * @return The new <code>Comment</code> object.
   */
  public Comment            createComment(String data) {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates a <code>CDATASection</code> node whose value  is the specified 
   * string.
   * @param data The data for the <code>CDATASection</code> contents.
   * @return The new <code>CDATASection</code> object.
   * @exception DOMException
   *   NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
   */
  public CDATASection       createCDATASection(String data)
                                               throws DOMException {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates a <code>ProcessingInstruction</code> node given the specified 
   * name and data strings.
   * @param target The target part of the processing instruction.
   * @param data The data for the node.
   * @return The new <code>ProcessingInstruction</code> object.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if an invalid character is specified.
   *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
   */
  public ProcessingInstruction createProcessingInstruction(String target, 
                                                           String data)
                                                           throws DOMException {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates an <code>Attr</code> of the given name. Note that the 
   * <code>Attr</code> instance can then be set on an <code>Element</code> 
   * using the <code>setAttribute</code> method. 
   * @param name The name of the attribute.
   * @return A new <code>Attr</code> object.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
   *   invalid character.
   */
  public Attr               createAttribute(String name)
                                            throws DOMException {
    return null; // === use the tagset if it exists; remember ownerDocument
  }

  /**
   * Creates an EntityReference object.
   * @param name The name of the entity to reference. 
   * @return The new <code>EntityReference</code> object.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
   *   invalid character.
   *   <br>NOT_SUPPORTED_ERR: Raised if this document is an HTML document. 
   */
  public EntityReference    createEntityReference(String name)
                                                  throws DOMException {
    return null; // === use the tagset if it exists; remember ownerDocument
  }


  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public TreeDocument() {
    super(Node.DOCUMENT_NODE, "#document");
  }

  /* === a document isn't an element anymore ===
  public TreeDocument(String tag) {
    super(tag, null, null, null);
    nodeType = (tag == null)? Node.DOCUMENT_NODE : Node.ELEMENT_NODE;
  }

  public TreeDocument(String tag, ActiveAttrList attrs, NodeList content) {
    super(tag, attrs, content);
    nodeType = (tag == null)? Node.DOCUMENT_NODE : Node.ELEMENT_NODE;
  }

  ========================= */

  public TreeDocument(ActiveAttrList attrs, 
		      ActiveElement headers, NodeList content) {
    this();
    // === this is a problem -- really wants to be an element ===
    if (headers != null) addChild(headers);
    if (headers != null) addChild(new TreeText("\n", true));
    Copy.appendNodes(content, this);
  }

  /**
   * deep copy constructor.
   */
  public TreeDocument(TreeDocument doc, boolean copyChildren){
    super(doc, copyChildren);
  }

  public ActiveNode shallowCopy() {
    return new TreeDocument(this, false);
  }

}



