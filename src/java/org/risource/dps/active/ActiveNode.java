////// ActiveNode.java: Interface for Nodes with actions.
//	$Id: ActiveNode.java,v 1.4 1999-04-07 23:20:58 steve Exp $

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


package org.risource.dps.active;
import org.w3c.dom.*;

import org.risource.dps.Active;
import org.risource.dps.Action;
import org.risource.dps.Handler;
import org.risource.dps.Syntax;
import org.risource.dps.Namespace;
import org.risource.dps.Input;
import org.risource.dps.Context;

/**
 * Interface for Active Nodes.
 *
 * <p>	By convention, an interface that extends <code>ActiveNode</code>
 *	has the name <code>Active<em>Xxxx</em></code>. 
 *
 * <p>	This interface contains strongly-typed convenience functions for
 *	conversion, navigation, and construction that are made significantly
 *	more efficient than the equivalent DOM functions by the fact that
 *	they do not need to perform run-time type casting.
 *
 * <p>	By the use of Cursor objects, it is possible to traverse and 
 *	manipulate trees made of ActiveNode objects without actually 
 *	seeing a Node.  In order to accomplish this, any DOM operation that 
 *	would normally return a NodeList has a corresponding ActiveNode 
 *	operation that returns a Cursor.  
 *
 * <p>	More specifically, a method that returns a Cursor that iterates over
 *	a set of Nodes (an Input) has a name starting with <code>from</code>,
 *	while a method that returns a Cursor to be used for construction (an
 *	Output) has a name starting with <code>into</code>.  A method that
 *	returns a Cursor that maps names to values has a name starting with
 *	<code>Map</code>
 *
 * @version $Id: ActiveNode.java,v 1.4 1999-04-07 23:20:58 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.w3c.dom.Node
 * @see org.risource.dps.Action
 * @see org.risource.dps.Active
 * @see org.risource.dps.Context
 * @see org.risource.dps.Processor
 */

public interface ActiveNode extends Active, Node {

  /************************************************************************
  ** Navigation:
  ************************************************************************/

  public ActiveNode getPrevActive();
  public ActiveNode getNextActive();
  public ActiveNode getActiveParent();
  public ActiveNode getFirstActive();
  public ActiveNode getLastActive();

  /************************************************************************
  ** Cursorial Navigation:
  ************************************************************************/

  /** Returns an Input iterating over the content (children) of the node. */
  public Input fromContent();

  /** Returns an Input iterating over the value of the node in the given
   *	context.  In most cases the value does not depend on the context,
   *	but it is required for correct expansion of entities. 
   */
  public Input fromValue(Context cxt);

  /************************************************************************
  ** Active Content:
  ************************************************************************/

  public ActiveAttrList getAttrList();
  public ActiveNodeList getContent();
  public ActiveNodeList getValueNodes(Context cxt);

  /************************************************************************
  ** Syntax:
  ************************************************************************/

  /** Returns the syntactic handler for this Node.
   */
  public Syntax getSyntax();

  /** Allows syntactic and semantic handlers to be set simultaneously. */
  public void setHandler(Handler newHandler);

  /************************************************************************
  ** Conversion convenience functions:
  ************************************************************************/

  /** Return the node typed as an ActiveElement, or <code>null</code> if it is
   *	not an Element. */
  public ActiveElement asElement();

  /** Return the node typed as an ActiveAttribute, or <code>null</code> if it
   *	is not an Attribute. */
  public ActiveAttr asAttribute();

  /** Return the node typed as an ActiveEntity, or <code>null</code> if it is
   *	not an Entity. */
  public ActiveEntity asEntity();

  /** Return the node or its content typed as a Namespace. */
  public Namespace asNamespace();

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes are copied, but 
   *	children are not. 
   */
  public ActiveNode shallowCopy();

  /** Return a deep copy of this Node.  Attributes and children are copied.
   */
  public ActiveNode deepCopy();

  /************************************************************************
  ** Construction Convenience Functions:
  ************************************************************************/

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild);

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString();

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString();

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString();

}
