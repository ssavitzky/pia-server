// ObjectFile.java
// $Id: ObjectIO.java,v 1.2 2001-04-03 00:05:34 steve Exp $

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


package org.risource.util;

import java.io.*;

import java.lang.ClassNotFoundException;

import org.risource.ds.List;
import org.risource.ds.Registered;
import org.risource.ds.Table;

/**
 * This class contains utilities that read and write Java serialized objects. 
 */
public class ObjectIO {

  /************************************************************************
  ** Object I/O:
  ************************************************************************/

  /**
   * Read an Object from a file.  If the object implements the Registered
   *	interface it is registered.  If the object is a List, every object
   *	on the list is registered if necessary.
   *
   *	@see org.risource.ds.List
   *	@see org.risource.ds.Registered
   */
  public static synchronized Object readObjectFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
  {
    FileInputStream f = null;
    ObjectInputStream source = null;

    try {
      f = new FileInputStream( fileName );
      source = new ObjectInputStream(f);
      return register(source.readObject());
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(ClassNotFoundException e){
      return null; //throw e;
    }catch(InvalidClassException e){
      return null; //throw e;
    }catch(StreamCorruptedException e){
      return null; //throw e;
    }catch(OptionalDataException  e){
      return null; //throw e;
    }catch(IOException e3){
      throw e3;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
  }
  
  /**
   * Read an Object from a stream.  If the object implements the Registered
   *	interface it is registered.  If the object is a List, every object
   *	on the list is registered if necessary.
   *
   *	@see org.risource.ds.List
   *	@see org.risource.ds.Registered
   */
  public static synchronized Object readObjectFrom( InputStream f )
       throws NullPointerException, IOException
  {
    ObjectInputStream source = null;

    try {
      source = new ObjectInputStream(f);
      return register(source.readObject());
    }catch(NullPointerException e1){
      throw e1;
    }catch(ClassNotFoundException e){
      return null; //throw e;
    }catch(InvalidClassException e){
      return null; //throw e;
    }catch(StreamCorruptedException e){
      return null; //throw e;
    }catch(OptionalDataException  e){
      return null; //throw e;
    }catch(IOException e3){
      throw e3;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
  }
  
  /**
   * Read an entire file into a List.  Any objects that implement the 
   *	Registered interface are registered.  Lists in the input file are
   *	quietly appended.
   *
   *	@see org.risource.ds.List
   *	@see org.risource.ds.Registered
   */
  public static synchronized List readObjectsFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
  {
    FileInputStream f = null;
    ObjectInputStream source = null;
    List list = new List();

    try {
      f = new FileInputStream( fileName );
      source = new ObjectInputStream(f);
      for ( ;; ) {
	Object o = source.readObject();
	if (o instanceof List) list.append((List)o);
	else		       list.push(o);
      }
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(ClassNotFoundException e){
      report(e); //throw e;
    }catch(InvalidClassException e){
      report(e); //throw e;
    }catch(StreamCorruptedException e){
      report(e); //throw e;
    }catch(OptionalDataException  e){
      report(e); //throw e;
    }catch(java.io.EOFException e){
      if ( list.nItems() == 0 ) {
	report(e);
	throw e;
      } // expected.
    }catch(IOException e){
      if ( list.nItems() > 0 ) report(e);
      else throw e;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    register(list);
    return list;
  }
  
  public static synchronized List readObjectsFrom( InputStream f )
       throws NullPointerException, FileNotFoundException, IOException
  {
    ObjectInputStream source = null;
    List list = new List();

    try {
      source = new ObjectInputStream(f);
      for ( ;; ) {
	Object o = source.readObject();
	if (o instanceof List) list.append((List)o);
	else		       list.push(o);
      }
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(ClassNotFoundException e){
      report(e); //throw e;
    }catch(InvalidClassException e){
      report(e); //throw e;
    }catch(StreamCorruptedException e){
      report(e); //throw e;
    }catch(OptionalDataException  e){
      report(e); //throw e;
    }catch(java.io.EOFException e){
      if ( list.nItems() == 0 ) {
	report(e);
	throw e;
      } // expected.
    }catch(IOException e){
      if ( list.nItems() > 0 ) report(e);
      else throw e;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    register(list);
    return list;
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

  /**
   * Register an object if it needs it.  
   *	If the object is a List, register every object in its contents.
   */
  public static Object register(Object obj) {
    if (obj instanceof List) {
      List list = (List) obj;
      for (int i = 0; i < list.nItems(); ++i) {
	Object o = list.at(i);
	if (o instanceof Registered) ((Registered)o).register();
      }
      return list;
    } else {
      if (obj instanceof Registered) ((Registered)obj).register();
      return obj;
    }
  }

  /**
   * Write an Object to a file.
   */
  public static synchronized void writeObjectTo( String fileName, Object o )
       throws IOException{

    FileOutputStream f = null;

    if (o == null) return;
    try{
      f = new FileOutputStream( fileName );
      ObjectOutputStream destination = new ObjectOutputStream(f);
      destination.writeObject( o );
      destination.flush();
      destination.close();
      f.flush();
      f.close();
      f = null;
    }catch(IOException e1){
      throw e1;
    }finally{
      if(f!=null) f.close();
    }
  }

  /**
   * Append an Object to a file.
   *
   */
  public static synchronized  void appendObjectTo( String fileName, Object o )
       throws NullPointerException, IOException{
    RandomAccessFile f = null;

    if (o==null) return;

    try{
      f = new RandomAccessFile(fileName, "rw");
      long length = f.length();
      f.seek( length );
      FileOutputStream ff = new FileOutputStream(f.getFD());
      ObjectOutputStream destination = new ObjectOutputStream(ff);
      destination.writeObject( o );
      destination.flush();
      destination.close();
      f = null;
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

}












