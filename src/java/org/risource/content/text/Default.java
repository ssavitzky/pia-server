//  Default.java
// $Id: Default.java,v 1.7 1999-10-14 00:52:09 steve Exp $

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

import org.risource.content.GenericContent;
import org.risource.content.StreamingContent;

import org.risource.pia.Content;
import org.risource.pia.ContentOperationUnavailable;
import org.risource.pia.Pia;

import org.risource.ds.Table;
import org.risource.ds.List;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Content wrapper for streams of text.
 * 	default for text/....  mime type.
 * 	Uses character reader / writer internally.
 * 	Incoming streams are converted to character readers, output written to
 * 	byte array after conversion (similar to OutputStreamWriter).
 * 	Manipulations can be done to character buffer.
 */
public class Default extends StreamingContent {

   protected char[] buf, pending;

  // override super class
  protected  void createBuffer( int size){
    buf = new char[size];
    buflength=size;
    pending = new char[0];
  }


  protected String encoding;
  //  transform original objects into reader
  protected Reader cReader;

  protected OutputStreamWriter cWriter ;
  protected Hashtable fileWriters = new Hashtable();

  /************************************************************
  ** constructors:
  ************************************************************/

  public Default(){
    // caller must set source
  }
  

  public Default(InputStream in){
    source(in);
  }


  public Default(Reader in){
    source(in);
  }

  /**
   * override source for Readers
   */
  public void source(Object in) throws ContentOperationUnavailable {
    try{
      source((Reader) in);
      return;
    } catch (Exception e){
      // conversion error use super class method
    }
     super.source(in);
  }

  public void source(InputStream in){
    source = in;	   //  conversion to reader happens at beginProcessing
    enterState(START);
  }

  public void source(Reader in) {
      //source=(InputStream)in;  // getSource just returns this
    cReader = in;	   
    enterState(START);
  }

  /************************************************************
  ** access methods:
  ************************************************************/

  /**
   * initialize everything prior to beginning processing
   */
  protected void beginProcessing(){
    // initialize buffer and enter states
    super.beginProcessing();

    if(encoding == null) initializeEncoding();
    if(cReader == null) {
      cReader = streamToReader(source); 
    }

  }

  /**
   * convert stream to reader
   */
  protected InputStreamReader  streamToReader(InputStream source){
    if(encoding == null) initializeEncoding();
    // define local variable in case use as utility
    InputStreamReader cReader = null;
    if ( encoding != null ){
      try{
	cReader = new InputStreamReader( source, encoding);
      } catch(UnsupportedEncodingException e) {
	// encoding not supported?? 
	// punt for now and use defaults
      } 
    }
    if(cReader == null)
	cReader = new InputStreamReader( source);

    return cReader;
  }

  /**
   * convert stream to appropriate writer
   */
  protected OutputStreamWriter streamToWriter( OutputStream out){
    if( out == null) {
      return null;
    }
    OutputStreamWriter writer = (OutputStreamWriter)fileWriters.get(out);
    if(writer == null) {
      if(encoding != null){
	try{
	  writer = new OutputStreamWriter( out, encoding);
	} catch(UnsupportedEncodingException e) {
	  System.err.println("streamToWriter error: " +e.getMessage());
	  // encoding not supported?? 
	  // punt for now and use defaults
	}
      } 
    }
    if(writer == null)
      writer = new  OutputStreamWriter(out);

    // Needs to be closed eventually
    fileWriters.put(out, writer);

    return writer;
  }


  /************************************************************
   ** internal read and write operations 
   ************************************************************/

   /**
    * set sink for output operations
    * subclass may want to change class or manipulate sink
    * The sink is associated with data output, but not taps
    */
   protected void setSink(OutputStream stream){
      sink = stream;
      cWriter = streamToWriter(stream);
   }

    /** Needed by annotatedhtml.  Need to clone the
     * content class and get access to the sink
     */
    public OutputStream getSink() {
	return sink;
    }

    public InputStream getSource() {
	return source;
    }
    public Reader getReader() {
	return cReader;
    }


   /**
    * unset sink -- output complete
    */
   protected void unsetSink(){
     if(cWriter != null) try {
       cWriter.flush();
     } catch (IOException e){}
     sink = null;
     cWriter = null;
   }

