////// SiteContext.java: Top Processor for PIA active documents
//	$Id: SiteContext.java,v 1.1 1999-08-18 18:06:31 steve Exp $

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


package org.risource.site.util;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

import java.net.URL;

import org.risource.dps.*;
import org.risource.dps.process.*;
import org.risource.dps.util.*;
import org.risource.dps.namespace.*;
import org.w3c.dom.NodeList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.active.ActiveNode;
import org.risource.dps.handle.Loader;

import org.risource.site.*;

import org.risource.ds.List;
import org.risource.ds.Table;
import org.risource.ds.Tabular;

/**
 * A TopProcessor for processing active documents in a Subsite.
 *
 * @version $Id: SiteContext.java,v 1.1 1999-08-18 18:06:31 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps
 * @see org.risource.dps.process.TopProcessor
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Context
 * @see org.risource.dps.site
 * @see org.risource.dps.site.Site
 * @see org.risource.dps.site.Subsite
 */

public class SiteContext extends TopProcessor {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected Subsite 	subsite		= null;
  protected Document	document	= null;

  /************************************************************************
  ** Site information:
  ************************************************************************/

  public Subsite getSubsite() { return subsite; }
  public Document getDocument() { return document; }

  public ActiveElement documentConfig() {
    ToNodeList out = new ToNodeList(null);
    document.reportConfig(0, true, out);
    ActiveNodeList nl = out.getList();
    for (int i = 0; i < nl.getLength(); ++i) {
      ActiveNode node = nl.activeItem(i);
      if (node.getNodeType() == NodeType.ELEMENT) {
	return (ActiveElement) node;
      }
    }
    return null;
  }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  /** Initialize the various entities.  
   *	Done in four separate methods (counting super), for easy customization.
   *
   * @see org.risource.dps.process.TopProcessor#initializeEntities
   * @see #initializeNamespaceEntities
   * @see #initializeDocumentEntities
   */
  public void initializeEntities() {
    if (entities == null) super.initializeEntities();
    initializeNamespaceEntities();
    initializeDocumentEntities();
  }

  /** Initialize the entities that contain namespaces. */
  public void initializeNamespaceEntities() {
    Namespace props = document.getProperties();
    define("PROP", props);
  }

  /** Initialize the normal entities that pertain to this document . */
  public void initializeDocumentEntities() {
    define("config", documentConfig());
  }


  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** Locate a resource accessible as a file.
   *  The following prefixes are recognized:
   *  <dl>
   *	<dt> <code>file:</code>
   *	<dd> forces the remainder of the path to be taken as a native
   *		path, and used as-is.
   *	<dt> <code>/</code>
   *	<dd> Interprets the path like a URL passed to the PIA.
   *	<dt> other
   *	<dd> paths are relative to the current agent.
   * </dl>
   * @see org.risource.pia.FileAccess#systemFileName
   */
  public File locateSystemResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      // file: is handled by systemFileName
      return new File(path.substring("file:".length()));
    } else {
      Resource r = subsite.locate(path, forWriting, null);
      if (r == null) return null;
      Document d = r.getDocument();
      return (d == null)? null : d.documentFile();
    }
  }	

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Load a Tagset by name. 
   * @param tsname the tagset name.  If null, returns the current tagset. 
   */
  public Tagset loadTagset(String tsname) {
    return (tsname == null) ? tagset : subsite.loadTagset(tsname);
  }

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param ts the tagset.  If null, the current tagset is used.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts) {
    if (ts == null) ts = tagset;
    return new SiteContext(in, cxt, out, ts, subsite, document);
  }

  /************************************************************************
  ** Handler Utilities:
  ************************************************************************/

  /** Return the current top context as an SiteContext object. */
  public static SiteContext getSiteContext(Context cxt) {
    TopContext top = cxt.getTopContext();
    return (top instanceof SiteContext)? (SiteContext)top : null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SiteContext() {
    super();
  }

  public SiteContext(Subsite subsite, Document document) {
    super();
    this.subsite = subsite;
    this.document = document;
    initializeEntities();
  }

  public SiteContext(Input in, Context cxt, Output out, Tagset ts,
		     Subsite subsite, Document document) {
    super(in, cxt, out, ts);
    this.subsite = subsite;
    this.document = document;
    initializeEntities();
  }

}

