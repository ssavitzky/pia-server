////// expandHandler.java: <expand> Handler implementation
//	$Id: expandHandler.java,v 1.5 1999-04-07 23:21:22 steve Exp $

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
import org.risource.dps.output.DiscardOutput;

/**
 * Handler for &lt;expand&gt;  <p>
 *
 * @version $Id: expandHandler.java,v 1.5 1999-04-07 23:21:22 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class expandHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Expand.processNodes(content, aContext, out);
  }

  /** This does the parse-time dispatching. */
  public Action expandActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "hide")) 	 return expand_hide.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public expandHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;		// EMPTY, QUOTED, 0 (check)
  }
}

class expand_hide extends expandHandler {
  /** This routine does the work, such as it is.  */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasActiveChildren()) {
      aContext.subProcess(in, new DiscardOutput()).processChildren();
    }
  }
  public expand_hide(ActiveElement e) { super(); }
  static Action handle(ActiveElement e) { return new expand_hide(e); }
}
