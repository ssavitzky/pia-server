////// Context.java: Document processing context interface
//	$Id: Context.java,v 1.5 1999-04-17 01:18:48 steve Exp $

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

import org.risource.dps.active.ActiveNode;
import org.risource.dps.active.ActiveNodeList;

import java.io.PrintStream;

/**
 * The interface for a document parsing or processing context stack. <p>
 *
 *	A Context amounts to a parse stack or execution stack.  It maintains a
 *	parse tree being constructed or traversed, and any additional state
 *	that applies at each level.  It need not be a stack or linked list,
 *	although this will usually be the simplest implementation. <p>
 *
 *	At any given level in the Context stack there is a current set of
 *	bindings for entities and handlers, and a current Node being operated
 *	on, normally by appending to it.  There is also a current Token being
 *	``expanded'' or processed.  Immediately before starting (pushing) a
 *	new Context, the current Token should correspond to the Element being
 *	pushed.  That way, immediately after popping, it will have the
 *	Handler that needs to be called to finalize the operation.  A Handler
 *	need not be associated with an end tag, which saves the Parser the
 *	trouble of looking it up. <p>
 *
 *	Note that the current Node does <em>not</em> have to be a child
 *	of the current Node one level up in the stack.  It is perfectly
 *	legitimate to build a parentless parse tree, manipulate it in 
 *	some way, and <em>then</em> append it. <p>
 *
 *	Handlers append their results to the parse tree under construction
 *	using the Context operations <code>putResult(<em>node</em>)</code> and 
 *	<code>putResults(<em>nodeList</em>)</code>.  This has several
 *	advantages:
 *   	<ul>
 *	    <li> Handlers can easily return multiple results without
 *		 constructing a tempory NodeList which the calling Context
 *		 would then have to take apart.
 *	    <li> A Processor (an extension of Context) can perform the
 *		 optimization of passing the results directly to an 
 *		 Output without constructing a tree at all.
 *	    <li> Handler operations are then free to return a Token which
 *		 represents a ``continuation.''
 *	</ul>
 *
 * @version $Id: Context.java,v 1.5 1999-04-17 01:18:48 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.dps.Handler
 * @see org.risource.dps.Output
 * @see org.risource.dps.Processor
 * @see org.risource.dps.Token
 * @see org.risource.dps.Namespace
 */

public interface Context {

  /************************************************************************
  ** Namespaces:
  ************************************************************************/

  /** Return a namespace with a given name.  If the name is null, 
   *	returns the most-local namespace.
   */
  public Namespace getNamespace(String name);

  /** Return the locally-defined namespace, if any. */
  public Namespace getLocalNamespace();

  /** Return the next context defining a local namespace. */
  public Context getNameContext();

  /************************************************************************
  ** Namespace convenience functions:
  ************************************************************************/

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNodeList getValueNodes(String name, boolean local);

  /** Set the value of an entity. 
   */
  public void setValueNodes(String name, ActiveNodeList value, boolean local);

  /** Get the namespace containing a given name. 
   * @return <code>null</code> if the name is undefined.
   */
  public Namespace locateBinding(String name, boolean local);

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNode getBinding(String name, boolean local);

  /** Set the binding (Entity node) of an entity, given its name. 
   *	Note that the given name may include a namespace part. 
   */
  public void setBinding(String name, ActiveNode ent, boolean local);

  /************************************************************************
  ** Context Stack:
  ************************************************************************/

  /** Return the depth of the context stack. */
  public int getDepth();

  /** Return the previous context on the context stack. */
  public Context getPreviousContext();

  /** Return the current top context. */
  public TopContext getTopContext();

  /** Return the innermost Processor on the context stack. */
  public Processor getProcessor();

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Construct a new Context linked to this one.  <p>
   *
   *	Recursive operations like <code>expand</code> use this to create a new
   *	evaluation stack frame when descending into an Element.
   */
  public Context newContext();

  /** Create a sub-processor with a given input and output. */
  public Processor subProcess(Input in, Output out);

  /** Create a sub-processor with a given input, output, and entities.
   *
   *	Commonly used to obtain an expanded version of the attributes
   *	and content of the parent's current node.
   */
  public Processor subProcess(Input in, Output out, EntityTable entities);

  /************************************************************************
  ** Message Reporting:
  ************************************************************************/

  /** Return a PrintStream suitable for error reporting. */
  public PrintStream getLog();

  /** Obtain the current verbosity level */
  public int getVerbosity();
  public void setVerbosity(int value);

  /** Report a message on the log, preceeded by indentation, provided
   *	the current <code>verbosity</code> exceeds the given level. <p>
   *
   *	The message is terminated by a newline if <code>endline</code> is
   *	<code>true</code>. 
   */
  public void message(int level, String text, int indent, boolean endline);

  /** Report an error message on the log file, provided the verbosity level
   *	is DEBUG (2) or higher.
   *
   *	Note that the message is <em>not</em> terminated by a newline. 
   */
  public void debug(String message);

  /** Report an error message on the log file, preceeded by indentation. */
  public void debug(String message, int indent);

}
