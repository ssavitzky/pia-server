// Resolver.java
// $Id: Resolver.java,v 1.10 1999-10-05 15:08:42 steve Exp $

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


package org.risource.pia;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Date;

import java.net.URL;

import org.risource.pia.Agent;
import org.risource.pia.Transaction;
import org.risource.util.Utilities;
import org.risource.site.Resource;

import org.risource.ds.Queue;
import org.risource.ds.Table;
import org.risource.ds.Tabular;
import org.risource.ds.List;





/**
 * A Resolver (i.e. an instance of PIA.Resolver) acts like a stack of
 * Transaction objects.  A resolver also has a list of agents which have
 * registered their interest.  For each transaction in the list, the
 * resolver attempts to match its features against every agent, and each
 * agent that matches  is given the chance to actOn the transaction.  In
 * general agent.actOn will either push some new transactions, register
 * a handler agent, or both.<p>
 *
 * After each agent has had its chance to act_on the transaction, any
 * registered handlers are called, after which the transaction is
 * discarded. <p>
 *
 * Each time around its loop, the Resolver also checks the time.
 * Approximately once per minute, it polls each Agent in its registry
 * to see whether it has any Crontab entries to run. <p>
 */
public class Resolver extends Thread {
  /**
   * Attribute index - a collection of agents by name.  Note that not all
   *	agents have a name; this collection only contains agents with 
   *	homes accessible as ``<code>~<em>name</em></code>''.
   */
  protected  Table agentsByName = new Table();

  /**
   * Attribute index - a collection of agents by the pathname of their 
   *	home resource.
   */
  protected  Table agentsByPathName = new Table();

  /** 
   * The order in which agents get to act on a transaction
   * is determined by their order of start-up.  This is not ideal;
   * we should be able to assign a priority to each agent...
   * Just a temporary fix for now.
   */
  protected  List agentsInOrder = new List();

  /** A table of agents listed by the prefix of the pathname to which they
   *	apply.  At the moment the only use of this is for authenticators. 
   */
  protected Table agentsByPrefix = new Table();

  /**
   * Attribute index - whether to stop running
   */
  protected boolean finish = false;

  /**
   * Time of last Crontab check. 
   */
  protected long cronTime = 0;

  /** 
   * Interval between Crontab checks in milliseconds.  Must be less than one
   *	minute to avoid missing a ``once-per-minute'' entry.
   */
  protected static long cronDELAY = 15000;

  /** Constant: one minute. */
  public static final long MINUTE = 60000;

  /** The Crontab. */
  protected Crontab crontab = new Crontab();
  
  /************************************************************************
  ** Transaction Queue:
  ************************************************************************/

  /**
   * Attribute index - transaction queue.
   */
  protected Queue transactions;

  /**
   * shift -- remove and return the transaction at front of list .
   * If there is no transaction returns null.
   */
  public final synchronized Transaction shift(){
    return (Transaction) transactions.shift();
  }

  /**
   * unshift -- put a transaction to the front of the list. 
   * returns the number of elements
   */ 
  public final synchronized int unshift( Transaction obj ){
    Pia.debug(this, "unshift()");
    int i = transactions.unshift( obj );
    notifyAll();
    return i;
  }

  /**
   * push -- push a transaction onto the end of the list. 
   * returns the number of elements
   */  
  public final synchronized int push( Transaction obj ){
    Pia.debug(this, "push()");
    int i = transactions.push( obj );
    notifyAll();
    //    Pia.debug(this, "current size" + size());
    return i;
  }

  /**
   * pop -- removes a transaction from the back of the queue and returns it. 
   * returns the number of elements
   */ 
  public final synchronized Transaction pop(){
    return (Transaction) transactions.pop();
  }

  /**
   * Number of transactions in queue
   */
  public final synchronized int size(){
    return transactions.size();
  }

  /************************************************************************
  ** Agents:
  ************************************************************************/

