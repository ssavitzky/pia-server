////// nodeBuilder.java: handler for tags that build nodes.
//	$Id: nodeBuilder.java,v 1.7 1999-11-09 01:19:59 steve Exp $

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
 * @version $Id: nodeBuilder.java,v 1.7 1999-11-09 01:19:59 steve Exp $
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
    String type = atts.getAttribute("type");
    if (type != null) type = type.trim();
    if (name != null) name = name.trim();
    if (name == null && type == null) {
      reportError(in, cxt, "No 'name' or 'type' attribute");
      return;
    }
    if (name.startsWith("#")) {
      if (type != null) {
	reportError(in, cxt,
		    "Node types specified in both 'name' or 'type' attributes");
	return;
      }
      type = name.substring(1);
      name = null;
    }

    ToNodeList content = new ToNodeList(cxt.getTopContext().getTagset());

    TreeAttrList ns = new TreeAttrList();
    ToNamespace loader = new ToNamespace(ns, cxt.getTopContext().getTagset());
    loader.setContext(cxt);
    loader.setBypass(content);
    Processor p = cxt.subProcess(in, loader, ns);
    // === need special recognition for <name> ===
    p.processChildren();
    ActiveNode result = finish(cxt, name, type, ns, content.getList(), atts);
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
  protected ActiveNode finish(Context cxt, String tagname, String type,
			      TreeAttrList ns, ActiveNodeList content,
			      ActiveAttrList atts) {
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
      tagname = TextUtil.getCharData(ns.getValueNodes(cxt, ".")).trim();
      ns.remove(".");
    }

    // check for tagname starting with #
    if (type != null && ! type.equalsIgnoreCase("element")) {
      // === need to use the tagset for this...
      if (type.equalsIgnoreCase("comment")) {
	return new TreeComment(TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("cdata")) {
	return new TreeCDATA(TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("text")) {
	return new TreeText(TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("string")) {
	return new TreeCharData(NodeType.STRING, TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("pi")) {
	return ts.createActiveNode(NodeType.PI, tagname,
				   TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("doctype")) {
	return ts.createActiveNode(NodeType.DOCTYPE, tagname,
				   TextUtil.getCharData(content));
      } else if (type.equalsIgnoreCase("declaration")) {
	return  ts.createActiveNode(NodeType.DECLARATION, tagname, content);
      } else {
	return new TreeComment("** unrecognized type " + type);
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

  /** We use children to distinguish <code>make</code> from <code>do</code>,
   *	so we need a unique instance every time. 
   */
  public boolean uniquify() { return true; }
}
