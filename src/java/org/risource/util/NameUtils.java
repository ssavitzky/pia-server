// NameUtils.java
// $Id: NameUtils.java,v 1.6 2000-08-30 22:58:24 steve Exp $

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


package org.risource.util;


/**
 * This class contains static functions that deal with names and name-like
 *	strings.
 */
public class NameUtils {

  /************************************************************************
  ** String constants:
  ************************************************************************/

  public static String filesep = System.getProperty("file.separator");
  public static String home     = System.getProperty("user.home");

  /************************************************************************
  ** Generic name utilities:
  ************************************************************************/

  /** Capitalize a string, producing a valid Java identifier.  
   *	Any character which is not a letter or digit is deleted and the next
   *	character is capitalized.  Uppercase characters are preserved.
   */
  public static final String capitalize(String s) {
    StringBuffer n = new StringBuffer();
    boolean capitalize = true;
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	capitalize = true;
      else {
	n.append(capitalize? Character.toUpperCase(s.charAt(i)) : s.charAt(i));
	capitalize = false;
      }
    }
    return n.toString();
  }

  /** Uppercase a string, producing  a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore. */
  public static final String uppercase(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
      else
	n.setCharAt(i, Character.toUpperCase(n.charAt(i)));
    }
    return n.toString();
  }

  /** Lowercase a string, producing  a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore. */
  public static final String lowercase(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
      else
	n.setCharAt(i, Character.toLowerCase(n.charAt(i)));
    }
    return n.toString();
  }


  /************************************************************************
  ** File name utilities:
  ************************************************************************/

  /** Make a path from a directory and a filename.   */
  public static String makePath(String directory, String filename) {
    if (! directory.endsWith(filesep) && ! directory.equals(""))
      directory += filesep;
    if (filename.startsWith(filesep) && ! directory.equals(""))
      filename = filename.substring(filesep.length());
    return directory + filename;
  }

  /** Extract the filename from a (system) path. */
  public static String filenamePart(String path) {
    char sep = System.getProperty("file.separator", "/").charAt(0);
    int i = path.lastIndexOf(sep);
    return (i < 0)? path : path.substring(i+1);
  }

  /** Extract the extension from a filename. */
  public static String filenameExt(String path) {
    char sep = System.getProperty("file.separator", "/").charAt(0);
    int i = path.lastIndexOf('.');
    int j = path.lastIndexOf(sep);
    return (i < 0 || i <= j)? null : path.substring(i+1);
  }

  /** Make a URL path (separated by "/") into a system path. */
  public static String systemPath(String base, String urlPath) {
    String s = (base == null)? "" : base;
    if (base != null && !base.endsWith(filesep)
	&& !urlPath.startsWith("/")) s += filesep;
    else if (base != null && base.endsWith(filesep)
	&& urlPath.startsWith("/")) urlPath = urlPath.substring(1);
    for (int i = 0; i < urlPath.length(); ++i) {
      char c = urlPath.charAt(i);
      if (c == '/') s += filesep; else s += c;
    }
    return s;
  }

  /************************************************************************
  ** Class name utilities:
  ************************************************************************/

  /** Turn a string into a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore,
   *	and capitalize the first character.  If the name is already
   *	mixed case or uppercase, this will be preserved. */
  public static final String javaName(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (i == 0) n.setCharAt(i, Character.toUpperCase(n.charAt(i)));
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
    }
    return n.toString();
  }

  /** Turn a string into a valid Java classname with more control over
   *	the treatment of special characters. 
   *
   * @param s the string
   * @param firstCase (-1: lower; 0: preserve; 1: raise) first letter
   * @param otherCase (-1: lower; 0: preserve; 1: raise) other letters
   * @param elideDash elide hyphen and capitalize the next letter; otherwise
   *	replace with underscore.
   * @param elideDot elide period and capitalize the next letter; otherwise
   *	replace with underscore.
   *
   */
  public static final String javaName(String s, int firstCase, int otherCase,
				      boolean elideDash, boolean elideDot) {
    String n = "";
    int nxcase = firstCase;
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if      (elideDash && c == '-') 		{ nxcase = 1; continue; }
      else if (elideDot  && c == '.') 		{ nxcase = 1; continue; }
      else if (!Character.isLetterOrDigit(c)) 	n += '_';
      else if (nxcase < 0) 			n += Character.toLowerCase(c);
      else if (nxcase > 0) 			n += Character.toUpperCase(c);
      else 					n += c;
      nxcase = otherCase;
    }
    return n;
  }

  /** Load a named class.  The <code>packagePrefix</code> is prepended only if
   *	the handle name contains no "." characters.  Util.javaName can be used
   *	to convert an SGML identifier (e.g. a tag) to a class name.
   *
   * @see Util#javaName
   */
  public static Class loadClass(String h, String prefix) {
    if (h.indexOf(".") < 0) h = prefix + h;
    try {
      Class handleClass = Class.forName(h);
      return handleClass;
    } catch (Exception e) { }
    return null;
  }

}
