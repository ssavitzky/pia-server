// Dofs.java
// $Id: Dofs.java,v 1.8 1999-09-22 00:23:14 steve Exp $

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


/** 
 * This is the class for the ``DOFS'' agent, which handles requests
 *	for local files.
 */

package org.risource.pia.agent;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Properties;

import java.net.URL;
import java.net.MalformedURLException;

import org.risource.pia.PiaRuntimeException;
import org.risource.pia.FormContent;
import org.risource.pia.Resolver;
import org.risource.pia.Agent;
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.Content;
import org.risource.pia.FileAccess;
import org.risource.ds.Features;
import org.risource.ds.Table;
import org.risource.ds.List;

import org.risource.util.Utilities;
import org.risource.dps.namespace.*;

import org.w3c.www.http.HTTP;
public class Dofs extends Agent {
  /**
   * Respond to a DOFS request. 
   * 	Figure out whether it's for a file or an active doc, and whether it's
   * 	to a sub-agent or to /DOFS/ itself.
   */
  public void respond(Transaction request, Resolver res)
       throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Dofs respond...");

    if( !request.isRequest() ) return;
    Pia.debug(this, "After testing is request...");

    URL url = request.requestURL();
    if( url == null )
      return;

    String myname = name();
    String mytype = type();

    String upath  = url.getFile();
    String mypath = pathName();

    Pia.debug(this, "path-->"+upath);
    Pia.debug(this, "myname-->"+myname);
    Pia.debug(this, "mytype-->"+mytype);

    /*
     * Examine the path to see what we have:
     *	 /pathName/   -- this is a real file request.
     *	 /pathName    -- redirect to pathName/ (possibly a mistake)
     *	 otherwise it's an agent document.
     */

    if (!myname.equals(mytype)
	&& (upath.equals(mypath) || upath.startsWith(mypath+"/"))) {
      if (upath.equals("/"+myname)) {
	redirectTo( request, upath+"/" );
	return;
      }
      try {
	if( !request.method().equalsIgnoreCase("put") )
	  FileAccess.retrieveFile(urlToFilename(url), request, this);
	else
	  FileAccess.writeFile(urlToFilename(url), request, this);
      }catch(PiaRuntimeException e){
	throw e;
      }
    } else {
      if (!respondWithDocument(request, res))
	  respondNotFound(request, url.getFile());
    }
  }
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public void initializeEntities() {
    super.initializeEntities();
    addBinding("root", new EntityIndirect("root", this, this));
  }

  /**
   * name and type needs to be set after this
   */
  public Dofs(){
    super("DOFS", "DOFS");
  }

  public Dofs(String name, String type){
    super(name, type);
  }



  /************************************************************************
  ** Attribute access:
  ************************************************************************/

  protected File rootDirFile = null;

  /**
   * @return the path to the DOFS's root directory.
   */
  public String root(){
    if (rootDirFile == null) {
      rootDirFile = fileAttribute("root");
      if (rootDirFile == null) {
	System.err.println("DOFS agent " + pathName() + " has no root attr!");
      }
    }
    if( rootDirFile != null && rootDirFile.exists() ){
      return rootDirFile.getPath();
    } else {
      if (rootDirFile != null)
	System.err.println("can not find root path " + rootDirFile.getPath());
      else
	System.err.println("root is null");
      return null;
    }
  }


  /************************************************************************
  ** File access:
  ************************************************************************/

  /**
   *Transaction handling.
   * === We need to be able to handle CGI scripts and plain HTML
   *     eventually.  We need this for the DOFS, in particular.
   * === URL-to-filetype mappings, whether active docs are permitted,
   *     local active docs, and similar annotations belong in the
   *     Document directory that corresponds to the DOFS agent.
   */

  /**
   * Convert a DOFS URL to a corresponding filename.
   * @param url the URL to convert
   * @return the file name corresponding to this url.
   * 	Returns null unless url path begins with <code>/AgentName</code>
   */
  protected String urlToFilename(URL url){
    if( url == null ) return null;

    String myroot = root();
    String upath = url.getFile();
    String prefix = pathName();

    if( myroot == null ) {
      System.err.println("DOFS agent " + prefix + " has no root!");
      return null;
    }

    if( myroot.endsWith("/") )
      myroot = myroot.substring(0, myroot.length()-1);

    if( !upath.startsWith( prefix ) ) {
      System.err.println("DOFS agent " + prefix + " given bad path " + upath);
      return null;
    }

    return myroot + upath.substring( prefix.length() );
  }


  /**
   * should we ignore this request?
   * for now ignore unless file exists
   */
  protected boolean ignore( Transaction request ){
    String filename = urlToFilename( request.requestURL() );
    File zfile = new File( filename );
    return ! zfile.exists();
  }

}
