// Agent.java
// Agent.java,v 1.11 1999/03/01 23:48:17 pgage Exp

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


package crc.tf;

import java.net.URL;

import crc.pia.Transaction;
import crc.pia.Pia;

import crc.tf.TFComputer;

public final class Agent extends TFComputer {

  /**
   * Get an agent's name in a request URL.
   * @param object A transaction 
   * @return agent's name as an object if exists otherwise null
   */
  public Object computeFeature(Transaction trans) {

    if (trans.isResponse()) trans = trans.requestTran();
    if (trans == null) return null;

    if (! trans.test("agent-request")) return "";

    URL url = trans.requestURL();
    if( url == null ) return "";

    String path = url.getFile();
    if( path == null ) return "";
      
    crc.pia.Agent agent = Pia.instance().resolver().agentFromPath(path);

    if (agent != null) 
      trans.assert("agent-type", agent.type());

    return (agent != null)? agent.name() : "";
  }
}









