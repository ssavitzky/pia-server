////// submitHandler.java: <submit> Handler implementation
//	$Id: submitHandler.java,v 1.3 1999-03-12 19:26:35 steve Exp $

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

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.process.ActiveDoc;

import org.risource.pia.Agent;
import org.risource.pia.agent.AgentMachine;
import org.risource.pia.Headers;
import org.risource.pia.BadMimeTypeException;
import org.risource.pia.FileAccess;
import org.risource.pia.InputContent;
import org.risource.pia.FormContent;
import org.risource.pia.Content;
import org.risource.pia.MultipartFormContent;
import org.risource.pia.Pia;

/**
 * Handler for &lt;submit&gt;....&lt;/&gt;  
 *
 * <p> The intent is for this to be equivalent to the old <submit-forms> tag. 
 *
 * @version $Id: submitHandler.java,v 1.3 1999-03-12 19:26:35 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class submitHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;submit&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    ActiveDoc top = (ActiveDoc)cxt.getTopContext();

    if ("form".equalsIgnoreCase(in.getTagName())) {
      // Handle the case where this is the handler for an actual <form>
      submit(top.getAgent(),
	     in.getActive().asElement().editedCopy(atts, content), 
	     atts);
    } else {
      handleContent(top.getAgent(), content, atts);
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public submitHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  submitHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Submit a form or request using an agent.
   * 	@param a	the agent submitting the form.
   *	@param form	the form to be submitted
   *	@param times	the timed-submission attributes.
   */
  protected void submit(Agent a, ActiveElement form,
			ActiveAttrList times) {
    if (!Forms.containsTimedSubmission(times)) times = null;

    if ( form.getTagName().equalsIgnoreCase("form") ){
      String url = form.getAttributeString("action");
      String method = form.getAttributeString("method");
      String encType = form.getAttributeString("encType");
      // Set default encoding type if not specified
      if (encType == null) {
	encType = "application/x-www-form-urlencoded";
      }

      // Initialize content type for submission
      String contentType;
      if (encType.equals("application/x-www-form-urlencoded")) {
	contentType = "application/x-www-form-urlencoded";
      } else if (encType.equals("multipart/form-data")) {
	contentType = "multipart/form-data; boundary="+Forms.multipartBoundary;
      } else {
	Pia.debug(this, "Unknown encoding specified in form: "+encType);
	return;
      }

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);

      // Create the HTTP request
      if (times == null) {
	Pia.debug(this,"Making request "+method+" "+url+" "+contentType);
	a.createRequest(m,method, url, formToEncoding(form,encType), contentType);
      } else { 
	a.createTimedRequest(method, url,
			     formToEncoding(form,encType).toString(),
			     contentType, Forms.getTimes(times));
      }

    } else if (form.hasTrueAttribute("href")) {

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);

      // Uncomment line below for debugging, it
      // will output to stdout everything returned
      // from the server which hosts the form
      // m.setCallback(new EchoCallback());

      String url = form.getAttributeString("href");
      if (times == null) 
	a.createRequest(m,"GET", url, (String) null, null);
      else {
	a.createTimedRequest("GET", url, (String) null, null,
			     Forms.getTimes(times));
      }
    }
  }

  /** Process forms in a NodeList
   */
  protected void handleContent(Agent a, NodeList forms,
			       ActiveAttrList times) {
    if (forms != null){
      NodeEnumerator nodes = forms.getEnumerator();
      for (Node n = nodes.getFirst(); n != null; n = nodes.getNext()) {
	if (n instanceof ActiveElement) handleNode(a, (ActiveElement)n, times);
      }
    }
  }

  /** Process an element.  
   *	If it is a form, submit it.  Otherwise, process its content.
   */
  protected void handleNode(Agent a, ActiveElement node,
			    ActiveAttrList times) {
    if (node.getTagName().equalsIgnoreCase("form") ) {
      submit(a, node, times);
    } else if (node.hasChildren()) {
      handleContent(a, node.getChildren(), times);
    }
  }

  /** Convert a form to either a query string
   *  or a multipart form encoding
   */
  protected static InputContent formToEncoding(ActiveElement it,
					       String encType) {
    if (encType.equals("application/x-www-form-urlencoded")) {
      return new FormContent(Forms.formToQuery(it));
    } else if (encType.equals("multipart/form-data")) {
      return new MultipartFormContent(Forms.formToMultipart(it));
    } else {
      // WHAT IS CORRECT ACTION HERE?
      //=== message Pia.debug(this, "Unknown encoding specified in form");
      return null;
    }
  }

}
