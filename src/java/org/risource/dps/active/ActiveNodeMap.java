////// ActiveNodeMap.java: Active Attribute List interface
//	$Id: ActiveNodeMap.java,v 1.4 2001-04-03 00:04:16 steve Exp $

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


package org.risource.dps.active;

import org.w3c.dom.*;
import org.risource.dps.Input;
import java.util.Enumeration;

/**
 * A DOM NamedNodeMap that includes additional convenience functions.
 *
 * <p>	In addition to the usual functions of a NamedNodeMap, an 
 *	ActiveNodeMap can associate unnamed nodes with arbitrary strings. 
 *
 * @version $Id: ActiveNodeMap.java,v 1.4 2001-04-03 00:04:16 steve Exp $
 * @author steve@rii.ricoh.com 
 */

public interface ActiveNodeMap extends NamedNodeMap {

  /************************************************************************
  ** Extensions:
  ************************************************************************/

  /** Associate an arbitrary node with a name. 
   *
   * <p> Unlike a NamedNodeMap, the name need not be the new item's n
   *	 <code>nodeName</code>, although it is in almost all cases.
   */
  public ActiveNode setBinding(String name, ActiveNode item);

  /** Retrieve the ActiveNode associated with (bound to) a name. */
  public ActiveNode getBinding(String name);

  /** Returns the bindings defined in this table, in the same order as the 
   *	names returned by <code>getNames</code>.
   */
  public Input getBindings();

  /** Returns an Enumeration of the names defined in this table, in the same 
   *	order as the bindings returned by <code>getBindings</code>. 
   */
  public Enumeration getNames();

  /************************************************************************
  ** Convenience functions:
  ************************************************************************/

  /** Return the ActiveNodeMap as an ActiveNodeList. 
   * <p>Every ActiveNodeMap trivially implements the NodeList interface, but 
   *	the ActiveNodeMap interface cannot extend <em>both</em> NodeList and
   *	NamedNodeMap because that introduces ambiguities.
   */
  public ActiveNodeList asNodeList();

  /** Associate an arbitrary node with a string. */
  public Node setNamedItem(String name, Node item);

  /** Convenience function: get an item by name and return its value. */
  public String getItemValue(String name);

  /** Convenience function: get an Item by name and return its value
   *	as a boolean
   */
  public boolean hasTrueItem(String name);

}
