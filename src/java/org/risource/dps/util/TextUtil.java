////// TextUtil.java: Text-Processing Utilities 
//	$Id: TextUtil.java,v 1.14 2001-04-03 00:05:02 steve Exp $

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


package org.risource.dps.util;

import org.w3c.dom.Node;
//import org.w3c.dom.Text;
 
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.output.*;
import org.risource.dps.input.*;
import org.risource.dps.tree.TreeEntity;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeNodeList;

import org.risource.dps.namespace.BasicEntityTable;

import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Association;

import org.risource.util.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.net.*;

/**
 * Text-processing utilities.
 *
 *	Many of these utilities operate on Text nodes in NodeLists, as well
 *	as (or instead of) on strings. 
 *
 * @version $Id: TextUtil.java,v 1.14 2001-04-03 00:05:02 steve Exp $
 * @author steve@rii.ricoh.com
 *
 */

public class TextUtil {

  /************************************************************************
  ** Standard Character Entities:
  ************************************************************************/

  static BasicEntityTable charEnts = new BasicEntityTable();
  public static BasicEntityTable getCharacterEntities() { return charEnts; }

  static protected void dc(char c, String name) {
    charEnts.setBinding(name, new TreeEntity(name, c));
  }

  static {
    dc('&', "amp");
    dc('<', "lt");
    dc('>', "gt");
  }

  /************************************************************************
  ** Value extraction:
  ************************************************************************/

  /** Convert a node to a string, ignoring markup.
   *	=== Conversion to string is problematic: need internal and external
   */
  public static String getTextString(ActiveNode n) {
    // First, see if n is a text node.  If it is, just convert it to a string.
    if (n.getNodeType() == Node.TEXT_NODE
	|| n.getNodeType() == Node.CDATA_SECTION_NODE
	|| n.getNodeType() == Node.ENTITY_REFERENCE_NODE
	|| n.getNodeType() == Node.ENTITY_NODE)
      return n.getNodeValue();

    // If that doesn't work, we have to go through its entire contents and
    // filter out the text.  We do it by cascading a FilterText (a kind of
    // Output) into a ToString (another kind of Output).

    ToString out = new ToString();
    Copy.copyNode(n, new FromParseTree(n), new FilterText(out));
    return out.getString();
  }

  /** Extract text from a nodelist.
   */
  public static ActiveNodeList getText(ActiveNodeList nl) {
    ToNodeList out = new ToNodeList(null);
    Copy.copyNodes(nl, new FilterText(out));
    return out.getList();
  }

  /** Return an Association between a node and its text content. */
  public static Association getTextAssociation(ActiveNode n, boolean caseSens) {
    String str = getTextString(n);
    if(!caseSens) {
      str = str.toLowerCase();
    }
    return Association.associate(n, str);
  }

  /** Return a list of text Associations.  Splits text nodes containing
   *	whitespace, but associates non-text markup with its concatenated text
   *	content.  Most useful for sorting nodes lexically.
   */
  public static List getTextList(ActiveNodeList nl, boolean caseSens) {
    List l = new List();
    Enumeration items = ListUtil.getListItems(nl);
    while (items.hasMoreElements()) {
      Association a = getTextAssociation((ActiveNode)items.nextElement(),
					 caseSens);
      if (a != null) l.push(a);
    }
    return l;
  }

  /** Convert a NodeList to a String in <em>internal</em> form.
   *	Character entities are replaced by their equivalent characters;
   *	all other markup is <em>also</em> converted to equivalent characters. 
   */
  public static String getCharData(ActiveNodeList nl) {
    ToCharData out = new ToCharData();
    Copy.copyNodes(nl, new FilterText(out));
    return out.getString();
  }

  /** Convert a NodeList to a String in <em>internal</em> form and trim it.
   *	Character entities are replaced by their equivalent characters;
   *	all other markup is <em>also</em> converted to equivalent characters. 
   *    Return null if the NodeList passed is null.
   */
  public static String trimCharData(ActiveNodeList nl) {
    if (nl == null) return null;
    ToCharData out = new ToCharData();
    Copy.copyNodes(nl, new FilterText(out));
    return out.getString().trim();
  }

