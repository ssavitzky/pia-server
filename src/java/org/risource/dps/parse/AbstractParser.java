////// AbstractParser.java: abstract implementation of the Parser interface
//	$Id: AbstractParser.java,v 1.24 2000-09-30 00:10:13 steve Exp $

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
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import org.w3c.dom.Node;

import org.risource.dps.Tagset;
import org.risource.dps.Parser;
import org.risource.dps.Processor;
import org.risource.dps.Handler;
import org.risource.dps.EntityTable;

import org.risource.dps.util.*;
import org.risource.dps.active.*;

import org.risource.dps.tree.TreeComment;
import org.risource.dps.tree.TreeElement;
import org.risource.dps.tree.TreeText;

/**
 * An abstract implementation of the Parser interface.  
 *
 * <p>	This class contains the methods required to recognize the basic
 *	low-level syntactic elements of SGML such as identifiers and tags,
 *	and to traverse the resulting Document.  It should probably be called
 *	something more descriptive of what it actually does, like ``Scanner.''
 *
 * <p>	Eventually, we could probably gain a significant amount of speed by
 *	reading into a char array instead of a StringBuffer.  It's unfortunate 
 *	that BufferedReader doesn't have a version of readLine that returns 
 *	a char array instead of a String.  This would also let us eliminate
 *	<code>last</code> and allow multiple-character backup.
 *
 * @version $Id: AbstractParser.java,v 1.24 2000-09-30 00:10:13 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Parser
 */

public abstract class AbstractParser extends CursorStack implements Parser
{
  /************************************************************************
  ** State:
  ************************************************************************/

  protected Processor   	processor	= null;
  protected ActiveNode 		document 	= null;
  protected LineNumberReader 	in 		= null;
  protected Tagset 		tagset 		= null;
  protected EntityTable 	entities	= null; 

  /** Parser state.  
   * 
   *<p>	Not every parser implementation requires state information, but it's
   *	essential in some cases.  Note that the state is <em>not</em> saved
   *	on the parse stack -- it's not usually necessary, and a subclass
   *	can maintain its own stack if it absolutely has to.
   */
  protected int			state		= 0;

  //protected int			lineNumber	= 1;
  protected final static int	lf		= '\n';
  protected final static int	cr		= '\r';

  /************************************************************************
  ** Access:
  ************************************************************************/

  public ActiveNode getDocument() { return document; }
  public void setDocument(ActiveNode aDocument) {
    document = aDocument; 
    retainTree = (document != null);
  }

  public Processor getProcessor() { return processor; }
  public void setProcessor(Processor aProcessor) { processor = aProcessor; }

  public Reader getReader() { return in; }

  /** Set the Reader.  Forcing this to be a <em>Buffered</em> reader
   *	roughly doubles the speed of the parser!
   */
  public void setReader(Reader aReader) { 
    if (aReader instanceof LineNumberReader) in = (LineNumberReader) aReader;
    else				   in = new LineNumberReader(aReader);
  }

  public Tagset getTagset() { return tagset; }
  public void setTagset(Tagset aTagset) {
    tagset = aTagset;
    if (aTagset != null) {
      caseFoldTagnames = aTagset.caseFoldTagnames();
      ActiveAttrList atts = aTagset.getAttrList();
      strictEndTags = ((atts == null)
		       || !atts.hasTrueAttribute("nonstrictEndTags"));
      if (atts != null) {
	documentWrapper = atts.getAttribute("documentWrapper");
      }
    }
  }

  public EntityTable getEntities() { return entities; }

  protected void setEntities(EntityTable anEntityTable) {
    entities = anEntityTable;
  }

  public void close() {
    if (in != null) try { in.close(); } catch (IOException e) {}
  }

  /** Return the current location as a string suitable for error messages. 
   *
   *<p> Note that line numbers will not be detected correctly on systems that
   * 	don't use <code>'\n'</code> (linefeed) as part of their end-of-line 
   *	sequence (i.e. the Mac).
   */
  public String location() {
    org.risource.site.Document doc
      = getProcessor().getTopContext().getDocument();
    String docName = (doc == null)? "" : doc.getPath() + ":";
    return docName + (in.getLineNumber() + 1) + ":";
  }

