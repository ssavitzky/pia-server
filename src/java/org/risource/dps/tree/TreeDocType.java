////// TreeDocType.java -- implementation of ActiveDeclaration
//	$Id: TreeDocType.java,v 1.4 2001-04-03 00:04:53 steve Exp $

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

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.active.*;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * An implementation of the ActiveDocType interface, suitable for use in 
 *	DPS parse trees.
 *
 * @version $Id: TreeDocType.java,v 1.4 2001-04-03 00:04:53 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.active.ActiveNode
 */
public class TreeDocType extends TreeDecl implements ActiveDocType {

  protected ActiveNodeMap entities;
  protected ActiveNodeMap notations;

  /************************************************************************
  ** Declaration Content:
  ************************************************************************/
 
  public NamedNodeMap   getEntities() { return entities; }

  public NamedNodeMap	getNotations() { return notations; }



  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeDocType() {
    super(Node.DOCUMENT_TYPE_NODE, "DOCTYPE", null, null);
  }

  public TreeDocType(TreeDocType e, boolean copyChildren) {
    super(e, copyChildren);
  }

  /** Construct a node with given data. */
  public TreeDocType(String name, String data) {
    this("DOCTYPE", name, new StringTokenizer(data), null);
  }

  /** Construct a node with given data and handler. */
  public TreeDocType(String dname, String iname, Enumeration items,
		     Handler handler) {
    // === really ought to parse the system and public id's out of items.
    super(Node.DOCUMENT_TYPE_NODE, dname, iname, items, handler);
  }


  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeDocType(this, false);
  }

}
