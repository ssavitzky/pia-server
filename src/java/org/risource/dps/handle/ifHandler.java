////// ifHandler.java: Node Handler generic implementation
//	$Id: ifHandler.java,v 1.7 1999-05-18 20:17:54 steve Exp $

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

import org.w3c.dom.Node;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

/**
 * Handler for &lt;if&gt;. <p>
 *
 *	This would actually be more efficient if the parser built a
 *	node of type if_node that cached the <code>then</code> and
 *	<code>else</code> children.  
 *	<p>
 *
 * @version $Id: ifHandler.java,v 1.7 1999-05-18 20:17:54 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.BasicTagset
 * @see org.risource.dps.Input 
 */

public class ifHandler extends GenericHandler {

  protected static Class elseHandlerClass = new elseHandler().getClass();
  protected static Class thenHandlerClass = new thenHandler().getClass();
  protected static Class elsfHandlerClass = new elsfHandler().getClass();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(Input in, Context aContext, Output out) {
    ActiveNodeList content = Expand.getContent(in, aContext);
    processConditional(in, aContext, out, content);
  }

  public boolean processConditional(Input in, Context aContext, Output out,
				    ActiveNodeList content) {
    boolean trueCondition = false;
    int len = content.getLength();
    for (int i = 0; i < len; ++i) {
      ActiveNode child = content.activeItem(i);
      /* 
       * Use a fast, efficient test for determining the syntactic class of
       * the children:  simply compare the classes of their handlers.
       */
      if (child.getNodeType() == Node.ELEMENT_NODE) {
	ActiveElement ct = (ActiveElement)child;
	Class cl = ct.getSyntax().getClass();
	if (cl == thenHandlerClass) {
	  if (trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return true;
	  }
	} else if (cl == elsfHandlerClass) {
	  if (!trueCondition) {
	    // else-if: just delegate to <else-if>'s (expanded) children.
	    if (processConditional(in, aContext, out,
				   Expand.processNodes(ct.getContent(),
						       aContext)))
	      return true;
	  }
	} else if (cl == elseHandlerClass) {
	  if (!trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return true;
	  }
	} else {
	  trueCondition = Test.trueValue(child, aContext);
	}
      } else if (Test.trueValue((ActiveNode)child, aContext)) {
	trueCondition = true;
      }
    }
    return false;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public ifHandler() {
    expandContent = false;
    syntaxCode = QUOTED;
  }
}
