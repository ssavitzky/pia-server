////// TextParser.java: parser for text (non-SGML) files
//	$Id: TextParser.java,v 1.12 2001-01-11 23:37:35 steve Exp $

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
 *	The idea is not so much to recognize the actual syntax of the text
 *	and its markup, but to identify, with <em>reasonable</em> accuracy,
 *	the different syntactic elements without changing the actual text.
 *
 * <p>	Code parsing is controlled by attributes of the tagset.  If they 
 *	are not specified, reasonable defaults are used that correspond
 *	roughly to Unix shell-script conventions.
 *
 * <p>	TextParser is similar to CodeParser except that it does a better
 *	job of recognizing and handling markup, and is able to recognize
 *	a number of different ways of embedding code in markup.
 *
 * @version $Id: TextParser.java,v 1.12 2001-01-11 23:37:35 steve Exp $
 * @author steve@rii.ricoh.com 
 * @see org.risource.dps.Parser
 * @see org.risource.dps.parse.CodeParser
 */

public class TextParser extends ShallowParser {

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

  /** State constant for SGML declarations.  This is basically code-like. */
  protected final static int IN_DECL = 3;

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

  /** State outside of the current tag or other markup. */
  protected int stateOutsideMarkup = 0;

  /** string that will end current markup */
  protected String markupEnd = null;
  protected char   markupEndChar = 0;

  protected String markupTag = null;

  /** current nesting depth in declarations */
  protected int declDepth = 0;

  /** Character that will end the current string */
  protected char currentStringDelim = 0;

  /** Escape character for the current string */
  protected char currentStringEscape = 0;

  /** indicate an end to the current markup and
   *	return <code>state</code> to what it was before the markup started.
   */
  protected void markupEnd() {
    markupEnd = null;
    markupEndChar = 0;
    state = stateOutsideMarkup;
  }

  /** Set the markupEnd string to <code>s</code>,
   *	save the current state in <code>stateOutsideMarkup</code> and
   *	set <code>state</code> to <code>newState</code>.
   */
  protected void markup(String s, String tag, int newState) { 
    markupEnd = s;
    markupEndChar = s.charAt(0);
    markupTag = tag;
    stateOutsideMarkup = state;
    state = newState;
  }


  /************************************************************************
  ** parse Parameters:
  ************************************************************************/

  /** table of programming-language keywords. */
  protected Table keywords = new Table();

  /** table of English stop-words (i.e. words not to index in text) */
  protected Table stopwords = new Table();

