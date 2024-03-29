// IsLocal.java
// $Id: IsLocal.java,v 1.4 2001-04-03 00:05:30 steve Exp $

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


package org.risource.tf;

import org.risource.ds.UnaryFunctor;
import org.risource.pia.Transaction;
import org.risource.pia.Pia;


public final class IsLocal implements UnaryFunctor{
  /**
   * Is the request for a local host to handle
   * @param o Transaction 
   * @return true if request's host start with "agency","", Pia's host is that same as request's host,
   * or request's host is "localhost".
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String host = trans.host();

      if( host != null ){
	String lhost = host.toLowerCase();
	if( lhost.startsWith("agency") || lhost == "" )
	  return new Boolean( true );


	String mhost = Pia.instance().host().toLowerCase();
	if( mhost.startsWith(lhost) )
	  return new Boolean( true );

	if( lhost.indexOf("localhost") != -1 )
	  return new Boolean( true );
      }
      return new Boolean( false );

    }
}




