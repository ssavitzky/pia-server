////// TreeElement.java -- implementation of ActiveElement
//	$Id: TreeElement.java,v 1.6 2001-01-11 23:37:39 steve Exp $

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

import org.w3c.dom.*;
import org.risource.dps.active.*;
import org.risource.dps.util.Test;

import org.risource.dps.Handler;

/**
 * An implementation of the ActiveElement interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeElement.java,v 1.6 2001-01-11 23:37:39 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Context
 * @see org.risource.dps.Processor
 */
public class TreeElement extends TreeNode implements ActiveElement
{

  /************************************************************************
  ** Element interface:
  ************************************************************************/


  public NamedNodeMap getAttributes(){ return attrList; }
  public ActiveAttrList getAttrList(){ return attrList; }
  
  /**
   * The name of the element. For example, in: &lt;elementExample 
   * id="demo"&gt;  ... &lt;/elementExample&gt; , <code>tagName</code> has 
   * the value <code>"elementExample"</code>. Note that this is 
   * case-preserving in XML, as are all of the operations of the DOM. The 
   * HTML DOM returns the <code>tagName</code> of an HTML element in the 
   * canonical uppercase form, regardless of the case in the  source HTML 
   * document. 
   */
  public String getTagName(){ return nodeName; }

  /**
   * Retrieves an attribute value by name.
   * @param name The name of the attribute to retrieve.
   * @return The <code>Attr</code> value as a string, or the empty  string if 
   *   that attribute does not have a specified or default value.
   */
  public String             getAttribute(String name) {
    Attr attr = getAttributeNode(name);
    return (attr == null)? null : attr.getValue();
  }

  /**
   * Adds a new attribute. If an attribute with that name is already present 
   * in the element, its value is changed to be that of the value parameter. 
   * This value is a simple string, it is not parsed as it is being set. So 
   * any markup (such as syntax to be recognized as an entity reference) is 
   * treated as literal text, and needs to be appropriately escaped by the 
   * implementation when it is written out. In order to assign an attribute 
   * value that contains entity references, the user must create an 
   * <code>Attr</code> node plus any <code>Text</code> and 
   * <code>EntityReference</code> nodes, build the appropriate subtree, and 
   * use <code>setAttributeNode</code> to assign it as the value of an 
   * attribute.
   * @param name The name of the attribute to create or alter.
   * @param value Value to set in string form.
   * @exception DOMException
   *   INVALID_CHARACTER_ERR: Raised if the specified name contains an 
   *   invalid character.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   */
  public void               setAttribute(String name, 
                                         String value)
                                         throws DOMException 
  {
    if( name == null ) return;
    if (attrList == null) attrList = new TreeAttrList();
    attrList.setAttribute( name, value );
  }

  /**
   * Removes an attribute by name. If the removed attribute has a default 
   * value it is immediately replaced.
   * @param name The name of the attribute to remove.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   */
  public void               removeAttribute(String name)
                                            throws DOMException 
  {
    if (attrList != null) attrList.removeNamedItem(name);
  }

  /**
   * Retrieves an <code>Attr</code> node by name.
   * @param name The name of the attribute to retrieve.
   * @return The <code>Attr</code> node with the specified attribute name or 
   *   <code>null</code> if there is no such attribute.
   */
  public Attr               getAttributeNode(String name) {
    return (attrList == null)? null : attrList.getActiveAttr(name);
  }

  /**
   * Adds a new attribute. If an attribute with that name is already present 
   * in the element, it is replaced by the new one.
   * @param newAttr The <code>Attr</code> node to add to the attribute list.
   * @return If the <code>newAttr</code> attribute replaces an existing 
   *   attribute with the same name, the  previously existing 
   *   <code>Attr</code> node is returned, otherwise <code>null</code> is 
   *   returned.
   * @exception DOMException
   *   WRONG_DOCUMENT_ERR: Raised if <code>newAttr</code> was created from a 
   *   different document than the one that created the element.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>newAttr</code> is already an 
   *   attribute of another <code>Element</code> object. The DOM user must 
   *   explicitly clone <code>Attr</code> nodes to re-use them in other 
   *   elements.
   */
  public Attr               setAttributeNode(Attr newAttr)
                                             throws DOMException
  {
    if( newAttr == null ) return newAttr;
    if (attrList == null) attrList = new TreeAttrList();
    // === worry about inuse ===
    attrList.setNamedItem(newAttr);
    return newAttr;
  }

