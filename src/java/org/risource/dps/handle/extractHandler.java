////// extractHandler.java: <extract> Handler implementation
//	$Id: extractHandler.java,v 1.26 2001-04-03 00:04:24 steve Exp $

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

import org.w3c.dom.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.ToNodeList;
import org.risource.dps.input.FromNodeList;
import org.risource.dps.tree.TreeEntity;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.namespace.BasicEntityTable;

import org.risource.ds.Association;
import org.risource.ds.List;
import org.risource.ds.SortTree;

import JP.ac.osaka_u.ender.util.regex.RegExp;
import JP.ac.osaka_u.ender.util.regex.MatchInfo;

import java.util.Enumeration;

/**
 * Handler for &lt;extract&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: extractHandler.java,v 1.26 2001-04-03 00:04:24 steve Exp $
 * @author steve@rii.ricoh.com
 */
public class extractHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;extract&gt; node. */
  public void action(Input in, Context cxt, Output out) {

    if (! in.toFirstChild()) return;

    // === strictly speaking, this should use the tagset to create the entity.
    String tag = in.getTagName();

    BasicEntityTable ents = new BasicEntityTable(tag);

    // Add a new entity for "list" -- the current list of extracted items.
    TreeEntity extracted = new TreeEntity("list");
    ents.setBinding("list", extracted);

    // Add a template entity for tm-like
    TreeEntity template = new TreeEntity("likelist");
    ents.setBinding("likelist", template);
    
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String sep = (atts == null)? null : atts.getAttribute("sep");

    //    extractLoop( extracted, in, cxt, out, atts, sep, ents);
    extractLoop( extracted, in, cxt, atts, sep, ents);

    in.toParent();

    putList(out, extracted.getValueNodes(cxt), sep);
  }

  /* Main loop of extract.
   *	Takes an already-initialized entity.
   */
  protected void extractLoop(TreeEntity extracted, Input in, 
			     Context cxt,  ActiveAttrList atts,
			     String sep, BasicEntityTable ents) {

     // This used to be in "action", but has been moved out for code sharing.

      //    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
      //    String sep = (atts == null)? null : atts.getAttribute("sep");

    // BasicEntityTable ents = new BasicEntityTable(tag);
    ToNodeList collect = new ToNodeList(cxt.getTopContext().getTagset());

    // default empty input set
    ActiveNodeList currentSet = null;
    boolean terminateExtract = false;

    Processor process = cxt.subProcess(in, collect, ents);


    // cut  here
    do {
      ActiveNode item = in.getActive();
      if (item == null) break;

      // if (terminateExtract) continue; === doesn't terminate properly ===
      // === exits without properly eating the rest of the input. 

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
    } while (in.toNext());

    
  }




  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Returns the current set of extracted nodes.  
   *	Works by looking up the appropriate entity in the given context.
   *	Used by sub-element handlers.
   */
  public ActiveNodeList getExtracted(Context cxt) {
    return cxt.getValueNodes("list", true);
  }

    /** new addition for tm_like tag */
  public ActiveNode getTemplateEntity(Context cxt) {
    return cxt.getBinding("likelist", true);
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
    ToNodeList out = new ToNodeList(null);
    extractByType(tname, extracted, out);
    return out.getList();
  }

  public ActiveNodeList extractNameItem(String name, ActiveNodeList extracted) {
    ToNodeList out = new ToNodeList(null);
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
      else if (node instanceof Namespace) {
	Namespace ns = (Namespace) node;
	ActiveNode n = ns.getBinding(name);
	if (n != null) out.putNode(n);
      }
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
	String name = item.getNodeValue().trim();
        ActiveNode binding = aContext.getBinding(name, false);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    String name = null;
    if (content != null) name = TextUtil.trimCharData(content);

    if (name == null || name.length() == 0) {
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String key = TextUtil.trimCharData(content);

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





/** &lt;sort&gt;<em>n</em>&lt;/&gt; extracts every node with a matching sort.
 */
class sortHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No extracted list: possibly not inside < extract >");
      return;
    }

    if ( content == null || content.toString().length() == 0 ) {
      reportError(in, aContext, "No key-search content: possibly not inside < sort >");
      return;
    }

    // decide if sort is numeric or not
    boolean numericSort = false;
    if (atts.hasTrueAttribute("numeric") ){
	numericSort = true;
    }
    boolean floatOnly = false;
    if (atts.hasTrueAttribute("floatonly") ){
	floatOnly = true;
	numericSort = true;
    }
    boolean intOnly = false;
    if (atts.hasTrueAttribute("intonly") ){
	intOnly = true;
	numericSort = true;
    }
    // decide on forward/reverse ordering
    boolean reverse = false;
    if (atts.hasTrueAttribute("reverse") ){
	reverse = true;
    }

    SortTree sorter = new SortTree();

    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
	// create an empty Output to store results of this key-search
	//	ToNodeList  keyResult = 	new 
	//  ToNodeList( aContext.getTopContext().getTagset() );

	// create a new Input (composed of the same old extract-style
	// content) for each separate key-search
	FromNodeList  fakeInput = 	new FromNodeList( content  );

	// set up entity tables 
	String tag = fakeInput.getTagName();
	BasicEntityTable fakeEnts = new BasicEntityTable(tag);

	// copy present item into the "current node list" (i.e. extracted)
	// Just like the list for the fakeInput, but this is a past
	// result rather than a future process
	ActiveNode exItem = extracted.activeItem(i).deepCopy();
	ActiveNodeList exItemList = new TreeNodeList();
	exItemList.append( exItem );

	//	TreeEntity fakeExtracted = new TreeEntity("list");
	TreeEntity fakeExtracted = new TreeEntity("list", exItemList );
	fakeEnts.setBinding("list", fakeExtracted);


	//exItemList.append( exItem );
	//fakeExtracted.append( exItem );

	//    content = new TreeNodeList(ListUtil.getListItems(content));
	//    putList(out, content);

	Context fakeCxt = aContext.newContext();
	ActiveAttrList fakeAtts = Expand.getExpandedAttrs(fakeInput,
							  fakeCxt);
	String sep = (fakeAtts == null)? null : 
	    fakeAtts.getAttribute("sep");

	extractLoop( fakeExtracted, (Input)fakeInput, 
		     fakeCxt, fakeAtts,  sep, fakeEnts);
	//     fakeCxt, (Output)keyResult, fakeAtts,  sep, fakeEnts);

	// take the "returned" (i.e. extracted) nodes, pull out text,
	// concatenate them into a single string, and associate that with
	// the node to be sorted.
	ActiveNodeList exList =  fakeExtracted.getValueNodes();


	Enumeration exEnum = ListUtil.getTextItems( exList ) ;


	String exString = "";

	// numericOnly means only keep numbers; strip all else
	Enumeration numList = MathUtil.getNumbers( exList );
	if (floatOnly || intOnly){
	    while( numList.hasMoreElements() ){
		Association testAssoc = (Association) numList.nextElement();
		String testStr = "";
		if (intOnly && testAssoc.isIntegral() )
		    testStr = MathUtil.numberToString(
						testAssoc.longValue(), 0);
		else if (intOnly){  // read float, truncate
		    testStr = MathUtil.numberToString(
					     testAssoc.doubleValue(), 0);
		}
		else{             // read float, keep
		    testStr = MathUtil.numberToString(
					     testAssoc.doubleValue(), -1);
		}
		exString += testStr;

	    }
	}
	else{
	    while( exEnum.hasMoreElements() ){
		exString += exEnum.nextElement().toString();
	    }
	}

	Association aa = Association.associate( exItem, exString, numericSort);
	sorter.insert(aa, false);
    }
    
    List resultList = new List();
    if(reverse) {
	sorter.descendingValues(resultList);
    }
    else
	sorter.ascendingValues(resultList);

    Enumeration resultEnum = resultList.elements();
    while (resultEnum.hasMoreElements() ){
	ActiveNode resultItem = (ActiveNode)resultEnum.nextElement();
	out.putNode( resultItem );
    }
  }
  sortHandler() { super(false, false); }
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //    if (dispatch(e, "recursive")) 	return new sort_recursive();
    //    if (dispatch(e, "all")) 	 	return new sort_recursive();
    return this;
  }
}

