////// agentInstall.java:  Handler for <agent-install>
//	$Id: agentInstall.java,v 1.4 1999-03-23 23:32:45 steve Exp $

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

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.process.ActiveDoc;
import org.risource.dom.NodeList;

/** Handler class for &lt;agent-install&gt tag 
 */
public class agentInstall extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getActiveDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    // === buggy -- should get form from content === 
    //if (content != null) 
    //  notify(in, aContext, "Bug: agent options in content not implemented.");

    org.risource.pia.Transaction trans = env.getTransaction();
    org.risource.pia.Transaction req = trans.requestTran();
    org.risource.ds.Table form = req.getParameters();
    if (form == null) return;

    String name = form.has("agent")? form.at("agent").toString() : null;
    if (name == null) 
      name = form.has("name")? form.at("name").toString() : null;

    org.risource.pia.agent.Admin admin = null;
    try {
      admin = (org.risource.pia.agent.Admin) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Admin agent");
      return;
    }
    try {
      admin.install(form); 
    } catch (org.risource.pia.agent.AgentInstallException e) {
      reportError(in, aContext, "Install exception: " + e.getMessage());
      return;
    } 
    aContext.message(0, "Agent "+name+" installed.", 0, true);    
    putText(out, aContext, name);
  }
}

