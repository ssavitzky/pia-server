// Version.java
// $Id: Version.java,v 1.3 1999-03-12 21:36:02 steve Exp $

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
 * <p> Note that this is currently different from PIA/Makefile
 *
 * @version $Id: Version.java,v 1.3 1999-03-12 21:36:02 steve Exp $
 * @see org.risource.pia.Setup
 */
public interface Version {
  public static final String MAJOR   = "2";
  public static final String MINOR   = "1";
  public static final String SUFFIX  = ""; // e.g. beta1 or whatever
  public static final String VERSION = MAJOR + "." + MINOR + SUFFIX;
  public static final String CVS_TAG = "$Name:  $";
  public static final String REVISION=
    "$Id: Version.java,v 1.3 1999-03-12 21:36:02 steve Exp $";
}
