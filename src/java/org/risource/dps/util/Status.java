////// Test.java: Utilities for testing nodes and strings
//	$Id: Status.java,v 1.6 1999-09-22 00:42:33 steve Exp $

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


package org.risource.dps.util;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.tree.TreeExternal;

import org.risource.site.*;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * Utilities to determine the status (properties) of resources. 
 *
 * @version $Id: Status.java,v 1.6 1999-09-22 00:42:33 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class Status {

  /************************************************************************
  ** Construct nodelist for return:
  ************************************************************************/

  protected static ActiveNodeList nodes(String v) {
    if (v == null) return null;
    return new TreeNodeList(new TreeText(v));
  }

  protected static ActiveNodeList nodes(long v) {
    return new TreeNodeList(new TreeText("" + v));
  }

  protected static ActiveNodeList nodes(boolean v) {
    return v? nodes("true") : null;
  }

  protected static ActiveNodeList nodes(String v[]) {
    TreeNodeList nl = new TreeNodeList();
    for (int i = 0; i < v.length; ++i) 
      nl.append(new TreeText(v[i]));
    return nl;
  }

  /************************************************************************
  ** Status of Files:
  ************************************************************************/

  /** Get the value of a named status item for a file.
   *	Returns all values if the name is null.
   */
  public static ActiveNodeList getStatusItem(Resource res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res).asNodeList();
    name = name.toLowerCase();

    if (name.equals("local")) return nodes(true);

    if (name.equals("path")) return nodes(res.getPath()); 
    if (name.equals("name")) return nodes(res.getName()); 
    //if (name.equals("absolute-path")) return nodes(res.getAbsolutePath());
    //if (name.equals("cannonical-path")) return nodes(res.getCanonicalPath());
    if (name.equals("parent"))
      return nodes((res.getContainer()==null)
		   ? "" : res.getContainer().getPath());
    if (name.equals("exists")) return nodes(res.exists());
    //if (name.equals("writeable")) return nodes(res.canWrite());
    //if (name.equals("readable")) return nodes(res.canRead());
    //if (name.equals("file")) return nodes(res.isFile());
    if (name.equals("directory")) return nodes(res.isContainer());
    //if (name.equals("last-modified")) return nodes(res.lastModified());
    //if (name.equals("length")) return nodes(res.length());
    if (name.equals("files"))
      return res.isContainer()? nodes(res.listNames()) : null;

    return null;
  }

  /** Return an attribute list containing all non-null status items
   *	for a File. 
   */
  public static ActiveAttrList getStatusItems(Resource res) {
    return getStatusItems(res, fileItems);
  }

  /** Return an attribute list containing the specified status items
   *	for a File.
   */
  public static ActiveAttrList getStatusItems(Resource res, String items[]) {
    TreeAttrList list = new TreeAttrList();
    for (int i = 0; i < items.length; ++i) {
      ActiveNodeList v = getStatusItem(res, items[i]);
      if (v != null) list.setAttributeValue(items[i], v);
    }
    return list;
  }

  public static String resourceItems[] = {
    "local", "name", "path", "absolute-path", "canonical-path", "parent", 
    "exists", "writeable", "readable", "file", "directory", "last-modified",
    "length", "files",
  };

  /************************************************************************
  ** Status of Files:
  ************************************************************************/

  /** Get the value of a named status item for a file.
   *	Returns all values if the name is null.
   */
  public static ActiveNodeList getStatusItem(File res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res).asNodeList();
    name = name.toLowerCase();

    if (name.equals("local")) return nodes(true);

    if (name.equals("path")) return nodes(res.getPath()); 
    if (name.equals("name")) return nodes(res.getName()); 
    if (name.equals("absolute-path")) return nodes(res.getAbsolutePath());
    //if (name.equals("cannonical-path")) return nodes(res.getCanonicalPath());
    if (name.equals("parent")) return nodes(res.getParent());
    if (name.equals("exists")) return nodes(res.exists());
    if (name.equals("writeable")) return nodes(res.canWrite());
    if (name.equals("readable")) return nodes(res.canRead());
    if (name.equals("file")) return nodes(res.isFile());
    if (name.equals("directory")) return nodes(res.isDirectory());
    if (name.equals("last-modified")) return nodes(res.lastModified());
    if (name.equals("length")) return nodes(res.length());
    if (name.equals("files"))
      return res.isDirectory()? nodes(res.list()) : null;

    return null;
  }

  /** Return an attribute list containing all non-null status items
   *	for a File. 
   */
  public static ActiveAttrList getStatusItems(File res) {
    return getStatusItems(res, fileItems);
  }

  /** Return an attribute list containing the specified status items
   *	for a File.
   */
  public static ActiveAttrList getStatusItems(File res, String items[]) {
    TreeAttrList list = new TreeAttrList();
    for (int i = 0; i < items.length; ++i) {
      ActiveNodeList v = getStatusItem(res, items[i]);
      if (v != null) list.setAttributeValue(items[i], v);
    }
    return list;
  }

  public static String fileItems[] = {
    "local", "name", "path", "absolute-path", "canonical-path", "parent", 
    "exists", "writeable", "readable", "file", "directory", "last-modified",
    "length", "files",
  };

  /************************************************************************
  ** Status of URL's:
  ************************************************************************/

  /** Get the value of a named status item for a URL. */
  public static ActiveNodeList getStatusItem(URL res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res).asNodeList();
    name = name.toLowerCase();

    if (name.equals("url")) return nodes(res.toString());
    if (name.equals("path")) return nodes(res.getFile()); 
    if (name.equals("protocol")) return nodes(res.getProtocol());
    if (name.equals("port")) return nodes(res.getPort());
    if (name.equals("reference")) return nodes(res.getRef());
    //if (name.equals("query")) return nodes(res.getQuery());

    if (name.equals("remote")) return nodes(true);

    return null;
  }

  public static ActiveAttrList getStatusItems(URL res) {
    return getStatusItems(res, urlItems);
  }

  public static ActiveAttrList getStatusItems(URL res, String items[]) {
    TreeAttrList list = new TreeAttrList();
    for (int i = 0; i < items.length; ++i) 
      list.setAttributeValue(items[i], getStatusItem(res, items[i]));
    return list;
  }

  public static String urlItems[] = {
    "remote", "url", "protocol", "host", "port", "path", "query",
    "reference",    
  };


  /************************************************************************
  ** Status of Entities:
  ************************************************************************/

  /** Get the value of a named status item for an Entity. */
  public static ActiveNodeList getStatusItem(ActiveNode res, String name) {
    if (res == null) return null;
    if (name == null) return getStatusItems(res).asNodeList();
    name = name.toLowerCase();

    if (name.equals("entity")) return nodes(true);
    if (name.equals("name")) return nodes(res.getNodeName());

    TreeExternal ext = (res instanceof TreeExternal) 
      ? (TreeExternal) res : null;

    if (ext != null) {
      if (name.equals("system")) return nodes(ext.getResourceName());
      if (ext.resourceURL != null) {
	if (name.equals("url"))	return nodes(ext.resourceURL.toString());
      }
      if (ext.resourceConnection != null) {
	URLConnection rc = ext.resourceConnection;
	if (name.equals("content-type")) return nodes(rc.getContentType());
	if (name.equals("last-modified")) return nodes(rc.getLastModified());
	if (rc instanceof HttpURLConnection) try {
	  HttpURLConnection hc = (HttpURLConnection) rc;
	  if (name.equals("method")) return nodes(hc.getRequestMethod());
	  if (name.equals("code")) return nodes(hc.getResponseCode());
	  if (name.equals("message")) return nodes(hc.getResponseMessage());
	} catch (IOException ex) {}
      } 
      if (ext.resourceFile != null) {
	if (name.equals("file")) return nodes(ext.resourceFile.getPath());
	if (name.equals("last-modified"))
	  return nodes(ext.resourceFile.lastModified());
      }
    }
    return null;
  }

  public static ActiveAttrList getStatusItems(ActiveNode res) {
    return getStatusItems(res, entityItems);
  }

  public static ActiveAttrList getStatusItems(ActiveNode res,
					      String items[]) {
    TreeAttrList list = new TreeAttrList();
    for (int i = 0; i < items.length; ++i) {
      ActiveNodeList v = getStatusItem(res, items[i]);
      if (v != null) list.setAttributeValue(items[i], v);
    }
    return list;
  }

  public static String entityItems[] = {
    "name", "system", "url", "code", "message", "method", 
    "file", "last-modified", "content-type"
  };
}
