////// statusHandler.java: <status> Handler implementation
//	$Id: statusHandler.java,v 1.6 1999-04-07 23:21:26 steve Exp $

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

import java.io.File;
import java.net.URL;

/**
 * Handler for &lt;status&gt;....&lt;/&gt;  
 *
 * <p>	Determine the status of a resource. 
 *
 * @version $Id: statusHandler.java,v 1.6 1999-04-07 23:21:26 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class statusHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;status&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    String entityName = atts.getAttribute("entity");
    String srcURL     = atts.getAttribute("src");
    if (srcURL != null) getStatusForURL(srcURL, cxt, out, atts, content);
    if (entityName != null) getStatusForEntity(entityName, cxt, out,
					       atts, content);
  }

  /** This does the parse-time dispatching.  */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "src")) 	 return status_src.handle(e);
    if (dispatch(e, "entity")) 	 return status_entity.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public statusHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, NORMAL, 0 (check)
  }

  statusHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /************************************************************************
  ** Actually doing the work:
  ************************************************************************/

  /** Get the status for an entity that may be connected to an external
   *	resource.
   */
  void getStatusForEntity(String name, Context cxt, Output out, 
			  ActiveAttrList atts, ActiveNodeList content) {
    String item = atts.getAttribute("item");
    ActiveEntity entity = cxt.getEntityBinding(name, false);
    if (entity == null) {
      if (content != null) Expand.processNodes(content, cxt, out);
    }
    putList(out, Status.getStatusItem(entity, item));
  }

  /** Get the status for a URL that refers to an external resource. */
  void getStatusForURL(String url, Context cxt, Output out,
		       ActiveAttrList atts, ActiveNodeList content) {
    String item = atts.getAttribute("item");
    TopContext top = cxt.getTopContext();
    if (top.isRemotePath(url)) {
      URL remote = top.locateRemoteResource(url, false);
      if (remote != null) {
	putList(out, Status.getStatusItem(remote, item));
      } else {
	if (content != null) Expand.processNodes(content, cxt, out);
      }
    } else {
      File local = top.locateSystemResource(url, false);
      if (local != null) {
	putList(out, Status.getStatusItem(local, item));
      } else {
	if (content != null) Expand.processNodes(content, cxt, out);
      }
    }
  }
}

class status_src extends statusHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, ActiveNodeList content) {
    String srcURL     = atts.getAttribute("src");
    getStatusForURL(srcURL, cxt, out, atts, content);
  }
  public status_src(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new status_src(e); }
}

class status_entity extends statusHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, ActiveNodeList content) {
    String entityName = atts.getAttribute("entity");
    getStatusForEntity(entityName, cxt, out, atts, content);
  }
  public status_entity(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new status_entity(e); }
}

