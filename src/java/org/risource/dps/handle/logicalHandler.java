////// logicalHandler.java: <logical> Handler implementation
//	$Id: logicalHandler.java,v 1.5 1999-04-07 23:21:24 steve Exp $

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

import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

/**
 * Handler for &lt;logical&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: logicalHandler.java,v 1.5 1999-04-07 23:21:24 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class logicalHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** The default action is to extract the true components. */
  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = Expand.getContent(in, aContext);
    int len = content.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode child = content.activeItem(i);
      ActiveNodeList value = Test.getTrueValue(child, aContext);
      if (value != null) putList(out, value);
    }
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "and")) 	 return logical_and.handle(e);
    if (dispatch(e, "or")) 	 return logical_or.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public logicalHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }
}

class logical_and extends logicalHandler {
  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = Expand.getContent(in, aContext);
    ActiveNodeList last = null;
    int len = content.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode child = content.activeItem(i);
      last = Test.getTrueValue(child, aContext);
      if (last == null) return;
    }
    putList(out, last);

  }
  public logical_and(ActiveElement e) { super(); }
  static Action handle(ActiveElement e) { return new logical_and(e); }
}

class logical_or extends logicalHandler {
  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = Expand.getContent(in, aContext);
    ActiveNodeList value = null;
    int len = content.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode child = content.activeItem(i);
      value = Test.getTrueValue(child, aContext);
      if (value != null) { putList(out, value); return; }
    }
  }
  public logical_or(ActiveElement e) { super(); }
  static Action handle(ActiveElement e) { return new logical_or(e); }
}

