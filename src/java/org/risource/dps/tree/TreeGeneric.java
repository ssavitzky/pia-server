// TreeGeneric.java
// $Id: TreeGeneric.java,v 1.3 1999-04-23 00:22:24 steve Exp $

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
import org.w3c.dom.*;
import org.risource.dps.active.*;

import org.risource.dps.Handler;
import org.risource.dps.Namespace;

/** 
 * Abstract base class for nodes with names, attributes, and a variable 
 *	nodeType.
 *
 * <p>	Generic nodes can be used for things like Tagset or Entity that
 *	might be either elements or declarations in different contexts.
 *
 * <p>	A Generic node has <em>two</em> names: its ``name'' and its
 *	``tagname''.  The first is always accessible using 
 *	<code>getName</code>, the second with <code>getTagName</code>. 
 *	When functioning as an Element, <code>getNodeName</code> returns
 *	the tagname, and the ``name'' is accessible as the value of the 
 *	<code>name</code> <em>attribute</em>.  
 */
public class TreeGeneric extends TreeElement  {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected NodeList value;
  protected Namespace names = null;
  protected boolean isAssigned = false;

  /** The ``name'' of this node.  Note that the ActiveNode protected
   *	variable <code>nodeName</code> contains the tagname. 
   */
  protected String name;

  /************************************************************************
  ** Accessors:
  ************************************************************************/

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(short value)		{ nodeType = value; }
  
  /** It is possible to set the tag name. */
  void setTagName(String value)		{ nodeName = value; }

  /**
   * Set name -- <em>not</em> the tagname.
   * @param name name.
   */
  public void setName(String name)	{ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName()		{ return name; }

  /** 
   * Return the DOM ``nodeName'' appropriate for the current node type.
   */
  public String getNodeName() {
    return (nodeType == Node.ELEMENT_NODE)? nodeName : name;
  }
  
  /** Get the associated namespace, if any. */
  public Namespace asNamespace() { return names; }

  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() 	{ return isAssigned; }


  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   */
  public void setValueNodes(NodeList newValue) {
    if (value == null) {
      isAssigned = false;
      value = null;
      return;
    } else {
      isAssigned = true;
    }
    if (newValue instanceof Namespace) {
      names = (Namespace)newValue;
      value = newValue;
    } else value = new TreeNodeList(newValue);
  }

  public void setValue(String newValue) {
    setValueNodes(new TreeNodeList(new TreeText(newValue)));
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public TreeGeneric() {
    super();
  }

  public TreeGeneric(String n, NodeList v) {
    super();
    setName( n );
    setValueNodes( v );
  }

  public TreeGeneric(String tag) {
    super(tag, null, null, null);
  }

  public TreeGeneric(String tag, String name) {
    super(tag, null, null, null);
    setName(name);
  }

  /**
   * deep copy constructor.
   */
  public TreeGeneric(TreeGeneric attr, boolean copyChildren){
    super((TreeElement)attr, copyChildren);
    setName( attr.getName());
    setValueNodes(attr.getValueNodes(null));
  }

  public ActiveNode shallowCopy() {
    return new TreeGeneric(this, false);
  }

  public ActiveNode deepCopy() {
    return new TreeGeneric(this, true);
  }

}