/** &lt;sort recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching sort; recurses into content.
 */
class sort_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    boolean all = atts.hasTrueAttribute("all");
    String key = TextUtil.trimCharData(content);

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
      String sort = getNodeName(item, caseSens);
      if (key == null || (sort != null && key.equals(sort))) {
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
  sort_recursive() { super(true, false); }
}




/** &lt;key&gt;<em>k</em>&lt;/&gt; extracts every node with a matching key. */
class keyHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    String sep = atts.getAttribute("sep");
    String key = TextUtil.trimCharData(content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    String  sep = atts.getAttribute("sep");
    boolean all = atts.hasTrueAttribute("all");
    String  key = TextUtil.trimCharData(content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    String id = TextUtil.trimCharData(content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    boolean all = atts.hasTrueAttribute("all");
    String  id = TextUtil.trimCharData(content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    String name = TextUtil.trimCharData(content);
    
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


/** &lt;has-attr&gt;<em>n</em>&lt;/&gt; extracts every Element with given attr.
 */ 
class hasAttrHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    String name = TextUtil.trimCharData(content);
    String value= atts.getAttribute("value");
    
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      if (item.getNodeType() == Node.ELEMENT_NODE) {
	ActiveElement e = (ActiveElement)item;
	if (value == null && e.hasTrueAttribute(name)) out.putNode(item);
	else if (value != null && value.equals(e.getAttribute(name))) 
	  out.putNode(item);
      }
    }    
  }
  hasAttrHandler() { super(true, true); } // text only.
}

/** &lt;match&gt;<em>re</em>&lt;/&gt; extracts every node matching re. */
class matchHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    String match = TextUtil.getCharData(content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    boolean caseSens = caseSensitive(atts);
    unimplemented(in, aContext);		// === xptr
  }
  xptrHandler() { super(true, true); }
}

