////// finallyHandler.java: <finally> sub-element of <repeat>
//	$Id: finallyHandler.java,v 1.3 2001-04-03 00:04:24 steve Exp $

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


package org.risource.dps.handle;

import org.w3c.dom.Node;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeText;
import org.risource.dps.namespace.BasicEntityTable;

import org.risource.ds.Association;
import org.risource.ds.List;

import java.util.Enumeration;

/** &lt;finally&gt; expands only during cleanup. */
class finallyHandler extends repeat_subHandler {

  /** Special case: does nothing except during cleanup. */
  public int getActionCode() {
    return Action.ACTIVE_NODE;
  }
  public void action(Input in, Context aContext, Output out) {
  }

  /** During cleanup, &lt;finally&gt; expands */
  public void finish(ActiveElement e, Processor p) {
    Expand.processChildren(e, p, p.getOutput());
  }
}

