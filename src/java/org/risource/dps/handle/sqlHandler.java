////// sqlHandler.java: <sql> Handler implementation
//	$Id: sqlHandler.java,v 1.2 1999-12-29 19:15:07 bill Exp $

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

import java.util.Enumeration;
import org.risource.dps.*;
import org.risource.dps.active.*;
import org.risource.dps.util.*;
import org.risource.dps.tree.*;

import java.sql.*;

/*
import org.gjt.mm.mysql.Driver;
import org.gjt.mm.mysql.EscapeTokenizer;
import org.gjt.mm.mysql.DatabaseMetaData;
import org.gjt.mm.mysql.Statement;
import org.gjt.mm.mysql.Buffer;
import org.gjt.mm.mysql.Debug;
import org.gjt.mm.mysql.Statement;
import org.gjt.mm.mysql.Statement;
import org.gjt.mm.mysql.Statement;
*/
import org.gjt.mm.mysql.*;
import org.gjt.mm.mysql.jdbc1.*;
import org.gjt.mm.mysql.jdbc2.*;

/**
 * Handler for &lt;sql&gt;....&lt;/&gt;  
 *
 * <p>	
 * initial implementation -- ship off string to specified database ...
 * @version $Id: sqlHandler.java,v 1.2 1999-12-29 19:15:07 bill Exp $
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

      // System.out.println("sqlURL=" + sqlURL + " user=" + user );
      try{
	  Class.forName("org.gjt.mm.mysql.Driver").newInstance();
      }catch(Exception e)
      {
	  System.out.println("can't register driver");
	  return;
      }
        
      java.sql.Connection conn = null;
      try {
	  conn = DriverManager.getConnection(sqlURL, 
					  user, 
					  password);
      } catch (java.sql.SQLException X) {
	  System.out.println( X.getMessage() );

	  reportError(in,cxt,"Cannot open connection to database.");
	  putText(out,cxt,"ERROR no database connection established");
	  return;
      }
      if ( cxt.getVerbosity() > 1)
	  System.out.println("Opened JDBC bridge to " + sqlURL + " as " + 
			     user);
      String myquery=content.toString();      
      if(myquery == null) return;
      myquery = myquery.trim();

      TreeElement result = new TreeElement("SQL:result");
      
      try {
	  java.sql.Statement statement = conn.createStatement();

	  // executeUpdate if not a select statement; otherwise query
	  if (myquery.indexOf("SELECT") != 0 &&  
	      myquery.indexOf("select") != 0 ){
	      int rowsUpdated = statement.executeUpdate(myquery);
	      result.setAttribute("update", myquery );
	      result.setAttribute("rows", String.valueOf( rowsUpdated ) );
	      result.addChild(new TreeText(String.valueOf( rowsUpdated ) +
				      " rows updated") );
	  }
	  else{
	      java.sql.ResultSet r = statement.executeQuery(myquery);
	      java.sql.ResultSetMetaData rsm = r.getMetaData();

	      //Build the result using generic nodes of name sql:
	      //  result, sql:row, sql:col
	      int colCount = rsm.getColumnCount();
	      result.setAttribute("select", myquery );
	      result.setAttribute("rows", String.valueOf( colCount ) );

	      String[] colName = new String[colCount];
	
	      for (int thisCol = 1; thisCol <= colCount; thisCol++){
		  colName[ thisCol-1 ] =  rsm.getColumnName( thisCol) ;
	      }

	      TreeElement row,col;
	      int thisRow = 1;
	      while(r.next()){
		  row = new TreeElement("SQL:row");
		  row.setAttribute("row", String.valueOf( thisRow ) );
		  result.addChild(new TreeText("\n") );
		  result.addChild( row);
		  result.addChild(new TreeText("\n"));
		  for (int i = 1; i <= colCount; i++) {
		      col = new TreeElement( colName[i-1] );
		      col.addChild(new TreeText("\n     " + r.getString(i) + "\n   "));
		      row.addChild(new TreeText("\n   ") );
		      row.addChild(col);
		  }
		  row.addChild(new TreeText("\n") );	
		  thisRow ++;
	      }

	  }
      } catch (java.sql.SQLException X) {
	  System.out.println( X.getMessage() );
	  reportError(in,cxt, "SQLException in sqlHandler");
      }

      try {
	  conn.close();
      } catch (java.sql.SQLException X) {
	  reportError(in,cxt, "Cannot close JDBC connection.");
      }
      if ( cxt.getVerbosity() > 1)
	  System.out.println("Closed JDBC bridge to " + sqlURL);

      out.putNode( result );

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
