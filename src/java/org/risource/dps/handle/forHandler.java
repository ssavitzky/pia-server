////// forHandler.java: <for> sub-element of <repeat> 
//	$Id: forHandler.java,v 1.2 1999-08-31 21:38:26 bill Exp $

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

/** &lt;for&gt; iterates through a sequence of numbers. */
class forHandler extends repeat_subHandler {

  /** Return a string, ensuring that it trims down to a number. */
  String numericParam(String s, String dflt) {
    s = s.trim();
    if (MathUtil.getNumeric(s) != null) return s;
    else return dflt;
  }

  /** Gets a text parameter from either the attributes or the items. */
  String getParameter(ActiveAttrList atts, ActiveNodeList items, 
		      String name, String dflt) {
    String result = atts.getAttribute(name);
    if (result != null) return numericParam(result, dflt);
    if (items == null) return dflt;

    int len = items.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode n = items.activeItem(i);
      if (n.getNodeType() == Node.ELEMENT_NODE
	  && n.getNodeName().equalsIgnoreCase(name)) {
	return numericParam(ListUtil.getFirstWord(n), dflt);
      }
    }
    return dflt;
  }

  /** Get an iteration parameter from the current context. */
  Association getParameter(Context cxt, String name) {
    ActiveNodeList v = cxt.getValueNodes(name, false);
    if (v == null) return null;
    return MathUtil.getNumeric(v);
  }

  ActiveNodeList toValue(String s) {
    return new TreeNodeList(new TreeText(s));
  }

  /** Set up iteration variables ENTITY, ENTITY-list, and ENTITY-index. */
  public void initialize(ActiveElement e, Processor p) {
    ActiveAttrList atts = Expand.expandAttrs(p, e.getAttrList());
    String name = getEntityName(atts, "n");
    ActiveNodeList items = Expand.processChildren(e, p);
    p.setValueNodes(name + "-step",
		     toValue(getParameter(atts, items, "step", "1")),
		     true);
    p.setValueNodes(name + "-stop",
		     toValue(getParameter(atts, items, "stop", "0")),
		     true);
    p.setValueNodes(name + "-start",
		     toValue(getParameter(atts, items, "start", "0")),
		     true);
  }

  /** This is the per-iteration action.  It would be more efficient if the
   *	<code>initialize</code> method replaced the handler, but that wouldn't
   *	be thread-safe.
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String name = getEntityName(atts, "n");

    Association iter = getParameter(cxt, name);
    Association stop = getParameter(cxt, name+"-stop");
    if (stop == null){
       	cxt.message(-2,  name + "-stop cannot be found initializig <for>",
		    0, true);
	return;
    }
    Association step = getParameter(cxt, name+"-step");
    if (step == null){
       	cxt.message(-2,  name + "-step cannot be found initializig <for>",
		    0, true);
	return;
    }
    long iiter;
    long istop;
    long istep;
    double fiter;
    double fstop;
    double fstep;

    if (iter == null) {
      // First time through the loop.  Set the iteration variable to start.
      // We also have to check for immediate termination.
      iter = getParameter(cxt, name+"-start");
      if (iter.isIntegral() && stop.isIntegral() && step.isIntegral()) {
	iiter = iter.longValue();
	istop = stop.longValue();
	istep = step.longValue();
	if (istep > 0) {
	  if (iiter > istop) iterationStop(cxt);
	} else if (istep < 0) {
	  if (iiter < istop) iterationStop(cxt);
	}
	cxt.setValueNodes(name, new TreeNodeList(new TreeText(iiter)), true);
      } else {
	fiter = iter.doubleValue();
	fstop = stop.doubleValue();
	fstep = step.doubleValue();

	if (fstep > 0.0) {
	  if (fiter > fstop) iterationStop(cxt);
	} else {
	  if (fiter < fstop) iterationStop(cxt);
	}
	cxt.setValueNodes(name, new TreeNodeList(new TreeText(fiter)), true);
      }
    } else if (iter.isIntegral() && stop.isIntegral() && step.isIntegral()) {
      iiter = iter.longValue();
      istop = stop.longValue();
      istep = step.longValue();

      if (istep > 0) {
	iiter += istep;
	if (iiter > istop) iterationStop(cxt);
      } else if (istep < 0) {
	iiter -= istep;
	if (iiter < istop) iterationStop(cxt);
      }
      cxt.setValueNodes(name, new TreeNodeList(new TreeText(iiter)), true);
    } else {
      fiter = iter.doubleValue();
      fstop = stop.doubleValue();
      fstep = step.doubleValue();

      if (fstep > 0.0) {
	fiter += fstep;
	if (fiter > fstop) iterationStop(cxt);
      } else {
	fiter -= fstep;
	if (fiter < fstop) iterationStop(cxt);
      }
      cxt.setValueNodes(name, new TreeNodeList(new TreeText(fiter)), true);
    }
  }
}