  /**
   * Removes the specified attribute.
   * @param oldAttr The <code>Attr</code> node to remove from the attribute 
   *   list. If the removed <code>Attr</code> has a default value it is 
   *   immediately replaced.
   * @return The <code>Attr</code> node that was removed.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
   *   <br>NOT_FOUND_ERR: Raised if <code>oldAttr</code> is not an attribute 
   *   of the element.
   */
  public Attr               removeAttributeNode(Attr oldAttr)
                                                throws DOMException
  {
    if( oldAttr == null ) throw new DPSException(DOMException.NOT_FOUND_ERR);
    if (attrList == null) throw new DPSException(DOMException.NOT_FOUND_ERR);
    Attr item = attrList.getActiveAttr(oldAttr.getName());
    if (item != oldAttr) throw new DPSException(DOMException.NOT_FOUND_ERR);
    attrList.removeNamedItem( oldAttr.getName());
    return item;
  }


  /**
   * Returns a <code>NodeList</code> of all descendant elements with a given 
   * tag name, in the order in which they would be encountered in a preorder 
   * traversal of the <code>Element</code> tree.
   * @param name The name of the tag to match on. The special value "*" 
   *   matches all tags.
   * @return A list of matching <code>Element</code> nodes.
   */
  public NodeList           getElementsByTagName(String name)
  {
    throw new DPSException(DOMException.NOT_SUPPORTED_ERR, 
			   "getElementsByTagName unimplemented");
    // === getElementsByTagName should use a filtered input
  }

  /**
   * Puts all <code>Text</code> nodes in the full depth of the sub-tree 
   * underneath this <code>Element</code> into a "normal" form where only 
   * markup (e.g., tags, comments, processing instructions, CDATA sections, 
   * and entity references) separates <code>Text</code> nodes, i.e., there 
   * are no adjacent <code>Text</code> nodes.  This can be used to ensure 
   * that the DOM view of a document is the same as if it were saved and 
   * re-loaded, and is useful when operations (such as XPointer lookups) that 
   * depend on a particular document tree structure are to be used.
   */
  public void               normalize() {
    throw new DPSException(DOMException.NOT_SUPPORTED_ERR, 
			   "normalize unimplemented");
  }


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  // Exactly one of the following will return <code>this</code>:

  public ActiveElement	 asElement() 	{ return this; }


  /************************************************************************
  ** ActiveElement interface:
  ************************************************************************/

  /** Get a named ActiveAttr node. */
  public ActiveAttr getActiveAttr(String name) {
    return (attrList == null)? null : attrList.getActiveAttr(name);
  }

  /** Make a copy with a different attribute list and content. */
  public ActiveElement editedCopy(ActiveAttrList atts, ActiveNodeList content) {
    return new TreeElement(this, atts, content);
  }

  /** Convenience function: get an Attribute by name and return its value. */
  public ActiveNodeList getAttributeValue(String name) {
    ActiveAttr attr = getActiveAttr(name);
    return (attr == null)? null : attr.getValueNodes(null);
  }

  public boolean hasTrueAttribute(String name) {
    return org.risource.dps.util.Test.trueValue(getActiveAttr(name));
  }

  /** Convenience function: change an attribute's value. 
   *	Carefully avoids replacing an old attribute binding by a new one,
   *	since the attribute may be special in some way.
   */
  public void setAttributeValue(String name, ActiveNodeList value) {
    ActiveAttr old = getActiveAttr(name);
    // === at some point we may want to make sure attr is not inherited
    // === from the DTD.
    if (old == null) setAttributeNode(new TreeAttr(name, value));
    else old.setValueNodes(null, value);
  }

