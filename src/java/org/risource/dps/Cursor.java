////// Cursor.java: Shared interface for a ``current node''
//	$Id: Cursor.java,v 1.5 1999-06-25 00:40:52 steve Exp $

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

import org.w3c.dom.Node;
import org.risource.dps.active.*;

/**
 * The shared interface for classes that maintain a ``current'' node.
 *
 *	This interface provides convenience functions that permit general DOM
 *	trees and parse trees to be operated on with minimal (none, for parse
 *	trees) run-time type checking.  This makes the most common operations
 *	in the DPS significantly more efficient. <p>
 *
 * @version $Id: Cursor.java,v 1.5 1999-06-25 00:40:52 steve Exp $
 * @author steve@rsv.ricoh.com
 * 
 * @see org.risource.dps.Input
 * @see org.risource.dps.Output
 * @see org.risource.dps.Active
 * @see org.risource.dps.Processor
 */

public interface Cursor {

  /************************************************************************
  ** Current Node:
  ************************************************************************/

  /** Returns the current Node. <p>
   */
  public Node getNode();

  /** Returns the current Node as an ActiveNode. <p>
   *
   * @return <code>null</code> if the current Node is not Active. 
   */
  public ActiveNode getActive();

  /************************************************************************
  ** Information:
  ************************************************************************/

  /** Get the attributes of the current node. */
  public ActiveAttrList getAttributes();

  /** Get the nodename of the current node. */
  public String getNodeName();

  /** Returns the action (semantic) handler, if known, for the current node. 
   */
  public Action getAction();

  /** Returns the syntax handler, if known, for the current node. 
   */
  public Syntax getSyntax();

  /** Returns the current nesting depth. 
   *	Note that this is not necessarily the same as the total distance
   *	of the current Node from the top level of its containing Document;
   *	the Cursor may be iterating over a subtree.
   */
  public int getDepth();


  /** Test whether the current node has attributes.
   *
   * @return <code>true</code> if the current node has attributes.
   */
  public boolean hasAttributes();

  /** Return the current node's tagName if it is an element. */
  public String getTagName();

  /** Returns <code>true</code> if and only if <code>toParent</code> will
   *	return <code>false</code>. <p>
   *
   *	Note that <code>atTop</code> may be <code>true</code> even if the
   *	current node actually has a parent, if the Input is iterating over
   *	a subtree or fragment of a document. <p>
   */
  public boolean atTop();

  /************************************************************************
  ** Nesting:
  ************************************************************************/

  /** Return true if we are currently nested inside an element with
   *	the given tag.
   *
   *	Note that this may give incorrect results if different regions
   *	of the Document have different case sensitivities for tagnames!
   *
   * @param tag the tag to check for
   * @param ignoreCase <code>true</code> if tag comparison ignores case
   * @param stopDepth do not compare nodes below this depth.
   */
  public boolean insideElement(String tag, boolean ignoreCase);

  /** Return the tagname of the element at the given level.
   *
   * @return <code>null</code> if the node at that level is not an
   *	Element, or if the level does not exist.
   */
  public String getTagName(int level);
 
}
