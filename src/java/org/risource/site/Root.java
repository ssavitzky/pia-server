////// Root.java -- interface for the topmost resource in a site
//	$Id: Root.java,v 1.1 1999-08-07 00:29:48 steve Exp $

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


package org.risource.site;

import org.w3c.dom.*;
import org.risource.dps.*;

import java.io.File;
import java.net.URL;

/**
 * Generic interface for the root of a resource tree.
 *
 * <p> 
 *
 * @version $Id: Root.java,v 1.1 1999-08-07 00:29:48 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public interface Root extends Resource {

  /** Returns the URL by which this resource tree can be accessed. */
  public URL getServerURL();

  /** Report an exception. */
  public void reportException(Exception e, String explanation);

  /** Report an message. */
  public void report(int severity, String message,
		     int indent, boolean noNewline);

  /** Obtain the current verbosity level for error reporting. */
  public int getVerbosity();

}
