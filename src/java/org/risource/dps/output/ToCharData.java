////// ToCharData.java:  Output to Character data
//	$Id: ToCharData.java,v 1.9 2001-01-11 23:37:31 steve Exp $

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
import org.w3c.dom.*;
import org.risource.dps.active.ActiveEntityRef;

import java.util.NoSuchElementException;

/**
 * Output to a String in <em>internal</em> form. <p>
 *
 * @version $Id: ToCharData.java,v 1.9 2001-01-11 23:37:31 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Output
 * @see org.risource.dps.output.ToString
 */

public class ToCharData extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected String destination = "";
  protected boolean expandEntities = true;
  protected EntityTable entityTable = TextUtil.getCharacterEntities();

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
    if (aNode.getNodeType() == Node.ENTITY_REFERENCE_NODE && expandEntities) {
      ActiveEntityRef e = (ActiveEntityRef)aNode;
      // === Should really check value in the entity itself as well ===
      Node value = entityTable.getBinding(e.getNodeName());
      if (value != null) write(value.getNodeValue());
      else write(aNode.toString()); // === undef entity is problematic
    } else if (aNode.getNodeType() == Node.TEXT_NODE || 
	       aNode.getNodeType() == Node.CDATA_SECTION_NODE) {
      write(aNode.getNodeValue());
    } else {
      write(aNode.toString());	// === need to recurse here.
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output. */
  public ToCharData() {}

  /** Construct an Output, specifying an entity table for expansion. */
  public ToCharData(EntityTable ents) {
    entityTable = ents;
    expandEntities = true;
  }
}
