////// Index.java: Utilities for handling index expressions
//	$Id: Index.java,v 1.8 1999-10-14 23:55:59 steve Exp $

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
import org.risource.dps.namespace.BasicNamespace;
import org.risource.dps.namespace.BasicEntityTable;

import org.risource.ds.Table;
import org.risource.ds.Association;

import java.util.Enumeration;

/**
 * Index Expression Utilities.
 *
 * @version $Id: Index.java,v 1.8 1999-10-14 23:55:59 steve Exp $
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
      return c.getValueNodes(index, false);
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
      c.setValueNodes(index, value, false);
    }
  }

  /** Get a value from a Context using a name and namespace. 
   *
   * @param c the Context in which to do the lookup.
   * @param space the name of the Namespace.
   * @param name  the name within the namespace.  If name is null,
   *	the list of names is returned.
   */
  public static ActiveNodeList getValue(Context c, String space, String name) {
    Namespace ns = c.getNamespace(space);
    if (ns == null) {
      c.message(1, "Namespace " + space + " not found.", 0, true);
      return null;
    }
    return getValue(c, ns, name);
  }

  /** Get a value from a Namespace using a Context and name. 
   *
   * <p> If the name contains a colon, getValue is called recursively.
   *
   * @param c the Context in which to compute the value
   * @param ns the Namespace
   * @param name  the name within the Namespace.  If name is null,
   *	the list of names is returned.
   */
  public static ActiveNodeList getValue(Context c, Namespace ns, String name) {
    if (ns == null) return null;
    if (name == null) {
      return new TreeNodeList(ns.getNames());
    }
    int i = name.indexOf(':');
    if (i < 0) return ns.getValueNodes(c, name);
    String space = name.substring(0, i);
    name = (i == name.length() - 1) ? null : name.substring(i+1);
    ActiveNode n = ns.getBinding(space);
    if (n == null) {
      c.message(2, "Namespace " + space + " not found in "
		+ ns.getName(), 0, true);
      return null;
    }
    ns = n.asNamespace();
    if (ns == null) {
      c.message(-2, "Binding of " + space + " not a namespace in "
		+ ns.getName(), 0, true);
      return null;
    }
    return (ns == null) ? null : getValue(c, ns, name);
  }

  public static void setValue(Context c, String space, String name,
			      ActiveNodeList value) {
    Namespace ns = c.getNamespace(space);

    if (ns != null) {
      //System.err.println("Setting " + name + " in '" + ns.getName() + ":'");
      ns.setValueNodes(c, name, value);
    } else {
      c.message(1, "No namespace found for '" + space + ":'", 0, true);
      // If there's nothing there, make a namespace and populate it.
      BasicEntityTable ents = new BasicEntityTable(space);
      Tagset ts = c.getTopContext().getTagset();
      ents.setValueNodes(c, name, value, ts);
      // === this has a problem: the context doesn't get the namespace.
    }
  }

}