  /** Array mapping the (one-character) strings that <em>start</em> strings
   *	into one- or two-character strings consisting of the end character
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
   *	<li> Start tags are tagged as &lt;tag&gt;; end tags as &lt;etag&gt;
   *	<li> SGML comments are tagged as &lt;comment&gt;
   *	<li> SGML processing instructions are tagged as &lt;pi&gt;; ASP/JSP 
   *	     code fragments delimited by &lt;% etc. are parsed as PI's. 
   *	<li> SGML declarations are tagged as &lt;decl&gt;
   *</ul>
   *
   *<p> A distinction is made between TEXT and CODE states.  TEXT may contain
   *	markup, and may also contain ``stopwords'' that are never cross-indexed.
   *	CODE may contain ``keywords'' which are not cross-indexed, but 
   *	<em>are</em> specially tagged, and may also contain strings and 
   *	comments.  Comments are considered TEXT.  Strings are TEXT-like, but 
   *	do not contain markup.
   *
   *<p> The content of SGML comment and processing instruction nodes is
   *	parsed as code.  The values of attributes are parsed as strings.
   *	
   *<p> There are a couple of major limitations:  (1) because we don't buffer 
   *	an entire line at a time, we don't detect things like `comment line 
   *	ending with ":"'.  (2) Because we only do a token at a time we can't
   *	easily distinguish filenames or URL's from their components.  (3) 
   *	Because of single-character lookahead, we can't distinguish between
   *	a word at the end of a sentence and an identifier ending with ".".
   */
  protected ActiveNode getToken() throws IOException {
    String id = null;
    int count = 0;		// count of consecutive markupEndChar's

    if (last == 0) last = in.read();
    if (last < 0) return null;

    // The implementation could probably be cleaner, but this gets the job 
    // done.  The basic idea is to dispatch on the current character, then
    // gobble the entire next token.  The big problem is that this approach
    // only gives you one character's worth of lookahead, which isn't enough
    // to do some things properly.  What we _really_ need is a Java version
    // of "lex".

    // Also, a lot of things would be simplified if we could buffer a 
    // line at a time.  This would allow lookahead over multiple tokens.

    if (last == lf || last == cr) { 	////// end of line //////////////////
      if (state == IN_COMMENT) { 	// ... which ends a comment.
	nextEnd = "rem";
	state = IN_CODE;
	return null;
      }
      eatEndOfLine();			// ... normal
      next = createActiveElement("line", null, true);
    } else if (last <= ' ') {		// generic whitespace
      eatSpacesInLine();
    } else if (state == IN_TEXT && last == '<') { ////// tag ////////////////
      // we skip over the "<" for the moment and look at the next char:
      last = in.read();
      if (last < 0) {		// unexpected EOF
	buf.append('<');
      } else if (last == '/') {		////// end tag.
	last = in.read();
	if (eatIdent()) {
	  if (last == '>') {
	    last = in.read();
	    next = createActiveText("/" + ident, false);
	    nextEnd = "tag";
	    return createActiveElement("tag", null, false);
	  } else {
	    buf.append("</" + ident);
	  }
	} else {
	  buf.append("</");
	}
      } else if (last == '?') {		////// processing instruction
	buf.append("<?");	// ===temp
	last = 0;
	markup("?>", "pi", IN_CODE);
	next = createActiveText(buf.toString(), false);
	return createActiveElement("pi", null, false);
      } else if (last == '%') {		////// JSP/ASP processing instruction
	buf.append("<%");	// ===temp
	last = 0;
	markup("%>", "pi", IN_CODE);
	next = createActiveText(buf.toString(), false);
	return createActiveElement("pi", null, false);
      } else if (last == '!') {		////// declaration or comment
	last = in.read();
	if (last == '-') {
	  last = in.read();
	  if (last == '-') {
	    last = in.read();
	    buf.append("<!--");	// === temp.
	    markup("-->", "comment", IN_TEXT); // neither CODE nor TEXT is right
	    next = createActiveText("<!--", false);
	    return createActiveElement("comment", null, false);
	  } else {
	    buf.append("<!-");
	  }
	} else {			////// declaration
	  // markup(">", "decl", IN_DECL);
	  state = IN_DECL;
	  declDepth = 1;
	  markupTag = "decl";
	  buf.append("<!");	// === temp.
	  next = createActiveText("<!", false);
	  return createActiveElement("decl", null, false);
	}

      } else if (idc[last] == 0) { 	////// stray '<'
	buf.append('<');
      } else {				////// Start tag
	state = IN_TAG;
	markupTag = "tag";
	eatIdent();		// === look up tagname for style
	next = createActiveText(ident, false); // === tagname should be xref
	return createActiveElement("tag", null, false);
      }
    } else if (state == IN_TAG && last == '>') {
      last = 0; 
      nextEnd = markupTag;
      markupTag = null;
      state = IN_TEXT;
      if (nextEnd == "tag") { return null; }
      else return createActiveText(">", false);
    } else if (state == IN_DECL && last == '>') {
      // === This is only part of the solution for declarations.  
      // === Need to handle [ and ]> and all that.   Punt.  See below.
      last = 0; 
      buf.append(">");
      nextEnd = markupTag;
      markupTag = null;
      if (--declDepth == 0) state = IN_TEXT;
      return createActiveText(">", false);
    } else if (state == IN_DECL && last == '<') {
      // === This is only part of the solution for declarations.  
      // === Need to handle [ and ]> and all that.   Punt.  For now, just
      // === make sure that < and > balance.  This will, of course, lose
      // === big if the declaration contains CDATA sections or comments.
      last = 0; 
      ++declDepth;
      buf.append("<");	// === temp.
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
	     && (state != IN_TAG || last != '>')
	     && (state != IN_DECL || last != '>')
	     && (count == 0 || last != '>')
	     ) {
	buf.append((char)last);
	if (last == markupEndChar) ++count; else count = 0;
	last = in.read();
      }

      // This ends a PI or declaration.
      if (count > 0 && last == '>') {
	buf.append((char)last);
	last = in.read();
	// === string or comment may need an end tag here ===
	markupEnd();
	return createActiveText(buf.toString(), false);
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
	if (last == '-') ++count; else count = 0;
	last = in.read();
      }
      // Take advantage of the fact that "-" can occur in identifiers, and
      // see whether this is actually the end of a comment.
      if (last == '>' && markupEndChar == '-' && count >= 2) {
	buf.append((char)last);
	last = 0;
	nextEnd = markupTag;
	markupEnd();
	return createActiveText(buf.toString(), false);
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
