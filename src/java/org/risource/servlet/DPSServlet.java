////// DPSServlet.java: PIA DPS Servlet implementation
//	$Id: DPSServlet.java,v 1.5 2000-04-14 23:06:22 steve Exp $

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
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-2000.  All
 * Rights Reserved.
 *
 * Contributor(s): steve@rsv.ricoh.com, paskin@rsv.ricoh.com
 *
 ***************************************************************************** 
*/

package org.risource.servlet;

import org.risource.pia.*;
import org.risource.dps.*;
import org.risource.dps.process.*;
import org.risource.ds.*;
import org.risource.site.*;
import org.risource.dps.tagset.Loader;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.FileOutputStream;

import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

/** A Servlet interface implementation for the PIA (Platform for 
 *	Information Applications).  Unlike PIAServlet, which  is also
 *	provided, DPSServlet supports only XML processing using the 
 *	DPS (Document Processing System).  No XML configuration file
 *	is used -- everything is obtained from its ServletContext and
 *	ServletConfig.
 *
 * <p>	The only information the DPSServlet requires from its environment
 *	is the directory in which to look for tagsets that are not specified
 *	by complete paths, and the table that maps file extensions to tagsets.
 *	The complete list of initialization variables is:
 * <ul>
 *   <li> <code>logfile</code> a file in which to log information.
 *   <li> <code>home</code> the PIA home directory.  Tagsets will be found
 *		in the <code>lib</code> subdirectory unless 
 *		<code>lib</code> is specified.
 *   <li> <code>lib</code> the default directory for tagset lookup.
 *   <li> <code>tagset</code> the default tagset name.
 *   <li> <code>tagset.<em>ext</em></code> tagset for files with an extension
 *		of <code><em>ext</em></code>.
 * </ul>
 *
 * @version $Id: DPSServlet.java,v 1.5 2000-04-14 23:06:22 steve Exp $
 * @author steve@rsv.ricoh.com after paskin@rsv.ricoh.com
 * @see org.risource.servlet.PIAServlet
 * @see org.risource.dps
 * @see org.risource.site
 */
