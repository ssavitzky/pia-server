// Agent.java
// $Id: Agent.java,v 1.12 1999-09-22 00:28:54 steve Exp $

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

package org.risource.pia;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.Resolver;
import org.risource.pia.Content;

import org.risource.dps.active.ActiveElement;
import org.risource.dps.Input;
import org.risource.dps.Context;
import org.risource.dps.Tagset;

import org.risource.site.Subsite;
import org.risource.site.Document;

import org.risource.ds.List;
import org.risource.ds.Criteria;
import org.risource.ds.Criterion;
import org.risource.ds.Tabular;

import org.risource.tf.UnknownNameException;

/**
 * An agent is an XML element that can be expanded under various conditions.
 *
 * <p>	An agent is associated with a ``home resource'' under
 *	the PIA server's <code>Site</code>; this home resource provides the
 *	context in which the Agent's code is expanded. 
 *
 * <p> Agents mainly exist to operate on transactions.  To do this,
 *	Agents register with the resolver a set of criteria for
 *	transactions they are interested in.  When the resolver finds a
 *	matching transaction, the agent's actOn method is called.
 *
 * <p> Rather than a set of resolver criteria, an Agent can schedule itself
 *	to be run at a particular time or at some regular interval.
 */
public interface Agent extends ActiveElement {

  /**
   * Default initialization; implementors may override
   */
  public void initialize();

  /************************************************************
  ** Access to attributes:
  ************************************************************/

  /**
   * Return the name of the Agent.  
   *
   *	The agent's name is the last component in the Agent's root URL.
   *
   * @return name of agent
   */
  public String name();

  /**
   * set name of agent
   */
  public void setName(String name);

  /** The Agent's ``home directory'' in the PIA's resource tree. 
   *
   *<p> By requiring this to be a Subsite, we guarantee that it will
   *	stick around, in the sense that all attempts to locate it by
   *	path will return the same object. 
   */
  public Subsite getHome();

  /**
   * set home Resource of agent
   */
  public void setHome(Subsite home);

  /** Return the complete pathname of the agent's home document.
   *
   * @return agent's root URL.
   */
  public String getHomePath();

  /** Return the agent's tagset.
   *	This is used for both parsing and processing the Agent's action.
   */
  public Tagset getTagset();

  /************************************************************
  ** operations for working with resolver:
  ************************************************************/

  /** The match criteria that cause the agent's action to be invoked.
   * <p>
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria();
  
  /** The Machine the Agent uses to send and receive transactions.
   * <p>
   * Agents are associated with a virtual machine which is an
   * interface for actually getting and sending transactionss.  Posts
   * explicitly to an agent get sent to the agent's machine (then  on to
   * the agent for processing). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine();

  /**
   * This method is called from the Resolver when an Agent's Criteria list
   * matches a Transaction's Features set.
   */
  public void actOn(Transaction ts, Resolver res);

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public boolean handle(Transaction ts, Resolver res);

  /**
   * This method is called to handle a ``crontab'' agent.
   */
  public boolean act();

  /************************************************************
  ** Interface to content objects:
  ************************************************************/
  /**
   * Agents can register interest in content objects using
   *	<code>content.notifyWhen</code>.  Content objects call the agent 
   *	back using <code>updateContent</code>.
   *
   * @param object: arbitrary object specified by agent in original
   *                notifyWhen call
   */
   public void updateContent(Content content, String state, Object object);

}










