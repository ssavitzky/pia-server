// Crontab.java
// $Id: Crontab.java,v 1.5 2001-04-03 00:05:10 steve Exp $

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

package org.risource.pia;

/** Registry for timed operations.
 *	The Crontab gets its name from the Unix <code>crontab</code> table,
 *	and has similar capabilities.  Entries are made in the Crontab
 *	by the &lt;submit-forms&gt; actor.<p>
 *
 *  A Crontab is associated with each Agent that needs one.  That means that
 *	an Agent's Crontab will be checkpointed along with it.<p>
 */

import org.risource.pia.Transaction;
import org.risource.pia.Agent;
import org.risource.pia.Resolver;

import org.risource.ds.Registered;
import org.risource.ds.Tabular;
import org.risource.ds.List;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;

public class Crontab extends List implements Serializable {

  /** The last time the crontab was run, as given by
   *	System.currentTimeMillis().
   */ 
  public long lastTime = 0;

  /************************************************************************
  ** Registry:
  ************************************************************************/


  public void addRequest(CrontabEntry entry) {
    push(entry);
  }

  /** Remove the earliest entry that matches the one given. */
  public void removeRequest(CrontabEntry entry) {

  }

  /************************************************************************
  ** Creating and Submitting Requests:
  ************************************************************************/

  /**
   * Given ann Agent and timing information, create a Crontab entry.
   *
   *	@param agent the Agent submitting the request.
   *	@param times  a Tabular containing the timing information
   *
   *	@see org.risource.pia.CrontabEntry
   */
  public void makeEntry(Agent agent, Tabular times) {
    // CrontabEntry will assume defaults for contentType, and will
    // convert queryString to a ByteArrayOutputStream
    addRequest(new CrontabEntry(agent, times));
  }


  /**
   * Make any requests that have come due since the last time.
   *	Each request is only submitted once, no matter how many times
   *	it may have matched the current time (for example, requests to
   *	be submitted every minute will run at most once when the PIA comes
   *	up after being down for an hour).  It is possible that requests
   *	that come due while the PIA is down will not get run at all. <p>
   *
   *	The repeat count of each request is decremented; any request that
   *	``expires'' with a repeat count of zero is removed.<p>
   */
  public void handleRequests(long time) {
    long previousTime = lastTime;
    lastTime = time;

    /* Loop through the items.  Start at the end and work down to prevent
     *	getting confused when an expired entry is removed. */

    for (int i = nItems(); --i >= 0; ) {
      CrontabEntry entry = (CrontabEntry)at(i);
      if (entry.handleRequest(previousTime, lastTime)
	  && entry.expired()) remove(entry);
    }
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Crontab() {
  }

  public Crontab(long time) {
    lastTime = time;
  }

}
