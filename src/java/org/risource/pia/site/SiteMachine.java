// SiteMachine.java
// $Id: SiteMachine.java,v 1.14 2001-04-03 00:05:20 steve Exp $

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


package org.risource.pia.site;

import org.w3c.www.http.HTTP;

import org.risource.pia.*;

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
 * <p> For initialization and similar purposes, SiteMachine is also able
 *	to process files entirely for their side-effects.
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
  ** Submitting Requests:
  ************************************************************************/

  /**
   * Default content type for form submissions
   */
  protected final static String DefaultFormSubmissionContentType
    = "application/x-www-form-urlencoded";

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   */
  public void createRequest(String method, String url,
			    String queryString, String contentType){
    makeRequest(method, url, queryString, contentType).startThread();
  }


  /**
   * Given a url string and content create a request transaction.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString content for a POST request.
   *	@param contentType defaults to DefaultFormSubmissionContentType
   */
  public void createRequest(String method, String url,
			    InputContent content, String contentType) {
    makeRequest(method, url, content, contentType).startThread();
  }

  /** Make a new request Transaction on this agent. */
  public Transaction makeRequest(String method, String url, 
				 String queryString, String contentType) {
    InputContent c;
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    if (contentType.startsWith("multipart/form-data")) {
      org.risource.pia.Pia.debug(this,"Making new MultipartFormContent");

      c = new MultipartFormContent
	(Utilities.StringToByteArrayOutputStream(queryString));
    } else {
      // Convert stream to String, taking care of nulls
      if (queryString == null) {
	// Make sure correct constructor of FormContent is called
	c = new FormContent((String)null);
      } else {
	c = new FormContent( queryString );
      }
    }

    return makeRequest(method, url, c, contentType);
  }

  /** Make a new request Transaction on this agent. */
  public Transaction makeRequest(String method, String url, 
				 InputContent content, String contentType) {
    if (contentType == null) contentType = DefaultFormSubmissionContentType;

    Pia.debug(this, "makeRequest -->"+method+" "+url);
    Pia.debug(this, "Content type "+contentType);

    Transaction request;

    // create things normally gotten from header
    String initString = "HTTP/1.0 "+ method +" "+url;

    // create the request but don't start processing
    request = new HTTPRequest( this, content, false );

    // Changed "Version" to "User-Agent"
    request.setHeader("User-Agent", "PIA" );
    //request.setHeader("User-Agent", "Mozilla/4.5b1 [en] (Win95; I)");

    request.setContentType(contentType);
    try {
      request.setContentLength(content.getCurrentContentLength());
    } catch (ContentOperationUnavailable e) {
      // If we cannot find content length, do not set header
    }
    request.setHeader("Accept", "image/png, image/x-xbitmap, image/jpeg, image/pjpeg, image/gif, */*");

    //    request.setContentLength( queryString.length() );

    request.setMethod( method );
    request.setRequestURL( url );
    return request;
  }
 

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
    Transaction response = new HTTPResponse( this,
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
					Transaction trans, Resolver res,
					String pathinfo) {
    Transaction response = new HTTPResponse( trans, false );

    SiteDoc proc = new SiteDoc(doc, null, trans, response, res);
    proc.setVerbosity(Pia.getVerbosity());

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
    if (pathinfo != null) proc.define("PATH_INFO", pathinfo);
    Date now = new Date();

    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setHeader("Last-Modified",
		       toGMTString(new Date(doc.getLastModified()))); 

    // TEMPORARY!!! hack to prevent active pages from being cached. ===
    // BAD JUJU with back button on form submissions!  TEMPORARY!!! ===
    //response.setHeader("Date", toGMTString(now));
    //response.setHeader("Expires", toGMTString(now));

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

  /** Test whether the given resource is in a home directory which
   *	should be redirected.
   */
  protected boolean redirectHome(Resource resource) {
    return true;		// for now, always redirect
  }

  /** Compute the appropriate redirection for a path. */
  protected String getRedirection(Resource resource, String path) {
    if (path.startsWith("/~") && redirectHome(resource)) {
      if (resource.isContainer() && !path.endsWith("/")) {
	return resource.getPath() + "/";
      } else {
	return resource.getPath();
      }
    } else if (resource.isContainer() && !path.endsWith("/") ) {
      return path + "/";
    } else {
      return null;
    }
  }

  public void requestAuthentication ( Transaction trans, Agent auth, 
				      Resource resource) {

    org.risource.pia.Authenticator aPolicy = auth.getAuthenticator();

   //do we need content for authentication response??
      Pia.debug(this, "Authentication required");
    Transaction response = new HTTPResponse( trans, false );
     if(trans.has("UnauthorizedUser")) 
       response.setStatus( 403 ); 
     else 
       response.setStatus( 401 ); 
    aPolicy.setResponseHeaders(response, auth);
    Content c = new org.risource.content.text.StringContent(
		   "Authorization required for access to "
		   + resource.getPath());
    response.setContentType( "text/plain" );
    response.setContentObj( c );
    response.startThread();
 }
  /**
   * Respond to a request directed at one of an site's documents, 
   * with a (possibly-modified) path.
   *
   * <p> There are several possible ways of handling the document, depending
   *	 on the "tagset name" associated with the document's extension:
   *
   * <ul>
   *	<li> <i>none</i> ship the document as an ordinary file.
   *    <li> <i>tagset</i> process the document with the named tagset
   *    <li> <code>#</code><i>tsname</i> hide the document.
   *    <li> <code>!</code><i></i> execute the document as a CGI.
   *    <li> <code>|</code><i>cmd</i> filter the document through the given
   *		command. 
   * </ul>
   *
   * @return false if the file cannot be found.
   */
  public boolean respondWithDocument(Transaction request, String path,
				     Resolver res){

    // If the path includes a query string, remove it now
    int end = path.indexOf('?');
    String query = null;
    if(end > 0) {
      query = path.substring(end+1);
      path = path.substring(0, end);
    }

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // Locate the resource.  Fail if it can't be found.
    StringBuffer tail = new StringBuffer();
    Resource resource = site.locate(path, (short)-1, null, tail);
    if (resource == null) return false;

    // If the resource is hidden, we're not supposed to show it.
    if (resource.isHidden()) return false;

    // Check for non-null pathinfo.
    String pathinfo = null;
    if (tail.length() == 0) {
      // No pathinfo, so check to see whether we need to redirect it.
      String redirection = getRedirection(resource, path);
      if (redirection != null) return sendRedirection(request, redirection);
    } else {
      // There's pathinfo.  Make sure the resource is a container.
      //   We may eventually be able to flag random resources as able to 
      //   handle pathinfo.  For the moment, we bomb out.
      if (!resource.isContainer()) return false;
      pathinfo = tail.toString();
    }

    // See if the request needs to be authenticated.
    Agent auth = res.getAuthenticatorForResource(resource);
    if (auth != null && !auth.authenticateRequest(request, resource)) {
      requestAuthentication(request, auth, resource);
      return true; // returning true because response has been set
    }

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
      if (pathinfo != null) return false; // passive docs don't handle pathinfo
      return sendStreamResponse(doc, ctype, request, res);
    } else if (tsname.startsWith("#")) {
      return false;
    } else if (tsname.startsWith("!")) {
      try{
	execCgi(ctype, request, doc.documentFile().getPath(), doc, pathinfo);
      }catch(PiaRuntimeException ee ){
	throw ee;
      }
    } else if (tsname.startsWith("|")) {
      try{
	execFilter(ctype, tsname.substring(1), request, doc, pathinfo);
      }catch(PiaRuntimeException ee ){
	throw ee;
      }
    } else if (pathinfo != null && ! (doc instanceof Listing)) {
      // not a Listing, hence not defined by IndexDocumentPath.
      //   There's no good way yet to tell whether a random XML file 
      //   can handle pathinfo, so we just bail out at this point.
      return false;
    } else {
      return sendProcessedResponse(doc, ctype, tsname, request, res, pathinfo);
    }
    return true;
  }

  protected void execCgi(String ctype, Transaction request, String file,
			 Document doc, String pathinfo)
       throws PiaRuntimeException
  {
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    OutputStream out;

    try{
      String[] envp = setupEnvironment(request, doc, pathinfo);

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
      String msg = "exception " + ee + " trying to exec "+file;
      throw new PiaRuntimeException (this, "respondWithDocument", msg) ;
    }
  }

  /**
   * Prepare environment variables for CGI
   *	See <a href="http://hoohoo.ncsa.uiuc.edu/cgi/env.html">
   *	    http://hoohoo.ncsa.uiuc.edu/cgi/env.html</a> for a writeup
   *	on the defined environment variables.
   */
  protected String[] setupEnvironment(Transaction req, Document doc, 
				      String pathinfo){
    String path = req.requestURL().getFile();

    String[] envp = new String[10];
    envp[0]="CONTENT_TYPE=";
    envp[1]="CONTENT_LENGTH=";
    
    if( req.method().equalsIgnoreCase( "POST" ) ){
      envp[0] += req.contentType();
      envp[1] += req.contentLength();
    }
    envp[2]="GATEWAY_INTERFACE=" + "CGI/1.0";
    envp[3]="SERVER_PORT="       + Pia.instance().virtualPort();
    envp[4]="SERVER_PROTO="      + req.version();
    envp[5]="REQUEST_METHOD="    + req.method();
    envp[6]="REMOTE_ADDR=";
    envp[7]="QUERY_STRING=";
    
    if( req.method().equalsIgnoreCase( "GET" ) )
      envp[7] += req.queryString();
    
    envp[8]="PATH_INFO="	+ pathinfo;
    envp[9]="SCRIPT_NAME="	+ doc.getPath();

    for(int i = 0; i < envp.length; i++){
      Pia.debug(this, "The environment var -->" + envp[i]);
    }
    return envp;
  }
  
  protected void execFilter(String ctype, String cmd, Transaction request,
			    Document doc, String pathinfo)
       throws PiaRuntimeException
  {
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    Pump pump;

    if (cmd.startsWith("pia:")) {
      cmd = cmd.substring(4);
      if (!cmd.startsWith("/")) cmd = "/" + cmd;
      cmd = Pia.instance().getProperty("home") + cmd;      
    }

    try{
      // String[] envp = setupEnvironment(request, doc, pathinfo);

      process = rt.exec(cmd);
      OutputStream out = process.getOutputStream();
      InputStream in = process.getInputStream();
      pump = new Pump(doc.documentInputStream(), out);
      pump.run();

      // === a ByteStreamContent is wrong for a CGI.
      // === We should really create a Machine and let Transaction
      // === parse the headers it returns.
      Content ct = new ByteStreamContent( in );
      Transaction response = new HTTPResponse( request, ct);
      
    }catch(IOException ee){
      String msg = "exception " + ee + " trying to exec " + cmd ;
      throw new PiaRuntimeException (this, "respondWithDocument", msg) ;
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SiteMachine(String host, int port, Site theSite ){
    super(host, port);
    site = theSite;
  }

}  
