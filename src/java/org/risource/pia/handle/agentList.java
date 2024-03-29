////// agentList.java:  Handler for <agent-home>
//	$Id: agentList.java,v 1.7 2001-04-03 00:05:18 steve Exp $

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


package org.risource.pia.handle;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeNodeArray;
import org.risource.dps.tree.TreeText;

import org.risource.pia.Agent;
import org.risource.pia.Resolver;
import org.risource.pia.site.SiteDoc;

import java.util.Enumeration;
import org.risource.ds.List;

/** Handler class for &lt;agent-list&gt tag 
 */
public class agentList extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, ActiveNodeList content) {
    SiteDoc env = SiteDoc.getSiteDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    // === type and subs  really should be deprecated
    String type = atts.getAttribute("type");
    boolean subs = atts.hasTrueAttribute("subs");

    Resolver resolver = env.getResolver();

    TreeNodeArray list = new TreeNodeArray();
    list.setSep(" ");

    Enumeration names = resolver.agentNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      list.append(new TreeText(name));
    }

    // === should really return list of agent names as a list ===
    putText(out, aContext, list.toString());
  }
}
