////// DiscardOutput
//	$Id: DiscardOutput.java,v 1.8 2001-04-03 00:04:40 steve Exp $

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


package org.risource.dps.output;

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.util.Log;

import java.io.PrintStream;

/**
 * An Output that discards all output.
 *
 *	Slightly more efficient than a Proxy with no target.  The real
 *	benefit is better documentation of the programmer's intent.
 *
 * @version $Id: DiscardOutput.java,v 1.8 2001-04-03 00:04:40 steve Exp $
 * @author steve@rii.ricoh.com 
 */

public class DiscardOutput implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int depth		= 0;

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public void putNode(Node aNode) { 
  }
  public void startNode(Node aNode) { 
    depth++;
  }
  public boolean endNode() { 
    depth --;
    return depth >= 0;;
  }
  public void startElement(String tagname, NamedNodeMap attrs) {
    depth++;
  }
  public boolean endElement(boolean optional) {
    depth --;
    return depth >= 0;
  }

  public void putNewNode(short nodeType, String nodeName, String value) {
  }
  public void startNewNode(short nodeType, String nodeName) {
    depth++;
  }
  public void putCharData(short nodeType, String nodeName,
			  char[] buffer, int start, int length) {
  }

  public void close() {}

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public DiscardOutput() {
  }

}
