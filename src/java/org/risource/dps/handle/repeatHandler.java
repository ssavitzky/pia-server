////// repeatHandler.java: <repeat> Handler implementation
//	$Id: repeatHandler.java,v 1.5 1999-04-07 23:21:26 steve Exp $

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

import org.risource.ds.Association;
import org.risource.ds.List;

import java.util.Enumeration;

/**
 * Handler for &lt;repeat&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: repeatHandler.java,v 1.5 1999-04-07 23:21:26 steve Exp $
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
    String tag = in.getTagName();
    Input  src = iterationSrc(content);
    Processor process  = iterationCxt(src, aContext, out, null, tag);
    List repeatSubs = new List();

    if (iterationInit(content, process, repeatSubs) > 0) {
      while (process.run()) src.toFirstNode();
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
    src.toFirstNode();
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
  public Input iterationSrc(ActiveNodeList nl) {
    return new FromNodeList(nl);
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


/* ***********************************************************************
 * Sub-elements:
 ************************************************************************/

/** Generic parent class for sub-elements. */
class repeat_subHandler extends repeatHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Restore the default action. */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /** Don't do any parse-time dispatching.  Let the subclasses handle it. */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  public repeat_subHandler() {
    /* Expansion control: */
    expandContent = true;	// false 	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

/** &lt;first&gt; expands only during initialization. */
class firstHandler extends repeat_subHandler {

  /** Special case: does nothing except during initialization. */
  public int actionCode(Input in, Processor p) {
    return Action.COMPLETED;
  }
  public void action(Input in, Context aContext, Output out) {
  }

  /** During initialization, &lt;first&gt; expands */
  public void initialize(ActiveElement e, Processor p) {
    Expand.processChildren(e, p, p.getOutput());
  }
}

/** &lt;finally&gt; expands only during cleanup. */
class finallyHandler extends repeat_subHandler {

  /** Special case: does nothing except during cleanup. */
  public int actionCode(Input in, Processor p) {
    return Action.COMPLETED;
  }
  public void action(Input in, Context aContext, Output out) {
  }

  /** During cleanup, &lt;finally&gt; expands */
  public void finish(ActiveElement e, Processor p) {
    Expand.processChildren(e, p, p.getOutput());
  }
}

/** &lt;while&gt; stops expansion if its contents are empty. */
class whileHandler extends repeat_subHandler {

  // No need for initialization 

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    if (! Test.orValues(content, aContext)) iterationStop(aContext);
  }
}


/** &lt;until&gt; stops expansion if its contents are not empty. */
class untilHandler extends repeat_subHandler {

  // No need for initialization 

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    if (Test.orValues(content, aContext)) iterationStop(aContext);
  }
}


/** &lt;foreach&gt; iterates through its content. */
class foreachHandler extends repeat_subHandler {

  /** Set up iteration variables ENTITY, ENTITY-list, and ENTITY-index. */
  public void initialize(ActiveElement e, Processor p) {
    String  name = getEntityName(Expand.expandAttrs(p, e.getAttrList()), "li");
    ActiveNodeList items = Expand.processChildren(e, p);
    items = new TreeNodeList(ListUtil.getListItems(items));
    p.setEntityValue(name + "-list", items, true);
    p.setEntityValue(name + "-index",
		     new TreeNodeList(new TreeText(0)), true);
    p.setEntityValue(name, null, true);
  }

  /** This is the per-iteration action.  It would be more efficient if the
   *	<code>initialize</code> method replaced the handler, but that wouldn't
   *	be thread-safe.
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String name = getEntityName(atts, "li");
    ActiveNodeList items = cxt.getEntityValue(name + "-list", true);
    String index = cxt.getEntityValue(name + "-index", true).toString();
    long n = MathUtil.getNumeric(index).longValue();

    if (n >= items.getLength()) {
      iterationStop(cxt);
      return;
    }

    ActiveNode item = items.activeItem((int)n);
    cxt.setEntityValue(name, new TreeNodeList(item), true);
    cxt.setEntityValue(name+"-index",
		       new TreeNodeList(new TreeText(n+1)), true);
  }
}

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
    ActiveNodeList v = cxt.getEntityValue(name, false);
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
    p.setEntityValue(name + "-step",
		     toValue(getParameter(atts, items, "step", "1")),
		     true);
    p.setEntityValue(name + "-stop",
		     toValue(getParameter(atts, items, "stop", "0")),
		     true);
    p.setEntityValue(name + "-start",
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
    Association step = getParameter(cxt, name+"-step");

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
	cxt.setEntityValue(name, new TreeNodeList(new TreeText(iiter)), true);
      } else {
	fiter = iter.doubleValue();
	fstop = stop.doubleValue();
	fstep = step.doubleValue();

	if (fstep > 0.0) {
	  if (fiter > fstop) iterationStop(cxt);
	} else {
	  if (fiter < fstop) iterationStop(cxt);
	}
	cxt.setEntityValue(name, new TreeNodeList(new TreeText(fiter)), true);
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
      cxt.setEntityValue(name, new TreeNodeList(new TreeText(iiter)), true);
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
      cxt.setEntityValue(name, new TreeNodeList(new TreeText(fiter)), true);
    }
  }
}

