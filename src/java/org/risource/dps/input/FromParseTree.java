////// FromParseTree.java: Input from ParseTree
//	$Id: FromParseTree.java,v 1.3 1999-03-12 19:26:50 steve Exp $

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


package org.risource.dps.input;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;

import org.risource.dom.Node;
import org.risource.dom.Element;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Input from a parse tree, comprised entirely of Active nodes.<p>
 *
 * @version $Id: FromParseTree.java,v 1.3 1999-03-12 19:26:50 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Fromken
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class FromParseTree extends ActiveInput implements Input {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected ActiveNode root = null;

  /************************************************************************
  ** Methods:
  ************************************************************************/

  public ActiveNode getRoot() { return root; }
  public void setRoot(ActiveNode newRoot) { root = newRoot; setNode(newRoot); }

  /************************************************************************
  ** Construction:
  ************************************************************************/
  public FromParseTree() {
  }

  public FromParseTree(ActiveNode newRoot) {
    setRoot(newRoot);
  }

  public FromParseTree(String tagName) {
    this(new ParseTreeElement(tagName, null));
  }
}
