// Generic.java
// $Id: Generic.java,v 1.4 1999-11-04 22:46:59 steve Exp $

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

package org.risource.pia.agent;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import org.risource.Version;

import org.risource.pia.*;
import org.risource.pia.site.SiteDoc;

import org.risource.content.ByteStreamContent;
import org.risource.content.text.ProcessedContent;

import org.risource.ds.Features;
import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Criteria;
import org.risource.ds.Tabular;
import org.risource.ds.Registered;

import org.risource.dps.*;
import org.risource.dps.tagset.Loader;
import org.risource.dps.active.*;
import org.risource.dps.util.Log;
import org.risource.dps.namespace.*;
import org.risource.dps.output.DiscardOutput;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.tree.TreeElement;
import org.risource.dps.tree.TreeNodeList;

import org.risource.site.*;

import org.risource.util.NullOutputStream;
import org.risource.util.NameUtils;
import org.risource.util.Utilities;

import java.util.Enumeration;
import java.util.Properties;
import java.io.Serializable;

import org.w3c.www.http.HTTP;

/** The minimum concrete implementation of the Agent interface.  A
 *	Generic is used if no specialized class can be loaded for
 *	an agent; it also serves as the base class for all known Agent
 *	implementations.
 *
 * <p> Since an Agent needs only one action, this is initialized from its
 *	content.  Text is stripped out, and any <code>&lt;initialize&gt</code>
 *	sub-elements are extracted and run immediately.
 *
 * @see org.risource.pia.Agent
 */
