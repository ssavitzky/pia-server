////// TopProcessor.java: Top-level Document Processor class
//	$Id: TopProcessor.java,v 1.15 1999-10-04 17:35:59 steve Exp $

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


package org.risource.dps.process;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Enumeration;
import java.text.DateFormat;
import java.util.Properties;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.*;
import org.risource.dps.namespace.*;

import org.risource.site.*;

import org.risource.ds.List;
import org.risource.ds.Tabular;

/**
 * A top-level Processor, implementing the TopContext and Processor
 *	interfaces.
 *
 *	A TopContext is the root of a document-processing Context stack. 
 *	As such, it contains the tagset, global entity table, and other
 *	global information. <p>
 *
 *	Note that there may be more than one top context in a stack; this
 *	may be done in order to insert a sub-document into the processing
 *	stream, or to switch to a different tagset.
 *
 * @version $Id: TopProcessor.java,v 1.15 1999-10-04 17:35:59 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Context */

public class TopProcessor extends BasicProcessor implements TopContext
{
  protected Tagset tagset;
  protected Document document = null;
  protected Resource location = null;

  protected URL documentLocation = null;

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset() 		 { return tagset; }

  /** Set the current Tagset.
   *	If the entity table has not been initialized yet, do so. 
   */
  public void setTagset(Tagset bindings) {
    tagset = bindings;
    if (entities == null && bindings != null) initializeEntities();
    // === It is not at all clear why this doesn't work, 
    // === but doing it in the constructor does work.
    //    if (tagset != null && input instanceof ProcessorInput) 
    //  ((ProcessorInput)input).setTagset(tagset);
  }

  /** Obtain the current Document resource. */
  public Document getDocument() { return document; }

  /** Set the current Document resource. */
  public void setDocument(Document doc) { 
    document = doc;
    if (doc != null && location == null) location = doc.getContainer();
  }

  public void setLocation(Resource loc) {
    if (loc != null && !loc.isContainer()) {
      document = loc.getDocument();
      location = loc.getContainer();
    } else {
      location = loc;
    }
  }

  /** Get the current Document's location. */
  public Resource getDocumentLoc() { return location;  }

  /** Get the current Document's Root. */
  public Root getRoot() {
    return location == null ? null : location.getRoot();
  }

  /** Get the current Document's configuration. */
  public Namespace getDocConfig() {
    Resource doc = getDocument();
    return (doc == null)? null : doc.getProperties();
  }

  /** Get the current location's configuration. */
  public Namespace getLocConfig() {
    Resource loc = getDocumentLoc();
    return (loc == null)? null : loc.getProperties();
  }

  public Namespace getRootConfig() {
    Root loc = getRoot();
    return (loc == null)? null : loc.getProperties();
  }

  /** Return the current ProcessorInput, if there is one. */
  public ProcessorInput getProcessorInput() {
    return (getInput() instanceof ProcessorInput)
      ? (ProcessorInput)getInput()
      : null;
  }

