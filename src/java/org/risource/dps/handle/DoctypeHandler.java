////// DoctypeHandler.java: Doctype Node Handler implementation
//	$Id: DoctypeHandler.java,v 1.1 1999-04-07 23:21:19 steve Exp $

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
import org.risource.dps.util.Index;
import org.risource.dps.util.Copy;
import org.risource.dps.tree.TreeDocType;

import org.risource.ds.Table;

/**
 * Handler for DocumentType nodes. <p>
 *
 *	<p>
 *
 * @version $Id: DoctypeHandler.java,v 1.1 1999-04-07 23:21:19 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.handle.GenericHandler
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.BasicTagset
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Output
 */

public class DoctypeHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default DoctypeHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final DoctypeHandler DEFAULT  = new DoctypeHandler();


  /************************************************************************
  ** State:
  ************************************************************************/

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the entity is active. 
   */
  public int actionCode(Input in, Processor p) {
    return Action.PUT_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   */
  public void action(Input in, Context aContext, Output out) {
    Copy.copyNode(null, in, out);
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine the correct Handler for a given Node.
   */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  public ActiveNode createNode(short nodeType, String name, String data) {
    return new TreeDocType(null, data);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public DoctypeHandler() {}

}

