// Agent.java
// $Id: Agent.java,v 1.6 1999-03-26 01:29:07 steve Exp $

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


import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Criteria;
import org.risource.ds.Criterion;
import org.risource.ds.Tabular;

import org.risource.tf.UnknownNameException;

/**
 * An agent is an object which maintains state and context and corresponds
 *	to a URL in the PIA server. 
 *
 * <p> Agents can receive requests directly (http://pia/AGENT:pathName/...);
 *	they can also operate on other transactions.  Direct requests are
 *	handled by the <code>respond</code> methods.  To operate on other
 *	transactions, Agents register with the resolver a set of criteria for
 *	transactions they are interested in.  When the resolver finds a
 *	matching transaction, the agent's actOn method is called.
 *
 * <p> An agent can completely handle a transaction by putting itself on the
 *	transaction's list of handlers -- which results in a call back to the
 *	agent's <code>handle</code> method.
 *
 * <p> Agent extends the <code>Tabular</code> interface, and all of its 
 *	attributes are also accessible through this interface.
 */
public interface Agent extends Tabular {

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
  public void name(String name);

  /**
   * Return the Agent's ``type'': the pathname of an agent from which 
   *	files are inherited.
   *
   * <p> If the agent inherits only from the root, the type will be equal
   *	 to the agent's name.  Otherwise it will be a valid path, starting
   *	 with "/".
   *
   * @return type of agent
   */
  public String type();

  /**
   * set type of agent
   */
  public void type(String type);

  /** 
   * Return the agent's parent in the agent type hierarchy.  This is, 
   *	by definition, the agent whose pathName is <code>type()</code>.
   */
  public Agent typeAgent();

  /**
   * Return the agent's ``mount point'' in the PIA's URL hierarchy. 
   * <p>
   *	The path will always begin and end with "/", so that the Agent's
   *	URL will always be <code>path() + name()</code>.
   *
   * @return agent's mount point in URL hierarchy.
   */
  public String path();

  /**
   * set path
   */
  public void path(String path);

  /** Return the complete ``pathname'' of this agent: the file part of the
   *	agent's root URL.
   *
   * <p> By definition, this is path + name, and is provided for convenience.
   *
   * @return agent's root URL.
   */
  public String pathName();


  /************************************************************
  ** operations for working with resolver:
  ************************************************************/

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria();
  
  /**
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
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine);

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
   * Set options with a hash table
   *
   */
  public void parseOptions(Table hash);

  /************************************************************
  ** Directories:
  ************************************************************/

  /** The agent's <em>home</em> directory: where its documents come from.
   *	
   * @return full path to the agent's home directory.
   */
  public String homeDirectory();

  /** The agent's <em>user</em> (customization) document directory.
   *
   *	Documents in the user directory override those in the home directory,
   *	allowing an individual user to customize documents belonging to a
   *	shared agent.
   *	
   * @return full path to the agent's user directory.
   */
  public String userDirectory();

  /** The agent's <em>data</em> directory.
   *
   *	Specifies the path to the directory used for the agent's data files.
   *	This directory must be writable, and is created if necessary.
   *	
   * @return full path to the agent's user directory.
   */
  public String dataDirectory();


  /** The agent's document directory search path. 
   *
   * @return a list of File objects refering to directories to search.
   */
  public List documentSearchPath();

  /** The agent's document directory search path. 
   *
   * @return a list of File objects refering to directories to search.
   */
  public List documentSearchPath(boolean forWriting);


  /**
   * Find a document, using a simple search path which allows for user
   *	overrides of an agent's documents, and a crude kind of inheritance.  
   */
  public String findDocument( String path );

  /**
   * Find an document, using an optional suffix search list and adjusting
   *	the directory search path according to whether writing is required.
   */
  public String findDocument( String path, String suffixSearch[], 
			       boolean forWriting );

  /**
   * Find a data file using an optional suffix search list. 
   */
  public String findDataFile( String path, String suffixSearch[], 
			      boolean forWriting );


  /************************************************************
  ** Responding to direct requests:
  ************************************************************/

  /**
   * Respond to a request directed at an agent.
   * The document's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
  public void respond(Transaction trans, Resolver res) throws PiaRuntimeException;

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *
   * <p> This method is primarily used in initialization and in other cases
   *	where a request is being made for its side effects rather than its
   *	results.
   *
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param contentType (optional) -- content type for a POST request.
   */
  public void createRequest(String method, String url,
			    String queryString, String contentType);

  /**
   * Given a url string and content create a request transaction.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param content content object for a POST or PUT request.
   *	@param contentType (optional) content type for a POST or PUT request.
   */
  public void createRequest(String method, String url,
			    InputContent content, String contentType);

  /**
   * Given a url string and content, create a request transaction.
   *       The results are discarded.
   *

   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param contentType (optional) -- content type for a POST request.
   *	@param times a Tabular containing the timing information
   */
  public void createTimedRequest(String method, String url, String queryString,
				 String contentType, Tabular times);


  /** 
   * Handle timed requests.  
   *
   * <p> This method is called periodically from the Resolver to handle any 
   *	 timed requests that may have been registered with createTimedRequest.
   *
   * @param time the current time 
   * @see org.risource.pia.Agent#createTimedRequest
   * @see org.risource.pia.Resolver
   */
  public void handleTimedRequests(long time);

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg );

  /**
   * Send error message for "document not found"
   */
  public void respondNotFound( Transaction req, String path);

  /**
   * Respond to a transaction with a stream of HTML.
   */
  public void sendStreamResponse ( Transaction trans, InputStream in );

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










