// ParseTreeNamed.java
// $Id: ParseTreeNamed.java,v 1.4 1999-03-31 23:08:22 steve Exp $

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


package org.risource.dps.active;

import java.io.*;
import org.risource.dom.NodeList;

import org.risource.dps.Handler;
import org.risource.dps.Namespace;
import org.risource.dps.Input;
import org.risource.dps.Context;
import org.risource.dps.input.FromParseNodes;

/** 
 * Abstract base class for nodes with names and values. 
 *	Should really distinguish between values in the children,
 *	and values in a nodelist.
 */
public abstract class ParseTreeNamed extends ParseTreeNode  {

  protected NodeList value = null;
  protected Namespace names = null;

  protected boolean isAssigned = false;
  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() { return isAssigned; }

  /** Get the node's value. 
   *
   *	Eventually we may want a way to distinguish values stored in
   *	the children from values stored in a separate nodelist.
   */
  public NodeList getValueNodes(){ return value/*getChildren()*/; }
  public String getValue(){ return value == null? null : value.toString(); }

  /** Get the node's value as an Input. 
   */
  public Input getValueInput(){ 
    return new FromParseNodes(getValueNodes());
  }

  /** Get the associated namespace, if any. */
  public Namespace asNamespace() { return names; }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   *
   * === WARNING! This will change substantially when the DOM is updated!
   */
  public void setValueNodes(NodeList newValue) {
    if (newValue == null) {
      isAssigned = false;
      value = null;
      names = null;
      return;
    } else {
      isAssigned = true;
      if (newValue instanceof Namespace) {
	names = (Namespace)newValue;
	value = newValue;
      } else {
	value = new ParseNodeList(newValue);
	names = null;
      }
    }
    //setChildren(newValue);
  }

  public void setValue(String value){
    setValueNodes(value==null? null
		  : new ParseNodeList(new ParseTreeText(value)));
  }


  public ParseTreeNamed() {
    super();
  }

  public ParseTreeNamed(String n, Handler h) {
    super(h);
    setName( n );
  }

  public ParseTreeNamed(String n) {
    super();
    setName( n );
  }

  public ParseTreeNamed(String n, NodeList v) {
    this( n );
    setValueNodes( v );
  }

  /**
   * deep copy constructor.
   */
  public ParseTreeNamed(ParseTreeNamed attr, boolean copyChildren){
    super(attr, copyChildren);
    setName( attr.getName() );
    setValueNodes( attr.getValueNodes() );
  }

  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name){ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName(){ return name; }
  
  /* attribute name */
  protected String name;

}



