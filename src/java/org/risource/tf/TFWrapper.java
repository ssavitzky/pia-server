// TFCWrapper.java
// $Id: TFWrapper.java,v 1.4 2001-04-03 00:05:32 steve Exp $

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
 * This code was initially developed by Ricoh Innovations, Inc.  Portions
 * created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


/**
 * Wrap a UnaryFunctor (legacy code) to turn it into a TFComputer.
 *	This is an interim hack. 
 */

package org.risource.tf;

import org.risource.pia.Transaction;
import org.risource.ds.Features;
import org.risource.ds.UnaryFunctor;

import org.risource.tf.TFComputer;

public class TFWrapper extends TFComputer {

  private UnaryFunctor wrapped; 

  /** Compute the value corresponding to the given feature.  
   */
  public Object computeFeature(Transaction parent) {
    return wrapped.execute(parent);
  }

  public TFWrapper(UnaryFunctor f) {
    wrapped = f;
  }
}
