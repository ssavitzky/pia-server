////// sqlHandler.java: <sql> Handler implementation
//	$Id: sqlHandler.java,v 1.1 1999-11-29 23:10:15 wolff Exp $

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

import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import java.sql.*;

/**
 * Handler for &lt;sql&gt;....&lt;/&gt;  
 *
 * <p>	
 * initial implementation -- ship off string to specified database ...
 * @version $Id: sqlHandler.java,v 1.1 1999-11-29 23:10:15 wolff Exp $
 * @author steve@rsv.ricoh.com
 */

public class sqlHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;sql&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, ActiveNodeList content) {
    // Actually do the work. 
      String sqlURL = atts.getAttribute("database");
      String user = atts.getAttribute("user");
      String password = atts.getAttribute("password");
      String columns = atts.getAttribute("columns"); // number of columns to return -- apparently this is not available from the result set

      Connection c = null;
      try {
	  c = DriverManager.getConnection(sqlURL, 
					  user, 
					  password);
      } catch (java.sql.SQLException X) {
	  reportError(in,cxt,"Cannot open connection to database.");
	  putText(out,cxt,"ERROR no database connection established");
	  return;
      }
      if (verbose)
	  System.out.println("Opened JDBC bridge to " + sqlURL + " as " + 
			     user);
      String myquery=content.toString();      
      if(myquery == null) return;

      try {
	  java.sql.Statement statement = connection.createStatement();
	  // if we assume only one result set per statement then this Works:
	  ResultSet r = statement.executeQuery(myquery);

	  // if more than one result set is possible, 
	  // use this statement: boolean r = statement.execute(myquery);
	  // see statement class documentation...

          //Build the result using generic nodes of name sql:result, sql:row, sql:col
	  TreeElement result=TreeElement("SQL:result");
	  TreeElement row,col;
	  while(r.next()){
	      row = TreeElement("SQL:row");
	      result.addChild(row);
	      for (int i = 1; i <= columns; i++) {
	          col = TreeElement("SQL:col");
		  col.addChild(TreeText(r.getString(i)));
		  row.addChild(col);
	      }
	  }
      } catch (java.sql.SQLException X) {
	  reportError(in,cxt, "Cannot execute query through JDBC connection.");
      }

      try {
	  c.close();
      } catch (java.sql.SQLException X) {
	  reportError(in,cxt, "Cannot close JDBC connection.");
      }
      if (verbose)
	  System.out.println("Closed JDBC bridge to " + sqlURL);

      return result;

  }

  /** This does the parse-time dispatching. 
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //Use this (and uncomment class def below) if want different actions based on parameters
    //    if (dispatch(e, "")) 	 return sql_.handle(e);
    // For now, use standard action given above
    return this;
  }
  */

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public sqlHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  sqlHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}



/** sub handlers not currently used for this tag **
class sql_ extends sqlHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, ActiveNodeList content) {
    unimplemented (in, cxt); // do the work
  }
  public sql_(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new sql_(e); }
}
*/