/** &lt;parent&gt; extracts every node's parent. */
class parentHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, ActiveNodeList content) {
    ActiveNodeList extracted = getExtracted(aContext);
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }

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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    String name = atts.getAttribute("name");
    boolean caseSens = caseSensitive(atts);
    boolean wholeNode = atts.hasTrueAttribute("node");

    int len = extracted.getLength();

    if (wholeNode) {
      for (int i = 0; i < len; ++i) {
	ActiveNode item = extracted.activeItem(i);
	if (item.getParentNode() != null) {
	    for (int j = 0; j < content.getLength(); j++){
		item.getParentNode().insertBefore(content.item(j).cloneNode(true), item);
	    }
	    item.getParentNode().removeChild(item);
	    //item.getParentNode().replaceChild(item,  content.item(0));
	    out.putNode(item);
	} else {
	  putList(out, content);
	}
      }
      return;
    }

    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      out.putNode(item);
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
	((ActiveEntityRef) item).setValueNodes(aContext, content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside < extract >");
      return;
    }
    int len = extracted.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode item = extracted.activeItem(i);
      out.putNode(item);
      if (children) {
	  if (NodeType.hasContent(item))
	    Copy.appendNodes(content, item);	
      } else {
	ActiveNode parent = null;
	ActiveNode p = (ActiveNode)item.getParentNode();
	if (p != null && p != parent) {
	  parent = p;
	  Copy.appendNodes(content, parent);
	}
      }
    }
    if (!children) putList(out, content);
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
    if (extracted == null) {
      reportError(in, aContext, "No list: possibly not inside <extract>");
      return;
    }

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
      out.putNode(item);
    }
  }
  insertHandler() { super(true, false); }
}


