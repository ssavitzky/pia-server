// Cache.java
// Cache.java,v 1.4 1999/03/01 23:47:54 pgage Exp

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
 * This is the class for the ``Cache'' agent, which caches
 *	documents requested from outside the agency.  Documents
 *      are written to the PIA agent directory/Cache/yyyy/mm/d
 */

package org.risource.pia.agent;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Locale;


import org.risource.pia.PiaRuntimeException;
import org.risource.pia.GenericAgent;
import org.risource.pia.Content;
import org.risource.pia.Resolver;
import org.risource.pia.Agent;
import org.risource.pia.Pia;
import org.risource.pia.Transaction;
import org.risource.pia.Machine;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Content;
import org.risource.pia.FileAccess;
import org.risource.pia.ContentOperationUnavailable;
import org.risource.pia.HeaderFactory;
import org.risource.pia.Headers;

import org.risource.util.Utilities;
import org.risource.util.CacheTable;

import org.risource.ds.Criterion;

import w3c.www.http.HTTP;

/** Caches each document fetched from outside the Agency.
  * Subsequent requests for the same document will be
  * retrieved from the cache rather than from the original
  * source.  A hashtable maps URLs to cache files; cached
  * files are stored under agent directory/yyyy/m/d.  At
  * agent start-up the cache will be built from the files
  * in today's subdirectory.
  */
public class Cache extends GenericAgent {

  String dataPath       = null;
  URL url               = null;
  CacheTable cacheTable = null;
  boolean doNotCache    = false;

  /** This is called on requests and responses*/
  public void actOn(Transaction ts, Resolver res){
    Pia.debug(this, name()+".actOn");
    
    if(ts.isRequest()) {
      actOnRequest(ts, res);
    }
    else {
      actOnResponse(ts, res);
    }
  }

  /************************************************************************
  ** Agent handler methods
  ************************************************************************/

  /** Does a lookup in the cache.  If request not available,
    * passes output for normal processing.
    */
  protected void actOnRequest(Transaction ts, Resolver res) {
    URL reqURL = ts.requestURL();

    if(ts.hasQueryString()) {
      doNotCache = true;
      return;
    }
    if(ts.method() != null) {
      if(ts.method().equalsIgnoreCase("POST") 
	 || ts.method().equalsIgnoreCase("PUT")) {
	doNotCache = true;
	return;
      }
    }


    // Look it up in the db
    String datafile = (String)cacheTable.get(reqURL.hashCode());
    if(datafile != null) {
      ts.push(this);
    }
    else {
      // document not cached
    }
  }

  /** Gotten a page from somewhere, determine whether it's
      a candidate for the cache.
  */
  protected void actOnResponse(Transaction ts, Resolver res) {
    
    // If this is anything but a request from the server,
    // e.g. a redirect, bail out.
    if(ts.statusCode() != 200) {
      // Tell handle method not get from cache
      doNotCache = true;
      return;
    }

    URL reqURL = ts.requestURL();
    String datafile = (String)cacheTable.get(reqURL.hashCode());
    if(datafile != null) {
      // Data file exists.  It will be retrieved in handle method
      return;
    }
    // Not in cache, so tap contents and cache
    addTap(ts);
  }

