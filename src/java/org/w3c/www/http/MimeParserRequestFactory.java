// MimeParserRequestFactory.java
// $Id: MimeParserRequestFactory.java,v 1.1 1999-03-12 19:51:26 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.http;

import org.w3c.www.mime.*;

/**
 * The MIME parser factory for HTTP requests.
 */

public class MimeParserRequestFactory implements MimeParserFactory {
    
    /**
     * Create a new HTTP request to hold the parser's result.
     * @param parser The parser that has something to parse.
     * @return A MimeParserHandler compliant object.
     */

    public MimeHeaderHolder createHeaderHolder(MimeParser parser) {
	return new HttpRequestMessage(parser) ;
    }
}
