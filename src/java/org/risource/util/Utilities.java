
// Utilities.java
// $Id: Utilities.java,v 1.8 2000-01-07 22:45:23 bill Exp $

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


package org.risource.util;
import java.io.*;

import org.risource.ds.List;
import org.risource.ds.Registered;
import org.risource.ds.Table;

/**
 * This class contains only static functions; all are general-purpose 
 *	utilities, mainly file-handling and data-format conversion.
 */
public class Utilities {

  /************************************************************************
  ** String and Byte Array I/O:
  ************************************************************************/

  /**
   * Read an entire file and return it as a String.
   */
  public static String readStringFrom( String filename )
       throws NullPointerException, FileNotFoundException, IOException{

      byte[] fromFile = readFrom( filename );
      String data = new String ( fromFile );
      return data;
  }

  /**
   * Read from an InputStream and return it as a String.
   */
  public static String readStringFrom( InputStream stream )
       throws NullPointerException, FileNotFoundException, IOException
  {
    StringBuffer buffer = new StringBuffer();
    int i;

    // === very inefficient; should use InputStreamReader ===
    
    for ( i = stream.read(); i != -1; i = stream.read()) {
      buffer.append((char)i);
    }
    return buffer.toString();
  }

  /**
   * Read to the end of a file and return it as a byte array.
   */
  public static synchronized byte[] readFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
  {
    FileInputStream source = null;
    File f = null;
    byte[]tmp = new byte[1024];
    byte[]buffer = null;
    int bytesRead = -1;
    int total = 0;

    try {
      f = new File( fileName );
      long len = f.length();
      buffer = new byte[ (int)len ];

      source = new FileInputStream( f );
      for(;;){
	bytesRead = source.read( tmp, 0, 1024 );

	if(bytesRead == -1) break;
	System.arraycopy(tmp, 0, buffer, total, bytesRead);
	total += bytesRead;
      }

      return buffer;  
      
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(IOException e3){
      throw e3;
    }catch(Exception e4){
      //ArrayindexOutOfBoundsException, ... -- for arraycopy
      return null;
    }finally{
      if( source != null )
	try {
	source.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    
  }
  

  /**
   * Write a String to a file.
   */
  public static synchronized void writeTo( String fileName, String str )
       throws IOException{
    File f;
    FileWriter destination = null;

    if(str == null) return;
    try{
      f = new File(fileName);
      destination = new FileWriter( f );
      destination.write( str );
      destination.flush();
    }catch(IOException e1){
      // either from open or write.
      throw e1;
    }finally{
      if(destination!=null)
	destination.close();
    }
  }

  /**
   * Append a String to a file.
   *
   */
  public static synchronized  void appendTo( String fileName, String str )
       throws NullPointerException, IOException{
    RandomAccessFile f = null;

    if(str == null) return;

    try{
      f = new RandomAccessFile(fileName, "rw");
      long length = f.length();
      f.seek( length );
      f.write( str.getBytes() );
    }catch(NullPointerException e1){
      // bad file name
      throw e1;
    }catch(IOException e2){
      throw e2;
    }
    finally{
      if(f!=null)
	try {
	f.close();
      }catch(IOException e3){
	throw e3;
      }
    }
  }


  /************************************************************************
  ** File copy and replace:
  ************************************************************************/

  /**
   * Copy a file.
   */
  public static synchronized void copyFile( String srcFileName,
					    String dstFileName )
       throws IOException
  {
    copyFile(new File(srcFileName), new File(dstFileName));
  }

  public static synchronized void copyFile( File src, File dst )
       throws IOException{

    System.err.println("Substituting "+src.getPath() + "->" + dst.getPath());

    FileWriter destination = null;
    FileReader source      = null;

    try{
      source      = new FileReader( src );
      destination = new FileWriter( dst );
      for (int i = source.read(); i != -1; i = source.read()) {
	destination.write((char)i);
      }
      destination.flush();
    }catch(IOException e1){
      // either from open or write.
      throw e1;
    }finally{
      if (source != null)      source.close();
      if (destination != null) destination.close();
    }
  }


  /**
   * Convert a String to a ByteArrayOutputStream
   *
   */
  public static synchronized ByteArrayOutputStream StringToByteArrayOutputStream(String s) {
    ByteArrayOutputStream B = new ByteArrayOutputStream();
    PrintWriter P = new PrintWriter((OutputStream)B);
    P.write(s);
    P.close();
    return B;
  }


  /** 
   * Report an exception.
   */
  public static void report(Exception e) {
    System.err.println(e.toString() + " " + e.getMessage());
    // e.printStackTrace();
  }

  /** 
   * Obtain a complete report on an exception as a String;
   */
  public static String reportString(Exception e) {
    java.io.StringWriter s = new java.io.StringWriter();
    //s.write(e.toString() + " " + e.getMessage());
    e.printStackTrace(new java.io.PrintWriter(s));
    return s.toString();
  }

  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /**
   * Conversion Utilities:
   * convert str to HTML by properly escaping &, <, and >.
   */
  public static synchronized String protect_markup(String str){
    if (str == null) return null;
    String res = "";
    for (int i = 0; i < str.length(); ++i) {
      char c = str.charAt(i);
      if      (c == '&') res += "&amp;";
      else if (c == '<') res += "&lt;";
      else if (c == '>') res += "&gt;";
      else               res += c;
    }
    return res;
  }

   /**
   * Unescape a HTTP escaped string
   * @param s The string to be unescaped
   * @return the unescaped string.
   */
  public static synchronized String unescape (String s) {

	StringBuffer digitBuf = null;
	int hb = -1;
	int lb = -1;
	StringBuffer sbuf = new StringBuffer () ;
	int len  = s.length() ;
	int ch = -1 ;

	for (int i = 0 ; i < len ; i++) {
	  
	  digitBuf = new StringBuffer();
	  switch (ch = s.charAt(i)) {
	  case '%':
	      if( i <= len - 3 ){
		digitBuf.append( s.charAt(++i) );
		digitBuf.append( s.charAt(++i) );
		
		if( digitBuf.length() > 0 ) try {
		  int foo = Integer.parseInt( new String( digitBuf ), 16 );
		  sbuf.append( (char)foo );
		} catch (java.lang.NumberFormatException e) {
		  sbuf.append((char)ch);
		  sbuf.append(digitBuf.charAt(0));
		  sbuf.append(digitBuf.charAt(1));
		}
	      } else {
		sbuf.append((char)ch);
	      }
	      break ;
	  case '+':
	    sbuf.append (' ') ;
	    break ;
	  default:
	    sbuf.append ((char) ch) ;
	  }
	}
	return sbuf.toString() ;
  }


   
    
  /**
   * encode a byte stream into base 64
   */
  public static String encodeBase64( byte[] buf){
     byte[] result = new byte[(buf.length + 2) / 3 * 4];
     for (int i = 0; i < buf.length; i += 3){
       encodeBase64Word(buf,i,buf.length - i, result);
     }
     return new String(result);
  }

    /** base64 6 bit values */
  private final static char b64_array[] = {
        //       0   1   2   3   4   5   6   7
                'A','B','C','D','E','F','G','H', // 0
                'I','J','K','L','M','N','O','P', // 1
                'Q','R','S','T','U','V','W','X', // 2
                'Y','Z','a','b','c','d','e','f', // 3
                'g','h','i','j','k','l','m','n', // 4
                'o','p','q','r','s','t','u','v', // 5
                'w','x','y','z','0','1','2','3', // 6
                '4','5','6','7','8','9','+','/'  // 7
        };


  // put into result offset for result is offset times 4/3
  private static void encodeBase64Word(byte buf[], int offset, int len, byte result[]){
        
	byte a,b,c;
	int roff = offset * 4 / 3;
	a = buf[offset];
	b = 0;
	c = 0;
        if (len > 1) {
            b = buf[offset + 1];
	}
        if (len > 2) {
            c = buf[offset + 2];
	}
	result[roff] = (byte)b64_array[(a >>> 2) & 0x3F];
	result[roff + 1] = (byte)b64_array[((a << 4) & 0x30) + ((b >>> 4) & 0xf)];
	result[roff + 2] = (byte)b64_array[((b << 2) & 0x3c) + ((c >>> 6) & 0x3)];
	result[roff + 3 ] =(byte)b64_array[c & 0x3F];
	if (len < 3) result[roff + 3 ] = (byte)'=';
	if (len < 2) result[roff + 2 ] = (byte)'=';
  }


/**
     * decode a base64 String.
     * @param input The string to be decoded.
     */
  public static byte[] decodeBase64(String input) {

    byte bytes[] = input.getBytes();

    //    int l = bytes.length * 3 / 4;
    int lOrig = bytes.length;

    if (lOrig == 0)
	return null;


    //  check for padding
    //    if(bytes[l-1] == '='){ System.out.println("decrem 1"); l--;}
    //if(bytes[l-1] == '=') { System.out.println("decrem 2"); l--;}
    if(bytes[lOrig - 1] == '='){ lOrig--;}
    if(bytes[lOrig - 1] == '=') { lOrig--;}
    int l = lOrig * 3 / 4;

    byte result[] =  new byte[l];
    for(int limit = 0; limit <= bytes.length - 4; limit += 4){
      for( int i =0; i< 4; i++) 
	bytes[limit+i] = (byte) cvB64(bytes[limit+i]);
      int decoded =  decodeBase64Word(bytes, limit,result);
      if( decoded < 3) return result;//return null if invalid string
    }
    return result;
  }

// utility function for converting base character
    private  static final int cvB64 (int ch) {
	if ((ch >= 'A') && (ch <= 'Z')) {
	    return ch - 'A' ;
	} else if ((ch >= 'a') && (ch <= 'z')) {
	    return ch - 'a' + 26 ;
	} else if ((ch >= '0') && (ch <= '9')) {
	    return ch - '0' + 52 ;
	} else {
	    switch (ch) {
	      case '=':
		  return 65 ;
	      case '+':
		  return 62 ;
	      case '/':
		  return 63 ;
	      default:
		  return -1 ;
	    }
	}
    }

  
  private static int decodeBase64Word(byte[] buf,int off,  byte[] result)
   {
     int roff = off * 3 / 4;
     int a=off;
     int b=off+1;
     int c= off+2;
     int d= off+3;
     if (buf[a] < 0 || buf[a] < 0 || buf[b] > 64 ||buf[b] > 64 ) return 0;
     result[roff] = (byte) (((buf[a] & 0x3f) << 2) | ((buf[b] & 0x30) >>> 4));
     if (buf[c] > 64 ) return 1;
     result[roff+1] = (byte) (((buf[b] & 0x0f) << 4) | ((buf[c] &0x3c) >>> 2));
     if (buf[d] > 64 ) return 2;
     result[roff+2] = (byte) (((buf[c] & 0x03) << 6) | (buf[d] & 0x3f) );
     return 3;
   }

  /** Decode a string in <code>x-www-form-urlencoded</code> format.
   *	This exists only because java.net.URLDecoder doesn't. <p>
   *	
   *	To convert a String, each character is examined in turn: 
   *	<ul>
   *	  <li> The ASCII characters 'a' through 'z', 'A' through 'Z',
   *		and '0' through '9' remain the same.   
   *	  <li> The plus sign character '+' is converted to a space ' '. 
   *	  <li> All other characters are converted into the 3-character
   *		string "%xy", where xy is the two-digit 
   *		hexadecimal representation of the lower 8-bits of the
   *		character.  
   *	</ul>
   */
  public static final String urlDecode(String s) {
    if (s.indexOf('+') < 0 && s.indexOf('%') < 0) return s;

    String ss = "";
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '+') {
	ss += ' ';
      } else if (c == '%') {
	String foo = "" + s.charAt(++i);
	foo += s.charAt(++i);
	int cc = Integer.valueOf(foo, 16).intValue();
	ss += (char)cc;
      } else {
	ss += c;
      }
    }
    return ss;
  }



  public static void main(String[] args){
    String fn = args[0];
    try{
      Utilities.writeTo( fn, "Hello world." );
      Utilities.appendTo( fn, " Baguette." );
    }catch(Exception e){
      System.out.println( e.toString() );
    }
  }


}












