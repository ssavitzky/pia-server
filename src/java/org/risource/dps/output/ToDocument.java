////// ToDocument.java: Token output Stream to Document
//	$Id: ToDocument.java,v 1.3 1999-03-12 19:27:06 steve Exp $

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

import org.risource.dom.Node;
import org.risource.dom.Document;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The basic implementation for a consumer of Token objects.<p>
 *
 *
 * @version $Id: ToDocument.java,v 1.3 1999-03-12 19:27:06 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Token
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
    root = doc;
  }

}