  public TopContext getTopContext() { return this; }

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput)    {
    input = anInput;
    if (input instanceof ProcessorInput) {
      ((ProcessorInput)input).setProcessor(this);
      if (tagset != null) ((ProcessorInput)input).setTagset(tagset);
    }
  }

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput) { output = anOutput; }

  /************************************************************************
  ** Namespaces:
  ************************************************************************/

  /** Get the binding (Entity node) of an entity, given its name. 
   *
   * <p> Search the tagset if there is no binding in the current Namespace
   *	 Bindings in the tagset are considered local.
   *
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNode getBinding(String name, boolean local) {
    ActiveNode ent = (entities == null)
      ? null : entities.getBinding(name);
    if (ent == null && tagset != null) ent = tagset.getBinding(name);
    //if (debug() && ent != null)
    //debug("Binding found for " + name + " " + ent.getClass().getName());
    return (local || ent != null || nameContext == null)
      ? ent : nameContext.getBinding(name, local);
  }

  /** Get the namespace containing a given name. 
   * @return <code>null</code> if the name is undefined.
   */
  public Namespace locateBinding(String name, boolean local) {
    ActiveNode ent = (entities == null)
      ? null : entities.getBinding(name);
    if (ent != null) return entities;
    ent = tagset.getBinding(name);
    if (ent != null) return tagset.getEntities();
    return (local || nameContext == null)
      ? null : nameContext.locateBinding(name, local);
  }

  /** Return a namespace with a given name.  If the name is null, 
   *	returns the most-locally namespace.  The tagset is also considered 
   *	a namespace.
   */
  public Namespace getNamespace(String name) {
    if (entities != null) {
      if (name == null || name.equals(entities.getName())) {
	return entities;
      } else if (entities.containsNamespaces()) {
	ActiveNode ns = entities.getBinding(name);
	if (ns != null && ns.asNamespace() != null)
	  return ns.asNamespace();
      }
    }
    if (tagset != null && tagset.getNamespace(name) != null)
      return tagset.getNamespace(name);
    if (nameContext != null) {
      return nameContext.getNamespace(name);
    } else {
      return null;
    }
  }

  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** Locate a resource relative to the Document being processed.
   * @param path a path.
   * @param forWriting <code>true</code> if the Resource is intended to 
   *		be written into. 
   */
  public Resource locateResource(String path, boolean forWriting) {
    if (location == null) return null;
    return location.locate(path, forWriting,
			   forWriting? new List() : null);
  }

  /** Determine whether a resource name is local or remote. */
  public boolean isRemotePath(String path) {
    if (path.startsWith("file:")) return false;
    return (path.indexOf(":") > 0 && path.indexOf("/") > 0 
	    && path.indexOf(":") < path.indexOf("/") );      
  }

  /** Determine whether a resource name is special. */
  protected boolean isSpecialPath(String path) {
    return false;    
  }

  /** Read from a resource. 
   *	The given path always uses ordinary (forward) slashes as file
   *	separators, because it is really a URI.  If a protocol and host
   *	are not specified, the path is relative to some implementation-
   *	dependent origin if it starts with '<code>/</code>', otherwise
   *	it is relative to the start of the document being processed.
   */
  public InputStream readExternalResource(String path)
    throws IOException {
    if (isSpecialPath(path)) {
      return readSpecialResource(path);
    } else if (isRemotePath(path)) {
      URL u = locateRemoteResource(path, false);
      if (u != null) {
	return u.openStream();
      }
    } else if (document != null) {
      Resource r = document.locate(path, false, null);
      if (r != null) return r.getDocument().documentInputStream();
    } else {
      File f = locateSystemResource(path, false);
      if (f != null) {
	return new java.io.FileInputStream(f);
      }
    }
    return null;
  }

  /** Write to a resource. 
   * @param path a path.
   * @param append append to an existing resource. 
   * @param createIfAbsent if no resource of the given name exists, create one
   * @param doNotOverwrite do not overwrite an existing resource
   */
  public OutputStream writeExternalResource(String path, boolean append,
					    boolean createIfAbsent,
					    boolean doNotOverwrite)
    throws IOException {
    if (isSpecialPath(path)) {
      return writeSpecialResource(path, append, createIfAbsent, doNotOverwrite);
    } else if (isRemotePath(path)) {
      URL u = locateRemoteResource(path, true);
      if (u != null) {
	return null; // === writeExternalResource remote unimplemented
      }
    } else if (document != null) {
      Resource r = document.locate(path, true, null);
      Document d = r.getDocument();
      // === worry about createIfAbsent/doNotOverwrite
      return d.documentOutputStream(append);
    } else {
      File f = locateSystemResource(path, true);
      if (f != null) {
	// === worry about createIfAbsent/doNotOverwrite here!
	return new java.io.FileOutputStream(f.getAbsolutePath(), append);
      }
    }
    return null;
  }

  /** Locate a resource accessible as a file. */
  public File locateSystemResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      // Just remove the "file:" prefix.
      path = path.substring(5);
    }
    if (path.startsWith("/")) {
      // Path starting with "/" is relative to document root
      return new File(path);
    } else if (path.indexOf(":") >= 0) {
      // URL: fail.
      return null;
    } else {
      // Path not starting with "/" is relative to documentBase.
      if (path.startsWith("./")) path = path.substring(2);
      return new File(path);
    }
  }

  /** Locate a resource accessible as a URL. */
  public URL locateRemoteResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      return null; // === file: in locateRemoteResource -- not clear what to do
    }
    if (path.indexOf(":") > 0 && path.indexOf("/") > 0 
	&& path.indexOf(":") < path.indexOf("/") ) {
      try {
	return new URL(path);
      } catch (MalformedURLException e) {
	return null;
      }
    } else if (documentLocation != null) {
      try {
	return new URL(documentLocation, path);
      } catch (MalformedURLException e) {
	return null;
      }
    }
    return null;
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  protected InputStream readSpecialResource(String path) {
    return null;
  }
  /** Hook on which to hang any specialized paths supported by a subclass. */
  public OutputStream writeSpecialResource(String path, boolean append,
					    boolean createIfAbsent,
					   boolean doNotOverwrite) {
    return null;
  }


  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Load a Tagset by name. 
   * @param tsname the tagset name.  If null, returns the current tagset. 
   */
  public Tagset loadTagset(String tsname) {
    if (tsname == null) return tagset;
    else return org.risource.dps.tagset.Loader.loadTagset(tsname, this);
  }

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   * @param ts the tagset.  If null, the current tagset is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts) {
    if (ts == null) ts = tagset;
    return new TopProcessor(in, cxt, out, ts);
  }

  /** Called on a parent document when a subDocument is finished.
   *	The parser's tagset is put back to its original value.
   */
  public void subDocumentEnd() {
    if (input instanceof ProcessorInput) 
      ((ProcessorInput)input).setTagset(tagset);
  }

  /************************************************************************
  ** Message Reporting:
  ************************************************************************/

  public void setLog(PrintStream log) 	 { this.log = log; }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  /** Convert an int to a string padded on the left with zeros */
  protected String pad(int i, int fieldLength) {
    String s = Integer.toString(i);
    while (s.length() < fieldLength) s = '0' + s;
    return s;
  }

  /** Make an entity-table entry for a lookup table. */
  public void define(String n, Tabular v) {
    NamespaceWrap ns = new NamespaceWrap(n, v);
    getLocalNamespace().setBinding(n, new EntityWrap(n,  ns));
  }

  /** Make an entity-table entry for a property list. */
  public void define(String n, Properties v) {
    PropertyTable ns = new PropertyTable(n, v);
    getLocalNamespace().setBinding(n, new EntityWrap(n,  ns));
  }

  /** Make an entity-table entry for a Namespace. */
  public void define(String n, Namespace ns) {
    getLocalNamespace().setBinding(n, new EntityWrap(n,  ns));
  }

  /** Make an entity-table entry for a node. */
  public void define(String n, ActiveNode v) {
    if (v != null) getLocalNamespace().setBinding(n, v);
  }

  /** Make an entity-table entry for a lookup table with a tag. */
  public void define(String n, String tag, Tabular v) {
    NamespaceWrap ns = new NamespaceWrap(tag, n, v);
    getLocalNamespace().setBinding(n, new EntityWrap(n, ns));
  }

  /** Make an entity-table entry for a String. */
  public void define(String n, Object v) {
    if (v == null)
      setValueNodes(n, new TreeNodeList(), false);
    else
      setValueNodes(n, new TreeNodeList(tagset.createActiveText(v.toString(),
								false)), false);
  }

  /** Make an entity-table entry for a List. */
  public void define(String n, Enumeration v) {
    setValueNodes(n, new TreeNodeList(v), false);
  }

  static List dayNames = List.split("Sunday Monday Tuesday Wednesday"
				    + " Thursday Friday Saturday");

  static List monthNames= List.split("January February March April"
				     + " May June July August"
				     + " September October November December");

  /** Initialize date-dependentent entities. */
  public void initializeDateEntities(Date date) {
    // The Calendar instance performs all the necessary extraction.
    Calendar today = new GregorianCalendar();
    String yyyy	   = pad(today.get(Calendar.YEAR), 4);
    int    m	   = today.get(Calendar.MONTH);
    String mm	   = pad(m+1, 2);
    String dd      = pad(today.get(Calendar.DAY_OF_MONTH), 2);
    String hh	   = pad(today.get(Calendar.HOUR_OF_DAY), 2);
    String min     = pad(today.get(Calendar.MINUTE), 2);
    String sec     = pad(today.get(Calendar.SECOND), 2);

    // Handle any reasonable value of Sunday.  We need Sunday = 0.
    int wday	   = (today.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;

    // Define the entities:
    define("second",		sec);
    define("minute",		min);
    define("hour",		hh);
    define("day",		dd);
    define("month",		mm);
    define("year",		yyyy);
    define("weekday",		pad(wday, 1));
    define("dayName",		dayNames.at(wday));
    define("monthName",		monthNames.at(m));
    define("yearday",		pad(today.get(Calendar.DAY_OF_YEAR), 3));
    define("date",		yyyy+mm+dd);
    define("time",		hh+":"+min);

    // Get a formatter to create a properly-formatted date.
    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
							  DateFormat.LONG);
    define("dateString",	formatter.format(date));
  }

  public void initializeEntities() {
    entities = new BasicNamespace("DOC"); // top level is called "DOC"

    define("LOC", getLocConfig());
    define("PROPS", getDocConfig());
    define("SITE", getRootConfig());

    // Extract formatted information from today's Date.
    initializeDateEntities(new Date());

    // Form counter.  Increment as each <form> is passed to the output.
    define("forms", 		"0");

    /* The following must be defined by the caller:
     *	fileName -- the name of the file.
     *  filePath -- the full (from root) path to the file
     *  pathExt  -- the path extension (i.e. path after the filename)
     */

    define("user.name",		System.getProperty("user.name"));
    define("user.home",		System.getProperty("user.home"));
    define("user.dir",		System.getProperty("user.dir"));
    define("ERROR",		"" );
    define("VERBOSITY",		"0");
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TopProcessor() {
    top = this;
  }

  public TopProcessor(Resource doc) {
    top = this;
    setLocation(doc);
  }

  public TopProcessor(Tagset ts, boolean defaultEntities) {
    tagset = ts;
    top = this;
    if (defaultEntities) initializeEntities();
  }

  public TopProcessor(Input in, Output out) {
    this(in, (Context)null, out, (Namespace)null);
    initializeEntities();
  }

  public TopProcessor(Input in, Output out, Namespace ents) {
    this(in, (Context)null, out, ents);
  }

  public TopProcessor(Input in, Context prev, Output out, Namespace ents) {
    super(in, prev, out, ents);

    if (input instanceof ProcessorInput) 
      ((ProcessorInput)input).setProcessor(this);

    top = this;
  }

  public TopProcessor(Input in, Context prev, Output out, Tagset ts) {
    this(in, prev, out, (Namespace)null);
    setTagset(ts);
    if (tagset != null && input instanceof ProcessorInput) 
      ((ProcessorInput)input).setTagset(tagset);
  }

  public TopProcessor(Input in, Context prev, Output out, Tagset ts, 
		      Resource doc) {
    this(in, prev, out, (Namespace)null);
    setTagset(ts);
    if (tagset != null && input instanceof ProcessorInput) 
      ((ProcessorInput)input).setTagset(tagset);
    setLocation(doc);
  }

}
