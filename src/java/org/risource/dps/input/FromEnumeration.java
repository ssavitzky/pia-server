////// FromEnumeration.java: Input from NodeList
//	$Id: FromEnumeration.java,v 1.1 1999-04-07 23:21:32 steve Exp $

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
 * Input from an enumeration of ActiveNodes.<p>
 *
 * @version $Id: FromEnumeration.java,v 1.1 1999-04-07 23:21:32 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class FromEnumeration extends ActiveInput implements Input {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Enumeration enum;

  /************************************************************************
  ** Overridden Methods:
  ************************************************************************/

  public Node toNextSibling() {
    if (depth > 0) return super.toNextSibling();
    if (enum == null) return null;
    if (!enum.hasMoreElements()) { enum = null; active = null; return null; }
    atFirst = (active == null);
    setNode((ActiveNode)enum.nextElement());
    return active;
  }

  /************************************************************************
  ** Local Methods:
  ************************************************************************/

  public Node toFirstNode() {
    if (active == null) return toNextSibling();
    else return atFirst? active : null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FromEnumeration(Enumeration e) {
    toFirstNode();
  }
}