  // Close OutputStreamWriters associated with taps
  protected void closeTaps() {

    Enumeration wrEnum = fileWriters.keys();

    while(wrEnum.hasMoreElements()) {
      OutputStreamWriter ow = (OutputStreamWriter)fileWriters.get(wrEnum.nextElement());

      try {
	ow.flush();
      }
      catch(IOException e) {
	e.printStackTrace();
      }
      try {
	ow.close();
      }
      catch(IOException e) {
	e.printStackTrace();
      }

    }
    fileWriters.clear();
  }


   /**
    *  write outgoing data
    * called by processOut which maintains the pointers
    */
   protected int writeData(int length) throws IOException{
     if(cWriter == null) return 0; // this should not happen
     if(length + nextOut > buf.length)  length = buf.length - nextOut;

     // do the actual work
     cWriter.write(buf,nextOut, length);
     //  exception could be caught here
     return length;
     
   }

  /**
   * method for sending data out taps.... inefficient but not used often
   */
   protected int writeData(OutputStream out, int start,int length) throws IOException{
     OutputStreamWriter w = streamToWriter(out);

     // do the actual work
     try {
       w.write(buf, start, length);
     }
     catch(IOException e) {
       System.err.println(e.getMessage());
     }

     //  exception could be caught here
     return length;
   }


   /**
    * read data from input source (Reader) into character buffer.
    * try to read length data, up to buffer size
    */

   protected int readData(int length) throws IOException
     {
       int read = 0;
       if(length + nextIn > buf.length)  length = buf.length - nextIn;
	
       if(cReader == null || length <= 0) return read;
       
       try {
	 read = cReader.read( buf,nextIn,length);
       }catch(IOException e){
				// read stream closed?
	 throw(e);
       }

       return read;
     }




  /**
   * insert any pending items
   * @return number of items inserted
   */

  protected int insertPending(int length){
    if(pending ==null || pending.length < 1)  return 0;
    int len = (pending.length > length) ? length : pending.length;
    try{
      System.arraycopy( pending, 0, buf, nextIn, len);
    }catch(ArrayIndexOutOfBoundsException e1){
      // this should not happen 
      len = 0;
    } catch(ArrayStoreException e2){
      // this should not happen --  conversion error
      // pretend nothing  happened since retry will cause errors
    }
    // totally inefficient...
    if(len == 0) return 0;
    if(len < pending.length){
      char[] p = pending;
      pending = new char[p.length - len];
      try{
	System.arraycopy( p, len,  pending, 0, pending.length);
      }catch(ArrayIndexOutOfBoundsException e1){
	// this should not happen  
	pending = new char[0];
      } catch(ArrayStoreException e2){
      // this should not happen --  conversion error
	pending = new char[0];
      }
    } else 	pending = new char[0];  //full pending buffer written
     return len;
  }


  /************************************************************
  ** character conversion
  ************************************************************/


   /**
    * initialize  character  encoding
    * currently attempts to get a character to byte translator as
    * means of checking the encoding.  Obviously this should be improved
    * when sun provides better encoding support.
    */

   protected void initializeEncoding() {
     if(headers != null){
       encoding = headers.header("Charset"); // should be ContentEncoding?
       //charset actually is a parameter of content type -- does header support?
       // should add headers.encoding method
     }
     /* === CharToByteConverter is a Sun class!
     if(encoding == null)
       encoding =CharToByteConverter.getDefault().getCharacterEncoding();
     try  {
       CharToByteConverter.getConverter(encoding);
     }  catch(Exception e){
       // if encoding not supported, get default
       encoding =CharToByteConverter.getDefault().getCharacterEncoding();
     }
     */
   }
  


  /************************************************************
  ** agent interactions:
  ************************************************************/
 
  /************************************************************
  ** content specific operations
  ************************************************************/

  /** store addition or replacement information */
  Table additions, replacements;
  List replacementKeys ;


  public void add(Object moreContent, int where) throws ContentOperationUnavailable {
    add(moreContent.toString(),where);
  }
  

  private boolean firstTime=true;
  private boolean lastTime = false;
  /**
   * process buffer with any replacements 
   * start at outLimit and process upTo nextIn
   * @param newChars number of characters added to input since last call
   * @return characters processed
   */
 protected  int processBuffer(int newChars){

   insertAdditions();
   
    int processed = 0;
    // do any substitutions between out limit and next in
    if(replacements  != null){
       processed = processReplacements(buf,outLimit,nextIn);
    }  else {
      // processed all characters up to next in
      processed = (wrapped == olWrapped)? nextIn - outLimit : buflength - outLimit + nextIn;
    }
    if( !moreToProcess() && !isCurrentState( READING) && !lastTime){ 
      lastTime=true;
      insertAddition(-1);
    }

    if(firstTime){
      firstTime = false;
    }
     return processed;   
  }

