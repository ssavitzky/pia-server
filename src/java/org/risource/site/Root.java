////// Root.java -- interface for the topmost resource in a site
//	$Id: Root.java,v 1.3 1999-09-17 23:39:51 steve Exp $

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


package org.risource.site;

import org.w3c.dom.*;
import org.risource.dps.*;

import java.io.File;
import java.net.URL;

/**
 * Generic interface for the root of a resource tree.
 *
 * <p> 
 *
 * @version $Id: Root.java,v 1.3 1999-09-17 23:39:51 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public interface Root extends Resource {

  /** Returns the URL by which this resource tree can be accessed. */
  public URL getServerURL();

  /** Report an exception. */
  public void reportException(Exception e, String explanation);

  /** Report an message. */
  public void report(int severity, String message,
		     int indent, boolean noNewline);

  /** Obtain the current verbosity level for error reporting. */
  public int getVerbosity();

  /** Obtain the home Resource associated with an agent. 
   *
   *<p> The home resource of an agent is also accessible as a virtual
   *	child of the Root, by preceeding its name by a ``<code>~</code>'' 
   *	character.  Note that agents themselves are not represented in the
   *	tree, although their resources and metadata are.  This way, we
   *	avoid constraining the implementation of the agents themselves.
   *
   * @param name the name of the agent (<em>without</em> a leading
   *	``<code>~</code>'' character). 
   * @see #registerAgentHome
   */
  public Resource agentHome(String name);

  /** Associate a home Resource with an agent.
   *
   * @param name the name of the agent (<em>without</em> a leading
   *	``<code>~</code>'' character). 
   * @param home the Resource associated with this agent.
   * @see #agentHome
   */
  public void registerAgentHome(String name, Resource home);

  /** List all registered agents. */
  public String[] listAgents();

  /** Construct a suitable TopContext for processing a Document. 
   *
   *<p>	Note that this operation is used for processing configuration 
   *	documents.  Therefore, it should not be assumed that the container
   *	of the document to be processed is fully configured yet.
   *
   * @param doc the Document to be processed.  If null, an unconfigured
   *	TopContext of the appropriate type is constructed.
   * @param ts the tagset with which to process the document.  If omitted, 
   *	the document provides its own Input.
   * @return a suitable TopContext.
   */
  public TopContext makeTopContext(Document doc, Tagset ts);
}
