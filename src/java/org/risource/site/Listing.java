////// Listing.java -- simple listing of a container document
//	$Id: Listing.java,v 1.1 1999-09-09 21:44:47 steve Exp $

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
import org.risource.ds.*;

import org.risource.util.Utilities;
import org.risource.util.NameUtils;

import JP.ac.osaka_u.ender.util.regex.RegExp;
import JP.ac.osaka_u.ender.util.regex.MatchInfo;

import java.io.*;
import java.net.URL;

/**
 * Simple implementation for a document resource that is a listing of a
 *	SubSite or other container resource.
 *
 * @version $Id: Listing.java,v 1.1 1999-09-09 21:44:47 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 */
public class Listing extends FileResource implements Document {

  /************************************************************************
  ** Formatting flags:
  ************************************************************************/

  // === formatting flags need to be set from configuration ===
  public boolean all = false;

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  /** Reports whether this resource has an explicit configuration.
   *
   * @return <code>true</code> if we have an explicit configuration element.
   */
  public boolean hasExplicitConfig() { return false; }

  /** Save the current configuration in a file. 
   *
   *<p> Since a document cannot contain its own configuration information,
   *	we have to add it to the configuration of the parent.
   */
  protected boolean basicSaveConfig() { return false; }

  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** @return <code>false</code> because the Resource is not a container. */
  public boolean isContainer() { return false; }

  /** @return <code>true</code> if the associated Document can be written.
   */
  public boolean isWritable() { return false; }

  /** @return <code>true</code> if the associated Document can be read. */
  public boolean isReadable() { return true; }

  /************************************************************************
  ** Metadata Convenience Functions:
  ************************************************************************/

  /** Returns the time that the associated document was last modified. */
  public long getLastModified() {
    return base.getLastModified();
  }

  /** Returns the MIME content type of the associated document. */
  public String getContentType() { 
    return "text/html"; /* base.getContentTypeFor("./"); */
  }

  /** Returns the name of the preferred tagset for processing the document. */
  public String getTagsetName() { return base.getTagsetNameFor("./"); }


  /************************************************************************
  ** Tree Navigation:
  ************************************************************************/

  /** Returns a Resource that refers to a named child.
   * @return <code>null</code> because documents have no children.
   */
  public Resource getChild(String name) { return null; }

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** Returns the Document associated with the Resource. */ 
  public Document getDocument() { return this; }

  /** @return null because this document does not correspond to a file */
  public File documentFile() { return null; }

  protected boolean ignoreFile(String name, Resource res) {
    // === really need lists for prefixes, suffixes, and names to reject ===
    // === also ought to ignore hidden resources
    return (name.startsWith(".") || name.startsWith("#")
	    || name.endsWith("~")
	    || name.equals("CVS"));
  }

  /**
   * Suck in the body part of an HTML file, as a string.
   *	If there is no &lt;body&gt; tag the entire file is returned.
   */
  protected static String suckBody( Resource res ){
    if (res.isContainer()) return "";

    Document doc = res.getDocument();

    StringBuffer data = new StringBuffer();

    try{
      data = new StringBuffer ( Utilities.readStringFrom(doc.documentInputStream()) );
      String zhead = "<head.*</head>";
      String htmlBeginBrak = "<html>";
      String htmlEndBrak   = "</html>";
      
      RegExp re = new RegExp(zhead);
      String nohead = re.substitute(new String( data ),"", true);

      re = new RegExp( htmlBeginBrak );
      String nobegin = re.substitute(nohead,"", true);

      re = new RegExp( htmlEndBrak );
      String zfinal = re.substitute(nobegin,"", true);

      return zfinal;
    }catch(Exception e ){
      return null;
    }
  }

