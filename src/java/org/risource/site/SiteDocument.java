////// SiteDocument.java -- implementation for a document resource
//	$Id: SiteDocument.java,v 1.9 2001-04-03 00:05:24 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
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
import org.risource.dps.input.FromNodeList;
import org.risource.dps.util.Status;

import java.io.*;
import java.net.URL;

/**
 * Simple implementation for a document resource that is contained under a
 *	Site (i.e. contained in a Subsite).
 *
 * <p> Some of a SiteDocument's configuration information may be
 *	derived from its parent, which is necessarily a Subsite. 
 *
 * @version $Id: SiteDocument.java,v 1.9 2001-04-03 00:05:24 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 */
public class SiteDocument extends ConfiguredResource implements Document {

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  /** Reports whether this resource has an explicit configuration.
   *
   * @return <code>true</code> if we have an explicit configuration element.
   */
  public boolean hasExplicitConfig() { return config != null; }

  /** Save the current configuration in a file. 
   *
   *<p> Since a document cannot contain its own configuration information,
   *	we have to add it to the configuration of the parent.
   */
  protected boolean basicSaveConfig() { 
    return false;	// === really need to get parent to add my config.
  }

  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** @return <code>false</code> because the Resource is not a container. */
  public boolean isContainer() { return false; }

  /** @return <code>true</code> if the associated Document can be written.
   */
  public boolean isWritable() {
    return (file == null)? false : file.canWrite();
  }

  /** @return <code>true</code> if the associated Document can be read. */
  public boolean isReadable() {
    return (file == null)? true :  file.canRead();
  }

  /************************************************************************
  ** Metadata Convenience Functions:
  ************************************************************************/

  /** Returns the time that the associated document was last modified. */
  public long getLastModified() {
    // === probably the wrong result for imaginary files.
    return (file == null)? 0 : file.lastModified()/Status.fileTimeFactor;
  }

  /** Returns the MIME content type of the associated document. */
  public String getContentType() { return base.getContentTypeFor(getName()); }

  /** Returns the name of the preferred tagset for processing the document. */
  public String getTagsetName() { return base.getTagsetNameFor(getName()); }


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

  /** @return a <code>File</code> for accessing a site document, or
   *      <code>null</code> if the resource is non-local. */
  public File documentFile() { return file; }

  /** @return a <code>BufferedInputStream</code> for accessing the document.
   */
  public BufferedInputStream documentInputStream() {
    if (parsedContent != null) {
      return new BufferedInputStream(new StringBufferInputStream
				     (parsedContent.toString()));
    } else try {
      return new BufferedInputStream(new FileInputStream(file));
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** @return a <code>LineNumberReader</code> for accessing the document.
   */
  public LineNumberReader documentReader() {
    if (parsedContent != null) {
      return new LineNumberReader(new StringReader(parsedContent.toString()));
    } else try {
      return new LineNumberReader(new FileReader(file));
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      System.err.println("Exception " + e + " opening " + getPath());
      return null;
    }	
  }

  /** @return an <code>OutputStream</code> for writing the document.
   */
  public OutputStream documentOutputStream(boolean append) {
    if (file == null) {
      getRoot().report(-2, "No real file for writing " + getPath(), 0, true);
      return null;
    }
    try {
      return append? new FileOutputStream(file.getPath(), append)
	           : new FileOutputStream(file);
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** @return a <code>Writer</code> for writing the document.
   */
  public Writer documentWriter(boolean append) {
    if (file == null) {
      getRoot().report(-2, "No real file for writing " + getPath(), 0, true);
      return null;
    }
    try {
      return append? new FileWriter(file.getPath(), append)
	           : new FileWriter(file);
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** Returns a DPS <code>Input</code> object suitable for traversing
   *	the document associated with the Resource. 
   *
   * @return an <code>Input</code> for traversing the document.
   *	Returns <code>null</code> if the resource is not readable.
   */
  public Input documentInput() {
    if (parsedContent != null) 
      return new FromNodeList(parsedContent);

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
  public Output documentOutput(boolean append) {
    return new org.risource.dps.output.ToWriter(documentWriter(append));
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
    if (!isReal()) {
      if (!base.realize()) return false;
      // replace file with a new one under its (realized) parent. 
      file = new File(new File(base.getRealPath()), getName());
      real = true;
    }

    return isReal();
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

  public SiteDocument(String name, Subsite parent, File file, 
		      boolean real, ActiveElement config) {
    super(name, parent, false, real, file, config, null);
  }

  public SiteDocument(Subsite parent, ActiveElement config) {
    super(null, parent, false, false, null, config, null);
  }
}
