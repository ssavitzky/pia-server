////// AgentBuilder.java: <namespace> Handler implementation
//	$Id: AgentBuilder.java,v 1.2 1999-05-06 20:46:58 steve Exp $

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
import org.risource.dps.process.ActiveDoc;

import org.risource.pia.*;
import org.risource.ds.Tabular;

/**
 * Handler for &lt;AGENT&gt;....&lt;/&gt; 
 *
 * <p>	Expand the content in a context that constructs a new Agent. 
 *	Install the Agent. 
 *
 * @version $Id: AgentBuilder.java,v 1.2 1999-05-06 20:46:58 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class AgentBuilder extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;AGENT&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    ActiveDoc env = ActiveDoc.getActiveDoc(cxt);
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    if (atts == null) atts = new TreeAttrList();
    String name = atts.getAttribute("name");
    if (name != null) name = name.trim();

    // Security check:  the current agent must be Admin.
    //	 we make an exception if the Admin agent hasn't been started yet.
    Agent admin = env.getAgent();
    if (!(admin instanceof Admin) && Admin.installed) {
      reportError(in, cxt, "only works in the Admin agent");
      return;
    }

    Agent agent = makeAgent(in, cxt, name, atts);
    agent.loadFrom(in, cxt, (Tabular)atts);
    Pia.instance().resolver().registerAgent( agent );
    out.putNode((ActiveNode)agent);
  }

  /** Construct the agent. */
  protected Agent makeAgent(Input in, Context cxt, String name,
			    ActiveAttrList atts) {
    String type = atts.getAttribute("type");
    if (type != null) type = type.trim();
    String className = atts.getAttribute("class");
    if (className != null) className = className.trim();

    if (className != null && className.indexOf('.') < 0) {
      className = "org.risource.pia.agent." + className; 
    }

    ActiveDoc env = ActiveDoc.getActiveDoc(cxt);
    if (env == null) {
      reportError(in, cxt, "PIA not running.");
      return null;
    }

    Agent newAgent = null;
    if (className != null) {
      try{
	newAgent = (Agent) (Class.forName(className).newInstance()) ;
	newAgent.name( name );
	newAgent.type( type );
      }catch(Exception ex){
	reportError(in, cxt, "Unable to load Agent class " + className);
      }
    }
    if (newAgent == null) {
      newAgent = new GenericAgent(name, type);
    }

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
