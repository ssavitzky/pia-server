////// ActiveDecl.java: Active SGML Declaration interface
//	$Id: ActiveDecl.java,v 1.1 1999-04-07 23:20:55 steve Exp $

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
import org.w3c.dom.*;

import org.risource.dps.Action;
import org.risource.dps.Syntax;
import org.risource.dps.Handler;

/**
 * A DOM Declaration node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version $Id: ActiveDecl.java,v 1.1 1999-04-07 23:20:55 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public interface ActiveDecl extends ActiveNode {
  /** Return the name of the item being declared.
   *	In the DOM, this is returned as the nodeName from DocumentType.
   *	In SGML, this is the word after the ``declaration name'', which in
   *	turn is the first identifier after the ``<code>!</code>'' character.
   *
   * @see #getDeclName
   */
  public String getItemName();

  /** Return the SGML ``declaration name''; the identifier immediately after
   *	the ``<code>&gt;!</code>'' character sequence that starts the 
   *	declaration.
   */
  public String getDeclName();

  /** Return the data of the declaration: everything after the name. */
  public String getData();
  public void setData(String value);
}
