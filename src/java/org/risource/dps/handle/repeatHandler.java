////// repeatHandler.java: <repeat> Handler implementation
//	$Id: repeatHandler.java,v 1.9 1999-06-25 00:41:41 steve Exp $

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

/**
 * Handler for &lt;repeat&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: repeatHandler.java,v 1.9 1999-06-25 00:41:41 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class repeatHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Actually do the work.  
    // There were no attributes to dispatch on, so repeat the content until
    // something stops the processor.
    content = new TreeNodeList(content);
    String tag = in.getTagName();
    FromNodeList src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, null, tag);
    List repeatSubs = new List();

    if (iterationInit(content, process, repeatSubs) > 0) {
      while (process.run()) {
	  src.toFirst();
      }
      iterationFinish(process, repeatSubs);
    }
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "numeric"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "start"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "stop"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "step"))	 return repeat_numeric.handle(e);
    if (dispatch(e, "list")) 	 return repeat_list.handle(e);
    return this;
  }


  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Perform one iteration through the content. */
  public void iterate(Processor p, ActiveNode n,
		      ActiveEntity var, Input src) {
    var.setValueNodes(p, new TreeNodeList(n));
    p.run();
    src.toFirst();
  }

  /** Scan the content and tell each sub-element to set itself up.
   *	The sub-elements are counted and pushed onto a list.
   */
  public int iterationInit(ActiveNodeList content,
			   Processor process, List subs) {
    int count = 0;
    int len = content.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = content.activeItem(i);
      Action a = item.getAction();
      if (a != null && a instanceof repeatHandler) {
	repeatHandler rh = (repeatHandler)a;
	subs.push(item);
	count++;
	rh.initialize(item.asElement(), process); 
      }
    }
    return count;
  }

  /** Go through the list of sub-elements made by iterationInit, and
   *	execute the <code>finish</code> method of each.
   */
  public void iterationFinish(Processor process, List subs) {
    Enumeration items = subs.elements();
    while (items.hasMoreElements()) {
      ActiveNode item = (ActiveNode)items.nextElement();
      Action a = item.getAction();
      if (a != null && a instanceof repeatHandler) {
	repeatHandler rh = (repeatHandler)a;
	rh.finish(item.asElement(), process); 
      }
    }
  }

  /** Return the name of the iteration variable (entity) */
  String getEntityName(ActiveAttrList atts, String dflt) {
    return (atts == null)? dflt
      : (atts.hasTrueAttribute("entity"))? atts.getAttribute("entity")
      : dflt;
  }

  /** Return an ActiveEntity to use as an iteration variable. */
  public ActiveEntity iterationVar(ActiveAttrList atts, Context cxt, 
				   String dflt) {
    String name = atts.getAttribute("entity");
    Tagset ts = cxt.getTopContext().getTagset();
    if (name != null) name = name.trim();
    if (name == null) name = dflt;
    return ts.createActiveEntity(name, null);
  }

  /** Return a suitable Input for iterating through the content. */
  public FromNodeList iterationSrc(ActiveNodeList nl) {
    return new FromNodeList(new TreeNodeList(nl));
  }

  /** Return a suitable context in which the iteration variables are bound. */
  public Processor iterationCxt(Input in, Context cxt, Output out,
				ActiveEntity var, String tag) {
    BasicEntityTable ents = new BasicEntityTable(tag);
    if (var != null) ents.setBinding(var.getNodeName(), var);
    return cxt.subProcess(in, out, ents);
  }

  /** Stop the iteration. */
  public void iterationStop(Context aContext) {
    aContext.getProcessor().stop();
  }

  /** Intended for sub-elements: perform initialization. */
  public void initialize(ActiveElement e, Processor p) {
  }

  /** Intended for sub-elements: perform cleanup. */
  public void finish(ActiveElement e, Processor p) {
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public repeatHandler() {
    /* Expansion control: */
    expandContent = false;	// true 	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }
}

/* ***********************************************************************
 * Attribute Subclasses:
 ************************************************************************/

class repeat_numeric extends repeatHandler {
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Association start = MathUtil.getNumeric(atts, "start", "0");
    Association stop  = MathUtil.getNumeric(atts, "stop", "0");
    Association step  = MathUtil.getNumeric(atts, "step", "1");

    ActiveEntity ent  = iterationVar(atts, aContext, "n");
    Input        src  = iterationSrc(content);
    Processor process = iterationCxt(src, aContext, out, ent, in.getTagName());

    if (start.isIntegral() && stop.isIntegral() && step.isIntegral()) {
      long iiter = start.longValue();
      long istop  = stop.longValue();
      long istep  = step.longValue();

      if (istep > 0) {
	for ( ; iiter <= istop; iiter += istep)
	  iterate(process, new TreeText(iiter), ent, src);
      } else {
	for ( ; iiter >= istop; iiter += istep)
	  iterate(process, new TreeText(iiter), ent, src);
      }
    } else {
      double fiter = start.doubleValue();
      double fstop = stop.doubleValue();
      double fstep = step.doubleValue();

      if (fstep > 0.0) {
	for ( ; fiter <= fstop; fiter += fstep)
	  iterate(process, new TreeText(fiter), ent, src);
      } else {
	for ( ; fiter >= fstop; fiter += fstep)
	  iterate(process, new TreeText(fiter), ent, src);
      }
    }
  }
  static repeat_numeric handle = new repeat_numeric();
  static Action handle(ActiveElement e) { return handle; }
}


class repeat_list extends repeatHandler {
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    ActiveEntity ent  = iterationVar(atts, aContext, "li");
    Input        src  = iterationSrc(content);
    Processor process = iterationCxt(src, aContext, out, ent, in.getTagName());

    Enumeration iter  = ListUtil.getListItems(atts.getAttributeValue("list"));
    while (iter.hasMoreElements())
      iterate(process, (ActiveNode) iter.nextElement(), ent, src);
  }
  static repeat_list handle = new repeat_list();
  static Action handle(ActiveElement e) { return handle; }
}


