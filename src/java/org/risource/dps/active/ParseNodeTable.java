// ParseNodeTable.java
// $Id: ParseNodeTable.java,v 1.3 1999-03-12 19:25:31 steve Exp $

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


package org.risource.dps.active;

import java.io.*;
import org.risource.dom.*;

import java.util.Hashtable;
import java.util.Enumeration;


/**
 * ParseNodeTable behaves like a map, allowing one
 * to associate (String) key with a Node.  
 *
 * === Should probably implement Namespace
 */
public abstract class ParseNodeTable implements Serializable {

  protected boolean caseSensitive = true;

  /**
   * Retreive a value base on key.
   * @param name Key used to retreive value.
   * @return Value associated with name.
   */
  public Node getItem(String name) {
    name = cannonizeName(name);
    return (Node) nameSpace.get( name );
  }

  /**
   *  Add a new item to the end of the list and associate it with the given
   *  name. If the name already exists, the previous object is replaced,
   *  and returned. 
   *  If no object of the same name exists, null is returned, and the
   *  named Attribute is added to the end of the item list; that is, it is
   *  accessible via the item method using the index one less than the value
   *  returned by getLength(). 
   *
   * Put an association into table
   * @param name The key.
   * @param o The value.
   * @return Previous value associated with name; otherwise null.
   */
  public Node setItem(String name, Node o) {
    name = cannonizeName(name);

    if( getItem( name ) == null ){
      itemList.append( o ); 
      nameSpace.put(name, o);
      return null;
    }else{
      Node prev = (Node)nameSpace.put(name, o);
      long pos = itemList.indexOf( prev );
      if (pos >= 0) try {
	itemList.replace( pos, o );
      } catch (NoSuchNodeException ex) {}
      return prev;
    }
  }
  
  /**
   * Remove value indicated by name.
   * @param name The key.
   * @return Value associated with key
   * @exception NoSuchNodeException if name does not exist. 
   */
  public Node removeItem(String name) throws NoSuchNodeException {
    name = cannonizeName(name);

    if( ! nameSpace.containsKey( name ) ) 
      throw new NoSuchNodeException("No such node exists.");

    Node item = (Node) nameSpace.remove( name );
    itemList.remove( item );
    return item;
  }
       
  /**
   * Access to value base on index.
   * @param index The position in list.
   * @exception NoSuchNodeException if index is < 0 or index >= getItemListLength()
   */
  public Node itemAt(long index) throws NoSuchNodeException {
    
    if( index >= itemList.size() || index < 0){
      String err = ("No such node exists.");
      throw new NoSuchNodeException(err);
    }

    return itemList.item( index );
  }

  /**
   * The number of object in this list.
   * @return list size.
   *
   */
  public long getItemListLength() { return nameSpace.size(); }


  /**
   * Give NodeEnumerator base on the sequential list of values.
   */
  protected NodeEnumerator getListEnumerator() {
    return itemList.getEnumerator();
  }

  /**
   * Copy another list.
   */
  protected void initialize( ParseNodeTable list ) {
    if( list == null ) return;
    itemList = new ParseNodeArray(list.itemList);

    Hashtable ht = list.nameSpace;
    if( ht != null ){
      Enumeration e = ht.keys();
      Object v = null;

      for( ; e.hasMoreElements(); )
	{
	  String key = (String)e.nextElement();
	  nameSpace.put( key, ht.get(key) );
	}
    }
  }
  protected Hashtable nameSpace = new Hashtable();
  protected ParseNodeArray itemList = new ParseNodeArray();

  /************************************************************************
  ** Namespace Operations (not a complete set):
  ************************************************************************/

  public boolean isCaseSensitive() { return caseSensitive; }
  public String cannonizeName(String name) {
    return caseSensitive? name.toLowerCase() : name;
  }
}
