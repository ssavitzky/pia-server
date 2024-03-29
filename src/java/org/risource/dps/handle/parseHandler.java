////// parseHandler.java: <parse> Handler implementation
//	$Id: parseHandler.java,v 1.10 2001-04-03 00:04:29 steve Exp $

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

import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Handler for &lt;parse&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id: parseHandler.java,v 1.10 2001-04-03 00:04:29 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class parseHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;parse&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext top  = cxt.getTopContext();
    String  tsname  = atts.getAttribute("tagset");
    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    
    // Get the content as a string in internal form.
    String cstring  = TextUtil.getCharData(content);
    if (cstring == null || cstring.length() == 0) return;
    parse(in, cxt, out, ts, cstring);
  }

  /** Parse a string using a given tagset. */
  public void parse(Input in, Context cxt, Output out, 
  		    Tagset ts, String cstring) {
    // Create a Parser and TopProcessor to process the string.  
    Parser p  = ts.createParser();
    p.setReader(new StringReader(cstring));
    TopContext proc = cxt.getTopContext().subDocument(p, cxt, out, ts);
    // Crank away.
    proc.copy();		// (was run.
    cxt.getTopContext().subDocumentEnd();
  }

  /** This does the parse-time dispatching.  */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "usenet")) 	 return parse_usenet.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public parseHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  parseHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}

class parse_usenet extends parseHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext top  = cxt.getTopContext();
    String  tsname  = atts.getAttribute("tagset");
    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    String cstring  = TextUtil.trimCharData(content);

    cstring = TextUtil.addMarkup(cstring);
    parse(in, cxt, out, ts, cstring);
  }
  public parse_usenet(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new parse_usenet(e); }
}
