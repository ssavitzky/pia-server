////// foreachHandler.java: <foreach> sub-element of <repeat>
//	$Id: foreachHandler.java,v 1.3 2001-04-03 00:04:25 steve Exp $

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

/** &lt;foreach&gt; iterates through its content. */
class foreachHandler extends repeat_subHandler {

  /** Set up iteration variables ENTITY, ENTITY-list, and ENTITY-index. */
  public void initialize(ActiveElement e, Processor p) {
    String  name = getEntityName(Expand.expandAttrs(p, e.getAttrList()), "li");
    ActiveNodeList items = Expand.processChildren(e, p);
    items = new TreeNodeList(ListUtil.getListItems(items));
    p.setValueNodes(name + "-list", items, true);
    p.setValueNodes(name + "-index",
		     new TreeNodeList(new TreeText(0)), true);
    p.setValueNodes(name, null, true);
  }

  /** This is the per-iteration action.  It would be more efficient if the
   *	<code>initialize</code> method replaced the handler, but that wouldn't
   *	be thread-safe.
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String name = getEntityName(atts, "li");
    ActiveNodeList items = cxt.getValueNodes(name + "-list", true);
    if ( cxt.getValueNodes(name + "-index", true) == null){
	cxt.message(-2,  name + "-index cannot be found in <foreach>",0, true);
	return;
    }
    String index = cxt.getValueNodes(name + "-index", true).toString();
    long n = MathUtil.getNumeric(index).longValue();

    if (n >= items.getLength()) {
      iterationStop(cxt);
      return;
    }

    ActiveNode item = items.activeItem((int)n);
    cxt.setValueNodes(name, new TreeNodeList(item), true);
    cxt.setValueNodes(name+"-index",
		       new TreeNodeList(new TreeText(n+1)), true);
  }
}

