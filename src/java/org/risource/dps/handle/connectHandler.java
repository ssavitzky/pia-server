////// connectHandler.java: <connect> Handler implementation
//	connectHandler.java,v 1.8 1999/03/01 23:46:07 pgage Exp

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


package crc.dps.handle;

import crc.dom.NodeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import java.io.*;

/**
 * Handler for &lt;connect&gt;....&lt;/&gt;  <p>
 *
 * <p> Essentially all of &lt;connect&gt;'s functionality is contained 
 *	within external entities.  That suggests that the best implementation
 *	is simply to create a suitable entity and return its value.
 *
 * @version connectHandler.java,v 1.8 1999/03/01 23:46:07 pgage Exp
 * @author steve@rsv.ricoh.com
 */

public class connectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;connect&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext  top = cxt.getTopContext();
    String      src = atts.getAttributeString("src");
    String    ename = atts.getAttributeString("entity");
    String   tsname = atts.getAttributeString("tagset");
    String   method = atts.getAttributeString("method");
    String     mode = atts.getAttributeString("mode");
    String   result = atts.getAttributeString("result");

    result = (result == null)? "" : result.toLowerCase();
    boolean  status = result.equals("status");
    boolean    hide = result.equals("none");
    boolean     doc = result.equals("document");

    Tagset       ts = top.loadTagset(tsname);	// correctly handles null
    TopContext proc = null;
    InputStream stm = null;

    ParseTreeExternal ent = null;
    if (ename != null) {
      ent = new ParseTreeExternal(ename, src, null);
      // If the user gave a name, he is expecting it to be bound.
      cxt.setEntityBinding(ename, ent, false);
    } else {
      ent = new ParseTreeExternal(in.getTagName(), src, null);
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
	ent.closeOutput();
      } else {
	// === should report an error if we can't output.
      }
    }

    // Having made the connection, all we really have to do is 
    // return either its value or its status.

    if (status) {
      putList(out, Status.getStatusItem(ent, null));
    } else if (doc) {
      out.putNode(ent.getDocument(cxt));
    } else if (!hide) {
      Input xin = ent.getValueInput(cxt);
      Copy.copyNodes(xin, out);
      ent.closeInput();
    }
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    // if (dispatch(e, "")) 	 return connect_.handle(e);
    return this;
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
