// Table.java
// $Id: Table.java,v 1.3 1999-03-12 19:28:48 steve Exp $

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

import java.util.Hashtable;
import java.util.Enumeration;

public class Table extends Hashtable implements Stuff, Tabular {

  /************************************************************************
  ** Stuff interface:
  ************************************************************************/

  /** The number of indexed items. */
  public int nItems() {
    return 0;
  }

  /** Access an individual item */
  public Object at(int i) {
    return null;
  }

  /** Replace an individual item <em>i</em> with value <em>v</em>. */
  public Stuff at(int i, Object v) {
    // === maybe this should throw an exception ===
    return this;
  }

  /** Remove and return the last item. */
  public Object pop() {
    return null;
  }

  /** Remove and return the first item. */
  public Object shift() {
    return null;
  }

  /** Append a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff push(Object v) {
    return this;
  }

  /** Prepend a new value <em>v</em>.  
   *	Returns the modified Stuff, to simplify chaining. */
  public Stuff unshift(Object v) {
    return this;
  }

  /** Access a named attribute */
  public Object at(String a) {
    return (a == null)? a : get((Object)a);
  }

  /** Add or replace an attribute */
  public Stuff at(String a, Object v) {
    if (a == null || v == null) 
      System.err.println("Table: attempting to put a="+a+", v="+v);
    super.put(a, v);
    return this;
  }

  /** Test for presence of  an individual item */
  public boolean has(String a) {
    return containsKey(a);
  }

  /** Return an array of all the attribute keys. */
  public List keyList() {
    return new List(keys());
  }


  /** Return true if the Stuff is a pure hash table, with no items */
  public boolean isTable() {
    return true;
  }

  /** Return true if the Stuff is a pure list, with no attributes. */
  public boolean isList() {
    return false;
  }

  /** Return true if the Stuff is an empty list. */
  public boolean isEmpty() {
    return true;
  }

  /** Return true if the Stuff is pure text, equivalent to a 
   * 	singleton list containing a String. */
  public boolean isText() {
    return false;
  }


  /************************************************************************
  ** Tabular interface:
  ************************************************************************/

  // in HashTable: public int size();
  // in HashTable: public Enumeration keys();

  public Object get(String key) { return get((Object)key); }
  public void put(String key, Object value) { super.put(key, value); }
  
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Table() {
    super();
  }

  public Table(int initialCapacity) {
    super(initialCapacity < 16? 16 : initialCapacity);
  }

  public Table(Table t) {
   this((t.size()>0)?t.size():1) ;
   append(t);
  }

  public Table(List l) {
    this(l.nItems());
    append(l);
  }

  public Table(Enumeration e) {
    this();
    append(e);
  }

  public Table(Enumeration e, boolean lowercase) {
    this();
    append(e, lowercase);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new Table(this);
  }

  /** Append a table */
  public void append(Table t) {
    Enumeration e = t.keys();

    while (e.hasMoreElements()) {
      Object k = e.nextElement();
      put(k, t.get(k));
    }
  }

  /** Append a List. */
  public void append(List l) {
    append(l.elements());
  }

  /** Append an Enumeration. */
  public void append(Enumeration e) {
    while (e.hasMoreElements()) {
      Object v = e.nextElement();
      at(v.toString(), v);
    }
  }

  /** Append an Enumeration, optionally lowercasing the keys. */
  public void append(Enumeration e, boolean lowercase) {
    if (!lowercase) {
      append(e);
      return;
    }
    while (e.hasMoreElements()) {
      Object v = e.nextElement();
      at(v.toString().toLowerCase(), v);
    }
  }

  /** Append key, value pairs from an enumeration. */
  public void appendPairs(Enumeration e) {
    while (e.hasMoreElements()) {
      Object k = e.nextElement();
      Object v = e.nextElement();
      at(k.toString(), v);
    }
  }

  /** Append a list of [key, value...] pairs. */
  public void appendPairs(List l) {
    appendPairs(l.elements());
  }

}
