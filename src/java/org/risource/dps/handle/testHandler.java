////// testHandler.java: <test> handler.
//	$Id: testHandler.java,v 1.12 2001-04-03 00:04:33 steve Exp $

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


package org.risource.dps.handle;

import org.risource.ds.Association;
import org.risource.ds.List;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.TreeText;

import JP.ac.osaka_u.ender.util.regex.RegExp;
import JP.ac.osaka_u.ender.util.regex.MatchInfo;

import java.util.Enumeration;

/**
 * Handler for <test>  <p>
 *
 * @version $Id: testHandler.java,v 1.12 2001-04-03 00:04:33 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class testHandler extends GenericHandler {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean  inverted = false;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This action routine ought to check <em>all</em> of the attributes, even 
   *	in the presence of parse-time dispatching, because some of the
   *	attributes may have contained entities.
   */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Default is simply to test for "truth"
    returnBoolean(Test.orValues(content, aContext), aContext, out, atts);
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();

    // The following tests only involve the content. 
    //   Note that we <em>must</em> test for "sorted" before "numeric"
    //   in order to resolve the ambiguity.

    if (dispatch(e, "zero", "op")) 	return test_zero.handle(e);
    if (dispatch(e, "positive", "op")) 	return test_positive.handle(e);
    if (dispatch(e, "negative", "op")) 	return test_negative.handle(e);
    if (dispatch(e, "null", "op")) 	return test_null.handle(e);
    if (dispatch(e, "sorted", "op"))	return test_sorted.handle(e);
    if (dispatch(e, "numeric", "op"))	return test_numeric.handle(e);
    if (dispatch(e, "markup", "op"))	return test_markup.handle(e);

    // The following test the content against the value of the attribute.

    if (dispatch(e, "equals")) 	 return test_equality.handle(e);
    if (dispatch(e, "greater"))  return test_greater.handle(e);
    if (dispatch(e, "less")) 	 return test_less.handle(e);
    if (dispatch(e, "match")) 	 return test_match.handle(e);

    if (e.getAttributes() == null || e.getAttributes().getLength() == 0)
      return this;
    else return new testHandler(e);
  }

  /** This returns a boolean <code>value</code> according to the 
   *	<code>not</code>, <code>iftrue</code>, and <code>iffalse</code>
   *	attributes.  <p>
   *
   *	The right information could be encoded into a new instace of the
   *	Handler if necessary; this would require more data but would
   *	definitely run faster.  We may want to consider this later.
   */
  public void returnBoolean(boolean value, Context cxt, Output out,
			    ActiveAttrList atts) {
    if (atts.getLength() == 0) {
      if (value) { putText(out, cxt, "1"); }
      return;
    }
    ActiveNodeList rv;
    if (atts.hasTrueAttribute("not")) value = !value;
    if (value) { putText(out, cxt, "1"); }
    // nothing to do if there's no false return value.
  }

  /** This returns a boolean <code>value</code> possibly modified by
   *	a <code>not</code> attribute already stored in the handler instance.
   *	<p>
   *
   *	This routine takes a Context because the <code>iftrue</code> and
   *	<code>iffalse</code> attributes were acquired at parse time and
   *	may contain entities that have to be expanded.
   */
  public void returnBoolean(boolean value, Context c, Output out) {
    //c.debug("<test> returning " + value + " " + getClass().getName() + "\n");
    if (inverted) value = !value;
    if (value) { putText(out, c, "1"); }
    // nothing to do if there's no false return value.
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public testHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY;
  }

  /** Construct a specialized action. */
  public testHandler(boolean string, boolean text, boolean invert) {
    inverted 	= invert;

    /* Expansion control: */
    textContent = text;		//	want only text in content?

    expandContent = true;	// false	Expand content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY;
  }

  public testHandler(ActiveElement e) {
    this(false, e.hasTrueAttribute("text"),
	 e.hasTrueAttribute("not"));
  }
  public testHandler(ActiveElement e, boolean text, boolean string) {
    this(string, text,
	 e.hasTrueAttribute("not"));
  }
}

/*	=== unimplemented stuff from legacy implementation ===
    boolean result = false;
    SGML test = Util.removeSpaces(it.content());

    if (it.hasAttr("link")) {
      ii.error(ia, "link attr unimplemented.");
    } else if (it.hasAttr("text")) {
      test = test.contentText();
    } 
*/

/* ***********************************************************************
 * Subclasses:
 *
 *	These subclasses cannot be used as stand-alone handlers; they
 *	really only work as <code>action</code> handlers because they
 *	assume that <code>trueValue</code>, <code>falseValue</code>, etc. 
 *	are set up properly. 
 *
 ************************************************************************/

/** Test for zero.  Whitespace is considered zero, but non-blanks are not. */
class test_zero extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(( (a.isNumeric() && a.doubleValue() == 0.0)
		    || (!a.isNumeric() && Test.isWhitespace(cstring))),
		  aContext, out);
  }
  public test_zero(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_zero(e); }
}

