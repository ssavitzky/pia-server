////// EntityRefHandler.java: Entity Reference Node Handler implementation
//	$Id: EntityRefHandler.java,v 1.4 1999-06-25 00:41:18 steve Exp $

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

import org.risource.ds.Table;

/**
 * Handler for Entity Reference nodes. <p>
 *
 *	<p>
 *
 * @version $Id: EntityRefHandler.java,v 1.4 1999-06-25 00:41:18 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.handle.GenericHandler
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.tagset.BasicTagset
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Output
 */

public class EntityRefHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default EntityHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final EntityRefHandler DEFAULT  = new EntityRefHandler(true, 0);

  /** An EntityRefHandler for active, indexed entities. */
  public static final EntityRefHandler INDEXED = new EntityRefHandler(true, 1);

  /** An EntityRefHandler for active, non-indexed entities. */
  public static final EntityRefHandler ACTIVE  = new EntityRefHandler(true, -1);

  /** An EntityRefHandler for passive entities, which should never be 
   *	replaced by their values during processing.
   */
  public static final EntityRefHandler PASSIVE = new EntityRefHandler(false, 0);

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean active = true;
  protected boolean simple  = false;
  protected boolean indexed = false;

  protected String  namespace = null;
  protected String  namepart  = null;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the entity is active. 
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
    ActiveEntityRef n = (ActiveEntityRef)in.getActive();
    if (!active) {
      out.putNode(n);
      return;
    }
    String name = n.getNodeName();
    ActiveNodeList value;
    if (simple) {
      value = (namepart != null)
	? aContext.getValueNodes(namepart, false)
	: aContext.getValueNodes(name, false);
    } else if (indexed) {
      value = (namepart != null) 
	? Index.getValue(aContext, namespace, namepart)
	: Index.getIndexValue(aContext, name);
    } else if (name.indexOf(':') >= 0) {
      value = Index.getIndexValue(aContext, name);
    } else {
      value = aContext.getValueNodes(name, false);
    }

    //aContext.debug("&" + name + "; => " + value + "\n");
    if (value == null) {
      out.putNode(n);
    } else {
      Copy.copyNodes(value, out);
    }
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine the correct Handler for a given Node.
   *	Does dispatching on the name to determine which of the canned
   *	handlers to return.
   *
   *	Eventually this needs to check for character entities and return
   *	PASSIVE for them.
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveEntityRef ent = (ActiveEntityRef)n;
    String name = ent.getNodeName();
    // === An indexed name really wants a new handler with namepart and space
    // === fields defined, but the action method is clever enough to check.
    return (name.indexOf(':') >= 0)? INDEXED : ACTIVE;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public EntityRefHandler() {}

  /** Construct an EntityRefHandler
   *
   * @param active <code>true</code> (default) if the EntityRef should ever be
   *	expanded, <code>false</code> otherwise.
   * @param indexed <code>0</code> (default) if we have to check the name for
   *	periods at run-time; <code>1</code> if the name is an index; 
   *	<code>-1</code> if the name is not an index.
   */
  public EntityRefHandler(boolean active, int syntax) {
    this.active  = active;
    this.indexed = syntax > 0;
    this.simple  = syntax < 0;
  }
  /** Construct an EntityRefHandler
   *
   * @param active <code>true</code> (default) if the EntityRef should ever be
   *	expanded, <code>false</code> otherwise.
   * @param name the name to look up
   * @param namespace the (name of the) namespace to look it up in.
   */
  public EntityRefHandler(boolean active, String name, String namespace) {
    this.active    = active;
    this.namepart  = name;
    this.namespace = namespace;
    this.indexed   = namespace != null;
    this.simple    = namespace == null;
  }

}

