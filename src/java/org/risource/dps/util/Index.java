////// Index.java: Utilities for handling index expressions
//	$Id: Index.java,v 1.4 1999-04-07 23:22:17 steve Exp $

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

import org.risource.dps.Context;
import org.risource.dps.EntityTable;
import org.risource.dps.Namespace;
import org.risource.dps.Tagset;
import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.tree.TreeNodeList;

import org.risource.ds.Table;
import org.risource.ds.Association;

import java.util.Enumeration;

/**
 * Index Expression Utilities.
 *
 * @version $Id: Index.java,v 1.4 1999-04-07 23:22:17 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 */
public class Index {

  /************************************************************************
  ** Front End:
  ************************************************************************/

  /** Get a value using an index. */
  public static ActiveNodeList getIndexValue(Context c, String index) {
    int i = index.indexOf(':');
    if (i == index.length() -1) {
      return getValue(c, index.substring(0, i), null);
    } else if (i >= 0) {
      return getValue(c, index.substring(0, i), index.substring(i+1));
    } else {
      return c.getEntityValue(index, false);
    }
  }

  public static void setIndexValue(Context c, String index,
				   ActiveNodeList value) {
    int i = index.indexOf(':');
    if (i == index.length() -1) {
      setValue(c, index.substring(0, i), null, value);
    } else if (i >= 0) {
      setValue(c, index.substring(0, i), index.substring(i+1), value);
    } else {
      c.setEntityValue(index, value, false);
    }
  }

  /** Get a value using a name and namespace. 
   *
   * @param c the context in which to do the lookup.
   * @param space the name of the namespace (ending with colon!)
   * @param name  the name within the namespace.  If name is null,
   *	the entire namespace is returned.
   */
  public static ActiveNodeList getValue(Context c, String space, String name) {
    Namespace ns = c.getNamespace(space);

    // If there's nothing there, return null.
    if (ns == null) return null;

    // If we wanted the whole space, return its list of bindings.
    if (name == null) return new TreeNodeList(ns.getBindings());
    else return ns.getValueNodes(c, name);
  }

  public static void setValue(Context c, String space, String name,
			      ActiveNodeList value) {
    Namespace ns = c.getNamespace(space);
    Tagset ts = c.getTopContext().getTagset();

    if (ns != null) {
      ns.setValueNodes(c, name, value);
    } else {
      // If there's nothing there, make a namespace and populate it.
      BasicEntityTable ents = new BasicEntityTable(space);
      ents.setEntityValue(c, name, value, ts);
    }
  }

}
