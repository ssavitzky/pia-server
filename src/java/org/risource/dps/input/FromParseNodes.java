////// FromParseNodes.java: Input from NodeList
//	$Id: FromParseNodes.java,v 1.4 1999-03-27 01:36:13 steve Exp $

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
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Input from a NodeList containing Active nodes.<p>
 *
 * @version $Id: FromParseNodes.java,v 1.4 1999-03-27 01:36:13 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Fromken
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class FromParseNodes extends ActiveInput implements Input {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected NodeList list;
  protected NodeEnumerator enum;

  /************************************************************************
  ** Overridden Methods:
  ************************************************************************/

  public Node toNextSibling() {
    if (depth > 0) return super.toNextSibling();
    setNode(enum.getNext());
    return active;
  }

  /************************************************************************
  ** Local Methods:
  ************************************************************************/

  public void toFirstNode() {
    enum = list.getEnumerator();
    setNode(enum.getFirst());
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FromParseNodes(NodeList nodes) {
    list = nodes;
    if (list == null) list = new ParseNodeList();
    enum = list.getEnumerator();
    setNode(enum.getFirst());
  }
}
