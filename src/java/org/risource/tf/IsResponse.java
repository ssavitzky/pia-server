// IsResponse.java
// $Id: IsResponse.java,v 1.3 1999-03-12 19:30:53 steve Exp $

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


 
package org.risource.tf;

import org.risource.ds.UnaryFunctor;
import org.risource.pia.Transaction;

import org.risource.tf.TFComputer;

public final class IsResponse extends TFComputer {

  /**
   * Is this a response transaction
   * @return true if this is a reponse transaction
   */
    public Object  computeFeature(Transaction trans) {
      return trans.isResponse() ? True : False;
    }
}





