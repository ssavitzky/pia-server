// GenericAgent.java
// $Id: GenericAgent.java,v 1.20 1999-05-07 23:36:19 steve Exp $

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
import java.io.InputStream;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import org.risource.Version;

import org.risource.pia.Agent;
import org.risource.pia.agent.AgentMachine;
import org.risource.pia.Transaction;
import org.risource.pia.HTTPRequest;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Machine;
import org.risource.pia.Resolver;
import org.risource.pia.Content;
import org.risource.pia.Authenticator;
import org.risource.pia.Pia;
import org.risource.pia.ContentOperationUnavailable;
import org.risource.pia.FileAccess;
import org.risource.pia.agent.ToAgent;

import org.risource.content.ByteStreamContent;
import org.risource.content.text.ProcessedContent;

import org.risource.ds.Features;
import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Criteria;
import org.risource.ds.Tabular;
import org.risource.ds.Registered;

import org.risource.dps.*;
import org.risource.dps.tagset.Loader;
import org.risource.dps.process.ActiveDoc;
import org.risource.dps.active.*;
import org.risource.dps.namespace.*;
import org.risource.dps.output.DiscardOutput;
import org.risource.dps.input.FromNodeList;

import org.risource.util.NullOutputStream;
import org.risource.util.NameUtils;
import org.risource.util.Utilities;

import java.util.Enumeration;
import java.util.Properties;
import java.io.Serializable;

import org.w3c.www.http.HTTP;

/** The minimum concrete implementation of the Agent interface.  A
 *	GenericAgent is used if no specialized class can be loaded for
 *	an agent; it also serves as the base class for all known Agent
 *	implementations.
 *
 *	@see org.risource.pia.Agent
 */
