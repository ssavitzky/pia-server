////// BasicEntityTable.java: Node Handler Lookup Table
//	$Id: BasicEntityTable.java,v 1.4 2001-04-03 00:04:37 steve Exp $

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

package org.risource.dps.namespace;

import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.*;

import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;

/**
 * The basic implementation for a EntityTable -- a lookup table for entities. 
 *
 *	This implementation is represented as an Element; the bindings
 *	are kept in its children.  <p>
 *
 * @version $Id: BasicEntityTable.java,v 1.4 2001-04-03 00:04:37 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Input 
 */

public class BasicEntityTable extends BasicNamespace implements EntityTable {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name. 
   */
  public ActiveNodeList getValueNodes(String name) {
    ActiveValue binding = (ActiveValue)getBinding(name);
    return (binding != null)? binding.getValueNodes() :  null;
  }

  /** Set the value for a given name.
   *
   * <p> This is a convenience function that is more efficient when the
   *	 caller is setting up multiple names in an empty namespace, and
   *	 is in a position to cache the Tagset.
   */
  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value, Tagset ts) {
    ActiveNode binding = getBinding(name);
    if (binding != null) {
      binding.setValueNodes(cxt, value); 
      return binding;
    } else {
      return newBinding(name, value, ts);
    } 
  }

  /** Set the value for a given name. */
  public ActiveNode setValueNodes(Context cxt, String name,
				  ActiveNodeList value) {
    ActiveNode binding = getBinding(name);
    if (binding != null) {
      binding.setValueNodes(cxt, value); 
    } else {
      binding = newBinding(name, value, cxt.getTopContext().getTagset());
    } 
    return binding;
  }

  /** Construct a new local binding. */
  protected ActiveEntity newBinding(String name, ActiveNodeList value,
				    Tagset ts) {
    ActiveEntity binding = ts.createActiveEntity(name, value);
    addBinding(name, binding);
    return binding;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicEntityTable() { super("ENTITIES"); }
  public BasicEntityTable(String name) {
    super(name); 
  }

}
