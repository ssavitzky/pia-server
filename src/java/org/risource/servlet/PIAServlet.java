////// PIAServlet.java: PIA Servlet implementation
//	$Id: PIAServlet.java,v 1.8 2000-04-19 00:02:45 steve Exp $

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

import org.risource.Version;
import org.risource.pia.*;
import org.risource.dps.*;
import org.risource.dps.namespace.BasicNamespace;
import org.risource.dps.util.Create;
import org.risource.dps.tagset.Loader;
import org.risource.dps.process.*;
import org.risource.ds.*;
import org.risource.site.*;
import org.risource.util.*;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;

import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

/** A Servlet interface implementation for the PIA (Platform for 
 *	Information Applications).  Unlike DPSServlet, which  is also
 *	provided, PIAServlet includes support for the PIA's site description
 *	package and a subset of the Agent functionality as well as the 
 *	DPS (Document Processing System).
 *
 * @version $Id: PIAServlet.java,v 1.8 2000-04-19 00:02:45 steve Exp $
 * @author steve@rsv.ricoh.com after paskin@rsv.ricoh.com
 * @see org.risource.servlet.DPSServlet
 * @see org.risource.dps
 * @see org.risource.site
 */
public class PIAServlet 
  extends javax.servlet.http.HttpServlet
	  // implements javax.servlet.SingleThreadModel 
{

  /** Cache for the servlet engine context. */
  protected ServletContext context;
  protected static String revision = "$Revision: 1.8 $";

  /** The configuration of this servlet, including any parameters passed
    * to the servlet.
    */
  protected ServletConfig config;

  /** The Site representing this server. */
  protected Site site;

  protected String logFileName = null;
  protected PrintStream logStream = null;

  /** Returns a Namespace containing the attributes of the servlet
   *  configuration. 
   */
  public Namespace getConfigNamespace() {
    Enumeration names = config.getInitParameterNames();
    BasicNamespace ns = new BasicNamespace();
    while (names.hasMoreElements()) {
      String n = names.nextElement().toString();
      ns.setValueNodes(null, n,
		       Create.createNodeList(config.getInitParameter(n)));
    }
    return ns;
  }

  /** Returns a Namespace containing the attributes of the servlet
   *  context. 
   */
  /*
  public Namespace getContextNamespace() {
    Enumeration names = context.getAttributeNames();
    BasicNamespace ns = new BasicNamespace();
    while (names.hasMoreElements()) {
      String n = names.nextElement().toString();
      // === This is bogus: getAttribute returns an Object, not a String ===
      ns.setValueNodes(null, n,
		       Create.createNodeList(context.getAttribute(n).toString()));
    }
    return ns;
  }
  */

  /** Log a message. 
   *
   */
  public void log(String msg) {
    if (logStream != null) {
      logStream.println(msg);
      logStream.flush();
    }
    super.log(msg);
  }

  /** Constructor.
    *
    */
  public PIAServlet() {
    super();
  }

  /** Returns a string containing information about the servlet, such as 
    * its author, version, and copyright. As this method may be called to 
    * display such information in an administrative tool that is servlet 
    * engine specfic, the string that this method returns should be plain 
    * text and not contain markup. 
    */
  public String getServletInfo() {
    return getServletId() + " in "
      + org.risource.Version.SERVER
      + " -- see RiSource.org/PIA for more info.";
  }

  protected String getServletRevision() {
    int i = revision.indexOf(" ");
    int j = revision.lastIndexOf(" ");
    return revision.substring(i+1, j);
  }

  protected String getServletId() {
    return this.getClass().getName() + "/" + getServletRevision();
  }

  /** An initialization routine which is called once by the HTTP server
    * when the servlet is initialized.
    * 
    * <p> Initializes the servlet and logs the initialization. The init method 
    * is called once, automatically, by the network service each time it 
    * loads the servlet. It is guaranteed to finish before any service 
    * requests are accepted. On fatal initialization errors, an 
    * UnavailableException should be thrown. It will not call the method 
    * System.exit. 
    *
    * <p> Servlet initialization parameters look like this in the properties
    *	  file:
    * <pre>
    # These properties define init parameters for each servlet that is invoked 
    # by its classname.
    # Syntax: servlet.[classname].initArgs=[name]=[value],[name]=[value],...
    # Default: NONE
    # servlet.org.fool.Dummy.initArgs=message=I'm a dummy servlet
    * </pre>
    *
    * <p> It seems safe to assume that what's passed in <code>config</code>
    *	  will be the simple name=value pairs above.
    *
    * <p> The PIAServlet looks for the following properties:
    * <ul>
    *	<li> <code>home</code>
    *	<li> <code>root</code>
    *	<li> <code>vroot</code>
    *	<li> <code>configfile</code> subsite config file name
    *	<li> <code>siteconfig</code> full path to the site config file
    *	<li> <code>logfile</code>    full path to log file (optional)
    * </ul>
    *
    * <p> If none of the above properties are present, the PIA will set itself 
    *	up with <code>root</code> pointing to the server's root (i.e. the
    *	directory that <code>context</code> maps <code>/</code> into).
    *
    * <p> Note that if a log file is not specified, some logging information
    *	  will end up being sent to STDERR, which may be redirected to 
    *	  <code>/dev/null</code> in a typical web server.
    */
  public void init(ServletConfig conf) throws ServletException
  {
    super.init(conf);
    //config = conf;
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

    if (context == null) log("*** context is null");

    Enumeration names = config.getInitParameterNames();
    if (!names.hasMoreElements()) { 
      log("*** no initialization parameters!");
    } else {
      log("Servlet initialization parameters:");
    }
    while (names.hasMoreElements()) {
      String n = names.nextElement().toString();
      log("    " + n + "=" + config.getInitParameter(n));
    }

    //get home, etc. out of config 

    String home = config.getInitParameter("home");
    String root = config.getInitParameter("root");
    String vroot = config.getInitParameter("vroot");
    String configfile = config.getInitParameter("configfile");
    String siteconfig = config.getInitParameter("siteconfig");

    // tell the tagset loader where .../lib is
    if (home != null) {
      String filesep  = System.getProperty("file.separator");
      String tsHome = home + filesep + "lib";
      Loader.setTagsetHome(tsHome);
    }

    // Root defaults to server root.  (Maybe only if configfile == null?)
    if (root == null) {
      root = context.getRealPath("/");
    }

    log("Site: home=" + home +
	",root=" + root + 
	",vroot=" + vroot + 
	",configfile=" + configfile +
	",siteconfig=" + siteconfig
	);

    // set up the Site
    site = new Site(root, vroot, null, configfile, null, null);
    if (siteconfig == null) site.loadConfig() ;
    else site.loadConfigFile(new File(siteconfig));

    log("initialization complete.");
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
    if (logStream != null) {
      try { logStream.close(); } catch (Exception e) {}
    }
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
    String uri 	   = req.getRequestURI(); // whole request after protocol

    String docPath = req.getPathInfo(); 	// path after servlet name
    String myPath  = req.getServletPath();    	// path including servlet name
    //    String cxtPath = req.getContextPath();    // path before servlet name
    String query   = req.getQueryString();

    //Enumeration headerNames = req.getHeaderNames();

    try {
      if (!respondWithDocument(req, docPath, query, resp)) {
	sendErrorResponse(resp, 404, docPath + " not found");
      }
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      sendErrorResponse(resp, 500, "exception in respondWithDocument: "
			+ reportString(e));
    }
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
    Document doc = locateDocument(req);
    if (doc == null) return -1;

    // === really need better indication of whether the doc. is volatile.
    if (doc.getTagsetName() != null) return -1;
    return doc.getLastModified();
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
    this.doGet(req, resp);
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



  /************************************************************************
  ** Handling Requests using Site:
  ************************************************************************/

  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public boolean sendProcessedResponse (Document doc, String ctype, String tsn,
					HttpServletRequest req,
					HttpServletResponse resp )
    throws IOException
  {
    // really ought to be ServletProcessor
    ServletDoc proc = new ServletDoc(doc, this, req, resp);

    //proc.setVerbosity(Pia.getVerbosity());

    Tagset ts  = doc.loadTagset(tsn);

    if (ts == null) {
      sendErrorResponse(resp, 500, "cannot load tagset " +tsn);
      return true;
    }

    proc.setTagset(ts);
    Reader reader =  doc.documentReader();

    if (reader == null) {
      sendErrorResponse(resp, 500, "Cannot read document " + doc.getPath());
      return true;
    }

    resp.setStatus( 200 ); 
    resp.setContentType( ctype );
    resp.setHeader("Servlet", getServletId() +
		   " in " + Version.SERVER);

    // resp.setDateHeader("Last-Modified", doc.getLastModified()); 

    Parser p = ts.createParser();
    Reader in = doc.documentReader();
    p.setReader(in);
    proc.setInput(p);

    Output out = new org.risource.dps.output.ToWriter(resp.getWriter());
    proc.setOutput(out);

    // Initiate the stream processing.

    proc.run();
    out.close();
    try { in.close(); } catch (IOException e) {}

    return true;
  }

  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public boolean sendStreamResponse (Document doc, String ctype,
				     HttpServletRequest req,
				     HttpServletResponse resp )
    throws IOException
  {
    OutputStream out = resp.getOutputStream();
    InputStream  in  = doc.documentInputStream();

    int block = 2048;
    byte buf[] = new byte[block];
    int n;

    resp.setStatus( 200 ); 
    resp.setContentType( ctype );
    resp.setDateHeader("Last-Modified", doc.getLastModified()); 
    resp.setHeader("Servlet", getServletId() +
		   " in " + Version.SERVER);

    while ((n = in.read(buf)) > 0) { 
      out.write(buf, 0, n);
    }
    try { in.close();  } catch (IOException e) {}
    try { out.close(); } catch (IOException e) {}

    return true;
  }

  /** Test whether the given resource is in a home directory which
   *	should be redirected.
   */
  protected boolean redirectHome(Resource resource) {
    return true;		// for now, always redirect
  }

  /** Compute the appropriate redirection for a path. */
  protected String getRedirection(Resource resource, String path) {
    if (path.startsWith("/~") && redirectHome(resource)) {
      if (resource.isContainer() && !path.endsWith("/")) {
	return resource.getPath() + "/";
      } else {
	return resource.getPath();
      }
    } else if (resource.isContainer() && !path.endsWith("/") ) {
      return path + "/";
    } else {
      return null;
    }
  }

  
  /**
   * Send redirection to client
   */
  protected boolean sendRedirection(HttpServletRequest req, String path,
				    HttpServletResponse resp)
    throws IOException
  {
    if (path == null) path = "";
    path = req.getServletPath() + "/" +  path;
    resp.sendRedirect(path /*resp.encodeRedirectURL(path)*/);

    return true;
  }

  public Document locateDocument(HttpServletRequest request) {
    String path = request.getPathInfo();

    if (path == null) {
      path = "/";
    }

    if (site == null) {
      log("*** site is null");
      return null;
    }

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // Locate the resource.  Fail if it can't be found.
    Resource resource = site.locate(path, false, null);
    if (resource == null) return null;

    // If the resource is hidden, we're not supposed to show it.
    if (resource.isHidden()) return null;

    return resource.getDocument();
  }

  /**
   * Respond to a request directed at one of an site's documents, 
   * with a (possibly-modified) path.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondWithDocument(HttpServletRequest request,
				     String path, String query,
				     HttpServletResponse resp)
    throws IOException
  {
    // Redirect empty path (actually just the servlet's URL) to "/"
    if (path == null) return sendRedirection(request, null, resp);

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // Sanity checking.
    if (site == null) {
      return sendErrorResponse(resp, 500, "site is null");
    }

    // Locate the resource.  Fail if it can't be found.
    Resource resource = site.locate(path, false, null);
    if (resource == null) return false;

    // If the resource is hidden, we're not supposed to show it.
    if (resource.isHidden()) return false;

    // If the request needs to be redirected, do so now.
    String redirection = getRedirection(resource, path);
    if (redirection != null)
      return sendRedirection(request, redirection, resp);

    /* === no resolver, so no way to get authenticator yet.
       === In any case, the servlet engine should do most of the work.

    // See if the request needs to be authenticated.
    Agent auth = res.getAuthenticatorForResource(resource);
    if (auth != null && !auth.authenticateRequest(request, resource)) {
      requestAuthentication(request, auth, resource, resp);
      return true; // returning true because response has been set
    }
    === */

    Document doc = resource.getDocument();
    if (doc == null) {
      resource = resource.locate("home", false, null);
      if (resource == null) {
	log("*** cannot locate home in " + path);
	if (resource == null) return false;
      }
      doc = resource.getDocument();
      if (doc == null) {
	log("*** no document for home in " + path);
	return false;
      }
    }

    String tsname = doc.getTagsetName();
    String ctype  = doc.getContentType();
    if (ctype == null) log("*** null content type for " + path);
    if (ctype == null) ctype = doc.getContentTypeFor(".*");
    if (ctype == null) ctype = "text/plain";

    if( tsname == null || tsname.length() == 0 ) {
      return sendStreamResponse(doc, ctype, request, resp);
    } else if (tsname.startsWith("!")) {
      sendErrorResponse(resp, 500, "servlet cannot handle CGI request");
      log("servlet cannot handle CGI request");
    } else if (tsname.startsWith("#")) {
      return false;
    } else {
      return sendProcessedResponse(doc, ctype, tsname, request, resp);
    }
    return true;
  }

  /** 
   * Send an error response.
   */
  public boolean sendErrorResponse(HttpServletResponse resp,
				   int code, String msg)
    throws IOException
  {
    log("*** Error " + code + ": " + msg);
    resp.sendError(code, msg);
    return true;
  }

  /** 
   * Obtain a complete report on an exception as a String;
   */
  public static String reportString(Exception e) {
    java.io.StringWriter s = new java.io.StringWriter();
    //s.write(e.toString() + " " + e.getMessage());
    e.printStackTrace(new java.io.PrintWriter(s));
    return s.toString();
  }


}
