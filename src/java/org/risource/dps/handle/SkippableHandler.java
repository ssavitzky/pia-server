////// SkippableHandler.java: Skippable Node Handler implementation
//	$Id: SkippableHandler.java,v 1.6 1999-06-25 00:41:23 steve Exp $

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


package org.risource.dps.handle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;

import org.risource.ds.Table;

/**
 * Handler for skippable nodes. <p>
 *
 *	No processing is done for the node or its children -- they 
 *	just disappear. <p>
 *
 * @version $Id: SkippableHandler.java,v 1.6 1999-06-25 00:41:23 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.handle.GenericHandler
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.tagset.BasicTagset
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Output
 */

public class SkippableHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default SkippableHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final SkippableHandler DEFAULT  = new SkippableHandler();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the skippable is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int getActionCode() {
    return Action.ACTIVE_NODE;
  }

  /** Process the content, but do nothing with the node itself. */
  public void action(Input in, Context aContext, Output out) {
    skipNode(in, out);
  }

  static final void skipNode(Input in, Output out) {
    Node n = in.getNode();
    if (in.hasChildren() && ! n.hasChildNodes()) {
      skipChildren(in, out);
    }
  }

  /** Copy the children of the input's current Node
   */
  static final void skipChildren(Input in, Output out) {
    if (! in.toFirstChild()) return;
    do {
      skipNode(in, out);
    } while (in.toNext());
    in.toParent();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SkippableHandler() {}

}

