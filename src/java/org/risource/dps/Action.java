////// Action.java: Active Node action handler interface
//	$Id: Action.java,v 1.8 2001-04-03 00:04:10 steve Exp $

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


package org.risource.dps;
import org.w3c.dom.Node;

import org.risource.dps.active.*;

/**
 * The interface for a Node's ``Action'' (semantic handler). 
 *
 *	A Node's Action provides all of the semantic actions required for
 *	processing (including presenting) a Node.  <p>
 *
 *
 * @version $Id: Action.java,v 1.8 2001-04-03 00:04:10 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Input 
 * @see org.w3c.dom.Node
 */
public interface Action {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Returns an action code to a Processor.
   *
   *	The current node is the one obtainable from the Input via 
   *	<code>getNode</code>.  The action routine is free to make any 
   *	necessary assumptions about the type of the node, and call
   *	the appropriate access function to obtain it.  This is much
   *	more efficient than copying the input node into yet another
   *	cursor to operate on it. <p>
   *
   * @return integer ``action code'' indicating what additional action to take:
   */
  public int getActionCode();

  /** Action code: copy the node and its contents. */
  public static final int COPY_NODE   =  0;

  /** Action code: expand entities in the node's attributes; perform
   *	processing actions in its content. 
   */
  public static final int EXPAND_NODE =  1;

  /** Action code: call the node's Action handler. 
   */
  public static final int ACTIVE_NODE =  2;

  /** Action code: put the node on the output.  Its content has either 
   *	already been parsed, or (more likely) does not exist. 
   */
  public static final int PUT_NODE    =  3;
  public static final int PUT_VALUE   =  4;

  public static final String actionNames[] = { 
    "COMPLETED", "COPY_NODE", "EXPAND_NODE", "EXPAND_ATTS",
    "PUT_NODE", "PUT_VALUE" };

  /** Performs the action associated with the current Node in a given Context.
   *	Calling this instead of calling <code>getActionCode</code> should always
   *	produce correct results. 
   */
  public void action(Input in, Context aContext, Output out);

  /** Returns the value associated with the given Node in the given context.
   *	The node need not be the current one, but it must be the one to which
   *	this Action applies.
   */
  public void getValue(Node aNode, Context aContext, Output out);

  /** Returns the value associated with the given name in a given Node and
   *	context.  The node need not be the current one, but it must be the one
   *	to which this Action applies.
   */
  public void getValue(String aName, Node aNode, Context aContext, Output out);

  /************************************************************************
  ** Processing Control Flags:
  ************************************************************************/

  /** If <code>true</code>, the content is expanded (processed). 
   *	Otherwise, it is simply copied.
   */
  public boolean expandContent();

}
