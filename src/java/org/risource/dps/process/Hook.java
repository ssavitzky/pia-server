////// HookProcessor.java: Top Processor for processing Agent hooks
//	Hook.java,v 1.8 1999/03/01 23:46:43 pgage Exp

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


package org.risource.dps.process;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import java.net.URL;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dom.NodeList;
import org.risource.ds.List;

import org.risource.pia.Pia;
import org.risource.pia.Agent;
import org.risource.pia.Transaction;
import org.risource.pia.Resolver;

/**
 * A TopProcessor for processing actOn hooks in PIA agents.
 *
 * @version Hook.java,v 1.8 1999/03/01 23:46:43 pgage Exp
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.pia
 * @see org.risource.dps.process.TopProcessor
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Context */

public class Hook extends ActiveDoc {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected Agent 	agent 		= null;
  protected Transaction	transaction 	= null;
  protected Resolver 	resolver 	= null;

  /************************************************************************
  ** PIA information:
  ************************************************************************/

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

  public Resolver getResolver() {
    return resolver;
  }    

  public Transaction getTransaction() {
    return transaction;
  }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  public void initializeEntities() {
    super.initializeEntities();
    initializeLegacyEntities();
    initializeHookEntities();
  }


  /** Initialize entities that differ for each hook called on a transaction. */
  public void initializeHookEntities() {
    // Set these even if we retrieved an entity table from the 
    // transaction -- the agent is (necessarily) different      

    define("agentName", agent.name());
    define("agentType", agent.type());
    if (agent.name().equals(agent.type())) {
      define("agentPath", "/"+agent.name());
    } else {
      define("agentPath", "/"+agent.type()+"/"+agent.name());
    }

  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  Hook() {
    super();
  }

  public Hook(Agent a, Transaction req, Transaction resp, Resolver res) {
    super(a, req, resp, res);
  }

}