  /************************************************************************
  ** Syntax tables:
  ************************************************************************/

  /** True for every character that is part of an identifier.  Does not
   *	distinguish the characters ('-' and '.') that are not officially
   *	permitted at the <em>beginning</em> of an identifier. */
  public static BitSet isIdent;

  /** Non-zero for every character that is part of an identifier. 
   *	This appears to be marginally faster than either a BitSet or
   *	a boolean array.   We should really use bits to distinguish
   *	-, ., _, and so on.
   */
  public static byte idc[] = new byte[Character.MAX_VALUE];

  /** True for every character that is whitespace. */
  public static BitSet isSpace;
  
  /** True for every character permitted in a URL */
  public static BitSet isURL;

  /** True for every character not permitted in an attribute */
  public static BitSet notAttr;

  /** Initialize the identifier and whitespace BitSet's.  Since we are only 
   *	concerned with the SGML reference syntax, we don't have to make these 
   *	public or have a set for each Parser object. */
  static void initializeTables() {
    int i;
    isIdent = new BitSet();
    isSpace = new BitSet();
    isURL = new BitSet();
    notAttr = new BitSet();
    for (i = 0; i <= ' '; ++i) { isSpace.set(i); notAttr.set(i); }
    for (i = 'A'; i <= 'Z'; ++i) { isIdent.set(i); isURL.set(i); idc[i]=1; }
    for (i = 'a'; i <= 'z'; ++i) { isIdent.set(i); isURL.set(i); idc[i]=1; }
    for (i = '0'; i <= '9'; ++i) { isIdent.set(i); isURL.set(i); idc[i]=1; }
    isIdent.set('-'); isURL.set('-'); idc['-']=1; 
    isIdent.set('.'); isURL.set('.'); idc['.']=1; 
    isIdent.set('_'); isURL.set('_'); idc['_']=1; 
    isIdent.set(':'); isURL.set(':'); idc[':']=1; 
    String url = "/?+~%&;";
    for (i = 0; i < url.length(); ++i) isURL.set(url.charAt(i));
    String s = "<>\"'";
    for (i = 0; i < s.length(); ++i) notAttr.set(s.charAt(i));
  }

  /************************************************************************
  ** SGML flags and data:
  **
  ** <p> The flags in this section control various parameters of the SGML
  **	 parsing process.  Strictly speaking they ought to be set from the
  **	 Tagset (DTD).  Defaults will usually differ among Parser
  **	 implementation classes.
  **
  ************************************************************************/

  /** If <code>true</code>, entities must be terminated by ';' */
  protected boolean strictEntities = true;

  /** If <code>true</code>, missing end tags generate error messages */
  protected boolean strictEndTags = true;

  /** The character that starts an entity (default '<code>&amp;</code>'). */
  protected char entityStart = '&';

  /** The character that starts a parameter entity (default '<code>%</code>').
   *	=== Parameter entities are presently not recognized. 
   */
  protected char parameterStart = '%';

  /** The character that ends an entity (default '<code>;</code>'). */
  protected char entityEnd = ';';

  /** Do we case-fold tagnames? */
  protected boolean caseFoldTagnames = true;

  /** Do we split text into whitespace and non-whitespace? */
  protected boolean splitTextTokens = false;

  /** Do we require non-identifier attributes to be quoted, or are we lenient?
   *	This interacts with both HTML (where URL's are often mistakenly not
   *	quoted), and XML (where the closing ``<code>/</code>'' that indicates
   *	an empty tag is often next to an identifier).
   */
  protected boolean strictAttributeQuotes = false;

  /** Tagname of an element in which to wrap the entire document. */
  protected String		documentWrapper = null;

  /************************************************************************
  ** Low-level Recognizers:
  ************************************************************************/

  /** Holds characters that have been ``eaten'' from the stream. */
  protected StringBuffer buf = new StringBuffer(256);

  /** Holds an identifier ``eaten'' from the stream. */
  protected String ident;

