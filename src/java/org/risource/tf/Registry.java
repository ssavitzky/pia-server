// Registry.java
// $Id: Registry.java,v 1.6 1999-06-25 01:12:25 wolff Exp $

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
 * A registry of calculators for computing features of a Transaction.
 *	A significant amount of kludgery is used to support both PERL
 *	and Java naming styles.  This can be expected to disappear in time. 
 *
 */

package org.risource.tf;

import org.risource.ds.UnaryFunctor;
import org.risource.ds.Table;
import org.risource.tf.TFComputer;
import org.risource.ds.Features;

public class Registry {
  /**
   * Cache for previously-created feature calculators.
   */
  protected static Table calcTable = new Table();

  protected static String packagePrefix = "org.risource.tf.";

  /**
   * Given a feature name, returns the corresponding feature calculator.
   * Create one if none existed.  If a class with the right name exists, 
   * load it.  If not, create a calculator that always returns null, to
   * avoid trying to load the class again. <p>
   *
   * Note that a name collision with some class that is <em>not</em> a feature
   * calculator is not considered an error at this time; later we may want
   * to put the actual calculators in a separate package from things like the
   * Registry.
   */
  public static TFComputer calculatorFor( String featureName ) {
    Object calc = calcTable.at( featureName );
    if( calc != null ) return (TFComputer) calc;
    else {
      String zname = packagePrefix + featureName;
      try{
	calc = Class.forName( zname ).newInstance();
      }catch (Exception e) {
      }
	
      if (calc == null) calc = TFComputer.UNDEFINED;
      else if (calc instanceof UnaryFunctor) {
	calc = new TFWrapper((UnaryFunctor)calc);
      } else if (! (calc instanceof TFComputer)) {
	// === later, could throw a PiaRuntimeException here ===
	calc = TFComputer.UNDEFINED;
      }

      calcTable.at( featureName, (TFComputer)calc );
      return (TFComputer)calc;
    }
  }

  /**
   * test loading classes
   */
  public static void main(String[] args){
    UnaryFunctor c;

    try {
      c = (UnaryFunctor) Registry.calculatorFor( "IsAgentRequest" );
    }catch(Exception e){
      System.out.println( e.toString() );
    }
  }

  /** Create an instance, which forces the constructor to be called. */
  protected static Registry instance = new Registry();

  /** The only real purpose of the constructor is to preload the table 
   *	with handlers with names different from their class name. */
  protected Registry() {
    // Pairlist: name, classname.
    String[] names = {
      "agent",		"Agent",
      "agent-pathname", "Agent",
      "agent-type",	"AgentType",
      "agent-path",	"AgentPath",
      "agent-name",	"AgentName",
      "agent-request",	"IsAgentRequest",
      "agent-response", "IsAgentResponse",
      "file-request",	"IsFileRequest",
      "html",		"IsHtml",
      "image",		"IsImage",
      "interform",	"IsInterform",
      "local-source",	"IsLclSrc",
      "local",		"IsLocal",
      "proxy-request",	"IsProxyRequest",
      "proxy-response",	"IsProxyResponse",
      "proxy-user",     "ProxyUser",
      "protocol",       "Protocol",
      "request",	"IsRequest",
      "response",	"IsResponse",
      "text",		"IsText",
      "title",		"Title",
    };
    for (int i = 0; i < names.length; i += 2) {
      String fname = names[i];	 // feature name
      String cname = names[i+1]; // class name

      TFComputer calc = calculatorFor(cname);
      calcTable.at(Features.cannonicalName(fname), calc);
      calcTable.at(Features.cannonicalName(cname), calc);
    }
  }

}


