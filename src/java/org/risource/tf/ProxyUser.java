// ProxyUser.java
// $Id: ProxyUser.java,v 1.1 1999-06-11 23:44:50 wolff Exp $

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
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import java.net.URL;


import org.risource.tf.TFComputer;

public final class ProxyUser extends TFComputer {

  /**
   * Returns the value of the ProxyUser field of the request (either one set by the 
   * proxy authentication code, or whatever the browser sends)
   */
  public Object computeFeature(Transaction trans) {
    Transaction req=trans.requestTran();
    
    if(req.hasHeader("AuthenticatedProxyUser")) {
      return req.header("AuthenticatedProxyUser");
    }

    if(req.hasHeader("proxy-authorization")) {
      String auth = req.header("proxy-authorization");
      int i = auth.indexOf(':');
      String user;
      if(i>0) {
        user = auth.substring(0,i);
      } else {
	user = auth;
      }
      return user;
    }
    return "";

    }
}


