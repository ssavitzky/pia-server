// Pia.java
// $Id: Pia.java,v 1.30 2000-06-09 16:56:02 steve Exp $

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



package org.risource.pia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.risource.pia.PiaInitException;
import org.risource.pia.Machine;
import org.risource.pia.Resolver;
import org.risource.pia.Logger;
import org.risource.pia.Transaction;

import org.risource.pia.agent.Admin;

import org.risource.ds.Table;
import org.risource.ds.Tabular;
import org.risource.ds.List;

import org.risource.site.*;
import org.risource.pia.site.*;

import org.risource.dps.Tagset;
import org.risource.dps.Parser;
import org.risource.dps.tagset.Loader;

import org.risource.dps.namespace.*;

import org.risource.pia.Configuration;

 /**
  * Pia contains the PIA's main program, and serves as a repository
  *	for the system's global information.  Most initialization is done
  *	by the auxiliary class Setup.
  *
  * <p> At the moment, the Tabular interface is simply delegated to the 
  *	<code>properties</code> attribute.  This will change eventually.
  *
  * @version $Id: Pia.java,v 1.30 2000-06-09 16:56:02 steve Exp $
  * @see org.risource.pia.Setup
  */
public class Pia implements Tabular {

  /************************************************************************
  ** String constants:
  ************************************************************************/

  /**
   * The tail of a property name (the prefix of which is protocol),
   * that indicates a proxy.  The value of the property should be the URL to 
   * proxy through.
   */
  public static final String PROXY = "_proxy";

  /**
   * The name of the property that contains a comma-separated list of
   *	sites not to proxy.
   */
  public static final String NO_PROXY = "no_proxy";

  /**
   * The tail of a property name (the prefix of which is protocol) that 
   * contains a proxy authorization <code>userid:passwd</code> pair.
   * Should be base-64 encoded unless the correspondinng PROXY_AUTH_ENC
   * property is non-null.
   */
  public static final String PROXY_AUTH = "_proxy-auth";

  /**
   * The tail of a property name (the prefix of which is protocol) that 
   * indicates that a corresponding proxy authorization is cleartext, and 
   * so needs to be base-64 encoded before being sent to the proxy.
   */
  public static final String PROXY_AUTH_ENC = "_proxy-auth-encode";

  /************************************************************************
  ** Static Data:
  ************************************************************************/

  private static Pia    instance	= null;
  private static Logger logger 		= null;

  // always print to screen or file if debugToFile is on
  // it used to be possible to set this from the properties
  private static boolean debugToFile= false;

  /** 
   *  the verbosity level.
   */
  protected static int verbosity = 0;

  /**
   * the site root.  
   *
   *<p> Note that this could easily be changed to have type Root
   *	instead of Site, but at the moment there seems to be no 
   *	good reason to do this, since Site is the only implementation.
   */
  protected static Site rootResource = null;

  protected static SiteMachine siteMachine = null;

  /************************************************************************
  ** Private fields:
  ************************************************************************/

  private Properties properties 	= null;
  private PropertyTable siteProperties	= null;
  private Properties specified		= null;

  private String  piaHomePath 		= null;
  private File	  piaHomeDir		= null;

  private String  piaRootPath		= null;
  private String  piaVirtualRootPath	= null;

  private String  piaConfigTagsetPath 	= "pia-config";


  private String  host       	= null;
  private int	  realPort   	= 8888;
  private int     virtualPort	= 8888;
  private int     reqTimeout 	= 50000;

  private Table proxies         = new Table();
  private List  noProxies       = new List();

  private String configFileName = "_subsite.xcf";
  private String siteConfigPath = null;
  private String initDocPath    = "initialize";

  // file separator
  private String filesep  = System.getProperty("file.separator");
  private String home     = System.getProperty("user.home");
  private String userName = System.getProperty("user.name");

  /************************************************************************
  ** Protected fields:
  ************************************************************************/

