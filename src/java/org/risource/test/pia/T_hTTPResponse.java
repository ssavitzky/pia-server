// T_hTTPResponse.java
// $Id: T_hTTPResponse.java,v 1.3 1999-03-12 19:30:33 steve Exp $

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
 * implement transaction for HTTP response
 */


package org.risource.test.pia;

import java.net.URL;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.StringTokenizer;

import org.risource.ds.Queue;
import org.risource.ds.Features;
import org.risource.ds.List;
import org.risource.pia.Machine;
import org.risource.pia.Content;
import org.risource.pia.Transaction;

import org.risource.tf.Registry;
import org.risource.pia.HTTPResponse;
import org.risource.content.ByteStreamContent;
import org.risource.pia.HTTPRequest;
import org.risource.pia.Headers;

public class  T_hTTPResponse {
  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }


  private static void test1( String filename ){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("Input is From machine getting its data from input/response.txt file.");
      System.out.println("Output is a dump of response's methods.");

      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      Machine machine2 = new Machine();

      boolean debug = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      printMethods( trans1 );
      System.exit( 0 );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2(String filename){
    //testing second constructor
    // with from, to machines and content
    System.out.println("Testing response w/ from, to, and content as arguments.");
    System.out.println("This response has a blank header and a content which is read from input/responsebody.txt.");
    System.out.println("In the run(), processInput() gets called.");

    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      Machine machine2 = new Machine();

      ByteStreamContent c = new ByteStreamContent();
      c.source( in );

      boolean dostart = false;
      boolean debug   = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, c, false, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      printMethods( trans1 );
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

  }

  private static void test3( String filename ){
    System.out.println("Testing response w/ request transaction and content as arguments.");
    System.out.println("This response has a blank header and content.");
    System.out.println("In the run(), processInput() gets called.");
    System.out.println("The request transaction is read from input/get.txt.");

    org.risource.pia.GenericAgent.DEBUG=true;
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }


      ByteStreamContent c = new ByteStreamContent();
      Transaction trans2 = new HTTPResponse( trans1, c, debug );
      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      printMethods( trans2 );
      System.exit(0);
    }catch(Exception e){
    }
  }

  private static void test4( String requestfile, String responsefile ){
    try{
      System.out.println("Testing response w/ Trasaction as the only argument.");
      System.out.println("The request transaction's from and to machine will be switched.");
      System.out.println("Also test setStatus, setReason, and setting headers information.");

      org.risource.pia.GenericAgent.DEBUG=true;
      InputStream in = new FileInputStream (requestfile);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      InputStream out = new FileInputStream (responsefile);
      Machine machine2 = new Machine();
      machine2.setInputStream( out );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      trans1.toMachine( machine2 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      boolean start = false;
      Transaction trans2 = new HTTPResponse( trans1, start, debug );
      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      System.out.println( "----->>>> Testing setting reason, message,... <<<<<------ " );
      Headers head = null;
      if( (head = trans2.headers())!= null ){
	head.setHeader("Server","FOOBAR/1.1");
	head.setHeader("MIME-version", "5.0");
	head.setHeader("Content-type", "image/gif");
      }
      trans2.setStatus(400);
      trans2.setReason("not found");
      printMethods( trans2 );
      System.exit(0);

    }catch(Exception e){
    }
  }

 private static void test5( String requestfile, String responsefile ){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("From machine gets its data from input/response.txt file.");
      System.out.println("Also, test transaction's features.");

      org.risource.pia.GenericAgent.DEBUG=true;
      InputStream in = new FileInputStream (requestfile);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      InputStream out = new FileInputStream (responsefile);
      Machine machine2 = new Machine();
      machine2.setInputStream( out );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      trans1.toMachine( machine2 );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }


      boolean start = false;
      Transaction trans2 = new HTTPResponse( trans1, start, debug );

      Thread thread2 = new Thread( trans2 );
      thread2.start();

      while( true ){
	sleep( 1000 );
	if( !thread2.isAlive() )
	  break;
      }

      Headers head = null;
      if( (head = trans2.headers())!= null ){
	head.setHeader("Version","PIA/blah.blah");
	head.setHeader("Content-type", "image/gif");
      }

      System.out.println( "Is agent response? ->" + trans2.compute("IsAgentResponse").toString() ); 

      Object o = trans2.compute("Title");
      if( o != null && o instanceof String ){
	String title = (String)o;
	System.out.println( "What is the tile, ya? ->" + title ); 
      }
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }


 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, (const. 1) --> java org.risource.pia.HTTPResponse -1 response.txt");
    System.out.println("For test 2, (const. 2) --> java org.risource.pia.HTTPResponse -2 responsebody.txt");
    System.out.println("For test 3, (const. 3) --> java org.risource.pia.HTTPResponse -3 get.txt");
    System.out.println("For test 4, (const. 4) --> java org.risource.pia.HTTPResponse -4 get.txt responsebody.txt");
    System.out.println("For test 5, (trans. features) --> java org.risource.pia.HTTPResponse -5 post.txt response.txt");
  }

  public static void main(String[] args){

    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }

    if( args.length == 1 ){
	printusage();
	System.exit( 1 );
    }else if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else if( args[0].equals ("-3") && args[1] != null )
	test3( args[1] );
    } else if( args.length == 3){ 
	if( args[0].equals ("-4") && args[1] != null && args[2] != null )
	  test4( args[1], args[2] );
	else if( args[0].equals ("-5") && args[1] != null && args[2] != null )
	  test5( args[1], args[2] );
	else{
	  printusage();
	  System.exit( 1 );
	}
    }

  }

  private static void printMethods(Transaction t){
      PrintStream out = new PrintStream( System.out );
      out.println( "----->>>> Testing response headers <<<<<------ " );
      out.print( t.headersAsString() );
      out.println( "----->>>> End testing headers <<<<<------ " );

      out.println("\n\n");
      out.println( "----->>>> Testing response methods <<<<<------ " );
      out.println( "method         -->" + t.method() );
      out.println( "content length -->" + Integer.toString( t.contentLength() ) );
      out.println( "content type   -->" + t.contentType() );
      out.println( "protocol       -->" + t.protocol() );
      out.println( "host           -->" + t.host() );
      out.println( "reason   -->" + t.reason() );
      out.println( "status code -->" + Integer.toString( t.statusCode()) );
      out.println( "proInitializationString -->" + t.protocolInitializationString() );
      if( t.requestURL() != null )
	out.println( "url            -->" + t.requestURL().getFile() );
      out.println( "HTTP version   -->" + t.version() ); 
      if( t.hasQueryString() )
	out.println( "query string -->" + t.queryString() );
      out.println( "----->>>> End testing methods <<<<<------ " );
      out.println("\n\n");

      out.println( "----->>>> The whole response message <<<<<------ " );
      try{
	t.printOn( out );
      }catch(Exception e){;}


  }
  
}









