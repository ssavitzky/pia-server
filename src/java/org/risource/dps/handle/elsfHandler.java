////// elsfHandler.java: Node Handler generic implementation
//	$Id: elsfHandler.java,v 1.9 2001-04-03 00:04:23 steve Exp $

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

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

/**
 * Handler for &lt;elsf&gt;. <p>
 *
 *	This is really just a syntactic placeholder, but the fact that
 *	it's a subclass makes it possible for &lt;if&gt; to use a faster
 *	and more reliable test than comparing tagnames.
 *	<p>
 *
 * @version $Id: elsfHandler.java,v 1.9 2001-04-03 00:04:23 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Tagset
 * @see org.risource.dps.tagset.BasicTagset
 * @see org.risource.dps.Input 
 */

public class elsfHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  public int getActionCode() {
    return Action.COPY_NODE;
  }

  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    ActiveElement e = in.getActive().asElement();
    ActiveElement element = e.editedCopy(atts, null);

    out.startNode(element);
    Copy.copyNodes(content, out);
    out.endElement(e.isEmptyElement() || e.implicitEnd());
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public elsfHandler() {
    expandContent = false;	// true		expand content?
  }
}
