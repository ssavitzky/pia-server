// HttpParserException.java
// $Id: HttpParserException.java,v 1.1 1999-03-12 19:51:07 pgage Exp $$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html


package org.w3c.www.http;

import org.w3c.www.mime.*;

public class HttpParserException extends MimeParserException {
    
    public HttpParserException(String msg) {
	super(msg);
    }

}
