////// ActiveNodeMap.java: Active Attribute List interface
//	$Id: ActiveNodeMap.java,v 1.1 1999-04-07 23:20:58 steve Exp $

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

/**
 * A DOM NamedNodeMap that includes additional convenience functions.
 *
 * <p>	In addition to the usual functions of a NamedNodeMap, an 
 *	ActiveNodeMap can associate unnamed nodes with arbitrary strings. 
 *
 * @version $Id: ActiveNodeMap.java,v 1.1 1999-04-07 23:20:58 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public interface ActiveNodeMap extends NamedNodeMap {

  /************************************************************************
  ** Extensions:
  ************************************************************************/

  /** Associate an arbitrary node with a string. */
  public ActiveNode setActiveItem(String name, ActiveNode item);

  public ActiveNode getActiveItem(String name);

  /************************************************************************
  ** Convenience functions:
  ************************************************************************/

  /** Associate an arbitrary node with a string. */
  public Node setNamedItem(String name, Node item);

  /** Convenience function: get an item by name and return its value. */
  public String getItemValue(String name);

  /** Convenience function: get an Item by name and return its value
   *	as a boolean
   */
  public boolean hasTrueItem(String name);

}
