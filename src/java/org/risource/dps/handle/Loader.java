////// Loader.java: Handler loading and initialization utilities.
//	$Id: Loader.java,v 1.12 2001-04-03 00:04:20 steve Exp $

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


package org.risource.dps.handle;

import org.risource.dps.Handler;
import org.risource.ds.Table;
import org.risource.util.NameUtils;

/** Loader for Handler classes. 
 *
 *	Note that because this class is in the <code>org.risource.dps.handle</code>
 *	package, it can load the non-public handler classes that are declared
 *	in the same files as top-level handlers, which typically handle
 *	elements specialized by one of their attributes.
 *
 */
public class Loader {


  /************************************************************************
  ** Handler cache:
  ************************************************************************/

  protected static Table handlerCache = new Table();

  /** Define (pre-load) a handler class.  
   *<p>	This is available for use in handler implementations, for preloading
   *	the cache with subclasses and variants.  Such a handler should perform
   *	any additional initialization in its default constructor.
   *
   *<p>	It may also be used in specialized processors, or even Agents.
   */
  public static void defHandle(String cname, AbstractHandler handler) {
    handlerCache.at(cname, handler);
  }

  static {
    defHandle("bind", new bindHandler());
    defHandle("tagset", new tagsetHandler());
    defHandle("define", new defineHandler());
    /**/defHandle("action", new actionHandler());
    /**/defHandle("value", new valueHandler());
    defHandle("connect", new connectHandler());
    defHandle("expand", new expandHandler());
    defHandle("extract", new extractHandler());
    defHandle("hide", new hideHandler());
    defHandle("if", new ifHandler());
    /**/defHandle("else", new elseHandler());
    /**/defHandle("elsf", new elsfHandler());
    /**/defHandle("then", new thenHandler());
    defHandle("get", new getHandler());
    defHandle("include", new includeHandler());
    defHandle("let", new letHandler());
    defHandle("logical", new logicalHandler());
    defHandle("namespace", new namespaceHandler());
    defHandle("numeric", new numericHandler());
    defHandle("parse", new parseHandler());
    defHandle("protect", new protectHandler());
    defHandle("repeat", new repeatHandler());
    /**/defHandle("foreach", new foreachHandler());
    /**/defHandle("for", new forHandler());
    /**/defHandle("while", new whileHandler());
    /**/defHandle("first", new firstHandler());
    /**/defHandle("finally", new finallyHandler());
    /**/defHandle("until", new untilHandler());
    defHandle("set", new setHandler());
    defHandle("status", new statusHandler());
    defHandle("subst", new substHandler());
    defHandle("test", new testHandler());
    defHandle("text", new textHandler());
    defHandle("nodeBuilder", new nodeBuilder());
    defHandle("propertyBuilder", new propertyBuilder());
    defHandle("date", new dateHandler());
  }

  /** Load an appropriate handler class and instantiate it. 
   *	If the class is not found and <code>defaultOK</code>, a
   *	new <code>GenericHandler</code> is returned.
   */
  public static AbstractHandler loadHandler(String tag, String cname,
				    int syntax, boolean defaultOK) {
    if (cname == null) return new GenericHandler(syntax);

    String name = ("".equals(cname))
      ? NameUtils.javaName(tag, -1, -1, true, false)
      : cname;

    AbstractHandler h = (AbstractHandler) handlerCache.at(name);
    if (h != null && syntax != 0) h.setSyntaxCode(syntax);
    if (h != null) {
      if (! h.uniquify()) return h;
      else try { 
	return (AbstractHandler) h.getClass().newInstance();
      } catch (Exception e) {}
    }

    h = (GenericHandler) loadHandler(name, syntax, false);
    if (h == null && defaultOK) {
      h = new GenericHandler();
    }
    if (h != null) handlerCache.at(name, h);
    return h;
  }

  /** Load an appropriate handler class and instantiate it. 
   *	If the class is not found and <code>defaultOK</code>, a
   *	new <code>BasicHandler</code> is returned.
   */
  public static AbstractHandler loadHandler(String cname, int syntax,
					    boolean defaultOK) {
    if (cname == null) return new BasicHandler(syntax);

    AbstractHandler h = (AbstractHandler) handlerCache.at(cname);
    if (h != null && syntax != 0) h.setSyntaxCode(syntax);
    if (h != null) {
      if (! h.uniquify()) return h;
      else try { 
	return (AbstractHandler) h.getClass().newInstance();
      } catch (Exception e) {}
    }

    Class c = NameUtils.loadClass(cname, "org.risource.dps.handle.");
    if (c == null) {
      c = NameUtils.loadClass(cname+"Handler", "org.risource.dps.handle.");
    }
    try {
      if (c != null) h = (BasicHandler)c.newInstance();
    } catch (Exception e) {}
    if (h == null && defaultOK) h = new BasicHandler(syntax);
    if (h != null) handlerCache.at(cname, h);
    return h;
  }

}
