// IsRequest.java
// IsRequest.java,v 1.5 1999/03/01 23:48:22 pgage Exp

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


 
package crc.tf;

import crc.pia.Transaction;
import crc.tf.TFComputer;

public final class IsRequest extends TFComputer {

  /**
   * Is this a request transaction
   * @param o Transaction 
   * @return true if this transaction is a request
   */
    public Object  computeFeature(Transaction trans) {
      return trans.isRequest() ? True : False;
    }
}

