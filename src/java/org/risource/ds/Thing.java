// Thing.java
// $Id: Thing.java,v 1.3 1999-03-12 19:28:51 steve Exp $

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

package org.risource.ds;

public class Thing implements Stuff {

  /************************************************************************
  ** Components:
  ************************************************************************/

  Stuff items;
  Stuff attrs;

  /************************************************************************
  ** Stuff interface:
  ************************************************************************/

  /** The list of indexed list items. 
   *	This may be the same as <code>this</code> if the Stuff is a pure 
   *	list with no attributes.
   */
  public Stuff content() {
    return items;
  }

  /** The number of indexed items. */
  public int nItems() {
    return items == null? 0 : items.nItems();
  }

  /** Access an individual item */
  public Object at(int i) {
    return (items == null)? null : items.at(i);
  }

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  public void at(int i, Object v) {
    if (items == null) {
      items = new List();
    }
    items.at(i, v);
  }

  /** Remove and return the last item. */
  public Object pop() {
    return (items == null)? null : items.pop(i);
  }

  /** Remove and return the first item. */
  public Object shift() {
    return (items == null)? null : items.shift(i);
  }

  /** Append a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff push(Object v) {
    if (items == null) {
      items = new List();
    }
    items.push(i, v);
  }

  /** Prepend a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff unshift(Object v) {
    if (items == null) {
      items = new List();
    }
    items.unshift(i, v);
  }

  /** Access a named attribute */
  public Object at(String a) {
    return (attrs == null)? null : attrs.at(a);
  }

  /** Add or replace an attribute */
  public Stuff at(String a, Object v){
    if (attrs == null) {
      atrrs = new Table();
    }
    items.at(a, v);
  }

  /** Return an array of all the attribute keys. */
  public String[] keyList() {
    return  (attrs == null)? null : attrs.keyList(a);
  }


  /** Return true if the Stuff is a pure hash table, with no items */
  public boolean isEmpty() {
    return items == null || items.isEmpty();
  }
  /** Return true if the Stuff is a pure list, with no attributes. */
  public boolean isList() {
    return attrs == null;
  }

  /** Return true if the Stuff is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return false;
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public Thing() {

  }

  public Thing(Thing t) {
    if (t.items != null) items = t.items.clone();
    if (t.attrs != null) attrs = t.attrs.clone();
  }

  public Object clone() {
    return new Thing(this);
  }
}
