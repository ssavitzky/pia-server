////// ServletDoc.java: Top Processor for PIA active documents
//	$Id: ServletDoc.java,v 1.1 2000-03-29 21:31:04 steve Exp $

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


package org.risource.servlet;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

import java.net.URL;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.namespace.*;
import org.w3c.dom.NodeList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeEntity;
import org.risource.dps.active.ActiveNode;
import org.risource.dps.handle.Loader;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

import javax.servlet.*;
import javax.servlet.http.*;

//import org.risource.pia.Pia;
//import org.risource.pia.Agent;
//import org.risource.pia.Resolver;

import org.risource.dps.process.TopProcessor;
import org.risource.site.*;

/**
 * A TopProcessor for processing active documents in the PIA.
 *
 * @version $Id: ServletDoc.java,v 1.1 2000-03-29 21:31:04 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.pia
 * @see org.risource.dps.process.TopProcessor
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Context */

public class ServletDoc extends TopProcessor {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected /*Agent*/Object	agent 		= null;
  protected HttpServletRequest	request 	= null;
  protected HttpServletResponse	response 	= null;
  protected HttpServlet		servlet		= null;

  /************************************************************************
  ** PIA information:
  ************************************************************************/

  /* ===
  public String getAgentName() {
    return agent.name();
  }

  public String getAgentName(String name) {
    return (name == null)? agent.name() : name;
  }

  public Agent getAgent(String name) {
    return (name == null)? agent : resolver.agent(name);
  }    

  public Agent getAgent() {
    return agent;
  }    

  public void setAgent(Agent a) {
    agent = a;
  }

  === */

  /************************************************************************
  ** Setup:
  ************************************************************************/

  /** Initialize the various entities.  
   *	Done in four separate methods (counting super), for easy customization.
   *
   * @see org.risource.dps.process.TopProcessor#initializeEntities
   * @see #initializeNamespaceEntities
   * @see #initializeLegacyEntities
   * @see #initializeHookEntities
   */
  public void initializeEntities() {
    if (entities == null) super.initializeEntities();
    initializeNamespaceEntities();
    initializeLegacyEntities();
    if (agent != null) {
      initializeHookEntities();
    }
  }

  /** Initialize the entities that contain namespaces. */
  public void initializeNamespaceEntities() {

    // These are done by super.initializeEntities()
    // define("LOC", getLocConfig());
    // define("PROPS", getDocConfig());
    // define("SITE", getRootConfig());
    // define("ENV", System.getProperties());

    /* === 
    define("PIA", Pia.instance().properties());
    define("AGENTS", Pia.resolver().getAgentTable());

    Transaction transaction = getTransaction();
    if (transaction != null) {
      define("TRANS", "TRANSACTION", transaction);
      //define("HEADERS",transaction.getHeaders());

      Transaction req = transaction.requestTran();
      define("REQ", "TRANSACTION", req);
      if (req != null && req.hasQueryString()){
	define("FORM", "FORM", req.getParameters());
      }
    }
    === */
  }

  /** Initialize the entities that correspond to entities in the old
   *	(legacy) InterForm processor.  Many files still use them.
   */
  public void initializeLegacyEntities() {
    /* ===
    Transaction transaction = getTransaction();

    if (transaction != null) {
      Transaction req = transaction.requestTran();

      URL url = transaction.requestURL();
      if (url != null) {
	define("url", transaction.requestURL().toString());
	String path = transaction.requestURL().getFile();
	define("urlPath", path);
	if (agent == null) agent = resolver.agentFromPath(path);
      }
      // form parameters might be either query string or POST data
      if (req != null && req.hasQueryString()){
        define("urlQuery",  req.queryString());
      } else {
	define("urlQuery",  "");
	define("FORM", new Table());
      }
      // if no parameters this is an empty table
    }

    Pia pia = Pia.instance();
    define("agentNames", resolver.agentNames());

    === */
  }