public class Generic extends TreeElement
  implements Agent, Registered, Serializable {
  
  /** Class name of Generic. */
  public static String genericClassName = "org.risource.pia.agent.Generic";

  /** 
   * Tagset to use for expanding hooks
   */
  protected transient Tagset tagset = null;

  /**
   * Attribute index - name of this agent
   */
  protected String agentName;

  /**
   * Attribute index - home of this agent
   */
  protected Subsite agentHome;

  /**
   * A list of match criteria.  These are matched against the
   * Features of every Transaction to see if this Agent's actOn method
   * should be called.
   */
  protected Criteria criteria;

  /** An Authenticator for paths under this agent. */
  protected Authenticator authPolicy = null;

  /**
   * Attribute index - virtual Machine to which local requests are directed.
   */
  protected AgentMachine virtualMachine;

  /**
   * Action Hook.  A pre-parsed piece of active document code that is run when 
   *	a transaction is matched by the agent.  
   */
  protected ActiveNodeList actionHook = null;	

  /** 
   * Initialization Hook.
   */
  protected ActiveNodeList initHook = null;

  /************************************************************************
  ** Access to fields:
  ************************************************************************/

  /**
   * @return name of agent
   */
  public String name() { return agentName; }

  /**
   * set name of agent
   */
  public void setName(String name) { agentName = name; }

  /**
   * @return home Resource of agent
   */
  public Subsite getHome() { return agentHome; }

  /**
   * @return home path of agent
   */
  public String getHomePath() { return agentHome.getPath(); }

  /**
   * set home Resource of agent
   */
  public void setHome(Subsite home) { agentHome = home; }

  /************************************************************************
  ** Matching Features:
  ************************************************************************/

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria() {
    return criteria;
  }

  /************************************************************************
  ** Tagsets:
  ************************************************************************/

  /** The name of this agent's tagset. */
  protected String tagsetName = null;

  public Tagset getTagset() {
    if (tagset == null) {
      if (tagsetName == null) {
	tagsetName = getAttribute("tagset");
	if (tagsetName == null || tagsetName.length() == 0)
	  tagsetName = "pia-xhtml";
      }
      Tagset ts = agentHome.loadTagset(tagsetName);
      if (ts == null) {
	// sendErrorResponse(trans, 500, "cannot load tagset " +tsn);
	return null;
      }
      tagset = ts;
    }
    return tagset;
  }


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Flag that says whether initialization has been done. */
  protected boolean initialized = false;

  protected void runInitHook() {
    if (initHook != null) {
      runDPSHook(initHook, null, Pia.resolver());
    }
  }

  /** Basic initialization: locate components. */
  protected void basicInitialization() {
    String s = getAttribute("name");
    if (s != null) agentName = s;
    s = getAttribute("criteria");
    if (s != null) criteria = new Criteria(s);

    // Add a few necessary things to the attributes:
    setAttribute("path", agentHome.getPath()); 

    // Look for an "authenticate" attribute. 
    s = getAttribute("authenticate");
    if (s != null) {
      authPolicy = new Authenticator("Basic", s);
    }

    // Look for <intialize> and <action> elements.
    // <action> can be omitted, though it may be dangerous to do so.
    ActiveNodeList content = null;
    for (ActiveNode child = getFirstActive();
	 child != null;
	 child = child.getNextActive()) {
      if (child.getNodeType() == NodeType.ELEMENT) {
	if (child.getNodeName().equals("initialize")) {
	  initHook = child.getContent();
	} else if (child.getNodeName().equals("action")) {
	  actionHook = child.getContent();
	} else {
	  if (content == null) content = new TreeNodeList();
	  content.append(child);
	}
      }
    }
    if (actionHook == null) actionHook = content;
  }

  /** Initialization.  
   */
  public void initialize(){
    if (initialized) return;
    initialized = true;

    basicInitialization();
    if (initHook != null) runInitHook();

    // Must be done after setting initialized=true
    register();

    if (Pia.verbose()) dumpDebugInformation();
  }

  /************************************************************************
  ** Debugging:
  ************************************************************************/

  protected void dumpDebugInformation() {
    System.err.println(debugInformation() + " Initialized");
  }

  protected String debugInformation() {
    String s = Pia.debug()? toString() : Log.node(this);
    return s;
  }


  /************************************************************************
  ** Registration and Unregistration:
  ************************************************************************/

  /** Register the Agent with the Resolver. */
  public void register() {
    Pia.resolver().registerAgent( this );
  }

  /** Remove the Agent from the Resolver's registry */
  public void unregister() {
    Pia.resolver().unRegisterAgent( this );
  }


  /************************************************************************
  **  Content interactions: 
  ************************************************************************/

 /** 
  * agents can ask content objects to notify them of state changes.
  * this is the callback method for that notification.
  * AgentMachines also use this as the default callback for sending a response.
  *
  * @see org.risource.pia.Content 
  * @see org.risource.pia.agent.AgentMachine
  */
  public void updateContent(Content c, String state, Object arg)
  {
    Pia.debug(this,"updating content object" + c.toString()
	      + " state "+ state, " argument "+arg);
    if(arg instanceof AgentMachine){
      // write content to a null stream for side effects
       try{
	 c.writeTo( new NullOutputStream());
       } catch(Exception e){}
      return;
    }
    // do something based on state of content
  }
  


  /************************************************************************
  ** Machine: 
  ************************************************************************/

  /**
   * Each Agent is associated with a virtual machine which is an
   * interface for actually getting and sending transactions.  Posts
   * explicitly to an agent get sent to the agent's machine (then to
   * the agent's <code>respond</code> method). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine(){
    Pia.debug(this, "Getting agent machine" );
    if( virtualMachine==null ){
      virtualMachine = new AgentMachine( (Agent)this );
      Pia.debug(this, "Creating virtual agent machine" );
    }
    return virtualMachine;
  }

  /**
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine){
    AgentMachine machine = null;
 
    if( virtualMachine==null && vmachine==null ){
      machine = new AgentMachine( (Agent) this );
    }
    if( machine!=null )
      virtualMachine = machine;
  }

  /************************************************************************
  ** Actions and Hooks:
  ************************************************************************/

  /**
   * Act on a transaction that we have matched.  
   */
  public void actOn(Transaction ts, Resolver res){
    if (actionHook == null) return;
    Pia.debug(this, name()+".actOnHook", "= DPS:"+actionHook.toString());
    runDPSHook((ActiveNodeList)actionHook, ts, res); 
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   *
   * <p> === DPS hooks are untested and probably won't work without
   *	     additional primitives. 
   */
  public boolean handle(Transaction ts, Resolver res) {
    if (actionHook == null)  return false;
    Pia.debug(this, name()+".handleHook", "= DPS: "+actionHook.toString());
    runDPSHook(actionHook, ts, res); 
    return true;
  }

  public boolean act() {
    if (actionHook == null) return false;
    runDPSHook(null, null, Pia.resolver());
    return true;
  }

  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public void runDPSHook (ActiveNodeList hook,
			  Transaction trans, Resolver res ) {
    if (hook == null || hook.getLength() == 0) return;
    SiteDoc proc = makeDPSProcessor(trans, res);
    proc.setInput(new FromNodeList(hook));
    proc.setOutput(new DiscardOutput());
    proc.run();
  }

  protected SiteDoc makeDPSProcessor(Transaction trans, Resolver res) {
    Transaction req  = (trans == null)? null : trans.requestTran();
    Transaction resp = (trans != null && trans.isResponse())? trans : null;

    // === This should be a Hook instead of a SiteDoc ===
    // === it only works with the xhtml tagset
    SiteDoc proc = new SiteDoc(agentHome, this, req, resp, res);
    proc.setVerbosity((Pia.verbose()? 1 : 0) + (Pia.debug()? 2 : 0));
    proc.setTagset(getTagset());
    return proc;
  }

  /************************************************************************
  ** Authentication:
  ************************************************************************/

  /**
   * authenticate the origin of a request
   * @return false if cannot authenticate
   */
  public boolean authenticateRequest(Transaction request, Resource resource){
    if (authPolicy == null) return true;
    return authPolicy.authenticateRequest(request, resource.getPath(), this);
  }

  public Authenticator getAuthenticator() { return authPolicy; }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /* name and state should be set latter */
  public Generic(){
    this(null, null);
  }

  public Generic(String name, Document state){
    super("AGENT");
    String cname = getClass().getName();
    if (!cname.equals(genericClassName)) setAttribute("class", cname);

    if (name != null) this.setName( name );
  }

}
