////// Namespace.java: Node Handler Lookup Table interface
//	$Id: Namespace.java,v 1.5 1999-04-17 01:18:52 steve Exp $

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

import org.risource.dps.active.*;

import java.util.Enumeration;

/**
 * The interface for a Namespace -- a lookup table for nodes.
 *
 * <p>	Note that a Namespace might be either a Node (e.g. BasicEntityTable)
 *	or a NamedNodeMap, or something else entirely.  As long as it maps
 *	names to values, we don't care.  Each value is contained in the value
 *	and/or children of some Node, called its <em>binding</em>.
 *
 * <p>	A Namespace is normally accessed through a name that ends in a 
 *	colon character.  It is not required to ``know'' its own name,
 *	however; it may simply be the value of that name in another 
 *	Namespace, or even <em>contained in</em> some name's value.
 *
 * @version $Id: Namespace.java,v 1.5 1999-04-17 01:18:52 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Input
 * @see org.risource.dps.active.ActiveNodeMap
 */

public interface Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and get a binding (node). */
  public ActiveNode getBinding(String name);

  /** Add a new binding or replace an existing one.  Returns the old binding,
   *	if any.  Removes existing binding if the new binding is
   *	<code>null</code>
   */
  public ActiveNode setBinding(String name, ActiveNode binding);

  /************************************************************************
  ** Information Operations:
  ************************************************************************/

  /** Returns the name of this namespace. */
  public String getName();

  /** Returns the bindings defined in this table, in the same order as the 
   *	names returned by <code>getNames</code>. */
  public Input getBindings();

  /** Returns an Enumeration of the names defined in this table, in the same 
   *	order as the bindings returned by <code>getBindings</code>. 
   */
  public Enumeration getNames();

  /** Returns <code>true</code> if the Namespace is case-sensitive. */
  public boolean isCaseSensitive();

  /** Convert a name to cannonical case. */
  public String cannonizeName(String name);

  /** Returns <code>true</code> if any of the bindings in the Namespace 
   *	implement the Namespace interface themselves. */
  public boolean containsNamespaces();
}
