// HttpCredential.java
// $Id: HttpCredential.java,v 1.1 1999-03-12 19:50:52 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.http;

import org.w3c.util.*;

/**
 * This class has a hack to handle basic authentication.
 * Basic  authentication (amongst others) is broken in the HTTP spec, to handle
 * the APIs more nicely, Jigsaw fakes a <code>cookie</code> auth param
 * with the appropriate basic-credentials.
 */

public class HttpCredential extends BasicValue {
    ArrayDictionary params = null;
    String          scheme = null;

    /**
     * parse.
     * @exception HttpParserException if parsing failed.
     */
    protected void parse() 
	throws HttpParserException
    {
	ParseState ps = new ParseState(roff, rlen);
	// Get the scheme first
	if ( HttpParser.nextItem(raw, ps) < 0 )
	    error("Invalid credentials: no scheme.");
	this.scheme = ps.toString(raw);
	// Depending on the scheme...
	this.params = new ArrayDictionary(4, 4);
	ps.prepare();
	if ( scheme.equalsIgnoreCase("basic") ) {
	    // Basic Auth nasty hack
	    if ( HttpParser.nextItem(raw, ps) < 0 )
		error("Invalid basic auth credentials, no basic-cookie.");
	    params.put("cookie", ps.toString(raw));
	} else {
	    // Normal credentials parsing
	    ParseState it = new ParseState();
	    it.separator  = (byte) '=';
	    ps.separator = (byte) ',';
	    while (HttpParser.nextItem(raw, ps) >= 0 ) {
		// Get the param name:
		it.prepare(ps);
		if (HttpParser.nextItem(raw, it) < 0)
		    error("Invalid credentials: bad param name.");
		String key = it.toString(raw, true);
		// Get the param value:
		it.prepare();
		if ( HttpParser.nextItem(raw, it) < 0)
		    error("Invalid credentials: no param value.");
		it.ioff = it.start;
		HttpParser.unquote(raw, it);
		params.put(key, it.toString(raw));
		ps.prepare();
	    }
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
	    if ( key.equals("cookie") )
		buf.append((String) params.elementAt(i));
	    else
		buf.appendQuoted(key, (byte)'=', (String)params.elementAt(i));
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
     * Get the authentication scheme identifier.
     * @return A String giving the auth scheme identifier.
     */

    public String getScheme() {
	validate();
	return scheme;
    }

    /**
     * Set the authentication scheme.
     * @param scheme The auth scheme for these credentials.
     */

    public void setScheme(String scheme) {
	if ((this.scheme != null) && ! this.scheme.equalsIgnoreCase(scheme) )
	    invalidateByteValue();
	this.scheme = scheme;
    }

    /**
     * Get an authentication parameter.
     * @param name The name of the parameter to fetch.
     * @return The String value, or <strong>null</strong> if undefined.
     */

    public String getAuthParameter(String name) {
	validate();
	return (params == null) ? null : (String) params.get(name);
    }

    /**
     * Set an authentication parameter.
     * @param name The name of the authentication parameter.
     * @param value The value of the authentication parameter.
     */

    public void setAuthParameter(String name, String value) {
	validate();
	if ( params == null )
	    params = new ArrayDictionary(4, 4);
	params.put(name, value);
    }

    public HttpCredential(boolean isValid, String scheme) {
	this.isValid = isValid;
	this.scheme  = scheme;
    }

    public HttpCredential() {
	this.isValid = false;
    }
}

