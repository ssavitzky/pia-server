////// AgentBuilder.java: <namespace> Handler implementation
//	$Id: AgentBuilder.java,v 1.5 2001-01-11 23:37:47 steve Exp $

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

import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.namespace.*;
import org.risource.dps.handle.GenericHandler;
import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.output.ToParseTree;

import org.risource.site.*;

import org.risource.pia.*;
import org.risource.pia.site.*;
import org.risource.ds.Tabular;

/**
 * Handler for &lt;AGENT&gt;....&lt;/&gt; 
 *
 * <p>	Expand the content in a context that constructs a new Agent. 
 *	Install the Agent. 
 *
 * @version $Id: AgentBuilder.java,v 1.5 2001-01-11 23:37:47 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class AgentBuilder extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;AGENT&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    SiteDoc env = SiteDoc.getSiteDoc(cxt);
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    if (atts == null) atts = new TreeAttrList();
    String name = atts.getAttribute("name");
    if (name != null) name = name.trim();

    // The old security check doesn't work anymore.
    //	 There's no Admin agent now.

    // Make the AGENT node with the correct class. 
    Agent agent = makeAgent(in, cxt, name, atts);

    // Set up the correct tagset for parsing the Agent's content.

    Tagset     ts = agent.getTagset();
    Output loader = new ToParseTree((ActiveNode)agent, ts);
    TopContext tp = env.subDocument(in, cxt, loader, ts);
    // It's up to the subDocument processor to switch tagsets.
    Processor p = tp.subProcess(tp.getInput(), loader);

    // Copy the children.  
    //   === We would like to use processChildren, but it seems to expand 
    //	 === things even inside of an <action> tag.  

    Copy.copyChildren(tp.getInput(), loader);
    //p.processChildren();
    loader.endNode();
    env.subDocumentEnd();

    // Initialize the Agent
    agent.initialize();
  }

  /** Construct the agent node. */
  protected Agent makeAgent(Input in, Context cxt, String name,
			    ActiveAttrList atts) {
    String className = atts.getAttribute("class");
    if (className != null) className = className.trim();

    if (className != null && className.indexOf('.') < 0) {
      className = "org.risource.pia.agent." + className; 
    }

    TopContext env = cxt.getTopContext();

    Agent newAgent = null;
    if (className != null) {
      try{
	newAgent = (Agent) (Class.forName(className).newInstance()) ;
	newAgent.setName( name );
	newAgent.setAttribute("class", className);
      }catch(Exception ex){
	reportError(in, cxt, "Unable to load Agent class " + className);
      }
    }
    if (newAgent == null) {
      newAgent = new Generic(name, env.getDocument());
    }

    cxt.message(1, "Creating agent from " + env.getDocument().getPath(), 
		2, false);

    ActiveNodeList nl = (atts == null)? null : atts.asNodeList();
    for (int i = 0; nl != null && i < nl.getLength(); i++) {
      ActiveAttr at = (ActiveAttr) nl.activeItem(i);
      newAgent.setAttributeNode(at);
    }

    Resource home = env.getDocument().getContainer();
    newAgent.setHome((Subsite)home);
    if (name != null) home.getRoot().registerAgentHome(name, home);

    return newAgent;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public AgentBuilder() {
    /* Expansion control: */
    expandContent = false;	// true 	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, NORMAL, 0 (check)
  }

  AgentBuilder(ActiveElement e) {
    this();
    // customize for element.
  }
}