/** &lt;from&gt; simply sets the current set to its (expanded) content. */
class tm_likeHandler extends extract_subHandler {
    protected void action(Input in, Context aContext, Output out, 
			  ActiveAttrList atts, ActiveNodeList content) {
	// default is keeping things like this, unless set otherwise
	boolean keepLike = true;
	if ( atts.hasTrueAttribute("nokeep") )
	     keepLike = false;

	ActiveNodeList likeContent = new TreeNodeList(ListUtil.getListItems(content));





        // load the content into the template entity
	TreeEntity  template = (TreeEntity) getTemplateEntity(aContext) ;
	template.setValueNodes( likeContent );
	ActiveNodeList templateList = (ActiveNodeList)template.getValueNodes();

	/*
	// try putting template to output
	putList(out, template.getValueNodes(aContext) );
	ActiveNode hrNode = new TreeElement("hr");
	out.putNode( hrNode);
	*/

      //  copy input to output as a default (copied from nameHandler)
	ActiveNodeList treematched = getExtracted(aContext);
	if (treematched == null) {
	    reportError(in, aContext, "No input list: <tm_like> should be second tag");
	    return;  
	}
	if (template == null) {
	    reportError(in, aContext, "No template list: <tm_like> needs content");
	    return;  
	}

    
	// do tree-matching of template vs treematched
	// call treematch recursively
	if (keepLike == true){
	    boolean checking = false;
	    int gotOne =  treematch(treematched, templateList,
				    checking, keepLike, out);
	}
	else{  // run through top-level nodes only, keeping each one 
	    // which doesn't match
	    int len = treematched.getLength();
	    for (int nodeIdx = 0; nodeIdx < len; nodeIdx ++){
		ActiveNode outItem = treematched.activeItem(nodeIdx).deepCopy();
		// check match for this one node by passing it in as the sole
		// entry on a list
		ActiveNodeList oneItemList = new TreeNodeList();
		oneItemList.append( outItem );
		boolean checking = true;
		int gotOne =  treematch(oneItemList, templateList,
					checking, keepLike, out);
		// only non-matches get kept
		if (gotOne == 0)
		    out.putNode(outItem);
	    }

	}

	/*
	  // finally, copy input nodes onto output for diagnostics
	ActiveNode hrNode2 = new TreeElement("hr");
	out.putNode( hrNode2);
	putList(out, treematched);  // cannablized from fromHandler 
	*/
    }
    tm_likeHandler() { super(true, false); }

    // diagnostic routine only
    void showList(ActiveNodeList testList, String title){
	if (testList == null)
	    return;
	int len = testList.getLength();
	System.out.println(">> " + title);
	for (int i = 0; i < len; i++){
	    ActiveNode keyItem = testList.activeItem( i );	
	    String keyName = getNodeName(keyItem, false /* case */);
	    String keyText = keyItem.toString().trim();
	    System.out.println("i = " +i+ " item = " +keyItem+ 
			       " name = " + keyName + " string: " + keyText +
			       " length = " + keyText.length() );
	}
	System.out.println( "<< " + title);
    }

    // returns true if nodex match by name, attributes, etc.
    boolean compareNodes(ActiveNode keyNode, ActiveNode srcNode){
	if (keyNode == null || srcNode == null)
	    return false;
	String nodeName = getNodeName(srcNode, false);
	String keyName = getNodeName(keyNode, false);
	if (keyName == null || nodeName == null)
	    return false;

	if ( !keyName.equalsIgnoreCase(nodeName)  )
	    return false;

	// !!! named non-elements (e.g. entities) might fall through here

	
	// to get here, both nodes must be elements with same name
	ActiveElement keyElt = (ActiveElement)keyNode;
	ActiveElement srcElt = (ActiveElement)srcNode;
	
	ActiveAttrList keyAtts = (ActiveAttrList)keyElt.getAttributes();
	ActiveAttrList srcAtts = (ActiveAttrList)srcElt.getAttributes();
	if ( keyAtts != null){
	    // if the key has attributes, make sure the
	    // source has the same ones with same values; otherwise no match
	    for (int idx = 0; idx < keyAtts.getLength(); idx++){
		String attName =  keyAtts.item(idx).getNodeName();
		if ( !keyAtts.getAttribute(attName).
		      equals( srcAtts.getAttribute(attName) ) ){
		    return false;
		}
	    }
	    
	}

	return true;									   
    }

    protected int nextNonNullIdx(ActiveNodeList testList, int startIdx){
	int len = testList.getLength();
	int outIdx = startIdx;

	while ( outIdx < len  && 
		(testList.activeItem( outIdx )).toString().trim().length() <= 0 ){
	    outIdx ++;
	}
	return outIdx;
	// warning:  this is capable of returning an out-of-range index (i.e.
	// == getLength() ); requester needs to check
    }

    // treematch can either look for the top of a candiate tree 
    // (checking==null), or check whether an already-found top in fact
    // matches.  