  /** Convert a NodeList to a String in <em>external</em> form.
   *    Return null if the NodeList passed is null.
   */
  public static String toExternalForm(ActiveNodeList nl) {
    if (nl == null) return null;
    ToString out = new ToString();
    Copy.copyNodes(nl, out);
    return out.getString();
  }

  /************************************************************************
  ** Trimming and padding:
  ************************************************************************/

  /** Trim leading blank spaces from a String. */
  public static final String trimLeading(String s) {
    s += '.';
    s = s.trim();
    return s.substring(0, s.length()-1);
  }

  /** Trim the trailing blanks from a String. */
  public static final String trimTrailing(String s) {
    s = "." + s;
    s = s.trim();
    return s.substring(1);
  }

  /** Trim leading and trailing whitespace.  Return an 
    * enumeration of nodes with whitespace removed.
    */
  public static Enumeration trimListItems(ActiveNodeList nl) {
    List results = new List();
    String rStr;

    int startIndex = -1;
    int endIndex = -1;
    
    // Get first non-whitespace text node
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode n = nl.activeItem(i);
      if(NodeType.isText(n)) {
	ActiveText t = (ActiveText)n;
	if (t.getIsWhitespace()) continue;
	startIndex = i;
	break;
      } else {
	startIndex = i;
	break;
      }
    }

    // Get larst non-whitespace text node
    for (int i = len-1; i >= 0; --i) {
      ActiveNode n = nl.activeItem(i);
      if(NodeType.isText(n)) {
	ActiveText t = (ActiveText)n;
	if (t.getIsWhitespace()) continue;
	endIndex = i;
	break;
      } else {
	endIndex = i;
	break;
      }
    }

    for (int i = startIndex; i <= endIndex; ++i) {
      ActiveNode n = nl.activeItem(i);
      if (n == null) continue;
      if(NodeType.isText(n)) {
	ActiveText tNode = (ActiveText)n;
	if (i == startIndex) {
	  if (i == endIndex) {
	    results.push(new TreeText(tNode.getData().trim()));
	  } else {
	    results.push(new TreeText(trimLeading(tNode.getData())));
	  }
	} else if (i == endIndex) {
	  results.push(new TreeText(trimTrailing(tNode.getData())));
	} else {
	  // Push all other text nodes
	  results.push(tNode);
	}
      } else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results.elements();
  }


  public static ActiveNodeList trim(ActiveNodeList nl) {
    return new TreeNodeList(trimListItems(nl));
  }

  /** Pad the text to the specified width with the specified
    * alignment. The default is left, meaning that spaces are
    * added to the right.
    */
  public static Enumeration padListItems(ActiveNodeList nl, boolean align,
					 boolean left, boolean right,
					 boolean center, int width) {
    List results = new List();
    int strLen = 0;

    // Extract string length from each node and push node onto
    // results list.
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode n = nl.activeItem(i);
      String nStr = getTextString(n);
      strLen += nStr.length();
      results.push(n);
    }
    
