// GenericAgent.java
// GenericAgent.java,v 1.73 1999/03/01 23:47:26 pgage Exp

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

import org.risource.tf.UnknownNameException;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import org.risource.pia.Agent;
import org.risource.pia.agent.AgentMachine;
import org.risource.pia.Transaction;
import org.risource.pia.HTTPRequest;
import org.risource.pia.HTTPResponse;
import org.risource.pia.Machine;
import org.risource.pia.Resolver;
import org.risource.pia.Content;
import org.risource.content.ByteStreamContent;
import org.risource.content.text.ProcessedContent;
import org.risource.pia.Authenticator;
import org.risource.pia.Pia;
import org.risource.pia.ContentOperationUnavailable;

import org.risource.ds.Features;
import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Criteria;
import org.risource.ds.Tabular;
import org.risource.ds.Registered;

import org.risource.dps.Tagset;
import org.risource.dps.tagset.Loader;
import org.risource.dps.process.ActiveDoc;

import org.risource.dom.NodeList;

// import org.risource.sgml.SGML;
import org.risource.util.NullOutputStream;

import org.risource.util.Utilities;

import java.util.Enumeration;
import java.util.Properties;
import java.io.Serializable;

// import org.risource.interform.Run;
import w3c.www.http.HTTP;

/** The minimum concrete implementation of the Agent interface.  A
 *	GenericAgent is used if no specialized class can be loaded for
 *	an agent; it also serves as the base class for all known Agent
 *	implementations.
 *
 *	@see org.risource.pia.Agent
 */
public class GenericAgent implements Agent, Registered, Serializable {
  
  /** Standard option (entity) names. */

  public static String agent_code_dir_name = "agentDIR";
  public static String agent_data_dir_name = "userDIR";
  public static String user_code_dir_name = "userAgentDIR";

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

  private String filesep = System.getProperty("file.separator");
  public static boolean DEBUG = false;  

  /** Suffix appended to the agent's name to get to its 'home InterForm' */
  public String homePathSuffix = "/home";

  /** Suffix appended to the agent's name to get to its 'index InterForm' */
  public String indexPathSuffix = "index";

  /** If true, run the request for <code>initialize.??</code> through the 
   *	Resolver.  Otherwise, run the interpretor over it directly.
   */
  public static boolean RESOLVE_INITIALIZE = false;

  /**
   * Attribute table for storing options
   */
  protected Table attributes = new Table();

  /**
   * Attribute index - name of this agent
   */
  protected String agentname;

  /**
   * Attribute index - type of this agent
   */
  protected String agenttype;

  /**
   * Attribute index - type of this agent
   */
  protected String version;

  /**
   * Attribute index - directory that this agent can write to
   */
  protected Table dirTable;

  /**
   * Attribute index - files that this agent can write to
   */
  protected Table fileTable;


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
   * Act-on Hook.  A pre-parsed piece of InterForm code that is run when 
   *	a transaction is matched by the agent.  Initialized by setting the 
   *	agent's <code>act-on</code> or <code>_act_on</code> attribute.
   */
  protected Object actOnHook;	

  /**
   * Handle Hook.  A pre-parsed piece of InterForm code that is run when 
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

  /** Load a tagset for this agent.  
   */
  public Tagset loadTagset(ActiveDoc proc, String name) {
    Tagset ts = null;
    if (piaXHTMLtagset != null && tagsets != null) {
      ts = (Tagset) tagsets.at(name);
      if (ts != null) return ts;
    }

    if (piaXHTMLtagset == null) {
      piaXHTMLtagset = proc.loadTagset("pia-xhtml");
      if (piaXHTMLtagset == null) piaXHTMLtagset = proc.loadTagset("xhtml");
    }

    ts  = proc.loadTagset(type()+"-" + name);
    if (ts == null && name.equals("xhtml")) ts = piaXHTMLtagset;
    if (ts == null) ts = proc.loadTagset("pia-" + name);
    if (ts == null) ts = proc.loadTagset(name);

    if (tagsets == null) tagsets = new Table();
    if (ts != null) tagsets.at(name, ts);
    return ts;
  }

  /** Make sure that the tagset gets fetched again. */
  public void invalidateTagset() { tagsets = null; }

