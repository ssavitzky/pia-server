// ParseChildEnum.java
// ParseChildEnum.java,v 1.2 1999/03/01 23:45:45 pgage Exp

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



package crc.dps.active;

import java.io.*;
import crc.dom.*;

/** Implementation of the NodeEnumerator interface for ParseChildList.
 *
 *	Note that this will be replaced with an Iterator eventually.
 */
class ParseChildEnum implements NodeEnumerator {

  public ParseChildEnum(ParseChildList list) throws NullPointerException{
    if ( list == null ){
      String err = ("ParseChildEnum must be initialized with a list.");
      throw new NullPointerException(err);
    }
    l = list;
    cursor = null;
  }

  /**
   * Returns the first node that the enumeration refers to, and resets the
   * enumerator to reference the first node.  If there are no nodes in the
   * enumeration, null is returned. 
   */
  public Node getFirst(){
    if( l.getLength() == 0 ) return null;
    try{
      cursor = l.item( 0 );
      return cursor;
    } catch( NoSuchNodeException e ){
      return null;
    }
  }

  /** Return the next node in the enumeration, and advances the enumeration. 
   * 	Returns null after the last node in the list has been passed, and
   * 	leaves the current pointer at the last node.
   */
  public Node getNext(){ 
    // pass last element
    if( cursor != null && cursor.getNextSibling() == null ) return null;
    cursor = cursor.getNextSibling();
    return cursor;
  }

  /** Return the previous node in the enumeration, and regresses the
   * 	enumeration.  Returns null after the first node in the enumeration has
   * 	been returned, and leaves the current pointer at the first node.
   */
  public Node getPrevious(){
    if( cursor != null && cursor.getPreviousSibling() == null )
      return null;

    cursor = cursor.getPreviousSibling();
    return cursor;
  }

  /** Returns the last node in the enumeration, and sets the enumerator to
   * 	reference the last node in the enumeration. If the enumeration is
   * 	empty, this method will return null. Doing a getNext() immediately
   * 	after this operation will return null.
   */
  public Node getLast(){
    if( isEmpty() )
      return null;

    long last = l.getLength() - 1;
    try{
      cursor = l.item( last );
      return cursor;
    }catch(NoSuchNodeException e){
      return null;
    }
  }

  /** This returns the node that the enumeration is currently referring to,
   * 	without affecting the state of the enumeration object in any way.
   */
  public Node getCurrent(){
    if(isEmpty()) return null;

    return cursor; 
  }

  /** Returns true if the enumeration's "pointer" is positioned at the start
   * 	of the set of nodes, i.e. if getCurrent() will return the same node as
   * 	getFirst() would return.
   */
  public boolean atStart(){
    if( isEmpty() ) return true;
    try{
      return cursor == l.item( 0 );
    }catch(NoSuchNodeException e){
      return true;
    }
  }

  /** Returns true if the enumeration's "pointer" is positioned at the end of
   * 	the set of nodes, i.e. if getCurrent() will return the same node as
   * 	getLast() would return.
   */
  public boolean atEnd(){
    if ( isEmpty() ) return true;
    try{
      int len = (int)l.getLength();
      return cursor == l.item( len - 1 );
    }catch(NoSuchNodeException e){
      return true;
    }
  }

  protected boolean isEmpty(){ 
    return l.getLength() == 0;
  }

  protected Node cursor = null;
  protected ParseChildList l;
}




