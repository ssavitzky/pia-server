// HttpBuffer.java
// $Id: HttpBuffer.java,v 1.3 1999-03-12 19:29:23 steve Exp $

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

// Please first read the full copyright statement in file COPYRIGHT.html

package org.risource.pia;

import java.io.*;

/**
 * A cool StringBuffer like class, for converting header values to String.
 * Note that for good reasons, this class is <em>not</em> public.
 */

class HttpBuffer {
    private static final int INIT_SIZE = 32 ;

    byte    buf[] = null ;
    int     len   = 0;
    byte    sep   = (byte) 0;

    final void append(byte b) {
    	ensureCapacity(1);
    	buf[len++] = b;
    }

    final void append(char ch) {
    	append((byte) ch);
    }

    final void append(int i) {
	append((byte) i);
    }

    final void appendInt(int i) {
	boolean neg = (i < 0);
	int hackpos = len;
	// Emit number in reverse order:
	if ( ! neg )
	    i = -i;
	while (i <= -10) {
	    append((byte) ('0'-(i%10)));
	    i = i / 10;
	}
	append((byte) ('0'-i));
	if ( neg )
	    append((byte) '-');
	// Reverse byte order
	int cnt = (len-hackpos) / 2 ;
	int j   = len-1;
	while ( --cnt >= 0 ) {
	    int pos = hackpos+len-j-1;
	    byte tmp = buf[j];
	    buf[j]   = buf[pos] ;
	    buf[pos] = tmp;
	    j--;
	}

    }

    final void ensureCapacity(int sz) {
	int req = len + sz ;
	if ( req >= buf.length ) {
	    int nsz = buf.length << 1;
	    if ( nsz < req )
		nsz = req + 1;
	    byte nb[] = new byte[nsz];
	    System.arraycopy(buf, 0, nb, 0, len);
	    buf = nb;
	}
	return;
    }

    void append(byte b[], int o, int l) {
	ensureCapacity(l);
	System.arraycopy(b, o, buf, len, l);
	len += l;
    }

    void append(byte b[]) {
      append(b, 0, b.length);
    }

    void append(String str) {
      append (str.getBytes());
    }

    void appendQuoted(String str) {
	append((byte) '"');
	append(str);
	append((byte) '"');
    }
	
    void append(String name, byte sep, String value) {
	append(name);
	append(sep);
	append(value);
    }

    void append(String name, byte sep, int value) {
	append(name);
	append(sep);
	appendInt(value);
    }

    void appendQuoted(String name, byte sep, String value) {
	append(name);
	append(sep);
	append('"');
	append(value);
	append('"');
    }

    void appendQuoted(String name, byte sep, String values[]) {
	append(name);
	if ( values.length > 0 ) {
	    append(sep);
	    append((byte) '"');
	    for (int i = 0 ; i < values.length ; i++) {
		if ( i > 0 )
		    append(',');
		append(values[i]);
	    }
	    append((byte) '"');
	}
    }

    void append(String name, byte sep, String values[]) {
	append(name);
	if ( values.length > 0 ) {
	    append(sep);
	    for (int i = 0 ; i < values.length ; i++) {
		if ( i > 0 )
		    append(',');
		append(values[i]);
	    }
	}
    }

    void append(double d) {
    	append(Double.toString(d));
    }

    public String toString() {
	return new String(buf, 0, len);
    }

    /**
     * Get a copy of the current byte buffer.
     */

    public byte[] getByteCopy() {
	byte v[] = new byte[len];
	System.arraycopy(buf, 0, v, 0, len);
	return v;
    }

    public final byte[] getBytes() {
	return buf;
    }

    public final int length() {
	return len;
    }

    public final void reset() {
	len = 0;
    }

    /**
     * Emit the content of this byte buffer to the given output stream.
     * @param out The output stream to emit the content to.
     * @exception IOException If sone IO error occurs during emitting.
     */

    public final void emit(OutputStream out) 
	 throws IOException
    {
	out.write(buf, 0, len);
    }

    HttpBuffer() {
	this.buf = new byte[INIT_SIZE];
    }


}





