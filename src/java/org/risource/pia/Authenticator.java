// Authenticator.java
// $Id: Authenticator.java,v 1.5 1999-09-22 00:28:54 steve Exp $

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

package org.risource.pia;

import org.risource.pia.Transaction;
import org.risource.pia.HTTPRequest;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Machine;
import org.risource.ds.Table;

import org.risource.util.Utilities;
import java.io.Reader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;

import misc.Jcrypt;

/**
 * implements the authentication mechanism for agent transactions.
 * typically uses basic authentication to verify user name and password
 */

public class Authenticator implements java.io.Serializable{

  // stores the type of authentication
  private String authType;

  // initial implementation does only basic authentication
  // should expand to other types such as digest

  // keep table of secret data e.g. file names or password table
  private Table attributes = new Table();
  
  public Authenticator(String type){
    authType = type;
    initialize();
  }

  /**
   * return a new authenticator of type using password file for verification
   * if passwordFile is null, all usernames will be accepted as ok
   */

  public Authenticator(String type, String passwordFile){
    authType = type;
    attributes.at("passwordFile", passwordFile);
    initialize();
  }


  private void initialize(){
    attributes.at("users", new Table());// hold passwords
  }

  /**
   * authenticate the given request -- eventually this will be complicated 
   * by different authentication methods and levels of service
   * for now simply require a username and password
   */

  public boolean authenticateRequest( Transaction request, String requestedFile, Agent agent){

    // dispatch should be based on authType --  for now basic is only type
     return authenticateRequestBasic(request,requestedFile, agent);
  }


  String authorizationHeader(Transaction request)
    {
      // returns appropriate header for this kind of authorization
      return request.header("authorization");
    }
  
  public boolean authenticateRequestBasic(Transaction request, String requestedFile, Agent agent){

    String authorization = authorizationHeader(request);
    
    if(authorization == null) return false;
    if( authorization.startsWith("Basic ") || authorization.startsWith("basic ") )  
      authorization =  authorization.substring(6); 
    else System.out.println(" unknown authentication method " + authorization);
      Pia.debug(this, "checking authorization");
    if ( authorization == null) return false;
	
    byte[] mybuf = Utilities.decodeBase64(authorization);

    if (mybuf.length == 0) return false;
    String user="", password=""; 
    String decoded=new String(mybuf);
    
    int icolon = decoded.indexOf (':') ;
    if ( (icolon > 0) && (icolon+1 <= decoded.length()) ) {
      user     = decoded.substring (0, icolon) ;
      if(icolon+1 < decoded.length()) 
	password = decoded.substring (icolon+1) ;
    } else if (icolon < 0 ) {
       user = decoded;
    }

    if(verifyPassword(user, password)){
      assertApproval(request, user, "Basic");
      
      return true;
    }  else {
      // should return403 e.g.  throw authentication exception
      assertFailure(request, user, "Basic");
      // for now just return false
      return false;
    }
    // should not get here
  }

  void assertApproval(Transaction request, String user, String method)
    {
      
      request.assert("AuthenticatedUser", user);	
      request.assert("AuthenticationMethod", "method");	
      // remove the password from the headers
      request.setHeader("authorization",user);
    }

  void assertFailure(Transaction request, String user, String method)
    {
      request.assert("UnauthenticatedUser", user);  
    }
  

  private boolean verifyPassword (String user, String password){
    Table users =(Table) attributes.at("users");
    String secret =(String) users.at(user);
    if(secret != null){
      String salt =  secret.substring(0, 2);
      if(secret.equals(Jcrypt.crypt(salt, password))) return true;
    }
    // try to load from password file
    String file = (String) attributes.at("passwordFile");
    // no password file specified implies that we should accept all users
    if(file == ""){ return true; }
         
    if(file != null){
      Reader pwfile = null;

      try {
	pwfile = new FileReader(file);
      } catch (IOException e) {
	System.out.println(" failed to open file");
	return false;
      }
    
      LineNumberReader rd = new LineNumberReader(pwfile);
      String line  = null;
      String entry = null;

      for ( ; ; ) {
	try {
	  line = rd.readLine();
	} catch (IOException e) {
	  line = null;
	}
	if (line == null) return false;
	if (line.startsWith(user+":")) break;
      }
      line = line.substring(user.length() + 1);
      if (line.indexOf(":") >= 0) {
	entry = line.substring(0, line.indexOf(":"));
      } else {
	entry = line;
      }
      
      if (entry.length() == 0 && password.length() == 0) return true;
      if (entry.length() < 3) return false;
      
      String salt = entry.substring(0, 2);
      if(!entry.equals(Jcrypt.crypt(salt, password))){
	return false;
      } else {
	users.at(user,entry);  //cache for next time
	 return true;
      }
    }
    return false;
  }

  /**
   * set headers of the response requesting auth from client
   */
  public void setResponseHeaders(Transaction response, Agent agent){
    // should dispatch on type for now assume basic
     response.setHeader("WWW-Authenticate", "Basic realm=\"" + agent.name() +"\"");
  }
			//WWW-Authenticate: Basic realm="AgentName"

}


