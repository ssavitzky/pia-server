////// ActiveValue.java: Active Value (parse tree element) interface
//	$Id: ActiveValue.java,v 1.4 2001-04-03 00:04:16 steve Exp $

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


package org.risource.dps.active;
import org.w3c.dom.*;

import org.risource.dps.Input;
import org.risource.dps.Context;

/**
 * An ActiveNode which has a ``value'' that can be set.
 *
 * @version $Id: ActiveValue.java,v 1.4 2001-04-03 00:04:16 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Active
 * @see org.risource.dps.Action
 * @see org.risource.dps.Syntax
 * @see org.risource.dps.Processor
 */

public interface ActiveValue extends ActiveNode {

  public void setValueNodes(Context cxt, ActiveNodeList v);
  public ActiveNodeList getValueNodes();

  public boolean getIsAssigned();
  public void setIsAssigned(boolean value);

}
