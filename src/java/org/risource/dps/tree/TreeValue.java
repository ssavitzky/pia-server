// TreeValue.java
// $Id: TreeValue.java,v 1.7 2001-04-03 00:04:58 steve Exp $

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


package org.risource.dps.tree;

import java.io.*;
import org.w3c.dom.*;
import org.risource.dps.active.*;

import org.risource.dps.Handler;
import org.risource.dps.Namespace;
import org.risource.dps.Input;
import org.risource.dps.Context;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.util.TextUtil;

/** 
 * Abstract base class for nodes with names and values. 
 *	The values are, by default, kept in the children.
 */
public abstract class TreeValue extends TreeNode implements ActiveValue {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Namespace 	   names       	= null;
  protected ActiveNodeList nodeValue 	= null;
  protected boolean 	   isAssigned 	= false;

  /************************************************************************
  ** Node interfaces:
  ************************************************************************/

  public String getName() { return getNodeName(); }
  public String getValue() {
    return TextUtil.getCharData(getValueNodes());
  }
  public void setValue(String newValue) {
    setValueNodes(new TreeNodeList(new TreeText(newValue)));
  }

  public String getNodeValue() { return getValue(); }
  public void setNodeValue(String newValue) { setValue(newValue); }

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() { return isAssigned; }
  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Get the node's value in a given context. 
   *
   * <p> ActiveValue nodes have intrinsic value, so we simply ignore 
   *	 the context in this case. 
   */
  public ActiveNodeList getValueNodes(Context cxt){ 
    return getValueNodes();
  }

  /** Get the node's value. 
   *
   * <p> Eventually we may want a way to distinguish values stored in
   *	 the children from values stored in a separate nodelist.
   */
  public ActiveNodeList getValueNodes(){ 
    return (nodeValue != null)? nodeValue 
      : hasChildNodes()? (ActiveNodeList) new TreeChildList( this )
      : nodeValue; 
  }

  /** Get the node's value as an Input. 
   */
  public Input fromValue(Context cxt){ 
    return new FromNodeList(getValueNodes(cxt));
  }

  protected void smashValue(ActiveNodeList newValue) {

    // nodeValue = new TreeNodeList(newValue); // === smash children!

    while (hasChildNodes()) { removeChild(getFirstChild()); }
    if (newValue != null) for (int i = 0; i < newValue.getLength(); ++i) {
      addChild(newValue.activeItem(i));
    }

    nodeValue = new TreeChildList(this); // kludge for legacy code
  }

  /** Get the associated namespace, if any. */
  public Namespace asNamespace() { return names; }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValueNodes(ActiveNodeList newValue) {
    if (newValue == null) {
      isAssigned = false;
      names = null;
    } else {
      isAssigned = true;
      if (newValue instanceof Namespace) {
	names = (Namespace)newValue;
	// === this may not work for namespaces (e.g. attr. lists)!!!!!
      } else {
	names = null;
      }
    }
    smashValue(newValue);
    ActiveNodeList v = getValueNodes();
    if (names == null && v != null && v.getLength() == 1 &&
	v.item(0) instanceof Namespace)
      names = (Namespace)v.item(0);
  }

  public void setValueNodes(Context cxt, ActiveNodeList newValue) {
    setValueNodes(newValue);
  }

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public TreeValue() {
    super();
  }

  public TreeValue(short type, String name, Handler h) {
    super(type, name, h);
  }

  public TreeValue(short type, String name) {
    super(type, name);
  }

  public TreeValue(short type, String name, ActiveNodeList v) {
    this(type, name);
    if (v != null) setValueNodes( v );
  }

  /** Construct a node with given data and handler. */
  public TreeValue(short type, String name, ActiveNodeList value,
		   Handler handler) {
    super(type, name, handler);
    if (value != null) setValueNodes(value);
  }

  /**
   * copy constructor.
   *  Note that we copy the value even if <code>copyChildren</code> is false. 
   */
  public TreeValue(TreeValue attr, boolean copyChildren){
    super(attr, false);
    names = attr.names;
    isAssigned = attr.isAssigned;
    setValueNodes(attr.getValueNodes());
  }

  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name){ nodeName = name; }
  
}



