////// DiscardOutput
//	DiscardOutput.java,v 1.3 1999/03/01 23:46:31 pgage Exp

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


package crc.dps.output;

import crc.dom.*;
import crc.dps.*;
import crc.dps.util.Log;

import java.io.PrintStream;

/**
 * An Output that discards all output.
 *
 *	Slightly more efficient than a Proxy with no target.  The real
 *	benefit is better documentation of the programmer's intent.
 *
 * @version DiscardOutput.java,v 1.3 1999/03/01 23:46:31 pgage Exp
 * @author steve@rsv.ricoh.com 
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
  public void startElement(Element anElement) {
    depth++;
  }
  public boolean endElement(boolean optional) {
    depth --;
    return depth >= 0;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public DiscardOutput() {
  }

}
