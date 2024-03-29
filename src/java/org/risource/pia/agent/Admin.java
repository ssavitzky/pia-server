// Admin.java
// $Id: Admin.java,v 1.8 2001-04-03 00:05:15 steve Exp $

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


/**
 * This is the class for the ``Admin'' agent; i.e. the one that
 *	performs PIA system administration.  It contains the specialized
 *	code that installs and removes agents.
 */

package org.risource.pia.agent;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Enumeration;

import java.net.URL;

import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Criterion;

import org.risource.site.*;

import org.risource.pia.Resolver;
import org.risource.pia.Agent;
import org.risource.pia.Pia;

public class Admin extends Generic {
  /**
   * This flag is set when the Admin agent is installed. 
   *	It exists so that certain handlers that depend on Admin can run
   *	before Admin exists, e.g. in order to load it.
   */
  static boolean installed = false;

  /**
   * Uninstall (unRegister) an agent.
   */
  public void unInstallAgent(String name){
    Pia.resolver().unRegisterAgent( name );
  }

  /**
   * Install (register) an agent.
   */
  public void installAgent(Agent newAgent){
    Pia.resolver().registerAgent( newAgent );
  }

  /**
   * Create and install a named agent.
   *	Automatically loads the class if necessary.
   *	@param ht initial options.
   *	@exception org.risource.pia.agent.AgentInstallException if problems are
   *	  found, for example a null table or missing parameter.
   */
  public void install(Table ht)
       throws NullPointerException, AgentInstallException {

    if( ht == null ) throw new NullPointerException("bad parameter Table ht\n");
    String name      = (String)ht.get("agent");
    String className = (String)ht.get("class");
    String docName   = (String)ht.get("state");

    if (name == null || name.equals(""))
      throw new AgentInstallException("No agent name");

    /* Compute a plausible class name from the type. */

    if (className != null 
	&& className.length() > 0 && className.indexOf('.') < 0) {
      className = "org.risource.pia.agent." + className; 
    }

    /* Load the class, if it exists. */

    Agent newAgent = null;
    if( className != null && className.length() > 0){
      try{
	newAgent = (Agent) (Class.forName(className).newInstance()) ;
	newAgent.setName( name );
      }catch(Exception ex){
	throw new AgentInstallException("cannot load agent class" + ex);
      }
    }

    /* If the class doesn't exist, use Generic. */

    if (newAgent == null) newAgent = new Generic(name, null);

    /* Install and initialize the new agent.  The Resolver actually 
       does the intialization, after installing the agent.
     */

    installAgent( newAgent );
  }
  /**
   * Constructor.
   */
  public Admin(String name, Document doc){
    super(name, doc);
    installed = true;
  }

  /** Default constructor. */
  public Admin() {
    super();
    installed = true;
  }

}





















