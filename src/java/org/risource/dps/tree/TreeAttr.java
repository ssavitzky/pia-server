////// TreeAttr.java -- implementation of ActiveAttr
//	$Id: TreeAttr.java,v 1.5 2001-01-11 23:37:37 steve Exp $

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

/**
 * An implementation of the ActiveAttr interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeAttr.java,v 1.5 2001-01-11 23:37:37 steve Exp $
 * @author steve@rii.ricoh.com 
 */
public class TreeAttr extends TreeValue implements ActiveAttr {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected boolean specified = false;

  /************************************************************************
  ** Attribute interface:
  ************************************************************************/

  public void setValueNodes(Context cxt, ActiveNodeList value){
    setIsAssigned( true );
    setSpecified(value != null);
    super.setValueNodes(value);
  }

  public void setValueNodes(ActiveNodeList value){
    setIsAssigned( true );
    setSpecified(value != null);
    super.setValueNodes(value);
  }

  public void setSpecified(boolean specified){ this.specified = specified; }
  public boolean getSpecified(){return specified;}


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public ActiveAttr asAttribute() 	{ return this; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeAttr() {
    super(Node.ATTRIBUTE_NODE, null);
  }

  public TreeAttr(TreeAttr e, boolean copyChildren) {
    super(e, copyChildren);
    specified = e.specified;
  }

  /** Construct a node with given data. */
  public TreeAttr(String name, ActiveNodeList value) {
    super(Node.ATTRIBUTE_NODE, name, value);
    
    // explicitly assigned value
    setIsAssigned( true );
    setSpecified( value != null );
  }

  /** Construct a node with given data and handler. */
  public TreeAttr(String name, ActiveNodeList value, Handler handler) {
    super(Node.ATTRIBUTE_NODE, name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  public String startString() {
    return getName() + ((! getSpecified() || getValueNodes() == null)? "" : "=");
  }

  public String contentString() {
    ActiveNodeList v = getValueNodes();
    if (! getSpecified() || v == null) return "";

    String sv = getValueNodes().toString();
    if (sv.indexOf('"') > 0) return "'" + sv + "'";
    else return "\"" + getValueNodes() + "\"";
  }

  public String endString() {
    return "";
  }


  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node. 
   *	Since an attribute's value is kept in its children, we actually
   *	need to do a deep copy.
   */
  public ActiveNode shallowCopy() {
    return new TreeAttr(this, false);
  }

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new TreeAttr(this, true);
  }

}
