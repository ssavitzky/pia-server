//  html.java
// html.java,v 1.9 1999/03/01 23:45:08 pgage Exp

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


package org.risource.content.text;

import org.risource.pia.Transaction;
import JP.ac.osaka_u.ender.util.regex.RegExp;
import JP.ac.osaka_u.ender.util.regex.MatchInfo;

/**
 * class for html which is not parsed. only difference with plain text is
 * that inserts of text happen after the body tag.
 * more sophisticated processing should use the ParsedContent class which uses
 * the interpreter.
 */

public class html extends Default
{

    protected Transaction transaction = null;

    /**   changes meaning of location 0 in additions to be just after the body tag
     */

    protected void insertAddition(int position){
      String k =  new Integer( position).toString();
      if(additions == null || !additions.has(k)) return ;

      String add = (String) additions.at(k);
    org.risource.pia.Pia.debug( this," inserting "+add + " at "+  position);


      if(position == 0){
	// insert after body tag
	// this is a hack -- eventually use html parser
	RegExp re = null;
	MatchInfo mi = null;
	// size of string to look for head and body in
	// this may be too small
        int size = 2048;
	if(buf.length < size) size = buf.length;
        if(! wrapped && size > nextIn)  size = nextIn;

	String s =  new String(buf,0, size);
	int bend=0; // end of body tag
	int hend = 0;  // end of head tag
	try{
	  // first look for a head
	  re = new RegExp("</(head|Head|HEAD)[^>]*>");
	  mi = re.match(s );
	  hend = mi.end();
	  bend = hend;

	  re = new RegExp("<(body|Body|BODY)[^>]*>");
	  mi = re.match(s, bend, s.length() );
	  bend=mi.end();

	  // if there is a head, but no body, assume it is a frame
	  // specification and we should do nothing
	  if(bend > 0 && hend == bend)  return;

	}catch(Exception e){
	  org.risource.pia.Pia.debug( this,"reg exp failed" );     
	  //if (org.risource.pia.Pia.debug()) e.printStackTrace();
	}
	

	  // if there is no body, assume it is a frame
	  // specification or something else bad and we should do nothing
	  if(bend == 0 )  {
    	    org.risource.pia.Pia.debug( this," no body tag detected");
            return;
	  }
	  
	org.risource.pia.Pia.debug( this," putting at"+  bend);
	
	 insert(add,bend);
         // remove this addition
         additions.remove(k);
	 
         return;
	 
      }
      
      // else
    org.risource.pia.Pia.debug(" super insert");
      super.insertAddition(position);
    }

  public html() {
    super();
  }

  public html(java.io.InputStream in){
    super(in);
  }

  public html(java.io.Reader in){
    super(in);
  }

    public html(java.io.InputStream in, Transaction trans){
	super(in);

	transaction = trans;
    }

    public html(java.io.Reader in, Transaction trans){
	super(in);
	
	transaction = trans;
    }


}
