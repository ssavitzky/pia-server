////// extractHandler.java: <extract> Handler implementation
//	$Id: extractHandler.java,v 1.9 1999-04-13 21:17:15 steve Exp $

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


package org.risource.dps.handle;

import org.w3c.dom.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.tree.TreeEntity;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeNodeList;

import org.risource.ds.Association;
import org.risource.ds.List;
import org.risource.ds.SortTree;

import JP.ac.osaka_u.ender.util.regex.RegExp;
import JP.ac.osaka_u.ender.util.regex.MatchInfo;

import java.util.Enumeration;

/**
 * Handler for &lt;extract&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: extractHandler.java,v 1.9 1999-04-13 21:17:15 steve Exp $
 * @author steve@rsv.ricoh.com
 */
public class extractHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;extract&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    String tag = in.getTagName();
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String sep = (atts == null)? null : atts.getAttribute("sep");

    BasicEntityTable ents = new BasicEntityTable(tag);
    ToNodeList collect = new ToNodeList();
    ActiveNodeList currentSet = null;
    boolean terminateExtract = false;

    // === strictly speaking, this should use the tagset to create the entity.
    TreeEntity extracted = new TreeEntity("list");
    ents.setBinding("list", extracted);
    Processor process = cxt.subProcess(in, collect, ents);

    for (Node item = in.toFirstChild();
	 item != null;
	 item = in.toNextSibling()) {
      if (terminateExtract) continue;

      switch (item.getNodeType()) {
      case Node.COMMENT_NODE:
      case Node.PROCESSING_INSTRUCTION_NODE:
      case Node.DOCUMENT_TYPE_NODE:
	break;

      case Node.TEXT_NODE:
	ActiveText t = (ActiveText)in.getActive();
	if (! t.getIsWhitespace()) {
	  Enumeration items = ListUtil.getTextItems(t.getData());

	  while (items.hasMoreElements()) {
	    currentSet = extractTextItem(items.nextElement().toString(),
					extracted.getValueNodes(cxt));
	    if (currentSet.getLength() == 0) terminateExtract = true;
	    extracted.setValueNodes(currentSet);
	  }

	  // === really ought to have something in the handler that tells the
	  // === parser whether to split text content into words or not.

	}
	break;

      default:
	process.processNode();
	currentSet = collect.getList();
	if (currentSet.getLength() == 0) terminateExtract = true;
	extracted.setValueNodes(currentSet);
	collect.clearList();
      }
    }
    in.toParent();

    putList(out, extracted.getValueNodes(cxt), sep);
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Returns the current set of extracted nodes.  
   *	Works by looking up the appropriate entity in the given context.
   *	Used by sub-element handlers.
   */
  public ActiveNodeList getExtracted(Context cxt) {
    return cxt.getEntityValue("list", true);
  }

  /** Handle a numeric item. */
  public ActiveNodeList  extractNumericItem(int n,
					    ActiveNodeList extracted) {
    List items = new List(ListUtil.getListItems(extracted));
    ActiveNode item = null;
    if (n >= 0) {
      item = (ActiveNode) items.at(n);
    } else {
      item = (ActiveNode) items.at(items.nItems() + n);
    }
    return new TreeNodeList(item);
  }

  /** Handle a text item (either name, type, or number). */
  public ActiveNodeList  extractTextItem(String item,
					 ActiveNodeList extracted) {
    Association n = MathUtil.getNumeric(item);
    if (n != null) return extractNumericItem((int)n.longValue(), extracted);
    else if (item.startsWith("#")) return extractTypeItem(item, extracted);
    else return extractNameItem(item, extracted);
  }

  public void extractByType(String item, ActiveNodeList extracted, Output out) {
    if (item.startsWith("#")) item = item.substring(1);
    int nodeType = NodeType.getType(item);
    if (nodeType == NodeType.ALL) putList(out, extracted);

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode node = extracted.activeItem(i);
      if (node.getNodeType() == nodeType) out.putNode(node);
    }
  }

  public ActiveNodeList extractTypeItem(String tname,
					ActiveNodeList extracted) {
    ToNodeList out = new ToNodeList();
    extractByType(tname, extracted, out);
    return out.getList();
  }

  public ActiveNodeList extractNameItem(String name, ActiveNodeList extracted) {
    ToNodeList out = new ToNodeList();
    extractByName(name, extracted, out);
    return out.getList();
  }

  /** Extract items by name and put them to an Output. */
  public void extractByName(String name, ActiveNodeList extracted, Output out) {
    if (extracted == null) return;
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode node = extracted.activeItem(i);
      String nn = getNodeName(node, true);
      if (nn != null && nn.equals(name)) out.putNode(node);
    }
  }

  /** Return a node's name, possibly forcing it to lowercase. */
  public String getNodeName(Node node, boolean caseSens) {
    String name = node.getNodeName();
    if (name != null && !caseSens) name = name.toLowerCase();
    return name;
  }

  /** Return a node's ID (name or id attribute). */
  public String getNodeId(Node node, boolean caseSens) {
    String name = null;
    if (node instanceof ActiveElement) {
      ActiveElement elt = (ActiveElement)node;
      name = elt.getAttribute("id");
      if (name == null) name = elt.getAttribute("name");
    } else {
      name = node.getNodeName();
    }
    if (name != null && !caseSens) name = name.toLowerCase();
    return name;
  }

  /** Return a key string from a node. */
  public String getNodeKey(Node item, boolean caseSens, String sep) {
    String key = null;
    if (!caseSens && sep != null) sep = sep.toLowerCase();
    if (item.getNodeType() == Node.ATTRIBUTE_NODE) {
      key = item.getNodeName();
    } else if (item.getNodeType() == Node.ENTITY_NODE) {
      key = item.getNodeName();
    } else if (item.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
      key = item.getNodeName();
    } else if (item.getNodeType() == Node.TEXT_NODE) {
      ActiveText t = (ActiveText)item;
      key = (t.getIsIgnorable())? null : t.getData();
    } else {
      key = TextUtil.getTextString((ActiveNode)item);
      if (key != null) key = key.trim();
    }
    if (!caseSens && key != null) key = key.toLowerCase();
    if (key != null) key = key.trim();
    if (sep != null && key != null) {
      if (key.indexOf(sep)>=0) {
	key = key.substring(0, key.indexOf(sep));
      } else {
	key = null;
      }
    }
    return key;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public extractHandler() {
    /* Expansion control: */
    expandContent = false;	// true		Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  extractHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /** This constructor is used by sub-elements to control their syntax. */
  extractHandler(boolean expand, boolean text) {
    /* Expansion control: */
    expandContent = expand;
    textContent = text;

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}


/************************************************************************
** Sub-Elements:
***********************************************************************/

/**
 * Parent Handler for &lt;extract&gt; sub-elements.   <p>
 *
 *	Sub-element handlers do not inherit directly from
 *	<code>extractHandler</code> because essentially all (perhaps all)
 *	sub-elements will want to use the five-argument <code>action</code>
 *	method.  <code>extractHandler</code> itself does not, however, so we
 *	need to restore the three-argument action to its original
 *	functionality.
 */
class extract_subHandler extends extractHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Restore the default action. */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  public extract_subHandler() { this(true, false); }

  /** This constructor is used by sub-elements to control their syntax. */
  extract_subHandler(boolean expand, boolean text) {
    /* Expansion control: */
    expandContent = expand;
    textContent = text;

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

/** &lt;from&gt; simply sets the current set to its (expanded) content. */
class fromHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    content = new TreeNodeList(ListUtil.getListItems(content));
    putList(out, content);
  }
  fromHandler() { super(true, false); }
}

class inHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    Enumeration items = ListUtil.getListItems(content);
    while (items.hasMoreElements()) {
      Node item = (Node) items.nextElement();
      if (item.getNodeType() == Node.TEXT_NODE) {
        Node binding = aContext.getEntityBinding(item.toString(), false);
	if (binding != null) out.putNode(binding);
      } else {
	out.putNode(item);
      }
    }
  }

  /** Content is text only, because all we want is the name */
  inHandler() { super(true, true); } // text only
}

