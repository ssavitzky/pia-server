////// transControl.java:  Handler for <trans-control>
//	$Id: transControl.java,v 1.4 1999-03-23 23:32:53 steve Exp $

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


package org.risource.pia.handle;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.process.ActiveDoc;

import org.risource.dom.NodeList;
import org.risource.pia.Agent;
import org.risource.pia.Resolver;

import java.util.Enumeration;
import org.risource.ds.List;

/** Handler class for &lt;trans-control&gt tag 
 */
public class transControl extends org.risource.dps.handle.GenericHandler {

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, NodeList content) {
    ActiveDoc env = ActiveDoc.getActiveDoc(aContext);
    if (env == null) {
      reportError(in, aContext, "PIA not running.");
      return;
    }

    env.getTransaction().addControl(content.toString());
  }
}
