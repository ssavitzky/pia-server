////// ToAgent.java: Output to Agent

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
import org.risource.dps.util.*;
import org.risource.dps.active.*;
import org.risource.dps.output.ToNamespace;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.namespace.BasicNamespace;

import org.risource.pia.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Output to an Agent.
 *
 * <p>	This class is used to construct an Agent, and in particular a
 *	Generic.
 *
 * @version $Id: ToAgent.java,v 1.5 2001-01-11 23:37:47 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.pia.Agent
 * @see org.risource.pia.agent.Generic
 */
public class ToAgent extends ToNamespace {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Agent agent = null;

  /************************************************************************
  ** Access Methods:
  ************************************************************************/

  /** Get the Agent under construction. */
  public Agent getAgent() { return agent; }


  /************************************************************************
  ** Construction:
  ************************************************************************/
  public ToAgent(Tagset ts) {
    this(new Generic(), ts);
  }

  public ToAgent(Agent a, Tagset ts) {
    super(a, ts);
    agent = a;
  }

}
