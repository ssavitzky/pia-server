// HttpChallenge.java
// $Id: HttpChallenge.java,v 1.1 1999-03-12 19:50:47 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.http;

import org.w3c.util.*;

public class HttpChallenge extends BasicValue {
    String          scheme = null;
    ArrayDictionary params = null;

    /**
     * parse.
     * @exception HttpParserException if parsing failed.
     */
    protected void parse()
	throws HttpParserException
    {
	ParseState ps = new ParseState(roff, rlen);
	// Get the auth scheme
	if ( HttpParser.nextItem(raw, ps) < 0 )
	    error("Invalid challenge: no scheme.");
	this.scheme = ps.toString(raw);
	// Get the list of params:
	ParseState it = new ParseState();
	it.separator  = (byte) '=';
	ps.separator  = (byte) ',';
	ps.prepare();
	while (HttpParser.nextItem(raw, ps) >= 0) {
	    it.prepare(ps);
	    // Get the param name
	    if ( HttpParser.nextItem(raw, it) < 0 )
		error("Invalid challenge: no param name.");
	    String key = it.toString(raw, true);
	    // Get the param value:
	    it.prepare();
	    if ( HttpParser.nextItem(raw, it) < 0 )
		error("Invalid challenge: no param value.");
	    it.ioff = it.start;
	    HttpParser.unquote(raw, it);
	    if ( params == null )
		params = new ArrayDictionary(5, 5);
	    params.put(key, it.toString(raw));
	    ps.prepare();
	}
    }

    protected void updateByteValue() {
	HttpBuffer buf = new HttpBuffer();
	buf.append(scheme);
	buf.append(' ');
	int len = params.size();
	for (int i = 0 ; len > 0 ; i++) {
	    String key = (String) params.keyAt(i);
	    if ( key == null )
		continue;
	    buf.appendQuoted(key, (byte) '=', (String) params.elementAt(i));
	    len--;
	}
	raw  = buf.getByteCopy();
	roff = 0;
	rlen = raw.length;
    }

    public Object getValue() {
	validate();
	return this;
    }

    /**
     * Get the challenge scheme.
     * @return A String encoding the challenge scheme identifier.
     */

    public String getScheme() {
	validate();
	return scheme;
    }

    /**
     * Get an authentication parameter.
     * @param name The name of the parameter.
     * @return A String encoded value for this parameter, or <strong>null
     * </strong>
     */

    public String getAuthParameter(String name) {
	validate();
	return (params == null) ? null : (String) params.get(name);
    }

    /**
     * Set an auth parameter value.
     * @param name The name of the parameter to set.
     * @param value The new value for this parameter.
     */

    public void setAuthParameter(String name, String value) {
	validate();
	if ( params == null )
	    params = new ArrayDictionary(4, 4);
	params.put(name.toLowerCase(), value);
    }

    HttpChallenge(boolean isValid) {
	this.isValid = isValid;
    }

    HttpChallenge(boolean isValid, String scheme) {
	this.isValid = isValid;
	this.scheme  = scheme;
    }

    public HttpChallenge() {
	super();
    }

}
