// HTTPResponse.java
// $Id: HTTPResponse.java,v 1.11 2001-04-03 00:05:11 steve Exp $

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


/** 
 * implement transaction for HTTP response
 */


package org.risource.pia;

import java.net.URL;
import java.io.IOException;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.StringTokenizer;

import org.risource.Version;
import org.risource.ds.Queue;
import org.risource.ds.List;
import org.risource.pia.Machine;
import org.risource.pia.Content;
import org.risource.pia.Transaction;

import org.risource.tf.Registry;

public class  HTTPResponse extends Transaction {

  /** 
   * status code of response
   */
  protected int  code = 200;

  /**
   * reason
   */
  protected  String reason = "ok";

  /**
   * Attribute index - controls to be insert into a response
   *
   */
  protected List controls = new List();



  /**
   * true if this transaction is a response
   */
  public boolean isResponse(){
    return   true;
  }

  /**
   * true if this transaction is a request
   */
  public boolean isRequest(){
    return   false;
  }


  /**
   * Returns HTTP + statuscode + message
   * @returns HTTP statuscode  message
   */
  public String protocolInitializationString(){
   //subclass should implement
    return "HTTP/"+major+"."+minor+ " "+  code + " " +reason();
 }	

 /**
   * Create header from fromMachine
   */
  protected void initializeHeader() throws PiaRuntimeException, IOException{
    try{
      super.initializeHeader();
      if( firstLineOk && headersObj == null ){
	// someone just give us the first line
	headersObj = new Headers();
      }
    }catch(PiaRuntimeException e){
      throw e;
    }catch(IOException ioe){
      throw ioe;
    }
  }


 /** 
  * Parse the first line of a response.
  * This line contains the protocol major and minor version numbers,
  *	response code, and message.
  */
  protected void parseInitializationString(String firstLine)
       throws IOException, PiaRuntimeException
  {
    if( firstLine == null ){
      String msg = "firstLine is null...\n";
      throw new PiaRuntimeException (this
				     , "parseInitializationString"
				     , msg) ;
    }

    if( org.risource.dps.util.Test.isWhitespace(firstLine) ){
      String msg = "firstLine is empty...<pre>'" + firstLine + "'</pre>...\n";
      throw new PiaRuntimeException (this
				     , "parseInitializationString"
				     , msg) ;
    }

    StringTokenizer tokens = new StringTokenizer(firstLine, " ");
    protocol = tokens.nextToken();
    if( protocol==null )
      throw new PiaRuntimeException(this, "parseInitializationString",
				    "No Protocol: '" + firstLine + "'");

    Pia.debug(this, "The first response line" + firstLine);

    if (! protocol.toLowerCase().startsWith("http/")) 
      throw new PiaRuntimeException(this, "parseInitializationString",
				    "Bad Protocol: '" + firstLine + "'");
    String majorMinor = protocol.substring("HTTP/".length());
    StringTokenizer mytokens = new StringTokenizer( majorMinor, "." );
    String zmajor = mytokens.nextToken();
    if( zmajor!=null ){
	major = zmajor;
	String zminor = mytokens.nextToken();
        minor = zminor;
    }

    if( tokens.hasMoreTokens() )
      code = Integer.parseInt( tokens.nextToken() );

    if( tokens.hasMoreTokens() )
      reason = tokens.nextToken();

    firstLineOk = true;
  }
  
  
  /** 
   * Send a response back to requester
   */
  public void defaultHandle( Resolver resolver ){
    sendResponse( resolver );
  }
 


  // response specific stuff
  /**
   * sendResponse -- Utilities to actually respond to a transaction, get a request, 
   * or generate (return) an error response transaction.
   *
   * These pass the resolver down to the Machine that actually does the 
   * work, because it might belong to an agent.
   *
   */
  public void sendResponse( Resolver resolver ){
    /*
     * Default handling for a response:
     * send it to the toMachine.  If the destination is not a reference 
     * to a machine, the response just gets dropped.  'Nowhere' is a good
     * non-reference to use in this case.
     */
    Pia.debug(this, "Transmitting response...");

    Machine machine = toMachine();
    
    if ( machine!= null ){
      try{
	machine.sendResponse(this, resolver);
      }catch(PiaRuntimeException e){
	Pia.debug(this, "User stop" );
	//errorResponse( e.getMessage() );
      }
    }
    else{
      Pia.debug(this, "dropping  response" );
    }
    
  }

  /**
   * Construct and return an error response.
   *	This is a new transaction, sent to <em>this</em> transaction's
   *	toMachine, in effect replacing this one.
   */
  public void errorResponse(int code, String msg){
    msg = errorMessage(code, msg);
    StringReader inputStream = new StringReader( msg );

    Content ct = new org.risource.content.text.html( inputStream, this );
    Transaction response = new HTTPResponse( Pia.getSiteMachine(),
					     toMachine(), ct, false);
    response.setStatus( code );
    response.setHeader("Server", Version.SERVER);
    response.setContentType( "text/html" );
    response.setContentLength( msg.length() );
    Pia.debug(this, "The header : \n" + response.headersAsString() );
    response.startThread();
  }

  /**
   * Get the reason phrase for this reply.
   * @return A String encoded reason phrase.
   */
  

  public String reason() {
    if(reason ==  null) reason = standardReason(code);

    return reason;
    
  }
  

  /**
   * Returns status code for this response.
   * @returns the status code for this response. 
   */
  public int statusCode(){
    return  code;
  }
  

