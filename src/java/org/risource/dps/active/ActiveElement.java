////// ActiveNode.java: Active Node (parse tree element) interface
//	ActiveElement.java,v 1.6 1999/03/01 23:45:40 pgage Exp

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


package crc.dps.active;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import crc.dps.Action;
import crc.dps.Syntax;
import crc.dps.Handler;

/**
 * A DOM Element node which includes extra syntactic and semantic
 *	information, making it suitable for use in active documents in
 *	the DPS.
 *
 * @version ActiveElement.java,v 1.6 1999/03/01 23:45:40 pgage Exp
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Active
 * @see crc.dps.ActiveNode
 * @see crc.dps.Action
 * @see crc.dps.Syntax
 * @see crc.dps.Processor
 */

public interface ActiveElement extends ActiveNode, Element {

  /************************************************************************
  ** Attribute convenience functions:
  ************************************************************************/

  /** Convenience function: get an Attribute by name. */
  public Attribute getAttribute(String name);

  /** Convenience function: get an Attribute by name and return its value. */
  public NodeList getAttributeValue(String name);

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttributeString(String name);

  /** Convenience function: get an Attribute by name and return its value
   *	as a boolean
   */
  public boolean hasTrueAttribute(String name);

  /** Convenience function: Set an attribute's value to a NodeList. */
  public void setAttributeValue(String name, NodeList value);

  /** Convenience function: Set an attribute's value to a Node. */
  public void setAttributeValue(String name, Node value);

  /** Convenience function: Set an attribute's value to a String. */
  public void setAttributeValue(String name, String value);

  /** Convenience function: Set an attribute's value to a NodeList,
   *	given the optimistic assumption that the attribute is currently
   *	undefined.
   */
  public void addAttribute(String name, NodeList value);

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Create a copy with a different attribute list and content */
  public ActiveElement editedCopy(AttributeList atts, NodeList content);

  /************************************************************************
  ** Syntax:  DTD entry:
  ************************************************************************/

  /** Returns the Token's definition from the Document's DTD. */
  public crc.dom.ElementDefinition getDefinition();

  /************************************************************************
  ** Syntax: convenience flags:
  ************************************************************************/

  /** Returns true if the Token corresponds to an Element that
   *	consists of a start tag with no content or corresponding end
   *	tag.
   */
  public boolean isEmptyElement();

  /** Sets the internal flag corresponding to isEmptyElement. */
  public void setIsEmptyElement(boolean value);

  /** Returns true if the Token corresponds to an empty Element and
   *	its (start) tag contains the final ``<code>/</code>'' that marks
   *	an empty element in XML.
   */
  public boolean hasEmptyDelimiter();

  /** Sets the internal flag corresponding to hasEmptyDelimiter. */
  public void setHasEmptyDelimiter(boolean value);

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, or to an end tag that was omitted from the input or 
   *	that should perhaps be omitted from the output.
   */
  public boolean implicitEnd();

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag);

}
