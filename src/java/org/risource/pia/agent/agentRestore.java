////// agentRestore.java:  Handler for <agent-restore>
//	$Id: agentRestore.java,v 1.4 1999-03-23 23:32:46 steve Exp $

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
import org.risource.pia.Agent;
import org.risource.ds.List;

import java.io.File;
import java.io.FileInputStream;

/** Handler class for &lt;agent-restore&gt tag 
 */
public class agentRestore extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getActiveDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    String fn = atts.getAttributeString("file");
    if (fn == null) {
      reportError(in, aContext, "File attribute missing.");
      return;
    }
    File file = env.locateSystemResource(fn, true);

    boolean exists = file.exists();
    boolean isdir  = file.isDirectory();

    String result = "";

    if (! file.exists()) {
      reportError(in, aContext, "File '"+name+"' does not exist.");
      return;
    }

    if (! file.canRead()) {
      reportError(in, aContext, "File '"+name+"' cannot be read.");
      return;
    }

    if (isdir) {
      reportError(in, aContext, "File '"+name+"' is a directory.");
      return;
    }

    try {
      FileInputStream stm = new FileInputStream(file);
      List list = org.risource.util.Utilities.readObjectsFrom(stm);
      for (int i = 0; i < list.nItems(); ++i) {
	if (list.at(i) instanceof Agent) {
	  putText(out, aContext, ((Agent)list.at(i)).name());
	}
      }
    } catch (Exception e) {
      // exception reported in readObjectsFrom
    }    
  }
}