  /** The name of the class that will perform our setup. 
   *	@see pia.Setup
   */
  protected String setupClassName 	= "org.risource.pia.Setup";

  /** The name of the class that will perform logging.
   *	@see org.risource.pia.Logger
   */
  protected String loggerClassName 	= "org.risource.pia.Logger";

  /** The command-line options passed to Java on startup.
   */
  protected String[] commandLine;

  /**
   * Attribute index - accepter
   */
  protected Accepter accepter;

  /**
   * Attribute index - thread pool
   */
  protected ThreadPool threadPool;

  /**
   *  Attribute index - resolver
   */
  protected Resolver resolver;


  /************************************************************************
  ** Access to Components:
  ************************************************************************/

  /** Return the root of the site resource tree, typed as a Root. */
  public static Root getRoot() { return rootResource; }

  /** Return the root of the site resource tree, typed as a Site. */
  public static Site getSite() { return rootResource; }

  /** Return the PIA home directory. */
  public static String getHome() { return instance.piaHomePath; }

  /** Return the PIA home directory as a File. */
  public static File getHomeDir() { return instance.piaHomeDir; }

  /** Get (construct, if necessary) our SiteMachine.
   *	The SiteMachine is constructed out of the host:virtualPort pair
   *	(which it needs in order to perform redirections), and the 
   *	rootResource, which is what it serves.
   */
  public static SiteMachine getSiteMachine() {
    if (siteMachine == null && rootResource != null) 
      siteMachine = new SiteMachine(instance.host,
				    instance.virtualPort,
				    rootResource);
    return siteMachine;
  }

  /**
   * @return global properties.
   */
  public Properties properties(){
    return properties;
  }

  /**
   * @return thread pool
   */
  public ThreadPool threadPool(){
    return threadPool;
  }

  /**
   * @return resolver
   */
  public static Resolver resolver(){
    return instance.resolver;
  }

  /** Set <code>true</code> after the Admin agent has been started. 
   *	This is used during initialization so that operations that normally
   *	require the Admin agent (such as loading an Agent from XML) can
   *	proceed in its absence. 
   */
  public static boolean adminStarted=false;


  /**
   * @return this host name
   */
  public String host(){
    return host;
  } 

  /**
   * @return the virtual port name
   */
  public String virtualPort(){
    Integer i = new Integer( virtualPort );
    return i.toString();
  } 

  /**
   * @return the virtual port number
   */
  public int virtualPortNumber() {
    return virtualPort;
  }

  /**
   * @return the real port number
   */
  public int realPortNumber() {
    return realPort;
  }

  /**
   * @return request time out
   */
  public int requestTimeout(){
    return reqTimeout;
  }
 
  /** @return the debuggin pseudo-flag. */
  public static final boolean debug() {
    return verbosity > 1;
  }

  /** @return the verbose pseudo-flag. */
  public static final boolean verbose() {
    return verbosity > 0 && (verbosity & 1) != 0;
  }

  /** @return the verbose flag */
  public static final int getVerbosity() {
    return verbosity;
  }

  /**
   * @return the table that maps protocols onto proxy URL's.
   */
  public Table proxies(){
    return proxies;
  } 

  /**
   * List of sites not to proxy.  This is a List and not a Table because
   *	we need to iterate down it looking for a <em>prefix</em> of the
   *	URL we are trying to reach.
   * @return the list of sites not to proxy.
   */
  public List noProxies() {
    return noProxies;
  } 

  /************************************************************************
  ** Tabular interface:
  ************************************************************************/

  /** Access an individual item by name. */
  public Object get(String key) {
    return properties.get(key);
  }

  /** Replace an individual named item with value <em>v</em>. */
  public void put(String key, Object v) {
    properties.put(key, v);
  }

  public String getProperty(String key) { return getProperty(key, null); }

  /** Return an enumeration of all the  keys. */
  public Enumeration keys() {
    return properties.keys();
  }

