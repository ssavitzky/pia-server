////// ActiveAttrList.java: Active Attribute List interface
//	$Id: ActiveAttrList.java,v 1.4 1999-04-07 23:20:53 steve Exp $

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
 * A DOM NamedNodeMap specialized as an attribute list.
 *
 * @version $Id: ActiveAttrList.java,v 1.4 1999-04-07 23:20:53 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public interface ActiveAttrList extends ActiveNodeMap  {

  /************************************************************************
  ** Attribute convenience functions:
  ************************************************************************/

  /** Convenience function: get an Attribute by name and return it. */
  public ActiveAttr getActiveAttr(String name);

  /** Convenience function: get an Attribute by name and return its value. */
  public String getAttribute(String name);

  /** Convenience function: get an Attribute by name and return its value. */
  public ActiveNodeList getAttributeValue(String name);

  /** Convenience function: get an Attribute by name and return its value
   *	as a boolean
   */
  public boolean hasTrueAttribute(String name);

  /** Convenience function: Set an attribute's value to a Node. */
  public void setAttributeValue(String name, ActiveNode value);

  /** Convenience function: Set an attribute's value to a NodeList. */
  public void setAttributeValue(String name, ActiveNodeList value);

  /** Convenience function: Set an attribute's value to a String. */
  public void setAttribute(String name, String value);

  /** Convenience function: Set an attribute's value to a NodeList,
   *	given the optimistic assumption that the attribute is currently
   *	undefined.
   */
  public void addAttribute(String name, ActiveNodeList value);

}
