////// tagsetHandler.java: <tagset> Handler implementation
//	$Id: tagsetHandler.java,v 1.12 2001-01-11 23:37:24 steve Exp $

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

import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tagset.*;
import org.risource.dps.output.DiscardOutput;

import java.util.StringTokenizer;

/**
 * Handler for &lt;tagset&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id: tagsetHandler.java,v 1.12 2001-01-11 23:37:24 steve Exp $
 * @author steve@rii.ricoh.com
 */

public class tagsetHandler extends GenericHandler {

  /************************************************************************
  ** Parse-time Operations:
  ************************************************************************/

  /** In this case we actually create a Tagset object.
   *	It goes into parse tree if we're building one, so the tagname
   *	has to match.  
   */
  public ActiveElement createElement(String tagname, ActiveAttrList attrs,
				     boolean hasEmptyDelim) {

    // We should check at this point to see if we need a different base class.
    String name = attrs.getAttribute("name");
    BasicTagset ts = new BasicTagset(tagname, name, attrs, null);
    ts.setHandler(this);

    if (hasEmptyDelim) ts.setHasEmptyDelimiter(hasEmptyDelim);
    ts.setIsEmptyElement(hasEmptyDelim);
    ts.setAction(getActionForNode(ts));

    return ts;

  }

  /* === This is very twisty. ===
    
     The element we create at parse time with createElement becomes part of
     the parse tree.  We probably don't want that to become the ``official''
     tagset, although it's possible we might.  At least at first, then,
     although we create a BasicTagset node, we might not end up using it.

     It makes a difference whether the Document _is_ a tagset, or whether this
     node is refering to one.  We may want a separate ``use_tagset'' handler
     for the latter case, especially since we may need to hack action routines
     on the Input.

     What we _do_ need to do, though, is put a reference to the tagset into
     the document parse tree under construction, because we're going to need
     it for looking up entities when we go to re-use the parse tree.

     When we get around to processing we need to make a Tagset, or track down
     an old one, and put it into a new TopContext where it can be hacked on.
     The parser is still using the old one.

     When we want a recursive Tagset, though, we really need to hack the one
     the parser is using.  DTD's and PI's may also need to do this (e.g. the
     doctype and the XML case- and space-handling pragmas).

     A recursive Tagset _really_ only changes the language used in <value> and
     <action> tags, not otherwise.  Basically, a recursive tagset leaves the
     parser using the processor's current tagset, while a non-recursive one
     replaces the one the _processor_ is using, by switching to a new
     TopProcessor.  If the Parser's tagset is locked, though, we have to build
     a new one.

     But that's only when encountering a <tagset> element in the normal course
     of processing.  When _defining_ a tagset, or when specifying a tagset in
     a PI or DTD, we _still_ have to be able to get from the processor to the
     parser.
     
   */

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will get a little tricky.  
   *	There are really two distinct possibilities here -- are we 
   *	<em>using</em> a tagset, or <em>defining</em> one?  
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveElement n  = in.getActive().asElement();
    TopContext   top = cxt.getTopContext();
    Tagset oldTagset = getParserTagset(top);

    String parserTSname = n.getAttribute("tagset");
    Tagset parserTagset = null;
    String parentTSname = n.getAttribute("parent");
    Tagset parentTagset = null;
    String inclusions   = n.getAttribute("include");
    boolean recursive   = n.hasTrueAttribute("recursive");
    String name		= n.getAttribute("name");

    // Check the TopProcessor.  If it's a TagsetProcessor, we're processing a
    // tagset file in the Loader (figures).  

    // Otherwise, we're just referencing a tagset === ignore this case for now.
    //	  Requires replacing/unlocking the top processor's tagset 
    //	  and if recursive, replacing the Parser's as well.

    TagsetProcessor tproc = (top instanceof TagsetProcessor)
      ? (TagsetProcessor) top : null;

