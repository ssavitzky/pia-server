////// FromEnumeration.java: Input from NodeList
//	$Id: FromEnumeration.java,v 1.3 2001-01-11 23:37:26 steve Exp $

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
 * @version $Id: FromEnumeration.java,v 1.3 2001-01-11 23:37:26 steve Exp $
 * @author steve@rii.ricoh.com 
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

  public boolean toNext() {
    if (depth > 0) return super.toNext();
    if (enum == null) return false;
    if (!enum.hasMoreElements()) { enum = null; active = null; return false; }
    atFirst = (active == null);
    setNode((ActiveNode)enum.nextElement());
    return active != null;
  }

  /************************************************************************
  ** Local Methods:
  ************************************************************************/

  public boolean toFirst() {
    if (active == null) return (toNext());
    else return atFirst && active != null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FromEnumeration(Enumeration e) {
    toFirst();
  }
}
