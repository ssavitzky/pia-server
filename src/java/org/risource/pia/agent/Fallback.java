// Fallback.java
// $Id: Fallback.java,v 1.4 1999-03-22 18:52:40 steve Exp $

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


/**
 * This is the class used for an Agency if we cannot find the Agency's
 *	documents.  It can also be used as an alternative Agency in 
 *	very small appliances.  Use <code>./stringify.pl</code> to convert 
 *	web pages to string constants.
 */

package org.risource.pia.agent;

import java.io.InputStream;
import java.util.Enumeration;

import java.net.URL;

import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.pia.GenericAgent;
import org.risource.pia.Resolver;
import org.risource.pia.Agent;
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.HTTPRequest;
import org.risource.pia.agent.Agency;

public class Fallback extends Agency {
  /**
   * Constructor.
   */
  public Fallback(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Fallback() {
    super();
  }

  public void respond(Transaction request, Resolver res)
       throws org.risource.pia.PiaRuntimeException{
    if (respondWithDocument(request, res)) return;

    /* No document found.  See if this a known fallback form. */

    URL url = request.requestURL();
    if( url == null )
      return;
    String path   = url.getFile();
    String myname = name();
    String mytype = type();
    Agent agnt	  = this;

    if (! path.startsWith("/"+myname+"/")) {
      if (!respondWithDocument(request, res))
	respondNotFound(request, url);
      return;
    }

    path = path.substring(("/"+myname+"/").length());

    String page = null;

    if (path.equals("ROOTindex.if")) page = rootPage;
    else if (path.equals("home.if")) page = homePage;
    else if (path.equals("install_agent.if")) page = installPage;

    if (page == null) {
      respondNotFound(request, url);
    } else {
      InputStream in =  Run.interformString(this, path, page, request, res);
      sendStreamResponse(request, in);
    }
  }

  public String rootPage =
       "<h1>Fallback Agency Root Page</h1>";
  public String homePage =
       "<h1>Empty Home Page</h1>";
  public String installPage =
       "<install-agent>&urlQuery;</install-agent> installed."; 
}





















