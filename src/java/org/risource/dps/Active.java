////// Active.java: Interface for things with actions.
//	$Id: Active.java,v 1.6 2001-04-03 00:04:10 steve Exp $

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


package org.risource.dps;

/**
 * Interface for objects that have an Action. <p>
 *
 *	By convention, a class that implements Active, or an interface
 *	that extends it, has the name <code>Active<em>Xxxx</em></code>. <p>
 *
 * @version $Id: Active.java,v 1.6 2001-04-03 00:04:10 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Action
 * @see org.risource.dps.Context
 * @see org.risource.dps.Processor
 */

public interface Active {

  /** Gets the Action for this object.  */
  public Action getAction();

  /** Sets the Action for this object.  May be a no-op if the Active
   *	object is immutable.
   */
  public void setAction(Action newAction);

}
