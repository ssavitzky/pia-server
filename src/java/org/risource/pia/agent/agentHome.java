////// agentHome.java:  Handler for <agent-home>
//	$Id: agentHome.java,v 1.5 1999-04-07 23:22:25 steve Exp $

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
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeElement;

import org.risource.pia.Agent;
import org.risource.ds.List;

/** Handler class for &lt;agent-home&gt tag 
 */
public class agentHome extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, ActiveNodeList content) {
    ActiveDoc env = ActiveDoc.getActiveDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String name = env.getAgentName(atts.getAttribute("agent"));
    String type = env.getAgentType(name);
    boolean link = atts.hasTrueAttribute("link");

    String home = (type.equals(name))? name : type + "/" + name;
    if (link) {
      ActiveElement t = new TreeElement("a", null, null, null);
      t.setAttribute("href", "/" + home + "/home");
      t.addChild(new TreeText(home));
      out.putNode(t);
    } else {
      putText(out, aContext, home);
    }
  }
}
