////// protectHandler.java: <protect> Handler implementation
//	$Id: protectHandler.java,v 1.9 2001-04-03 00:04:30 steve Exp $

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

import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

/**
 * Handler for &lt;protect&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: protectHandler.java,v 1.9 2001-04-03 00:04:30 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class protectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Just return the content. */
  public void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    if (atts.hasTrueAttribute("markup")) {
      putText(out, aContext, TextUtil.toExternalForm(content));
    } else {
      putList(out, content);
    }
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "result"))	 return protect_result.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public protectHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = false;	// true	recognize tags?
    parseEntitiesInContent = false;	// true	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }

  protectHandler(ActiveElement e) {
    this();
    // customize for element.
    if (e.hasTrueAttribute("result")) {
      syntaxCode=NORMAL;
      parseElementsInContent = true;	// false	recognize tags?
      parseEntitiesInContent = true;	// false	recognize entities?
    }
  }
}

class protect_result extends protectHandler {
  // The inherited action works because syntaxCode=NORMAL in super(e).
  public protect_result(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new protect_result(e); }
}

