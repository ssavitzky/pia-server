////// Document.java -- interface for a document resource
//	$Id: Document.java,v 1.4 2001-01-11 23:37:50 steve Exp $

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
import org.risource.ds.*;

import java.io.*;
import java.net.URL;

/**
 * Generic interface for a document accessible through a URL.
 *
 * <p> The Document interface is used to give access to the content of the
 *	(unique) document associated with a resource.  This document can be
 *	accessed in several different ways: e.g. as a character stream, a DOM
 *	parse tree, or a DPS cursor.  For any given Document one of these will
 *	be significantly more efficient, so the metadata will indicate which
 *	is the preferred access method.  Note that all the contents of a
 *	container need not have the same preferred access method.
 *
 * @version $Id: Document.java,v 1.4 2001-01-11 23:37:50 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 */
public interface Document extends Resource {

  /************************************************************************
  ** Predicates:
  ************************************************************************/

  /** @return <code>true</code> if the Document can be written.
   */
  public boolean isWritable();

  /** @return <code>true</code> if the Document can be read. */
  public boolean isReadable();

  /************************************************************************
  ** Metadata Convenience Functions:
  ************************************************************************/

  /** Returns the MIME content type of the document. */
  public String getContentType();

  /** Returns the name of the preferred tagset for processing the document. */
  public String getTagsetName();

  /************************************************************************
  ** Document Access:
  ************************************************************************/

  /** @return a <code>File</code> for accessing a local document, or
   *      <code>null</code> if the resource is non-local. */
  public File documentFile();

  /** @return a <code>BufferedInputStream</code> for accessing the document.
   */
  public BufferedInputStream documentInputStream();

  /** @return a <code>LineNumberReader</code> for accessing the document.
   */
  public LineNumberReader documentReader();

  /** @return an <code>OutputStream</code> for writing the document.
   */
  public OutputStream documentOutputStream(boolean append);

  /** @return a <code>Writer</code> for writing the document.
   */
  public Writer documentWriter(boolean append);

  /** Returns a DPS <code>Input</code> object suitable for traversing
   *	the document associated with the Resource. 
   *
   * @return an <code>Input</code> for traversing the document.
   *	Returns <code>null</code> if the resource is not readable.
   */
  public Input documentInput();

  /** Returns a DPS <code>Output</code> object suitable for writing to
   *	the document associated with the Resource. 
   *
   * @return an <code>Output</code> for (re)writing the document.
   *	Returns <code>null</code> if the resource is not writable.
   */
  public Output documentOutput(boolean append);

  /************************************************************************
  ** Resource Manipulation:
  ************************************************************************/

  public boolean delete();

  public boolean rename(String name);

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize();
}