/** &lt;child&gt;<em>n</em>&lt;/&gt; extracts the <em>n</em>th child
 *	of each extracted node. */
class childHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      ++termsHandled;
      String term = terms.nextElement().toString();
      for (int i = 0; i < len; ++i) {
	ActiveNode item = extracted.activeItem(i);
	if (item.hasChildNodes()) {
	  putList(out, extractTextItem(term, item.getContent()));
	}
      }
    }
    if (termsHandled == 0) {
      // no terms.  Return all the children.
      for (int i = 0; i < len; ++i) {
	ActiveNode item = extracted.activeItem(i);
	if (item.hasChildNodes()) {
	  putList(out, item.getContent());
	}
      }
    }
  }
  childHandler() { super(true, false); }
}

/** &lt;nodes&gt;<em>term</em>*&lt;/&gt; extracts each node matching a term. */
class nodesHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      String term = terms.nextElement().toString();
      putList(out, extractTextItem(term, extracted));
    }
  }
  nodesHandler() { super(true, false); }
}

/** &lt;name&gt;<em>n</em>&lt;/&gt; extracts every node with a matching name.
 */
class nameHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    String name = content.toString();
    if (name != null) name = name.trim();
    if (name == null) {
      extractNames(extracted, out); 
    } else if (name.startsWith("#")) {
      extractByType(name, extracted, out);
    } else extractByName(name, extracted, out);
  }
  nameHandler() { super(true, false); }
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new name_recursive();
    if (dispatch(e, "all")) 	 	return new name_recursive();
    return this;
  }
  protected void extractNames(ActiveNodeList extracted, Output out) {
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode node = extracted.activeItem(i);
      String nname = node.getNodeName();
      if (!nname.startsWith("#")) {
	out.putNode(new TreeText(node.getNodeName()));
      }
    }
  }
}

