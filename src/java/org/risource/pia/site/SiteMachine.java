// SiteMachine.java
// $Id: SiteMachine.java,v 1.1 1999-09-09 21:54:45 steve Exp $

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


package org.risource.pia.site;

import org.w3c.www.http.HTTP;

import org.risource.pia.Machine;
import org.risource.pia.Agent;
import org.risource.pia.Pia;
import org.risource.pia.Resolver;
import org.risource.pia.Transaction;
import org.risource.pia.Content;
import org.risource.pia.HTTPResponse;
import org.risource.pia.PiaRuntimeException;

import org.risource.content.text.ProcessedContent;
import org.risource.content.*;
import org.risource.util.*;

import org.risource.Version;
import org.risource.site.*;
import org.risource.dps.*;

import java.io.*;
import java.net.*;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Subclass of Machine that serves as the source of documents located
 *	in the PIA's "Site".
 *
 * @see org.risource.site
 * @see org.risource.site.Site
 * @see org.risource.pia.Pia
 */
public class SiteMachine extends Machine {

  /************************************************************************
  ** State and state access:
  ************************************************************************/

  /**
   * Site root
   */
  protected Site site;

  /************************************************************************
  ** Responding to Requests:
  ************************************************************************/

  // inherit sendResponse

  /**
   * Handle a get request to a resource.
   */
  public void getRequest(Transaction request, Resolver resolver)
       throws PiaRuntimeException {

    URL url = request.requestURL();
    if (url == null)
      throw new PiaRuntimeException(this, "getRequest", "null request URL");

    String path = url.getFile();

    if (!respondWithDocument(request, path, resolver))
      respondNotFound(request, path);
  }

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg ) {
    req.errorResponse(code, msg);
  }

  /**
   * Send error message for document not found
   */
  public void respondNotFound( Transaction req, String path){
    String msg = "File <code>" + path + "</code> not found. ";
    sendErrorResponse(req, HTTP.NOT_FOUND, msg);
  }

  /**
   * Respond to a transaction with a stream of HTML.
   */
  public void sendStreamResponse ( Transaction trans, InputStream in ) {

    Content c = new org.risource.content.text.html( in );

    Transaction response = new HTTPResponse( trans, false );
    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setContentType( "text/html" );
    response.setContentObj( c );
    response.startThread();
  }

  /**
   * Send redirection to client
   */
  protected boolean sendRedirection( Transaction req, String path ) {
    URL oldUrl = req.requestURL();

    URL redirUrl = null;
    String redirUrlString = null;

    try{
      redirUrl = new URL(oldUrl, path);
      redirUrlString = redirUrl.toExternalForm();
      Pia.debug(this, "The redirected url-->" + redirUrlString);
    }catch(MalformedURLException e){
      String msg = "Malformed URL redirecting to "+path;
      throw new PiaRuntimeException(this, "redirectTo", msg);
    }

    String msg ="Redirecting " + oldUrl.toExternalForm()
      + " to:" + redirUrlString; 

    Pia.debug(this, msg);

    Content ct = new org.risource.content.text.html( new StringReader(msg) );
    Transaction response = new HTTPResponse( Pia.thisMachine(),
					     req.fromMachine(), ct, false);
    response.setHeader("Location", redirUrlString);
    // Got a redirect message for all agency agents
    // response.setStatus(HTTP.TEMPORARY_REDIRECT);
    response.setStatus(HTTP.MOVED_PERMANENTLY);
    response.setContentLength( msg.length() );
    response.startThread();

    return true;
  }

  /** Convert a Date to a String in the GMT timezone according to
   *	the Internet (IETF) standard.  Replaces the deprecated
   *	Date.toGMTString, from which the code has been shamelessly
   *	copied. 
   *
   * @see java.util.Date#toGMTString
   */
  public static String toGMTString(Date date) {
    DateFormat formatter
      = new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    return formatter.format(date);
  }


  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public boolean sendProcessedResponse (Document doc, String ctype, String tsn,
					Transaction trans, Resolver res ) {
    Transaction response = new HTTPResponse( trans, false );

    SiteDoc proc = new SiteDoc(doc, null, trans, response, res);
    proc.setVerbosity((Pia.verbose()? 1 : 0) + (Pia.debug()? 2 : 0));

    Tagset ts  = doc.loadTagset(tsn);

    if (ts == null) {
      sendErrorResponse(trans, 500, "cannot load tagset " +tsn);
      return false;
    }

    proc.setTagset(ts);
    Reader reader =  doc.documentReader();

    if (reader == null) {
      sendErrorResponse(trans, 500, "Cannot read document " + doc.getPath());
      return false;
    }

    Content c = new ProcessedContent(doc.getPath(), reader, proc);
    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setHeader("Last-Modified",
		       toGMTString(new Date(doc.getLastModified()))); 
    response.setContentType( ctype );
    response.setContentObj( c );
    response.startThread();
    return true;
  }

  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public boolean sendStreamResponse (Document doc, String ctype,
				     Transaction trans, Resolver res ) {
    HTTPResponse response = new HTTPResponse( trans, false );
    Content c;
    if (ctype.indexOf("html") == -1) {
      c = new ByteStreamContent( doc.documentInputStream() );
    } else {
      c = new org.risource.content.text.html( doc.documentReader() );
    }
    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setContentType( ctype );
    response.setContentObj( c );
    response.startThread();
    return true;
  }

  /**
   * Respond to a request directed at one of an site's documents, 
   * with a (possibly-modified) path.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondWithDocument(Transaction request, String path,
				    Resolver res){

    // If the path includes a query string, remove it now
    int end = path.indexOf('?');
    if(end > 0) path = path.substring(0, end);

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // Locate the resource
    Resource resource = site.locate(path, false, null);
    if (resource == null) return false;

    // If the request needs to be redirected, do so now.
    if (resource.isContainer() && !path.endsWith("/")) 
      return sendRedirection(request, path + "/");

    /* === worry about authentication ===

    // authenticate the requester if necessary;
    // internal requests are not authenticated currently--eg. virtual machines
    if( !(request.fromMachine() instanceof AgentMachine)
	&& !authenticateRequest(request,file)){
      requestAuthentication(request);
      return true; //not clear-returning true because response has been set
    }

    */

    Document doc = resource.getDocument();
    if (doc == null) {
      resource = resource.locate("home", false, null);
      if (resource == null) {
	System.err.println("=== cannot locate home in " + path);
	if (resource == null) return false;
      }
      doc = resource.getDocument();
      if (doc == null) {
	System.err.println("=== no document for home in " + path);
	return false;
      }
    }

    String tsname = doc.getTagsetName();
    String ctype  = doc.getContentType();
    if (ctype == null) ctype = doc.getContentTypeFor(".*");
    if (ctype == null) ctype = "text/plain";

    if( tsname == null || tsname.length() == 0 ) {
      return sendStreamResponse(doc, ctype, request, res);
    } else if (tsname.startsWith("!")) {
      try{
	execCgi(ctype, request, doc.documentFile().getPath());
      }catch(PiaRuntimeException ee ){
	throw ee;
      }
    } else if (tsname.startsWith("#")) {
      return false;
    } else {
      return sendProcessedResponse(doc, ctype, tsname, request, res);
    }
    return true;
  }

  protected void execCgi(String ctype, Transaction request, String file )
       throws PiaRuntimeException
  {
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    OutputStream out;

    try{
      String[] envp = setupEnvironment( request );

      process = rt.exec( file, envp );

      if( request.method().equalsIgnoreCase( "POST" ) ){
	out = process.getOutputStream();
	out.write( request.queryString().getBytes() );
	out.flush();
      }

      in = process.getInputStream();

      // === a ByteStreamContent is wrong for a CGI.
      // === We should really create a Machine and let Transaction
      // === parse the headers it returns.
      Content ct = new ByteStreamContent( in );
      Transaction response = new HTTPResponse( request, ct);
      
    }catch(IOException ee){
      String msg = "can not exec :"+file;
      throw new PiaRuntimeException (this, "respondWithDocument", msg) ;
    }
  }

  /**
   * Prepare environment variables for CGI
   */
  protected String[] setupEnvironment(Transaction req){
    String path = req.requestURL().getFile();

    String[] envp = new String[9];
    envp[0]="CONTENT_TYPE=";
    envp[1]="CONTENT_LENGTH=";
    
    if( req.method().equalsIgnoreCase( "POST" ) ){
      envp[0] += req.contentType();
      envp[1] += req.contentLength();
    }
    envp[2]="GATEWAY_INTERFACE=" + "CGI/1.0";
    envp[3]="SERVER_PORT="       + Pia.instance().port();
    envp[4]="SERVER_PROTO="      + req.version();
    envp[5]="REQUEST_METHOD="    + req.method();
    envp[6]="REMOTE_ADDR=";
    envp[7]="QUERY_STRING=";
    
    if( req.method().equalsIgnoreCase( "GET" ) )
      envp[7] += req.queryString();
    
    envp[8]="PATH_INFO="	+ path;

    for(int i = 0; i < envp.length; i++){
      Pia.debug(this, "The environment var -->" + envp[i]);
    }
    return envp;
  }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SiteMachine( Site theSite ){
    site = theSite;
  }

}  
