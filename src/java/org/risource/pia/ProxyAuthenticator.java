// ProxyAuthenticator.java
// $Id: ProxyAuthenticator.java,v 1.3 2001-04-03 00:05:14 steve Exp $


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

package org.risource.pia;

import org.risource.pia.Authenticator;
import org.risource.pia.Transaction;
import org.risource.pia.HTTPRequest;
import org.risource.pia.HTTPResponse;

/**
 * implements the authentication mechanism for proxied transactions.
 * typically uses basic authentication to verify user name and password
 */

public class ProxyAuthenticator extends Authenticator{

  public ProxyAuthenticator(String type, String passwordFile)
    {
      super(type,passwordFile);
    }
  
  String authorizationHeader(Transaction request)
    {
      // returns appropriate header for this kind of authorization
      return request.header("proxy-authorization");
    }

  void assertApproval(Transaction request, String user, String method)
    {
      
      request.assert("AuthenticatedProxyUser", user);	
      request.assert("AuthenticationProxyMethod", "method");	
      // remove the password from the headers
      request.setHeader("proxy-authorization",user);
    }

  void assertFailure(Transaction request, String user, String method)
    {
      request.assert("UnauthenticatedProxyUser", user);  
    }

  /**
   * set headers of the response  requesting auth from client
   */
  public void setResponseHeaders(Transaction response, Agent agent){
    // should dispatch on type for now assume basic
    response.setStatus( 407 );
    
     response.setHeader("Proxy-Authenticate", "Basic realm=\"" + agent.name() +" Proxy \"");
  }
  
}
