////// TreeDecl.java -- implementation of ActiveDeclaration
//	$Id: TreeDecl.java,v 1.2 1999-06-04 22:40:35 steve Exp $

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
import org.risource.dps.*;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * An implementation of the ActiveDecl interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeDecl.java,v 1.2 1999-06-04 22:40:35 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeDecl extends TreeNode implements ActiveDecl {

  /************************************************************************
  ** Declaration interface:
  ************************************************************************/

  protected String data = "";
  protected String declName = "";

  /** Return the name of the construct being declared.
   *	In the DOM, this is returned as the nodeName from DocumentType.
   *	In SGML, this is the word after the ``declaration name'', which in
   *	turn is the first identifier after the ``<code>!</code>'' character.
   *
   * @see #getDeclName
   * @see #getName
   */
  public String getItemName() 		{ return getNodeName(); }
  public void setItemName(String name) 	{ nodeName = name; }

  /** Return the name of the construct being declared.
   *	This is the same as <code>getItemName</code>; <code>getName</code>
   *	is used for this purpose in <code>DocumentType</code>.
   *
   * @see #getDeclName
   * @see #getName
   * @see org.w3c.dom.DocumentType#getName
   */
  public String getName() 		{ return getNodeName(); }

  /** Return the SGML ``declaration name''; the identifier immediately after
   *	the ``<code>&gt;!</code>'' character sequence that starts the 
   *	declaration.
   */
  public String getDeclName() 		{ return declName; }
  public void setDeclName(String value) { declName = value; }

  public void setData(String value) 	{ data = value; }
  public String getData() 	   	{ return data; }

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(short value) { nodeType = value; }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeDecl() {
  }

  public TreeDecl(TreeDecl e, boolean copyChildren) {
    super(e, copyChildren);
    setDeclName(e.getDeclName());
    setItemName(e.getItemName());
    setData(e.getData());
  }

  /** Construct a node with given declaration name, item name, and data.
   *	If the item name is null, it is obtained from the data.
   */
  public TreeDecl(short type, String dname, String iname, String data) {
    this(type, dname, iname, new StringTokenizer(data), null);
  }

  /** Construct a node with given names, data and handler.
   *	If the item name is null, it is obtained from the data.
   */
  public TreeDecl(short type, String dname, String iname, 
		  Enumeration items,  Handler handler) {
    super(type, iname, handler);
    setDeclName(dname);
    if (iname == null) {
      if (items.hasMoreElements()) {
	setItemName(items.nextElement().toString());
      }
    }
    String data = "";
    while (items.hasMoreElements()) {
      data += items.nextElement().toString();
      if (items.hasMoreElements()) data += " ";
    }
    setData(data);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "<!" + getDeclName() + " "
      + ((getItemName() == null)? "" : getItemName() + " ");
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getData()
      + (hasChildNodes()? "[" + getChildNodes().toString() + "]" : "");
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return ">";
  }


  /** Convert the Node to a String.
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
    return new TreeDecl(this, false);
  }

  public ActiveNode deepCopy() {
    return new TreeDecl(this, true);
  }

}
