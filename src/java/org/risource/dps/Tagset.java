////// Tagset.java: Node Handler Lookup Table interface
//	$Id: Tagset.java,v 1.10 1999-08-20 00:00:58 steve Exp $

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

import java.util.Enumeration;
import java.io.File;

import org.risource.dps.active.*;
import org.w3c.dom.NamedNodeMap;

/**
 * The interface for a Tagset -- a lookup table for syntax. <p>
 *
 *	A Node's Handler provides all of the necessary syntactic and semantic
 *	information required for parsing, processing, and presenting the Node.
 *	A Tagset can be regarded as either a lookup table for syntactic
 *	information, or as a node factory for the documents so described.  <p>
 *
 *	Note that a Tagset can be used to construct either generic DOM Node's,
 *	or DPS ActiveNode's.  A Parser is free to use either.  In the current
 *	implementation, however, <em>all</em> documents produced in the DPS
 *	are Active parse trees, so the default is for the ``generic'' nodes
 *	produced by the standard tagsets are identical to the active ones.
 *	The only difference is the return type.  The specialized Output
 *	ToDocument should be used for conversion.  <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Tagset will also be a Node, so that tagsets can be read and
 *	stored as documents or (better) DTD's.  <p>
 *
 * === 	need encoders/decoders for character entities, URLs, etc.
 *
 * @version $Id: Tagset.java,v 1.10 1999-08-20 00:00:58 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Active
 * @see org.risource.dps.active.ActiveNode
 * @see org.risource.dps.Input
 * @see org.risource.dps.Output
 * @see org.risource.dps.output.ToDocument
 */

public interface Tagset  {

  /************************************************************************
  ** Context:
  ************************************************************************/

  /** Returns a Tagset which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public Tagset getContext();

  /** Include definitions from a given tagset. 
   */
  public void include(Tagset ts);

  /** Include definitions, defaults, and other parameters from a given tagset. 
   */
  public void setParent(Tagset ts);

  /** Get the tagset's name. */
  public String getName();


  /************************************************************************
  ** Entity Bindings:
  ************************************************************************/

  /** Return a namespace with a given name.  Returns the entity table 
   *	associated with the tagset (possibly in the context chain) having 
   *	the given name.
   */
  public Namespace getNamespace(String name);

  /** Obtain the current Entity bindings. */
  public EntityTable getEntities();

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNode getBinding(String name);

  /** Bind an Entity.
   */
  public void setBinding(String name, ActiveNode ent);


  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.  Returns <code>getHandlerForType(Node.ELEMENT_NODE)</code> 
   *	as a default.
   */
  public Handler getHandlerForTag(String tagname);

  public void setHandlerForTag(String tagname, Handler newHandler);

  /** Called during parsing to return a suitable Handler for a generic
   *	Node, given the Node's type.
   */
  public Handler getHandlerForType(short nodeType);

  public void setHandlerForType(short nodeType, Handler newHandler);

  /** Called during parsing to determine whether a Handler exists for a
   *	given attribute name.  Returns <code>null</code> as a default.
   */
  public Handler getHandlerForAttr(String attrName);

  public void setHandlerForAttr(String attrName, Handler newHandler);

  /************************************************************************
  ** Status:
  ************************************************************************/

   /** Test whether the Tagset is ``locked.''
   *
   *	A locked Tagset must be extended by creating a new Tagset with
   *	the locked Tagset as its context.
   */
  public boolean isLocked();

  /** Change the lock status. */
  public void setLocked(boolean value);

  /** Test whether the Tagset is up to date.  */
  public boolean upToDate();

  /** Set the Tagset's source File for timestamp check. */
  public void setSourceFile(File f);

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser();

  /** Creates an ActiveAttrList from a NamedNodeMap. */
  public ActiveAttrList createActiveAttrs(NamedNodeMap attrs);

  /** Creates an ActiveElement suitable for use in any document described
   *	by this Tagset.
   */
  public ActiveElement createActiveElement(String tagname,
					   ActiveAttrList attributes,
					   boolean hasEmptyDelim);

  /** Creates an ActiveNode of arbitrary type with name and (optional) data.
   */
  public ActiveNode createActiveNode(short nodeType, String name, String data);

  /** Creates an ActiveNode of arbitrary type with name and content.
   */
  public ActiveNode createActiveNode(short nodeType, String name,
				     ActiveNodeList data);

  /** Creates an ActiveAttribute node with name and value.
   */
  public ActiveAttr createActiveAttr(String name, ActiveNodeList value);

  /** Creates an ActiveEntity node with name and value.
   */
  public ActiveEntity createActiveEntity(String name, ActiveNodeList value);

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  public ActiveText createActiveText(String text, boolean isIgnorable);

  /************************************************************************
  ** Syntactic Information:
  ************************************************************************/

  /** Does this Tagset treat uppercase and lowercase tagnames the same?
   */
  public boolean caseFoldTagnames();

  /** Convert a tagname to the cannonical case. */
  public String cannonizeTagname(String name);

  /** Does this Tagset treat uppercase and lowercase attribute names 
   *	the same?
   */
  public boolean caseFoldAttributes();

  /** Set the case folding parameters */
  public void setCaseFolding(boolean forTags, boolean forAttrs);


  /** Convert an attribute name to the cannonical case. */
  public String cannonizeAttribute(String name);


  /** Return the tag of the paragraph element, implicitly started
   *	when text appears inside an element that should not contain it. 
   */
  public String paragraphElementTag();

  /** Return the Tagset's DTD.  In some implementations this may be
   *	the Tagset itself.
   */
  //  public DocumentType getDocumentType();


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the element names defined in this
   *	table.  Note that there is no good way to get the handlers for
   *	Node types other than Element unless the implementation gives
   *	them distinctive, generated names.
   */
  public Enumeration handlerNames();

  /** Returns an Enumeration of the element names defined in this table and
   *	its context, in order of definition (most recent last). */
  public Enumeration allHandlerNames();

  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  /** Convenience function to define a tag with a given syntax. */
  public Handler defTag(String tag, String notIn, String parents, int syntax,
			String cname, ActiveNodeList content);

}
