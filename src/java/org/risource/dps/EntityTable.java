////// EntityTable.java: Entity Lookup Table interface
//	$Id: EntityTable.java,v 1.3 1999-03-12 19:24:49 steve Exp $

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

import org.risource.dps.active.ActiveEntity;

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;

import java.util.Enumeration;

/**
 * The interface for a EntityTable -- a lookup table for values. 
 *
 *	Entities are named values that can be substituted into a document.
 *
 * @version $Id: EntityTable.java,v 1.3 1999-03-12 19:24:49 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dom.Entity
 * @see org.risource.dps.active.ActiveEntity
 * @see org.risource.dps.util.BasicEntityTable
 */

public interface EntityTable extends Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return the corresponding Entity. */
  public ActiveEntity getEntityBinding(String name);

  public NodeList getEntityValue(Context cxt, String name);

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames();

}
