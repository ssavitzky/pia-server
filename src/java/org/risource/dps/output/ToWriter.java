////// ToWriter.java:  Output to Writer
//	$Id: ToWriter.java,v 1.8 2001-04-03 00:04:44 steve Exp $

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


package org.risource.dps.output;

import org.risource.dps.*;
import org.risource.dps.util.*;
import org.w3c.dom.*;

import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Output to a Writer (character output stream). <p>
 *
 * @version $Id: ToWriter.java,v 1.8 2001-04-03 00:04:44 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Output
 * @see org.risource.dps.Processor
 */

public class ToWriter extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected BufferedWriter destination = null;

  public Writer getWriter() { return destination; }
  protected void setWriter(Writer w) {
    destination = (w instanceof BufferedWriter)
      ? (BufferedWriter) w : new BufferedWriter(w);
  }

  public void close() {
    try {
      destination.close();
    } catch (IOException e) {}
  }


  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected void write(String s) {
    try {
      destination.write(s);
    } catch (IOException e) {}
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output given a destination Writer */
  public ToWriter(Writer dest) {
    setWriter(dest);
  }

  /** Construct an Output given a destination filaname.  Opens the file. */
  public ToWriter(String filename) throws java.io.IOException {
    setWriter(new FileWriter(filename));
  }
}
