////// weekdayHandler.java: <weekday> Handler implementation
//	$Id: weekdayHandler.java,v 1.1 1999-10-06 23:39:13 bill Exp $

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

import org.w3c.dom.*;

import org.risource.ds.List;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.Package;
import org.risource.pia.Pia;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.*;


import org.risource.dps.tree.TreeElement;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeFragment;
import org.risource.dps.tree.TreeCharData;

import org.risource.ds.Association;
import java.util.Enumeration;
import java.lang.String;
import java.lang.StringBuffer;


/**
 * Handler for &lt;weekday&gt; &lt;/&gt;  This tag prints
 * the weekday associated with a given date (or nothing, if incorrect).
 * <br>	
 *
 * @version $Id: weekdayHandler.java,v 1.1 1999-10-06 23:39:13 bill Exp $
 * @author steve@rsv.ricoh.com
 */

public class weekdayHandler extends GenericHandler {

    List printList = null;

	
    int indentIncrement = 4;
  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;weekday&gt; node.  Requires either a <unixdate> tag
      or all three of <date>, <month>, and <year> tags.  Expands to the
      named day-of-week with no attributes, or the named month (with 
      the "monthname" attribute nonempty), or the last day of month (with
      "lastdate" attribute nonempty), or the (negative) day to start 
      printing a calandar (with "startsun").

   */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {



    // get subelements
    int date = -1, month = -1, year = -1;
    long unixdate = -1;
    for (int i = 0; i < content.getLength(); ++i) {

	
	String name;
	if (	content.activeItem(i).getNodeType() == Node.ELEMENT_NODE){
	    name = content.activeItem(i).getNodeName();
	}
	else
	    continue;

	Enumeration en;
	Association as;

	if (name.equalsIgnoreCase("date")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    date = (int)( as.longValue() );
	    }
	}
	if (name.equalsIgnoreCase("month")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    // let people input month at 1-12, not 0-11
		    month = (int)( as.longValue() ) - 1;
	    }
	}
	if (name.equalsIgnoreCase("year")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    year = (int)( as.longValue() );
	    }
	}
	if (name.equalsIgnoreCase("unixdate")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    unixdate =  as.longValue();
	    }
	}

    }

    // got all the available tags; now figure out what to return
    if (unixdate < 0 && (date < 0 ||month < 0 || year < 0 )){
	reportError(in, cxt,"weekday tag has missing date/month/year");
	return;
    }
    
    // create the calendar, either by date/month/year or unixdate
    GregorianCalendar theDay = new GregorianCalendar(year, month, date);
    if (unixdate >= 0){
	Date theTime = new Date( unixdate*1000 ); //unix gets seconds, not msec
	theDay.setTime( theTime );
	month = theDay.get(Calendar.MONTH);
	date = theDay.get(Calendar.DAY_OF_MONTH);
	year = theDay.get(Calendar.YEAR);
    }
    
    // Package pack = Package.getPackage("java.lang");
    // System.out.println("Version: " + pack.getImplementationVersion() );


    //  Date theTime = theDay.getTime();
    //theDay.setTime(theTime);

    // these checking functions should really be getActual[Minimum/Maximum],
    // but those aren't implemented methods yet.
    if (date < theDay.getActualMinimum(Calendar.DAY_OF_MONTH) ||
	date > theDay.getActualMaximum(Calendar.DAY_OF_MONTH) ||
	month < theDay.getActualMinimum(Calendar.MONTH) ||
	month > theDay.getActualMaximum(Calendar.MONTH) ){
	reportError(in, cxt,"weekday tag has illegal date/month/year");
	return;
    }
    int maxDay = theDay.getActualMaximum(Calendar.DAY_OF_MONTH);
    int wday = (theDay.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;
    List dayNames = List.split("Sunday Monday Tuesday Wednesday"
				    + " Thursday Friday Saturday");
    long longDay = theDay.getTime().getTime() / 86400000;

    String wkday = (String)dayNames.at( wday );
    // done with compuatation

    // look at attributes for weekdayting
    if (atts.hasTrueAttribute("lastdate") ){
	ActiveNode outText;
	outText = new TreeText( maxDay );
	out.putNode(outText);
	return;
    }
    if (atts.hasTrueAttribute("longday") ){
	ActiveNode outText;
	outText = new TreeText( longDay );
	out.putNode(outText);
	return;
    }
    if (atts.hasTrueAttribute("startsun") ){
	// find which day (negative!) to start Sunday on to get
	// the first of the month on the right weekday (a printing issue)
	theDay.set(year, month, 1);
	int firstSun = 2 - theDay.get(Calendar.DAY_OF_WEEK);
	ActiveNode outText;
	outText = new TreeText( firstSun );
	out.putNode(outText);
	return;
    }
    if (atts.hasTrueAttribute("monthname") ){
	List monthNames= List.split("January February March April"
				     + " May June July August"
				     + " September October November December");

	String monthName = (String)monthNames.at( month );
	ActiveNode outText;
	outText = new TreeText( monthName );
	out.putNode(outText);
	return;
    }

    if (atts.hasTrueAttribute("monthnum") ){
	ActiveNode outText;
        int outMonth = month + 1;
	outText = new TreeText( outMonth );
	out.putNode(outText);
	return;
    }

    // default is showing weekday
    ActiveNode outText;
    outText = new TreeText( wkday );
    out.putNode(outText);

  }
    

}

