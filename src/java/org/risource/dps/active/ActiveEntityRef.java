////// ActiveEntityRef.java: Active Entity Reference
//	$Id: ActiveEntityRef.java,v 1.4 2001-04-03 00:04:15 steve Exp $

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
import org.risource.dps.Output;
import org.risource.dps.Context;

/**
 * A DOM EntityReference node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id: ActiveEntityRef.java,v 1.4 2001-04-03 00:04:15 steve Exp $
 * @author steve@rii.ricoh.com 
 */

public interface ActiveEntityRef extends ActiveNode, EntityReference {
}