  /**
   * Register an agent with the resolver. 
   *
   *<p> The agent is assumed to have either match criteria or a set of crontab
   *	timing attributes.  An agent with <code>criteria="false"</code>, which
   *	of course cannot match anything, can be used to register its home
   *	resource as <code>~<em>name</em></code>.
   */
  public void registerAgent( Agent agent ){
    if ( agent != null && agent.name() != null ){
      agentsByName.put(agent.name(), agent);
      agentsByPathName.put(agent.getHomePath(), agent);
    }
    agent.initialize();

    boolean registered = false;

    if (agent.criteria() != null) {
      if (!agent.getAttribute("criteria").equalsIgnoreCase("false"))
	agentsInOrder.push(agent);
      registered = true;
    } 
    if (agent.hasTrueAttribute("authenticate")) {
      registerAuthenticator(agent);
      registered = true;
    } 
    Table timings = CrontabEntry.getTimings(agent);
    if (timings != null) {
      crontab.makeEntry(agent, timings);
      registered = true;
    }

    if (!registered) {
      Pia.warningMsg("Agent with unknown function " + agent.startString());
    }
  }

  /**
   * Unregister an agent by name or pathname, removing and returning it.
   *	A name that contains a "/" is considered to be a pathname, and
   *	is forced to start with a "/".
   * @return null if deletion is not successful.
   */
  public Agent unRegisterAgent( String name ){
    if (name.indexOf("/") > 0) name = "/" + name;
    return unRegisterAgent(agent(name));
  } 

  /**
   * Unregister an agent by reference, removing and returning it.
   * @return the unregistered agent.
   */
  public Agent unRegisterAgent( Agent agent ){
    if (agent == null) return agent;
    if (agent.name() != null) agentsByName.remove(agent.name());
    agentsByPathName.remove(agent.getHomePath());
    agentsByPrefix.remove(agent.getHomePath());
    agentsInOrder.remove(agent);
    return agent;
  } 

  public void registerAuthenticator(Agent agent) {
    agentsByPrefix.put(agent.getHomePath(), agent);
  }

  public Agent getAuthenticatorForResource(Resource resource) {
    if (! resource.isContainer()) resource = resource.getContainer();
    for ( ; resource != null; resource = resource.getContainer()) {
      Agent a = (Agent)agentsByPrefix.at(resource.getPath());
      if (a != null) return a;
    }
    return null;
  }

  /**
   * agents that are able to match transactions.
   * @return Enumeration of all agents with criteria.
   */
  public Enumeration matchingAgents(){
    return agentsInOrder.elements();
  }

  /** 
   * Table of <em>all</em> agents.
   */
  public Tabular getAgentTable() {
    return agentsByPathName;
  }

  /**
   * Agents' names 
   * @return agents' names
   */
  public Enumeration agentNames(){
    return agentsByName.keys();
  }

  /**
   * Agents' pathNames 
   * @return agents' pathNames
   */
  public Enumeration agentPathNames(){
    return agentsByPathName.keys();
  }

  /**
   * Find agent by name OR pathname.  Pathname is tried first.
   * @return agent according to name
   */
  public Agent agent( String name ){
    int i = name.indexOf("/");
    if (i >= 0) {
      if (i > 0) name = "/" + name;
      return (Agent) agentsByPathName.get(name);
    }
    // If the name didn't have a "/", look for it in agentsByName
    return (Agent) agentsByName.get(name);
  }

  /** 
   * Find agent addressed by a pathname.
   */
  public Agent agentFromPath(String path) {

    // URL-decode path first to catch tilde passed as %7e
    path = Utilities.urlDecode(path);

    // Check for leading "~"

    if (path.startsWith("/~")) {
      path = path.substring(2);
      if (path.indexOf("/") > 0) {
	path = path.substring(0, path.indexOf("/"));
      }
      return (Agent)agentsByName.at(path);
    }

    // See whether the path is an agent's home:

    if (path.lastIndexOf("/") > 0) {
      path = path.substring(0, path.lastIndexOf("/"));
    }
    return (Agent) agentsByPathName.at(path);
    
    /* === The following no longer seems like the right behavior,
    // === but we leave the code in for old times' sake.

    // Go through all the agents looking for the one
    // 	  whose pathName is the longest prefix of the path.
    int max = 0;
    Agent agent = null;
    Enumeration e = agentsByPathName.keys();
    while( e.hasMoreElements() ){
      String pn = e.nextElement().toString();
      if (path.startsWith(pn) && pn.length() > max) {
	agent = (Agent)agentsByPathName.at(pn);
	max = pn.length();
      }
    }
    return agent;
    */
  }

