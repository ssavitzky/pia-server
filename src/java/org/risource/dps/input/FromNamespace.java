////// FromNamespace.java: Input from NodeList
//	$Id: FromNamespace.java,v 1.3 2001-01-11 23:37:26 steve Exp $

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


package org.risource.dps.input;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Input from an Namespace.
 *
 * <p>	The Namespace's bindings are returned in exactly the same order as the 
 *	Namespace's list of item names (obtained by its <code>getNames</code>
 *	method.
 *
 * @version $Id: FromNamespace.java,v 1.3 2001-01-11 23:37:26 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Namespace
 */

public class FromNamespace extends ActiveInput implements Input {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Enumeration names;
  protected Namespace namespace;

  /************************************************************************
  ** Overridden Methods:
  ************************************************************************/

  public boolean toNext() {
    if (depth > 0) return super.toNext();
    if (names == null) return false;
    if (!names.hasMoreElements()) { names = null; active = null; return false; }
    atFirst = (active == null);
    String name = names.nextElement().toString();
    setNode(namespace.getBinding(name));
    return active != null;
  }

  /************************************************************************
  ** Local Methods:
  ************************************************************************/

  public boolean toFirst() {
    names = namespace.getNames();
    atFirst = true;
    active = null;
    return toNext();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FromNamespace(Namespace ns) {
    namespace = ns;
    toFirst();
  }
}
