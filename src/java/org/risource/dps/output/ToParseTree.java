////// ToParseTree.java:  Output to ParseTree
//	$Id: ToParseTree.java,v 1.8 2001-04-03 00:04:43 steve Exp $

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


package org.risource.dps.output;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeElement;

import org.w3c.dom.Node;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to a parse tree, comprised entirely of Active nodes.<p>
 *
 * @version $Id: ToParseTree.java,v 1.8 2001-04-03 00:04:43 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class ToParseTree extends ActiveOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ActiveNode root = null;

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveNode getRoot() { return root; }
  public void setRoot(ActiveNode newRoot) { root = newRoot; setNode(newRoot); }

  public void putNode(Node aNode) {
    super.putNode(aNode);
    if (root == null) root = active;
  }

  public void startNode(Node aNode) {
    super.startNode(aNode);
    if (root == null) root = active;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToParseTree(Tagset ts) {
    super(ts);
  }

  public ToParseTree(ActiveNode newRoot, Tagset ts) {
    this(ts);
    setRoot(newRoot);
  }

}