  public void setAttributeValue(String name, ActiveNode value) {
    setAttributeValue(name, new TreeNodeList(value));
  }

  public void setAttributeValue(String name, String value) {
    setAttributeValue(name, new TreeText(value));
  }

  /** Append a new attribute.
   *	Can be more efficient than setAttribute.
   */
  public void addAttribute(String aname, ActiveNodeList value) {
    ActiveAttr attr = new TreeAttr(aname, value);
    attr.setSpecified(value != null);
    setAttributeNode(attr);
    //System.err.println("***Added attribute " + attr.toString() 
    //			 + " value= " + attr.getChildren());
  }


  /************************************************************************
  ** Additional Syntactic Operations:
  ************************************************************************/

  protected boolean isEmptyElement = false;
  protected boolean hasEmptyDelim = false;
  protected boolean implicitEnd = false;
  protected boolean mixedContent = true;

  /** Returns <code>true</code> if the Element has no content. 
   *
   *	This flag is redundant given a valid DTD; it exists to take care
   *	of the common case where the DTD is unknown or incomplete.  Also
   *	it can greatly speed up many operations that would otherwise require
   *	knowledge of the DTD.
   */
  public boolean isEmptyElement() {
    //System.err.println("<" + getTagName() + "> is " + 
    //	       (isEmptyElement? "empty" : "non-empty"));
    return isEmptyElement && !hasChildNodes();
  }

  /** Sets the internal flag corresponding to isEmptyElement. */
  public void setIsEmptyElement(boolean value) { 
    isEmptyElement = value;
    //System.err.println("<" + getTagName() + "> defined as " + 
    //	       (isEmptyElement? "empty" : "non-empty"));
  }

  /** Returns <code>true</code> if the Element has an XML-style 
   *	``<code>/</code>'' denoting an empty element.
   *
   *	This flag is redundant given a valid DTD; it exists to take care of
   *	the common case where the DTD is unknown or incomplete; for example,
   *	where XML extensions are mixed in with HTML.  Also it can greatly
   *	speed up many operations that would otherwise require knowledge of the
   *	DTD.
   */
  public boolean hasEmptyDelimiter() { 
    if (hasChildNodes()) hasEmptyDelim = false;
    return hasEmptyDelim;
  }

  /** Sets the internal flag corresponding to hasEmptyDelim. */
  public void setHasEmptyDelimiter(boolean value) {
    hasEmptyDelim = value;
    if (value) isEmptyElement = true;
  }

