////// dateHandler.java: <date> Handler implementation
//	$Id: dateHandler.java,v 1.4 2001-01-11 23:37:17 steve Exp $

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
 * Contributor(s): softky@rii.ricoh.com, steve@rii.ricoh.com
 *
 ***************************************************************************** 
*/


package org.risource.dps.handle;

import org.w3c.dom.*;

import org.risource.ds.List;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.SimpleTimeZone;

import java.text.SimpleDateFormat;

import org.risource.util.Julian;

import org.risource.pia.Pia;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.output.*;


import org.risource.dps.tree.TreeNodeList;
import org.risource.dps.tree.TreeElement;
import org.risource.dps.tree.TreeText;
import org.risource.dps.tree.TreeFragment;
import org.risource.dps.tree.TreeCharData;

import org.risource.ds.Association;
import java.util.Enumeration;
import java.lang.String;
import java.lang.StringBuffer;


/**
 * Handler for &lt;date&gt; &lt;/&gt;  With no attributes, outputs a 
 *	Unix-compatible date string.  Dispatches on the name of the tag
 *	as well as the attribute.
 *
 * <p>	This code has been hacked by Steve Savitzky so that it works
 *	correctly under Java version 1.1; Calendar under 1.1 fails to
 *	recalculate the day-of-the-week after resetting the date.
 *	The rewrite involves 35-year-old Julian Day code originally 
 *	written in FORTRAN IV by Abraham Savitzky.
 *
 * @version $Id: dateHandler.java,v 1.4 2001-01-11 23:37:17 steve Exp $
 * @author softky@rii.ricoh.com
 * @see org.risource.util.Julian
 */
public class dateHandler extends GenericHandler {

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

  /** Origin for Unix dates (``The Epoch'').
   *	We do our own date calculations, because some versions of Java
   *	get it wrong on some versions of libc. 
   */
  static long epoch = Julian.jday(1970, 1, 1);

  static long seconds_per_day = 60 * 60 * 24;

  List monthNames = List.split("January February March April"
			       + " May June July August"
			       + " September October November December");

  List dayNames = List.split("Sunday Monday Tuesday Wednesday"
			     + " Thursday Friday Saturday");

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;date&gt; node.  Requires either a <seconds> tag
      or all three of <date>, <month>, and <year> tags.  Expands to the
      named day-of-week with no attributes, or the named month (with 
      the "monthname" attribute nonempty), or the last day of month (with
      "lastday" attribute nonempty), or the (negative) day to start 
      printing a calandar (with "startsun").

