////// Input.java: Depth-first enumerator for parse trees
//	$Id: Input.java,v 1.6 1999-07-14 20:19:53 steve Exp $

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


package org.risource.dps;

import org.w3c.dom.Node;

/**
 * The interface for depth-first enumeration of a document or parse tree.
 *
 *	The Processor maintains a stack of Input objects from which
 *	input is obtained as needed.  An Input may have to be
 *	provided with a reference back to the Processor it is sending
 *	input to, in case it needs additional information (e.g. for
 *	parsing).  This is done during initialization and is not part
 *	of the interface. <p>
 *
 *	Note that although an Input superficially resembles the DOM's
 *	TreeIterator, the two interfaces are quite different.  A 
 *	TreeIterator is able to traverse a Document tree in any direction,
 *	whereas an Input is unidirectional and so is able to handle
 *	trees that are generated on-the-fly and that need not be retained. <p>
 *	
 *	In addition, an Input has a ``current'' node (which the TreeIterator
 *	explicitly does <em>not</em>), which can be queried and converted. <p>
 *
 *	Note that an Input which is being used as an Enumeration may
 *	have to ``look ahead'' to ensure that <code>hasMoreElements</code>
 *	can return an accurate result. <p>
 *
 * @version $Id: Input.java,v 1.6 1999-07-14 20:19:53 steve Exp $
 * @author steve@rsv.ricoh.com
 * 
 * @see org.risource.dps.Processor
 * @see java.util.Enumeration */

public interface Input extends Cursor {

  /************************************************************************
  ** Navigation Operations:
  ************************************************************************/

  /** Returns to the first node from this source and makes it current.  
   *	May ascend levels if necessary. <p>
   *
   * @return  <code>false</code> if no nodes were ever
   *	available from this source, or if the source cannot be reset. 
   */
  public boolean toFirst();

  /** Returns the next sibling of the current node and makes it current.  
   *	May require traversing all of the (old) current node if its
   *	children have not yet been seen. <p>
   *
   * @return  <code>false</code> if and only if no more nodes are
   *	available at this level. 
   */
  public boolean toNext();

  /** Moves the Input to the parent of the current Node.
   *	After calling <code>toParent</code>, <code>toNext</code> will
   *	move to the parent's next sibling.
   *
   * @return <code>false</code> if the current Node has no parent, or if 
   *	<code>depth&nbsp;==&nbsp;0</code>.
   */
  public boolean toParent();

  /** Moves the Cursor to the first child of the current Node. 
   *	A subsequent call on <code>toNext</code> will move the Cursor to the 
   *	second child, if any.
   *
   * @return <code>false</code> if and only if the current Node has no 
   *	children. 
   */
  public boolean toFirstChild();

  /** Returns <code>true</code> if and only if the current node has no
   *	previous siblings.  This occurs only with a node returned by
   *	<code>toFirstChild</code> or <code>toFirst</code>.
   */
  public boolean atFirst();

  /** Returns <code>true</code> if and only if the current node has no next
   *	sibling.  A subsequent call on <code>toNext</code> will return
   *	<code>false</code>
   */
  public boolean atLast();

  /** Test whether the current node is expected to have children.
   *
   *	Note that this is <em>not</em> the same as calling
   *	<code>hasChildren</code> on the current node!  The parse tree may be
   *	being constructed on the fly, and nodes already seen may no longer
   *	be accessible.  What <em>is</em> true is that <em>if</em> calling
   *	<code>hasChildren</code> on a node returns true, all of its
   *	children will have been seen. <p>
   *
   * @return <code>true</code> if the current node has children.
   */
  public boolean hasChildren();


  /** Test whether the current node is expected to have children <em>that
   *	may need expansion</em>. <p>
   *
   *	Note that implementations are free to return the pessimistic
   *	answer of <code>true</code> in all cases, but it will be more
   *	efficient if they can be more discriminating. <p>
   *
   * @return <code>true</code> if the current node has active children.
   */
  public boolean hasActiveChildren();


  /** Test whether the current node has attributes.
   *
   * @return <code>true</code> if the current node has attributes.
   */
  public boolean hasAttributes();


  /** Test whether the current node has attributes <em>that may need
   *	expansion</em>. <p>
   *
   *	Note that implementations are free to return the pessimistic
   *	answer of <code>true</code> in all cases, but it will be more
   *	efficient if they can be more discriminating. <p>
   *
   * @return <code>true</code> if the current node has active attributes.
   */
  public boolean hasActiveAttributes();


  /** Returns <code>true</code> if and only if <code>getParent</code> will
   *	return <code>null</code>. <p>
   *
   *	Note that <code>atTop</code> may be <code>true</code> even if the
   *	current node actually has a parent, if the Input is iterating over
   *	a subtree, nodelist, or document fragment. <p>
   */
  public boolean atTop();

  /************************************************************************
  ** Processing Operations:
  ************************************************************************/

  /** Returns the action handler, if known, for the current node. 
   */
  public Action getAction();

}
