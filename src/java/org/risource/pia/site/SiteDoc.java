////// SiteDoc.java: Top Processor for PIA active documents
//	$Id: SiteDoc.java,v 1.1 1999-09-09 21:54:44 steve Exp $

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


package org.risource.pia.site;

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
import org.risource.dps.active.ActiveNode;
import org.risource.dps.handle.Loader;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

import org.risource.pia.Pia;
import org.risource.pia.Agent;
import org.risource.pia.Transaction;
import org.risource.pia.Resolver;

import org.risource.dps.process.TopProcessor;
import org.risource.site.*;

/**
 * A TopProcessor for processing active documents in the PIA.
 *
 * @version $Id: SiteDoc.java,v 1.1 1999-09-09 21:54:44 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.pia
 * @see org.risource.dps.process.TopProcessor
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Context */

public class SiteDoc extends TopProcessor {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected Document 	document	= null;
  protected Agent 	agent 		= null;
  protected Transaction	request 	= null;
  protected Transaction	response 	= null;
  protected Resolver 	resolver 	= null;

  /************************************************************************
  ** PIA information:
  ************************************************************************/

  public Document getDocument() { return document; }

  public String getAgentName() {
    return agent.name();
  }

  /** Convenience function: return the name of the current agent as a 
   *	default if the given name is null.
   */
  public String getAgentName(String name) {
    return (name == null)? agent.name() : name;
  }

  public String getAgentType(String name) {
    if (name == null) return agent.type();
    Agent ia = resolver.agent(name);
    return (ia == null)? null : ia.type();
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

  public Resolver getResolver() {
    return resolver;
  }    

  public Transaction getTransaction() {
    return (response == null)? request : response;
  }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  static {
    // Preload known handler classes.
    Loader.defHandle("submit", new org.risource.dps.handle.submitHandler());
  }

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
    if (agent != null) {
      initializeNamespaceEntities();
      initializeLegacyEntities();
      initializeHookEntities();
    }
  }

  /** Initialize the entities that contain namespaces. */
  public void initializeNamespaceEntities() {

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

  }

  /** Initialize the entities that correspond to entities in the old
   *	(legacy) InterForm processor.  Many files still use them.
   */
  public void initializeLegacyEntities() {
    Transaction transaction = getTransaction();

    if (transaction != null) {
      Transaction req = transaction.requestTran();

      URL url = transaction.requestURL();
      if (url != null) {
	define("url", transaction.requestURL().toString());
	define("urlPath", transaction.requestURL().getFile());
      }
      // form parameters might be either query string or POST data
      if (req != null && req.hasQueryString()){
        define("urlQuery",  req.queryString());
      } else {
	define("urlQuery",  "");
	define("FORM", new Table());
      }
      // if no parameters this is an empty table

      if (transaction.test("agent-request") ||
	   transaction.test("agent-response")) {

	String pname = transaction.getFeatureString("agent");
	Agent  agent = resolver.agent(pname);

	String aname = transaction.getFeatureString("agent-name");
	String apath = transaction.getFeatureString("agent-path");
	String atype = transaction.getFeatureString("agent-type");

	define("TRANS-AGENT", (ActiveNode)agent);
	define("transAgentName", aname);
	define("transAgentType", atype);
	define("transAgentPath", apath);
	define("transAgentPathName", pname);
      } else {
	define("transAgentName", (Object)null);
	define("transAgentType", (Object)null);
	define("transAgentPath", (Object)null);
	define("transAgent", (Object)null);
      }
    }

    Pia pia = Pia.instance();
    define("PIA", pia.properties());

    define("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
    define("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
    define("piaPORT", pia.properties().getProperty(Pia.PIA_PORT));
    define("piaDIR", pia.properties().getProperty(Pia.PIA_ROOT));

    define("usrDIR", pia.properties().getProperty(Pia.USR_ROOT));

  }

  /** Initialize entities that differ for each hook called on a transaction. */
  public void initializeHookEntities() {
    // Set these even if we retrieved an entity table from the 
    // transaction -- the agent is (necessarily) different      

    define("AGENT", (ActiveNode)agent);
    define("agentName", agent.name());
    define("agentType", agent.type());
    define("agentPath", agent.path());
    define("agentPathName", agent.pathName());

    define("agentNames", resolver.agentNames());

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
      // file: is handled by systemFileName
      return new File(org.risource.pia.FileAccess.systemFileName(path));
    } else if (path.startsWith("pia:")) {
      path = path.substring(4);
      // pia: Strip the "pia:" and handle the rest with systemFileName
      return new File(org.risource.pia.FileAccess.systemFileName(path));
    } else if (path.indexOf(":") >= 0) {
      // URL: fail.
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
    return (!path.startsWith("pia:") && super.isRemotePath(path));    
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
    return new SiteDoc(in, cxt, out, ts, document,
		       agent, request, response, resolver); 
  }

  /************************************************************************
  ** Handler Utilities:
  ************************************************************************/

  /** Return the current top context as an SiteDoc object. */
  public static SiteDoc getSiteDoc(Context cxt) {
    TopContext top = cxt.getTopContext();
    return (top instanceof SiteDoc)? (SiteDoc)top : null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SiteDoc() {
    super();
  }

  public SiteDoc(Document doc,
		 Agent a, Transaction req, Transaction resp, Resolver res) {
    super();
    document = doc;
    agent = a;
    request = req;
    response = resp;
    resolver = res;
  }

  public SiteDoc(Input in, Context cxt, Output out, Tagset ts, Document doc,
		 Agent a, Transaction req, Transaction resp, Resolver res) {
    super(in, cxt, out, ts);
    document = doc;
    agent = a;
    request = req;
    response = resp;
    resolver = res;
    initializeEntities();
  }

}
