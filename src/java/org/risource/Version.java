// Version.java
// $Id: Version.java,v 1.5 1999-03-13 01:08:12 steve Exp $

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

package org.risource;

/** Version (release number) information for the PIA.
 *
 * <p> Note that what this interface calls the ``VERSION'' is what CVS calls
 *	the ``release number'' of the PIA system, not what CVS calls the 
 *	``revision''. 
 *
 * <p> Note that this is currently not changed from PIA/Makefile
 *
 * @version $Id: Version.java,v 1.5 1999-03-13 01:08:12 steve Exp $
 * @see org.risource.pia.Setup
 */
public interface Version {
  public static final String VENDOR_TAG = "PIA";
  public static final int    RELEASE    = 2;
  public static final int    MAJOR      = 0;
  public static final int    MINOR      = 2;
  public static final String SUFFIX     = ""; // e.g. beta1 or whatever
  public static final String VERSION    = RELEASE + "." + MAJOR
    + ((MINOR == 0)? "" : "." + MINOR) + SUFFIX;
  public static final String CVS_TAG    = "$Name:  $";
  public static final String CVS_REV    =
    "$Id: Version.java,v 1.5 1999-03-13 01:08:12 steve Exp $";
}
