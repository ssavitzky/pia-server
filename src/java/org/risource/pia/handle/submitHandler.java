////// submitHandler.java: <submit> Handler implementation
//	$Id: submitHandler.java,v 1.3 2001-04-03 00:05:18 steve Exp $

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


package org.risource.pia.handle;

import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.handle.*;

import org.risource.pia.Agent;
import org.risource.pia.Headers;
import org.risource.pia.BadMimeTypeException;
import org.risource.pia.InputContent;
import org.risource.pia.FormContent;
import org.risource.pia.Content;
import org.risource.pia.MultipartFormContent;
import org.risource.pia.Pia;
import org.risource.pia.site.SiteDoc;
import org.risource.pia.site.SiteMachine;

/**
 * Handler for &lt;submit&gt;....&lt;/&gt;  
 *
 * <p> The intent is for this to be equivalent to the old <submit-forms> tag. 
 *
 * @version $Id: submitHandler.java,v 1.3 2001-04-03 00:05:18 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class submitHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;submit&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    if ("form".equalsIgnoreCase(in.getTagName())) {
      // Handle the case where this is the handler for an actual <form>
      submit(cxt,
	     in.getActive().asElement().editedCopy(atts, content), 
	     atts);
    } else {
      handleContent(cxt, content, atts);
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
  protected void submit(Context cxt, ActiveElement form,
			ActiveAttrList times) {
    if (!Forms.containsTimedSubmission(times)) times = null;
    SiteMachine from = Pia.getSiteMachine();
    TopContext top = cxt.getTopContext();

    if ( form.getTagName().equalsIgnoreCase("form") ){
      String url = form.getAttribute("action");
      String method = form.getAttribute("method");
      String encType = form.getAttribute("encType");
      // Set default encoding type if not specified
      if (encType == null) {
	encType = "application/x-www-form-urlencoded";
      }
      if (method == null) method = "GET";
      if (! url.startsWith("http:") &&
	  ! url.startsWith("/")) {
	url = top.getDocument().getContainer().getPath() + "/" + url;
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

      // Create the HTTP request
      if (times == null) {
	Pia.debug(this,"Making request "+method+" "+url+" "+contentType);
	from.createRequest(method, url, formToEncoding(form,encType),
			   contentType);
      } else { 
	top.message(-2, "Timed requests no longer supported", 0, false);
      }

    } else if (form.hasTrueAttribute("href")) {

      String url = form.getAttribute("href");
      if (times == null) 
	from.createRequest("GET", url, (String) null, null);
      else {
	top.message(-2, "Timed requests no longer supported", 0, false);
      }
    }
  }

  /** Process forms in a ActiveNodeList
   */
  protected void handleContent(Context cxt, ActiveNodeList forms,
			       ActiveAttrList times) {
    if (forms != null){
      int len = forms.getLength();
      for (int i = 0; i < len; i++) {
	ActiveNode n = forms.activeItem(i);
	if (n instanceof ActiveElement) handleNode(cxt, (ActiveElement)n, times);
      }
    }
  }

  /** Process an element.  
   *	If it is a form, submit it.  Otherwise, process its content.
   */
  protected void handleNode(Context cxt, ActiveElement node,
			    ActiveAttrList times) {
    if (node.getTagName().equalsIgnoreCase("form") ) {
      submit(cxt, node, times);
    } else if (node.hasChildNodes()) {
      handleContent(cxt, node.getContent(), times);
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