  /** Make sure that all tagsets get fetched again. */
  public void invalidateAllTagsets() { piaXHTMLtagset = null; tagsets = null; }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  protected boolean initialized = false;

  /** Initialization.  Subclasses may override, but this should rarely
   *	be necessary.  If they <em>do</em> override this method it is
   *	important to call <code>super.initialize()</code>.
   */
  public void initialize(){
    if (initialized) return;

    String n = name();
    String t = type();
    put("name", n);
    put("type", t);

    String url = "/" + n + "/" + "initialize";
    if (! n.equals(t)) url = "/" + t + url;

    // Fake a request for the initialization file. 
    //    We might not need it, in which case this is a waste.
    Transaction req = makeRequest(machine(), "GET", url, (String)null, null);

    if( DEBUG ) {
      System.out.println("[GenericAgent]-->"+"Hi, I am in debugging mode." +
			 "No interform request is put onto the resolver.");
      return;
    }

    ActiveDoc   proc = null;
    Resolver    res = Pia.instance().resolver();

    // Force tagsets to load if necessary. 
    if (piaXHTMLtagset == null || findInterform(name()+"-xhtml") != null) {
      System.err.println(name() + " Loading tagset(s).");
      proc = makeDPSProcessor(req, res);
    }

    /* Run the initialization in the current thread to ensure that 
     * agents are initialized in the correct order and that no requests
     * are made on partially-initialized agents.
     */
    String    fn = findInterform("initialize");
    if (fn != null) try {
      if (RESOLVE_INITIALIZE) {	
	// We can force initialization to use the resolver if necessary.
	createRequest("GET", url, null, null );
      } else {
	// Run an XHTML initialization file.
	if (proc == null) {
	  // make a new processor if we haven't done so already
	  proc = makeDPSProcessor(req, res);
	}
	org.risource.dps.Parser p = proc.getTagset().createParser();
	p.setReader(new FileReader(fn));
	proc.setOutput(new org.risource.dps.output.DiscardOutput());
	proc.setInput(p);
	proc.define("filePath", fn);
	proc.run();
      }
    } catch (Exception e) {
      System.err.println("Exception while initializing " + name());
      System.err.println(e.toString());
      e.printStackTrace();
      System.err.println("PIA recovering; " + name() + " incomplete.");
    }
    initialized = true;
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
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString content for a POST request.
   *	@param contentType defaults to DefaultFormSubmissionContentType
   */
  public void createRequest(Machine m, String method, String url,
			    String queryString, String contentType) {
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    makeRequest(m, method, url, queryString, contentType).startThread();
  }


  /**
   * Given a url string and content create a request transaction.
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString content for a POST request.
   *	@param contentType defaults to DefaultFormSubmissionContentType
   */
  public void createRequest(Machine m, String method, String url,
			    InputContent content, String contentType) {
    if (contentType == null) contentType = DefaultFormSubmissionContentType;
    makeRequest(m, method, url, content, contentType).startThread();
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

    // Changed "Version" to "User-Agent", not sure why
    // it was version in the first place as it gets assigned
    // to PIA/{agent name} 
    request.setHeader("User-Agent", version());
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
   * set type of agent
   */
  public void type(String type){
    this.agenttype = type;
  }

  /**
   * @return version
   */
  public String version(){
    if(version ==null){
      StringBuffer v = new StringBuffer( "PIA/" );
      if( !type().equalsIgnoreCase( name() ) )
	v.append( type() + "/" );
      v.append( name() );
      return new String( v );
    }
    else
      return version;
  }

  /**
   * set version
   */
  public void version(String version){
    if( version != null)
      this.version = version;
  }

  /************************************************************************
  ** File attributes:
  ************************************************************************/

  // === file attributes: used only in Dofs (for root) ===
  // === ... should really use the regular attributes.

  /**
   * set a file attribute
   *
   */
  public void fileAttribute(String key, List value){
    fileTable.put( key, value ); 
  }

  /**
   * Get an attribute that contains a file name.  
   *
   *	<p> Replaces leading "~" with the user's home directory.
   */
  public List fileAttribute(String key){
    String v = null;
    List res = null;

    if( fileTable.containsKey( key ) )
      res = (List)fileTable.get(key);
    if( res == null ){
      v = getObjectString( key );
      if ( v!=null && v.startsWith("~/") ){
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  res = new List();
	  res.push( v );
	  fileTable.put( key, res );
      } else if( v != null){
	res = new List();
	res.push( v );
	fileTable.put( key, res );
      }
      
    }
    
    return res;
  }

  /**
   * set a directory attribute
   *
   */
  public void dirAttribute(String key, List value){
    dirTable.put( key, value ); 
  }

  /**
   * Retrieve an attribute that contains a directory or search path. 
   *
   * <p> Makes sure that each directory name ends in a file separator 
   *	 character, and replaces leading "~" with the user's home directory. 
   */
  public List dirAttribute(String key){
    String v = null;
    List res = null;

    if (dirTable.containsKey(key))
      res = (List)dirTable.get(key);
    if (res == null) {
      v = getObjectString(key);
      if (v == null) return null;
      if (!v.endsWith(filesep)) { v = v + filesep; }
      if (v.startsWith("~"+filesep)) {
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  if(!v.endsWith(filesep) ) { v = v + filesep; }
	  res = new List();
	  res.push( v );
	  dirTable.put( key, res );
      } else {
	res = new List();
	res.push( v );
	dirTable.put( key, res );
      }
    }

    return res;
  }

  /**
   *  Returns a path to a directory that we can write data into.
   *  <p> Searches for a writeable directory in the following order:
   *  <ol>
   *    <li> usrRoot/agentName/
   *    <li> usrRoot/agentType/
   *    <li> /tmp/agentName/  
   *  </ol>
   *  A directory is created if none of the above exist.
   *
   * <p> === This needs to use agent-specific and global defaults.
   *	 === should probably try usrRoot/agentType/agentName first.
   *
   * @return the first qualified directory out of the possible three above.
   */
  public String agentDirectory(){
    List directories = dirAttribute(agent_data_dir_name);
    if( directories!=null && directories.nItems() > 0)
      return (String)directories.at(0);

    String name = name();
    String type = type();
    String root = Pia.instance().usrRoot();

    String[] possibilities = { root + filesep + name() + filesep,
			       root + filesep + type() + filesep,
			       filesep + "tmp" + filesep + name() + filesep };

    for(int i = 0; i < possibilities.length; i++){
      String dir = possibilities[i];

      File myFileDir = new File( dir );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  List dirs = new List();
	  dirs.push( dir );
	  dirAttribute( agent_data_dir_name, dirs );
	  return dir;
	}
      }
    }

    Pia.errLog( name()+ "could not find appropriate, writable directory");
    return null;
  }