    if(strLen < width) {
      // Pad amount specified
      int padLen = (width - strLen);
      // Create a node full of spaces
      TreeText pNode = null;
      if(right) {
	pNode = createPadNode(padLen);
	results.insertAt(pNode, 0);
      }
      else if(center) {
	int extraSpace = 0;
	if((padLen % 2) != 0)
	  extraSpace = 1;
	padLen = padLen / 2;
	// Add new node of spaces to end of list
	pNode = createPadNode(padLen + extraSpace);
	results.insertAt(pNode, results.nItems());
	// Add new node of spaces to front of list
	pNode = createPadNode(padLen);
	results.insertAt(pNode, 0);

      }
      else {
	pNode = createPadNode(padLen);
	results.insertAt(pNode, results.nItems());
      }
    }
    return results.elements();
  }

  public static final TreeText createPadNode(int width) {
    
    String nodeStr = "";
    for(int i = 0; i < width; i++) {
      nodeStr += " ";
    }
    TreeText ptt = new TreeText(nodeStr);
    return ptt;
  }
  

  /************************************************************************
  ** Inserting and deleting markup:
  ************************************************************************/

  /** Protect markup in a string by converting &lt;, &gt;, and &amp; to
   *	the corresponding entities.  The string remains a string.
   * === should handle &amp;#... character entities!
   */
  public static final String protectMarkup(String s) {
    if (s == null || s.length() < 1) return s;
    String n = "";
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '&') n += "&amp;";
      else if (s.charAt(i) == '>') n += "&gt;";
      else if (s.charAt(i) == '<') n += "&lt;";
      else n += s.charAt(i);
    }
    return n;    
  }

  /** === There should be a corresponding insertCharacterEntities that actually
   *	outputs a NodeList.
   */

  /** Replace character entities in a NodeList with their values. */
  public static final String expandCharacterEntities(ActiveNodeList nl) {
    ToString out = new ToString(charEnts);
    Copy.copyNodes(nl, out);
    return out.getString();
  }

  /** Add markup to a String, using commonly-accepted text
   *	conventions.  Things that look like tags are boldfaced; things
   *	that look like attributes are italicized, and so on.
   *
   *	The string remains a string.
   */
  public static final String addMarkup(String s) {

    // === at some point addMarkup needs to be parametrized ===

    if (s == null || s.length() < 1) return "";
    String n = "";
    boolean inUC = false;	// inside string of uppercase chars.
    boolean inIT = false;	// inside italics  (_..._)
    boolean inBF = false;	// inside boldface (*...*)
    boolean inAN = false;	// inside attr. name
    boolean inTAG = false;	// inside tag
    boolean inAV = false;	// inside attr. value
    char AVend = 0;		// quote for attr. value
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      char nc = (i+1 >=  s.length())? 0 : s.charAt(i+1);
      if (c == '&') n += "&amp;";
      else if (c == '>') n += "&gt;";
      else if (c == '<') {
	n += "&lt;";
	if (isIDchar(nc) || nc == '/') {
	  inTAG = true;
	  n += "<b>";
	} 
      } else if (inTAG && (isIDchar(c) || c == '/')) {
	n += c;
	if (! isIDchar(nc)) {
	  inTAG = false;
	  n += "</b>";
	}
      }
      else if (Character.isUpperCase(c) && ! inUC &&
	       Character.isUpperCase(nc)) {
	inUC = true;
	n += "<tt>";
	n += Character.toLowerCase(c);
      } else if (Character.isUpperCase(c) && inUC) {
	n += Character.toLowerCase(c);
	if (! Character.isUpperCase(nc)) {
	  n += "</tt>";
	  inUC = false;
	}
      }
      else if (c == '=' && isIDchar(nc)) {
	inAV = true;
	AVend = 0;
	n += c;
	n += "<i>";
      }
      else if (c == '=' && (nc == '\'' || nc == '"')) {
	inAV = true;
	AVend = nc;
	n += c;
	n += "<i>";
	n += nc;
	i ++;
      }
      else if (inAV) {
	n += c;
	if (AVend != 0 && nc == AVend) {
	  inAV = false;
	  n += nc;
	  n += "</i>";
	  i ++;
	} else if (AVend == 0 && ! isIDchar(nc)) {
	  inAV = false;
	  n += "</i>";
	}
      }
      else n += s.charAt(i);
    }
    return n;
  }

  public static boolean isIDchar(char c) {
    return (Character.isLetterOrDigit(c) || c == '-' || c == '.');
  }


  /** Add markup to a String, using commonly-accepted text
   *	conventions.  Things that look like tags are boldfaced; things
   *	that look like attributes are italicized, and so on.
   *
   *	Markup is sent to an Output. === should produce real nodes ===
   */
  public static final void addMarkup(String s, Output out) {
    out.putNode(new TreeText(addMarkup(s)));
  }


  /************************************************************************
  ** Encoding/Decoding
  ************************************************************************/


  /** Find each occurrence of encoded html special characters
    * in a text string and replace with their text
    * equivalents.
    * @param text The string contain the encoded character(s).
    */
  public static String decodeEntity(String text) {

    String d     = subEntity("&amp;", text);
    String dd    = subEntity("&lt;", d);
    String ddd   = subEntity("&gt;", dd);
    String dddd  = subEntity("&apos;", ddd);
    String ddddd = subEntity("&quot;", dddd);
    return ddddd; 
  }

  /** Loop through string and replace each occurrence of
      markup entity with its ascii equivalent.  For example,
      replace &lt; with <.
      @param ent The entity to be replaced.
      @param text The string containing the entity.
  */ 
  protected static String subEntity(String ent, String text){
    int spos=0;
    int epos=0;
    int fpos=0;
    boolean found = false;
    StringBuffer sb = new StringBuffer();
    
    fpos = text.indexOf(ent);

    while( fpos != -1 ){
      found = true;
      // System.out.println("spos is-->"+Integer.toString(spos));
      // System.out.println("fpos is-->"+Integer.toString(fpos));
      // System.out.println("The substring is-->"+text.substring(spos, fpos));
      sb.append(text.substring(spos, fpos));
      spos = fpos + ent.length();
      sb.append((String)predefEntityTab.get(ent));
      fpos = text.indexOf( ent, fpos+1 );
    }
    if( !found ) 
      return text;
    
    if( spos != 0 )
      sb.append(text.substring(spos, text.length()));
    
    return new String( sb );
  }

  /** Lookup table for html entities and their ascii equivalents */
  protected static Hashtable predefEntityTab;

  static{
    predefEntityTab = new Hashtable();
    predefEntityTab.put("&lt;", "<");
    predefEntityTab.put("&gt;", ">");
    predefEntityTab.put("&amp;", "&");
    predefEntityTab.put("&apos;", "'");
    predefEntityTab.put("&quot;", "\"");
  }


  /** Encode text node strings using URL encoding. */
  public static List encodeURLListItems(ActiveNodeList nl) {
    List results = new List();
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
	//      ActiveNode n = nl.activeItem(i);
      ActiveNode n = (nl.activeItem(i)).shallowCopy();
      if(NodeType.isText(n)) {
	ActiveText tNode = (ActiveText)n;
	String bStr = URLEncoder.encode(tNode.toString());
	tNode.setData(bStr);
	results.push(tNode);
      }
      else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results;
  }

  /** Encode text node contents in base64 */
  public static List encodeBase64ListItems(ActiveNodeList nl) {
    List results = new List();
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
	//      ActiveNode n = nl.activeItem(i);
      ActiveNode n = (nl.activeItem(i)).shallowCopy();
      if(NodeType.isText(n)) {
	ActiveText tNode = (ActiveText)n;
	String s = tNode.getNodeValue();
	String bStr = Utilities.encodeBase64(s.getBytes());
	tNode.setData(bStr);
	results.push(tNode);
      }
      else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results;
  }

  /** Returns an List of string tokens which include text plus tokens for
    * markup characters as well as any accompanying text.
    */
  public static List encodeEntityListItems(ActiveNodeList nl,
					   String markupChars) {
    List tokenList = new List();
    List resultList = new List();
    int len = nl.getLength();
    for (int i = 0; i < len; ++i) {
	//ActiveNode n = nl.activeItem(i);
      ActiveNode n = (nl.activeItem(i)).shallowCopy();
      if (NodeType.isText(n)) {
	ActiveText tNode = (ActiveText)n;
	String s = tNode.getNodeValue(); // === internal/external form?

	// true means return delimiters as tokens
	tokenList.append(List.split(s, markupChars, true));
      }
      else {
	  // push other node type unchanged.  Not clear what to do
	  // for non-string elements
	  //tokenList.push(n);
      }
    }
    // Convert each string token to the correct node type
    Enumeration tlEnum = tokenList.elements();
    while(tlEnum.hasMoreElements()) {
      String tmpStr = (String)tlEnum.nextElement();
      // Get token type
      if(markupChars.indexOf(tmpStr) == -1) {
	// Not a markup token
	TreeText ptt = new TreeText(tmpStr);
	resultList.push(ptt);
      } else {
	// Markup token:  do a table lookup to get encoding
	String tStr = (String)TextUtil.encodeEntityTab.get(tmpStr);
	TreeEntity pte = new TreeEntity(tStr);
	resultList.push(pte);
      }
    }
    return resultList;
  }


  /** Lookup table for text characters that are encoded as entities.
      ParseTreeEntity adds &; to encoding.
  */
  public static Hashtable encodeEntityTab;

  static{
    encodeEntityTab = new Hashtable();
    encodeEntityTab.put("<", "lt");
    encodeEntityTab.put(">", "gt");
    encodeEntityTab.put("&", "amp");
    encodeEntityTab.put("'", "apos");
    encodeEntityTab.put("\"", "quot");
  }
}
