////// propertyBuilder.java: <namespace> Handler implementation
//	$Id: propertyBuilder.java,v 1.2 1999-11-09 01:18:47 steve Exp $

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
import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.namespace.*;
import org.risource.dps.output.ToProperties;

/**
 * Handler for &lt;properties&gt;....&lt;/&gt; 
 *
 * <p>	Expand the content in a context that contains a new local namespace. 
 *	Return the namespace as the result. 
 *
 * @version $Id: propertyBuilder.java,v 1.2 1999-11-09 01:18:47 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class propertyBuilder extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;properties&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    if (atts == null) atts = new TreeAttrList();
    String name = atts.getAttribute("name");
    if (name != null) name = name.trim();
    boolean pass = atts.hasTrueAttribute("pass");
    String tag = in.getTagName();

    Namespace ns = makeNamespace(cxt, tag, name, atts);
    ToProperties loader = new ToProperties(ns, cxt.getTopContext().getTagset());
    loader.setContext(cxt);
    if (pass) loader.setBypass(out);
    Processor p = cxt.subProcess(in, loader, ns);
    p.processChildren();
    ActiveNode result = finish(cxt, ns, atts);
    if (result != null) out.putNode(result);
  }

  /** Construct the namespace.  Specialized subclasses may override this. */
  protected Namespace makeNamespace(Context cxt, String tag, String name,
				    ActiveAttrList atts) {
    return new PropertyTable(tag, name, atts);
  }

  /** Perform any necessary cleanup or initialization.  Return the namespace
   *	as the result, unless the <code>hide</code> attribute is present.
   *	Specialized subclasses may need to override this.
   */
  protected ActiveNode finish(Context cxt, Namespace ns, ActiveAttrList atts) {
    if (atts.hasTrueAttribute("hide") ||
	atts.hasTrueAttribute("pass")) return null;
    return (ActiveNode)ns;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public propertyBuilder() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  propertyBuilder(ActiveElement e) {
    this();
    // customize for element.
  }
}
