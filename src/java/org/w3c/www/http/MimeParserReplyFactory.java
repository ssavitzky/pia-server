// MimeParserReplyFactory.java
// $Id: MimeParserReplyFactory.java,v 1.1 1999-03-12 19:51:24 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.http;

import org.w3c.www.mime.*;

/**
 * The MIME parse factory for HTTP replies.
 */

public class MimeParserReplyFactory implements MimeParserFactory {

    /**
     * Create a new HTTP reply to hold the parser's result.
     * @param parser The MimeParser that wants to parse some message.
     * @return A MimeParserHandler compliant object.
     */

    public MimeHeaderHolder createHeaderHolder(MimeParser parser) {
	return new HttpReplyMessage(parser) ;
    }

}