  /** Get a string for the contents of the listing page. 
   *  	=== really ought to return a DOM tree ===
   *	=== really needs to use parent resource, not File ===
   * @return a string corresponding to the listing.
   */
  protected String getListingString() {
      String[] ls;
      int i;
      File f;
      String head = null;
      String mypath = base.getPath();
      String entry;
      SortTree entries = new SortTree();

      if (!mypath.endsWith("/")) mypath += "/";

      for (ls = base.listNames(), i = 0; ls != null && i < ls.length; i++){
	String   zfile = ls[i];
	Resource zres  = base.getChild(zfile);

	if (zfile.toLowerCase().equals("header.html") 
	    && ! ignoreFile(zfile, zres)) {
	  head = suckBody(zres);
	  if (!all) continue;
	} else if (zfile.toLowerCase().equals("header")
		   && ! ignoreFile(zfile, zres)) {
	  head = "<pre>"+suckBody(zres)+"</pre>";
	  if (!all) continue;
	}

	if (all || !ignoreFile(zfile, zres)) {
	  entry = "<li> ";
	  if ( zres.isContainer() )
	    entry += " <a href=\"" + mypath + zfile + "/\">" + zfile + "/</a>";
	  else
	    entry += "<a href=\"" + mypath + zfile + "\">" + zfile + "</a>" ;

	  entries.insert(Association.associate(entry, zfile));
	}
      }

      /* Java doesn't list "..", so include it here. */

      entry = "<li> <a href=\"" + mypath +  ".." + "/\">" + " / " + "</a>";
      entries.insert(Association.associate(entry, ".."));

      if (head == null) head = "<h1>Directory listing of "+ mypath +"</h1>";

      List sortList = new List();
      entries.ascendingValues(sortList);
      String allurls = sortList.join("\n");

      return "\n" + "<html>\n<head>" + "<title>" + mypath + "</title>"
	+ "</head>\n<body>" + head
	+ "<h3><a href=\"" + mypath + "\">" + mypath + "</a> " + "</h3>"
	//+ "<h4><a href=\"file:" + realpath + "\">file:" + realpath + "</a></h4>"
	+ "<ul>" + allurls + "</ul>" + "</body>\n</html>\n";
  }

  /** @return a <code>BufferedInputStream</code> for accessing the document.
   */
  public BufferedInputStream documentInputStream() {
    return new BufferedInputStream(new StringBufferInputStream(getListingString()));
  }

  /** @return a <code>LineNumberReader</code> for accessing the document.
   */
  public LineNumberReader documentReader() {
    return new LineNumberReader(new StringReader(getListingString()));
  }

  /** @return an <code>OutputStream</code> for writing the document.
   */
  public OutputStream documentOutputStream() {
    return null;
  }

  /** @return a <code>Writer</code> for writing the document.
   */
  public Writer documentWriter() {
    return null;
  }

  /** Returns a DPS <code>Input</code> object suitable for traversing
   *	the document associated with the Resource. 
   *
   * @return an <code>Input</code> for traversing the document.
   *	Returns <code>null</code> if the resource is not readable.
   */
  public Input documentInput() {
    String tsname = getTagsetName();
    Tagset ts = loadTagset(tsname);
    Parser p = ts.createParser();
    p.setReader(documentReader());
    return p;
  }

  /** Returns a DPS <code>Output</code> object suitable for writing to
   *	the document associated with the Resource. 
   *
   * @return an <code>Output</code> for (re)writing the document.
   *	Returns <code>null</code> if the resource is not writable.
   */
  public Output documentOutput() {
    return null;
  }


  /************************************************************************
  ** Container Access:
  ************************************************************************/

  /** Returns an array of strings naming the contents. 
   *
   * @return <code>null</code> because a Document is not a container.
   */
  public String[] listNames() { return null; }


  /** Load a tagset. */
  public Tagset loadTagset(String name) {
    if (name == null) name = getTagsetName();
    return getContainer().loadTagset(name);
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete() { return file.delete(); }

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize() { 
    return false;
  }

  /** Create a new child Resource.  
   * @return <code>null</code> because a Document has no children.
   */
  public Resource create(String name, boolean container, boolean virtual, 
			 Element config) {
    return null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Listing(String name, AbstractResource parent) {
    super(name, parent, null);
  }

  public Listing(String name, AbstractResource parent, File f) {
    super(name, parent, f);
  }

  public Listing(ConfiguredResource parent, ActiveElement config) {
    super(null, parent, false, config);
  }
}
