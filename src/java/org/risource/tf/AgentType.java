// AgentType.java
// $Id: AgentType.java,v 1.4 1999-03-24 20:49:57 steve Exp $

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


package org.risource.tf;

import java.net.URL;

import org.risource.pia.Transaction;
import org.risource.pia.Pia;

import org.risource.tf.TFComputer;

public final class AgentType extends Agent {

  /**
   * Get an agent's type in a request URL.
   * @param trans A transaction 
   * @return agent's name as an object if exists otherwise null
   */
  public Object computeFeature(Transaction trans) {
    if (trans == null) return null;
    org.risource.pia.Agent agent = computeAgentFeatures(trans);
    return (agent == null)? "" : agent.type();
  }
}









