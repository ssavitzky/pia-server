// T_Entity.java
// T_Entity.java,v 1.2 1999/03/01 23:48:06 pgage Exp

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


package crc.test.dom;

import crc.dom.*;

public class T_Entity{
  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command -->");
  }


  public static void main(String[] args){
    if( args.length < 1 ){
      printusage();
      System.exit( 1 );
    }
    
    if( args[0].equals ("1") )
	test1( "" );
    else
      if( args[0].equals ("2") ) 
	test2( "" );
    else
      if( args[0].equals ("3") ) 
	test3( "" );
    System.exit( 1 );
  }


  /**
  * For testing.
  * 
  */ 
  private static void test1(String dummy){
    Report.debug("\nTesting  Localentity...");
    
    LocalEntity le = new LocalEntity("localfoo");
    le.setContent("5");
    Report.debug("The content is: "+le.getContent());

    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)le).dump();

    Report.debug("\n\nTest cloning TextEntity");
    TextEntity te = le;
    TextEntity nn = (TextEntity)te.clone();
    Report.debug("Dumping the rest of fields...");
    nn.dump();

    BasicText bt = null;
    NodeList v= le.getValue();
    try{
      bt = (BasicText)v.item(0);
    }catch(Exception e){}
    bt.setData("6");
    Report.debug("The content is: "+nn.getContent());
    Report.debug("The content of original entity is: "+le.getContent());


    Report.debug("\n\nTesting LocalEntity copy constructor...");
    LocalEntity ae = new LocalEntity( le );
    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)ae).dump();
    Report.debug("The content of ae is: "+ae.getContent());
  }

 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String dummy){
    Report.debug("\nTesting  AgentEntity...");
    
    AgentEntity le = new AgentEntity("nichi", "");
    le.setContent("2");
    Report.debug("The agent content is: "+le.getContent());

    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)le).dump();

    Report.debug("\n\nTest cloning AgentEntity");
    AgentEntity nn = (AgentEntity)le.clone();
    Report.debug("Dumping the rest of fields...");
    nn.dump();

    BasicText bt = null;
    NodeList v= le.getValue();
    try{
      bt = (BasicText)v.item(0);
    }catch(Exception e){}
    bt.setData("3");
    Report.debug("The content is: "+nn.getContent());
    Report.debug("The content of original entity is: "+le.getContent());


    Report.debug("\n\nTesting AgentEntity copy constructor...");
    AgentEntity ae = new AgentEntity( le );

    bt.setData("4");

    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)ae).dump();
    Report.debug("The content of ae is: "+ae.getContent());
    Report.debug("The content of original entity is: "+le.getContent());

  }
 
  /**
  * For testing.
  * 
  */ 
  private static void test3( String dummy){
    Report.debug("\nTesting  SystemEntity...");
    
    SystemEntity le = new SystemEntity("nichi", "foo/bar");
    le.setContent("2");
    Report.debug("The agent content is: "+le.getContent());

    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)le).dump();

    Report.debug("\n\nTest cloning SystemEntity");
    SystemEntity nn = (SystemEntity)le.clone();
    Report.debug("Dumping the rest of fields...");
    nn.dump();

    BasicText bt = null;
    NodeList v= le.getValue();
    try{
      bt = (BasicText)v.item(0);
    }catch(Exception e){}
    bt.setData("3");
    Report.debug("The content is: "+nn.getContent());
    Report.debug("The content of original entity is: "+le.getContent());


    Report.debug("\n\nTesting SystemEntity copy constructor...");
    SystemEntity ae = new SystemEntity( le );

    bt.setData("4");

    Report.debug("Dumping the rest of fields...");
    ((AbstractEntity)ae).dump();
    Report.debug("The content of ae is: "+ae.getContent());
    Report.debug("The content of original entity is: "+le.getContent());

  }
 
}