public class DPSServlet 
  extends javax.servlet.http.HttpServlet
  implements javax.servlet.SingleThreadModel 
{
  /** Table that maps extensions into tagset names. */
  protected Table tagsetMap = new Table();

  /** Tagset library directory. */
  protected String tslib = null;

  /** PIA home directory. */
  protected String home = null;

  /** Table that maps extensions into result MIME types. */
  protected Table mimeTypeMap = new Table();

  /** Default tagset name. */
  protected String defaultTagsetName = "xhtml";

  /** Cache for the servlet engine context. */
  protected ServletContext context;

  /** The configuration of this servlet, including any parameters passed
    * to the servlet.
    */
  protected ServletConfig config;

  /** The Site representing this server. */
  protected Site site;

  protected String logFileName = null;
  protected PrintStream logStream = null;

  /** Constructor.
    *
    */
  public DPSServlet() {
    super();
  }

  /** Returns a string containing information about the servlet, such as 
    * its author, version, and copyright. As this method may be called to 
    * display such information in an administrative tool that is servlet 
    * engine specfic, the string that this method returns should be plain 
    * text and not contain markup. 
    */
  public String getServletInfo() {
    return "DPSServlet in "
      + org.risource.Version.SERVER
      + " -- see RiSource.org/PIA for more info.";
  }

  /** An initialization routine which is called once by the HTTP server
    * when the servlet is initialized.
    * 
    * <p>Initializes the servlet and logs the initialization. The init method 
    * is called once, automatically, by the network service each time it 
    * loads the servlet. It is guaranteed to finish before any service 
    * requests are accepted. On fatal initialization errors, an 
    * UnavailableException should be thrown. It will not call the method 
    * System.exit. 
    *
    */
  public void init(ServletConfig conf) throws ServletException
  {
    super.init(conf);
    config = getServletConfig();
    context = config.getServletContext();

    // Start by opening the log file, if there is one.
    String logfile = config.getInitParameter("logfile");
    if (logfile != null) {
      try {
	logFileName = logfile;
	logStream   = new PrintStream(new FileOutputStream(logFileName));
	Loader.setLog(logStream);
	Site.setReporting(logStream);
	Loader.setVerbosity(1);
	Site.setVerbosity(1);
      } catch (IOException e) {
	logFileName = null;
	logStream = null;
	log(e.toString() + " attempting to open log file " + logFileName);
      }
    }
    //get home, etc. out of config 

    home  = config.getInitParameter("home");
    tslib = config.getInitParameter("lib");

    // tell the tagset loader where .../lib is
    if (home != null && tslib == null) {
      String filesep  = System.getProperty("file.separator");
      tslib = home + filesep + "lib";
    }
    if (tslib != null) Loader.setTagsetHome(tslib);

    // get the default tagset name.
    String n = config.getInitParameter("tagset");
    if (n != null) defaultTagsetName = n;

    // Go through the init parameters looking for "tagset.ext" entries.
    Enumeration names = config.getInitParameterNames();
    while (names.hasMoreElements()) {
      n = names.nextElement().toString();
      if (n.startsWith("tagset.")) {
	String v = config.getInitParameter(n);
	n = n.substring("tagset.".length());
	tagsetMap.at(n, v);
      }
    }

    context.log("DPSServlet initialization complete.");
  }

  /** A finalization routine which is called once by the HTTP server
    * when the servlet is terminated.
    *
    * Destroys the servlet, cleaning up whatever resources are being held, 
    * and logs the destruction in the servlet log file. This method is called
    * once, automatically, by the network service each time it removes the 
    * servlet. After destroy is run, it cannot be called again until the 
    * network service reloads the servlet. 
    *
    * When the network service removes a servlet, it calls destroy after all 
    * service calls have been completed, or a service-specific number of 
    * seconds have passed, whichever comes first. In the case of long-running
    * operations, there could be other threads running service requests when 
    * destroy is called. The servlet writer is responsible for making sure 
    * that any threads still in the service method complete. 
    */
  public void destroy()
  {
    super.destroy();
  }

  /** Receives an HTTP GET request from the protected
    * <code>service</code> method and handles the request. 
    * The GET method allows a client to read information
    * from the Web server, passing a query string appended
    * to an URL to tell the server what information
    * to return.
    *
    * <p>The GET method should be safe, that is, without
    * any side effects for which users are held responsible.
    * For example, most form queries have no side effects.
    * If a client request is intended to change stored data,
    * the request should use some other HTTP method.
    *
    * <p>The GET method should also be idempotent, meaning
    * that it can be safely repeated. Sometimes making a
    * method safe also makes it idempotent. For example, 
    * repeating queries is both safe and idempotent, but
    * buying a product online or modifying data is neither
    * safe nor idempotent. 
    *
    * <p>If the request is incorrectly formatted, <code>doGet</code>
    * returns an HTTP BAD_REQUEST message.
    * 
    *
    * @param req	an {@link HttpServletRequest} object that
    *			contains the request the client has made
    *			of the servlet
    *
    * @param resp	an {@link HttpServletResponse} object that
    *			contains the response the servlet sends
    *			to the object
    * 
    * @exception IOException	if an input or output error is 
    *				detected when the servlet handles
    *				the GET request
    *
    * @exception ServletException	if the request for the GET
    *					could not be handled
    *
    * 
    * @see javax.servlet.ServletResponse#setContentType
    *
    */
  protected void doGet(HttpServletRequest req,
		       HttpServletResponse resp) 
    throws ServletException, IOException 
  {
    String docPath = req.getPathTranslated(); // path after servlet name
    String myPath  = req.getServletPath();    // path including servlet name
    //    String cxtPath = req.getContextPath();    // path before servlet name
    String query   = req.getQueryString();
    Enumeration headerNames = req.getHeaderNames();

    if (docPath == null) docPath = context.getRealPath(myPath);

    int dot = docPath.lastIndexOf(".");
    String ext = (dot >= 0 && dot > docPath.lastIndexOf("/"))
      ? docPath.substring(dot+1) : "";

    File f = new File(docPath);
    if (!f.exists()) {
      throw new ServletException("Unable to open file " + docPath);
    }

    String resultType = (String) mimeTypeMap.get(ext);
    if (resultType == null) resultType = context.getMimeType(docPath);
    resp.setContentType(resultType);

    String tagsetName = (String) tagsetMap.get(ext);
    if (tagsetName == null) tagsetName = defaultTagsetName;

    Tagset ts = org.risource.dps.tagset.Loader.loadTagset(tagsetName);
    if (ts == null) {
      throw new ServletException("Unable to load tagset " + tagsetName);
    }
    // should we cache tagsets?

    Parser p = ts.createParser();
    p.setReader(new FileReader(f));
    TopProcessor ii = new TopProcessor();
    ii.setInput(p);
    ii.setTagset(ts);
    ii.define("docName", f.getName());
    ii.define("docPath", docPath);
    Output out = new org.risource.dps.output.ToWriter(resp.getWriter());
    ii.setOutput(out);

    // Initiate the stream processing.
    try {
      ii.run();
    } catch (Exception X) {
      out.close();
      context.log(X, "exception thrown while running the document processor");
      throw new ServletException("A processing error occurred: " + X.toString());
    }
    out.close();
  }

  /** Returns the time the <code>HttpServletRequest</code>
    * object was last modified,
    * in milliseconds since midnight January 1, 1970 GMT.
    * If the time is unknown, this method returns a negative
    * number.
    *
    * <p>Servlet engines that support HTTP GET requests
    * should override <code>getLastModified</code> to
    * provide an accurate object modification time. This
    * makes browser and proxy caches work more effectively,
    * reducing the load on server and network resources.
    *
    *
    * @param req	the <code>HttpServletRequest</code> 
    *			object that is sent to the servlet
    *
    * @return		a <code>long</code> integer specifying
    *			the time the <code>HttpServletRequest</code>
    *			object was last modified, in milliseconds
    *			since midnight, January 1, 1970 GMT, or
    *			-1 if the time is not known
    *
    */
  protected long getLastModified(HttpServletRequest req)
  {
    return super.getLastModified(req);
  }

    /** Receives an HTTP POST request from the protected
      * <code>service</code> method and handles the request.
      * The HTTP POST method allows the client to send
      * data of unlimited length to the Web server once
      * and is useful when posting information such as
      * credit card numbers.
      *
      * <p>This method does not need to be either safe or idempotent.
      * Operations requested through POST can have side effects for
      * which the user can be held accountable, for example, 
      * updating stored data or buying items online.
      *
      * <p>If the HTTP POST request is incorrectly formatted,
      * <code>doPost</code> returns an HTTP BAD_REQUEST message.
      *
      *
      * @param req	an {@link HttpServletRequest} object that
      *			contains the request the client has made
      *			of the servlet
      *
      * @param resp	an {@link HttpServletResponse} object that
      *			contains the response the servlet sends
      *			to the object
      * 
      * @exception IOException	if an input or output error is 
      *				detected when the servlet handles
      *				the request
      *
      * @exception ServletException	if the request for the POST
      *					could not be handled
      *
      *
      * @see javax.servlet.ServletOutputStream
      * @see javax.servlet.ServletResponse#setContentType
      */
  protected void doPost(HttpServletRequest req,
			HttpServletResponse resp) 
    throws ServletException, IOException 
  {
    super.doPost(req, resp);
  }

  /**
    * Receives an HTTP PUT request from the protected
    * <code>service</code> method and handles the
    * request. The PUT operation allows a client to 
    * place a file on the server and is similar to 
    * sending a file by FTP.
    *
    * <p>This method does not need to be either safe or idempotent.
    * Operations that <code>doPut</code> performs can have side
    * effects for which the user can be held accountable. If you
    * override this method, you may want to save a copy of the
    * affected URL in temporary storage.
    *
    * <p>If the HTTP PUT request is incorrectly formatted,
    * <code>doPut</code> returns an HTTP BAD_REQUEST message.
    *
    *
    * @param req	the {@link HttpServletRequest} object that
    *			contains the request the client made of
    *			the servlet
    *
    * @param resp	the {@link HttpServletResponse} object that
    *			contains the response the servlet returns
    *			to the client
    *
    * @exception IOException	if an input or output error occurs
    *				while the servlet is handling the
    *				PUT request
    *
    * @exception ServletException	if the request for the PUT
    *					cannot be handled
    */
  protected void doPut(HttpServletRequest req,
		       HttpServletResponse resp) 
    throws ServletException, IOException 
  {
    super.doPut(req, resp);
  }
}
