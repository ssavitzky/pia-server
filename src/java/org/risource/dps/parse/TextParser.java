////// TextParser.java: parser for text (non-SGML) files
//	$Id: TextParser.java,v 1.5 2000-10-06 01:19:06 steve Exp $

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
 * A shallow parser for text files. 
 *
 * <p>	TextParser is designed to add ``virtual markup'' to text files; this 
 *	includes both English text and text containing markup.  It is able
 *	to recognize ``code'' of various kinds embedded in the text or markup.
 *
 * <p>	Code parsing is controlled by attributes of the tagset.  If they 
 *	are not specified, reasonable defaults are used that correspond
 *	roughly to Unix shell-script conventions.
 *
 * <p>	TextParser is similar to CodeParser except that it does a better
 *	job of recognizing and handling markup, and is able to recognize
 *	a variety of ways of embedding code in markup.
 *
 * @version $Id: TextParser.java,v 1.5 2000-10-06 01:19:06 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Parser
 * @see org.risource.dps.parse.CodeParser
 */

public class TextParser extends AbstractParser {

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

  /** string that will end current markup */
  protected String markupEnd = null;
  protected char   markupEndChar = 0;

  /** Character that will end the current string */
  protected char currentStringDelim = 0;

  /** Escape character for the current string */
  protected char currentStringEscape = 0;

  protected void markupEnd(String s) {
    markupEnd = s;
    if (s != null) markupEndChar = s.charAt(0);
  }

  protected void markup(String end, String tag) {
    markupEnd = end;
    
  }


  /************************************************************************
  ** parse Parameters:
  ************************************************************************/

  /** table of programming-language keywords. */
  protected Table keywords = new Table();

  /** table of English stop-words (i.e. words not to index in text) */
  protected Table stopwords = new Table();

  /** Table mapping the (one-character) strings that <em>start</em> strings
   *	into one or two character strings consisting of the end character
   *	and the escape character for that starting delimiter.
   *
   * === we'd gain speed if stringDelims was a String array. ===
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

  /** Name of cross-reference namespace */
  protected String xrefsName = null;

  /** cached reference to cross-reference namespace */
  protected Namespace xrefs = null;
  protected TopContext top = null;

  /************************************************************************
  ** Cross-references:
  ************************************************************************/

  /** Look up a cross-reference
   *
   * @return a URL.
   */
  protected String lookupXref(String id) {
    if (xrefs == null && xrefsName != null) {
      if (top == null) top = getProcessor().getTopContext();
      ActiveNode n = Index.getBinding(getProcessor(), xrefsName);
      if (n == null) top.message(-2, "no binding for "+xrefsName, 0, true);
      else xrefs = n.asNamespace();
      if (xrefs == null) {
	if (n != null)
	  top.message(-2, ("binding exists but not a namespace "
			   + n.getClass().getName()), 0, true);
	xrefsName = null;	// only give error once per document.
      }
    }
    if (xrefs != null) {
      ActiveNodeList v = xrefs.getValueNodes(top, id);
      return (v != null)? v.toString() : null;
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
   *	<li> Start tags are tagged as &lt;tag&gt;; end tags as &lt;etag&gt;
   *</ul>
   *
   *<p> A distinction is made between TEXT and CODE states.  TEXT may contain
   *	markup, and may also contain ``stopwords'' that are never cross-indexed.
   *	CODE may contain ``keywords'' which are not cross-indexed, but 
   *	<em>are</em> specially tagged, and may also contain strings and 
   *	comments.  Comments are considered TEXT.  Strings are TEXT-like, but 
   *	do not contain markup.
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
    } else if (state == IN_TEXT && last == '<') { // tag
      // we skip over the "<" for the moment and look at the next char:
      last = in.read();
      if (last < 0) {		// unexpected EOF
	buf.append('<');
      } else if (last == '/') {	// end tag.
	last = in.read();
	if (eatIdent()) {
	  if (last == '>') {
	    last = 0;
	    next = createActiveText("/" + ident, false);
	    nextEnd = "tag";
	    return createActiveElement("tag", null, false);
	  } else {
	    buf.append("</" + ident);
	  }
	} else {
	  buf.append("</");
	}
      } else if (last == '?') {	// processing instruction
	buf.append("<?");	// ===temp
	last = 0;
	markup("?>", "pi");
	
      } else if (last == '!') {	// declaration or comment
	last = in.read();
	if (last == '-') {
	  last = in.read();
	  if (last == '-') {
	    last = 0;
	    markup("-->", "comment");
	    buf.append("<!--");	// === temp.
	  } else {
	    buf.append("<!-");
	  }
	} else {
	  markup(">", "decl");
	  buf.append("<!");	// === temp.
	}

      } else if (idc[last] == 0) { // stray '<'
	buf.append('<');
      } else {
	state = IN_TAG;
	eatIdent();		// === look up tagname for style
	next = createActiveText(ident, false); // === tagname should be xref
	return createActiveElement("tag", null, false);
      }
    } else if (state == IN_TAG && last == '>') {
      last = 0; 
      state = IN_TEXT;
      nextEnd = "tag";
      return null;
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
      if (last > 0) {
	buf.append((char)last);
	last = in.read();
      }
    } else if (state == IN_STRING && 0 == idc[last]) {
      while (last > ' ' && idc[last] == 0 && last != currentStringEscape
	     && last != currentStringDelim) {
	buf.append((char)last);
	last = in.read();
      }
    } else if (0 == idc[last]) { 	// other punctuation ================
      while (last > ' ' && idc[last] == 0 
	     && (state != IN_CODE || stringDelim(last) == null)
	     && (state != IN_TAG || last != '>')) {
	buf.append((char)last);
	last = in.read();
      }
      // Check for start of comment terminated by EOL. 
      // === for now, just start comment with # or // as first nonblank.
      if (state == IN_CODE && clength > 0 && lookingAt(comment, clength) ) {
	state = IN_COMMENT;
	next = createActiveText(buf.toString(), false);
	return createActiveElement("rem", null, false);
      } else if (state == IN_CODE
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
    xrefsName = tse.getAttribute("xrefs");

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
    
    state = IN_TEXT;

    next = createActiveElement("line", null, true);
    super.initialize();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TextParser() {
    super();
  }

  public TextParser(InputStream in) {
    super(in);
  }

  public TextParser(Reader in) {
    super(in);
  }

}
