// IO.java
// $Id: IO.java,v 1.1 1999-03-12 19:50:13 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1998.
// Please first read the full copyright statement in file COPYRIGHT.html
 
package org.w3c.util;

import java.io.*;

/**
 * @version $Revision: 1.1 $
 * @author  Benoît Mahé (bmahe@w3.org)
 */
public class IO {

    /**
     * Copy source into dest.
     */
    public static void copy(File source, File dest) 
	throws IOException
    {
	BufferedInputStream in = 
	    new BufferedInputStream( new FileInputStream(source) );
	BufferedOutputStream out =
	    new BufferedOutputStream( new FileOutputStream(dest) );
	byte buffer[] = new byte[1024];
	int read = -1;
	while ((read = in.read(buffer, 0, 1024)) != -1)
	    out.write(buffer, 0, read);
	out.flush();
	out.close();
	in.close();
    }
    
}