  /**
   * Get this property value, as a boolean.
   * @param name The name of the property to be fetched.
   * @param def The default value, if the property isn't defined.
   * @return A Boolean instance.
   */
  public boolean getBoolean(String name, boolean def) {
    String v = getProperty(name, null);
    if ( v != null )
      return "true".equalsIgnoreCase(v) ? true : false ;
    return def ;
  }

  /**
   * Set this property value, as a boolean.
   * @param name The name of the property to be defined.
   * @param def The value to define.
   */
  public void setBoolean(String name, boolean def) {
    setProperty(name, def? "true" : "false");
  }

  /**
   * Get this property value, as an integer.
   * @param name The name of the property to be fetched.
   * @param def The default value, if the property isn't defined.
   * @return An integer value.
   */
  public int getInteger(String name, int def) {
    String v = getProperty(name, null);
    if ( v != null ) {
      try {
	if (v.startsWith("0x")) {
	  return Integer.valueOf(v.substring(2), 16).intValue();
	}
	if (v.startsWith("#")) {
	  return Integer.valueOf(v.substring(1), 16).intValue();
	}
	return Integer.valueOf(v).intValue();
      } catch (NumberFormatException e) {
      }
    }
    return def ;
  }

  /**
   * Set this property value, as an integer.
   * @param name The name of the property to be defined.
   * @param def The value to define.
   */
  public void setInteger(String name, int def) {
   setProperty(name, "" + def);
  }


  public String getProperty(String key, String def) {
    return properties.getProperty(key, def);
  }

  public void setProperty(String key, String value) {
    if (value != null) properties.put(key, value);
    else properties.remove(key);
  }

  /************************************************************************
  ** Error Reporting and Messages:
  ************************************************************************/

  /**
   * Fatal system error -- print message and throw runtime exception.
   * Do a stacktrace.  ONLY CALLED IN ACCEPTOR!
   */
  public static void errSys(Exception e, String msg){
    System.err.println("Pia: " + msg);
    e.printStackTrace();
    
    throw new RuntimeException(msg);
  }

  /**
   * Print warning message
   *  ONLY CALLED IN RESOLVER!
   */
  public static void warningMsg( String msg ){
    System.err.println( msg );
  }

  /**
   * Print warning message
   *  ONLY CALLED IN ACCEPTOR!
   */
  public static void warningMsg(Exception e, String msg ){
    System.err.println( msg );
    e.printStackTrace();
  }

  public static void message(int level, String msg) {
    if (level <= verbosity) System.err.println(msg);
  }

  /** 
   * Display a message to the user if the "verbose" flag is set.
   */
  public static void verbose(String msg) {
    if (debug()) debug(msg);
    else if (verbose()) System.err.println(msg);
  }

  /**
   * Dump a debugging statement to the trace file
   *
   */
  public static void debug( String msg )
  {
    if (!debug()) return;
    if( logger != null && debugToFile )
	logger.trace ( msg );
    else
      System.out.println( msg );
  }

  /**
   * Dump a debugging statement to the trace file on behalf of
   * an object
   */
  public static void debug(Object o, String msg) {
    if (!debug()) return;
    if( logger != null && debugToFile )
	logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
    else
      System.out.println("[" +  o.getClass().getName() + "]-->" + msg );
  }

    
  /**
   * Dump a debugging statement to the trace file, with an extra message
   *	if verbose.
   */
  public static void debug( String msg, String vmsg ) {
    if (!debug()) return;
    if (verbose()) msg = (msg == null)? vmsg : msg + vmsg;
    if (msg == null) return;
    if( logger != null && debugToFile )
	logger.trace ( msg );
    else
      System.out.println( msg );
  }

  /**
   * Dump a debugging statement to the trace file on behalf of
   *	 an object, with an extra message if verbose.
   */
  public static void debug(Object o, String msg, String vmsg) {
    if (!debug()) return;
    if (verbose()) msg = (msg == null)? vmsg : msg + vmsg;
    if (msg == null) return;
    if( logger != null && debugToFile )
	logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
    else
      System.out.println("[" +  o.getClass().getName() + "]-->" + msg );
  }

    
  /**
   * Output a message to the log.
   *
   */
  public static void log( String msg )
  {
    if( logger != null )
	logger.log ( msg );
  }