    // Construct a new tagset.  
    // 	  === Clone the specified PARENT tagset, if any.
    BasicTagset newTagset = /* ("HTML".equals(parentTSname))
      ? new HTML_ts("TAGSET", name, n.getAttrList(), null)
      :*/ new BasicTagset("TAGSET", name, n.getAttrList(), null);

    if (parentTSname == null) {
      parentTagset = new BasicTagset("TAGSET", name, n.getAttrList(), null);
//  } else if ("HTML".equals(parentTSname)) {

    } else {
      parentTagset = tproc.loadTagset(parentTSname);
      cxt.debug("Loading tagset=" + parentTSname + 
		((parentTagset == null)? " FAILED" : " OK"));
      if (parentTagset == null) {
	reportError(in, cxt, "Cannot load parent tagset " + parentTSname 
		    + " of " + name );
      } else {
	newTagset.setParent(parentTagset);
      }
    } 

    // Handle the inclusions.
    if (inclusions != null) {
      StringTokenizer inames = new StringTokenizer(inclusions);
      while (inames.hasMoreElements()) {
	String incN  = inames.nextElement().toString();
	Tagset incTS = tproc.loadTagset(incN);
	cxt.debug("Loading tagset=" + incN + 
		   ((incTS == null)? " FAILED" : " OK"));
	if (incTS == null) {
	  reportError(in, cxt, "Cannot load included tagset " + incN);
	} else {
	  newTagset.include(incTS);
	}
      }
    }

    // If there's a parser tagset (TAGSET attribute), load it.
    if (parserTSname != null) {
      // load the specified parserTagset (TAGSET attribute)
      // Make it the current tagset in the parser.
      if (recursive) {
	reportError(in, cxt, "TAGSET and RECURSIVE attrs are incompatible.");
      }
      if (parserTSname.equals("tagset")) parserTagset = new tagset();
      else parserTagset = tproc.loadTagset(parserTSname);
	// org.risource.dps.tagset.Loader.require(parserTSname);
      cxt.debug("Loading tagset=" + parserTSname + 
		   ((parserTagset == null)? " FAILED" : " OK"));
      if (parserTagset == null) {
	reportError(in, cxt, "Cannot load parser tagset " + parserTSname);
	setParserTagset(top, parentTagset);
      } else {
	setParserTagset(top, parserTagset);
      }      
    } else if (recursive) { 
      // If the tagset is recursive, set it in the parser.
      setParserTagset(top, newTagset);
    } else {
      // Otherwise parse with the parent tagset.
      setParserTagset(top, parentTagset);
    }

    // Load the tagset. 
    if (tproc != null) {
      // We're loading a Tagset, so we don't need output.  Go through the
      // input and quietly discard the results.
      // === at this point it's a little unclear whether we need a new node...
      BasicTagset inTagsetNode = (BasicTagset) n;
      if (tproc.getNewTagset() == null) tproc.setNewTagset(newTagset);
      cxt.debug("Initializing tagset " + tproc.getNewTagset().getName() + "\n");
      cxt.subProcess(in, new DiscardOutput()).processChildren();
      cxt.debug("Done with " + tproc.getNewTagset().getName() + "\n");
    } else {
      // We're using or defining a Tagset in an ordinary document. 
      // For this we really need a new TopProcessor. 
      unimplemented(in, cxt, "using a sub-tagset");
      cxt.subProcess(in, out).processChildren();
    }

    // Lock the new tagset

    newTagset.setLocked(true);

    // Restore the top processor's old tagset.
    setParserTagset(top, oldTagset);
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  protected Tagset getParserTagset(TopContext top) {
    ProcessorInput in = top.getProcessorInput();
    return (in == null)? null : in.getTagset();
  }

  protected void setParserTagset(TopContext top, Tagset ts) {
    ProcessorInput in = top.getProcessorInput();
    if (in != null) in.setTagset(ts);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public tagsetHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

