////// Realizable.java -- interface for a resource in a site
//	$Id: Realizable.java,v 1.1 1999-08-31 23:32:10 steve Exp $

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

package org.risource.site;

import org.w3c.dom.*;
import org.risource.ds.*;
import org.risource.dps.*;

import java.io.File;
import java.net.URL;

/**
 * Interface for a Resource which is ``realizable'' in the sense that it
 *	descends, or can be made to descend, directly from the root of a
 *	site. 
 *
 * @version $Id: Realizable.java,v 1.1 1999-08-31 23:32:10 steve Exp $
 * @author steve@rsv.ricoh.com 
 * @see java.io.File
 * @see java.net.URL 
 * @see org.risource.dps
 * @see org.w3c.dom
 * @see org.risource.site.Document

 */
public interface Realizable {

  /** Returns <code>true</code> if the resource is a local file or directory
   *	in direct descent from a real root directory.
   *
   *<p>	Note that <code>isReal</code> differs from <code>isLocal</code>: 
   *	a resource can be local without being real, e.g. if it is an alias.
   */
  public boolean isReal();

  /** Returns the absolute path to this resource's real location in the 
   *	filesystem. 
   *
   * @return the absolute path to this resource.  Returns <code>null</code>
   *	if the resource has not been realized.
   */
  public String getRealPath();

  /** Make the associated resource real. 
   *
   * @return <code>false</code> if the Resource cannot be realized.
   */
  public boolean realize();
}
