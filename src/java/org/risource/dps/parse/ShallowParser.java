////// ShallowParser.java: perform a ``shallow'' parse of SGML files
//	$Id: ShallowParser.java,v 1.3 2000-10-20 23:54:54 steve Exp $

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


package org.risource.dps.parse;

import org.w3c.dom.Node;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.namespace.*;
import org.risource.dps.util.Copy;
import org.risource.dps.util.Index;

import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeComment;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * A Parser that produces a ``shallow'' parse of text, code, or markup files. 
 *
 * <p>	ShallowParser parses marked-up text in essentially the same way as
 *	BasicParser <em>except</em> that the markup it generates is 
 *	<em>not</em> the markup in the file itself, but instead describes
 *	the file's metasyntactic structure.  For example, start tags
 *	and end tags are exposed as &lt;tag&gt; and  &lt;etag&gt; elements,
 *	respectively.
 *
 * @version $Id: ShallowParser.java,v 1.3 2000-10-20 23:54:54 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Parser
 */

public abstract class ShallowParser extends AbstractParser {


  /************************************************************************
  ** Cross-references:
  ************************************************************************/

  /** Name of cross-reference namespace */
  protected String xrefsName = null;

  /** cached reference to cross-reference namespace */
  protected Namespace xrefs = null;
  protected PropertiesWrap pxrefs = null;

  /** Prefix for cross-references */
  protected String xprefix = null;

  protected TopContext top = null;

  /** Look up a cross-reference.  
   *	All keywords are lowercased for lookup, which is a crock: it ought
   *	to be specified by the application.
   *
   * @return a URL.
   */
  protected String lookupXref(String id) {
    if (xrefs == null && xrefsName != null) {
      if (top == null) top = getProcessor().getTopContext();
      ActiveNode n = Index.getBinding(getProcessor(), xrefsName);
      if (n == null) {
	//top.message(-2, "no binding for "+xrefsName, 0, true);
      } else {
	xrefs = n.asNamespace();
	if (xrefs instanceof PropertiesWrap) pxrefs = (PropertiesWrap)xrefs; 
      }
      if (xrefs == null) {
	if (n != null)
	  top.message(-2, ("binding exists but not a namespace "
			   + n.getClass().getName()), 0, true);
	xrefsName = null;	// only give error once per document.
      }
    }
    id = id.toLowerCase();
    if (pxrefs != null) {
      // if we have a PropertiesWrap, look up the string directly.
      String s = pxrefs.getProperty(id);
      if (s == null) return null;
      if (xprefix != null) s = xprefix + s;
      if (s.endsWith("/")) s += id;
      return s;
    } else if (xrefs != null) {
      ActiveNodeList v = xrefs.getValueNodes(top, id);
      if (v == null) return null;
      String s = v.toString();
      if (xprefix != null) s = xprefix + s;
      if (s.endsWith("/")) s += id;
      return s;
    } else {
      return null;
    }
  }


  /************************************************************************
  ** Recognizers:
  ************************************************************************/

  /** Returns true if aString is an initial substring of buf
   */
  public final boolean lookingAt(String aString, int length) {
    if (buf.length() < length) return false;
    for (int i = 0; i < length; ++i)
      if (buf.charAt(i) != aString.charAt(i)) return false;
    return true;
  }

  /** Returns true if aString is a substring of buf
   */
  public final boolean bufContains(String aString) {
    int slength = aString.length();
    int blength = buf.length();
    if (blength < slength) return false;
    for (int i = 0; i <= blength - slength; ++i) {
      boolean x = true;
      for (int j = 0; j < slength; ++j)
	if (buf.charAt(i + j) != aString.charAt(j)) {
	  x = false;
	  break;
	}
      if (x) return true;
    }
    return false;
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  protected void initialize() {
    // grab stuff as needed from the attributes of the tagset. 
    // We know that a tagset is really an ActiveElement, so cast it.
    ActiveElement tse = (ActiveElement)tagset;

    xrefsName = tse.getAttribute("xrefs");
    xprefix = tse.getAttribute("xprefix");

    super.initialize();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ShallowParser() {
    super();
  }

  public ShallowParser(InputStream in) {
    super(in);
  }

  public ShallowParser(Reader in) {
    super(in);
  }

}
