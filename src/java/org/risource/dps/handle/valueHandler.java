////// valueHandler.java: <value> Handler implementation
//	$Id: valueHandler.java,v 1.6 1999-07-14 20:20:30 steve Exp $

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
 * Handler for &lt;value&gt;....&lt;/&gt;  <p>
 *
 *	This is a sub-element of &lt;define&gt;.  It actually performs no
 *	actions; we just need to make sure a corresponding node ends up in
 *	the output where <code>defineHandler</code> can find it.
 *
 *	The handler's class is used to recognize the corresponding element.
 *
 * @version $Id: valueHandler.java,v 1.6 1999-07-14 20:20:30 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class valueHandler extends GenericHandler {

  /** The default is to expand the content at the point where it's defined. */
  static valueHandler DEFAULT = new valueHandler();

  /** QUOTED_VALUE is not expanded at the point where it is defined. */
  static valueHandler QUOTED_VALUE  = new valueHandler(QUOTED);

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;value&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Actually do the work. 
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);
    // === should be able to skip expanding the attrs altogether for <value>
    out.startNode(element);
    Copy.copyNodes(content, out);
    out.endElement(e.isEmptyElement() || e.implicitEnd());
  }

  /** This does the parse-time dispatching.
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "quoted")) 	 return QUOTED_VALUE;
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public valueHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  valueHandler(int syntax) {
    syntaxCode = syntax;
  }

  valueHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
