////// Site.java -- implementation of Root
//	$Id: PiaSite.java,v 1.4 1999-12-14 18:40:10 steve Exp $

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


package org.risource.pia.site;

import org.w3c.dom.*;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.process.TopProcessor;
import org.risource.dps.namespace.PropertyTable;

import org.risource.ds.*;
import org.risource.pia.*;
import org.risource.site.*;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;

/**
 * A specialized Site for use in the PIA.
 *
 * @version $Id: PiaSite.java,v 1.4 1999-12-14 18:40:10 steve Exp $
 * @author steve@rsv.ricoh.com 
 */

public class PiaSite extends Site {

  /************************************************************************
  ** Overrides:
  ************************************************************************/

  /** Construct a TopContext of the correct type. 
   *	This method may be overridden in a subclass in order to provide
   *	a suitable environment in which to run documents. 
   */
  protected TopContext makeTopContext() {
    return new SiteDoc();
  }

  protected Resource getPrefixedResource(String path, String prefix) {
    if (prefix.equalsIgnoreCase("pia:")) {
      while (path.startsWith("/")) { path = path.substring(1); }
      File home = Pia.getHomeDir();
      if (home == null) return null;
      return new FileDocument("pia:", null, home).getRelative(path);
    } else {
      return super.getPrefixedResource(path, prefix); 
    }
  }

  protected Resource locatePrefixedResource(String path, String prefix,
					    boolean create, List extensions) {
    if (prefix.equalsIgnoreCase("pia:")) {
      while (path.startsWith("/")) { path = path.substring(1); }
      File home = Pia.getHomeDir();
      if (home == null) return null;
      return new FileDocument("pia:", null, home).locate(path, create,
							 extensions);
    } else {
      return super.locatePrefixedResource(path, prefix, create, extensions); 
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a Site from a location, configuration, and properties. */
  public PiaSite(File location, ActiveElement config) {
    super(location, config);
  }

  /** Construct a Site with an explicit virtual path.
   *
   *<p> This constructor allows both the real and virtual locations to be
   *	specified before attempting to load the configuration file.  This
   *	is necessary in case the configuration file is virtual.  
   *
   * @param realLoc the pathname of the real location of the Site
   * @param virtualLoc the pathname of the virtual location of the Site
   * @param defaultDir the pathname of the directory of default documents
   * @param configFileName the default configuration file name
   * @param configTagsetName the tagset to use for loading configuration files.
   */
  public PiaSite(String realLoc, String virtualLoc, String defaultDir,
		 String configFileName, String configTagsetName,
		 PropertyTable props) {
    super(realLoc, virtualLoc, defaultDir, configFileName, 
	  ((configTagsetName != null)? configTagsetName : "pia-config"),
	  props);
  }
}
