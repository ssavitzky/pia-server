// Nil.java
// Nil.java,v 1.2 1999/03/01 23:47:09 pgage Exp

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

/** Nil is a class used to represent "null" in places where you can't
 *	return a null for some reason.   There is only one instance, 
 *	accessed as <code>Nil.value</code>. */
public final class Nil {

  public String toString() { return ""; };

  /** There is only one instance of Nil, so the constructor is private. */
  private Nil() {};

  /** This is the unique instance of Nil */
  public final static Nil value = new Nil();
}
