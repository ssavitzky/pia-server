////// CodeParser.java: parser for "code" (non-SGML) files
//	$Id: CodeParser.java,v 1.4 2001-04-03 00:04:45 steve Exp $

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


package org.risource.dps.parse;

import org.w3c.dom.Node;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.Copy;
import org.risource.dps.util.Index;

import org.risource.dps.tree.TreeAttrList;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeComment;


import org.risource.ds.Table;
import org.risource.ds.List;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * A shallow parser for non-SGML (code) files. 
 *
 * <p>	CodeParser is designed to add ``virtual markup'' to "code" files;
 *	i.e. source code in various programming languages.  
 *
 * <p>	Parsing is controlled by attributes of the tagset.  If they 
 *	are not specified, reasonable defaults are used that correspond
 *	roughly to Unix shell-script conventions.
 *
 * <p>	Unlike TextParser, CodeParser has only a limited ability to recognize
 *	markup (and in fact the initial implementation can't recognize markup
 *	at all).
 *
 * @version $Id: CodeParser.java,v 1.4 2001-04-03 00:04:45 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Parser
 * @see org.risource.dps.parse.TextParser
 */

public class CodeParser extends ShallowParser {

  /************************************************************************
  ** State Constants:
  ************************************************************************/

  /** State constant for "code": look for programming-language keywords,
   *	comments, and strings. 	All code-like states have 1 in the low-order 
   *	bit.
   */
  protected final static int IN_CODE = 1; 

  /** Check to see if we are in a code-like state. */
  protected final boolean inCode() { return (state & 1) != 0; }

  /** State constant for SGML start tags.  This is basically code-like. */
  protected final static int IN_TAG = 3;

  /** State constant for "text": keywords become stopwords.
   *	All text-like states have 0 in the low-order bit.
   */
  protected final static int IN_TEXT = 0;

  /** Check to see if we are in a text-like state. */
  protected final boolean inText() { return (state & 1) == 0; }

  /** Inside a comment that is terminated by EOL.  Text-like state. */
  protected final static int IN_COMMENT = 2;

  /** Inside a comment that is terminated by "cend".  Text-like state. */
  protected final static int IN_DELIMITED_COMMENT = 4;

  /** Inside a quoted string.  This is a Text-like state. */
  protected final static int IN_STRING = 6;

  /************************************************************************
  ** State:
  ************************************************************************/

  /** State outside of a tag. */
  protected int outsideTag = 0;

  /** Character that will end the current string */
  protected char currentStringDelim = 0;

  /** Escape character for the current string */
  protected char currentStringEscape = 0;

  /************************************************************************
  ** parse Parameters:
  ************************************************************************/

  /** table of programming-language keywords. */
  protected Table keywords = new Table();

  /** table of English stop-words (i.e. words not to index in text) */
  protected Table stopwords = new Table();

  /** Array mapping the (one-character) strings that <em>start</em> strings
   *	into one or two character strings consisting of the end character
   *	and the escape character for that starting delimiter.
   */
  protected String stringDelims[] = new String[256];
  protected final String stringDelim(int c) {
    return (c > 0 && c < 255)? stringDelims[c] : null;
  }

  /** The string that starts a comment that ends at the end of the line. */
  protected String comment = "#";	// default to script-like comments.
  protected int	   clength = 1;

  /** The string that starts a comment that ends with "cend". */
  protected String cbegin   = null;
  protected int	   cblength = 0;

  /** The string that ends a comment that started with "cbegin". */
  protected String cend    = null;

  /************************************************************************
  ** Recognizers:
  ************************************************************************/

