////// bindHandler.java: <bind> Handler implementation
//	$Id: bindHandler.java,v 1.1 1999-04-30 23:37:00 steve Exp $

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
 * Handler for &lt;bind&gt;.
 *
 * <p>	Bind a name to the content.  It differs from &lt;set&gt; in that
 *	the content is quoted, and the name does not contain a namespace. 
 *	Bind must be an immediate child of a Namespace, and entities are 
 *	not expanded in its attributes.  
 *
 * @version $Id: bindHandler.java,v 1.1 1999-04-30 23:37:00 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class bindHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This action routine is highly specialized.  We don't even try to expand
   *	the attributes, and the namespace being initialized is assumed to be
   *	local to the current context.
   */
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList attrs = in.getActive().getAttrList();
    ActiveNodeList content = Expand.getContent(in, aContext);
    if (content == null) content = new TreeNodeList();
    // Actually do the work. 
    String name = attrs.getAttribute("name");
    if (name != null) name = name.trim();
    if (name == null || name.equals("")) {
      aContext.message(-2, "Binding null name to "+content, 0, true);
      return;
    }
    aContext.setValueNodes(name, content, true);
  }

   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public bindHandler() {
    /* Expansion control: */
    expandContent = false;	// true		Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED; 		// EMPTY, 	NORMAL, 0 (check)
  }
}