/** &lt;name recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching name; recurses into content.
 */
class name_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String key = content.toString();
    if (key != null) key = key.trim();
    if (key != null && !caseSens) key = key.toLowerCase();
    if (key != null && key.startsWith("#")) {
      int ntype = NodeType.getType(key.substring(1));
      extractByType(extracted, ntype, all, out);
    } else {
      extract(extracted, key, caseSens, all, out);
    }
  }
  protected void extract(ActiveNodeList extracted, String key, 
			boolean caseSens, boolean all, Output out) {
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String name = getNodeName(item, caseSens);
      if (key == null || (name != null && key.equals(name))) {
	out.putNode(item);
      } else if (!all && item.hasChildNodes()) {
	extract(item.getContent(), key, caseSens, all, out);
      }
      if (all && item.hasChildNodes()) {
	extract(item.getContent(), key, caseSens, all, out);
      }
    }
  }
  protected void extractByType(ActiveNodeList extracted, int ntype,
			       boolean all, Output out) {
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (ntype == NodeType.ALL || ntype == item.getNodeType()) {
	out.putNode(item);
      } else if (!all && item.hasChildNodes()) {
	extractByType(item.getContent(), ntype, all, out);
      }
      if (all && item.hasChildNodes()) {
	extractByType(item.getContent(), ntype, all, out);
      }
    }
  }
  name_recursive() { super(true, false); }
}

/** &lt;key&gt;<em>k</em>&lt;/&gt; extracts every node with a matching key. */
class keyHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String sep = atts.getAttribute("sep");
    String key = content.toString();
    if (key != null) key = key.trim();
    if (key != null && !caseSens) key = key.toLowerCase();

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String k = getNodeKey(item, caseSens, sep);
      if (key == null || key.equals(k))	out.putNode(item);
    }    
  }
  keyHandler() { super(true, true); } // text only
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new key_recursive();
    if (dispatch(e, "all")) 	 	return new key_recursive();
    return this;
  }
}

/** &lt;key recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching key; recurses into content.
 */
class key_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String  sep = atts.getAttribute("sep");
    boolean all = atts.hasTrueAttribute("all");
    String  key = content.toString();
    if (key != null) key = key.trim();
    if (key != null && !caseSens) key = key.toLowerCase();
    extract(extracted, key, caseSens, sep, all, out);
  }
  protected void extract(ActiveNodeList extracted, String key, boolean caseSens,
			String sep, boolean all, Output out) {
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String k = getNodeKey(item, caseSens, sep);
      if (key == null || (k != null && key.equals(k))) {
	out.putNode(item);
      } else if (!all && item.hasChildNodes()) {
	extract(item.getContent(), key, caseSens, sep, all, out);
      }
      if (all && item.hasChildNodes()) {
	extract(item.getContent(), key, caseSens, sep, all, out);
      }
    }
  }
  key_recursive() { super(true, false); }
}

/** &lt;id&gt;<em>k</em>&lt;/&gt; extracts every node with a matching id. */
class idHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String id = content.toString();
    if (id != null) id = id.trim();
    if (id != null && !caseSens) id = id.toLowerCase();

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String k = getNodeId(item, caseSens);
      if (id == null || id.equals(k))	out.putNode(item);
    }    
  }
  idHandler() { super(true, true); } // text only
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new id_recursive();
    if (dispatch(e, "all")) 	 	return new id_recursive();
    return this;
  }
}

