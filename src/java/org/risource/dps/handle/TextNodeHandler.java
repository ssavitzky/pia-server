////// TextNodeHandler.java: Text Node Handler implementation
//	$Id: TextNodeHandler.java,v 1.9 2001-04-03 00:04:21 steve Exp $

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


package org.risource.dps.handle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.Index;
import org.risource.dps.util.Copy;

import org.risource.ds.Table;

/**
 * Handler for active or passive Text nodes. <p>
 *
 *	<p>
 *
 * @version $Id: TextNodeHandler.java,v 1.9 2001-04-03 00:04:21 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.handle.GenericHandler
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.tagset.BasicTagset
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Output
 */

public class TextNodeHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default TextNodeHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final TextNodeHandler DEFAULT  = new TextNodeHandler(false);

  /** An TextNodeHandler for active entities. */
  public static final TextNodeHandler ACTIVE  = new TextNodeHandler(true);

  /** An TextNodeHandler for passive entities, which should never be 
   *	replaced by their values during processing.
   */
  public static final TextNodeHandler PASSIVE = new TextNodeHandler(false);

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean active = false;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the text is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int getActionCode() {
    if (active) {
      return Action.ACTIVE_NODE;
    } else return Action.PUT_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   *	=== eventually this should use <code>getValue(node, context)</code>.
   */
  public void action(Input in, Context aContext, Output out) {
    ActiveText n = (ActiveText)in.getActive();
    if (!active) {
      out.putNode(n);
    } else {
      // === This is actually an error at the moment. ===
      aContext.message(-1, "Active text with no action defined.", 0, true); 
      out.putNode(n);
    }
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine the correct Handler for a given Node
   *	The default action is to return <code>this</code>.
   */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TextNodeHandler() {}

  /** Construct a TextNodeHandler
   *
   * @param active <code>true</code> (default) if the text should ever be
   *	expanded, <code>false</code> otherwise.
   */
  public TextNodeHandler(boolean active) {
    this.active = active;
  }

}