public class GenericAgent extends BasicNamespace
  implements Agent, Registered, Serializable, Tabular {
  
  /** Class name of GenericAgent. */
  public static String genericAgentClassName = "org.risource.pia.GenericAgent";

  /** Standard option (entity) names. */

  public static String home_doc_dir_name = "home-dir";
  public static String user_doc_dir_name = "user-dir";
  public static String data_dir_name = "data-dir";

  /** Extensions of executable files.
   *	Everything with <code><em>index</em>&gt;=firstDPSType</code> is
   *	processed with the DPS. 
   */
  protected int firstDPSType = 1;
  protected String executableTypes[] = {
    "cgi", 	"xh",	  	"xx"};
  protected String resultTypes[] = {
    null, 	"text/html",	"text/xml"};
  protected String tagsetNames[] = {
    null, 	"xhtml", 	"xxml"};


  /** A ``search path'' of filename suffixes for (possibly) executable files. */
  protected String codeSearch[] = {
    "xh", "xx", "cgi",
    "html", "xml", "htm", "txt", 
  };

  /** A ``search path'' of filename suffixes for data files. */
  protected String dataSearch[] = {
    "html", "xml", "htm", "txt", 
  };

  /** Suffix appended to the agent's name to get to its 'home page' */
  public String homePathSuffix = "/home";

  /** Suffix appended to the agent's name to get to its 'index page' */
  public String indexPathSuffix = "index";

  /** If true, run the request for <code>initialize.??</code> through the 
   *	Resolver.  Otherwise, run the interpretor over it directly.
   */
  public static boolean RESOLVE_INITIALIZE = false;

  /**
   * Attribute table for storing options
   */
  protected Table properties = new Table();

  /**
   * Attribute index - name of this agent
   */
  protected String agentname;

  /**
   * Attribute index - type of this agent
   */
  protected String agenttype;

  /**
   * Attribute index - path to this agent
   */
  protected String path;

  /**
   * Attribute index - files that correspond to agent options.
   */
  protected Table fileTable;

  /** File object that refers to data directory. */
  protected File dataDirFile;

  /** File object that refers to home directory. */
  protected File homeDirFile;

  /** File object that refers to user directory. */
  protected File userDirFile;

  /** List of File objects for document lookup path. */
  protected List documentFileList;

  /**
   * A list of match criteria.  These are matched against the
   * Features of every Transaction to see if this Agent's actOn method
   * should be called.
   */
  protected Criteria criteria;

  /**
   * Attribute index - virtual Machine to which local requests are directed.
   */
  protected AgentMachine virtualMachine;

  /**
   * Act-on Hook.  A pre-parsed piece of active document code that is run when 
   *	a transaction is matched by the agent.  Initialized by setting the 
   *	agent's <code>act-on</code> or <code>_act_on</code> attribute.
   */
  protected Object actOnHook;	

  /**
   * Handle Hook.  A pre-parsed piece of active document code that is run when 
   *	a transaction is being satisfied by the agent.  Initialized by 
   *	setting the agent's <code>handle</code> or <code>_handle</code> 
   *	attribute.
   */
  protected Object handleHook;	// === needs to be Object for old/new

  /**
   * Path prefix for data subdirectory.
   */
  protected String DATA = "~";

  /**
   * Path prefix for home subdirectory.
   */
  protected String HOME = "~";

  /** 
   * Crontab for timed requests.
   */
  protected Crontab crontab;

  /**
   * policy for authentication
   */
 protected Authenticator authPolicy;

  /**
   * Name of file to store content in a PUT request
   */
  protected String destFileName;
  
  /**
   * Default content type for form submissions
   */
  protected final static String DefaultFormSubmissionContentType
    = "application/x-www-form-urlencoded";


  /************************************************************************
  ** Tagsets:
  ************************************************************************/

  /** Shared XHTML tagset for the PIA. 
   *	=== wouldn't have to be transient if timestamps worked. ===
   */
  protected transient static Tagset piaXHTMLtagset = null;

  /** The Tagsets being used by this agent. */
  protected transient Table tagsets = null;

  /** The Tagsets being used by this agent. */
  protected transient Tagset defaultTagset = null;

  /** Running time in milliseconds; used for timings. */
  protected transient long time;

  protected void time() {
    time = System.currentTimeMillis();
  }

  /** Return a string representing the number of seconds 
   *	since <code>time()</code> or <code>timing()</code> was last called. 
   */
  protected String timing() {
    long t = System.currentTimeMillis();
    String s = "" + (t - time)/1000 + "." + ((t - time)%1000 + 5)/10;
    time = t;
    return s;
  }

  /** Load a tagset for this agent.  
   */
  public Tagset loadTagset(ActiveDoc proc, String name) {
    Tagset ts = null;
    if (piaXHTMLtagset != null && tagsets != null) {
      ts = (Tagset) tagsets.at(name);
      if (ts != null) return ts;
    }

    time();
    if (piaXHTMLtagset == null) {
      piaXHTMLtagset = proc.loadTagset("pia-xhtml");
      if (piaXHTMLtagset == null) piaXHTMLtagset = proc.loadTagset("xhtml");
      
      System.err.println(name() + " Loaded tagset '" + piaXHTMLtagset.getName()
			 + "' in " + timing() + " seconds.");
    }

    // Note: this will no longer pick up tagset from type.
    //	as good a reason as any to drop the agent-name prefix.
    ts  = proc.loadTagset(name()+"-" + name);
    if (ts == null && name.equals("xhtml")) ts = piaXHTMLtagset;
    if (ts == null) ts = proc.loadTagset("pia-" + name);
    if (ts == null) ts = proc.loadTagset(name);
    if (ts != null && ts != piaXHTMLtagset) 
      System.err.println(name() + " Loaded tagset '" + ts.getName()
			 + "' in " + timing() + " seconds'.");

    if (tagsets == null) tagsets = new Table();
    if (ts != null) tagsets.at(name, ts);
    return ts;
  }

  /** Make sure that the tagset gets fetched again. */
  public void invalidateTagset() { tagsets = null; defaultTagset = null; }

  /** Make sure that all tagsets get fetched again. */
  public void invalidateAllTagsets() { piaXHTMLtagset = null; tagsets = null; }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Flag that says whether initialization has been done. */
  protected boolean initialized = false;

  /** Flag that says whether to run the initialization file. */
  public boolean runInitFile = true;

  /**
   * Set options with a hash table (typically a form).
   *	Ignore the <code>agent</code> option, which comes from the fact
   *	that most install forms use it in place of <code>name</code>.
   */
  public void parseOptions(Tabular hash){
    if (hash == null) return;
    Enumeration e = hash.keys();
    while( e.hasMoreElements() ){
      Object keyObj = e.nextElement();
      String key = (String)keyObj;
      // Ignore "agent", which is replaced by "name".
      if (key.equalsIgnoreCase("agent")) continue;
      Object v = hash.get(key);
      String value = (v instanceof ActiveNode)
	? ((ActiveNode)v).getNodeValue() : hash.get( key ).toString();
      put(key, value);
    }
  }

  /** 
   * Load entities from an Input
   */
  public void loadFrom(Input in, Context cxt, Tabular opts) {
    ActiveDoc env = ActiveDoc.getActiveDoc(cxt);
    runInitFile = false;

    parseOptions(opts);
    initialize();

    ToAgent loader = new ToAgent(this);
    loader.setContext(cxt);
    Tagset ts = loadTagset(env, "xhtml");
    Processor p = env.subDocument(in, cxt, loader, ts);
    // It's up to the subDocument processor to switch tagsets.
    p = p.subProcess(in, loader, this);
    p.processChildren();
  }

  /** Initialization.  Subclasses may override, but this should rarely
   *	be necessary.  If they <em>do</em> override this method it is
   *	important to call <code>super.initialize()</code>.
   */
  public void initialize(){
    if (initialized) return;

    String n = name();
    String t = type();
    if (t != null && ! t.equals(name()) && ! t.startsWith("/")) {
      t = "/" + t;
      type(t);
    }
    put("name", n);
    put("type", t);
    put("path", path());
    put("pathName", pathName());

    // Fake a request for the initialization file. 
    //    We might not need it, in which case this is a waste, 
    //	  but we need a processor in order to load tagsets.
    String url = pathName() + "/" + "initialize.xh";
    Transaction req = makeRequest(machine(), "GET", url, (String)null, null);
    ActiveDoc   proc = null;
    Resolver    res = Pia.instance().resolver();

    // Force tagsets to load if necessary. 
    if (piaXHTMLtagset == null || findDocument(name()+"-xhtml") != null) {
      proc = makeDPSProcessor(req, res);
    }

    // At this point, we have all our options and files. 
    if (Pia.verbose()) dumpDebugInformation();

    initialized = true;
    ActiveNodeList initHook = getValueNodes(null, "initialize");
    if (initHook != null) {
      proc.setInput(new FromNodeList(initHook));
      proc.setOutput(new DiscardOutput());
      proc.run();
    }

    if (runInitFile) runInitFile();
  }

  /** Run the initialization file. 
   *
   * <p> Clears the <code>runInitFile</code> flag, so that the file will not
   *     be run again when the Agent is loaded from XML.  The initialization
   *     file may set the corresponding attribute, which will force the file
   *     to be re-run whenever the Agent is re-initialized.
   */
  public void runInitFile() {
    runInitFile = false;

    /* Run the initialization in the current thread to ensure that 
     * agents are initialized in the correct order and that no requests
     * are made on partially-initialized agents.
     */
    String    fn = findDocument("initialize");

    if (fn != null) try {
      String url = pathName() + "/" + "initialize";
      if (RESOLVE_INITIALIZE) {	
	// We can force initialization to use the resolver if necessary.
	createRequest("GET", url, (String)null, null );
      } else {
	// Run an XHTML initialization file.  Fake the request.
	Transaction req = makeRequest(machine(), "GET", url,
				      (String)null, null);
	Resolver    res = Pia.instance().resolver();
	ActiveDoc   proc = makeDPSProcessor(req, res);
	org.risource.dps.Parser p = proc.getTagset().createParser();
	p.setReader(new FileReader(fn));
	proc.setOutput(new DiscardOutput());
	proc.setInput(p);
	proc.define("filePath", fn);
	proc.run();
      }
    } catch (Exception e) {
      System.err.println("Exception in " + fn + " initializing " + name());
      System.err.println(e.toString());
      e.printStackTrace();
      System.err.println("PIA recovering; " + name() + " incomplete.");
    }
  }

  /** Make sure that directories get checked again. */
  public void invalidatePaths() {
    dataDirFile = null;
    homeDirFile = null;
    userDirFile = null;
    documentFileList = null;
  }

  /************************************************************************
  ** Debugging:
  ************************************************************************/

  protected void dumpDebugInformation() {
    System.err.println(debugInformation());
  }

  protected String debugDir(String dir, File dirFile) {
    return dirFile.exists()? dir : "(" + dirFile.getPath() + ")";
  }

  protected String debugInformation() {
    String s = "Agent " + pathName() + " name=" + name() + " type=" + type();
    s += "\n      home=" + debugDir(homeDirectory(), homeDirFile);
    s += "\n      user=" + debugDir(userDirectory(), userDirFile);
    s += "\n      data=" + debugDir(dataDirectory(), dataDirFile);
    s += "\n";
    Enumeration e = keys();
    while (e.hasMoreElements()) {
      String key = e.nextElement().toString();
      Object value = get(key);
      if (value instanceof ActiveNode) value = "[Node]";
      if (value instanceof ActiveNodeList) value = "[NodeList]";
      s += "\n      " + name() + ":" + key + "=" + value;
    }
    return s;
  }


  /************************************************************************
  ** Registration and Unregistration:
  ************************************************************************/

  /** Register the Agent with the Resolver. */
  public void register() {
    //    System.err.println("Registering "+ type() + "/" + name());
    Pia.instance().resolver().registerAgent( this );
  }

  /** Remove the Agent from the Resolver's registry */
  public void unregister() {
    Pia.instance().resolver().unRegisterAgent( this );
  }


  /************************************************************************
  ** Creating and Submitting Requests:
  ************************************************************************/

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   */
  public void createRequest(String method, String url,
			    String queryString, String contentType){
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    makeRequest(machine(), method, url, queryString, contentType).startThread();
  }


  /**
   * Given a url string and content create a request transaction.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString content for a POST request.
   *	@param contentType defaults to DefaultFormSubmissionContentType
   */
  public void createRequest(String method, String url,
			    InputContent content, String contentType) {
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    makeRequest(machine(), method, url, content, contentType).startThread();
  }


  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param times a Tabular containing the timing information
   */
  public void createTimedRequest(String method, String url,
				 String queryString, 
				 String contentType, Tabular times) {
    if (crontab == null) crontab = new Crontab();
    crontab.makeEntry(this, method, url, queryString, contentType, times);
  }


  /** Make a new request Transaction on this agent. */
  public Transaction makeRequest(Machine m, String method, String url, 
				 String queryString, String contentType) {
    InputContent c;
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    if (contentType.startsWith("multipart/form-data")) {
      org.risource.pia.Pia.debug(this,"Making new MultipartFormContent");

      c = new MultipartFormContent(Utilities.StringToByteArrayOutputStream(queryString));
    } else {
      // Convert stream to String, taking care of nulls
      if (queryString == null) {
	// Make sure correct constructor of FormContent is called
	c = new FormContent((String)null);
      } else {
	c = new FormContent( queryString );
      }
    }

    return makeRequest(m, method, url, c, contentType);
  }

  /** Make a new request Transaction on this agent. */
  public Transaction makeRequest(Machine m, String method, String url, 
				 InputContent content, String contentType) {
    if (contentType == null) contentType = DefaultFormSubmissionContentType;

    Pia.debug(this, "makeRequest -->"+method+" "+url);
    Pia.debug(this, "Content type "+contentType);

    Transaction request;

    // create things normally gotten from header
    String initString = "HTTP/1.0 "+ method +" "+url;

    // create the request but don't start processing
    request = new HTTPRequest( m, content, false );

    // Changed "Version" to "User-Agent"
    request.setHeader("User-Agent", "PIA/" + pathName());
    //request.setHeader("User-Agent", "Mozilla/4.5b1 [en] (Win95; I)");

    request.setContentType(contentType);
    try {
      request.setContentLength(content.getCurrentContentLength());
    } catch (ContentOperationUnavailable e) {
      // If we cannot find content length, do not set header
    }
    request.setHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
    //    request.setHeader("Agent", name);
    //    request.setContentLength( queryString.length() );

    // to machine should be gotten from url
    //   request.toMachine( Pia.instance().thisMachine() );
    request.setMethod( method );
    request.setRequestURL( url );
    return request;
  }
 
  /** 
   * Handle timed requests.
   */
  public void handleTimedRequests(long time) {
    if (crontab != null) {
      crontab.handleRequests(this, time);
    }
  }

  /************************************************************************
  ** Access to fields:
  ************************************************************************/

  /**
   * @return name of agent
   */
  public String name(){
    return agentname; 
  }

  /**
   * set name of agent
   */
  public void name(String name){
    this.agentname = name;
  }

  /**
   * @return type of agent
   */
  public String type(){
    return agenttype;
  }

  /**
   * set type of agent.
   *	Force the type into path form if it contains a "/".
   */
  public void type(String type){
    if (type == null) type = name();
    else if (type.indexOf("/") > 0) type = "/" + type;
    this.agenttype = type;
  }

  /** 
   * Return the agent's parent in the agent type hierarchy. 
   */
  public Agent typeAgent() {
    // Handle the anomalous (legacy) situation where type == name
    if (type() != null && type().equals(name())) return null;
    return (type() == null)? null : Pia.instance().resolver().agent(type());
  }

  /** 
   * Test for whether a type agent has been specified.
   */
  public boolean typeAgentSpecified() {
    if (type() != null && type().equals(name())) return false;
    else return (type() != null);
  }    

  /**
   * Return the agent's ``mount point'' in the PIA's URL hierarchy. 
   *	If never set, this will be "/".
   *
   * @return agent's mount point in URL hierarchy.
   */
  public String path(){
    if (path == null) {
      path = getObjectString("path");
      if (path == null) path = "/";
      else if (! path.startsWith("/")) path = "/" + path;
      if (! path.endsWith("/")) path += "/";
    }
    return path;
  }

  /**
   * set path
   */
  public void path(String path){
    if (path == null) path = "/";
    else if (! path.startsWith("/")) path = "/" + path;
    if (! path.endsWith("/")) path += "/";
    this.path = path;
    put("path", path);
    put("pathName", pathName());
  }

  /** Return the complete ``pathname'' of this agent: the agent's root URL.
   *
   * @return agent's root URL.
   */
  public String pathName(){
    return path() + name();
  }

  /************************************************************************
  ** File attributes:
  ************************************************************************/

  /** Get an attribute that contains a file name.  
   *
   *	<p> Replaces leading "~" with the user's home directory.
   */
  public File fileAttribute(String key){
    if( fileTable.containsKey( key ) )
      return (File)fileTable.get(key);

    String v = getObjectString( key );
    if (v == null) return null;

    File res = new File(FileAccess.systemFileName(v));
    fileTable.put( key, res );
    
    return res;
  }

  /** Return the system path to the agent's files relative to a given base. */
  public String agentPath(String base) {
    return NameUtils.systemPath(base, pathName());
  }

  /** The agent's <em>home</em> directory: where its documents come from.
   *
   * <p> The default home directory is the PIA agent root + pathName();
   *
   * @return full path to the agent's home directory.
   */
  public String homeDirectory() {
    if (homeDirFile == null) {
      homeDirFile = fileAttribute(home_doc_dir_name);
      if (homeDirFile == null) 
	homeDirFile = new File(agentPath(Pia.instance().piaAgents()));
    }
    return homeDirFile.exists()? homeDirFile.getPath() : null;
  }

  /** The agent's <em>user</em> (customization) document directory.
   *
   *	Documents in the user directory override those in the home directory,
   *	allowing an individual user to customize documents belonging to a
   *	shared agent.
   *	
   * @return full path to the agent's user directory.
   */
  public String userDirectory() {
    if (userDirFile == null) {
      userDirFile = fileAttribute(user_doc_dir_name);
      if (userDirFile == null) 
	userDirFile = new File(agentPath(Pia.instance().usrAgents()));
    }
    return userDirFile.exists()? userDirFile.getPath() : null;
  }

  /** The agent's <em>data</em> directory.
   *
   *  Attempts to create the directory if it does not exist.
   * @return path to a writeable data directory, or null.
   */
  public String dataDirectory(){
    if (dataDirFile == null) {
      dataDirFile = fileAttribute(data_dir_name);
      if (dataDirFile == null) 
	dataDirFile = new File(agentPath(Pia.instance().usrRoot()));
    }
    return dataDirFile.getPath();

    /* It is probably not appropriate to create the data directory here
    if (dataDirFile.exists() || dataDirFile.mkdir()) {
      if (dataDirFile.isDirectory() && dataDirFile.canWrite())
	return dataDirFile.getPath();
    }
    Pia.errLog( name() + "could not find appropriate, writable directory");
    return null;
    */
  }



  /************************************************************************
  ** Matching Features:
  ************************************************************************/

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria(){
    if (criteria == null) criteria = new Criteria();
    return criteria;
  }


  /*************************** *********************************************
  **  Content interactions: 
  ************************************************************************/

 /** 
  * agents can ask content objects to notify them of state changes.
  * this is the callback method for that notification.
  * AgentMachines also use this as the default callback for sending a response.
  *
  * @see org.risource.pia.Content 
  * @see org.risource.pia.agent.AgentMachine
  */
  public void updateContent(Content c, String state, Object arg)
  {
    Pia.debug(this,"updating content object" + c.toString()
	      + " state "+ state, " argument "+arg);
    if(arg instanceof AgentMachine){
      // write content to a null stream for side effects
       try{
	 c.writeTo( new NullOutputStream());
       } catch(Exception e){}
      return;
    }
    // do something based on state of content
  }
  


  /************************************************************************
  ** Machine: 
  ************************************************************************/

  /**
   * Each Agent is associated with a virtual machine which is an
   * interface for actually getting and sending transactions.  Posts
   * explicitly to an agent get sent to the agent's machine (then to
   * the agent's <code>respond</code> method). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine(){
    Pia.debug(this, "Getting agent machine" );
    if( virtualMachine==null ){
      virtualMachine = new AgentMachine( (Agent)this );
      Pia.debug(this, "Creating virtual agent machine" );
    }
    return virtualMachine;
  }

  /**
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine){
    AgentMachine machine = null;
 
    if( virtualMachine==null && vmachine==null ){
      machine = new AgentMachine( (Agent) this );
    }
    if( machine!=null )
      virtualMachine = machine;
  }

  /************************************************************************
  ** Actions and Hooks:
  ************************************************************************/

  /**
   * Act on a transaction that we have matched.  
   */
  public void actOn(Transaction ts, Resolver res){
    if (actOnHook == null) return;
    else if (actOnHook instanceof ActiveNodeList) {
      Pia.debug(this, name()+".actOnHook", "= DPS:"+actOnHook.toString());
      runDPSHook((ActiveNodeList)actOnHook, ts, res); 
    } else {
      Pia.debug(this, name()+".actOnHook", "=???"+actOnHook.toString());
    }
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   *
   * <p> === DPS hooks are untested and probably won't work without
   *	     additional primitives. 
   */
  public boolean handle(Transaction ts, Resolver res) {
    if (handleHook == null)  return false;
    else if (handleHook instanceof ActiveNodeList) {
      Pia.debug(this, name()+".handleHook", "= DPS: "+handleHook.toString());
      runDPSHook((ActiveNodeList)handleHook, ts, res); 
      return true;
    } else {
      Pia.debug(this, name()+".handleHook", "= ???: "+handleHook.toString());
      return false;
    }
  }


  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public void runDPSHook (ActiveNodeList hook,
			  Transaction trans, Resolver res ) {
    if (hook == null || hook.getLength() == 0) return;
    ActiveDoc proc = makeDPSProcessor(trans, res);
    proc.setInput(new FromNodeList(hook));
    proc.setOutput(new DiscardOutput());
    proc.run();
  }

  protected ActiveDoc makeDPSProcessor(Transaction trans, Resolver res) {
    Transaction req  = trans.requestTran();
    Transaction resp = trans.isResponse()? trans : null;

    // === This should be a Hook instead of an ActiveDoc ===
    // === it only works with the xhtml tagset
    ActiveDoc proc = new ActiveDoc(this, req, resp, res);
    proc.setVerbosity((Pia.verbose()? 1 : 0) + (Pia.debug()? 2 : 0));

    Tagset ts = loadTagset(proc, "xhtml");
    if (ts == null) {
      // sendErrorResponse(trans, 500, "cannot load tagset " +tsn);
      return null;
    }

    proc.setTagset(ts);
    return proc;
  }

  /**
   * Respond to a direct request.
   * 	This is called from the agent's AgentMachine
   */
  public void respond(Transaction trans, Resolver res)
       throws PiaRuntimeException{

    Pia.debug(this, "Running active doc...");
    if (! respondWithDocument( trans, res ) ){
      respondNotFound( trans, trans.requestURL().getFile() );
    }
  }

  /************************************************************************
  ** Tabular interface: 
  ************************************************************************/

  /** Retrieve an item by name.  Returns null if no such item exists.  */
  public synchronized Object get(String name) {
    ActiveNode binding = bindings.getBinding(name);
    if (binding == null || !(binding instanceof EntityIndirect)) 
      return binding;

    if (name.equals("criteria")) {
	return (criteria() == null)? "" : criteria().toString();
    }
    return properties.get(name);
  }

  /** Returns an enumeration of the table keys */
  public Enumeration keys() {
    return properties.keys();
  }

  /** Set an item. 
   *	Items that correspond to class member variables require 
   *	special handling.  This is ugly, but there seems to be no good way
   *	to do it in Java.  (A switch would be better than the chained if's.)
   */
  public synchronized void put(String name, Object value) {
    if (name == null) return;
    ActiveNode binding = bindings.getBinding(name);
    if (binding != null	&& binding instanceof EntityIndirect)
      putProperty(name, value);
    else if (value instanceof ActiveNode) 
      setBinding(name, (ActiveNode)value);
    else if (value instanceof ActiveNodeList) 
      setValueNodes(null, name, (ActiveNodeList)value);
    else 
      putProperty(name, value);

  }

  protected void putProperty(String name, Object value) {
    if (value != null) 
      properties.put(name, value);
    else
      properties.remove(name);

    if (name.equals("act-on")) {
      actOnHook = value;
      Pia.debug(this, "Setting ActOn hook", ":="+value);
    } else if (name.equals("handle")) {
      handleHook = value;
      Pia.debug(this, "Setting handle hook", ":="+value);
    } else if (name.equals("criteria")) {
      if (value == null || value.equals("")) criteria = null;
      else criteria = new Criteria(value.toString());
    } else if (name.equals("authentication")) {
      if (authPolicy != null || value == null) {
	//should only allow more stringent authentication
	Pia.debug(this, "attempt to change authPolicy ignored");
	if (value == null) return;
      }
      authPolicy = new Authenticator( "Basic", value.toString());
      Pia.debug(this, "Setting  authentication passwordfile:=" +value);
    }
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String getObjectString(String name) {
    Object o = get(name);
    return (o == null)? null : o.toString();
  }


  /************************************************************************
  ** Namespace Interface:
  ************************************************************************/

  protected void indirect(String name) {
    addBinding(name, new EntityIndirect(name, this, this));
  }

  protected void indirect(String name, boolean entityAlso) {
    setAttributeNode(new AttrIndirect(name, this, this));
    if (entityAlso)
      addBinding(name, new EntityIndirect(name, this, this, name));
  }

  public void initializeEntities() {
    indirect("name", true);
    indirect("path", true);
    indirect("type", true);
    indirect("class", true);
    indirect("criteria");
    indirect("act-on");
    indirect("handle");
    indirect("authentication");
    indirect("home-dir", true);
    indirect("data-dir", true);
    indirect("user-dir", true);
    indirect("pathName", true);
  }

  /** Replace an existing binding. 
   *	DO NOT replace a specialized binding -- just set its value.
   */
  protected void replaceBinding(String name, ActiveNode old,
				ActiveNode binding) {
    if (old instanceof EntityIndirect) {
      ((EntityIndirect)old).setValueNodes(binding.getValueNodes(null));
    } else {
      super.replaceBinding(name, old, binding);
    }
  }

  /************************************************************************
  ** Finding and Executing Documents:
  ************************************************************************/

  /**
   * Send redirection to client
   */
  protected boolean redirectTo( Transaction req, String path ) {
    URL oldUrl = req.requestURL();

    URL redirUrl = null;
    String redirUrlString = null;

    try{
      redirUrl = new URL(oldUrl, path);
      redirUrlString = redirUrl.toExternalForm();
      Pia.debug(this, "The redirected url-->" + redirUrlString);
    }catch(MalformedURLException e){
      String msg = "Malformed URL redirecting to "+path;
      throw new PiaRuntimeException(this, "redirectTo", msg);
    }

    String msg ="Redirecting " + oldUrl.toExternalForm()
      + " to:" + redirUrlString; 

    Pia.debug(this, msg);

    Content ct = new org.risource.content.text.html( new StringReader(msg) );
    Transaction response = new HTTPResponse( Pia.instance().thisMachine,
					     req.fromMachine(), ct, false);
    response.setHeader("Location", redirUrlString);
    // Got a redirect message for all agency agents
    // response.setStatus(HTTP.TEMPORARY_REDIRECT);
    response.setStatus(HTTP.MOVED_PERMANENTLY);
    response.setContentLength( msg.length() );
    response.startThread();
    return true;
  }

  /**
   * Test whether a request is a redirection.
   * @return true if the request has been handled.
   */
  protected boolean isRedirection( Transaction req, String path ) {
    if ( path == null ) return false;

    String originalPath = path;
    String pname = pathName();
    String name = "/" + name();

    Pia.debug(this, "  path on entry -->"+ path);

    if (path.equals(pname) || path.equals(pname + HOME)) {
      if (homePathSuffix != null) path += homePathSuffix;
    } else if (path.equals(pname + "/") || path.equals(pname + HOME + "/")) {
      if (indexPathSuffix != null) path += indexPathSuffix;
    }

    // Handle /name instead of /path/name
    if (path.equals(name) || path.equals(name + HOME)) {
      if (homePathSuffix != null) path += homePathSuffix;
    } else if (path.equals(name + "/") || path.equals(name + HOME + "/")) {
      if (indexPathSuffix != null) path += indexPathSuffix;
    }

    // If the original path is unchanged, we don't have redirection.
    //	  Return false and the caller will handle it.
    if (originalPath == path) return false;

    // Redirect, so check for existence (no point if the file doesn't exist).
    String wholePath = findDocument( path );
    if( wholePath == null ){
      respondNotFound(req, path);
    } else {
      redirectTo(req, path);
    }
    return true;
  }

  /** 
   * Return a list of File objects refering to directories in which 
   *	an agent's documents might be found.
   */
  public List documentSearchPath() {
    return documentSearchPath(false);
  }

  /** 
   * Return a list of File objects refering to directories in which 
   *	an agent's documents might be found.
   */
  public List documentSearchPath(boolean forWriting) {

    /* If we have already found the list, just return it. */
    if (!forWriting && documentFileList != null) return documentFileList;

    List path = new List();

    /* This used to be a complex process: we would check for 
     *	type/name, name, and type under both usrAgents and piaAgents.
     *  Things are simpler now.
     */

    /* These days, all we have to search are userDIR, homeDIR, and
     *	the documentSearchPath of the type agent.
     */

    if (userDirectory() != null) path.push(userDiFile);
    if (!forWriting && homeDirectory() != null) path.push(homeDirFile);

    if (typeAgent() != null) {
      path.append(typeAgent().documentSearchPath(forWriting));
    } else {
      path.push(Pia.instance().usrAgentsDir());
      if (!forWriting) path.push(Pia.instance().piaAgentsDir());
      if (typeAgentSpecified()) {
	// type agent is specified but hasn't started yet:
	// return a stopgap, but don't cache it yet!
	return path;
      } 
    }

    if (!forWriting) documentFileList = path;
    return path;
  }

  /** 
   * Return a suitable path list for a data file
   */
  public List dataSearchPath() {
    List path = new List();
    path.push(dataDirectory());
    return path;
  }

  /** Remove the agent's pathname from the given path. */
  public String stripAgentPath(String path) {
    String mypath = pathName();
    if (path.startsWith(mypath)) {
      path = path.substring(mypath.length());
    } else if (path.startsWith("/" + name())) {
      // === For the moment accept a path starting with just name()
      path = path.substring(name().length() + 1);
    }
    return path;
  }

  /**
   * Find a filename relative to this Agent.
   */
  public String findDocument(String path){
    return findDocument(path, null, false);
  }

  /**
   * Make sure that the parent directory of a given file exists.
   * 
   * @param path the absolute path to a file to be created.
   * @return the original path, or null if directories in the path cannot
   *	be created.
   */
  public String forceParent(String path) {
    File f = new File(path);
    File p = new File(f.getParent());
    if (! p.exists()) {
      if (! userDirFile.exists()) // The userDir didn't exist.
	documentFileList = null; // so play safe and recompute the path.
      p.mkdirs();
    }
    return (p.exists() && p.isDirectory() && p.canWrite())? path : null;
  }

  /**
   * Find a data file. 
   */
  public String findDataFile(String path, String suffixPath[],
			     boolean forWriting) {
    // === should be similar to findDocument below, but with data dir
    
    return forceParent(NameUtils.systemPath(dataDirectory(), path));
  }

  /**
   * Find a filename relative to this Agent.
   */
  public String findDocument(String path, String suffixPath[],
			     boolean forWriting) {
    if ( path == null ) return null;

    if (path.startsWith("/~")) {	// old-style data path
      path = "/" + path.substring(2);
      path = stripAgentPath(path);
      if (path.startsWith(HOME)) path = path.substring(HOME.length());
      path = "~" + path;
    } else if (path.startsWith("/")) {	// Absolute path
      path = stripAgentPath(path);
      // Treat /PATH/NAME~ same as /PATH/NAME -- agent handles if different.
      if (path.startsWith(HOME)) path = path.substring(HOME.length());
      path = path.substring(1);
    }
    // OK: at this point we have a relative path.

    if (path.startsWith(DATA)) {
      path = path.substring(DATA.length());
      if (suffixPath == null) suffixPath = dataSearch;
      return findDataFile(path, suffixPath, forWriting);
    }

    if (suffixPath == null) suffixPath = codeSearch;
    List dirPath = forWriting? dataSearchPath(): documentSearchPath();
    String found =  FileAccess.findFile(path, dirPath, suffixPath);
    if (found != null) return found;
    if (forWriting) { // Didn't find anything, but it may be ok to create it.
      return forceParent(NameUtils.systemPath(userDirFile.getPath(), path));
    }
    return null;
  }

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg ) {
    msg = "Agent=" + name()
      + (! name().equals(type())? " Type=" + type() : "")
      + "<br>\n"	+ msg
      + "<pre>\n"      	+ debugInformation()	+ "</pre>"
      + "\n";
    req.errorResponse(code, msg);
  }

  /**
   * Send error message for document not found
   */
  public void respondNotFound( Transaction req, String path){
    String msg = "File <code>" + path + "</code> not found. "
      + "See this agent's <a href=\"/" + name() + "/\">index page</a> "
      + "for a more information.";
    sendErrorResponse(req, HTTP.NOT_FOUND, msg);
  }

  /**
   * Respond to a transaction with a stream of HTML.
   */
  public void sendStreamResponse ( Transaction trans, InputStream in ) {

    Content c = new org.risource.content.text.html( in );

    Transaction response = new HTTPResponse( trans, false );
    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setContentType( "text/html" );
    response.setContentObj( c );
    response.startThread();
  }

  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public void sendProcessedResponse ( String file, String path, 
				      Transaction trans, Resolver res ) {
    Transaction response = new HTTPResponse( trans, false );
    String tsn = tagsetName(file);
    ActiveDoc proc = new ActiveDoc(this, trans, response, res);
    proc.setVerbosity((Pia.verbose()? 1 : 0) + (Pia.debug()? 2 : 0));

    Tagset ts  = loadTagset(proc, tsn);

    if (ts == null) {
      sendErrorResponse(trans, 500, "cannot load tagset " +tsn);
      return;
    }

    proc.setTagset(ts);
    Content c = new ProcessedContent(this, file, path, proc,
				     trans, response, res);
    response.setStatus( 200 ); 
    response.setHeader("Server", Version.SERVER);
    response.setContentType( resultType(file) );
    response.setContentObj( c );
    response.startThread();
  }

  /**
   * authenticate the origin of a request
   * @return false if cannot authenticate
   */
  public boolean authenticateRequest( Transaction request, String requestedFile){
    if(authPolicy == null) return true;
    return authPolicy.authenticateRequest(request,requestedFile, this);
  }

  /**
   * requests authentication
   */
 public void requestAuthentication ( Transaction trans ) {

   //do we need content for authentication response??
      Pia.debug(this, "Authentication required");
    Transaction response = new HTTPResponse( trans, false );
     if(trans.has("UnauthorizedUser")) 
       response.setStatus( 403 ); 
     else 
       response.setStatus( 401 ); 
    authPolicy.setResponseHeaders(response, this);
    Content c = new org.risource.content.text.StringContent(
		   "Authorization required for access to agent "+ name());
    response.setContentType( "text/plain" );
    response.setContentObj( c );
    response.startThread();
 }

  /**
   * Respond to a request directed at one of an agent's documents.
   *
   * @return false if the file cannot be found.  This allows the caller
   *	to check for a document first, then do something different with
   *	other requests.
   */
  public boolean respondWithDocument(Transaction request, Resolver res){

    URL url = request.requestURL();
    if (url == null) return false;

    return respondWithDocument(request, url.getFile(), res);
  }

  /**
   * Strip off destination file name for put.
   *
   * <p> In order for this to work, the URL must contain an explicit 
   *	 filename extension.  The part <em>after</em> this extension is
   *	 returned in agent.destFileName === not thread safe! ===.
   *	 === What it should really return is the index of the / that 
   *	 === separates the active document from the path extension!
   *
   * @return path with document or cgi
   */
  protected String stripDestFile(String path){
    // If there is a destination file name after the active doc, strip it
    destFileName = null;

    for (int i = 0; i < executableTypes.length; ++i) {
      String ext = "." + executableTypes[i] + "/";
      int pos = path.indexOf(ext);
      if( pos  > 0 ){
	destFileName = path.substring(pos+ext.length());
	return path.substring(0, pos+ext.length());
      }
    }
    return path;
  }

  protected boolean isDPSType(String path) {
    for (int i = firstDPSType; i < executableTypes.length; ++i) {
      if (path.endsWith("."+executableTypes[i])) return true;
    }
    return false;
  }

  protected String resultType(String path) {
    for (int i = 0; i < executableTypes.length; ++i) {
      if (path.endsWith("."+executableTypes[i])
	  && resultTypes[i] != null) return resultTypes[i];
    }
    return "text/html";
  }

  protected String tagsetName(String path) {
    for (int i = 0; i < executableTypes.length; ++i) {
      if (path.endsWith("."+executableTypes[i])
	  && resultTypes[i] != null) return tagsetNames[i];
    }
    return null;
  }

  /** Perform any necessary rewriting on the given path. */
  protected String rewritePath(Transaction request, String path) {
    return path;
  }

  /**
   * Respond to a request directed at one of an agent's documents, 
   * with a (possibly-modified) path.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondWithDocument(Transaction request, String path,
				    Resolver res){

    // If the path includes a query string, remove it now
    int end = path.indexOf('?');
    if(end > 0) path = path.substring(0, end);

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // If there is a destination file name after the active doc's URL, strip it
    // === really should return index of the separating "/".
    path = stripDestFile( path );

    // If the request needs to be redirected, do so now.
    if (isRedirection( request, path )) return true;

    // Rewrite the path if necessary.
    path = rewritePath(request, path);

    // Find the file.  If not found, return false.
    String file = findDocument( path );
    if( file == null ) return false;
      
    if( file != null )
      Pia.debug(this, "The document path is -->"+file);

    // authenticate the requester if necessary;
    // internal requests are not authenticated currently--eg. virtual machines
    if( !(request.fromMachine() instanceof AgentMachine)
	&& !authenticateRequest(request,file)){
      requestAuthentication(request);
      return true; //not clear-returning true because response has been set
    }
      
    if( file.endsWith(".cgi") ){
      try{
	execCgi( request, file );
      }catch(PiaRuntimeException ee ){
	throw ee;
      }
    } else if (isDPSType(file)) {
      sendProcessedResponse(file, destFileName, request, res);
    } else {
      FileAccess.retrieveFile(file, request, this);
    }
    return true;
    }

  protected void execCgi( Transaction request, String file )
       throws PiaRuntimeException
  {
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    OutputStream out;

    try{
      String[] envp = setupEnvironment( request );

      process = rt.exec( file, envp );

      if( request.method().equalsIgnoreCase( "POST" ) ){
	out = process.getOutputStream();
	out.write( request.queryString().getBytes() );
	out.flush();
      }

      in = process.getInputStream();

      // === a ByteStreamContent is wrong for a CGI.
      // === We should really create a Machine and let Transaction
      // === parse the headers it returns.
      Content ct = new ByteStreamContent( in );
      Transaction response = new HTTPResponse( request, ct);
      
    }catch(IOException ee){
      String msg = "can not exec :"+file;
      throw new PiaRuntimeException (this, "respondWithDocument", msg) ;
    }
  }

  /**
   * Prepare environment variables for CGI
   */
  protected String[] setupEnvironment(Transaction req){
    String path = req.requestURL().getFile();

    String[] envp = new String[9];
    envp[0]="CONTENT_TYPE=";
    envp[1]="CONTENT_LENGTH=";
    
    if( req.method().equalsIgnoreCase( "POST" ) ){
      envp[0] += req.contentType();
      envp[1] += req.contentLength();
    }
    envp[2]="GATEWAY_INTERFACE=" + "CGI/1.0";
    envp[3]="SERVER_PORT="       + Pia.instance().port();
    envp[4]="SERVER_PROTO="      + req.version();
    envp[5]="REQUEST_METHOD="    + req.method();
    envp[6]="REMOTE_ADDR=";
    envp[7]="QUERY_STRING=";
    
    if( req.method().equalsIgnoreCase( "GET" ) )
      envp[7] += req.queryString();
    
    envp[8]="PATH_INFO="	+ path;

    for(int i = 0; i < envp.length; i++){
      Pia.debug(this, "The environment var -->" + envp[i]);
    }
    return envp;
  }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  /* name and type should be set latter */
  public GenericAgent(){
    this("GenericAgent", "GenericAgent");
    String cname = getClass().getName();
    if (!cname.equals(genericAgentClassName)) put("class", cname);
  }

  public GenericAgent(String name, String type){
    super("AGENT", name);
    String cname = getClass().getName();
    if (!cname.equals(genericAgentClassName)) put("class", cname);
    
    fileTable = new Table();
    initializeEntities();

    if( name != null ) this.name( name );
    if( type != null )
      this.type( type );
    else
      this.type( name );
  }

}
