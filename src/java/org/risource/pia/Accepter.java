// Accepter.java
// Accepter.java,v 1.11 1999/03/01 23:47:14 pgage Exp

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
 * This object accepts connections on a port.
 */

package org.risource.pia;

import java.io.*;
import java.net.*;

import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.Pia;
import org.risource.pia.HTTPRequest;

public class Accepter extends Thread {
  /**
   * The default port number if none is given.
   */
  public final static int DEFAULT_PORT=8001;

  /**
   * Attribute index - The port to listen on.
   */
  protected int port;

  /**
   * Attribute index - The socket that Accepter listens on.
   */
  protected ServerSocket listenSocket;

  /**
   * Attribute index - whether to shutdown
   */
  protected boolean finish = false;

  /**
   * Stop receiving requests
   */
  protected void shutdown(){
    finish = true;
  }

  /**
   * Shutdown socket
   */
  protected void cleanup(boolean restart){
    try {
      listenSocket.close();
      listenSocket = null;
      finish = false;
    }catch(IOException ex){
      Pia.debug(this, "[cleanup]: IOException while closing server socket.");
      Pia.errLog ("[cleanup]: IOException while closing server socket.");
    }
  }

  /**
   * Clean up without restarting
   */
  protected void finalize() throws IOException{
    cleanup( false );
  }
  
 /**
  * Loop for connections from clients.
  * @return nothing. 
  */ 
  public void run(){
    // make sure we never die GJW
    while( !finish ){
      try{
        //Do internal loop for slight effieciency
        while(!finish){
	  if(listenSocket == null) {
	    listenSocket = new ServerSocket( port ); 
	  } else  // listen for connections only if listen is not null
	  {	  
	    Socket clientSocket = listenSocket.accept();
	    if( listenSocket != null && clientSocket != null ){
	      handleConnection( clientSocket );
	    }
	  }
	}
      }catch(IOException e){
        Pia.debug(this, "There is an exception while listening for connection.");
        Pia.errSys(e, "There is an exception while listening for connection.");
      }catch(Exception e){
        Pia.errSys(e, "General exception while listening for connection.");
      }
    //at this point acceptor has thrown an error...clean up (and loop if !finished)
    cleanup(false);
    }
  }

  /**
   * This gets called by accepter whenever a new request is received.
   * A transaction is created, and it automatically places itself onto the resolver.
   */ 
  protected void handleConnection(Socket clientSocket) {
   
    InetAddress iaddr = clientSocket.getInetAddress();
    int         port  = clientSocket.getPort();

    String hostName = iaddr.getHostName();

    Pia.debug( this, "connection from: " + hostName
	       + " at: " + String.valueOf( port ) );
    Pia.log( "connection from: " + hostName
	     + " at: " + String.valueOf( port ) +"\n" );
    
    createRequestTransaction(hostName, port, clientSocket);

  }

 /**
  * Creates a transaction from the client's request
  * @return a PIA transaction. 
  */ 
 protected void createRequestTransaction ( String addr, int port, Socket client) {
    // Create a request transaction

    Machine machine =  new Machine(addr, port, client);
    new HTTPRequest( machine );
 }

  /**
   * Restart accepter  ??? never used ??? GJW 4/98
   */
  protected void restart(){
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      Pia.debug(this, "There is an exception creating accepter's socket.");
      Pia.errSys(e, "There is an exception creating accepter's socket.");
    }
    Pia.verbose("Accepter: listening on port" + port);
    this.start();
  }

  /**
  * Starts thread here
  * 
  */ 
  public Accepter( int port ) throws IOException{
    if(port == 0) port = DEFAULT_PORT;

    this.port = port;
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      throw e;
    }

    Pia.verbose("Accepter: listening on port " + port);
    this.start();
  }

}