  /** Returns true if the Element has content but no end tag, 
   *	because the end tag can be deduced from context.
   *
   * <p> This flag is redundant given a valid DTD; it exists to take care
   *	 of the common case where the DTD is unknown or incomplete, or where
   *	 an effort needs to be made to preserve exact input formatting in
   *	 the parse tree.
   *
   * === Strictly speaking it has to be turned off if a Text node is inserted
   *	 following this Element.
   */
  public boolean implicitEnd() 		   { return implicitEnd; }

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag) { implicitEnd = flag; }


  /** Set the flag that says whether mixed text and elements are permitted
   *	in the content.
   */
  public void setMixedContent(boolean value) 	{ mixedContent = value; }

  /** Returns true if mixed text and elements are permitted in the content.
   *	Returns false if only elements are permitted.
   */
  public boolean hasMixedContent() { return mixedContent; }

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  /* Note that we don't need an instance variable for the tagname, 
   *	because that goes into the nodeName.
   */

  /** attribute list */
  protected ActiveAttrList attrList = null;

  /** flag for presence of closing semicolon on an Entity. */
  protected boolean hasClosingDelim = true;


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TreeElement(){
    super(Node.ELEMENT_NODE, "");
  }

  public TreeElement(String tagName){
    super(Node.ELEMENT_NODE, tagName);
  }

  public TreeElement(String tagName, Handler handler){
    super(Node.ELEMENT_NODE, tagName, handler);
  }

  /** Construct a TreeElement with given tagname, syntax,
   *	and Handler.
   * @see org.w3c.dom.Element
   */
  public TreeElement(String tagname, ActiveAttrList attrs, Handler handler)
  {
    this(tagname, handler);
    if (attrs != null) attrList = new TreeAttrList( attrs );
  }

  /** Construct a TreeElement with given tagname, attributes, and content. 
   * @see org.w3c.dom.Element
   */
  public TreeElement( String tag, ActiveAttrList atts, ActiveNodeList content ){
    this(tag);
    if (atts != null) 	 attrList = new TreeAttrList(atts);
    if (content != null) copyContent(content);
  }

  public TreeElement( String tag, ActiveAttrList atts, ActiveNodeList content,
			   Handler handler){
    this(tag, atts, handler);
    if (content != null) copyContent(content);
  }

  public TreeElement(Element e, NamedNodeMap atts, NodeList content) {
    this(e.getTagName());
    if (e instanceof ActiveElement) copyActiveInfo((ActiveElement)e);
    if (atts != null)       	    attrList = new TreeAttrList(atts);
    if (content != null) 	    copyContent(content);
  }

  protected final void copyContent(NodeList content) {
    if (content != null) {
      int len = content.getLength();
      for (int i = 0; i < len; ++i) {
	  appendChild(content.item(i));
      }
    }
  }

  protected final void copyActiveInfo(ActiveElement e) {
    handler = (Handler)e.getSyntax();
    action = e.getAction();
    isEmptyElement = e.isEmptyElement();
    hasEmptyDelim  = e.hasEmptyDelimiter();
    implicitEnd = e.implicitEnd();
  }

  public TreeElement(ActiveElement e,
		     ActiveAttrList atts, ActiveNodeList content) {
    this(e.getTagName());
    copyActiveInfo(e);
    if (atts != null)       	    attrList = new TreeAttrList(atts);
    if (content != null) 	    copyContent(content);
  }

  /** Copy constructor.  Selects deep or shallow copy */
  public TreeElement(TreeElement e, boolean copyChildren) {
    this(e.getTagName());
    copyActiveInfo(e);
    ActiveAttrList attrs = e.getAttrList();
    if (attrs != null) attrList = new TreeAttrList( attrs );
    if (copyChildren) copyChildren(e);
  }

  public TreeElement(Element e, NamedNodeMap  atts) {
    this(e, atts, null);
  }

  /** Construct a TreeElement with given tagname and attributes. 
   * @see org.w3c.dom.Element
   */
  public TreeElement(String tagname, ActiveAttrList attrs) {
    this(tagname);
    if (attrs != null) attrList = new TreeAttrList( attrs );
  }


  /** Construct a TreeElement with given tagname and attributes. 
   * @see org.w3c.dom.Element
   */
  public TreeElement(String tagname, NamedNodeMap attrs) {
    this(tagname);
    if (attrs != null) attrList = new TreeAttrList( attrs );
  }


  /** Construct a TreeElement with given tagname and syntax,
   *	and a given implicitEnd flag (almost invariably <code>true</code>).
   * @see org.w3c.dom.Element
   */
  public TreeElement(String tagname, ActiveAttrList attrs, boolean implicit)
  {
    this(tagname, attrs);
    implicitEnd = implicit;
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    String s = "<" + (nodeName == null ? "" : nodeName);
    NamedNodeMap attrs = getAttributes();
    if (attrs != null && attrs.getLength() > 0) {
      s += " " + attrs.toString();
    }
    if (hasEmptyDelimiter()) s += " /";
    s += ">";
    // if (!mixedContent && hasChildNodes()) s += "\n";
    return s;
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    if (!mixedContent && hasChildNodes()) {
      String s = "\n";
      for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
	s += "  " + n.toString() + "\n";
      }
      return s;
    } else {
      return (getChildNodes() == null)? "" : getChildNodes().toString();
    }
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    if (implicitEnd() || hasEmptyDelimiter()) return "";
    else return "</" + (nodeName == null ? "" : nodeName) + ">";
  }

  /** Convert the elment to a String in external form..
   */
  public String toString() {
    return startString() + contentString() + endString(); 
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeElement(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeElement(this, true);
  }

}
