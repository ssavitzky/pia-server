// TreeAttrList.java
// $Id: TreeAttrList.java,v 1.3 1999-04-23 00:22:18 steve Exp $

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
import java.util.Enumeration;

import org.w3c.dom.*;
import org.risource.dps.active.*;

import org.risource.dps.Namespace;
import org.risource.dps.Tagset;
import org.risource.dps.Context;
import org.risource.dps.Input;

import org.risource.dps.util.Test;

/**
 * Implementing Attribute list.
 *
 */
public class TreeAttrList extends TreeNodeMap
  implements ActiveAttrList, NodeList, Namespace
{

  public TreeAttrList(){
    caseSensitive=false;
  }

  /**
   * Copy another Attribute list.
   * Deep copy.
   */
  public TreeAttrList(NamedNodeMap l){
    this();
    if( l != null )
      initialize( l );
  }

  public ActiveAttr getActiveAttr(String name) {
    return (ActiveAttr)getNamedItem(name);
  }

  /**
   * Deep copy of all attributes in the given list.
   */
  protected void initialize(NamedNodeMap l){
    if( l == null ) return;

    for (int i = 0; i < l.getLength(); ++i) {
      Attr attr = (Attr)l.item( i );
      if ( attr != null ) {
	setNamedItem(attr);
      }
    }
  }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    String result = "";
    int length = getLength();
    for (int i = 0; i < length; ++i) try {
      Attr attr = (Attr)item(i);
      if (i != 0) result += " ";
      result += attr.toString();
    }catch(DOMException e){
    }
    return result;
  }

  /************************************************************************
  ** Attribute convenience functions:
  ************************************************************************/

  /** Convenience function: get an Attribute by name and return its value. */
  public ActiveNodeList getAttributeValue(String name) {
    ActiveAttr attr = getActiveAttr(name);
    return (attr == null)? null : attr.getValueNodes(null);
  }

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttribute(String name) {
    ActiveNode attr = getActiveAttr(name);
    // === getAttribute: need to return value as a string in INTERNAL form
    NodeList value = (attr == null)? null : attr.getValueNodes(null);
    return (value == null)? null : value.toString();
  }
	 
  /** Convenience function: get an Attribute by name and return its value
   *	as a boolean
   */
  public boolean hasTrueAttribute(String name) {
    return Test.trueValue(getActiveAttr(name));
  }

  public void setAttributeValue(String aname, ActiveNodeList value) {
    TreeAttr attr = new TreeAttr(aname, value);
    //attr.setSpecified(value != null);
    setBinding( aname, attr );
  }

  public void setAttributeValue(String name, ActiveNode value) {
    setAttributeValue(name, new TreeNodeList(value));
  }

  public void setAttribute(String name, String value) {
    setAttributeValue(name, new TreeText(value));
  }

  /** Append a new attribute.
   *	Can be more efficient than setAttribute.
   */
  public void addAttribute(String aname, ActiveNodeList value) {
    ActiveAttr attr = new TreeAttr(aname, value);
    //attr.setSpecified(value != null);
    setBinding( aname, attr );
  }

  /************************************************************************
  ** Namespace Operations:
  ************************************************************************/

  public String getName() { return "#attributes"; }

  public ActiveNodeList getValueNodes(Context cxt, String name) {
    // === getValueNodes should pass context to getAttributeValue ===
    return getAttributeValue(name);
  }

  public ActiveNode setBinding(String name, ActiveNode binding) {
    if (!(binding instanceof ActiveAttr)) 
      throw new DPSException(DPSException.NOT_ACTIVE_NODE_ERR,
			     "ActiveAttr expected");
    return super.setBinding(name, binding);
  }

  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value) {
    TreeAttr attr = new TreeAttr(name, value);
    //attr.setSpecified(value != null);
    setBinding( name, attr );
    return attr;
  }
  
  public Input getBindings() {
    return null; // === getBindings
  }

  public Enumeration getNames() {
    return null; // === getNames
  }

  public boolean containsNamespaces() { return false; }
}



