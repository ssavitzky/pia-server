////// ContextStack.java: A linked-list stack of current nodes.
//	$Id: ContextStack.java,v 1.11 2001-04-03 00:04:58 steve Exp $

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


package org.risource.dps.util;

import java.io.PrintStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.*;
import org.risource.dps.process.BasicProcessor;
import org.risource.dps.namespace.BasicNamespace;
import org.risource.dps.namespace.BasicEntityTable;

/**
 * A stack frame for a linked-list stack of current nodes. 
 *	It is designed to be used for saving state in a Cursor that is
 *	not operating on a real parse tree.
 *
 * @version $Id: ContextStack.java,v 1.11 2001-04-03 00:04:58 steve Exp $
 * @author steve@rii.ricoh.com
 * 
 * @see org.risource.dps.Cursor
 */
public class ContextStack  implements Context {

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  public Context newContext() {
    return new ContextStack(input, this, output, entities);
  }

  public Processor subProcess(Input in, Output out) {
    return new BasicProcessor(in, this, out, entities);
  }

  public Processor subProcess(Input in, Output out, Namespace entities) {
    return new BasicProcessor(in, this, out, entities);
  }

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Context 	stack = null;
  protected int 	depth = 0;
  protected Namespace	entities = null;
  protected Input 	input;
  protected Output 	output;
  protected int 	verbosity = 0;
  protected TopContext 	top = null;
  protected PrintStream	log = System.err;

  protected Context   	nameContext = null;

 /************************************************************************
 ** Accessors:
 ************************************************************************/

  /** getBindings returns the most-local Entity namespace. */
  protected Namespace getBindings() 	{ 
    return (entities == null && nameContext != null) 
      ? nameContext.getNamespace(null) 
      : entities;
  }
  protected void setBindings(Namespace bindings) { entities = bindings; }

  public Input getInput() 		{ return input; }
  public void  setInput(Input in) 	{ input = in; }

  public Output getOutput() 		{ return output; }
  public void  setOutput(Output out) 	{ output = out; }

  public int getDepth() 		{ return depth; }
  public Context getPreviousContext() 	{ return stack; }
  public TopContext getTopContext() 	{ return top; }
  public Context getNameContext() 	{ return nameContext; }
  public Processor getProcessor() {
    return (stack == null)? null : stack.getProcessor();
  }

  /************************************************************************
  ** Namespaces:
  ************************************************************************/

  /** Return a namespace with a given name.  If the name is null, 
   *	returns the most-locally namespace.
   */
  public Namespace getNamespace(String name) {
    if (entities != null) {
      if (name == null || name.equals(entities.getName())) {
	return entities;
      } else if (entities.containsNamespaces()) {
	ActiveNode ns = entities.getBinding(name);
	if (ns != null && ns.asNamespace() != null)
	  return ns.asNamespace();
      }
    }
    if (nameContext != null) {
      return nameContext.getNamespace(name);
    } else {
      return null;
    }
  }

  /** Return the locally-defined namespace, if any. */
  public Namespace getLocalNamespace() {
    return entities;
  }

  /************************************************************************
  ** Namespace convenience functions:
  ************************************************************************/

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNodeList getValueNodes(String name, boolean local) {
    ActiveNode binding = getBinding(name, local);
    if (binding == null) return null;
    return binding.getValueNodes(this);
  }

  /** Set the value of an entity. 
   */
  public void setValueNodes(String name, ActiveNodeList value, boolean local) {
    Namespace ns = locateBinding(name, local);
    if (ns != null) {
      ns.setValueNodes(this, name, value);
    } else {
      if (entities == null && (local || nameContext == null))
	entities = new BasicEntityTable();
      Tagset ts = this.getTopContext().getTagset();
      // We can't just create a binding because the namespace might be 
      // a specialized subclass, e.g. TreeAttrList.
      //getNamespace(null).setBinding(name, ts.createActiveEntity(name, value));
      getNamespace(null).setValueNodes(this, name, value);
    } 
  }

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveNode getBinding(String name, boolean local) {
    ActiveNode ent = (entities == null)
      ? null : entities.getBinding(name);
    //if (debug() && ent != null) 
    //  debug("Binding found for " + name + " " + ent.getClass().getName());
    return (local || ent != null || nameContext == null)
      ? ent : nameContext.getBinding(name, local);
  }

  /** Get the namespace containing an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public Namespace locateBinding(String name, boolean local) {
    ActiveNode ent = (entities == null)
      ? null : entities.getBinding(name);
    if (ent != null) return entities;
    return (local || nameContext == null)
      ? null : nameContext.locateBinding(name, local);
  }

  /** Set the binding (Entity node) of an entity, given its name. 
   *	Note that the given name may include a namespace part. 
   */
  public void setBinding(String name, ActiveNode ent, boolean local) {
    Namespace ns = locateBinding(name, local);
    if (ns == null) {
      if (entities == null && (local || nameContext == null))
	entities = new BasicEntityTable();
      ns = getBindings();
    } 
    ns.setBinding(name, ent);
  }


  /************************************************************************
  ** Debugging:
  **	This is a subset of org.risource.util.Report.
  ************************************************************************/

  public int 	getVerbosity() 		{ return verbosity; }
  public void 	setVerbosity(int value) { verbosity = value; }
  public PrintStream getLog() 		 { return log; }
  public void setLog(PrintStream stream) { log = stream; }

  public void message(int level, String text, int indent, boolean endline) {

      //      int nodeValue(
    if (verbosity < level) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += text;

    if (endline) log.println(s); else log.print(s);




    //    if (false &&  level < 2 ) try{
    if ( level < 2 ) try{ // Bill's error-inserting method
	
	ActiveNode temp = getBinding("ERROR", false);
	if (temp != null){
	    temp.getValueNodes(this).append(new TreeText(text));
	    if (endline) 	
		temp.getValueNodes(this).append(new TreeElement("br") );
	}
    }
    catch(Exception e){};


  }

  public final boolean debug() { return verbosity >= 2; }

  public final void debug(String message) {
    if (verbosity >= 2) log.print(message);
  }

  public final void debug(String message, int indent) {
    if (verbosity < 2) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    log.print(s);
  }

  public String logNode(Node aNode) { return Log.node(aNode); }
  public String logString(String s) { return Log.string(s); }


  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void copy(ContextStack old) {
    input 	= old.input;
    output 	= old.output;
    verbosity 	= old.verbosity;
    top 	= old.top;
    log 	= old.log;
  }

  public ContextStack() {}

  public ContextStack(ContextStack prev) {
    copy(prev);
    stack = prev;
    nameContext = (prev.entities == null)? prev.nameContext : prev;
    entities = null;
  }

  public ContextStack(Input in, Context prev, Output out, Namespace ents) {
    stack    	= prev;
    input    	= in;
    output   	= out;
    entities 	= ents;
    top	     	= prev.getTopContext();
    nameContext = ((prev.getLocalNamespace() == null)?
		   prev.getNameContext() : prev);
    verbosity 	= prev.getVerbosity();
    log	      	= prev.getLog();
    depth    	= prev.getDepth() + 1;
  }

  public ContextStack(Input in, Context prev, Output out) {
    stack    	= prev;
    input    	= in;
    output   	= out;
    entities 	= null;
    top	     	= prev.getTopContext();
    nameContext = ((prev.getLocalNamespace() == null)?
		   prev.getNameContext() : prev);
    verbosity 	= prev.getVerbosity();
    log	      	= prev.getLog();
    depth    	= prev.getDepth() + 1;
  }


}
