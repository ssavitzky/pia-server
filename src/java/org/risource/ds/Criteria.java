////// Criteria.java:  List of match criteria
//	Criteria.java,v 1.9 1999/03/01 23:47:05 pgage Exp

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

/**
 *  A Criteria performs a matching operation on a Features object.
 *	It consists of a List the elements of which are Criterion objects.
 */
public class Criteria extends List {

  /** Match the features in parent (which we need for computing values). */
  public boolean match(Features features, HasFeatures parent) {
    for (int i = 0; i < nItems(); ++i) {
      Criterion c = (Criterion)at(i);
      // org.risource.pia.Pia.debug(this, "         "+c.toString()+"?");
      if (! c.match(features, parent)) {
	// org.risource.pia.Pia.debug(this, "         failed");
	return false;
      }
    }
    return true;
  }

  public Criteria() {
    super();
  }

  /** Initialize from a space-separated list of name=value pairs. 
   *	A name without a value matches any non-null value; nothing after the
   *	"=" matches null, "", or False.
   */
  public Criteria(String str) {
      this(List.split(str));
  }

  /** Initialize from a list of String or Criterion objects.  Anything else
   * 	is silently ignored. */
  public Criteria(List l) {
    for (int i = 0; i < l.nItems(); ++i) {
      Object o = l.at(i);
      if (o instanceof String) push(Criterion.toMatch(o.toString()));
      else if (o instanceof Criterion) push(o);
    }
  }

  /** Convert to a space-separated list of name=value pairs. */
  public String toString() {
    String s = "";
    java.util.Enumeration e = elements();
    while (e.hasMoreElements()) {
      s += e.nextElement().toString();
      if (e.hasMoreElements()) s += " ";
    }
    return s;
  }
}
