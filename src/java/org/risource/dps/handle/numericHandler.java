////// numericHandler.java: <numeric> Handler implementation
//	$Id: numericHandler.java,v 1.9 1999-11-15 18:56:38 steve Exp $

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


package org.risource.dps.handle;

import org.w3c.dom.NodeList;

import org.risource.ds.SortTree;
import org.risource.ds.List;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;

import org.risource.ds.Association;
import java.util.Enumeration;

/**
 * Handler for &lt;numeric&gt;....&lt;/&gt;  <p>
 *
 * @version $Id: numericHandler.java,v 1.9 1999-11-15 18:56:38 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class numericHandler extends GenericHandler {

  protected boolean integerOp  = false;
  protected boolean extendedOp = false;
  protected int     digits     = -1;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;numeric&gt; node.  Default is just output the numbers. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    while (args.hasMoreElements()) {
      boolean intOp  = true;
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
	putText(out, cxt, MathUtil.numberToString(iresult, base));
      } else {
	fresult = a.doubleValue();
	putText(out, cxt, MathUtil.numberToString(fresult, precision));
      }
    }
  }

  /** This does the parse-time dispatching. */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "sum", "op")) 	 return numeric_sum.handle(e);
    if (dispatch(e, "difference", "op")) return numeric_difference.handle(e);
    if (dispatch(e, "diff", "op"))	 return numeric_difference.handle(e);
    if (dispatch(e, "product", "op")) 	 return numeric_product.handle(e);
    if (dispatch(e, "prod", "op")) 	 return numeric_product.handle(e);
    if (dispatch(e, "quotient", "op")) 	 return numeric_quotient.handle(e);
    if (dispatch(e, "quot", "op")) 	 return numeric_quotient.handle(e);
    if (dispatch(e, "remainder", "op"))	 return numeric_remainder.handle(e);
    if (dispatch(e, "rem", "op"))	 return numeric_remainder.handle(e);
    if (dispatch(e, "sort", "op"))    	 return numeric_sort.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public numericHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  numericHandler(ActiveElement e) {
    this();
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    digits       = MathUtil.getInt(atts, "digits", -1);
    extendedOp   = atts.hasTrueAttribute("extended");
    long modulus = MathUtil.getLong(atts, "modulus", 0);

    integerOp    = extendedOp || (modulus != 0)
      		   || atts.hasTrueAttribute("integer");

    // customize for element. integer, extended, modulus
  }
}

class numeric_sum extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult += a.doubleValue();
      if (intOp) {
	iresult += a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_sum(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_sum(e); }
}

class numeric_difference extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult -= a.doubleValue();
      if (intOp) {
	iresult -= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_difference(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_difference(e); }
}

class numeric_product extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = true;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult *= a.doubleValue();
      if (intOp)  {
	iresult *= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_product(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_product(e); }
}

class numeric_quotient extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = integerOp;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult /= a.doubleValue();
      if (intOp) {
	iresult /= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_quotient(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_quotient(e); }
}


class numeric_power extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = true;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = Math.pow(fresult, a.doubleValue());
      if (intOp) {
	iresult = (long) Math.pow(iresult, a.longValue());
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_power(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_power(e); }
}


class numeric_remainder extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;
    int precision = (digits < 0)? MathUtil.getInt(atts, "digits", -1): digits;
    long modulus  =  MathUtil.getLong(atts, "modulus", 0);
    int base	  =  MathUtil.getInt(atts, "base", -1);

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = Math.IEEEremainder(fresult, a.doubleValue());
      if (intOp) {
	iresult %= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, MathUtil.numberToString(iresult, base));
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, precision));
    }
  }
  public numeric_remainder(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_remainder(e); }
}

class numeric_sort extends numericHandler {
  protected boolean reverse  = false;
  protected boolean caseSens = false;
  protected boolean pairs    = false;

  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    Enumeration args = MathUtil.getNumbers(content);

    SortTree sorter = new SortTree();
    Association a;
    while(args.hasMoreElements()) {
      a = (Association)args.nextElement();
      sorter.insert(a, true);
    }
    List resultList = new List();
    if(reverse)
      sorter.descendingValues(resultList);
    else
      sorter.ascendingValues(resultList);

    putEnum(out, resultList.elements());
  }

  public numeric_sort(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    reverse  = atts.hasTrueAttribute("reverse");
    caseSens = atts.hasTrueAttribute("case");
    pairs    = atts.hasTrueAttribute("pairs");
  }
  static Action handle(ActiveElement e) { return new numeric_sort(e); }
}











