////// XMLUtil.java: the Document Processing System used stand-alone as a filter
//	$Id: XMLUtil.java,v 1.5 2001-04-03 00:05:25 steve Exp $

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


package org.risource.site.util;

import java.io.*;

import org.risource.dps.*;
import org.risource.dps.output.*;
import org.risource.dps.active.*;
import org.risource.dps.tree.TreeElement;
import org.risource.dps.process.TopProcessor;
import org.risource.dps.tagset.TagsetProcessor;
import org.risource.dps.tagset.Loader;

import org.risource.site.*;

/**
 * Load an XML configuration file.
 */
public class XMLUtil {
  static int verbosity = 0;

  public static ActiveElement load(File file, Tagset ts, Resource r) {
    if (ts == null) {
      ts = Loader.loadTagset("xhtml");
    }
    FileInputStream in = null;
    try {
      in = new FileInputStream(file);
    } catch (Exception e) {
      System.err.println("Cannot open input file " + file.getPath());
      return null;
    }
    /* Ask the Tagset for an appropriate parser, and set its Reader. */
    Parser p = ts.createParser();
    p.setReader(new InputStreamReader(in));
    ToNodeList out = new ToNodeList(null);

    /* Finally, create a Processor and set it up. */
    TopContext ii = (r == null)
      ? new TopProcessor()
      : r.getRoot().makeTopContext(null, null);
    ii.setInput(p);
    ii.setTagset(ts);
    return load(ii);
  }


  public static ActiveElement load(Document d, Tagset ts) {
    TopContext ii = d.getRoot().makeTopContext(d, ts);
    return load(ii);
  }

  public static ActiveElement load(TopContext ii) {
    ToNodeList out = new ToNodeList(null);
    ii.setOutput(out);
    ii.run();
    ii.getInput().close();
    ActiveNodeList nl = out.getList();
    for (int i = 0; i < nl.getLength(); ++i) {
      ActiveNode node = nl.activeItem(i);
      if (node.getNodeType() == NodeType.ELEMENT) {
	return (ActiveElement) node;
      }
    }
    return null;
  }

}
