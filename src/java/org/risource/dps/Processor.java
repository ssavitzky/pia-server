////// Processor.java: Document Processor interface
//	$Id: Processor.java,v 1.4 1999-04-07 23:20:48 steve Exp $

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


package org.risource.dps;

/**
 * The interface for a document Processor. 
 *
 *  <p>	A Processor has an Input, an Output, and a Context; the latter is a
 *	dynamic link up the processing context stack.  Processor
 *	<em>extends</em> Context, which means that it also has an entity
 *	table.
 *
 *  <p>	A TopContext (q.v.) is a specialized Processor with links to its
 *	current Tagset and, possibly, Parser.
 *
 * @version $Id: Processor.java,v 1.4 1999-04-07 23:20:48 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Context
 * @see org.risource.dps.Input 
 * @see org.risource.dps.Output
 * @see org.risource.dps.TopContext
 */

public interface Processor extends Context {

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput);

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput);

  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Run the Processor, obtaining nodes from its Input and performing
   *	their actions, usually involving copying to the Output.
   *
   * @return <code>true</code> if processing ran to completion, 
   *	<code>false</code> if <code>stop()</code> was called.
   */
  public boolean run();

  /** Test whether the Processor is ``running''.
   */
  public boolean isRunning();

  /** Turn off the Processor's ''running'' flag. */
  public void stop();

  /** Copy nodes from the input to the output. */
  public void copy();

  /** Process the current Node */
  public void processNode();

  /** Process the current Node by expanding its attributes,
   *	then processing its children. */
  public void expandCurrentNode();

  /** Process the children of the current Node.
   *
   * @return <code>true</code> if processing ran to completion, 
   *	<code>false</code> if <code>stop()</code> was called.
   */
  public boolean processChildren();

}
