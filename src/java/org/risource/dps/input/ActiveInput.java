////// AbstractInput.java: Input abstract base class
//	$Id: ActiveInput.java,v 1.7 1999-09-22 00:31:43 steve Exp $

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


package org.risource.dps.input;

import org.w3c.dom.Node;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.active.*;


/**
 * An abstract base class for implementations of the Input interface
 *	that operate on ActiveNode objects.<p>
 *
 *	The assumption that an ActiveInput is restricted to parse trees
 *	makes for a considerable gain in efficiency. <p>
 *
 * @version $Id: ActiveInput.java,v 1.7 1999-09-22 00:31:43 steve Exp $
 * @author steve@rsv.ricoh.com
 * 
 * @see org.risource.dps.Processor
 */

public abstract class ActiveInput extends CurrentActive implements Input {
  public boolean toParent() 		{ return super.toParent(); }
  public boolean toNext() 		{ return super.toNext(); }
  public boolean toFirstChild()		{ return super.toFirstChild(); }
  public boolean atFirst() 		{ return super.atFirst(); }
  public boolean atLast() 		{ return super.atLast(); }
  public boolean hasChildren() 		{ return super.hasChildren(); }
  public boolean hasAttributes() 	{ return super.hasAttributes(); }
  public boolean toFirst() 		{ return super.toFirst(); }
  public ActiveInput(Tagset ts) 	{ super(ts); }
  public ActiveInput() 			{ super(null); }

  public void close() 			{}
}
