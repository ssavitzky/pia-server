////// agentInstall.java:  Handler for <agent-install>
//	$Id: agentRemove.java,v 1.7 2001-04-03 00:05:17 steve Exp $

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


package org.risource.pia.agent;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.pia.site.SiteDoc;

/** Handler class for &lt;agent-install&gt tag 
 */
public class agentRemove extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, ActiveNodeList content) {
    SiteDoc env = SiteDoc.getSiteDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String name = atts.getAttribute("agent");
    if (name == null || name.equals("")) name = atts.getAttribute("name");
    if (name == null) {
      reportError(in, aContext, "no agent name given");
      return;
    }

    org.risource.pia.agent.Admin admin = null;
    try {
      admin = (org.risource.pia.agent.Admin) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Admin agent");
      return;
    }
    env.getResolver().unRegisterAgent( name );
    aContext.message(0, "Agent "+name+" removed.", 0, true);    
    putText(out, aContext, name);
  }


  public agentRemove() { syntaxCode = EMPTY; }

}

