// HttpDate.java
// $Id: HttpDate.java,v 1.1 1999-03-12 19:50:53 pgage Exp $$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html


package org.w3c.www.http;

import java.util.*;

public class HttpDate extends BasicValue {
    protected static String days[] = { "Sun", "Mon", "Tue", "Wed",
				       "Thu" , "Fri", "Sat" };
    protected static String months[] = { "Jan", "Feb", "Mar", "Apr",
					 "May", "Jun", "Jul", "Aug",
					 "Sep", "Oct", "Nov", "Dec" };
					 
    protected Long date = null;
    protected int  tz   = -1;

    protected void parse() {
	ParseState ps = new ParseState();
	ps.ioff   = roff;
	ps.bufend = rlen;
	date = new Long(HttpParser.parseDateOrDeltaSeconds(raw, ps));
    }

    protected void updateByteValue() {
	// Compute the time zone offset, first call only.
	if ( tz == -1 ) {
	    tz = new Date().getTimezoneOffset();
	}
	// Dump the date, according to HTTP/1.1 prefered format
	HttpBuffer buf = new HttpBuffer();
	Date d = new Date(date.longValue()+(tz*60*1000));
	buf.append(days[d.getDay()]);
	buf.append(','); buf.append(' ');
	buf.appendInt(d.getDate(), 2, (byte) '0');
	buf.append(' ');
	buf.append(months[d.getMonth()]);
	buf.append(' ');
	buf.appendInt(d.getYear()+1900, 2, (byte) '0');
	buf.append(' ');
	buf.appendInt(d.getHours(), 2, (byte) '0');
	buf.append(':');
	buf.appendInt(d.getMinutes(), 2, (byte) '0');
	buf.append(':');
	buf.appendInt(d.getSeconds(), 2, (byte) '0');
	buf.append(" GMT");
	raw  = buf.getByteCopy();
	roff = 0;
	rlen = raw.length;
    }

    /**
     * Get the date value.
     * @return A Long giving the date as a number of mmilliseconds since epoch.
     */

    public Object getValue() {
	validate();
	return date;
    }

    /**
     * Set this date object value.
     * @param date The new date value, as the number of milliseconds since
     * epoch.
     */

    public void setValue(long date) {
	if ( date == this.date.longValue() )
	    return ;
	invalidateByteValue();
	this.date = new Long(date);
	this.isValid = true ;
    }
    
    HttpDate(boolean isValid, long date) {
	this.isValid = isValid;
	this.date    = new Long(date);
    }

    HttpDate() {
	this.isValid = false;
    }

    public static void main(String args[]) {
	Date date = new Date();
	System.out.println("tz offset: "+date.getTimezoneOffset());
	System.out.println("local: "+date.toString());
	System.out.println("gmt  : "+date.toGMTString());
	long ld = date.getTime();
	ld += (date.getTimezoneOffset()*60*1000);
	System.out.println("hacke: "+new Date(ld));
    }

}
