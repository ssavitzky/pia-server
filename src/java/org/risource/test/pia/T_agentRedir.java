// T_agentRedir.java
// $Id: T_agentRedir.java,v 1.3 1999-03-12 19:30:27 steve Exp $

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


package org.risource.test.pia;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Properties;

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
import org.risource.pia.HTTPRequest;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Content;
import org.risource.content.ByteStreamContent;
import org.risource.ds.Features;
import org.risource.ds.Table;
import org.risource.ds.List;

import org.risource.util.Utilities;

import org.risource.pia.agent.Dofs;
import org.risource.pia.agent.Agency;
import org.risource.pia.ThreadPool;

public class T_agentRedir{
  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }


  public static Agency setupAgency(){
    Agency.DEBUG=true;
    Agency pentagon = new Agency("pentagon", "agency");

    System.out.println("\n\nDumping options -- name , type");
    //System.out.println("Option for name: "+ pentagon.optionAsString("name"));
    //System.out.println("Option for type: "+pentagon.optionAsString("type"));
    System.out.println("Version " + pentagon.version());

    return pentagon;
  }

 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test, here is the command --> java org.risource.pia.T_agentRedir input/get_agentredir.txt");
  }

  /**
   * For testing.
   * 
   */ 
 public static void main(String[] args){

    if( args.length < 1 ){
      printusage();
      System.exit( 1 );
    }

    if( args[0] != null )
      test( args[0] );
    else{
      printusage();
      System.exit( 1 );
    }

  }


  public static void test(String filename){
    Pia.debug("Testing redirection");
    Agency pentagon = setupAgency();
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );
      machine1.setOutputStream( System.out );

      Transaction trans1 = new HTTPRequest( machine1, true );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }
     
      trans1.assert("IsAgentRequest", new Boolean( true ) );

      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");

      Dofs.DEBUG=true;

      Table ht = new Table();
      ht.put("agent", "popart");
      ht.put("type", "Dofs");
      ht.put("root", "~/");
      ht.put("all", "false");
      pentagon.install( ht );
      
      Resolver res = Pia.instance().resolver();

      // put dofs' machine as toMachine of transaction
      pentagon.actOn( trans1, res );

      // will eventually call getRequest of dofs' machine

      trans1.handleRequest( res );

      ThreadPool tp = Pia.instance().threadPool();
      while( true ){
	sleep( 1000 );
	if( !tp.isThreadRunning() )
	  break;
      }
      System.exit(0);
      
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

  }

}






















































