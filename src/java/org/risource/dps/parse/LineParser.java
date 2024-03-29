////// LineParser.java: parser for text (non-SGML) files
//	$Id: LineParser.java,v 1.4 2001-04-03 00:04:46 steve Exp $

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


package org.risource.dps.parse;

import org.w3c.dom.Node;

import org.risource.dps.Parser;
import org.risource.dps.Syntax;
import org.risource.dps.active.*;
import org.risource.dps.util.Copy;

import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeComment;

import org.risource.dps.Context;


import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * A parser for non-SGML (text) files that splits them into lines and 
 *	optionally adds line-separator elements.
 *
 * <p>	Parsing is controlled by entities defined in the tagset.  If they 
 *	are not specified, reasonable defaults are used that correspond
 *	roughly to Unix shell-script conventions.
 *
 * @version $Id: LineParser.java,v 1.4 2001-04-03 00:04:46 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Parser
 */

public class LineParser extends AbstractParser {

  /************************************************************************
  ** Recognizers:
  ************************************************************************/

  /** Get text starting with <code>last</code>.
   *
   *<p>	We don't have to worry about entities, etc., but we may need to 
   *	provide line markers.  We do it by abusing first and next, with
   *	the line going into first and the line number of the following
   *	line going into next.
   */
  protected ActiveNode getToken() throws IOException {
    if (eatLine()) eatEndOfLine();
    if (buf.length() <= 0) return null;
    next = createActiveElement("line", null, true);
    return createActiveText(buf.toString(), false);
  }


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  protected void initialize() {
    // grab stuff from tagset as needed
    next = createActiveElement("line", null, true);
    super.initialize();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public LineParser() {
    super();
  }

  public LineParser(InputStream in) {
    super(in);
  }

  public LineParser(Reader in) {
    super(in);
  }

}
