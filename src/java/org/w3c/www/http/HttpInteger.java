// HttpInteger.java
// $Id: HttpInteger.java,v 1.1 1999-03-12 19:51:00 pgage Exp $$
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.http;

public class HttpInteger extends BasicValue {
    Integer value = null;

    protected void parse() {
	ParseState ps = new ParseState();
	ps.ioff   = 0;
	ps.bufend = raw.length;
	value = new Integer(HttpParser.parseInt(raw, ps));
    }

    protected void updateByteValue() {
	HttpBuffer buf = new HttpBuffer();
	buf.appendInt(value.intValue());
	raw  = buf.getByteCopy();
	roff = 0;
	rlen = raw.length;
    }

    public Object getValue() {
	validate();
	return value;
    }

    public void setValue(int ival) {
	if ( value.intValue() == ival )
	    return;
	invalidateByteValue();
	value   = new Integer(ival) ;
	isValid = true ;
    }

    public void setValue(Integer ival) {
	if ( ival.intValue() == value.intValue() )
	    return;
	invalidateByteValue();
	value   = ival ;
	isValid = true ;
    }

    HttpInteger(boolean isValid, int ival) {
	this.isValid = isValid;
	this.value   = new Integer(ival) ;
    }

    public HttpInteger() {
	this.isValid = false;
    }
}
