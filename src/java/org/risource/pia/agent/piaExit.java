////// piaExit.java:  Handler for <agent-restore>
//	$Id: piaExit.java,v 1.4 2001-04-03 00:05:17 steve Exp $

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
import org.risource.dps.util.MathUtil;

import org.risource.pia.Agent;
import org.risource.ds.List;
import org.risource.pia.site.SiteDoc;

import java.io.File;
import java.io.FileInputStream;

/** Handler class for &lt;pia-exit&gt tag 
 */
public class piaExit extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, ActiveNodeList content) {
    SiteDoc env = SiteDoc.getSiteDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }
    Admin admin = null;
    try {
      admin = (Admin) env.getAgent();
    } catch (Exception e) {
      reportError(in, aContext, "only works in the Admin agent");
      return;
    }

    if (admin.hasTrueAttribute("lockExit")) {
      reportError(in, aContext, "no exit: locked");
      return;
    }

    String msg = (content == null)
      ? "PIA shut down by Admin agent." 
      : content.toString();

    aContext.message(0, msg, 0, true);
    System.exit((int)MathUtil.getLong(atts, "status", 1));
  }
}