  /**
   * error to log
   *
   */
  public static void errLog( String msg )
  {
    if( logger != null )
	logger.errlog ( msg );
  }

  /**
   * error to log on behalf of an object
   *
   */
  public static void errLog(Object o, String msg )
  {
    if( logger != null )
	logger.errlog ("[" +  o.getClass().getName() + "]-->" + msg );
  }

  /**
   * Get the server URL.
   *	Only called in HTTPRequest for computing full request URL
   */
   public String url() {
     return ( virtualPort != 80 ) 
       ? "http://" + host + ":" + virtualPort() 
       : "http://" + host ;
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /**
   * Initialize the server logger and the statistics object.
   */
    private void initializeLogger() {
      if ( piaRootPath == null || piaRootPath.length() == 0) return;
      if ( loggerClassName != null ) {
	try {
	  logger = (Logger) Class.forName(loggerClassName).newInstance() ;
	  logger.initialize (this) ;
	} catch (Exception ex) {
	  logger = null;
	  warningMsg("Unable to create logger: " + ex.getMessage());
	}
      } else {
	warningMsg("no logger specified, not logging.");
      }
    }

  /** Iterate through the properties looking for keys that end in 
   *	<code>_proxy</code>.  The key <code>no_proxy</code> is assumed to 
   *	contain a comma-separated list of URL's; others are assumed to
   *	contain a single URL.
   */
  protected void initializeProxies(){
    String noproxies = null;
    Enumeration e =  properties.propertyNames();
    while( e.hasMoreElements() ){
      String propName = (String) e.nextElement();
      if (propName.endsWith(NO_PROXY)) {
	noproxies = properties.getProperty(propName);
      } else if (propName.endsWith(PROXY)) {
	/* Remove _proxy and leading dot-separated path from key */
	String scheme = propName.substring(0, propName.indexOf(PROXY));
	if (scheme.indexOf(".") >= 0) 
	  scheme = scheme.substring(scheme.indexOf(".")+1);
	String v = properties.getProperty(propName);
	if (! v.endsWith("/")) v += "/";
	proxies.put(scheme, v);
      } else if (propName.endsWith(PROXY_AUTH_ENC)) {
	// Also look for "_proxy-auth" and "_proxy-auth-encode"
	// If the ...-encode string is present, print out the encoded
	// userID/password string for the user to fix.
	String scheme = propName.substring(0, propName.indexOf(PROXY_AUTH_ENC));
	String auth = properties.getProperty(scheme + PROXY_AUTH);
	if (auth != null) {
	  // The following line replaces one that used the now-deprecated
	  // getBytes(int, int, byte[], int).  Hopefully it will still work.
	  byte bytes[] = auth.getBytes() ;
	  auth = org.risource.util.Utilities.encodeBase64(bytes);
	  System.err.println("*** Please edit the your pia.props file"
			     + " to remove a security hole: ***");
	  System.err.println("    Replace the line defining "
			     + scheme + PROXY_AUTH + " with the following:");
	  System.err.println(scheme+PROXY_AUTH+"="+auth);
	  System.err.println("    Remove the line defining "
			     + scheme + PROXY_AUTH_ENC);
	}
      }
    }
    
    if (noproxies != null) {
      StringTokenizer parser = new StringTokenizer(noproxies, ",");
      while (parser.hasMoreTokens()) {
	String v = parser.nextToken().trim();
	noProxies.push( v );
      }
    }
  }

  /** 
   * Initialize the Pia from the properties that we already have.
   *	Put the values of any properties that defaulted back into
   *	the property table.
   *
   *	@exception PiaInitException if the host cannot be identified.
   */
  protected void initializeProperties() throws PiaInitException {
    String thisHost = null;
    String path = null;

    try {
      thisHost = InetAddress.getLocalHost().getHostName();
    }catch (UnknownHostException e) {
      thisHost = "localhost";
    }

    /* Set global variables from properties. */

    verbosity		= getInteger("verbosity", 0);
    piaHomePath		= getProperty("home", null);
    piaRootPath		= getProperty("root", null);
    piaVirtualRootPath	= getProperty("vroot", null);

    host 		= getProperty("host", thisHost);
    reqTimeout 		= getInteger("req_timeout", 60000);

    // "port" sets _both_ real and virtual ports, either of which can 
    // then be overridden with the appropriate flag.
    int port 		= getInteger("port", 8888);
    virtualPort		= getInteger("virtualport", port);
    realPort		= getInteger("realport", port);
    //loggerClassName 	= getProperty(PIA_LOGGER, loggerClassName);

    siteConfigPath   = getProperty("site", siteConfigPath);
    configFileName   = getProperty("configfile", configFileName);
    initDocPath	     = getProperty("initalize", initDocPath);

    /* Handle -v and -d flags, which affect the verbosity. */
    if (getProperty("debug", null) != null) verbosity += 2;
    if (getProperty("verbose", null) != null) verbosity += 1;

    /* If we still don't know our host name, complain. */

    if( host == null ){
      throw new PiaInitException(this.getClass().getName()
				 +"[initializeProperties]: "
				 +"[host] undefined.");
    }

    /* Try to find the PIA's root directory.  If it doesn't exist, 
     *	complain.
     */
    if (piaHomePath == null) {
      piaHomeDir = null;
      warningMsg("No PIA home directory (-home option) specified" 
		 + " -- proceeding.");
    } else {
      piaHomeDir = new File( piaHomePath );
      if (piaHomeDir.exists() && piaHomeDir.isDirectory()) {
	piaHomePath = piaHomeDir.getAbsolutePath();
      } else {
	piaHomePath = null;
	warningMsg("Cannot locate PIA's home directory, specified as '"
		   + piaHomePath + "' -- proceeding.");
      }
    }

    /* Try to find the site configuration file, if specified.
     *	It may turn out to be a directory, in which case assume
     *	that it was meant to be the root.
     */
    if (siteConfigPath != null && siteConfigPath.length() != 0) {
      File f = new File(siteConfigPath);
      if (f.exists() && f.isDirectory()) {
	message(0, "Specified site configuration file is a directory.");
	piaRootPath = siteConfigPath;
	if (piaVirtualRootPath == null) piaVirtualRootPath = "";
	siteConfigPath = null;
	message(0, "... assuming you meant it to be the real root."
		+  ((piaVirtualRootPath == "")? "  No virtual root!" : ""));
      }
    }
    
    /* Try to find the ``root'' (state) directory. */
  
    File piaRootDir = (piaRootPath == null)? null : new File( piaRootPath );
    if (piaRootDir == null) {
      warningMsg("No root directory specified.");
    } else if (piaRootPath.equals("")) {
      piaRootDir = null;
    } else if ((piaRootDir.exists() || piaRootDir.mkdir())
	&& piaRootDir.isDirectory() && piaRootDir.canWrite()) {
      piaRootPath = piaRootDir.getAbsolutePath();
    } else if (piaRootDir.exists() && ! piaRootDir.isDirectory()) {
      message(-2, "Specified root " + piaRootPath + " is not a directory.");
      System.exit(1);
    } else if (piaRootDir.exists() && ! piaRootDir.canWrite()) {
      warningMsg("Specified root " + piaRootPath + " cannot be written.");
    } else {
      warningMsg("Cannot locate or create root directory " + piaRootPath);
    }

    if (piaRootPath == null && piaVirtualRootPath == null &&
	piaHomePath == null && siteConfigPath == null) {
      message(-2, "Either a PIA home, a configuration file, "
	     + "or a valid root directory must be specified.");
      System.exit(1);
    }
	
    if (piaVirtualRootPath == null) piaVirtualRootPath = piaHomePath;

    if (piaHomePath != null) {
      String tsHome = piaHomePath + filesep + "lib";
      Loader.setTagsetHome(tsHome);
    }

    // Now set the properties that had to be computed.

    setInteger("verbosity", verbosity);
    setProperty("home", piaHomePath);
    setProperty("root", piaRootPath);
    setProperty("vroot", piaVirtualRootPath);
    setProperty("host", host);
    setInteger("virtualport", virtualPort);
    setInteger("realport", realPort);
    setInteger("req_timeout", reqTimeout);
    //setProperty(PIA_LOGGER, loggerClassName);
    setProperty("site", siteConfigPath);
    setProperty("configfile", configFileName);
    setProperty("initialize", initDocPath);

    setProperty("port", virtualPort());
    setProperty("url", url());
    setProperty("rootPath", "/");
  }

  /** Reset any properties that were changed in the site config. file, but
   *	NOT overridden on the command line.
   *
   *<p> It's an open question of whether the command-line overrides,
   *	which are usually temporary, ought to be reflected back into the
   *	configuration file. 
   */
  protected void handleSiteProps() {
    for (int i = 0; i < piaPropTable.length; ++i) {
      String p = siteProperties.getProperty(piaPropTable[i]);
      String q = specified.getProperty(piaPropTable[i]);
      if (p == null) {
	// This property was not specified in the file, so use the default 
      } else if (q != null) {
	// This property was specified on the command line, 
	// which overrides whatever was in the config file.
      } else {
	setProperty(piaPropTable[i], p);
      }
    }
    resetProperties();
  }

  /** Reset variables from the properties. */
  public void resetProperties() {

    /* Set proxy tables from properties that end in "_proxy" */
    initializeProxies(); 

    verbosity		= getInteger("verbosity", verbosity);
    piaHomePath		= getProperty("home", piaHomePath);
    piaRootPath		= getProperty("root", piaRootPath);
    host 		= getProperty("host", host);
    int port 		= getInteger("port", 8888);
    virtualPort		= getInteger("virtualport", port);
    realPort		= getInteger("realport", port);
    reqTimeout 		= getInteger("req_timeout", reqTimeout);
    //loggerClassName 	= getProperty(PIA_LOGGER, loggerClassName);
    //siteConfigPath   = getProperty("site", siteConfigPath);
    configFileName   = getProperty("configfile", configFileName);
    initDocPath	     = getProperty("initalize", initDocPath);
  }

  /** Get a processor suitable for an initialization document.
   */
  public SiteDoc getProcessor(Document doc) {
    if (doc == null) return null;

    String tsname = doc.getTagsetName();
    SiteDoc proc = new SiteDoc(doc, null, null, null, Pia.resolver());
    proc.setVerbosity((Pia.verbose()? 1 : 0) + (Pia.debug()? 2 : 0));

    Tagset ts  = doc.loadTagset(tsname);
    proc.setTagset(ts);
    Reader reader =  doc.documentReader();

    Parser p = ts.createParser();
    p.setReader(reader);
    proc.setInput(p);

    return proc;
  }

  protected void createPiaSite() {
    threadPool   = new ThreadPool();
    resolver     = new Resolver();
    Transaction.resolver = resolver;

    File cfgFile = (siteConfigPath == null)? null : new File(siteConfigPath);
    if (cfgFile != null && !cfgFile.canRead()) {
      System.err.println("Cannot read configuration file " + siteConfigPath);
      System.exit(-1);
    }

    /* make a new property table.  Could initialize from cmd line, 
     *	but whether to do that requires some thought.
     */
    siteProperties = null; //new PropertyTable("/");
    
    rootResource = new PiaSite(piaRootPath, piaVirtualRootPath, null, 
			       configFileName, piaConfigTagsetPath,
			       siteProperties);
    rootResource.setVerbosity(getVerbosity());
    if (siteConfigPath == null) rootResource.loadConfig() ;
    else rootResource.loadConfigFile(cfgFile);
    siteProperties = (PropertyTable) rootResource.getProperties();

    handleSiteProps();

    Resource init = rootResource.locate(initDocPath, false, null);
    Document initDoc = (init == null)? null : init.getDocument();
    SiteDoc proc = getProcessor(initDoc);
    if (proc != null) {
      proc.setOutput(new org.risource.dps.output.DiscardOutput());
      proc.run();
    }
  }

  /** Start the PIA. 
   *
   *<ol>
   *	<li> Create the Site
   *	<li> Start the Accepter
   *</ol>
   */
  protected void startup() {
    createPiaSite();

    try{
      accepter = new Accepter( realPort );
    }catch(IOException e){
      System.err.println("Can not create Accepter: " + e.getMessage());
      if (0 <= e.getMessage().indexOf("denied")) {
	System.err.println(  "  Permission denied means that the port you"
			   + " specified, " + realPort + ",\n"
			   + "  is privileged, i.e. reserved for root.");
      } else if (0 <= e.getMessage().indexOf("in use")) {
	System.err.println(  "  There is already a server listening on "
			   + "the port you specified, " + realPort);
      }
      System.err.println(  "  Try using a different port number:\n" 
			 + "    pia -port nnnn\n"
			 + "  8000+your user ID is a good choice.");
      System.exit(1);
    }

    if (verbosity >= 0) 
      System.err.println("Point your browser to <URL: " + url() + ">");
  }

  /** Initialize the Pia from the properties.  Can be called again if the
   *	properties change.
   */
  protected boolean initialize() {
    try{
      initializeProperties();
      initializeLogger();

      return true;
    }catch(Exception e){
      System.out.println( e.toString() );
      e.printStackTrace();
      return false;
    }
  }

  /************************************************************************
  ** Shutdown and cleanup:
  ************************************************************************/

  /** Perform cleanup operations, and possibly try to restart.
   *	@param restart - if true, try to restart operations.
   */

  protected void cleanup(boolean restart){
    debug(this, "Shutting down accepter...");
    accepter.shutdown();

    /*
    debug(this, "Shutting down threads...");
    threadPool().stop();

    debug(this, "Shutting down resolver...");
    resolver.shutdown();

    // Finally close the log
    if ( logger != null )
      logger.shutdown() ;
    logger = null;


    Piaproperties initProps = properties ;
    properties = null;

    if ( restart ){
      try {
	initialize( initProps );
	accepter.restart();
	resolver.restart();
      } catch(Exception ex){
	System.out.println("*** restart failed.") ;
	ex.printStackTrace() ;
      }
    }
    */

  }
  
  /** Shut down the PIA
   *	@param restart - If true, start back up again.
   */
  public void shutdown(boolean restart){
    cleanup( restart );
  }

  /** Call cleanup when is finalized. */
  protected void finalize() throws IOException{
    cleanup( false );
  }


  /************************************************************************
  ** Creating the Pia instance:
  ************************************************************************/

  /** Create the Pia's single instance. */
  private Pia() {
    instance = this;
  }

  /** Return the Pia's only instance.  */
  public static Pia instance() {
    if( instance != null )
      return instance;
    else{
      String[] args = new String[1];
      args[0] ="PIA_HOME=../../../..";
      main(args);		// fake a call to main (GAAK!)
      return instance;
    }
  }


  static void reportProps(Properties p, String msg) {
    if (verbose()) {
      if (msg != null) verbose(msg);
      Enumeration e =  p.propertyNames();
      while( e.hasMoreElements() ){
	try{
	  String k = (String) e.nextElement();
	  String v = p.getProperty( k );
	  verbose("    " + k + "="+ v);
	}catch( NoSuchElementException ex ){
	}
      }
    }
  }


  /************************************************************************
  ** Main Program:
  ************************************************************************/

  public static void main(String[] args){

    /* Create a PIA instance */

    Pia pia = new Pia();
    pia.commandLine = args;
    pia.debugToFile = false;
    pia.properties = new Properties();

    pia.setProperty("user.name", System.getProperty("user.name"));
    pia.setProperty("user.dir", System.getProperty("user.dir"));
    pia.setProperty("user.home", System.getProperty("user.home"));

    /** Configure it. */

    Configuration config
      = new Configuration(piaEnvTable, piaOptTable, pia.properties);

    if (config.configure(args)) {
      System.out.println("PIA version " + org.risource.Version.VERSION);
      System.out.println("Usage: pia [-option | attr=value]* site-config?" );
      config.usage();

      /** Continue with the initialization if the user requested props. */

      boolean verbose = pia.getBoolean("pia.verbose", false);
      if (verbose) {
	pia.reportProps(pia.properties, "Properties");
      }
      System.exit(1);
    }

    switch (config.otherArguments.nItems()) {
    case 0:
      break;
    case 1:
      if (pia.get("site") == null) { // try site
	pia.setProperty("site", config.otherArguments.at(0).toString());
      } else if (pia.get("root") == null) { // try root
	pia.setProperty("root",  config.otherArguments.at(0).toString());
      } else if (pia.get("vroot") == null) { // try vroot
	pia.setProperty("vroot",  config.otherArguments.at(0).toString());
      } else {
	System.err.println("Can't figure out what the filename argument" +
			   "is supposed to be.\n" +
			   "   site, root, vroot are already specified." );
	System.exit(1);
      }
      break;
    default: 
      System.err.println("Too many extra arguments specified.");
      System.exit(1);
    }

    if (pia.getBoolean("p-version", false)) {
        System.out.println("PIA version " + org.risource.Version.VERSION);
	System.exit(0);
    }

    /** Initialize PIA from its properties. */
    pia.specified = config.specified;

    if (! pia.initialize()) System.exit(1);

    message(-1, "PIA version " + org.risource.Version.VERSION);
    reportProps(instance.properties, "PIA Properties:");
    reportProps(System.getProperties(), "System Properties:");

    try {
      pia.startup();
      //new Thread( new Shutdown() ).start();
    }catch(Exception e){
      System.out.println ("===> Initialization failed, aborting !") ;
      e.printStackTrace () ;
      System.exit(1);
    }
  }

  /************************************************************************
  ** Configuration tables:
  ************************************************************************/

  /** PIA environment table: */
  protected static String[] piaEnvTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
    "PIA_HOME",	"home",
    "PIA_ROOT",	"root",
    "PIA_PORT",	"port",
    "REAL_PORT", "realport",
    "VIRTUAL_PORT", "virtualport",
  };

  /** PIA option table: */
  protected static String[] piaOptTable = {
    "-version", "p-version",  	"bool", 	null,
    "-d",	"debug",	"bool",		null,
    "-v",	"verbose",	"bool",		null,
    "-verbosity", "verbosity",	"number",	"0",
    "-verb",	"verbosity",	"number",	"0",
    "-home",	"home",		"dir",		null,
    "-p",	"port",		"number",	"8888",
    "-port",	"port",		"number",	"8888",
    "-vport",	"virtualport",	"number",	"8888",
    "-rport",	"realport", 	"number",	"8888",
    "-root",	"root",		"dir",		null,
    "-vroot",	"vroot",	"dir",		null,
    "-host",	"host", 	"name",		null,
    "-config",	"configfile",	"file",		"_subsite.xcf",
    "-site",	"site",		"file",		null,
    "", 	"", 		"other", 	null,
  };

  /** PIA property table: */
  protected static String[] piaPropTable = {
    "verbosity", "home", "virtualport", "realport", "root", "vroot",
    "host", "configfile",
    "http_proxy", "ftp_proxy", "gopher_proxy", "no_proxy",
  };

}
