////// getHandler.java: <get> Handler implementation
//	$Id: getHandler.java,v 1.9 2001-04-03 00:04:26 steve Exp $

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
 * Handler for &lt;get&gt;  <p>
 *
 *	This is an approximation to the legacy &gt;get&gt;; it lacks many
 *	of the old extraction modifiers, which have moved to &lt;find&gt;. <p>
 *
 *	It is permissible for the <code>name</code> attribute to be missing,
 *	in which case the entire namespace will be returned.  The 
 *	<code>keys</code>, <code>values</code>, and <code>bindings</code>
 *	attributes are supported.
 *
 * @version $Id: getHandler.java,v 1.9 2001-04-03 00:04:26 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class getHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Actually do the work. 
    String name = atts.getAttribute("name");
    boolean expand = atts.hasTrueAttribute("expand");

    if (name != null) name = name.trim();
    if (name == null || name.equals("")) {
      aContext.message(-2, "<get> with null name", 0, true);
      return;
    }
    /* === this unaccountably fails
    ActiveNode binding = Index.getBinding(aContext, name);
    if (binding == null) Expand.processNodes(content, aContext, out);
    else if (expand) {
	aContext.subProcess(binding.fromValue(aContext), out).run();
    } else {
	Copy.copyNodes(binding.fromValue(aContext), out);
    }
    */
    ActiveNodeList value = Index.getIndexValue(aContext, name);
    if (value == null) Expand.processNodes(content, aContext, out);
    else if (expand) {
	Expand.processNodes(value, aContext, out);
    } else {
	Copy.copyNodes(value, out);
    }
    
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public getHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED; 		// NORMAL, EMPTY, 0 (check)
  }
}
