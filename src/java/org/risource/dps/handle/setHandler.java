////// setHandler.java: <set> Handler implementation
//	$Id: setHandler.java,v 1.8 1999-11-17 18:33:50 steve Exp $

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
import org.risource.dps.tree.TreeNodeList;

/**
 * Handler for &lt;set&gt;  <p>
 *
 * <p>	This is an approximation to the legacy &gt;set&gt;; it lacks many
 *	of the old extraction modifiers, which have moved to &lt;extract&gt;.
 *
 * @version $Id: setHandler.java,v 1.8 1999-11-17 18:33:50 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class setHandler extends GenericHandler {

  public static final Namespace namespaceContent(ActiveNodeList content) {
    if (content == null || content.getLength() != 1) return null;
    ActiveNode item = content.activeItem(0);
    return (item instanceof Namespace)? (Namespace)item : null;
  }

  public static final String contentName(ActiveNodeList content) {
    if (content == null || content.getLength() != 1) return null;
    ActiveNode item = content.activeItem(0);
    if (item instanceof Namespace) return ((Namespace)item).getName();
    String nm = item.getNodeName();
    return (nm.startsWith("#"))? null : nm;
  }

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Actually do the work. 
    if (content == null) content = new TreeNodeList();
    if (! atts.hasTrueAttribute("notrim")) content = TextUtil.trim(content);

    String name = atts.getAttribute("name");
    //if (name == null) name = contentName(content);
    if (name != null) name = name.trim();
    if (name == null || name.equals("")) {
      aContext.message(-2, "<set> with null name; setting to "+content, 0, true);
      return;
    }

    Index.setIndexValue(aContext, name, content);
  }

   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public setHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL; 		// EMPTY, QUOTED, 0 (check)
  }
}