  /**
   * Set this reply status code.
   * This will also set the reply reason, to the default HTTP/1.1 reason
   * phrase.
   * @param status The status code for this reply.
   */

  public void setStatus(int s) {
    if ((statusCode() != s) || (reason() == null))
      reason = standardReason(s);
    code = s;
  }

  /**
   * Get the reason phrase for this reply.
   * @return A String encoded reason phrase.
   */
  public String getReason() {
    return reason;
  }

  /**
   * Set the reason phrase of this reply.
   * @param reason The reason phrase for this reply.
   */

  public void setReason(String reason) {
    this.reason = reason;
  }



  /**
   * controls -- Add controls (buttons,icons,etc.) for agents to this response
   * @param aThing any control
   */
  public void addControl( Object aThing ){
    controls.push( aThing );
  }

  /**
   * Return controls as an array of Objects.
   * @return an array of Objects
   */
  public List controls(){
    return controls;
  }



  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * Header and content is created from the fromMachine.
   * @param from where request is originated
   * @param to where response is sent to
   */
  public HTTPResponse( Machine from, Machine to ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, machine to ] on duty...");
    
    fromMachine( from );
    toMachine(  to );

    startThread();
  }
 
  /**
   * A response to a request transaction -- a blank header is created.
   * @param t request transaction
   * @param doStart if false thread does not start automatically
   */
  public HTTPResponse(  Transaction t, boolean doStart  ){
    Pia.debug(this, "Constructor-- [ transaction t, boolean startThread ] on duty...");

    handlers = new Queue();

    contentObj = null;
    headersObj = new Headers(); //  blank header

    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

    if( doStart )
      startThread();
  }
  

  /**
   *  @param t request transaction
   *  @param from source from which header and content are created
   */
  public HTTPResponse(Transaction t,  Machine from ){
    super();

    Pia.debug(this, "Constructor-- [ Transaction t, machine from ] on duty...");
    
    requestTran = t;
    fromMachine( from );
    Pia.debug(this, "Constructor-- after fromMachine");
    toMachine(  t.fromMachine() );
    Pia.debug(this, "Constructor-- after toMachine");

    startThread();
  }
 


  /**
   * Use to create error response -- a blank header is created.
   * @param from where request is originated
   * @param to where to send response
   * @param ct a define content
   * @param doStart if false thread does not start -- allows user to set header information.
   */
  public HTTPResponse( Machine from, Machine to, Content ct, boolean doStart ){
    super();

    Pia.debug(this, "Constructor-- [ machine from, machine to, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    fromMachine( from );
    toMachine(  to );
    
    if( doStart )
      startThread();
     
  }
 
  /**
   * Use to create error response to a transaction
   * @param t the transaction to which this is a response.
   * @param from where request is originated
   * @param to where to send response
   * @param ct a content
   * @param doStart if false thread does not start -- allows user to set header information.
   */
  public HTTPResponse( Transaction t, Machine from, Content ct,
		       boolean doStart ){
    super();

    Pia.debug(this, "Constructor-- [ transaction t, machine from, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );

    requestTran = t;
    fromMachine( from );
    toMachine(  t.fromMachine() );
    
    if( doStart )
      startThread();
  }
 


  /**
   * Content is known and a blank header is created.
   * @param t request transaction
   * @param ct a content
   */
  public HTTPResponse(  Transaction t, Content ct ){
    super();

    Pia.debug(this, "Constructor-- [ transaction t, content ct ] on duty...");

    contentObj = ct;
    headersObj = new Headers(); //  blank header

    if( contentObj != null )
      contentObj.setHeaders( headersObj );
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );
    startThread();
  }
  
  /**
   * Content and  header are known.
   * @param t request transaction
   * @param ct a defined content
   * @param hd a defined header
   */
  public HTTPResponse(  Transaction t, Content ct, Headers hd ){
    super();

    Pia.debug(this, "Constructor-- [ transaction t, content ct, headers hd ] on duty...");

    contentObj = ct;
    headersObj = hd; //  maybe generate?

    if( contentObj != null )
      contentObj.setHeaders( headersObj );
  
    requestTran = t;
    fromMachine( t.toMachine() );
    toMachine( t.fromMachine() );

    startThread();
  }
  
  /**
   *  Create a content object from the fromMachine.
   * 
   */
  protected void initializeContent() throws PiaRuntimeException{
    InputStream in;
    String ztype = null;

    try{
      in = fromMachine().inputStream();

      if( (ztype = contentType()) == null )
	ztype = "text/html";

      contentObj = cf.createContent( ztype, in );
      if( contentObj != null )
	contentObj.setHeaders( headers() );
      else{
	Pia.debug(this, "Unknown header type...");
	String msg = "Unknown header type...\n";
	throw new PiaRuntimeException (this
				       , "initializeContent"
				       , msg) ;
      }

    }catch(IOException e){
      Pia.debug( e.toString() );
    }
  }

  /**
   * output protocolInitializationString, headers, and content.
   */
  public void printOn(OutputStream stream) throws IOException{
    OutputStreamWriter out = new OutputStreamWriter(stream);

     String responseLine = protocolInitializationString();
     if( responseLine != null )
       out.write( responseLine + "\n" );
     
     String headersString= headersAsString();
     if( headersString != null )
       out.write( headersString );
     
     Content c = contentObj();
     if( c!= null )
       out.write( c.toString() );
       
     out.flush();
  }


  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }

  public void run(){
      super.run();
  }
}









