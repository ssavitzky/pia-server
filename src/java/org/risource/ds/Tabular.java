// Tabular.java -- interface for tabular data
// 	$Id: Tabular.java,v 1.4 1999-04-23 00:22:49 steve Exp $

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

import java.util.Enumeration;

/** This is the interface for objects in the PIA that behave like
 *	<code>Map</code>s or <code>HashTable</code>s.
 *
 * <p> The names of some of the methods, e.g. <code>get</code>,
 * 	<code>put</code>, and <code>size</code>, come from Java's
 * 	<code>HashTable</code> and <code>Map</code>. This is in contrast with
 * 	the older <code>Stuff</code>, which is derived from Perl.
 *
 * @version $Id: Tabular.java,v 1.4 1999-04-23 00:22:49 steve Exp $
 * @author steve@rsv.ricoh.com
 * @see java.util.HashTable
 * @see org.risource.ds.Stuff
 */
public interface Tabular {
  /** Access an individual item by name. */
  public Object get(String key);

  /** Replace an individual named item with value <em>v</em>. */
  public void put(String key, Object v);

  /** Return an enumeration of all the  keys. */
  public Enumeration keys();
}

