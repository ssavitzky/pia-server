// Pump.java
// $Id: Pump.java,v 1.1 2000-09-20 00:34:30 steve Exp $

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


package org.risource.util;
import java.io.*;
import org.w3c.tools.timers.EventManager;
import org.w3c.tools.timers.EventHandler;
import org.risource.ds.UnaryFunctor;



/**
 * Pump is a Runnable that ``pumps'' data from an InputStream to an 
 *   	OutputStream.  It is particularly useful on the input or output of a
 *	Process, and especially one created by <code>Runtime.exec</code>
 */
public class Pump implements Runnable {
  BufferedInputStream in;
  BufferedOutputStream out;

  public Pump(InputStream i, OutputStream o) {
    in = (i instanceof BufferedInputStream)
      ? (BufferedInputStream)i
      : new BufferedInputStream(i);
    out = (o instanceof BufferedOutputStream)
      ? (BufferedOutputStream)o
      : new BufferedOutputStream(o);
  }

  public void run() {
    byte buf[] = new byte[1024];
    int len;
    try {
      for (len = in.read(buf, 0, 1024); len > 0; len = in.read(buf, 0, 1024)) {
	out.write(buf, 0, len);
      }
    } catch (Exception e) {}

    finally {
      try { in.close(); } catch (Exception ex) {}
      try { out.close(); } catch (Exception ex) {}
    }
  }
}