/* Test for equality. */
class test_equality extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);

    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String attstring = atts.getAttribute("equals");
    Association att = Association.associateNumeric(null, attstring);

    returnBoolean( a.isNumeric() && att.isNumeric() &&
		   a.doubleValue() ==  att.doubleValue() , 
		   aContext, out);
  }
  public test_equality(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_equality(e); }
}


/* Test for for less-than. */
class test_less extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);

    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String attstring = atts.getAttribute("less");
    Association att = Association.associateNumeric(null, attstring);

    returnBoolean( a.isNumeric() && att.isNumeric() &&
		   a.doubleValue() <  att.doubleValue() , 
		   aContext, out);
  }
  public test_less(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_less(e); }
}


/* Test for greater than. */
class test_greater extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);

    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String attstring = atts.getAttribute("greater");
    Association att = Association.associateNumeric(null, attstring);

    returnBoolean( a.isNumeric() && att.isNumeric() &&
		   a.doubleValue() >  att.doubleValue() , 
		   aContext, out);
  }
  public test_greater(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_greater(e); }
}


class test_positive extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() > 0.0, aContext, out);
  }
  public test_positive(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_positive(e); }
}

class test_negative extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.doubleValue() < 0.0, aContext, out);
  }
  public test_negative(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_negative(e); }
}

/** Test for numeric.  Although whitespace is considered equal to zero,
 *	it is not considered numeric. */
class test_numeric extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);
    Association a = Association.associateNumeric(null, cstring);
    returnBoolean(a.isNumeric(), aContext, out);
  }
  public test_numeric(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_numeric(e); }
}

class test_sorted extends testHandler {

  protected boolean reverse = false;
  protected boolean caseSens = false;
  protected boolean text = false;
  boolean result = false;

  public void action(Input in, Context aContext, Output out,
		     ActiveAttrList atts, ActiveNodeList content) {

    boolean result = true;

    reverse = atts.hasTrueAttribute("reverse");
    caseSens = caseSensitive(atts);
    text = dispatch(atts, "text", "filter");
    boolean numeric = dispatch(atts, "numeric", "keys");

    List tl;
    if (text) {			// text: filter out the markup
      tl = TextUtil.getTextList(TextUtil.getText(content), caseSens);
    } else if (numeric) {
      tl = MathUtil.getNumericList(content);
    } else {			// default: sort on markup's text key
      tl = TextUtil.getTextList(content, caseSens);
    }
      
    long len = tl.size();
    for(int i = 1; i < len; i++) {
      int c = ((Association)tl.at(i)).compareTo((Association)tl.at(i-1));
      if ( (reverse && (c > 0)) || (!reverse && (c < 0)) ) {
	result = false;
	break;
      }
    }

    returnBoolean(result, aContext, out);
  }

  public test_sorted(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_sorted(e); }
}

class test_match extends testHandler {
  boolean exactMatch = false;
  boolean caseSens   = false;
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String cstring = textContent
      ? Expand.getProcessedTextString(in, aContext)
      : Expand.getProcessedContentString(in, aContext);

    String match = atts.getAttribute("match");
    if (match == null) match = "";
    boolean result = false;
    if (exactMatch) {
      result = caseSens? match.equals(cstring)
		       : match.equalsIgnoreCase(cstring);
    } else {
      if (! caseSens) {
	cstring = cstring.toLowerCase();
	match   = match.toLowerCase();
      }
      try {
	RegExp re = new RegExp(match);
	MatchInfo mi = re.match(cstring);
	result = (mi != null && mi.end() >= 0);
      } catch (Exception ex) {
	// === ii.error(ia, "Exception in regexp: "+ex.toString());
      }
    }
    returnBoolean(result, aContext, out);
  }
  public test_match(ActiveElement e) {
    super(e);
    exactMatch = e.hasTrueAttribute("exact");
    caseSens   = caseSensitive(e.getAttrList());
  }
  static Action handle(ActiveElement e) { return new test_match(e); }

}

class test_null extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = textContent
      ? Expand.getProcessedText(in, aContext)
      : Expand.getProcessedContent(in, aContext);

    returnBoolean(content == null || content.getLength() == 0, aContext, out);
  }
  public test_null(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new test_null(e); }
}

/** Test for the presence of markup, defined as anything other than text. */
class test_markup extends testHandler {
  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = Expand.getProcessedContent(in, aContext);
    int len = (content == null)? 0 : content.getLength();
    for (int i = 0; i < len; i++) {
      if (! NodeType.isText(content.activeItem(i))) {
	returnBoolean(true, aContext, out);
	return;
      }
    }
    returnBoolean(false, aContext, out);
  }
  public test_markup(ActiveElement e) { super(e, true, true); }
  static Action handle(ActiveElement e) { return new test_markup(e); }
}

