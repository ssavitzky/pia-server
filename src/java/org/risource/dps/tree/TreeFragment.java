// TreeFragment.java
// $Id: TreeFragment.java,v 1.4 2001-04-03 00:04:55 steve Exp $

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


package org.risource.dps.tree;

import java.io.*;
import org.risource.dps.active.*;
import org.w3c.dom.*;

import org.risource.dps.Handler;
import org.risource.dps.Namespace;
import org.risource.dps.util.Copy;

/** 
 * Class for DocumentFragment nodes. <p>
 */
public class TreeFragment extends TreeNode implements ActiveFragment
{


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /**
   * Convert the node and its content to a String.
   */
  public String toString(){
    return startString() + contentString() + endString();
  }

  /** Return SGML/XML comment left bracket; that is "<!--".
   *  Subclasses are suppose to override this function
   *  to return appropriate start tag.
   */
  public String startString(){
    return "";
  }
  
  /** Return the String equivalent of this node type's content or data.
   *  Subclasses are suppose to override this function
   *  to return appropriate content string.
   */
  public String contentString(){
    return (getChildNodes() == null)? "" : getChildNodes().toString();
  }

  /** Return SGML/XML comment right bracket; that is "-->".
   *  Subclasses are suppose to override this function
   *  to return appropriate end tag.
   *	
   */
  public String endString(){
    return "";
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public TreeFragment() {
    super(Node.DOCUMENT_FRAGMENT_NODE, null);
  }

  public TreeFragment(ActiveNodeList content) {
    super(Node.DOCUMENT_FRAGMENT_NODE, null);
    Copy.appendNodes(content, this);
  }

  /**
   * deep copy constructor.
   */
  public TreeFragment(TreeFragment f, boolean copyChildren){
    super(f, copyChildren);
  }

  public ActiveNode shallowCopy() {
    return new TreeFragment(this, false);
  }

}



