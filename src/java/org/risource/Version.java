// Version.java
// $Id: Version.java,v 1.1 1999-03-12 02:32:22 steve Exp $

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

/** Version and release number information for the PIA.
 *
 * <p> Note that this is currently different from PIA/Makefile
 *
 * @version $Id: Version.java,v 1.1 1999-03-12 02:32:22 steve Exp $
 * @see org.risource.pia.Setup
 */
public interface Version {
  public static final String MAJOR   = "2";
  public static final String MINOR   = "1";
  public static final String SUFFIX  = ""; // e.g. beta1 or whatever
  public static final String VERSION = MAJOR + "." + MINOR + SUFFIX;
}
