////// includeHandler.java: <include> Handler implementation
//	$Id: includeHandler.java,v 1.9 1999-07-08 21:38:40 bill Exp $

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

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

import org.risource.dps.tree.TreeComment;

/**
 * Handler for &lt;include&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id: includeHandler.java,v 1.9 1999-07-08 21:38:40 bill Exp $
 * @author steve@rsv.ricoh.com
 */

public class includeHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;include&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext top  = cxt.getTopContext();
    String     url  = atts.getAttribute("src");
    String  tsname  = atts.getAttribute("tagset");
    String entname  = atts.getAttribute("entity");
    boolean quoted  = atts.hasTrueAttribute("quoted");

    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    TopContext proc = null;
    InputStream stm = null;
    ActiveNode  ent = null;

    // Check the entity.  If it's already defined, we can just use its value

    if (entname != null) {
      entname = entname.trim();
      ent = cxt.getBinding(entname, false);
      if (ent != null) {
	proc = top.subDocument(ent.fromValue(cxt), cxt, out, ts);
	proc.run();
	top.subDocumentEnd();
	return;
      }
    }

    // === at this point we should consider checking for file= and href=
    if (url == null) {
      reportError(in, cxt, "No SRC document specified in include.");
      out.putNode(new TreeComment("No SRC document specified in include")); 
      return;
    }

    // Try to open the stream.  Croak if it fails. 

    try {
      stm = top.readExternalResource(url);
    } catch (IOException e) {
      reportError(in, cxt, e.getMessage());
      if (content != null) Expand.processNodes(content, cxt, out);
      else out.putNode(new TreeComment(e.getMessage()));
      return;
    }

    if (ts == null) {
      reportError(in, cxt, "Cannot open tagset " + tsname);
      if (content != null) Expand.processNodes(content, cxt, out);
      else out.putNode(new TreeComment("Cannot open tagset " + tsname));
      return;
    }

    if (stm == null) {
      if (content != null) Expand.processNodes(content, cxt, out);
      else {
	reportError(in, cxt, "Cannot open " + url);
	out.putNode(new TreeComment("Cannot open " + url));
      }
      return;
    }

    // Create a Parser and TopProcessor to process the stream.  

    Parser p  = ts.createParser();
    p.setReader(new InputStreamReader(stm));
    proc = top.subDocument(p, cxt, out, ts);

    // If we're caching in an entity, tell the parser to save the tree in it.

    if (entname != null) {
      ent = ts.createActiveEntity(entname, null);
      p.setDocument(ent);
    }

    // Crank away.
    if (quoted) proc.copy(); else proc.run();
    top.subDocumentEnd();

    if (ent != null) cxt.setBinding(entname, ent, false);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public includeHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, NORMAL, 0 (check)
  }

  includeHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
