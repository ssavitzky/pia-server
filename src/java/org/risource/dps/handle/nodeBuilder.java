////// nodeBuilder.java: handler for tags that build nodes.
//	$Id: nodeBuilder.java,v 1.3 1999-07-14 20:20:25 steve Exp $

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
import org.risource.dps.tree.*;
import org.risource.dps.namespace.*;
import org.risource.dps.output.ToNamespace;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.input.FromParseTree;

/**
 * Handler for &lt;element&gt;....&lt;/&gt; 
 *
 * <p>	Constructs and then processes a new node, typically an Element. 
 *
 * @version $Id: nodeBuilder.java,v 1.3 1999-07-14 20:20:25 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class nodeBuilder extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;namespace&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    if (atts == null) atts = new TreeAttrList();
    String name = atts.getAttribute("name");
    if (name != null) name = name.trim();
    if (name == null) {
      reportError(in, cxt, "No 'name' attribute");
      return;
    }
    ToNodeList content = new ToNodeList(cxt.getTopContext().getTagset());

    TreeAttrList ns = new TreeAttrList();
    ToNamespace loader = new ToNamespace(ns, cxt.getTopContext().getTagset());
    loader.setContext(cxt);
    loader.setBypass(content);
    Processor p = cxt.subProcess(in, loader, ns);
    // === need special recognition for <name> ===
    p.processChildren();
    ActiveNode result = finish(cxt, name, ns, content.getList(), atts);
    if (result == null) return;
    if (this.hasChildNodes()) {
      p = cxt.subProcess(new FromParseTree(result), out, null);
      p.processNode();
    } else {
      out.putNode(result);
    }
  }

  /** Actually construct the new node. 
   */
  protected ActiveNode finish(Context cxt, String tagname, TreeAttrList ns,
			      ActiveNodeList content, ActiveAttrList atts) {
    Tagset ts = cxt.getTopContext().getTagset();

    // If there's a binding for "...", that's the content.
    if (ns.getBinding("...") != null) {
      content = ns.getValueNodes(cxt, "...");
      ns.remove("...");
    } else {
      content = new TreeNodeList(TextUtil.trimListItems(content));
    }

    // If there's a binding for ".", that's the name.
    if (ns.getBinding(".") != null) {
      tagname = ns.getValueNodes(cxt, ".").toString().trim();
      ns.remove(".");
    }

    // check for tagname starting with #
    if (tagname.startsWith("#")) {
      // === need to use the tagset for this...
      if (tagname.equalsIgnoreCase("#comment")) {
	return new TreeComment(content.toString());
      } else if (tagname.equalsIgnoreCase("#cdata")) {
	return null; // new TreeCData(content.toString());
      } else if (tagname.equalsIgnoreCase("#text")) {
	return new TreeText(content.toString());
      } else if (tagname.equalsIgnoreCase("#string")) {
	return new TreeCharData(NodeType.STRING, content.toString());
      } else {
	return new TreeComment("** unrecognized type " + tagname);
      }
    } else {
      ActiveElement e = ts.createActiveElement(tagname, ns,
					       content.getLength()==0);
      Copy.appendNodes(content, e);
      return e;
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public nodeBuilder() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  nodeBuilder(ActiveElement e) {
    this();
    // customize for element.
  }
}
