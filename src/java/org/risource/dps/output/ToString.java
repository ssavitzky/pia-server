////// ToString.java: Token output Stream to String
//	$Id: ToString.java,v 1.3 1999-03-12 19:27:11 steve Exp $

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


package org.risource.dps.output;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dom.*;
import org.risource.dps.NodeType;
import org.risource.dps.active.ActiveEntity;

import java.util.NoSuchElementException;

/**
 * Output a Token stream to a String <em>in external form</em>. <p>
 *
 * @version $Id: ToString.java,v 1.3 1999-03-12 19:27:11 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Output
 * @see org.risource.dps.output.ToCharData
 */

public class ToString extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected String destination = "";
  protected boolean expandEntities = false;
  protected EntityTable entityTable = null;

  public final String getString() { return destination; }

  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected final void write(String s) {
    destination += s;
  }

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
    if (aNode.getNodeType() == NodeType.ENTITY && expandEntities) {
      ActiveEntity e = (ActiveEntity)aNode;
      // === Should really check value in the entity itself as well ===
      NodeList value = entityTable.getEntityValue(null, e.getName());
      if (value != null) write(value.toString());
      else write(aNode.toString());
    } else {
      write(aNode.toString());
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output. */
  public ToString() {}

  /** Construct an Output, specifying an entity table for expansion. */
  public ToString(EntityTable ents) {
    entityTable = ents;
    expandEntities = true;
  }
}
