////// ActiveAttribute.java: Active Attribute (parse tree element) interface
//	$Id: ActiveAttribute.java,v 1.3 1999-03-12 19:25:14 steve Exp $

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
import org.risource.dom.Attribute;

import org.risource.dps.Action;
import org.risource.dps.Syntax;
import org.risource.dps.Handler;

/**
 * A DOM Attribute node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id: ActiveAttribute.java,v 1.3 1999-03-12 19:25:14 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dom.Node
 * @see org.risource.dps.Active
 * @see org.risource.dps.ActiveNode
 * @see org.risource.dps.Action
 * @see org.risource.dps.Syntax
 * @see org.risource.dps.Processor
 */

public interface ActiveAttribute extends Attribute, ActiveNode {

}
