////// ActiveOutput.java:  abstract base class for Output implementations.
//	$Id: ActiveOutput.java,v 1.7 1999-11-06 01:08:13 steve Exp $

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

import org.risource.dps.util.CurrentActive;
import org.risource.dps.*;
import org.w3c.dom.*;

/**
 * An abstract base class for implementations of the Output interface
 *	that operate exclusively on ActiveNode's.<p>
 *
 * @version $Id: ActiveOutput.java,v 1.7 1999-11-06 01:08:13 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Context
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public abstract class ActiveOutput extends CurrentActive implements Output {

  public void putNode(Node aNode) { super.putNode(aNode); }
  public void startNode(Node aNode) { super.startNode(aNode); }
  public boolean endNode() { return super.endNode(); }
  public void startElement(String tagname, NamedNodeMap attrs) {
    super.startElement(tagname, attrs);
  }
  public boolean endElement(boolean optional) {
    return super.endElement(optional);
  }
  public void putNewNode(short nodeType, String nodeName, String value) {
    super.putNewNode(nodeType, nodeName, value);
  }
  public void startNewNode(short nodeType, String nodeName) {
    super.startNewNode(nodeType, nodeName);
  }
  public void putCharData(short nodeType, String nodeName,
			  char[] buffer, int start, int length) {
    super.putCharData(nodeType, nodeName, buffer, start, length);
  }
  public void close() {}

  public ActiveOutput(Tagset ts) { super(ts); }
}