    protected int treematch(ActiveNodeList treematched,
			    ActiveNodeList templateList, 
			    boolean checking, boolean keepLike, Output out) {
	int gotSome = 0;
      
	// empty keylist matches anything by default
	if ( templateList == null || templateList.getLength() == 0 )
	    return 1;

	// empty source with non-empty keylist is no match
	if ( treematched == null || treematched.getLength() == 0 )
	    return 0;

	int len = treematched.getLength();

	boolean myChecking  = true;

	int myKey = nextNonNullIdx (templateList, 0);

	if (myKey == templateList.getLength() ){
	    // over-range check
	    return 1;
	}

	int goodStartNode = -1; // Is this ok for first on list?
	int goodStopNode = -1; // Is this ok for last on list?
	int keyLen = templateList.getLength();

	int doDepthSearch = 0;
	if (checking == false )
	    doDepthSearch = 1;

	for (int depthSearch = 0; depthSearch <= doDepthSearch; depthSearch++){
	    for (int i = 0; i < len; ++i) {

		ActiveNode keyItem  = templateList.activeItem( myKey );	

		String keyName = getNodeName(keyItem, false /* case */);


		String keyText = "";
		if (keyName == null){  // only load keyText if no node name
		    keyText = keyItem.toString().trim();
		    if (keyText.length() == 0 ){ // no  name exists ?!?
			System.out.println("Empty key name/text in treematch");
			return 0;
		    }
		    else
			keyName = ""; // no null pointer, just empty
		}

		// get content item and name
		ActiveNode item = treematched.activeItem(i);

		String nodeName = getNodeName(item, false);
		String nodeText = "";
		if (nodeName == null){  // only load nodeText if no node name
		    nodeText = item.toString().trim();
		}
		int itemResult = 0;

		// don't match two empty text strings; make one different
		if (keyText.equals("") && nodeText.equals("") )
		    keyText = "?";


                // if the nodes at this level match, great; otherwise, see
		// if there is a match in their content instead.
		if ( depthSearch == 0 && 
		     !( compareNodes(keyItem, item) || 
			keyName.equalsIgnoreCase("any-tag") ||
			(keyName == null && nodeText.indexOf(keyText) >= 0) )  ){


		    if (checking == true){
			// look inside this tag for a match, once top one found
			ActiveNodeList oneKeyList = new TreeNodeList();
			oneKeyList.append( keyItem );


			itemResult =  treematch( item.getContent(), 
						 oneKeyList,
						 myChecking, keepLike, out);
		    }


		   if (itemResult == 0){
		       continue; 
		       // no match if depth-first still doesn't find it
		   }

		}
                
		// got a candidate! ... keep track of node, whether to
		// check below it, and next key to next check against

		// pass on keyList and itemList (empty or not);
		// nonzero result means a true  or default match
		if (depthSearch == 0){
		    itemResult +=   treematch( item.getContent(), 
					   keyItem.getContent(),
					   myChecking, keepLike, out);
		}
		else // dropping the whole topNode search down a level,
		    // using whole template and source-item's contents.
		    // Record whole nodes outputted, not piecemeal matching;
		    // don't advance myKey.
		    gotSome +=   treematch( item.getContent(), 
					   templateList,
					   false, keepLike, out);

		if (itemResult > 0 && depthSearch == 0){

		    // matched! ==> look for next key
		    myKey = nextNonNullIdx( templateList, myKey + 1);
		    if ( goodStartNode < 0 ) { // first matching node
			goodStartNode = i;
		    }
		}
		
		// Have we already found the last node in key?
		if ( myKey == keyLen && depthSearch == 0 ){ 
		    if (checking == false){
			goodStopNode = i;	
			// put all matching nodes,
			// from start to now (=stop),  onto output
			for (int goodNodes = goodStartNode; 
			     goodNodes <= goodStopNode; goodNodes ++){
			    ActiveNode outItem = treematched.activeItem(i).deepCopy();
	
			    // keepLike says to keep nodes like this
			    // (as opposed to keeping all but those
			    // like this)
			    if (keepLike)
				out.putNode(outItem);
			}
			gotSome ++;

			// reset start/stop/key and start search afresh on
			// remaining nodes
			myKey = nextNonNullIdx( templateList, 0);
			goodStartNode = -1;
			goodStopNode = -1;
		    }
		    else{  // if checking, only need one match for ok
			return 1;
		    }

		}// end if-myKey

	    }// end ii < len loop
	} // end depthSearch loop
	return gotSome;
    }// end treematch()

}// end tm_likeHandler
