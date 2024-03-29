////// MathUtil.java: Mathematical Utilities
//	$Id: MathUtil.java,v 1.9 2001-04-03 00:05:01 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package org.risource.dps.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.output.*;

import org.risource.ds.Table;
import org.risource.ds.List;
import org.risource.ds.Association;

import java.util.Enumeration;

/**
 * Mathematical utilities.
 *
 *	A list is normally handled as an Enumeration, and a number as
 *	an Association between a Node and its value.
 *
 * @version $Id: MathUtil.java,v 1.9 2001-04-03 00:05:01 steve Exp $
 * @author steve@rii.ricoh.com
 *
 * @see org.risource.ds.Association
 * @see java.util.Enumeration
 */

public class MathUtil {

  /************************************************************************
  ** Attribute Conversion:
  ************************************************************************/

  public static Association getNumeric(ActiveAttrList atts, String name,
				       String dflt) {
    String v = atts.getAttribute(name);
    if (v == null) {
      if (dflt == null) return null;
      else v = dflt;
    }
    Association a = Association.associateNumeric(v, v);
    return a.isNumeric()
      ? a
      : (dflt == null)? null : Association.associateNumeric(null, dflt);
  }

  public static int getInt(ActiveAttrList atts, String name, int dflt) {
    String v = atts.getAttribute(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? (int)a.longValue() : dflt;
  }

  public static long getLong(ActiveAttrList atts, String name, long dflt) {
    String v = atts.getAttribute(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? a.longValue() : dflt;
  }

  public static double getDouble(ActiveAttrList atts, String name,
				 double dflt) {
    String v = atts.getAttribute(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? a.doubleValue() : dflt;
  }

  /************************************************************************
  ** Input Conversion:
  ************************************************************************/

  /** Return a numeric Association between a node and its numeric value. 
   *	Return null if the node contains no numeric text.
   */
  public static Association getNumeric(ActiveNode n) {
    Association a = Association.associateNumeric(n, ListUtil.getFirstWord(n));
    return (a.isNumeric())? a : null;
  }

  /** Return a numeric Association between an object and its numeric value. 
   *	Return null if the object contains no numeric text.
   */
  public static Association getNumeric(Object o) {
    Association a = Association.associateNumeric(o, o.toString());
    return (a.isNumeric())? a : null;
  }

  /** Return a list of numeric Associations.  Recursively descends into
   *  	nodes with children, and splits text nodes containing whitespace. 
   *	Most useful for extracting <em>all</em> numbers from a piece of a 
   *	document.
   */
  public static Enumeration getNumbers(ActiveNodeList nl) {
    List l = new List();
    Enumeration items = ListUtil.getTextItems(nl);
    while (items != null && items.hasMoreElements()) {
      Association a = getNumeric((Node)items.nextElement());
      if (a != null) l.push(a);
    }
    return l.elements();
  }

  /** Return a list of numeric Associations.  Splits text nodes containing
   *	whitespace, but associates non-text markup with the numeric value of
   *	their first text item.  Most useful for sorting nodes numerically.
   */
  public static List getNumericList(ActiveNodeList nl) {
    List l = new List();
    Enumeration items = ListUtil.getListItems(nl);
    while (items != null && items.hasMoreElements()) {
      Association a = getNumeric((Node)items.nextElement());
      if (a != null) l.push(a);
    }
    return l;
  }

  /** Return an enumeration of numeric Associations.  
   */
  public static Enumeration getNumericItems(ActiveNodeList nl) {
    return getNumericList(nl).elements();
  }

  

  /************************************************************************
  ** Output Conversion:
  ************************************************************************/

  /** Return the string representation of a floating-point number, 
   *	with digits after the decimal point.
   */
  public static String numberToString(double number, int digits)
  {
   if(digits < 0) return  java.lang.Double.toString(number);

  // round it off
     double factor = Math.pow(10,digits);
     long val = Math.round(number * factor);
     
     String s =  java.lang.Long.toString(val);
     while(s.length() < digits){
       s = "0"+s;
     }
     String result;
     
     int l = s.length();
     if(l>digits){
       result = s.substring(0,l-digits);
     }
     else{
       result = "0";
     }
     if(digits>0){
       result += "." + s.substring(l-digits);
     }
     return result;
  }



  /** Return the string representation of a long integer with a given base .*/
  public static String numberToString(long number, int base)
  {
    if (base <= 0) return Long.toString(number);
    else return Long.toString(number, base);
  }
  
  /** Return the string representation of a long integer with a given base,
   *  optionally padded on the left with zero's to a given minimum length.
   */
  public static String numberToString(long number, int base, int pad)
  {
    if (pad <= 0) return numberToString(number, base);
    if (number < 0) {
      return "-" + numberToString(-number, base, pad - 1);
    }
    String s = numberToString(number, base);
    while (s.length() < pad) { s = '0' + s; }
    return s;
  }
  

  /************************************************************************
  ** 
  ************************************************************************/


}
