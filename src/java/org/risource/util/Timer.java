// Timer.java
// $Id: Timer.java,v 1.6 2001-04-03 00:05:34 steve Exp $

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


package org.risource.util;
import java.io.*;
import org.w3c.tools.timers.EventManager;
import org.w3c.tools.timers.EventHandler;
import org.risource.ds.UnaryFunctor;



/**
 * implements a one shot timer
 */
public class Timer implements EventHandler{

  /**
   * store functor that will be execute upon timer arrival
   */
  UnaryFunctor f;

  /**
   * data to be passed to functor
   */ 
  Object zdata;

  /**
   * handle in case need to stop timer
   */
  Object handle;

  public boolean notdone = true;

  private static EventManager em = null;

  private EventManager em(){
    if( em == null ){
      em = new EventManager();
      em.setDaemon(true);
      em.start();
    }
    return em;
  }
  public synchronized void handleTimerEvent (Object data, long time) {
    UnaryFunctor f = (UnaryFunctor)data;
    f.execute( zdata );
    notdone = false;
  }

  public synchronized void setTimeout (int ms) {
    handle = em().registerTimer (ms, this, f) ;
  }

  public void stop(){
    em().recallTimer( handle );
  }

  public Timer( UnaryFunctor f, Object data ){
    this.f = f;
    this.zdata = data;
  }


  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }
  
}











