// Header.java
// $Id: Headers.java,v 1.4 1999-03-12 19:49:57 pgage Exp $

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import org.risource.ds.Table;
import org.w3c.www.http.HttpEntityMessage;
import org.w3c.www.http.HttpMessage;
import org.w3c.www.mime.MimeType;

import org.w3c.www.mime.MimeTypeFormatException;

import org.risource.pia.BadMimeTypeException;

public class Headers {
  /**
   * HttpMessage
   */
  private HttpEntityMessage zheaders;

  /**
   * @return content length
   */
  public int contentLength(){
    int len = -1;
    if( zheaders!= null )
      len = zheaders.getContentLength();
    return len;
  }

  /**
   * set content length
   */
  public void setContentLength(int length){
    if( zheaders!= null )
      zheaders.setContentLength( length );
  }

  /**
   * @return content type
   */
  public String contentType(){
    if( zheaders!= null ){
      MimeType mt = zheaders.getContentType();
      if( mt != null )
	return mt.toString();
      else return null;
    }
    else
      return null;
  }

  /**
   * Set content type.
   *	@param type the MIME type (e.g. <code>text/html</code>).
   *	@exception org.risource.pia.BadMimeTypeException if type cannot be parsed as
   *	 a valid MIME type.
   */
  public void setContentType(String type) throws BadMimeTypeException{
    if( zheaders!= null ){
      try{
	MimeType mt = new MimeType( type );
	zheaders.setContentType( mt );
	org.risource.pia.Pia.debug(this,"Content type set to "+type);
      }catch( MimeTypeFormatException e ){
	throw new BadMimeTypeException("Bad mime type.");
      }
    }
  }

  /**
   * Set content type
   * @param type the Mime type
   */
  public void setContentType(MimeType type)
    {
      if (zheaders != null) {
	zheaders.setContentType(type);
	org.risource.pia.Pia.debug(this,"Content type set to "+type.toString());
      }
    }

  /**
   * @return a header field value as a String.
   */
   public String header(String name){
    if( zheaders!= null )
      return zheaders.getValue( name );
    else
      return null;
  }

  /**
   * set value of a field
   */
  public void setHeader(String name,
                       String strval){
    if( zheaders!= null )
      zheaders.setValue( name, strval );
  }


  /**
   * remove a field
   */
  public void removeHeader(String name) {
    if (zheaders != null) {
      zheaders.removeHeader(name);
    }
  }

  /** 
  * Sets all the headers to values given in hash table
  * hash keys are field names
  * throws exception if not allowed to set.
  */
  public void setHeaders(Table table){
    if( zheaders!= null ){
      Enumeration keys = table.keys();
      while( keys.hasMoreElements() ){
	String key = (String)keys.nextElement();
	String v   = (String)table.get( key );
	zheaders.setValue( key, v );
      }
    }
  }

  /**
   * Return as a string all existing header information for this object.
   * @return String with HTTP style header <tt> name: value </tt><br>
   */
  public String toString(){
    ByteArrayOutputStream out;
    if( zheaders!=null ){
      out = new ByteArrayOutputStream();
      try{
	zheaders.emit( out, HttpMessage.EMIT_HEADERS);
	return out.toString();
      }catch(IOException e){return null;}
    }
    return null;
  }

  protected Headers(HttpEntityMessage h){
    zheaders = h;
  }


  /**
   * This is public because we nee to test it
   */
  public Headers(){
    zheaders = new HttpEntityMessage();
  }

}







