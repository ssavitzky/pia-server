////// agentSave.java:  Handler for <agent-save>
//	agentSave.java,v 1.2 1999/03/01 23:48:01 pgage Exp

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
import org.risource.dps.util.*;
import org.risource.dps.process.ActiveDoc;

import org.risource.dom.NodeList;
import org.risource.pia.Agent;
import org.risource.ds.List;

import java.io.File;
import java.io.FileInputStream;


/** Handler class for &lt;agent-save&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-save">Manual Entry</a> 
 *	for syntax and description.
 */
public class agentSave extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getInterFormContext(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    List list = new List(ListUtil.getListItems(atts.getAttributeValue("list")));

    if (list.nItems() == 0) {
      String name = atts.getAttributeString("agent");
      if (name == null) name = env.getAgentName();
      if (name != null) list.push(new ParseTreeText(name));
    }

    List agents = new List();
    for (int i = 0; i < list.nItems(); ++i) {
      Agent agent = env.getAgent(list.at(i).toString());
      if (agent != null) {
	agents.push(agent);
      }
    }

    if (agents.nItems() == 0) {
      reportError(in, aContext,
		  "No agents specified or specified agents not found");
      return;
    }

    String fn = atts.getAttributeString("file");
    if (fn == null) {
      reportError(in, aContext, "Must have non-null file attribute.");
      return;
    }
    File file = env.locateSystemResource(fn, true);
    if (file == null) {
      reportError(in, aContext, "Cannot locate " + fn);
      return;
    }
    File parent = (file.getParent()!=null)? new File(file.getParent()) : null;

    String errmsg = null;
    try {
      if (parent != null && ! parent.exists()) {
	if (! parent.mkdirs())
	  errmsg = "Cannot make parent directory for " + file.getPath();
      }
      if (atts.hasTrueAttribute("append")) {
	org.risource.util.Utilities.appendObjectTo(file.getPath(), agents);
      } else {
	org.risource.util.Utilities.writeObjectTo(file.getPath(), agents);
      }
      putText(out, aContext, file.getPath());
    } catch (Exception e) {
      System.out.println("exception: " + e.getMessage());
      e.printStackTrace();
      errmsg = "Write failed on " + file.getPath();
    }

    if (errmsg != null) {
      reportError(in, aContext, errmsg);
      putText(out, aContext, errmsg);
    }
  }
}
