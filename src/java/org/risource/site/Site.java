////// Site.java -- implementation of Root
//	$Id: Site.java,v 1.1 1999-08-07 00:29:49 steve Exp $

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
import org.risource.dps.active.*;

import java.io.*;
import java.net.URL;

/**
 * Implementation of Root, the root of a resource tree.
 *
 * <p> All real container resources that descend from a Site can be 
 *	assumed to be Subsite objects. 
 *
 * @version $Id: Site.java,v 1.1 1999-08-07 00:29:49 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public class Site extends Subsite implements Root {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected int verbosity = 0;
  protected PrintStream log = System.err;
  protected URL serverURL = null;
  protected String configFileName = "_subsite.cfg";

  /************************************************************************
  ** Root interface:
  ************************************************************************/

  /** Returns the URL by which this resource tree can be accessed. */
  public URL getServerURL() { return serverURL; }

  /** Report an exception. */
  public void reportException(Exception e, String explanation) {
    String message = "Exception " + e + " " + explanation + "\n";
    report(-2, message, 0, true);
  }

  /** Report an message. */
  public void report(int severity, String message,
		     int indent, boolean noNewline) {
    if (getVerbosity() < severity) return;
    for (int i = 0; i < indent; ++i) log.print(" ");
    if (noNewline) log.print(message);
    else	   log.println(message);
  }

  /** Obtain the current verbosity level for error reporting. */
  public int getVerbosity() { return verbosity; }

  /************************************************************************
  ** Resource interface:
  ************************************************************************/

  public Root getRoot() { return this; }
  
  /************************************************************************
  ** Initialization:
  ************************************************************************/

  public void setVerbosity(int v) { verbosity = v; }
  public void setReporting(PrintStream s) { log = s; }
  public void setServerURL(URL u) { serverURL = u; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Site(File location, ActiveElement config, Namespace props) {
    super("/", null, location, config, props);
  }
}