  /** Initialize entities that differ for each hook called on a transaction. */
  public void initializeHookEntities() {
    // Set these even if we retrieved an entity table from the 
    // transaction -- the agent is (necessarily) different      
    if (agent == null) return;

    /** ===
    if (agent != null) {
      define("AGENT", (Namespace)((ActiveNode)agent).getAttrList());
      define("agentNode", new TreeEntity("agentNode",
					 new TreeNodeList((ActiveNode)agent)));
    }
    define("agentName", agent.name());
    define("agentHome", agent.getHome().getPath());
    === */

    define("entityNames", "");
    define("entityNames", entities.getNames());
  }

  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** Locate a resource accessible as a file.
   *  The following prefixes are recognized:
   *  <dl>
   *	<dt> <code>file:</code>
   *	<dd> forces the remainder of the path to be taken as a native
   *		path, and used as-is.
   *	<dt> <code>pia:</code>
   *	<dd> Interprets the path using 
   *		<a href="org.risource.pia.FileAccess#systemFileName">
   *		org.risource.pia.FileAccess.systemFileName</a>
   *
   *	<dt> <code>/</code>
   *	<dd> Interprets the path like a URL passed to the PIA.
   *	<dt> other
   *	<dd> paths are relative to the current agent.
   * </dl>
   * @see org.risource.pia.FileAccess#systemFileName
   */
  public File locateSystemResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      // file: is handled by systemFileName === locateSystemResource bogus
      return new File(stripPrefix(path));
    } else if (path.startsWith("pia:")) {
      path = stripPrefix(path);
      // pia: Strip the "pia:" and look relative to PIA_HOME
      if (!path.startsWith("/")) path = "/" + path;
      //path = Pia.instance().getProperty("home") + path;
      //return new File(path);
      return null;
    } else if (path.startsWith("r:") || path.startsWith("root:")) {
      path = stripPrefix(path);
      if (!path.startsWith("/")) path = "/" + path;
      // get root off the Site.
      //path = Pia.instance().getProperty("root") + path;
      return new File(path);
    } else if (path.startsWith("v:") || path.startsWith("vroot:")) {
      path = stripPrefix(path);
      if (!path.startsWith("/")) path = "/" + path;
      //path = Pia.instance().getProperty("vroot") + path;
      return new File(path);
    } else if (path.indexOf(":") >= 0) {
      // URL: fail.
      return null;
    } else if (document == null) {
      return null;
    } else {
      Resource r = document.locate(path, forWriting, null);
      if (r == null) return null;
      Document d = r.getDocument();
      if (d == null) return null;
      else return d.documentFile();
    }
  }

  /** Determine whether a resource name is a remote path. 
   *	In our case, paths starting with <code>pia:</code> as well as  
   *	<code>file:</code> are considered system paths.
   *
   * @see org.risource.pia.FileAccess#systemFileName
   */
  public boolean isRemotePath(String path) {
    return (!path.startsWith("pia:")
	    && !path.startsWith("r:") && !path.startsWith("v:")
	    && !path.startsWith("root:") && !path.startsWith("vroot:")
	    && super.isRemotePath(path));    
  }

  /** Determine whether a resource name is special. 
   *	In our case, paths starting with <code>pia::</code> require
   *	special handling, and should be sent to the resolver.
   */
  protected boolean isSpecialPath(String path) {
    return (path.startsWith("pia::"));    
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  protected InputStream readSpecialResource(String path) {
    // === reading from the pia is hard, because machines go the wrong way.
    return null;
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  public OutputStream writeSpecialResource(String path, boolean append,
					    boolean createIfAbsent,
					   boolean doNotOverwrite) {
    return null;
  }

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param ts the tagset.  If null, the current tagset is used.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts) {
    if (ts == null) ts = tagset;
    return new ServletDoc(in, cxt, out, ts, document,
			  servlet, request, response); 
  }

  /************************************************************************
  ** Handler Utilities:
  ************************************************************************/

  /** Return the current top context as an ServletDoc object. */
  public static ServletDoc getServletDoc(Context cxt) {
    TopContext top = cxt.getTopContext();
    return (top instanceof ServletDoc)? (ServletDoc)top : null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ServletDoc() {
    super();
  }

  public ServletDoc(Resource doc) {
    super(doc);
  }

  public ServletDoc(Resource doc, HttpServlet srv,
		    HttpServletRequest req, HttpServletResponse resp) {
    super(doc);
    servlet = srv;
    request = req;
    response = resp;
  }

  public ServletDoc(Input in, Context cxt, Output out, Tagset ts, 
		    Resource doc, HttpServlet srv, 
		    HttpServletRequest req, HttpServletResponse resp) {
    super(in, cxt, out, ts, doc);
    servlet = srv;
    request = req;
    response = resp;
    initializeEntities(); // this may not be necessary
  }

}

