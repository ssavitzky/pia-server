////// weekdayHandler.java: <weekday> Handler implementation
//	$Id: weekdayHandler.java,v 1.3 1999-10-08 16:16:19 steve Exp $

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
 * Contributor(s): softky@rsv.ricoh.com, steve@rsv.ricoh.com
 *
 ***************************************************************************** 
*/


package org.risource.dps.handle;

import org.w3c.dom.*;

import org.risource.ds.List;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.risource.util.Julian;

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
 * Handler for &lt;weekday&gt; &lt;/&gt;  This tag outputs
 * the weekday associated with a given date (or nothing, if incorrect).
 *
 * <p>	This code has been hacked by Steve Savitzky so that it works
 *	correctly under Java version 1.1; Calendar under 1.1 fails to
 *	recalculate the day-of-the-week after resetting the date.
 *	The rewrite involves 35-year-old Julian Day code originally by
 *	Abraham Savitzky.
 *
 * @version $Id: weekdayHandler.java,v 1.3 1999-10-08 16:16:19 steve Exp $
 * @author softky@rsv.ricoh.com
 * @see org.risource.util.Julian
 */

public class weekdayHandler extends GenericHandler {

  static int maxday[] = { 
    /* jan feb mar apr may jun jul aug sep oct nov dec */
       31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
  };

  /** Compute the maximum day of the given month in the given year. 
   *
   *<p>	Note that this computation is accurate only for the Gregorian
   *	calendar, not the Julian.  It substitutes for the Java 1.2
   *	function getActualMaximum(Calendar.DAY_OF_MONTH) on 
   *	GregorianCalendar.
   *
   *<p>  === remove maxDayOfMonth when Java 1.2 is universally used.
   */
  static int maxDayOfMonth(int month, int year) {
    /** NOTE: January is 0, February is 1 */
    return (month != 1)? maxday[month]
      : (year % 400 == 0)? 29
      : (year % 100 == 0)? 28
      : (year % 4   == 0)? 29
      : 28;
  }

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
	if (content.activeItem(i).getNodeType() == Node.ELEMENT_NODE){
	  name = content.activeItem(i).getNodeName();
	} else {
	  continue;
	}

	Enumeration en;
	Association as;

	if (name.equalsIgnoreCase("date")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    date = (int)( as.longValue() );
	    }
	} else
	if (name.equalsIgnoreCase("month")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    // let people input month at 1-12, not 0-11
		    month = (int)( as.longValue() ) - 1;
	    }
	} else
	if (name.equalsIgnoreCase("year")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    year = (int)( as.longValue() );
	    }
	} else
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
    Calendar theDay = Calendar.getInstance();
    theDay.clear();
    theDay.set(year, month, date);
    if (unixdate >= 0){
	Date theTime = new Date( unixdate*1000 ); //unix gets seconds, not msec
	theDay.clear();
	theDay.setTime( theTime );
	month = theDay.get(Calendar.MONTH);
	date = theDay.get(Calendar.DAY_OF_MONTH);
	year = theDay.get(Calendar.YEAR);
    }

    // Calendar in 1.1.3 is totally broken -- there is no way to recompute the 
    // day-of-the-week field after setting the year/month/day.

    // these checking functions should really be getActual[Minimum/Maximum],
    // but those aren't implemented methods yet.
    int maxDay = maxDayOfMonth(month, year); 
    // int maxDay = theDay.getActualMaximum(Calendar.DAY_OF_MONTH)
    int minDay = 1;            // theDay.getActualMaximum(Calendar.DAY_OF_MONTH)

    if (date < minDay || date > maxDay ||
	month < 0 /* theDay.getActualMinimum(Calendar.MONTH) */ ||
	month > 11 /* theDay.getActualMaximum(Calendar.MONTH) */ ){
	reportError(in, cxt,"weekday tag has illegal date/month/year");
	return;
    }

    /** Note: Julian uses natural origins: Jan. 1 is 1/1 */
    long jday = Julian.jday(year, month + 1, date);
    int wday = Julian.weekday(jday);

    //int wday = (theDay.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;
    List dayNames = List.split("Sunday Monday Tuesday Wednesday"
				    + " Thursday Friday Saturday");
    long longDay = theDay.getTime().getTime() / 86400000;

    String wkday = (String)dayNames.at( wday );
    // done with compuatation

    // look at attributes for weekday
    if (atts.hasTrueAttribute("lastdate") ){
	ActiveNode outText;
	outText = new TreeText( maxDay );
	out.putNode(outText);
	return;
    } else
    if (atts.hasTrueAttribute("longday") ){
	ActiveNode outText;
	outText = new TreeText( longDay );
	out.putNode(outText);
	return;
    } else
    if (atts.hasTrueAttribute("julian") ){
	ActiveNode outText;
	outText = new TreeText( jday );
	out.putNode(outText);
	return;
    } else
    if (atts.hasTrueAttribute("startsun") ){
	// find which day (negative!) to start Sunday on to get
	// the first of the month on the right weekday (a printing issue)
      theDay.set(year, month, 1);
      //theDay = new GregorianCalendar(year, month, 1);
      //int firstSun = 2 - theDay.get(Calendar.DAY_OF_WEEK);
      int firstSun = 1 - Julian.weekday(Julian.jday(year, month+1, 1));
	ActiveNode outText;
	outText = new TreeText( firstSun );
	out.putNode(outText);
	return;
    } else
    if (atts.hasTrueAttribute("monthname") ){
	List monthNames= List.split("January February March April"
				     + " May June July August"
				     + " September October November December");

	String monthName = (String)monthNames.at( month );
	ActiveNode outText;
	outText = new TreeText( monthName );
	out.putNode(outText);
	return;
    } else
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