  /** Get token starting with <code>last</code>.
   *
   *<p>	The input is split into a sequence of Text objects, with whitespace, 
   *	punctuation, identifiers, and newlines in separate Text objects. 
   *	Markup is inserted as follows:
   *
   *<ul>
   *	<li> An empty &lt;line&gt; element preceeds each line.
   *	<li> Language keywords are tagged as &lt;kw&gt;
   *	<li> Other identifiers are tagged as &lt;id&gt;
   *	<li> Comments are tagged as &lt;rem&gt;
   *	<li> Strings are tagged as &lt;str&gt;
   *	<li> &lt;&gt;
   *</ul>
   */
  protected ActiveNode getToken() throws IOException {
    String id = null;

    if (last == 0) last = in.read();
    if (last < 0) return null;

    if (last == lf || last == cr) { 	// end of line
      if (state == IN_COMMENT) { 	// ... which ends a comment.
	nextEnd = "rem";
	state = IN_CODE;
	return null;
      }
      eatEndOfLine();			// ... normal
      next = createActiveElement("line", null, true);
    } else if (last <= ' ') {		// generic whitespace
      eatSpacesInLine();
    } else if (state == IN_CODE && stringDelim(last) != null) {
      String delims = stringDelim(last);
      currentStringDelim = delims.charAt(0);
      currentStringEscape= (delims.length() > 1)? delims.charAt(1) : 0;
      buf.append((char)last);
      last = in.read();
      state = IN_STRING;
      next = createActiveText(buf.toString(), false);
      return createActiveElement("str", null, false);
    } else if (state == IN_STRING && last == currentStringDelim) {
      buf.append((char)last);
      last = in.read();
      state = IN_CODE;
      nextEnd = "str";
    } else if (state == IN_STRING && last == currentStringEscape) {
      buf.append((char)last);
      last = in.read();
      buf.append((char)last);
      last = in.read();
    } else if (state == IN_STRING && 0 == idc[last]) {
      while (last > ' ' && idc[last] == 0 && last != currentStringEscape
	     && last != currentStringDelim) {
	buf.append((char)last);
	last = in.read();
      }
    } else if (0 == idc[last]) { 	// other punctuation ================
      while (last > ' ' && idc[last] == 0 
	     && (state != IN_CODE || stringDelim(last) == null)) {
	buf.append((char)last);
	last = in.read();
      }
      // Check for start of comment terminated by EOL. 
      // === for now, just start comment with # or // as first nonblank.
      if (state != IN_COMMENT && state != IN_DELIMITED_COMMENT
	  && clength > 0 && lookingAt(comment, clength) ) {
	state = IN_COMMENT;
	next = createActiveText(buf.toString(), false);
	return createActiveElement("rem", null, false);
      } else if (state != IN_COMMENT && state != IN_DELIMITED_COMMENT
	  && cblength > 0 && lookingAt(cbegin, cblength) ) {
	state = IN_DELIMITED_COMMENT;
	next = createActiveText(buf.toString(), false);
	return createActiveElement("rem", null, false);
      } 
      // Note that this is not "else if" -- need to handle /**/, etc.
      if (state == IN_DELIMITED_COMMENT && bufContains(cend) ) {
	state = IN_CODE;
	nextEnd = "rem";
      }
    } else if (last >= '0' && last <= '9') { // number
      while (last > 0 && idc[last] != 0) {
	buf.append((char)last);
	last = in.read();
      }
    } else {				// identifier
      while (last > 0 && idc[last] != 0) {
	buf.append((char)last);
	last = in.read();
      }
      // Is this a language keyword, cross-referenced identifier, or other?
      id = buf.toString();
      nextEnd = (keywords.at(id) == null)? null : "kw";

      if (nextEnd != null && state == IN_CODE) {
	next = createActiveText(id, false);
	return createActiveElement(nextEnd, null, false);
      } else if (nextEnd != null) {
	nextEnd = null;		// keywords in text are effectively stopwords.
      } else if (stopwords.at(id.toLowerCase()) == null) {
	// === It may not be necessary to check stopwords if state == IN_CODE

	// At this point we know that the word is neither a keyword nor a 
	// stopword, so go ahead and check for cross-references.  

	// === eventually parametrize the kw, id tags and whether we always 
	// === wrap identifiers even if they don't have an xref.

	// do xref check HERE where we can be efficient. 
	String xref = lookupXref(id);
	if (xref != null) {
	  nextEnd = "id";
	  next = createActiveText(id, false);
	  ActiveElement e = createActiveElement(nextEnd, null, false);
	  e.setAttribute("href", xref);
	  return e;
	} else {
	  nextEnd = null;
	}
      }
    }
    if (buf.length() <= 0) return null;
    if (id == null) id = buf.toString();
    return createActiveText(id, false);
  }


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  protected void initialize() {
    // grab stuff as needed from the attributes of the tagset. 
    // We know that a tagset is really an ActiveElement, so cast it.
    ActiveElement tse = (ActiveElement)tagset;

    String wds = tse.getAttribute("keywords");
    if (wds == null) 
      wds = "if else elsif elif while until do for func sub switch case "
	  + "try catch new self this super and or not define "
	  + "class interface static public private protected final "
	  + "int long short byte char void boolean true false null " 
	  + "return print unless my include require import package";
    keywords.append(List.split(wds));

    wds = tse.getAttribute("stopwords");
    if (wds == null) 
      wds = "a an and are do for either i if is me my no nor not of or our so "
 	  + "the this they them then to we were what when where who why yes ";
    stopwords.append(List.split(wds));

    comment = tse.getAttribute("comment");
    cbegin  = tse.getAttribute("cbegin");
    cend    = tse.getAttribute("cend");

    wds = tse.getAttribute("string");
    if (wds == null) {
      stringDelims['"'] = "\"\\";
      stringDelims['\''] = "'\\";
    } else {
      // === set up string delimiters ===
    }

    if (comment == null && cbegin == null) comment="#";

    // Cache delimiter lengths -- we're going to be using them a lot.
    clength = (comment != null)? comment.length() : 0;
    cblength = (cbegin != null)? cbegin.length() : 0;
    
    state = IN_CODE;

    next = createActiveElement("line", null, true);
    super.initialize();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public CodeParser() {
    super();
  }

  public CodeParser(InputStream in) {
    super(in);
  }

  public CodeParser(Reader in) {
    super(in);
  }

}