  /**
   * additions can be put into specific locations -- eventually this should be 
   * obsoleted by using parsed html
   * for now, only understands additions at 0
   */
  protected void insertAdditions(){

    // if there's something at 0, try to insert it
    if(additions != null && additions.at("0") != null) {
      insertAddition(0);
    }
   
  }



  /**
   * place to  for editing operations to store items to be added to input
   * at next opportunity
   */
  protected void insertAddition(int  location){
    if(additions == null) return;
    String addition ="";
    String k =  new Integer(location).toString(); 

    if(location == -1){ 
       if(additions.has(k)) addition = (String) additions.at(k);
       additions.remove(k);

       //insert anything that is left as white space to make up        
        //for the changes in content length 
       StringBuffer sb = new StringBuffer();
       Enumeration e = additions.elements();
        while (e.hasMoreElements()) {
         String s = (String) e.nextElement();
          for(int i=0;i<s.length();i++){
            sb.append(' ');
            // note we should probably remove element from table..
	  }
	}
	addition = sb.insert(0,addition).toString();
    } else {
       if(!additions.has(k)) return ;
       addition = (String) additions.at(k);
    }
    
    
    if( addition != null){
      if( location == 0)
	insert( addition, nextOut); // earliest possible spot
	else appendToPending(addition);
      additions.remove(k);
      
    }
  }

  /************************************************************
  ** operations for manipulating a "pending" buffer
  ************************************************************/


  protected void appendToPending(String s){
    char[] c = new char[s.length()];
    try{
      s.getChars(0,c.length,c,0);
      appendToPending(c);
    }catch(Exception e){
      // string problems?
    }
  }


  protected void prependToPending(String s){
    char[] c = new char[s.length()];
    try{
      s.getChars(0,c.length,c,0);
      prependToPending(c);
    }catch(Exception e){
      // string problems?
    }
  }

  protected void prependToPending( char[] c){
    prependToPending(c, 0, c.length);
  }

  protected void appendToPending( char[] c){
    appendToPending(c, 0, c.length);
  }

  /**
   * add contents of array c  starting at src to our pending buffer
   */
  protected void prependToPending( char[] c,int src, int len){
    int length = len + pending.length;
     char[] p = pending;
     pending = new char[length];
     try{
	System.arraycopy( c, src,  pending, 0, len);
	System.arraycopy( p, 0,  pending, len, p.length);
      }catch(ArrayIndexOutOfBoundsException e1){
	// this should not happen 
      } catch(ArrayStoreException e2){
	//if pending and o  are different types, we are screwed.  Ignore o
	pending = p;
      }
  }

  /**
   * add contents of c to pending buffer
   */

  protected void appendToPending( char[] c,int src, int len){
    int length = len + pending.length;
    char[] p = pending;
    pending = new char[length];
     
     try{
	System.arraycopy( p, 0,  pending, 0, p.length);
	System.arraycopy( c, src,  pending, p.length, len);
      }catch(ArrayIndexOutOfBoundsException e1){
	// this should not happen 
      } catch(ArrayStoreException e2){
	// should not happen
	pending = p;
      }
  }


  /************************************************************
  ** character operations
  ************************************************************/


  /**
   * character operations are performed on segment between
   * outLimit and nextIn as data moves through
   * these operations may impact things like content length
   * === TBD fix content length for special cases===
   * Fix content length operations other than add
   */
  public void add(String addition, int location) throws ContentOperationUnavailable
  {
    if(isVisitedState(END) || (isVisitedState(WRITING) && location == 0)) {
      throw(new  ContentOperationUnavailable(" too late for insertion at"+ location));
    }

    if(additions == null){
      additions = new Table();
    }
    String add = addition;

    int addLength = add.length();  //need to update the content length header

    String k =  new Integer(location).toString();
    if(additions.at(k) != null){
      add = additions.at(k) + add;
    }
    additions.at(k, add);
    // change the length specified in the content header -- this may do no good
    // if we have already started writing data out...
    if(headers.contentLength() > 0) {
      headers.setContentLength(headers.contentLength() + addLength);
    }
    

    Pia.debug("adding "+add + " at "+  location);
  }
  