   */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {

    String tag = in.getTagName();

    // The current time, and variables for the date.

    int day = -1, month = -1, year = -1;
    Date now = new Date();
    long mstime = now.getTime(); 	// Java time in milliseconds
    long seconds = mstime/1000; 	// Unix time in seconds
    long jday = epoch + seconds / seconds_per_day;
    boolean dmy = false;	// day/month/year specified

    // get subelements
    // 	 We're looking for either <day> + <month> + <year>,
    //	 <seconds>, <mstime>, or <julian>
    if (content == null) content = new TreeNodeList();
    for (int i = 0; i < content.getLength(); ++i) {
	
	String name;
	if (content.activeItem(i).getNodeType() == Node.ELEMENT_NODE){
	  name = content.activeItem(i).getNodeName();
	} else {
	  continue;
	}

	Enumeration en;
	Association as;

	if (name.equalsIgnoreCase("day")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    day = (int)( as.longValue() );
	    }
	    seconds = -1;
	} else
	if (name.equalsIgnoreCase("month")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    // let people input month at 1-12, not 0-11
		    month = (int)( as.longValue() ) - 1;
	    }
	    seconds = -1;
	} else
	if (name.equalsIgnoreCase("year")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    year = (int)( as.longValue() );
	    }
	    seconds = -1;
	} else
	if (name.equalsIgnoreCase("seconds")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    seconds =  as.longValue();
		mstime = seconds * 1000;
		jday = epoch + seconds / seconds_per_day;
	    }
	} else
	if (name.equalsIgnoreCase("mstime")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    mstime =  as.longValue();
		seconds = mstime / 1000;
		jday = epoch + seconds / seconds_per_day;
	    }
	} else
	if (name.equalsIgnoreCase("julian")){
	    en = MathUtil.getNumbers( content.activeItem(i).getContent() );
	    while( en != null && en.hasMoreElements() ){
		as = (Association)en.nextElement();
		if ( as.isIntegral() )
		    jday =  as.longValue();
		seconds  = (jday - epoch) * seconds_per_day;
		mstime   = seconds / 1000;
	    }
	}
    }

    // got all the available tags; now figure out what to return
    if (seconds < 0 && (day < 0 || month < 0 || year < 0 )){
	reportError(in, cxt,"date tag has missing day/month/year");
	return;
    }
    
    // create the calendar, either by day/month/year or seconds
    Calendar theDay = null;
    if (seconds >= 0){
	Date theTime = new Date( seconds*1000 ); //unix gets seconds, not msec
	theDay = new GregorianCalendar( );
	theDay.setTime(theTime);
	long j = (seconds / seconds_per_day) + epoch;
	month = Julian.month(j) - 1;
	day   = Julian.day(j);
	year  = Julian.year(j);
	if (cxt.getVerbosity() > 1) {
	  if (month != theDay.get(Calendar.MONTH)) 
	    System.err.println("Java computes wrong month");
	  if (day != theDay.get(Calendar.DAY_OF_MONTH)) 
	    System.err.println("Java computes wrong day");
	  if (year != theDay.get(Calendar.YEAR)) 
	    System.err.println("Java computes wrong year");
	  //System.out.println(" " + seconds + "=" + year + "/" + month + "/" + date);
	}
    } else {
      theDay = new GregorianCalendar(year, month, day);
      jday = Julian.jday(year, month + 1, day);
      seconds  = (jday - epoch) * seconds_per_day;
      mstime   = seconds * 1000;
      dmy = true;
    }

    // Calendar in 1.1.3 is totally broken -- there is no way to recompute the 
    // day-of-the-week field after setting the year/month/day.

    // these checking functions should really be getActual[Minimum/Maximum],
    // but those aren't implemented methods yet.
    int maxDay = maxDayOfMonth(month, year); 
    // int maxDay = theDay.getActualMaximum(Calendar.DAY_OF_MONTH)
    int minDay = 1;            // theDay.getActualMaximum(Calendar.DAY_OF_MONTH)

    if (day < minDay || day > maxDay ||
	month < 0 /* theDay.getActualMinimum(Calendar.MONTH) */ ||
	month > 11 /* theDay.getActualMaximum(Calendar.MONTH) */ ){
	reportError(in, cxt,"date tag has illegal day/month/year");
	return;
    }

    /** Note: Julian uses natural day and month numbering: Jan. 1 is 1/1 */
    //long jday = Julian.jday(year, month + 1, day);
    int wday = Julian.weekday(jday);

    //int wday = (theDay.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;
    // System.out.println(" " + javaDay + "=" + year + "/" + month + "/" + day);

    long longDay = jday - epoch;
    long javaday = theDay.getTime().getTime() / 86400000L;

    Date theDate = new Date(mstime);
    SimpleDateFormat fmt;

    if (javaday != longDay && cxt.getVerbosity() >= 0) {
      System.err.println("Warning: javaday=" + javaday + " longDay=" + longDay);
    }

    // done with compuatation.  Examine attributes for result.
    // === should have result="..."

    // look at attributes for date
    if (atts.hasTrueAttribute("lastday")) {
      ActiveNode outText;
      outText = new TreeText( maxDay );
      out.putNode(outText);
    } else if (atts.hasTrueAttribute("longday")) {
      ActiveNode outText;
      outText = new TreeText( longDay );
      out.putNode(outText);
    } else if (atts.hasTrueAttribute("julian")) {
      ActiveNode outText;
      outText = new TreeText( jday );
      out.putNode(outText);
    } else if (atts.hasTrueAttribute("startsun")) {
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
    } else if (atts.hasTrueAttribute("monthname") ){
      fmt = new SimpleDateFormat("MMMM");
      if (dmy) fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      putText(out, cxt, fmt.format(theDate));

     //String monthName = (String)monthNames.at( month );
     //ActiveNode outText;
     //outText = new TreeText( monthName );
     //out.putNode(outText);

    } else if (atts.hasTrueAttribute("monthnum") ){
      ActiveNode outText;
      int outMonth = month + 1;
      outText = new TreeText( outMonth );
      out.putNode(outText);
    } else if (atts.hasTrueAttribute("format")) {
      String format = atts.getAttribute("format");
      fmt = new SimpleDateFormat(format);
      putText(out, cxt, fmt.format(theDate));
    } else if (atts.hasTrueAttribute("gmt")) {
      fmt = new SimpleDateFormat("d MMM yyyy HH:mm:ss 'GMT'");
      fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      putText(out, cxt, fmt.format(theDate));
    } else if (tag.equals("weekday")) {
      fmt = new SimpleDateFormat("EEEE");
      if (dmy) fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      putText(out, cxt, fmt.format(theDate));
    } else {
      fmt = new SimpleDateFormat(dmy
				 ? "EEE MMM dd HH:mm:ss yyyy"
				 : "EEE MMM dd HH:mm:ss zzz yyyy");
      if (dmy) fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
      putText(out, cxt, fmt.format(theDate));
    }
  }
    

}

