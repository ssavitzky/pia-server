////// ToDocument.java:  Output to Document
//	$Id: ToDocument.java,v 1.6 1999-07-14 20:20:43 steve Exp $

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


package org.risource.dps.output;

import org.risource.dps.Output;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Output to a Document, using the target document's tagset.<p>
 *	=== At the moment this is completely unimplemented!
 *
 * @version $Id: ToDocument.java,v 1.6 1999-07-14 20:20:43 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Input
 * @see org.risource.dps.Processor
 */

public class ToDocument extends ActiveOutput {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Node current = null;
  protected Document root = null;


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Output tokens to a given Document. */
  public ToDocument(Document doc) {
    super(null);		// === bogus ===
    root = doc;
  }

}