  /**
   *  Returns a path to a directory that we can write InterForms into.
   *  Creates one if necessary, starting with the following directory in order:
   *  usrRoot/Agents/agentName/,
   *  usrRoot/Agents/agentType/,
   *  /tmp/Agents/agentName
   * @return the first qualified directory out of the possible three above.
   * A directory is qualified if it can be writen into.
   */
  public String agentIfDir(){
    List directories = dirAttribute(user_code_dir_name);
    if( directories!=null && directories.nItems() > 0)
      return (String)directories.at(0);

    String name = name();
    String type = type();
    String root = Pia.instance().usrRoot();
    root += filesep + "Agents";

    String[] possibilities = { root + filesep + name() + filesep,
			       root + filesep + type() + filesep,
			       filesep + "tmp" + filesep + name() + filesep };

    for(int i = 0; i < possibilities.length; i++){
      String dir = possibilities[i];

      File myFileDir = new File( dir );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  List dirs = new List();
	  dirs.push( dir );
	  dirAttribute( user_code_dir_name, dirs );
	  return dir;
	}
      }
    }

    Pia.errLog( name()+ "could not find appropriate, writable directory");
    return null;
  }



  /**
   * returns the base url (as string) for this agent
   * optional path argument just for convenience--
   * returns full url for accessing that file
   */
  public String agentUrl(String path){
    String url = Pia.instance().url() + "/" + name() + "/";

    if( path!= null )
      url += path;

    return url;
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
   * the agent's interform_request method). Other requests can be
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
    else if (actOnHook instanceof NodeList) {
      Pia.debug(this, name()+".actOnHook", "= DPS:"+actOnHook.toString());
      runDPSHook((NodeList)actOnHook, ts, res); 
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
    else if (handleHook instanceof NodeList) {
      Pia.debug(this, name()+".handleHook", "= DPS: "+handleHook.toString());
      runDPSHook((NodeList)handleHook, ts, res); 
      return true;
    } else {
      Pia.debug(this, name()+".handleHook", "= ???: "+handleHook.toString());
      return false;      // run DPS hook!
    }
  }


  /**
   * Respond to a transaction with a stream of HTML generated using the DPS.
   */
  public void runDPSHook (NodeList hook, Transaction trans, Resolver res ) {
    if (hook == null || hook.getLength() == 0) return;
    ActiveDoc proc = makeDPSProcessor(trans, res);
    proc.setInput(new org.risource.dps.input.FromParseNodes(hook));
    proc.setOutput(new org.risource.dps.output.DiscardOutput());
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

    org.risource.pia.Pia.debug(this, "Running interform...");
    if (! respondToInterform( trans, res ) ){
      respondNotFound( trans, trans.requestURL() );
    }
  }

  /************************************************************************
  ** Tabular interface: 
  ************************************************************************/

  /** Return the number of defined. */
  public synchronized int size() {
    return attributes.size();
  }

  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. Accepts attributes in other agents. */
  public synchronized Object get(String name) {
    int i = name.indexOf(":");
    if (i > 0) {
      String aname = name.substring(0, i);
      name = name.substring(i+1);
      Agent a = Pia.instance().resolver().agentaname);
      return (a == null)? null : a.get(name);
    }

    if (name.equals("criteria")) {
	return (criteria() == null)? "" : criteria().toString();
    }
    return attributes.get(name.toLowerCase());
  }

  /** Returns an enumeration of the table keys */
  public java.util.Enumeration keys() {
    return attributes.keys();
  }

  /** Set an attribute. 
   *	Attributes that correspond to class member veriables require 
   *	special handling.  This is ugly, but there seems to be no good way
   *	to do it in Java.  (A switch would be better than the chained if's.)
   */
  public synchronized void put(String name, Object value) {
    if (name == null) return;
    name = name.toLowerCase();
    if (value != null) 
      attributes.put(name, value);
    else
      attributes.remove(name);

    if (name.equals("act-on") || name.equals("_act_on")) {
      actOnHook = value;
      Pia.debug(this, "Setting ActOn hook", ":="+value);
    } else if (name.equals("handle") || name.equals("_handle")) {
      handleHook = value;
      Pia.debug(this, "Setting handle hook", ":="+value);
    } else if (name.equals("criteria")) {
      if (value == null) criteria = null;
      else criteria = new Criteria(value.toString());
    } else if (name.equals("authentication")
	       || name.equals("_authentication")) {
      if (authPolicy != null || value == null) {
	//should only allow more stringent authentication
	Pia.debug(this, "attempt to change authPolicy ignored");
	if (value == null) return;
      }
      authPolicy = new Authenticator( "Basic", value.toString());
      Pia.debug(this, "Setting  authentication passwordfile:=" +value);
    }
  }

  /**
   * Set options with a hash table (typically a form).
   *	Ignore the <code>agent</code> option, which comes from the fact
   *	that most install forms use it in place of <code>name</code>.
   *	=== eventually need agent= path+name ===
   */
  public void parseOptions(Table hash){
    if (hash == null) return;
    Enumeration e = hash.keys();
    while( e.hasMoreElements() ){
      Object keyObj = e.nextElement();
      String key = (String)keyObj;
      // Ignore "agent", which is replaced by "name".
      if (key.equalsIgnoreCase("agent")) continue;
      String value = (String)hash.get( keyObj );
      put(key, value);
    }
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String getObjectString(String name) {
    Object o = get(name);
    return (o == null)? null : o.toString();
  }

  /************************************************************************
  ** Finding and Executing InterForms:
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
    response.setStatus(HTTP.MOVED_TEMPORARILY);
    response.setContentLength( msg.length() );
    response.startThread();
    return true;
  }

  /**
   * Test whether an InterForm request is a redirection.
   * @return true if the request has been handled.
   */
  protected boolean isRedirection( Transaction req, String path ) {
    String originalPath = null;
    URL redirUrl = null;
    String redirUrlString = null;
    URL url = req.requestURL();

    if ( path == null ) return false;

    Pia.debug(this, "  path on entry -->"+ path);
    // === path, name, and typed were all getting lowercased.  Wrong!

    String myname = name();
    String mytype = type();

    // default to index.if

    originalPath = path;

    if (path.equals("/" + myname) || path.equals("/" + myname + HOME)
	|| path.equals("/" + mytype)    
	|| path.equals("/" + mytype + "/" + myname) 
	|| path.equals("/" + mytype + "/" + myname + HOME) ) {
      if (homePathSuffix != null) path += homePathSuffix;
    } else if (path.equals("/" + myname + "/")
	       || path.equals("/" + myname + HOME + "/")
	       || path.equals("/"+ mytype + "/")
	       || path.equals("/" + mytype + "/" + myname + "/") 
	       || path.equals("/" + mytype + "/" + myname + HOME + "/") ) {
      if (indexPathSuffix != null) path += indexPathSuffix;
    }

    if( originalPath == path ) // we don't have redirection
      return false;

    // check for existence
    String wholePath = findInterform( path );
    if( wholePath == null ){
      respondNotFound(req, path);
    } else {
      redirectTo(req, path);
    }
    return true;
  }

  /** 
   * Return a suitable path list for InterForm search.
   *  The path puts any  defined if_root first 
   *   (if_root/myname, if_root/mytype/myname if_root/mytype, if_root),
   *  If the above is not defined, it will try:
   *    .../name, .../type/name, .../type
   *    relative to each of (usrAgentsStr, piaAgentsStr)
   *
   * and finally  (usrAgentsStr, piaAgentsStr)
   */
  public List interformSearchPath() {
    List path = dirAttribute("if_path");
    if (path != null) return path;

    /* The path attribute wasn't defined, so do it now. */

    path = new List();

    /* Tails: type/name, name, type 
     *	 Check type/name first because it's the most specific.  That way,
     *	 sub-agents don't interfere with top-level agents with the same name.
     */

    String myname = name();
    String mytype = type();

    List tails = new List();

    if (!myname.equals(mytype)) tails.push(mytype + filesep + myname + filesep);
    tails.push(myname + filesep);
    if (!myname.equals(mytype)) tails.push(mytype + filesep);

    /* Roots: if_root, ~/.pia/Agents, pia/Agents */

    List roots = dirAttribute( agent_code_dir_name );
    if (roots == null) roots = new List();

    for (int i = 0; i < roots.nItems(); ++i) {
      int fslength = filesep.length();

      // handle a user-defined root first:  Trim a trailing /name or /type
      // because it gets automatically added below.
	
      String root = (String)roots.at(i);
      if ( !root.endsWith( filesep ) ) { root = root + filesep; }
      if ( root.endsWith( filesep + myname + filesep )) {
	root = root.substring(0, root.length() - myname.length() - fslength);
      } else if ( root.endsWith( filesep + mytype + filesep )) {
	root = root.substring(0, root.length() - mytype.length() - fslength);
      }

      roots.at(i, root);
    }	

    roots.push(Pia.instance().usrAgents());
    roots.push(Pia.instance().piaAgents());

    /* Make sure all the roots end in filesep */

    for (int i = 0; i < roots.nItems(); ++i) {
      String root = (String)roots.at(i);
      if ( !root.endsWith(filesep) ) { roots.at(i, root + filesep); }
    }	

    /* Now combine the roots and tails
     *	Do all the roots for each tail so that , for example, 
     *	usr/name/x will override pia/type/x
     */

    for (int i = 0; i < tails.nItems(); ++i) 
      for (int j = 0; j < roots.nItems(); ++j) {
	String dirname = roots.at(j).toString() + tails.at(i).toString();
	File dir = new File(dirname);
	if (dir.isDirectory()) path.push(dirname);
      }
    
    /* Finally, try just the roots */

    for (int j = 0; j < roots.nItems(); ++j)
      path.push(roots.at(j).toString());

    if(DEBUG )
      for(int i=0; i < path.nItems(); i++){
	String onePath = path.at(i).toString();
	System.out.println("GenericAgent findInterform-->"+(String)onePath );
      }

    // Now cache the lookup path list as a dirAttribute

    dirAttribute("if_path", path );
    return path;
  }

  /** 
   * Return a suitable path list for a writable InterForm.
   */
  public List writeSearchPath() {
    // === bogus for now. ===
    List path = new List();

    /* Tails: type/name, name, type 
     *	 Check type/name first because it's the most specific.  That way,
     *	 sub-agents don't interfere with top-level agents with the same name.
     */

    String myname = name();
    String mytype = type();

    List tails = new List();

    if (!myname.equals(mytype)) tails.push(mytype + filesep + myname + filesep);
    tails.push(myname + filesep);
    if (!myname.equals(mytype)) tails.push(mytype + filesep);

    List roots = new List();
    roots.push(Pia.instance().usrAgents());
    /* Make sure all the roots end in filesep */

    for (int i = 0; i < roots.nItems(); ++i) {
      String root = (String)roots.at(i);
      if ( !root.endsWith(filesep) ) { roots.at(i, root + filesep); }
    }	

    /* Now combine the roots and tails
     *	Do all the roots for each tail so that , for example, 
     *	usr/name/x will override pia/type/x
     */

    for (int i = 0; i < tails.nItems(); ++i) 
      for (int j = 0; j < roots.nItems(); ++j) {
	String dirname = roots.at(j).toString() + tails.at(i).toString();
	File dir = new File(dirname);
	// allow non-existant dir: if (dir.isDirectory()) path.push(dirname);
      }

    return path;
  }

  /**
   * Find a filename relative to this Agent.
   */
  public String findInterform(String path){
    if ( path == null ) return null;
    return findInterform(path, name(), type(), interformSearchPath(), null);
  }

  /**
   * Find a filename relative to this Agent.
   */
  public String findInterform(String path, String suffixPath[],
			      boolean forWriting){
    if ( path == null ) return null;
    List if_path = forWriting? writeSearchPath(): interformSearchPath();
    String found =  findInterform(path, name(), type(), if_path, suffixPath);
    if (found != null) return found;
    // Didn't find anything, but it may be ok to create it.
    if (forWriting) {
      // === this will fail for files with a leading path ===
      return agentDirectory() + path;       
    }
    return null;
  }

  /**
   * Find a filename relative to an arbitrary agent.  
   *	Note that the path separator <em>must</em> be ``<code>/</code>'',
   *	i.e. the path must follow the conventions for a URL.
   */
  public String findInterform(String path, String myname, String mytype,
			      List if_path, String suffixPath[]){
    if ( path == null ) return null;
    Pia.debug(this, "  path on entry -->"+ path);

    boolean hadName = false;	// these might be useful someday.
    boolean hadType = false;
    boolean wasData = false;

    /* Remove a leading /~ from the path and replace it with /
     *	This indicates the agent's data directory.
     */
    if (path.startsWith("/~")) {
      path = "/" + path.substring(2);
      wasData = true;
    }

    /* Remove a leading /type or /name or /type/name from the path. */
    // === Should really handle an arbitrary prefix (mount point) ===
    if (! myname.equals(mytype) && path.startsWith("/" + mytype)) {
      path = path.substring(mytype.length() + 1);
      hadType = true;
    }
    
    if (path.startsWith("/" + myname)) {
      path = path.substring(myname.length() + 1);
      hadName = true;
      if (path.startsWith(HOME)) {
	path = path.substring(HOME.length());
      }
    } 
    
    if (path.startsWith("/" + DATA )) {
      path = path.substring(DATA.length() + 1);
      wasData = true;
    }

    if (wasData) {
      if_path = new List();
      if_path.push(agentDirectory());
    }
    
    if (suffixPath == null) suffixPath = wasData? dataSearch : codeSearch;
    
    return findFile(path, if_path, suffixPath);
  }

  /** Find a file given a search-path of directories and a search-path of
   *	suffixes.
   */
  public String findFile(String path, List dirPath, String suffixPath[]){
    // Remove leading "/" because every directory name in the search path
    // ends with it. 
    if( path.startsWith("/") )	path = path.substring(1);
    Pia.debug(this, "Looking for -->"+ path);

    int lastDot = path.lastIndexOf(".");
    int lastSlash = path.lastIndexOf("/");
    boolean hasSuffix = lastDot >= 0 && lastDot > lastSlash;

    // === The following code fails miserably if fileSep is not "/".
    // === It also needs to handle missing suffix (use xxxSearch)
    // === Needs to handle file/extra_path_info
    // === We should be caching File objects because lookup is so elaborate.
    File f;
    Enumeration e = dirPath.elements();
    if (hasSuffix) {
      while( e.hasMoreElements() ){
	String zpath = e.nextElement().toString();
	String wholepath = zpath + path;
	Pia.debug(this, "  Trying -->"+ wholepath);
	f = new File( wholepath );
	if( f.exists() ) return wholepath;
      }
    } else {
      while( e.hasMoreElements() ){
	String zpath = e.nextElement().toString();
	String wholepath = zpath + path;
	Pia.debug(this, "  Trying -->"+ wholepath);
	f = new File( wholepath );
	if( f.exists() ) return wholepath;
	wholepath += ".";
	for (int i = 0; i < suffixPath.length; ++i) {
	  String xpath = wholepath + suffixPath[i];
	  Pia.debug(this, "  Trying -->"+ xpath);
	  f = new File( xpath );
	  if (f.exists()) return xpath;
	}
      }
    }
    return null;
  }

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg ) {
    msg = "Agent=" + name()
      + (! name().equals(type())? " Type=" + type() : "")
      + "<br>\n"
      + msg ;
    req.errorResponse(code, msg);
  }

  /**
   * Send error message for not found interform file
   */
  public void respondNotFound( Transaction req, URL url){
    String msg = "No InterForm file found for <code>" +
      url.getFile() + "</code>.  "
      + "See this agent's <a href=\"/" + name() + "/\">index page</a> "
      + "for a more information.";
    sendErrorResponse(req, HTTP.NOT_FOUND, msg);
  }

  /**
   * Send error message for not found interform file
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
   * Respond to a request directed at one of an agent's interforms.
   *
   * @return false if the file cannot be found.  This allows the caller
   *	to check for an InterForm first, then do something different with
   *	other requests.
   */
  public boolean respondToInterform(Transaction request, Resolver res){

    URL url = request.requestURL();
    if (url == null) return false;

    return respondToInterform(request, url.getFile(), res);
  }

  /**
   * Strip off destination file name for put
   * @return path with interform or cgi
   * Store destination file name in destFileName === not string safe!
   */
  protected String stripDestFile(String path){
    // If there is a destination file name after the interform file, strip it
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
  protected String rewriteInterformPath(Transaction request, String path) {
    return path;
  }

  /**
   * Respond to a request directed at one of an agent's interforms, 
   * with a (possibly-modified) path.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondToInterform(Transaction request, String path,
				    Resolver res){

    // If the path includes a query string, remove it now
    int end = path.indexOf('?');
    if(end > 0) path = path.substring(0, end);

    // Perform URL decoding:
    path = Utilities.urlDecode(path);

    // If there is a destination file name after the interform file, strip it
    path = stripDestFile( path );

    // If the request needs to be redirected, do so now.
    if (isRedirection( request, path )) return true;

    // Rewrite the path if necessary.
    path = rewriteInterformPath(request, path);

    // Find the file.  If not found, return false.
    String file = findInterform( path );
    if( file == null ) return false;
      
    if( file != null )
      Pia.debug(this, "The path of interform is -->"+file);

    String interformOutput = null;

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
      org.risource.pia.FileAccess.retrieveFile(file, request, this);
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
      throw new PiaRuntimeException (this, "respondToInterform", msg) ;
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
    dirTable = new Table();
    fileTable = new Table();
    name("GenericAgent");
    type("GenericAgent");
  }

  public GenericAgent(String name, String type){
    dirTable = new Table();
    fileTable = new Table();

    if( name != null ) this.name( name );
    if( type != null )
      this.type( type );
    else
      this.type( name );
  }

}






