////// Action.java: Active Node action handler interface
//	Action.java,v 1.14 1999/03/01 23:45:23 pgage Exp

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


package org.risource.dps;
import org.risource.dom.Node;
import org.risource.dom.Element;
import org.risource.dom.NodeList;
import org.risource.dom.AttributeList;

import org.risource.dps.active.*;

/**
 * The interface for a Node's ``Action'' (semantic handler). 
 *
 *	A Node's Action provides all of the semantic actions required for
 *	processing (including presenting) a Node.  <p>
 *
 *
 * @version Action.java,v 1.14 1999/03/01 23:45:23 pgage Exp
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Input 
 * @see org.risource.dom.Node */

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
  public int actionCode(Input in, Processor p);

  /** Action code: <code>actionCode</code> has completed the action. */
  public static final int COMPLETED   = -1;

  /** Action code: copy the node and its contents. */
  public static final int COPY_NODE   =  0;

  /** Action code: expand entities in the node's attributes; perform
   *	processing actions in its content. 
   */
  public static final int EXPAND_NODE =  1;

  /** Action code: expand entities in the node's attributes; blindly copy
   *	its content. 
   */
  public static final int EXPAND_ATTS =  2;

  /** Action code: put the node on the output.  Its content has either 
   *	already been parsed, or (more likely) does not exist. 
   */
  public static final int PUT_NODE    =  3;
  public static final int PUT_VALUE   =  4;

  public static final String actionNames[] = { 
    "COMPLETED", "COPY_NODE", "EXPAND_NODE", "EXPAND_ATTS",
    "PUT_NODE", "PUT_VALUE" };

  /** Performs the action associated with the current Node in a given Context.
   *	Calling this instead of calling <code>actionCode</code> should always
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

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. <p>
   *
   *	Note that a Nodewould be quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.<p>
   *
   *	<b>Implementation Note:</b> It is important that the Handler's
   *	<code>convertToString</code> method <em>not</em> call the Token's
   *	<code>toString</code>, method, since that will normally call the
   *	Handler and produce an infinite recursion.  Use
   *	<code>basicToString</code> instead. <p>
   *
   * === Not clear where entity, url encoding and decoding is done. ===
   */
  public String convertToString(ActiveNode n);

  /** Converts the Node to a String according to the given syntax. <p>
   *
   *  The string corresponding to a node is:
   *	<pre>
   *	     convertToString(t, -1) + 
   *	     convertToString(t,  0) +
   *	     convertToString(t,  1)
   *	</pre>
   */
  public String convertToString(ActiveNode n, int syntax);

}