  /** Run through the Crontab and run anything that needs running. */
  public void runCrontab() {

    /* Ensure that this check and last check fall into different 1-minute
     *	buckets as measured since EPOCH. */

    long thisTime = System.currentTimeMillis();
    if (thisTime / MINUTE <= cronTime / MINUTE) return;

    cronTime = thisTime;
    Pia.debug("Running crontabs at cronTime=" + cronTime);
    crontab.handleRequests(cronTime);

  }

  /************************************************************************
  ** Control:
  ************************************************************************/

  /**
   * stop thread 
   */
  protected void shutdown(){
    finish = true;
  }

  /**
   * tell each machine in transaction to shutdown?
   */
  protected void cleanup(boolean restart){
    // off course should check number of transaction
    //pop each transaction and tells its machine to shutdown
    finish = restart;
  }

  protected void finalize() throws IOException{
    cleanup( false );
  } 

  protected void restart(){
    start();
  }

  /************************************************************************
  ** Main Loop:
  ************************************************************************/

  /** Count of the total number of transactions processed. */
  public long count;

  /**
   *  Resolve -- This is the resolver's main loop.  It starts with one or more
   *  incoming transactions that have been pushed onto its queue, and
   *  loops until they're all taken care of.
   */
  public void run(){
    Transaction tran;
    String urlString;
    long delay = 100;

    count = 0;

    // Main loop.  
    //	 Entered with some transactions in the input queue.
    //	 Returns total number of transactions processed.
    while( !finish ){
      //      Pia.debug(this, "Pending items = " + size());
      if( size() == 0 ) synchronized (this) {
	try{
	  // === should really wait for push to signal us. ===
	  //Thread.currentThread().sleep(delay);
	  wait(delay);
	}catch(InterruptedException ex){;}
	catch(Exception e){
	  //	  Pia.debug(this,"exception caught in run");
	  //	  e.printStackTrace();
	}
      } else try {
	tran = pop();
	count++;

	if (Pia.debug())
	  Pia.debug(this, "Popped "+count+", " + size()+ " left.  Matching.");
	/*
	  Look for matches.
	  Matching agents have their act_on method called with both the
	  transaction and the resolver as arguments; they can either push
	  transactions onto the resolver, push satisfiers onto the
	  transaction, or directly modify the transaction.
	  */
	int n = match( tran );
	if (Pia.debug())
	  Pia.debug(this, "..." + n + " matches.  Satisfying.");
	
	/*
	  Tell the transaction to go satisfy itself.  
	  It does this by calling each of the handlers that matched agents
	  have pushed onto its queue, and looking for a true response.
	  Do it indirectly by notifying transaction that it is resolved,
	*/
	tran.resolved();      
      } catch (Exception e) {
	  System.err.println("Resolver caught exception in run()");
	  e.printStackTrace();
      }

      if (System.currentTimeMillis() > cronTime + cronDELAY) 
	runCrontab();
      
    }
    Pia.debug(this, "RUN thread EXITED");
    //    System.out.println("RUN exited");
    cleanup(false);
    
  }

  /************************************************************************
  ** Matching features:
  ************************************************************************/

  /**
   * Find all agents that match the given transaction 
   * each agent that matches gets its actOn method called.
   * @Return the number of matches.
   */

  public int match( Transaction tran ){
    Enumeration e = matchingAgents();
    Agent agent;
    int matches = 0;

    /*
     Loop through all the agents looking for matches.
     Every agent that matches is allowed to push new requests onto the
     resolver, or to modify the transaction directly.
    */
    
    while( e.hasMoreElements() ){
      agent = (Agent) e.nextElement();
      if (Pia.debug()) Pia.debug(this, "   match " + agent.name() + "?");

      if (tran.matches( agent.criteria() )){
	if (Pia.debug()) Pia.debug(this, "   matched " + agent.name());
	agent.actOn( tran, this );
	++ matches;
      }
    }
    return matches;
  }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Resolver(){
    transactions    = new Queue();
    this.start();
  }

}