/** &lt;id recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching id; recurses into content.
 */
class id_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String  id = content.toString();
    if (id != null) id = id.trim();
    if (id != null && !caseSens) id = id.toLowerCase();
    extract(extracted, id, caseSens, all, out);
  }
  protected void extract(ActiveNodeList extracted, String id, boolean caseSens,
			boolean all, Output out) {
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String k = getNodeId(item, caseSens);
      if (id == null || (k != null && id.equals(k))) {
	out.putNode(item);
      } else if (!all && item.hasChildNodes()) {
	extract(item.getContent(), id, caseSens, all, out);
      }
      if (all && item.hasChildNodes()) {
	extract(item.getContent(), id, caseSens, all, out);
      }
    }
  }
  id_recursive() { super(true, false); }
}

/** &lt;attr&gt;<em>n</em>&lt;/&gt; extracts every Attribute with matching name.
 */
class attrHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String name = content.toString();
    if (name != null) name = name.trim();
    
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (item.getNodeType() == Node.ATTRIBUTE_NODE) {
	Attr n = (Attr)item;
	if (name == null
	    || (caseSens && name.equals(n.getName()))
	    || (!caseSens && name.equalsIgnoreCase(n.getName())))
	  out.putNode(item);
      } else if (item.getNodeType() == Node.ELEMENT_NODE) {
	ActiveElement e = (ActiveElement)item;
	ActiveAttrList atl = e.getAttrList();
	ActiveAttr a = (atl == null)? null : atl.getActiveAttr(name);
	if (a != null) out.putNode(a);
      }
    }    
  }
  attrHandler() { super(true, true); } // text only.
}

/** &lt;match&gt;<em>re</em>&lt;/&gt; extracts every node matching re. */
class matchHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String match = content.toString();
    // === WARNING: match is not being trimmed! ===
    if (!caseSens) match = match.toLowerCase();

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      String k = getNodeKey(item, caseSens, null);
      try {
	RegExp re = new RegExp(match);
	MatchInfo mi = re.match(k);
	if (mi != null && mi.end() >= 0) out.putNode(item);
      } catch (Exception ex) {
	// === ii.error(ia, "Exception in regexp: "+ex.toString());
      }
    }
  }
  matchHandler() { super(true, true); }
}

/** &lt;xptr&gt;<em>xp</em>&lt;/&gt; extracts using an XPointer. */
class xptrHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    unimplemented(in, aContext);		// === xptr
  }
  xptrHandler() { super(true, true); }
}

/** &lt;parent&gt; extracts every node's parent. */
class parentHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    Node previous = null;

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      Node p = item.getParentNode();
      if (p != null && p != previous ) {
	out.putNode(p);
	previous = p;
      }
    }
  }
  parentHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;content&gt; extracts every node's content (children). */
class contentHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (item.hasChildNodes()) { putList(out, item.getContent()); }
    }
  }
  contentHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;next&gt; extracts every node's successor. */
class nextHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      Node n = item.getNextSibling();
      if (n != null) out.putNode(n);
    }
  }
  nextHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;prev&gt; extracts every node's predecessor. */
class prevHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      Node n = item.getPreviousSibling();
      if (n != null) out.putNode(n);
    }
  }
  prevHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;eval&gt; extracts every node's value. */
class evalHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      ActiveNodeList v = null;	
      switch (item.getNodeType()) {
      case Node.ATTRIBUTE_NODE:
      case Node.ENTITY_NODE:
      case Node.ENTITY_REFERENCE_NODE:
	v = item.getValueNodes(aContext);
	if (v != null) putList(out, v);
	break;

      case Node.ELEMENT_NODE:
      case Node.TEXT_NODE:
      case Node.CDATA_SECTION_NODE:
	out.putNode(item);
	break;
      }
    }
  }
  evalHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;replace&gt; replace every node's value or content. */
class replaceHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    String name = atts.getAttribute("name");
    boolean caseSens = atts.hasTrueAttribute("case");

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      switch (item.getNodeType()) {
      case Node.ATTRIBUTE_NODE:
	if (name != null 
	     && !(caseSens && name.equals(item.getNodeName())
		  || !caseSens && name.equalsIgnoreCase(item.getNodeName())))
	  continue;
	((ActiveAttr) item).setValueNodes(aContext, content);
	break;

      case Node.ENTITY_NODE:
      case Node.ENTITY_REFERENCE_NODE:
	if (name != null 
	     && !(caseSens && name.equals(item.getNodeName())
		  || !caseSens && name.equalsIgnoreCase(item.getNodeName())))
	  continue;
	((ActiveEntity) item).setValueNodes(aContext, content);
	break;

      case Node.ELEMENT_NODE:
	ActiveElement elt = (ActiveElement)item;
	if (name != null) {
	    elt.setAttributeValue(name, content);
	  
	} else {
	  for (Node child = item.getFirstChild(); child != null; 
	       child = item.getFirstChild()) {
	    try { item.removeChild(child); } catch (Exception e) {}
	  }
	  Copy.appendNodes(content, item);
	  // putList(out, extracted);
	  // === attributes in content should become attributes of a ===

	}
	break;
      }
    }
  }
    replaceHandler() { super(true, false); }
}

/** &lt;remove&gt; remove every extracted node.
 * <p> === unreliable until new DOM implementation in place.
 */
class removeHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      Node parent = item.getParentNode();
      if (parent != null)
	try { parent.removeChild(item); } catch (Exception e) {}
    }
  }
  removeHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;unique&gt; remove every duplicate text extracted node.
 * <p> === unreliable until new DOM implementation in place.
 */
class uniqueHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    List textAssoc = TextUtil.getTextList(extracted, false);
    Enumeration textAssocEnum = textAssoc.elements();

    SortTree sorter = new SortTree();
    Association a;
    
    while(textAssocEnum.hasMoreElements()) {
      a = (Association)textAssocEnum.nextElement();
      sorter.insert(a, false);
    }

    List tmpList = new List();
    List resultList = new List();
    sorter.ascendingValues(tmpList);
    String prev;
    String cur;
    for(int i = 0; i < tmpList.nItems(); i++) {
	if(i == 0) {
	    resultList.push(tmpList.at(i));
	}
	else {
	    cur = tmpList.at(i).toString();
	    prev = tmpList.at(i - 1).toString();
	    if(cur.equals(prev)) {
		;
	    }
	    else {
		resultList.push(tmpList.at(i));
	    }
	}  
    }
    ActiveNodeList selList = ListUtil.toNodeList(resultList);
    putList(out, selList);
    
    // putEnum(out, resultList.elements());
  }
  uniqueHandler() { super(true, false); syntaxCode = EMPTY; }
}


/** &lt;append&gt; Append nodes to extraction or children of extraction.
 * <p> === unreliable until new DOM implementation in place.
 */
class appendHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    boolean children = atts.hasTrueAttribute("children");

    ActiveNodeList extracted = getExtracted(aContext);
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (children) {
	  if (NodeType.hasContent(item))
	    Copy.appendNodes(content, item);	
	  // out.putNode(parent);
      } else {
	ActiveNode parent = null;
	ActiveNode p = (ActiveNode)item.getParentNode();
	  if (p != null && p != parent) {
	    parent = p;
	    Copy.appendNodes(content, parent);
	  }
	  // putList(out, extracted);
	  // putList(out, content);
      }
    }
  }
  appendHandler() { super(true, false); }
}


/** &lt;insert&gt; Insert nodes into the content of extracted nodes.
 *	The <code>child</code> attribute contains a number thatt becomes 
 *	the index of the inserted node.  Hence 0 inserts a new first node; 
 *	-1 inserts a new last node.  
 */
class insertHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    int child = MathUtil.getInt(atts, "child", -1);
    ActiveNodeList extracted = getExtracted(aContext);

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (!NodeType.hasContent(item)) continue;
      ActiveNodeList children = item.getContent();
      int nchildren = (children == null) ? 0 : children.getLength();
      if (child == -1 || nchildren == 0) {
	// -1 always inserts at the end;
	// just append if there are no children present to begin with.
	Copy.appendNodes(content, item);	
      } else if (child >= 0 && child >= nchildren) {
	// >= 0 but off the end: just append.
	Copy.appendNodes(content, item);	
      } else {
	int n = (child >= 0)? child : nchildren + 1 + child;
	if (n < 0) n = 0;
	ActiveNode next = children.activeItem(n);
	int clen = content.getLength();
	for (int j = 0; j < clen; j++) {
	  item.insertBefore(content.item(j), next);
	}
      }
    }
  }
  insertHandler() { super(true, false); }
}

