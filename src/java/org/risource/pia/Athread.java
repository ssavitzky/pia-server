// Athread.java
// $Id: Athread.java,v 1.4 2001-04-03 00:05:09 steve Exp $

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


package org.risource.pia;
import org.risource.pia.Pia;
import java.io.*;
import org.risource.ds.UnaryFunctor;

public class Athread implements UnaryFunctor{
  static final int C_IDLE = 0;	// Zombie
  static final int C_BUSY = 1;	// Is in busy list
  
  protected int status;
  protected Thread zthread;

  /**
   * Creates a thread to run a transaction.
   * @param o a transaction as an object.
   */
  public Object execute( Object o ){
    ThreadPool tp = Pia.instance().threadPool();

    Transaction t = (Transaction) o;
    zthread = new Thread(tp.group, t );
    zthread.start();
    return o;
  }

  Athread(){
    status = C_IDLE;
  }
}

