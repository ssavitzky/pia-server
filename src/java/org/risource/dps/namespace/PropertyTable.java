////// PropertyTable.java: Node and String Lookup Table
//	$Id: PropertyTable.java,v 1.5 1999-11-04 22:33:48 steve Exp $

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

package org.risource.dps.namespace;

import org.w3c.dom.NodeList;

import org.risource.dps.active.*;
import org.risource.dps.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.*;

import java.util.Enumeration;

import org.risource.ds.List;
import org.risource.ds.Table;

/**
 * A Namespace optimized for storing site resource properties. 
 *
 * <p>	The 
 *
 * @version $Id: PropertyTable.java,v 1.5 1999-11-04 22:33:48 steve Exp $
 * @author steve@rsv.ricoh.com
 *
 * @see org.risource.site
 * @see org.risource.site.Resource
 */

public class PropertyTable extends BasicNamespace implements PropertyMap {

  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Look up a name and return its value.
   *
   * @param name the name to look up
   * @return the list of nodes associated with the name.  If the binding
   *	is an Element (the typical case) its children are returned.
   */
  public ActiveNodeList getValueNodes(Context c, String name) {
    ActiveNode binding = getBinding(name);
    if (binding == null) {
      binding = getActiveAttr(name);
    }
    if (binding == null) return null;
    else if (binding instanceof Namespace) {
      return new TreeNodeList(((Namespace)binding).getBindings());
    } else {
      return binding.getValueNodes(c);
    }
  }

  public ActiveNodeList getValueNodes(String name) {
    return getValueNodes(null, name);
  }

  /** Set the value of a name.
   *
   *<p>	If the name is not present, a new Element is created with the
   *	given name as its tagname, and the given nodelist becomes its content.
   *
   * @param name the name to bind
   * @param value the value to associate with it.
   */
  public void setValueNodes(String name, ActiveNodeList value) {
    ActiveNode binding = getBinding(name);
    if (binding != null) {
      // === this is wrong, but difficult to fix just now. 
      setBinding(name, new TreeElement(name, null, value));
    } else {
      setBinding(name, new TreeElement(name, null, value));
    }
  }

  /** Bind a named Element within a map that represents a property.
   *
   * @param binding the element to store.
   * @see org.w3c.dom.NamedNodeMap
   */
  public  void setNamedBinding(String map, String name, ActiveElement elt) {
    if (elt instanceof Namespace) {
      setBinding(name, elt);
      return;
    }

    Namespace binding = (Namespace)getBinding(map);
    
    if (binding == null) {
      binding = new BasicNamespace(map);
      binding.setBinding(name, elt);
      setBinding(map, (BasicNamespace)binding);
    } else {
      binding.setBinding(name, elt);
    }
  }

  /** Bind an Element that represents a property.
   *
   *<p> The exact behavior depends on the element's attributes.  If it has a
   *	<code>name</code> or <code>ID</code> attribute, it is associated with
   *	that name in a Namespace binding, which in turn is associated with
   *	the Element's tagname.  Otherwise, it is simply associated with the 
   *	Element's tagname, just like a NamedNodeMap.
   *
   * @param binding the element to store.
   * @see org.w3c.dom.NamedNodeMap
   */
  public void setPropertyBinding(ActiveElement binding) {
    String name = binding.getAttribute("name");
    if (name == null) name = binding.getAttribute("ID");
    if (name == null) {
      setBinding(binding.getTagName(), binding);
    } else {
      setNamedBinding(binding.getTagName(), name, binding);
    }
  }

  /** Look up a name and return its value (in internal form) as a String. 
   *
   * @param name the name to look up
   */
  public String	getProperty(String name) {
    ActiveNodeList value = getValueNodes(name);
    return (value == null)? null : TextUtil.getCharData(value);
  }

  /** Set a string associated with a name.
   *
   * @param name the name to set
   * @param value the corresponding value string
   */
  public void setProperty(String name, String value) {
    ActiveNodeList vl = new TreeNodeList(new TreeText(value));
    setValueNodes(name, vl);
  }

  /** Look up a name and return its value as a String, with default.
   *
   * @param name the name to look up
   * @param dflt the default value if the name is not found.
   */
  public String getProperty(String name, String dflt){
    ActiveNodeList value = getValueNodes(name);
    return (value == null)? dflt : TextUtil.getCharData(value);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public PropertyTable() { super("Properties"); }
  public PropertyTable(String name) {
    super("Properties", name); 
  }

  public PropertyTable(String tag, String name) {
    super(tag, name); 
  }

  public PropertyTable(String name, ActiveAttrList attrs) {
    super("Properties", name, attrs); 
  }

  public PropertyTable(String name, java.util.Properties props) {
    super("#Properties", name); 
    Enumeration e =  props.propertyNames();
    while( e.hasMoreElements() ){
	String k = (String) e.nextElement();
	String v = props.getProperty( k );
	setProperty(k, v);
    }
    
  }

}
