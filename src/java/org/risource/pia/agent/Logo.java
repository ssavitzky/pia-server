// Logo.java
// $Id: Logo.java,v 1.6 1999-05-20 20:21:03 steve Exp $

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
 * This is the class for the ``Logo'' agent, which synthesizes scaled images
 *	in the PIA's specialized logo font. <p>
 *
 *	For the moment, we are likely to punt and transfer control to a
 *	slightly-simplified version of the original PERL code.
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

import org.risource.pia.PiaRuntimeException;
import org.risource.pia.GenericAgent;
import org.risource.pia.FormContent;
import org.risource.pia.Resolver;
import org.risource.pia.Agent;
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Content;
import org.risource.pia.FileAccess;

import org.risource.util.Utilities;

import org.w3c.www.http.HTTP;

public class Logo extends GenericAgent {
  /**
   * Respond to a request. 
   * 	Figure out whether it's for an image or an active document.
   */
  public void respond(Transaction request, Resolver res)
       throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Logo respond...");

    if( !request.isRequest() ) return;
    URL url = request.requestURL();
    if( url == null ) return;

    /* First try a document.  If one exists, we're done. */
    if (respondWithDocument( request, res ) ) return;

    String path   = url.getFile();

    if (true) {
      /* === extreme hack!! call a PERL program! === */
      String cgi = findDocument("Logo.cgi");
      execProgram(request, "perl " + cgi+" -cgi "+path);
      
    } else {
      /* === EVEN MORE extreme hack!! Redirect to PERL PIA!!! === */

      String redirUrlString = "http://gelion:8001"+path;
      String msg = "Extreme kludge: see "+redirUrlString;
      Content ct = new org.risource.content.text.StringContent(msg);
      Transaction response = new HTTPResponse( Pia.thisMachine(),
					       request.fromMachine(),
					       ct, false);
      response.setHeader("Location", redirUrlString);
      response.setStatus(HTTP.MOVED_PERMANENTLY);
      response.setContentLength( msg.length() );
      response.startThread();
    }
  }
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * name and type needs to be set after this
   */
  public Logo(){
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






















































