// PI.java
// PI.java,v 1.3 1999/03/01 23:45:22 pgage Exp

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

 /**
  * A PI node is a "processing instruction". The content of the PI node is the entire
  * content between the delimiters of the processing instruction 
  * 
  * wstring name 
  *     XML defines a name as the first token following the markup that begins the
  *      processing instruction, and this attribute returns that name. For HTML, the
  *      returned value is null. 
  * wstring data 
  *      The content of the processing instruction, from the character immediately after
  *      the <? (after the name in XML) to the character immediately preceding the ?>. 
  */

public interface PI extends Node {

  void setName(String name);
  String getName();

  void setData(String data);
  String getData();

};
