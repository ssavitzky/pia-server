////// filterHandler.java: <filter> Handler implementation
//	$Id: filterHandler.java,v 1.3 2000-09-20 00:34:22 steve Exp $

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


package org.risource.dps.handle;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import org.risource.site.*;

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.util.Pump;

import org.risource.dps.tree.TreeComment;

/**
 * Handler for &lt;filter&gt;....&lt;/&gt;  <p>
 *
 * <p>	Filter is basically include, except that the input stream is piped
 *	through a filter (command) before being parsed.  If the src attribute
 *	is unspecified, the contents of the tag are piped instead.  If the 
 *	shell attribute is present, it is used as a shell to run the script
 *	specified by the cmd attribute. 
 *
 * @version $Id: filterHandler.java,v 1.3 2000-09-20 00:34:22 steve Exp $
 * @author steve@rsv.ricoh.com
 */

public class filterHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;filter&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    TopContext top  = cxt.getTopContext();
    String     url  = atts.getAttribute("src");
    String  tsname  = atts.getAttribute("tagset");
    String entname  = atts.getAttribute("entity");
    boolean quoted  = atts.hasTrueAttribute("quoted");
    Tagset      ts  = ("".equals(tsname))? null : top.loadTagset(tsname);
    String     cmd  = atts.getAttribute("cmd");
    String   shell  = atts.getAttribute("shell");
    TopContext  proc = null;
    Reader 	 rdr = null;
    ActiveNode   ent = null;
    InputStream  istm = null;
    InputStream  rstm = null;
    OutputStream ostm = null;
    InputStream  estm = null; 
    String	  pia = null;

    // recognize pia: on cmd and shell.   
    //   Can't just use locateResource because there might be spaces in the
    //   command strings.  Besides that, locateResource returns a file.

    if (cmd != null && cmd.startsWith("pia:")) {
      pia = locatePia(cxt);
      if (pia == null) {
	reportError(in, cxt, "cannot locate PIA_HOME");
	return;
      }
      cmd = org.risource.util.NameUtils.systemPath(pia, cmd);
    }
      
    if (shell != null && shell.startsWith("pia:")) {
      if (pia == null) pia = locatePia(cxt);
      if (pia == null) {
	reportError(in, cxt, "cannot locate PIA_HOME");
	return;
      }
      shell = org.risource.util.NameUtils.systemPath(pia, shell);
    }

    // === content used as stdin not supported yet ===

    // Check the entity.  If it's already defined, we can just use its value

    if (entname != null) {
      entname = entname.trim();
      ent = cxt.getBinding(entname, false);
      if (ent != null) {
	proc = top.subDocument(ent.fromValue(cxt), cxt, out, ts);
	proc.run();
	top.subDocumentEnd();
	return;
      }
    }

    if (cmd == null) {
      reportError(in, cxt, "No command specified in filter.");
      out.putNode(new TreeComment("No command specified in filter")); 
      return;
    }

    // Look for a resource

    Document doc = null;
    Resource res = (url == null)? null : top.locateResource(url, false);

    if (url == null) {		// no src -- use content
      // punt for now.
    } else if (res != null) {	// We found one.  Open the document
      doc = res.getDocument();
      istm = doc.documentInputStream();
    } else {			// Try to open the stream.  Croak if it fails. 
      try {
	if (top.isRemotePath(url)) {
	  istm = top.readExternalResource(url);
	} else if (top.getDocument() == null) {
	  // fail immediately if the path is not remote, but top has a
	  // document; this means that locateResource tried and failed.
	  File f = top.locateSystemResource(url, false);
	  if (f != null) istm = new FileInputStream(f);
	}
      } catch (IOException e) {
	reportError(in, cxt, e.toString());
	out.putNode(new TreeComment(e.toString()));
	return;
      }
      if (istm == null) {
	reportError(in, cxt, "Cannot open " + url);
	out.putNode(new TreeComment("Cannot open " + url));
	return;
      }
    }

    // Set up the subprocess that runs the command.

    String  error   = "";
    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    Pump    pump    = null;

    try {
      if (shell != null) {
	String cmdArray[] = { shell, "-c",  cmd };
	process = runtime.exec(cmdArray);
      } else {
	process = runtime.exec(cmd);
      }
      if (process == null) {
	reportError(in, cxt, "could not create process\n");
	return;
      }
      rstm = process.getInputStream();
      rdr  = new InputStreamReader(rstm);
      estm = process.getErrorStream();
    } catch (Exception e) {
      try {
	for(;;){
	  int b = estm.read();
	  if (b == -1) break;
	  error += (char)b;
	}
	if (rdr != null) rdr.close();
	if (istm != null) istm.close();
	if (rstm != null) rstm.close();
	if (ostm != null) ostm.close();
	if (estm != null) estm.close();
      } catch (IOException ee) {}
      reportError(in, cxt, e.toString() + "\n STDERR=" + error + "\n");
      return;
    }

    // If the tagset is "", just gobble lines as text. 

    if ("".equals(tsname)) {
      LineNumberReader lnr = new LineNumberReader(rdr);
      String line;
      if (istm != null) {
	pump = new Pump(istm, process.getOutputStream());
	pump.run();
      }
      do {
	try {	
	  line = lnr.readLine();
	  line = (line == null)? "\n" : line+"\n"; 
	  putText(out, cxt, line);
	  if (!lnr.ready()) break;
	} catch (IOException e) { break; }
      } while (line != null);
      try {      
	if (lnr != null) lnr.close();
	if (rdr != null) rdr.close();
	if (istm != null) istm.close();
	if (rstm != null) rstm.close();
	if (ostm != null) ostm.close();
	if (estm != null) estm.close();
      } catch (IOException e) {}
      return;
    } else if (ts == null) {
      reportError(in, cxt, "Cannot open tagset " + tsname + "\n");
      if (content != null) Expand.processNodes(content, cxt, out);
      else out.putNode(new TreeComment("Cannot open tagset " + tsname));
      try {      
	if (rdr != null) rdr.close();
	if (istm != null) istm.close();
	if (rstm != null) rstm.close();
	if (ostm != null) ostm.close();
	if (estm != null) estm.close();
      } catch (IOException e) {}
      return;
    }

    // Create a Parser and TopProcessor to process the stream.  

    Parser p  = ts.createParser();
    p.setReader(rdr);
    proc = top.subDocument(p, cxt, out, ts);
    if (doc != null) proc.setDocument(doc);

    // If we're caching in an entity, tell the parser to save the tree in it.

    if (entname != null) {
      ent = ts.createActiveNode(Node.ENTITY_NODE, entname, (String)null);
      p.setDocument(ent);
    }

    // Fire off a thread if necessary to pump istm into the command's STDIN
    
    if (istm != null) {
      pump = new Pump(istm, process.getOutputStream());
      pump.run();
    }

    // Crank away.
    if (quoted) proc.copy(); else proc.run();
    top.subDocumentEnd();

    // === Somehow, a message about reaped threads is getting onto the screen,
    // === but only if pia's STDOUT is a TTY.  Doesn't seem fixable.

    // Clean up.
    try {
      try { process.waitFor(); } catch (Exception e) {}
      process.destroy();
      try {
	for(;;){
	  int b = estm.read();
	  if (b == -1) break;
	  error += (char)b;
	}
      } catch (IOException ee) {}
      if (process.exitValue() != 0) 
	reportError(in, cxt, "exit value=" + process.exitValue() + "\n");
      if (error.length() != 0) 
	reportError(in, cxt, "STDERR=" + error + "\n" );
      if (rstm != null) rstm.close();
      if (ostm != null) ostm.close();
      if (estm != null) estm.close();
      if (istm != null) istm.close();
      if (rdr  != null)  rdr.close();
    } catch (IOException e) {}

    if (ent != null) cxt.setBinding(entname, ent, false);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public filterHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, NORMAL, 0 (check)
  }

  filterHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
