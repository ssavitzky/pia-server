// Julian.java -- Julian date utilities
// $Id: Julian.java,v 1.2 2001-01-11 23:37:53 steve Exp $

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
 * Contributor(s): Abraham Savitzky
 *
 ***************************************************************************** 
*/


package org.risource.util;


/**
 * This class contains static functions that deal with Julian days.
 *
 * <p> Julian days were originally used in the astronomical community 
 *	because they greatly simplify time calculations.  They are of 
 *	interest in the Java community because many Java 1.1 implementations
 *	of GregorianCalendar are seriously broken and cannot perform 
 *	day-of-the-week calculations correctly.
 *
 * <p> Note that all calculations are carried out using Java's 64-bit long
 *	integers, because that precision is needed for the Julian day.  Note
 *	also that, although 32 bits of precision is sufficient, 31 bits are
 *	not.  This class <em>will not work</em> in Java 1.0, and is incorrect
 *	for dates before the changeover from the Julian to the Gregorian
 *	calendar.  
 *
 * <p> This code is directly derived from a C package written in November, 
 *	1986; that in turn was based on a calendar package my father wrote 
 *	in the late 1960's in Fortran IV.  Dad died this year, but the code
 *	lives on.
 *
 * @version $Id: Julian.java,v 1.2 2001-01-11 23:37:53 steve Exp $
 * @author Abraham Savitzky, steve@rii.ricoh.com
 */
public class Julian {


/*********************************************************************\
**
** CONSTANTS
**
**      char *datMname[12]      names of the months
**      char *datDname[ 7]      names of the days of the week
**      int datMdays[12]        lengths of the months (non-leapyear)
**
\*********************************************************************/

  public static String datMname[] = {
    "January",    "February",   "March",      "April",
    "May",        "June",       "July",       "August",
    "September",  "October",    "November",   "December",
  };

  public static String datDname[] = {
    "Sunday",     "Monday",     "Tuesday",    "Wednesday",
    "Thursday",   "Friday",     "Saturday",
  };

  public static int monthMaxLength[] = {
    31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
  };

/*********************************************************************\
**
** Assorted Utilities
**
** int   leapyear(y)        TRUE if y is a leapyear
** int  datMlen(m, y)   length of month m in year y
**
\*********************************************************************/

  /** Determine whether the given year is a leapyear according to the 
   *	Gregorian calendar. 
   *
   * @param y the year
   * @return <code>true</code> if the given year is a leap year.
   */
  public static boolean leapyear(long y)
  {
    return (y % 4 == 0) && (y % 100 != 0 || y % 400 == 0);
  }

  /** Determine the length of the given month.
   * 
   * @param y the year
   * @param m the month
   * @return the number of days in the given month of the given year.
   */
  public static int monthLength(int y, int m)
  {
    if (m == 2 && leapyear(y)) return(29);
    else                       return(monthMaxLength[m - 1]);
  }


/*********************************************************************\
**
** Julian Day Utilities:
**
**  long datJday(year, month, day)      date -> julian day
**  datDate(jd, &year, &month, &day)    julian day  -> date
** int datDay(jd)                       julian day  -> day of month
** int datMonth(jd)                     julian day  -> month
** int datYear(jd)                      julian day  -> year
** int datJweek(jd)                     julian day  -> day of week (Sun = 0);
**
\*********************************************************************/

  /** Compute a Julian Day.
   *	
   * @param y the year
   * @param m the month
   * @param d the day
   * @return the Julian day.
   */
  public static long jday(long y, long m, long d)
  {
    long c, ya;

    if (m > 2) {                        /* march = 0 kludge */
        m -= 3;
    } else {
        m += 9; y -= 1;
    }
    c = y/100; ya = y % 100;
    return((146097L * c) / 4 + (1461L * ya) / 4 +
             (153L * m + 2) / 5 + d + 1721119L);
  }

  /*
datDate(j, y, m, d)
long j;
int *y, *m, *d;
{
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;
    jy = 100 * jy + ji;

    if (jm < 10) jm += 3;
    else             {jm -= 9; jy += 1;}
    *d = jd; *m = jm; *y = jy;
}
*/

  public static int day(long j)
  {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;
    jy = 100 * jy + ji;

    if (jm < 10) jm += 3;
    else             {jm -= 9; jy += 1;}
    return (int)jd;
}


  public static int month(long j)
  {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;

    if (jm < 10) jm += 3;
    else             {jm -= 9;}
    return (int)jm;
}


  public static int year(long j)
  {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;
    jy = 100 * jy + ji;

    if (jm < 10) jm += 3;
    else             {jm -= 9; jy += 1;}
    return((int)jy);
  }



  public static int weekday(long j)
  {
    return((int)((j + 1L) % 7L));
  }


/*********************************************************************\
**
** int datStep(&y, &m, &d)      Step to next day in calendar
**      returns 0:  same month
**                1:    next month
**                2:  next year
**
\*********************************************************************/
  /*
    int datStep(y, m, d)
    int *y, *m, *d;
    {
    if ((*d)++ < monthLength(*y, *m)) return(0);
    *d = 1;
    if ((*m)++ < 12) return(1);
    *m = 1; (*y)++;
    return(2);
    }
*/

/*********************************************************************\
**
**  int datNth(y, m, wd, n)
**
**      return the nth wd   of the given year and month.    n = -1 means
**      the last, -2 second from last, etc.  Returns -1 if no such
**      day exists (e.g. 10th Saturday).
**
**  long datNJ(jd, wd, n)
**
**      return the next julian date after jd which is the nth wd
**      of the month.
**
\*********************************************************************/

  static int datNth(int y, int m, int wd, int n)
  {
    int d, mlen;
    long j;

    mlen = monthLength(y, m);
    if (n < 0) {
        d = datNth(y, m, wd, 4);
        if (d + 7 <= mlen) d += 7;
        d += 7 * (n - 1);
    } else {
        d = 1;
        j = jday(y, m, d);
        if (weekday(j) < wd) d += wd - weekday(j);
        else                        d += weekday(j) - wd;
        d += (n - 1) * 7;
        if (d > mlen) d = -1;
    }
    return(d);
  }


  static long datNJ(long jd, int wd, int n)
  {
    long j;
    int y, m, d;

    y = year(jd);
    m = month(jd);
    d = day(jd);
    d = datNth(y, m, wd, n);
    return(jday(y, m, d));
  }


/*********************************************************************\
**
**  int datWstr(str)        string->day of week (0..6)
** int datMstr(str)     string->month         (1..12)
**
**      Both permit the string to be a prefix of the name, and ignore case.
**      Both return -1 if no match.
**
\*********************************************************************/
/*
int datWstr(s)
char *s;
{
    register int i;
    register char c, *p, *q;
    for (i = 0; i < 7; ++i) {
        p = datDname[i]; q = s;
        for ( ; *p > ' '; ++p, ++q)
            if (lowercase(*p) != (c = lowercase(*q))) break;
        if (c < 'a' || c > 'z') return(i);
    }
    return(-1);
}


int datMstr(s)
char *s;
{
    register int i;
    register char c, *p, *q;
    for (i = 0; i < 12; ++i) {
        p = datMname[i]; q = s;
        for ( ; *p > ' '; ++p, ++q)
            if (lowercase(*p) != (c = lowercase(*q))) break;
        if (c < 'a' || c > 'z') return(i + 1);
    }
    return(-1);
}
*/

}
