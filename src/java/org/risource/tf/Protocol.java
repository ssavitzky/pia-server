// Protocol.java
// $Id: Protocol.java,v 1.2 2001-04-03 00:05:31 steve Exp $

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


 
package org.risource.tf;

import org.risource.ds.UnaryFunctor;
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import java.net.URL;


import org.risource.tf.TFComputer;

public final class Protocol extends TFComputer {

  /**
   * Returns the value of the protocol field of the request e.g. http
   */
  public Object computeFeature(Transaction trans) {
    Transaction req=trans.requestTran();
    if(req != null) return req.protocol();

    //if req is null
    return "";
    
  }
  
}


