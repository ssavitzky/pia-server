////// namespaceHandler.java: <namespace> Handler implementation
//	$Id: namespaceHandler.java,v 1.3 1999-03-12 19:26:24 steve Exp $

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
import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;
import org.risource.dom.Element;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

/**
 * Handler for &lt;namespace&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id: namespaceHandler.java,v 1.3 1999-03-12 19:26:24 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class namespaceHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;namespace&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    unimplemented(in, cxt); // do the work
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //if (dispatch(e, "")) 	 return namespace_.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public namespaceHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  namespaceHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
/*
class namespace_ extends namespaceHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, NodeList content) {
    unimplemented (in, cxt); // do the work
  }
  public namespace_(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new namespace_(e); }
}
*/
