////// Parser.java: Parser interface
//	$Id: Parser.java,v 1.7 2001-04-03 00:04:12 steve Exp $

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

import org.risource.dps.active.*;
import java.io.Reader;

/**
 * The interface for an Input that converts a character stream (Reader) into 
 *	a ``virtual parse tree''.  <p>
 *
 *	Being a ProcessorInput, the Parser gets all of the syntactic
 *	information and parse-stack state it needs from the Processor.
 *
 * @version $Id: Parser.java,v 1.7 2001-04-03 00:04:12 steve Exp $
 * @author steve@rii.ricoh.com */

public interface Parser extends ProcessorInput {

  /************************************************************************
  ** Reader Access:
  ************************************************************************/

  /** Returns the Reader from which this Parser is obtaining input. */
  public Reader getReader();

  /** Sets the Reader from which this Parser will obtain input. */
  public void setReader(Reader aReader);

  /** Returns the top-level Document node under construction. */
  public ActiveNode getDocument();

  /** Sets the top-level Document node.
   *	Note that this also sets retainTree.  
   *
   * <p> In future implementations this should be an ActiveDocument node,
   *	 which would let us use the Document's factory methods. 
   */
  public void setDocument(ActiveNode aDocument);

  /** Returns the Parser's location in the current document, in a format
   *	suitable for use in error messages.
   */
  public String location();
}