  /** Add a tap to the document content data stream so it can be
    * written to a file.
    */
  protected void addTap(Transaction ts) {

    // Set the global variable, so it can be updated by updateContents
    url = ts.requestURL();
    Content con = ts.contentObj();

    // Returns a string representing the path and file name
    // without extension.  Subdirectories exist, but files are
    // still being created.
    dataPath = createFileBasename();

    if(dataPath == null)
      return;

    // Add file extension.  Separate one for header info
    String dataFile = dataPath + ".dat";

    String hdr = ts.headers().toString();
    writeHeader(dataPath, hdr);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(dataFile);
    }
    catch(IOException e) {
      System.err.println("IO error: " + e.getMessage());
      return;
    }
    try {
      con.tapIn(fos);
    }
    catch(ContentOperationUnavailable e) {
      System.err.println("Tap error: " + e.getMessage());
      return;
    }
    try {
      con.notifyWhen(this, "END", fos);
    }
    catch(ContentOperationUnavailable e) {
      System.err.println("Tap error: " + e.getMessage());
    }
  }

  /** Handle the response by reading the data from file and
    * constructing the appropriate objects.
    */
  public boolean handle(Transaction ts, Resolver res) {

    Transaction response = null;
    if(doNotCache == true) {
      doNotCache = false;
      return false;
    }

    String datapath = (String)cacheTable.get(ts.requestURL().hashCode());
    if(datapath == null) {
      return false;
    }
    String datafile = datapath + ".dat";
    File dFile = new File(datafile);
    long mtime = dFile.lastModified();
    FileInputStream fis = null;

    try {
       fis = new FileInputStream(datafile);
    }
    catch(IOException e) {
      System.err.println("IO error: " + e.getMessage());
    }
      
    Content bs = new org.risource.content.text.Default(fis);
    
    response = new HTTPResponse(ts, false);
    response.setContentObj( bs );
    response.setStatus( HTTP.OK );

    Date mDate = new Date( mtime );
    response.setHeader( "Last-Modified", FileAccess.toGMTString(mDate) ); 
    response.setHeader("Source:", "from cache");

    // Create a header from the header file
    FileInputStream fhs = null;
    String hdrFile = datapath + ".hdr";
    try {
      fhs = new FileInputStream(hdrFile);
    }
    catch(IOException e) {
      System.err.println("IO error: " + e.getMessage());
    }
    HeaderFactory hf = new HeaderFactory();
    Headers headers = hf.createHeader(fhs);

    if(headers != null) {
      response.setContentType(headers.contentType());
      response.setContentLength(headers.contentLength());
    }
    response.startThread();
    return true;
  }


  /** Handle callback from content triggered by notifyWhen */
  public void updateContent(Content content, String state, Object object) {

    FileOutputStream fs = (FileOutputStream)object;
    try {
      fs.close();
    }
    catch(IOException e) {
      System.err.println("IO error: " +e.toString() + e.getMessage());
      return;
    }

    // Data was written to file successfully.  Add to lookup table.
    cacheTable.put(url.hashCode(), dataPath);
  }


  /************************************************************************
  ** Database methods
  ************************************************************************/


  /** Creates a data directory based on the agent path/yy/mm/dd.
  */
  protected String createDataDirectory() {
    Calendar today = new GregorianCalendar();

    int subdirLen = 3;
    int subdirs[] = new int[subdirLen];

    subdirs[0]	  = today.get(Calendar.YEAR);
    subdirs[1]	  = today.get(Calendar.MONTH) + 1;
    subdirs[2]	  = today.get(Calendar.DAY_OF_MONTH);

    String sep = System.getProperty("file.separator");
    String cachePath = agentDirectory();
    if(cachePath == null) {
      // Message will have been written to log
      return null;
    }

    for(int i = 0; i < subdirLen; i++) {
      if(i == 0)
	cachePath += subdirs[i];
      else
	cachePath += sep + subdirs[i];

      if(! createSubDir(cachePath)) {
	String msg = "Could not create or write to: " + cachePath;
	Pia.errLog(name() + msg);
	return null;
      }
    }
    return cachePath;
  }

  /** Creates data files for cached data.  Paths are based
      on agent directory/yy/mm/dd/hhmmssms.dat hhmmssms.hdr.  
      If a path root cannot be found, returns null.
  */  
  protected String createFileBasename() {

    String fileBasename = createDataDirectory();
    if(fileBasename == null)
      return null;

    Calendar today = new GregorianCalendar();
    String sep = System.getProperty("file.separator");

    int hh	  = today.get(Calendar.HOUR_OF_DAY);
    int min	  = today.get(Calendar.MINUTE);
    int sec       = today.get(Calendar.SECOND);
    long mil      = today.get(Calendar.MILLISECOND);

    StringBuffer sb = new StringBuffer();
    sb.append(sep);
    sb.append(hh);
    sb.append(min);
    sb.append(sec);
    sb.append(mil);
    
    fileBasename += sb.toString();
    return fileBasename;
  }

  /** Finds an existing subdir or creates it if necessary.
    * returns false, if cannot create or is not writable.
    */
  protected boolean createSubDir(String dirPath) {
    File cacheDirFile = new File(dirPath);
    if(cacheDirFile.exists() || cacheDirFile.mkdir()) {
      if( cacheDirFile.isDirectory() && cacheDirFile.canWrite() ){
	return true;
      }
    }
    return false;
  }

  /** Write the header to file as a string */
  protected void writeHeader(String filepath, String header) {
    FileOutputStream fos = null;
    String hdrPath = new String(filepath + ".hdr");
    try {
      fos = new FileOutputStream(hdrPath);
    }
    catch(IOException e) {
      System.err.println("writeHeader IO error: " + e.getMessage());
      return;
    }
    
    OutputStreamWriter fosWrite = new OutputStreamWriter(fos);
    try {
      fosWrite.write(header, 0, header.length());
      String lf = System.getProperty("line.separator");
      String urlStr = new String("Url: " + url.toString() + lf);
      fosWrite.write(urlStr, 0, urlStr.length());
    }
    catch(IOException e) {
      System.err.println("writeHeader error: " + e.getMessage());
    }
    try {
      fosWrite.flush();
      fosWrite.close();
      fos.close();
    }
    catch(IOException e) {
      System.err.println("writeHeader error: " + e.getMessage());
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * name and type needs to be set after this
   */
  public Cache(){
    super();
  }

  /**
   * initialize 
   */
  public void initialize() {
    Pia.debug(this, "Cache initialize called");
    Criterion.toMatch("IsAgentRequest", false);
    Criterion.toMatch("IsAgentResponse", false);
    String dir = createDataDirectory();
    // Only ever want one of these
    if(cacheTable == null)
      cacheTable = new CacheTable(dir);
    super.initialize();
  }

}


