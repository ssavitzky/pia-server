// HeaderFactory.java
// $Id: HeaderFactory.java,v 1.4 1999-03-12 19:49:57 pgage Exp $

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



package org.risource.pia;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.risource.pia.Headers;
import org.risource.pia.Pia;
import org.w3c.www.mime.MimeParserFactory;
import org.w3c.www.mime.MimeParser;
import org.w3c.www.http.HttpEntityMessage;
import org.w3c.www.http.MimeParserMessageFactory; 

/**  HeaderFactory
 * creates an appropriate header object from a HTTP stream
 */
public class HeaderFactory
{
 /**  
  * creates a new headers object 
  */
  public HeaderFactory()
  {
  }

  /**
   * Creates a blank header.
   */
  public Headers createHeader(){
      Headers headers = new Headers();
      return headers;
  }

  /** create a header object from the given stream
   */
  public Headers createHeader(InputStream input)
  {
    // Create the factory:
    MimeParserFactory f = null;

    f = (MimeParserFactory) new MimeParserMessageFactory();
    
    try{
      MimeParser p  = new MimeParser(input, f);

      Pia.debug(this, "Parsing header...\n\n");
      HttpEntityMessage jigsawHeaders = (HttpEntityMessage) p.parse();

      Headers headers = new Headers( jigsawHeaders );
      Pia.debug(this, "Header is done\n");

      return headers;
    }catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
    
  }
}












