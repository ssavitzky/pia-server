////// EntityTable.java: Entity Lookup Table interface
//	$Id: EntityTable.java,v 1.5 1999-04-17 01:18:50 steve Exp $

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
import org.risource.dps.active.ActiveNodeList;

import java.util.Enumeration;

/**
 * The interface for a EntityTable -- a lookup table for values. 
 *
 *	Entities are named values that can be substituted into a document.
 *
 * @version $Id: EntityTable.java,v 1.5 1999-04-17 01:18:50 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.active.ActiveEntity
 * @see org.risource.dps.util.BasicEntityTable
 */

public interface EntityTable extends Namespace {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return the corresponding Entity. */
  public ActiveEntity getEntityBinding(String name);

  /** Look up a name and return its value. 
   *	No context is required, because we can guarantee that the table
   *	contains only Entity nodes, which have values.
   */
  public ActiveNodeList getValueNodes(String name);

  /** Set a value.  
   *	A context is required because we may need its tagset
   *	in order to construct a new binding.
   */
  public void setValueNodes(Context cxt, String name, ActiveNodeList value);

}
