// DPSException.java
// $Id: DPSException.java,v 1.1 1999-04-07 23:21:00 steve Exp $

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

/** This is the implementation of <code>org.w3c.dom.DOMException</code>.
 *	it includes a few locally-defined exception codes, and has
 *	a constructor that requires only the code. 
 */
public class DPSException extends org.w3c.dom.DOMException {
  protected short code;

  public static final short NOT_ACTIVE_NODE_ERR = 11;

  protected static String messages[] = {
    "INDEX_SIZE_ERR",
    "DOMSTRING_SIZE_ERR",
    "HIERARCHY_REQUEST_ERR",
    "WRONG_DOCUMENT_ERR",
    "INVALID_CHARACTER_ERR",
    "NO_DATA_ALLOWED_ERR",
    "NO_MODIFICATION_ALLOWED_ERR",
    "NOT_FOUND_ERR",
    "NOT_SUPPORTED_ERR",
    "INUSE_ATTRIBUTE_ERR",
    "NOT_ACTIVE_NODE_ERR",
  };

  public DPSException(short code, String message){
    super(code, message);
  }
  public DPSException(short code){
    super(code, messages[code-1]);
  }
};
