// Content.java
// $Id: Content.java,v 1.4 2001-04-03 00:05:10 steve Exp $

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



package org.risource.pia;

import org.risource.pia.Machine;
import org.risource.pia.Agent;
import org.risource.pia.ContentOperationUnavailable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Content
 *  is an abstract interface for those objects which can serve as the
 *  content (data portion) of a transaction.    
 *  Content objects sit between a to and a from machine. <p>
 *
 *  A Content is generally created by the ContentFactory which first
 *  parses the stream headers to determine the type of object then
 *  creates the appropriate content object.  They can also be generated 
 *  directly by agents.   <p>
 *
 *  Implementors of the Content interface act as wrappers for
 *  the objects which contain actual data; their constructors are
 *  expected to take a ``native'' type as an argument.<p>
 *
 *  Note that content objects are generally streams, and that
 *  processing should be delayed as long as possible. (In some cases,
 *  such as video, it may be impossible to create the full object.  In
 *  most other cases, it is simply a matter of wanting to start
 *  feeding data to the client as early as possible.)  Most content
 *  types provide appropriate editing methods that allow agents to operate
 *  on content objects -- the operations are not actually performed until
 *  the data is needed. <p>
 *
 *  Most implementators of Content are in <code>org.risource.content</code> except
 *  for FormContent, which is used rather differently.
 *
 *  @see org.risource.content 
 *  @see org.risource.pia.FormContent
 */

public interface Content {

 /**
  * Headers generally contain meta-data about the object and the transaction.
  * The content object should generally maintain content specific items like
  * content-length, while the transaction maintains most others.
  * @return Headers object
  */
  public Headers headers();

  /**
   * set Headers object
   */
  public void setHeaders( Headers headers );



  /************************************************************
  ** Content producers:
  ************************************************************/

  /**   The actual data object e.g. the stream or data structure,
   *    should be specified
   *    in the construction of this content object.
   *    Normally this would be specified in the constructor, however
   *     content objects often created by name (see ContentFactory)
   *    so need a way to specify source.
   *
   *	@exception org.risource.pia.ContentOperationUnavailable if processing has
   *	  already begun or wrong type of object
   */
  public void source(Object input) throws ContentOperationUnavailable;
  
  /************************************************************
  ** Access functions:
  ************************************************************/

 /** 
  * Access functions. 
  *  when the "To machine" is ready to send a response, it reads
  *  data from the content object.  At that point (if not sooner)
  *  the content object should start sucking data from the source,
  *  processing in any matter specified by agents, and then finally spitting
  *  out the data. <p>
  *
  *  Essentially stream functions.
  */

  /**
   * sets the target for write operations and writes data to this stream
   * this method will block until all data is written to output stream.
   * @param outStream the OutputStream to write on.
   * @return the number of items written
   * @exception org.risource.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   * @exception java.io.IOException if thrown by the OutputStream.
   */
  public int writeTo(OutputStream outStream)
       throws ContentOperationUnavailable, IOException;


  /************************************************************
  ** Agent interactions:
  ************************************************************/


  /** Add an output stream to "tap" the data before it is written. 
   * Taps will get data during a read operation just before the data "goes
   * out the door" 
   * @exception org.risource.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   */
  public void tapOut(OutputStream tap) throws ContentOperationUnavailable;


  /** Add an output stream to "tap" the data as it is read.
   * 	Taps will get data  as soon as it is  available to the content 
   * 	-- before any processing occurs.  
   * 	tapOut == tapIn if the content does not support editing/processing.
   * @exception org.risource.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   */
  public void tapIn(OutputStream tap) throws ContentOperationUnavailable;

  
  /** specify an agent to be notified when  entering a state, for
   * example the object is complete 
   * @param state: string naming the state change agent is interested in
   * @param arg:  arbitrary object that will be sent back to be agent when
   *            the state occurs and the agents contentUpdate method is called
   * @exception org.risource.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   */
  public void notifyWhen(Agent interested, String state, Object arg)
       throws ContentOperationUnavailable;

  /**
   * return a list of states that this content can go through
   * agents can use this to determine when to be notified
   */
   public String[] states();
  

  /**
   * If the content object exists as a data structure in memory, then
   * it is persistent. Otherwise, it is not persistent (streams are
   * not persistent).
   */
  public boolean isPersistent();


  /** 
   * Begin processing input.
   * The transaction calls this before the "to machine" is ready to
   * send a response, giving us an opportunity to fill up our internal
   * buffers
   *
   * @return false if content is complete */
  public boolean processInput();

  /************************************************************
  ** Constructors:
  ************************************************************/
  /**
   * Typically constructors should accept "native" source types as arguments
   */

  /************************************************************
  ** Content specific operations:
  ************************************************************/
  /**
   * Filtering, editing, etc.
   */
  /**
   * Add an object to the content; 
   * if object is not a compatible type, throws exception
   * @param  moreContent additional content to be inserted.
   * @param  where 	 where to insert. 
   *	Exact interpretation depends on content;  
   *	by convention 0 means at front
   *	 -1 means at end, everything else is subject to interpretation.
   *	Implementors should define suitable constants.
   * @exception org.risource.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   */
  public void add(Object moreContent, int where)
       throws ContentOperationUnavailable;

}	









