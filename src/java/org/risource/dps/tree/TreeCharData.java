////// TreeCharData.java -- implementation of ActiveCharData
//	$Id: TreeCharData.java,v 1.5 1999-11-04 22:34:03 steve Exp $

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


package org.risource.dps.tree;

import org.risource.dps.active.*;
import org.w3c.dom.*;

import org.risource.dps.*;
import org.risource.dps.util.*;

/**
 * An implementation of the ActiveCharData and CharacterData interfaces.
 *
 * <p>	This class can also be used as a generic ``raw'' text node that does 
 *	not perform entity conversion and is not marked in any way.  Such a
 *	node cannot be read back in as XML, but is almost essential when
 *	generating non-XML output.
 *
 * @version $Id: TreeCharData.java,v 1.5 1999-11-04 22:34:03 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see org.risource.dps.Context
 * @see org.risource.dps.Processor
 */
public class TreeCharData extends TreeNode implements ActiveCharData {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected String data = "";

  protected boolean ignorable = false;

  /** flag for whether the Text is whitespace. */
  protected boolean isWhitespace = false;

  /************************************************************************
  ** CharacterData interface:
  ************************************************************************/

  public void   setData(String data) 	{ 
    this.data = data;
    setIsWhitespace(Test.isWhitespace(data));
  }
  public String getData() 	   	{ return data; }

  public int	getLength() 		{ return data.length(); }

  public String getNodeValue() 		{ return getData(); }
  public void   setNodeValue(String newData) { setData(data); }

  /** CharData nodes evaluate to a NodeList containing the node. */
  public ActiveNodeList getValueNodes(Context cxt) {
    return new TreeNodeList(this);
  }
  
  public String substringData(int offset, int count) {
    return data.substring(offset, offset+count);
  }

  public void appendData(String newData) {
    setData(data + newData);
  }

  public void insertData(int offset, String newData) {
    setData(data.substring(0, offset) + newData + data.substring(offset));
  }

  public void deleteData(int offset, int count) {
    setData(data.substring(0, offset) + data.substring(offset + count));
  }

  public void replaceData(int offset, int count, String newData) {
    setData(data.substring(0, offset) + newData
	    + data.substring(offset + count));
  }

  /************************************************************************
  ** ActiveText interface:
  ************************************************************************/

  public void setIsIgnorable(boolean isIgnorable){ 
    ignorable = isIgnorable;
  }
  public boolean getIsIgnorable() { return ignorable; }

  public boolean getIsWhitespace() { return isWhitespace; }
  public void setIsWhitespace(boolean value) { isWhitespace = value; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public TreeCharData() {
  }

  public TreeCharData(TreeCharData e, boolean copyChildren) {
    super(e, copyChildren);
    data = e.data;
    ignorable = e.ignorable;
    isWhitespace = e.isWhitespace;
  }

  /** Construct a node with given data. */
  public TreeCharData(short type, String data) {
    super(type, null);
    this.data = data;
    setIsWhitespace(Test.isWhitespace(data));
  }

  /** Construct a node with a single character as data. */
  public TreeCharData(short type, char data) {
    this(type, String.valueOf(data));
  }

  /** Construct a node with an integer value. */
  public TreeCharData(short type, long data) {
    this(type, String.valueOf(data), false, false);
  }

  /** Construct a node with a floating-point value. */
  public TreeCharData(short type, double data) {
    this(type, String.valueOf(data), false, false);
  }

  /** Construct a node with given data and handler. */
  public TreeCharData(short type, String data, Handler handler) {
    this(type, data);
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeCharData(short type, String data, boolean isIgnorable,
		       boolean isWhitespace, Handler handler) {
    this(type, data);
    ignorable = isIgnorable;
    this.isWhitespace = isWhitespace;
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeCharData(short type, String data, boolean isIgnorable,
		       boolean isWhitespace) {
    this(type, data, isIgnorable, isWhitespace, null);
  }

  /** Construct a node with given data, flags, and handler. */
  public TreeCharData(short type, String data, boolean isIgnorable) {
    this(type, data);
    setIsIgnorable(isIgnorable);
  }

  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Node's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "";
  }

  /** Return the String equivalent of the Node's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getData(); 	// DO NOT insert character entities
    //return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Node's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "";
  }


  /** Convert the Node to a String.
   */
  public String toString() {
    return getData();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Node.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new TreeCharData(this, false);
  }

}
