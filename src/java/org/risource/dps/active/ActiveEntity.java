////// ActiveEntity.java: Active Entity node (parse tree element) interface
//	$Id: ActiveEntity.java,v 1.4 1999-03-31 23:08:15 steve Exp $

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


package org.risource.dps.active;
import org.risource.dom.Node;
import org.risource.dom.NodeList;
import org.risource.dom.Entity;

import org.risource.dps.Input;
import org.risource.dps.Output;
import org.risource.dps.Context;

/**
 * A DOM Entity node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id: ActiveEntity.java,v 1.4 1999-03-31 23:08:15 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.Active
 * @see org.risource.dps.ActiveNode
 * @see org.risource.dps.Action
 * @see org.risource.dps.Syntax
 * @see org.risource.dps.Processor
 */

public interface ActiveEntity extends Entity, ActiveNode {

  public NodeList getValueNodes(Context cxt);
  public void setValueNodes(Context cxt, NodeList value); 

  /** Get the node's value as an Input. */
  public Input getValueInput(Context cxt);

  /** Get an Output that writes into the node's value. */
  public Output getValueOutput(Context cxt);

  public boolean getIsAssigned();
  public void setIsAssigned(boolean value);
}
