////// FileDocument.java -- implementation for a document resource
//	$Id: FileDocument.java,v 1.1 1999-08-07 00:29:46 steve Exp $

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

import java.io.*;
import java.net.URL;

/**
 * Simple implementation for a document resource that specifies a local file.
 *
 * <p> As much of a FileDocument's configuration information as possible
 *	is derived from its corresponding File. 
 *
 * @version $Id: FileDocument.java,v 1.1 1999-08-07 00:29:46 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 */
public class FileDocument extends FileResource implements Document {

  /************************************************************************
  ** Configuration Management:
  ************************************************************************/

  /** Returns the <code>ActiveElement</code> node representing this 
   *	Resource's configuration.  
   * 
   *<p>	Since a FileDocument by definition has no configuration 
   *	information, this returns null. 
   */
  ActiveElement getConfig() { return null; }

  protected boolean basicSaveConfig() { return false; }


  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** @return <code>true</code> if the associated Document can be written.
   */
  public boolean isWritable() { return file.canWrite(); }

  /** @return <code>true</code> if the associated Document can be read. */
  public boolean isReadable() { return file.canRead(); }

  /************************************************************************
  ** Metadata Convenience Functions:
  ************************************************************************/

  /** Returns the time that the associated document was last modified. */
  public long getLastModified() { return file.lastModified(); }

  /** Returns the MIME content type of the associated document. */
  public String getContentType() { return getContentTypeFor(getName()); }

  /** Returns the name of the preferred tagset for processing the document. */
  public String getTagsetName() { return getTagsetNameFor(getName()); }


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

  /** @return a <code>File</code> for accessing a local document, or
   *      <code>null</code> if the resource is non-local. */
  public File documentFile() { return file; }

  /** @return a <code>BufferedInputStream</code> for accessing the document.
   */
  public BufferedInputStream documentInputStream() {
    try {
      return new BufferedInputStream(new FileInputStream(file));
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** @return a <code>LineNumberReader</code> for accessing the document.
   */
  public LineNumberReader documentReader() {
    try {
      return new LineNumberReader(new FileReader(file));
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** @return an <code>OutputStream</code> for writing the document.
   */
  public OutputStream documentOutputStream() {
    try {
      return new FileOutputStream(file);
    } catch (IOException e) {
      getRoot().reportException(e, "opening " + getPath());
      return null;
    }	
  }

  /** @return a <code>Writer</code> for writing the document.
   */
  public Writer documentWriter() {
    try {
      return new FileWriter(file);
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
    return new org.risource.dps.output.ToWriter(documentWriter());
  }

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete() { return file.delete(); }

  /** Create a new child Resource.  
   * @return <code>null</code> because a Document has no children.
   */
  public Resource create(String name, boolean container, boolean virtual, 
			 Element config) {
    return null;
  }

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize() { 
    if (!isReal()) {
      if (!getContainer().realize()) return false;
      // replace file with a new one under its (realized) parent. 
      file = new File(new File(getContainer().getRealPath()), getName());
      real = true;
    }

    return isReal();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public FileDocument(String name, AbstractResource parent) {
    super(name, parent);
  }

  public FileDocument(String name, AbstractResource parent, Element config) {
    super(name, parent, false, config);
  }
}