  /**
    * Replace target with replacement.
    * 	The exact meaning is subject to interpretation.
    *
    * <p> This was originally in Content, but is actually never used.
    *
    *	@param target	the object to be replaced
    *	@param replacement if null, the target is simply removed.
    * 	@exception org.risource.pia.ContentOperationUnavailable if the operation
    *	cannot be performed by this type of Content.
   */

  public void replace(String target, String replacement)
    throws ContentOperationUnavailable
  {
    
   if( isVisitedState(READING)){
     throw(new ContentOperationUnavailable(" already reading"));
   }

      if( replacements == null){
	 replacements =  new Table();
	 replacementKeys =  new List();
      }
      if( replacements.has( target)){
	throw(new ContentOperationUnavailable(" already replacing" + target + " with " + replacement));
      }
      //store character arrays
      replacements.at(target, replacement);
      replacementKeys.push(target);
  }
	
  /**
   * other operations will include indexing,...
   */


  /**
   * do any replacements --  incredibly inefficient
   *  return num of chars looked at
   */
  protected int processReplacements(char[] c,int start, int stop){
          // really inefficient -- loop for each key
    //should build state machine 
    int max = 0;
    int i;
    int processed = 0;
    // wrap around is a killer here
    if(stop < start) stop += c.length;
    for(i=start;i<stop;i++){
      for(int j=0;j< replacementKeys.nItems();j++){
	String s = (String) replacementKeys.at(j);
	if(s.length()>max) max = s.length();
	char k = s.charAt(0);
	if(c[i%c.length] == k){
	  if(s.equals(getString(c,i%c.length,s.length()))){
	     String r = (String) replacements.at(s);
	     // delete S
	     delete(i,s.length());
	     // insert replacement and modify nextIn
	     insert(r,i%c.length);
	      processed = i - start;
	     // cannot process any more because everything in pending now
	     return ( processed - max > 0) ? processed : 0;
	  }
	}
      }
    }
    return ( processed - max > 0) ? processed : 0;
  }



  /**
   * convert characters into string including wrap around
   */
  protected String getString(char[] c, int start, int length){
    
    if(start+ length > c.length){
      return(getString(c,start,c.length-start) + getString(c,0,length-(c.length-start)));
    }
    try{
      return  new String(c,start,length);
    }catch(Exception e) {
    }
    return "";
  }

  /**
   * remove elements --  does not check to make sure that we are not
   * doing something dumb like removing items already output
   */

  protected int delete(int start, int length){
    if( length > buf.length) {// trouble
      length=buf.length;
    }
    char[] temp= new char[length];
    int cstart = start +  length;
    if(cstart > buf.length) cstart = cstart % buf.length;
    int l = length;

    try{
    // copy old into temporary array
      if(cstart + length >buf.length) l=buf.length - cstart;
      System.arraycopy(buf,cstart,temp,0,l);
      System.arraycopy(buf,cstart+l,temp,l, length - l);

    // copy old into current
       l = length;
       if(start + length >buf.length) l=buf.length - start;
       System.arraycopy(temp,0,buf, start,l);
       System.arraycopy(temp,l,buf, start + l, length - l);
    } catch(Exception e){}

    // reset nextIn assume outLimit, nextOut unchanged
    advanceNextIn(-length);
    return length;
  }


  /**
   * insert string at position by prepending to pending buffer, modifies nextIn
   */
  protected void insert(String s,int position){
    char[] c = new char[s.length()];
    try{
       s.getChars(0,c.length,c,0);
       insert(c, position);
    }catch(Exception e){
      // string problems?
    }
  }
    
    
  /**
   * insert characters at position, if not enough room, prepend extra to pending
   * modifies nextIn.
   */
  protected void insert(char[] chars,int position){
    // easy way is to prepend to  pending everything after position
    Pia.debug("inserting " + chars.length + "chars at " + position);
     int cpos=0,csize=chars.length;
     if(wrapped && position >= nextIn){
       // move wrapped stuff
       prependToPending(buf,0,nextIn);
       prependToPending(buf,position,buflength - position);
       // subtract from next in
       advanceNextIn( (position - buflength) - nextIn);
       
     } else if(position <= nextIn) {
       
       prependToPending(buf,position,nextIn - position);
       advanceNextIn( position - nextIn);
     } else {
       // invalid specification for position... just append chars...
     }
     prependToPending(chars);
     // next in should be at position now
  }

 /**
   *return true if more processing needs to be done
   */

  protected boolean moreToProcess(){
    return (pending.length > 0 ) || super.moreToProcess();    
  }

}
