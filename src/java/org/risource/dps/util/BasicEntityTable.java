////// BasicEntityTable.java: Node Handler Lookup Table
//	BasicEntityTable.java,v 1.10 1999/03/01 23:46:50 pgage Exp

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

package org.risource.dps.util;

import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;

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
 * @version BasicEntityTable.java,v 1.10 1999/03/01 23:46:50 pgage Exp
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Token
 * @see org.risource.dps.Input 
 * @see org.risource.dom.Node 
 * @see org.risource.dom.Attribute
 */

public class BasicEntityTable extends BasicNamespace implements EntityTable {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Return the value for a given name.  Performs recursive lookup in the
   *	context if necessary. 
   */
  public NodeList getEntityValue(Context cxt, String name) {
    ActiveEntity binding = getEntityBinding(name);
    return (binding != null)? binding.getValueNodes(cxt) :  null;
  }

  /** Set the value for a given name.
   */
  public void setEntityValue(Context cxt, String name,
			     NodeList value, Tagset ts) {
    ActiveEntity binding = getEntityBinding(name);
    if (binding != null) {
      binding.setValueNodes(cxt, value); 
    } else {
      newBinding(name, value, ts);
    } 
  }

  /** Look up a name and get a binding. */
  public ActiveEntity getEntityBinding(String name) {
    ActiveNode n = getBinding(name);
    return (n == null)? null : n.asEntity();
  }

  /** Construct a new local binding. */
  protected void newBinding(String name, NodeList value, Tagset ts) {
    addBinding(name, ts.createActiveEntity(name, value));
  }


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the entity names defined in this table. 
   */
  public Enumeration entityNames() { 
    return getNames();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicEntityTable() { super(); }
  public BasicEntityTable(String name) {
    super(name); 
  }

}
