////// connectHandler.java: <connect> Handler implementation
//	$Id: connectHandler.java,v 1.7 1999-11-06 01:08:11 steve Exp $

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

import java.io.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.TreeExternal;

/**
 * Handler for &lt;connect&gt;....&lt;/&gt;  <p>
 *
 * <p> Essentially all of &lt;connect&gt;'s functionality is contained 
 *	within external entities.  That suggests that the best implementation
 *	is simply to create a suitable entity and return its value.
 *
 * @version $Id: connectHandler.java,v 1.7 1999-11-06 01:08:11 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class connectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;connect&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext  top = cxt.getTopContext();
    String      src = atts.getAttribute("src");
    String    ename = atts.getAttribute("entity");
    String   tsname = atts.getAttribute("tagset");
    String   method = atts.getAttribute("method");
    String     mode = atts.getAttribute("mode");
    String   result = atts.getAttribute("result");

    result = (result == null)? "" : result.toLowerCase();
    boolean  status = result.equals("status");
    boolean    hide = result.equals("none");
    boolean     doc = result.equals("document");

    Tagset       ts = top.loadTagset(tsname);	// correctly handles null
    TopContext proc = null;
    InputStream stm = null;

    TreeExternal ent = null;
    if (ename != null) {
      ename = ename.trim();
      ent = new TreeExternal(ename, src, null);
      // If the user gave a name, he is expecting it to be bound.
      cxt.setBinding(ename, ent, false);
    } else {
      ent = new TreeExternal(in.getTagName(), src, null);
      // not clear whether to bind the entity or not here.
    }

    ent.setMode(mode);
    ent.setMethod(method);
    ent.setRequestContent(content);

    ent.tagsetName = tsname;

    // At this point, we have the entity.  Perform output operations.

    if (ent.isWritable()) {
      Output xout = ent.getValueOutput(cxt);
      if (xout != null) {
	Copy.copyNodes(content, xout);
	xout.close();
      } else {
	// === should report an error if we can't output.
      }
    }

    // Having made the connection, all we really have to do is 
    // return either its value or its status.

    if (status) {
      putList(out, Status.getStatusItem(ent, null));
    } else if (doc) {
      out.putNode(ent.getDocumentAsElement(cxt));
    } else if (!hide) {
      Input xin = ent.getValueInput(cxt);
      Copy.copyNodes(xin, out);
      xin.close();
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public connectHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  connectHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}

/* === probably won't be needing these... 
class connect_direct extends connectHandler {
  public void action(Input in, Context aContext, Output out,
  		     ActiveAttrList atts, NodeList content) {
    // do the work
  }
  public connect_direct(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new connect_direct(e); }
}


class connect_entity extends connectHandler {
  public void action(Input in, Context aContext, Output out,
  		     ActiveAttrList atts, NodeList content) {
    // do the work
  }
  public connect_entity(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new connect_entity(e); }
}
... === */
