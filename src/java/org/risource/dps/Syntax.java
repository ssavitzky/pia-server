////// Syntax.java: Node Syntax Handler interface
//	$Id: Syntax.java,v 1.5 1999-06-04 22:39:26 steve Exp $

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
import org.w3c.dom.NodeList;

import org.risource.dps.active.*;

/**
 * The interface for a Node's Syntax handler. 
 *
 *	A Node's Syntax handler provides all of the necessary syntactic
 *	information required for parsing a Node. <p>
 *
 *	Syntax has booleans that correspond to tests that a Parser would
 *	otherwise have to make using information from the DTD.  This permits a
 *	(non-verifying) parser to be built without exposing the gory details
 *	of SGML to the casual programmer. <p>
 *
 * @version $Id: Syntax.java,v 1.5 1999-06-04 22:39:26 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Handler
 * @see org.risource.dps.Input 
 */

public interface Syntax {

  /************************************************************************
  ** Flags:
  ************************************************************************/

  /** Syntax for an empty element. */
  public final static int EMPTY   =  1;
  /** Syntax for a normal element.  The contents are expanded. */
  public final static int NORMAL  =  2;
  /** Syntax for a quoted element:  contents are parsed but not expanded. */
  public final static int QUOTED  =  6;

  /** Syntax flag (to be or'ed in) to suppress expansion. */
  public final static int NO_EXPAND	=  4;
  /** Syntax flag (to be or'ed in) to suppress parsing of entities. */
  public final static int NO_ENTITIES	=  8;
  /** Syntax flag (to be or'ed in) to suppress parsing of elements. */
  public final static int NO_ELEMENTS	= 16;

  /** Syntax for a literal: elements and entities are not recognized. */
  public final static int LITERAL =  NO_ENTITIES | NO_ELEMENTS | NO_EXPAND;

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine whether the given Node (for which this is
   *	the Handler) is an empty element, or whether content is expected.
   *	It is assumed that <code>this</code> is the result of the Tagset
   *	method <code>handlerForTag</code>.
   *
   * @param t the Node for which this is the handler, and for which the
   *	ssyntax is being checked.
   * @return <code>true</code> if the Node is an empty Element.
   * @see org.risource.dps.Tagset
   */
  public boolean isEmptyElement(Node n);

  /** Called to construct a node for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.  
   *	May perform additional dispatching on <code>name</code> 
   *	or <code>data</code>
   *
   * @param nodeType the node type
   * @param name an optional node name
   * @param data optional data
   * @return a new ActiveNode having <code>this</code> as its Syntax. 
   */
  public ActiveNode createNode(short nodeType, String name, String data);

  /** Called to construct a node for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.  
   *	May perform additional dispatching on <code>name</code> 
   *	or <code>data</code>
   *
   * @param nodeType the node type
   * @param name an optional node name
   * @param value optional value
   * @return a new ActiveNode having <code>this</code> as its Syntax. 
   */
  public ActiveNode createNode(short nodeType, String name,
			       ActiveNodeList value);

  /** Called to construct an element for the given handler. 
   *
   *	Internally calls getActionForNode if necessary.
   *	May perform additional dispatching on <code>tagname</code> or
   *	<code>attributes</code>.
   *
   * @param tagname the Element's tag name.
   * @param attributes the Element's attributes.
   * @param hasEmptyDelim an XML `empty' delimiter (closing `/') is present.
   * @return a new ActiveElement having <code>this</code> as its Syntax. 
   */
  public ActiveElement createElement(String tagname, ActiveAttrList attributes,
				     boolean hasEmptyDelim);

  /** Called to determine the correct Action handler for a Node. 
   *
   *	It is assumed that <code>this</code> is the result of the
   *	Tagset method <code>handlerForTag</code>.  Normally just
   *	returns <code>this</code>, but a handler may further examine
   *	the Node's attributes and return something more specific. 
   *	The Handler is also permitted to <em>modify</em> the Node.
   *	<p>
   *
   *	Note that this replaces the earlier technique of dispatching
   *	to a separate named actor, although that may still be useful
   *	in some cases.  Since <code>getActionForNode</code> is called
   *	at parse time, it preceeds any actual processing of the Node. <p>
   *
   * @param n the Node for which the syntax is being checked.
   * @return the correct Handler for the Node.  
   * @see org.risource.dps.Tagset
   */
  public Action getActionForNode(ActiveNode n);


  /** If <code>true</code>, Element tags are recognized in content.  If 
   *	<code>false</code>, the content will consist only of Text and 
   *	(possibly) entity references.
   */
  public boolean parseElementsInContent();

  /** If <code>true</code>, Entity references are recognized in content. */
  public boolean parseEntitiesInContent();

  /** Return <code>true</code> if Text nodes are permitted in the content.
   */
  public boolean mayContainText();

  /** Return <code>true</code> if paragraph elements are permitted in the
   *	content.  If this is <code>true</code> and <code>mayContainText</code>
   *	is false, whitespace is made ignorable and non-whitespace is 
   *	commented out.
   */
  public boolean mayContainParagraphs();

  /** Return true if this kind of Node implicitly ends the given one. 
   *	This is not as powerful a test as using the DTD, but it will work
   *	in most cases and permits a simpler parser.
   */
  public boolean implicitlyEnds(String tag);

  /** Return true if this kind of Node is a potential child of the given one.
   */
  public boolean isChildOf(String tag);

}
