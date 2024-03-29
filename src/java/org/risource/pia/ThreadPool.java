// ThreadPool.java
// $Id: ThreadPool.java,v 1.5 2001-04-03 00:05:14 steve Exp $

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
import org.risource.ds.UnaryFunctor;
import org.risource.pia.Athread;
import java.util.Vector;

public class ThreadPool{
  /**
   * name of thread group
   */
  public static final String TP_NAME = "Transaction group";

  /**
   * use for shutdown of all threads
   */
  ThreadGroup group = null;

  /**
   * max number of threads
   */
  public final static String MAXTHREADS = "org.risource.pia.maxthreads";

  int              maxThreads = 50;

  Vector freeList = null;

  public static final int MAXTHREADCOUNT = 50;

  boolean running = true;

  /**
   *
   */
  public boolean isThreadRunning(){
    Athread one = null;
    for(int i = 0; i < freeList.size(); i ++){
      one = (Athread)freeList.elementAt( i );
      if( one.status == one.C_BUSY )
	return true;
    }
    return false;
  }

  /**
   * check a thread out 
   */
  public synchronized Athread checkOut(){
    Athread one = null;
    
    for(int i = 0; i < freeList.size(); i ++){
      one = (Athread)freeList.elementAt( i );
      if( one.status == one.C_IDLE )
	break;
    }

    if( one!= null ){
      one.status = one.C_BUSY;
      return one;
    }
    else
      return null;
  }

  public synchronized void notifyDone(Athread e){
    e.status = e.C_IDLE;
    e.zthread = null;
  }


  protected synchronized void addThread( ){
    Athread oneThread = new Athread();

    freeList.addElement( oneThread );
  } 

  ThreadPool(){

    group = new ThreadGroup( TP_NAME );

    //get pool properities from Pia
    maxThreads = Pia.instance().getInteger(MAXTHREADS, MAXTHREADCOUNT);
   
    freeList = new Vector();

    // now creates a bunch of threads
    for( int i =0; i < maxThreads; i++ ){
      addThread();
    }
    
  }
}



