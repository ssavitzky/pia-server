////// outputHandler.java: <output> Handler implementation
//	$Id: outputHandler.java,v 1.9 2001-01-11 23:37:22 steve Exp $

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

import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.OutputStreamWriter;

import org.risource.site.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.ToWriter;
import org.risource.dps.tree.TreeComment;

/**
 * Handler for &lt;output&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: outputHandler.java,v 1.9 2001-01-11 23:37:22 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class outputHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;output&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext top  = cxt.getTopContext();
    String     url  = atts.getAttribute("dst");
    boolean append  = atts.hasTrueAttribute("append");
    boolean directory = atts.hasTrueAttribute("directory");

    OutputStream stm = null;
    Writer writer = null;
    Resource res = null;
    Document doc = null;

    // === at this point we should consider checking for file= and href=
    if (url == null || url.length()==0) {
      reportError(in, cxt, "No DST document specified in <output/>.");
      return;
    }

    if (directory) {
      if (! url.endsWith("/")) url += "/";
      res = top.locateResource(url, true);
      if (res != null) return;
      File dir = top.locateSystemResource(url, true);
      if (dir == null) {
	out.putNode(new TreeComment("Cannot create directory " + url));
	top.message(-2, "Cannot create directory " + url, 0, true);
      } else if (! dir.exists()) dir.mkdirs();
      return;
    }

    // Try to open the stream.  Croak if it fails. 

    res = top.locateResource(url, true);
    if (res != null) {		// We found one.  Open the document
      doc = res.getDocument();
      writer = doc.documentWriter(append);
    } else {			// Try to open the stream.  Croak if it fails. 
      try {
	stm = top.writeExternalResource(url, append, true, false);
      } catch (IOException e) {
	out.putNode(new TreeComment(e.getMessage()));
	return;
      }
      if (stm != null) writer = new OutputStreamWriter(stm);
    }
    if (writer == null) {
      out.putNode(new TreeComment("Cannot open " + url));
      top.message(-2, "Cannot write " + url, 0, true);
      return;
    }

    ToWriter output = new ToWriter(writer);
    Copy.copyNodes(content, output);

    try {
      output.close();
      writer.close();
      if (stm != null) stm.close();
    } catch (IOException e) {}

  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public outputHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  outputHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
