// Camera.java
// $Id: Camera.java,v 1.4 1999-03-12 19:50:01 pgage Exp $

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


/**
 * This is the class for the ``Camera'' agent, which does special things 
 * with images.
 */

package org.risource.pia.agent;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import org.risource.pia.*;

import org.risource.util.Utilities;

import org.w3c.www.http.HTTP;

public class Camera extends GenericAgent {
  /**
   * Respond to a request. 
   * 	Figure out whether it's for an image or an interform.
   */
  public void respond(Transaction request, Resolver res)
       throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Camera respond...");

    if( !request.isRequest() ) return;
    URL url = request.requestURL();
    if( url == null ) return;

    String path   = url.getFile();
    FileInputStream s = null;

     if (path.endsWith(".gif")) {
      String fn = findInterform(path);
      if (fn == null) {
	sendErrorResponse(request, HTTP.NOT_FOUND, "Missing .gif file");
	return;
      }
      try {
	 s = new FileInputStream(fn);
      } catch ( java.io.IOException e ) {
	sendErrorResponse(request, HTTP.NOT_FOUND,
			  "Exception: " + e.getMessage());
      }
      reply = new HTTPResponse(request, false);
      reply.setStatus(200);
      reply.setContentType("image/gif");
      reply.setContentObj(new org.risource.content.ByteStreamContent(s));
      reply.startThread();	
      return;
    }
    
    /* Then try an InterForm.  If one exists, we're done. */
    if (respondToInterform( request, res ) ) return;
    sendErrorResponse(request, HTTP.NOT_FOUND, "Cannot find " + path);

  }
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * name and type needs to be set after this
   */
  public Camera(){
    super();
  }

  private void execProgram(Transaction request, String cmd) { 
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    PrintStream out;
    String cmdArray[] = {"/bin/sh", "-c", cmd};


    try{
      process = rt.exec( cmdArray );
      in = process.getInputStream();
      Transaction response = new HTTPResponse( request, new Machine(in));
      
    }catch(Exception ee){
      String msg = "can not exec :"+cmd;
      throw new PiaRuntimeException (this, "execProgram", msg) ;
    }
  }

}






















