  /** Holds the character that terminated the current token, or -1 if
   *    the token was terminated by end-of-file.  It will be prepended to 
   *	the <em>next</em> token if non-null.  This gives the scanner
   *	one character of lookahead, which is almost always enough.
   */
  protected int last = 0;

  /** Returns true if it is known that no more tokens are available. 
   *	The implementation takes advantage of the fact that a Reader returns
   *	<code>-1</code> if no input is available, and that the last character
   *	read is always in <code>last</code>.
   */
  public boolean atEnd() {
    return last < 0;
  }

  /** Returns true if aString is an initial substring of buf
   */
  public final boolean lookingAt(String aString) {
    int len = aString.length();
    if (buf.length() < len) return false;
    for (int i = 0; i < len; ++i)
      if (buf.charAt(i) != aString.charAt(i)) return false;
    return true;
  }

  /** Returns true if aChar is the first character of buf.
   *	Blindly assumes that buf contains at least one character.
   */
  public final boolean lookingAt(char aChar) {
    return buf.charAt(0) == aChar;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to
   *	<code>buf</code> until the next non-ordinary character (&amp; or
   *	&lt;) or end-of-buffer is seen.  The terminating character ends
   *	up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. */
   protected final boolean eatText() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    if (last == entityStart || last == '<') return true;
    do {
      // if (last == lf) lineNumber++;
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != entityStart && last != '<');
    return last >= 0;		// === should probably return true
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to
   *	<code>buf</code> until the next non-ordinary character (&amp; or
   *	&lt;), end-of-line, or end-of-buffer is seen.  The terminating
   *	character ends up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. 
   */
   protected final boolean eatTextInLine() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    if (last == entityStart || last == '<' || last == lf || last == cr)
      return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != lf  && last != cr &&
	     last != entityStart && last != '<');
    return true;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to
   *	<code>buf</code> until end-of-line or end-of-buffer is seen.  
   *	The terminating character ends up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. 
   */
   protected final boolean eatLine() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    if (last == lf || last == cr) return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != lf  && last != cr);
    return true;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), eat and return an end-of-line
   *	sequence. 
   *
   *	@return true if at least one character is eaten. 
   */
   protected final boolean eatEndOfLine() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    buf.append((char)last);
    if (last == cr) {
      last = in.read();
      if (last == lf) { 
	buf.append((char)last);
	last = in.read();
      }
    } else {
      last = in.read();
    }
    return true;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to a
   *	String until a character that does not belong in an identifier
   *	is found.  Identifiers, in SGML-land, may include letters,
   *	digits, "-", and ".".  The terminating character ends up in
   *	<code>last</code>, and the string in <code>ident</code>.
   *
   *	@return true if at least one character is eaten.
   */
   protected final boolean eatIdent() throws IOException {
    if (last == 0) last = in.read();
    String id = "";
    if (last < 0 || 0 == idc[last]) {
      // if (last == lf) lineNumber++;
      return false;
    }
    do {
      id += (char)last;
      last = in.read();
    } while (last > 0 && 0 != idc[last]);
    // was while (last > 0 && isIdent.get(last))
    // if (last == lf) lineNumber++;
    ident = id;
    return true;    
  }
    
  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aCharacter</code> (typically a
   *	quote) is seen.
   *
   *	@return false if end-of-file is reached before a match. */
   protected final boolean eatUntil(int aCharacter, boolean checkEntities)
       throws IOException {
    if (last == 0) last = in.read();
    // if (last == lf) lineNumber++;
    while (last >= 0 && last != aCharacter
	   && !(checkEntities && last == entityStart)) {
      buf.append((char)last);
      last = in.read();
      // if (last == lf) lineNumber++;
    } 
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aCharacter</code> (typically a
   *	quote) or newline is seen.
   *
   *	@return false if end-of-file is reached before a match. */
   protected final boolean eatLineUntil(int aCharacter, boolean checkEntities)
       throws IOException {
    if (last == 0) last = in.read();
    while (last >= 0 && last != aCharacter && last != lf && last != cr
	   && !(checkEntities && last == entityStart)) {
      buf.append((char)last);
      last = in.read();
    } 
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until a character in <code>aBitSet</code> is seen.
   *
   *	@return false if end-of-file is reached before a match. */
   protected final boolean eatUntil(BitSet aBitSet, boolean checkEntities)
       throws IOException {
    if (last == 0) last = in.read();
    while (last >= 0 && ! aBitSet.get(last)
	   && !(checkEntities && last == entityStart)) {
      buf.append((char)last);
      last = in.read();
    } 
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aString</code> (typically an end
   *	tag) is matched.  A case-insensitive match is done.
   *
   *	@return false if end-of-file is reached before a match. */
   protected final boolean eatUntil(String aString, boolean checkEntities)
       throws IOException {
    int start = buf.length();
    aString = aString.toLowerCase();
    int matchLength = aString.length();
    char aCharacter = aString.charAt(0);
    int itsPosition = -1;
    int nextPosition = aString.indexOf(aCharacter, 1);

    if (last == 0) last = in.read();
    while (last >= 0 && !(checkEntities && last == entityStart)) {

      /* This could be faster, but it could be a lot slower, too.  We
       * append to buf while looking for aCharacter, the first character in
       * aString.  We keep track of its position in itsPosition, the
       * tentative starting point of a match to aString.
       */
      if (Character.toLowerCase((char)last) == aCharacter && itsPosition < 0) {
	itsPosition = buf.length();
      }
      buf.append((char)last);

      /* When we have enough characters to match the whole string, we
       * try for a match.  This would be much simpler if StringBuffer
       * had all the methods of String.
       */
      if (itsPosition >= 0 &&
	  (buf.length() - itsPosition) == matchLength) {
	int i = 1; 
	int j = itsPosition + 1;
	for ( ; i < matchLength; ++i, ++j) {
	  if (aString.charAt(i) != Character.toLowerCase(buf.charAt(j))) {
	    break;
	  }
	}
	if (i == matchLength) {		// Success
	  buf.setLength(buf.length() - matchLength);
	  last = 0;
	  return true;
	}

	/* The match failed.  Advance the tentative starting point to the
	 * next occurrence of aCharacter, if any.  
	 */
	if (nextPosition > 0 && nextPosition < i) {
	  // We matched a second occurrence in aString, so go there.
	  itsPosition += nextPosition;
	} else {
	  itsPosition = -1;
	  // It doesn't occur in the part we matched, but may occur later.
	  for (++i, ++j; i < matchLength; ++i, ++j) {
	    if (Character.toLowerCase(buf.charAt(j)) == aCharacter) {
	      itsPosition = j;
	      break;
	    }
	  }
	}
      }
      last = in.read();
      // if (last == lf) lineNumber++;
    } 
    return last >= 0;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append spaces to 
   * <code>buf</code> until a non-blank character is reached.  */
   protected final boolean eatSpaces() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    while (last >= 0 && last <= ' ') {
      // if (last == lf) lineNumber++;
      buf.append((char)last);
      last = in.read();
    }
    return last >= 0;    
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append spaces to 
   * <code>buf</code> until a non-blank character or newline is reached.  */
   protected final boolean eatSpacesInLine() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    while (last >= 0 && last <= ' ' && last != lf && last != cr) {
      buf.append((char)last);
      last = in.read();
    }
    return last >= 0;    
  }


  /************************************************************************
  ** Parsing Utilities:
  ************************************************************************/


  /** Holds the first item in a three-item queue of tokens.  
   *
   *<p>	In SGML-like markup languages, this will invariably be a text node.
   *	In text-like files, this will usually be a start tag to be 
   *	<em>wrapped around</em> the next text item.
   */
  protected ActiveNode first;

  /** Holds the second item in a three-item queue of tokens.  
   *
   *<p>	In SGML-like markup languages, this will be the start tag or entity
   *	that terminated the text node in <code>first</code>.
   */
  protected ActiveNode next;

  /** Holds the next unmatched end tag. */
  protected String nextEnd;

  protected abstract ActiveNode getToken() throws IOException;

  /** Advance to the next ``token''. 
   *	
   *<p>	The next ``token'' might be either text, in <code>first</code>, 
   *	an ordinary Node, in <code>next</code>, or an end tag, in 
   *	<code>nextEnd</code>.  They are checked in that order.  This allows
   *	getToken one node's worth of lookahead.
   *
   *<p>	Should really use a queue of tokens. 
   *
   * @return <code>null</code> if the next token is an end tag (with the tag
   *	name in <code>nextEnd</code>), otherwise return the next node.
   */
  protected ActiveNode nextToken() {
    ActiveNode n = null;
    if (first == null && next == null && nextEnd == null) try {
      buf.setLength(0);
      first = getToken();	// Try to get some text.
    } catch (IOException e) {
      return null;
    }
    if (first != null) {
      // === here we should check for an implicitly-required paragraph tag.
      n = first;
      first = null;
    } else if (next != null) {
      n = next;
      next = null;
    } 
    return n;
  }

  /** Perform syntax checking for an end tag. 
   *	The normal action for an end tag is to set the atLast flag,
   *	causing the next call on <code>toNext</code> to return false.
   *
   * @return <dl compact> 
   *		<dt>-1<dd> if it is unmatched (and should be ignored), 
   *	      	<dt> 0<dd> if it ends the current Element (normal),
   *		<dt> 1<dd> if it ends a higher level.
   *	     </dl>
   */
  protected int checkEndTag(String tag) {
    if (stack == null) {
      next = createActiveNode(Node.COMMENT_NODE, "Bad end tag: " + tag);
      getProcessor().message(-2, "Bad end tag </" + getTagName(depth-1) + "> near </" + tag + ">", 0, true);
      return -1;
    }
    String inside = stack.getTagName();
    if (caseFoldTagnames) {
      tag = tag.toLowerCase();
      inside = inside.toLowerCase();
    }
    if (tag == null || tag.equals(inside)) {
      // Current tag.  Everything's fine.
      return 0;
    } if (insideElement(tag, caseFoldTagnames)) {
      // ... Yes, we're OK.  End the current element.
	if (strictEndTags == true){
	    getProcessor().message(-2, (location() + "Missing end tag </"
					+ getTagName(depth-1)
					+ "> inserted before </" + tag + ">"),
				   0, true);	
	}
	return 1;
    } else {
      // ... Bad nesting.  Change next to an appropriate comment.
      next = createActiveNode(Node.COMMENT_NODE,
			      "Badly nested end tag: /" + tag);
      getProcessor().message(-2, (location() + "Badly nested end tag </"
				  + getTagName(depth-1) + "> before </"
				  + tag + ">"),
			     0, true);
      return -1;
    }
  }


  /** Perform syntax checking for a start tag or other node. 
   *
   * @return <dl compact> 
   *		<dt>-1<dd> if a different element should be started first.
   *			   (The handler will tell us which one.)
   *			   (currently unimplemented.)
   *	      	<dt> 0<dd> if it is acceptable in this context (normal),
   *		<dt> 1<dd> if it ends the current Element.
   *	     </dl>
   */
  protected int checkStartTag(ActiveNode n) {
    // need to check for implicitEnd
    ActiveElement e = n.asElement();
    if (e == null) {
      // === no check for a node starting a different element;
      // === the text check is done in nextToken.
      return 0;
    }
    String tag = getTagName(depth-1);
    if (tag != null && e.getSyntax().implicitlyEnds(tag)) return 1;
    if (tag != null && !e.getSyntax().isChildOf(tag)) return 1;

    return 0;			// === checkStartTag doesn't work yet.
  }


  /************************************************************************
  ** Node Creation:
  ************************************************************************/
  
  /** Creates an ActiveElement; otherwise identical to CreateElement. 
   */
  protected ActiveElement createActiveElement(String tagname,
					      ActiveAttrList attributes,
					      boolean hasEmptyDelim) {
    if (tagset == null) {
      ActiveElement e = new TreeElement(tagname, attributes);
      if (hasEmptyDelim) e.setHasEmptyDelimiter(hasEmptyDelim);
      e.setIsEmptyElement(e.getSyntax().isEmptyElement(e));
      return e;
    } else {
      return tagset.createActiveElement(tagname, attributes, hasEmptyDelim);
    }
  }

  /** Creates an ActiveNode of arbitrary type with (optional) data.
   */
  protected ActiveNode createActiveNode(short nodeType, String data) {
    return createActiveNode(nodeType, null, data);
  }

  /** Creates an ActiveNode of arbitrary type with name and (optional) data.
   */
  protected ActiveNode createActiveNode(short nodeType,
					String name, String data) {
    ActiveNode n = /* (tagset == null)
      ? Create.createActiveNode(nodeType, name, data)
      : */tagset.createActiveNode(nodeType, name, data);
    return n;
  }

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  protected ActiveText createActiveText(String text, boolean isIgnorable) {
    ActiveText n = (tagset == null)
      ? new TreeText(text, isIgnorable)
      : tagset.createActiveText(text, isIgnorable);
    n.setAction(n.getSyntax().getActionForNode(n));
    return n;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  protected void initialize() { 
    if (documentWrapper != null) {
      setNode(createActiveElement(documentWrapper, null, false));
    } else {
      advanceParser(); 
    }
  }

   
  public AbstractParser() {
    if (isIdent == null) initializeTables();
  }
   
  public AbstractParser(InputStream in) {
    setReader(new InputStreamReader(in));
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(Reader in) {
    setReader(in);
    if (isIdent == null) initializeTables();
  }

  /************************************************************************
  ** Traversal:
  ************************************************************************/
  
  /** Advance the parser to the next Node in the input. 
   *	Used internally in cases where we know there is no possible
   *	sibling, i.e. most of the time.
   */
  protected Node advanceParser() {
    int check;
    // Need to know whether we've seen all the children...
    ActiveNode n = nextToken();
    if (n != null) {
      check = checkStartTag(n);
      if (check == 0) {
	setNode(n);
	if (retainTree) {
	  if (depth > 0) {
	    Node parent = getNode().getParentNode();
	    if (parent != null) Copy.appendNode(n, parent);
	  } else {
	    if (document != null) Copy.appendNode(n, document);
	  }
	} 
	atLast = false;
	return n;
      } else {			// === checkStartTag -> -1 not supported
	atLast = true;
	next = n;		// put the start tag back for next time
	return null;
      }
    } else if (nextEnd != null) {
      // End tag, in nextEnd.
      check = checkEndTag(nextEnd);
      if (check < 0) { 
	n = next;
	next = null;
	nextEnd = null;
	atLast = false;
	return n;
      }
      // If the end tag terminates THIS level, we're done with it.
      if (check == 0) nextEnd = null;
      atLast = true;
      return null;
    } else {
      // end of file. 
      return null;
    }
  }

  public boolean toParent() { return super.toParent(); }

  /** toNext does not test for real siblings; we <em>must</em>
   *	be making a single left-to-right traversal of the file being parsed.
   *
   *<p> We used to check for the presence of an actual sibling.
   *	This messes up severely if the node is already in some tree! 
   *    The original justification was to allow for multiple nodes to be
   *	generated from one token.  This had better not happen; if it does
   *	we will have to add some kind of pushback mechanism to nextToken.
   */
  public boolean toNext() {
    // Check for, and consume, any unparsed children.
    if (hasUnparsedChildren() && !sawChildren) {
      if (toFirstChild()) {
	for ( ; toNext(); ) {}
	toParent();
      }
    }
    return advanceParser() != null;
  }

  public boolean toFirstChild() {
    pushInPlace();
    Node n = advanceParser();
    if (n == null) { popInPlace(); }
    return n != null;
  }

  public boolean toFirst() {
    if (active == null && atFirst()) return toNext();
    else return false;
  }

  public boolean atLast() {
    return atLast;
  }

  /** Test whether the current node has children that have not been parsed. */
  protected boolean hasUnparsedChildren() {
    if (node == null) return false;
    return !node.hasChildNodes()
      && tagName != null && !active.asElement().isEmptyElement();
  }

  public boolean hasChildren() {
    if (node == null) return false;
    return node.hasChildNodes()
      || (tagName != null && !active.asElement().isEmptyElement());
  }

  public void retainTree() {
    retainTree = true;
  }
}
