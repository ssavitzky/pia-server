////// includeHandler.java: <include> Handler implementation
//	$Id: includeHandler.java,v 1.16 2001-04-03 00:04:27 steve Exp $

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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import org.risource.site.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

import org.risource.dps.tree.TreeComment;

/**
 * Handler for &lt;include&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id: includeHandler.java,v 1.16 2001-04-03 00:04:27 steve Exp $
 * @author steve@rii.ricoh.com
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

    Tagset      ts  = ("".equals(tsname))? null : top.loadTagset(tsname);
    TopContext proc = null;
    InputStream stm = null;
    Reader 	rdr = null;
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

    // Look for a resource

    Document doc = null;
    Resource res = top.locateResource(url, false);
    if (res != null) {		// We found one.  Open the document
      doc = res.getDocument();
      rdr = doc.documentReader();
    } else {			// Try to open the stream.  Croak if it fails. 
      try {
	if (top.isRemotePath(url)) {
	  stm = top.readExternalResource(url);
	  rdr = new InputStreamReader(stm);
	} else if (top.getDocument() == null) {
	  // fail immediately if the path is not remote, but top has a
	  // document; this means that locateResource tried and failed.
	  File f = top.locateSystemResource(url, false);
	  if (f != null) rdr = new FileReader(f);
	}
      } catch (IOException e) {
	if (content != null) Expand.processNodes(content, cxt, out);
	else {
	  reportError(in, cxt, e.toString());
	  out.putNode(new TreeComment(e.toString()));
	}
	return;
      }
      if (rdr == null) {
	if (content != null) Expand.processNodes(content, cxt, out);
	else {
	  reportError(in, cxt, "Cannot open " + url);
	  out.putNode(new TreeComment("Cannot open " + url));
	}
	return;
      }
    }

    if ("".equals(tsname)) {
      LineNumberReader lnr = new LineNumberReader(rdr);
      String line;
      do {
	try {	
	  if (!lnr.ready()) break;
	  line = lnr.readLine();
	  line = (line == null)? "\n" : line+"\n"; 
	  putText(out, cxt, line);
	} catch (IOException e) { break; }
      } while (line != null);
      try {      
	if (lnr != null) lnr.close();
	if (rdr != null) rdr.close();
	if (stm != null) stm.close();
      } catch (IOException e) {}
      return;
    } else if (ts == null) {
      reportError(in, cxt, "Cannot open tagset " + tsname);
      if (content != null) Expand.processNodes(content, cxt, out);
      else out.putNode(new TreeComment("Cannot open tagset " + tsname));
      try {      
	if (rdr != null) rdr.close();
	if (stm != null) stm.close();
      } catch (IOException e) {}
      return;
    }

    // Create a Parser and TopProcessor to process the stream.  

    Parser p  = ts.createParser();
    p.setReader(rdr);
    proc = top.subDocument(p, cxt, out, ts);
    if (doc != null) proc.setDocument(doc);

    // If we're caching in an entity, tell the parser to save the tree in it.

    if (entname != null) {
      ent = ts.createActiveNode(Node.ENTITY_NODE, entname, (String)null);
      p.setDocument(ent);
    }

    // Crank away.
    if (quoted) proc.copy(); else proc.run();
    top.subDocumentEnd();

    // Clean up.
    try {
      proc.getInput().close();
      if (rdr != null) rdr.close();
      if (stm != null) stm.close();
    } catch (IOException e) {}

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
