////// ifHandler.java: Node Handler generic implementation
//	$Id: ifHandler.java,v 1.3 1999-03-12 19:26:20 steve Exp $

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
import org.risource.dom.Node;
import org.risource.dom.Element;
import org.risource.dom.NodeList;
import org.risource.dom.NodeEnumerator;
import org.risource.dom.Attribute;
import org.risource.dom.AttributeList;

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
 * @version $Id: ifHandler.java,v 1.3 1999-03-12 19:26:20 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.BasicTagset
 * @see org.risource.dps.Input 
 * @see org.risource.dom.Node
 */

public class ifHandler extends GenericHandler {

  protected static Class elseHandlerClass = new elseHandler().getClass();
  protected static Class thenHandlerClass = new thenHandler().getClass();
  protected static Class elsfHandlerClass = new elsfHandler().getClass();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public void action(Input in, Context aContext, Output out) {
    boolean trueCondition = false;
    ParseNodeList content = Expand.getProcessedContent(in, aContext);
    NodeEnumerator enum = content.getEnumerator();

    for (Node child = enum.getFirst(); child != null; child = enum.getNext()) {
      /* 
       * Use a fast, efficient test for determining the syntactic class of
       * the children:  simply compare the classes of their handlers.
       */
      if (child.getNodeType() == NodeType.ELEMENT) {
	ActiveElement ct = (ActiveElement)child;
	Class cl = ct.getSyntax().getClass();
	if (cl == thenHandlerClass) {
	  if (trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return;
	  }
	} else if (cl == elsfHandlerClass) {
	  if (!trueCondition) {
	    // else-if: just delegate to <else-if>'s (expanded) children.
	    content = Expand.processNodes(ct.getChildren(), aContext);
	    action(in, aContext, out, null, content);
	    return;
	  }
	} else if (cl == elseHandlerClass) {
	  if (!trueCondition) {
	    Expand.processChildren(ct, aContext, out);
	    return;
	  }
	} else {
	  trueCondition = true;
	}
      } else if (Test.trueValue((ActiveNode)child, aContext)) {
	trueCondition = true;
      }
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public ifHandler() {
    expandContent = true;
  }
}
