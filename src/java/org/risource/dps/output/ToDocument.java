////// ToDocument.java: Token output Stream to Document
//	ToDocument.java,v 1.6 1999/03/01 23:46:35 pgage Exp

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


package crc.dps.output;

import crc.dps.Output;

import crc.dom.Node;
import crc.dom.Document;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The basic implementation for a consumer of Token objects.<p>
 *
 *
 * @version ToDocument.java,v 1.6 1999/03/01 23:46:35 pgage Exp
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
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
