// ModelGroup.java
// ModelGroup.java,v 1.2 1999/03/01 23:45:17 pgage Exp

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

package crc.dom;

public interface ModelGroup extends Node {
  
  // The ints for the following two methods should
  // be constants defined in the ConnectionType class.

  void setConnector(int connector);
  int getConnector();

  // The ints for the two methods below should be
  // constants defined in the OccurrenceType class.

  void setOccurrence(int occurrence);
  int getOccurrence();

  void setTokens(NodeList tokens);
  NodeList getTokens();

};
