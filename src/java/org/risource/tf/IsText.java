// IsText.java
// $Id: IsText.java,v 1.4 2001-04-03 00:05:30 steve Exp $

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

public final class IsText implements UnaryFunctor{

  /**
   * Is this transaction's content type is that of "text"
   * @param o Transaction 
   * @return true if content type starts with "text"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String s = trans.contentType();
      if( s != null ){
	String ls = s.toLowerCase();
	if( ls.startsWith("text") )
	  return new Boolean( true );
      }
      return new Boolean( false );
     
    }
}


