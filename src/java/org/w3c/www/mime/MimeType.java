// MimeType.java
// $Id: MimeType.java,v 1.1 1999-03-12 19:51:34 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.www.mime ;

import java.util.*;
import java.io.*;

/**
 * This class is used to represent parsed MIME types. 
 * It creates this representation from a string based representation of
 * the MIME type, as defined in the RFC 1345.
 */

public class MimeType implements Serializable {
    /** 
     * List of well known MIME types:
     */
    public static MimeType TEXT_HTML                         = null ;
    public static MimeType APPLICATION_POSTSCRIPT            = null ;
    public static MimeType TEXT_PLAIN                        = null ;
    public static MimeType APPLICATION_X_WWW_FORM_URLENCODED = null ;
    public static MimeType MULTIPART_FORM_DATA               = null ;
    public static MimeType APPLICATION_X_JAVA_AGENT          = null ;
    public static MimeType MESSAGE_HTTP                      = null;
    public static MimeType TEXT_CSS                          = null;

    static {
	try {
	    TEXT_HTML 
		= new MimeType("text/html");
	    APPLICATION_POSTSCRIPT 
		= new MimeType ("application/postscript") ;
	    TEXT_PLAIN             
		= new MimeType("text/plain") ;
	    APPLICATION_X_WWW_FORM_URLENCODED
		= new MimeType("application/x-www-form-urlencoded") ;
	    MULTIPART_FORM_DATA
		= new MimeType("multipart/form-data") ;
	    APPLICATION_X_JAVA_AGENT
		= new MimeType("application/x-java-agent") ;
	    MESSAGE_HTTP
		= new MimeType("message/http");
	    TEXT_CSS
		= new MimeType("text/css");
	} catch (MimeTypeFormatException e) {
	    System.out.println ("httpd.MimeType: invalid static init.") ;
	    System.exit (1) ;
	}
    }

    public final static int MATCH_TYPE             = 1 ;
    public final static int MATCH_SPECIFIC_TYPE    = 2 ;
    public final static int MATCH_SUBTYPE          = 3 ;
    public final static int MATCH_SPECIFIC_SUBTYPE = 4 ;

    protected String type      = null ;
    protected String subtype   = null ;
    protected String pnames[]  = null;
    protected String pvalues[] = null;
    protected String external  = null ;

    /**
     * How good the given MimeType matches the receiver of the method ?
     *  This method returns a matching level among:
     * <dl>
     * <dt>MATCH_TYPE<dd>Types match,</dd>
     * <dt>MATCH_SPECIFIC_TYPE<dd>Types match exactly,</dd>
     * <dt>MATCH_SUBTYPE<dd>Types match, subtypes matches too</dd>
     * <dt>MATCH_SPECIFIC_SUBTYPE<dd>Types match, subtypes matches exactly</dd>
     * </dl>
     * @param other The other MimeType to match against ourself.
     */

    public int match (MimeType other) {
	int match = -1;
	// match types:
	if ( type.equals("*") || other.type.equals("*") ) {
	    match = MATCH_TYPE;
	} else if ( ! type.equals (other.type) ) {
	    return -1 ;
	} else {
	    match = MATCH_SPECIFIC_TYPE;
	}
	// match subtypes:
	if ( subtype.equals("*") || other.subtype.equals("*") ) {
	    match = MATCH_SUBTYPE ;
	} else if ( ! subtype.equals (other.subtype) ) {
	    return -1;
	} else {
	    match = MATCH_SPECIFIC_SUBTYPE;
	}
	return match;
    }

    /**
     * A printable representation of this MimeType. 
     * The printed representation is guaranteed to be parseable by the
     * String constructor.
     */

    public String toString () {
	if ( external == null ) {
	    StringBuffer sb = new StringBuffer (type) ;
	    sb.append((char) '/') ;
	    sb.append (subtype) ;
	    if ( pnames != null ) {
		for (int i = 0 ; i < pnames.length; i++) {
		    sb.append(';');
		    sb.append(pnames[i]);
		    if ( pvalues[i] != null ) {
			sb.append('=');
			sb.append(pvalues[i]);
		    }
		}
	    }
	    external = sb.toString() ;
	}
	return external ;
    }


    /**
     * Does this MIME type has some value for the given parameter ?
     * @param name The parameter to check.
     * @return <strong>True</strong> if this parameter has a value, false
     *    otherwise.
     */

    public boolean hasParameter (String name) {
	if ( pnames != null ) {
	    for (int i = 0 ; i < pnames.length ; i++) {
		if ( pnames[i].equals(name) ) 
		    return true ;
	    }
	}
	return false ;
    }

    /**
     * Get the major type of this mime type.
     * @return The major type, encoded as a String.
     */

    public String getType() {
	return type;
    }

    /**
     * Get the minor type (subtype) of this mime type.
     * @return The minor or subtype encoded as a String.
     */

    public String getSubtype() {
	return subtype;
    }


    /**
     * Get a mime type parameter value.
     * @param name The parameter whose value is to be returned.
     * @return The parameter value, or <b>null</b> if not found.
     */
    
    public String getParameterValue (String name) {
	if ( pnames != null ) {
	    for (int i = 0 ; i < pnames.length ; i++) {
		if ( pnames[i].equals(name) ) 
		    return pvalues[i];
	    }
	}
	return null ;
    }


    /**
     * Construct  MimeType object for the given string.
     * The string should be the representation of the type. This methods
     * tries to be compliant with HTTP1.1, p 15, although it is not
     * (because of quoted-text not being accepted).
     * FIXME
     * @parameter spec A string representing a MimeType
     * @return A MimeType object
     * @exception MimeTypeFormatException if the string couldn't be parsed.
     */

    public MimeType (String spec)
	throws MimeTypeFormatException
    {
	int strl  = spec.length() ;
	int start = 0, look = -1 ;
	// skip leading/trailing blanks:
	while ((start < strl) && (spec.charAt (start)) <= ' ')
	    start++ ;
	while ((strl > start) && (spec.charAt (strl-1) <= ' '))
	    strl-- ;
	// get the type:
	StringBuffer sb = new StringBuffer () ;
	while ((start < strl) && ((look = spec.charAt(start)) != '/')) {
	    sb.append ((char) look) ;
	    start++ ;
	}
	if ( look != '/' ) 
	    throw new MimeTypeFormatException (spec) ;
	this.type = sb.toString() ;
	// get the subtype:
	start++ ;
	sb.setLength(0) ;
	while ((start < strl) 
	       && ((look = spec.charAt(start)) > ' ') && (look != ';')) {
	    sb.append ((char) look) ;
	    start++ ;
	}
	this.subtype = sb.toString() ;
	// get parameters, if any:
	while ((start < strl) && ((look = spec.charAt(start)) <= ' '))
	    start++ ;
	if ( start < strl ) {
	    if (spec.charAt(start) != ';') 
		throw new MimeTypeFormatException (spec) ;
	    start++ ;
	    Vector vp = new Vector(4) ;
	    Vector vv = new Vector(4) ;
	    while ( start < strl ) {
		while ((start < strl) && (spec.charAt(start) <= ' ')) start++ ;
		// get parameter name:
		sb.setLength (0) ;
		while ((start < strl) 
		       && ((look=spec.charAt(start)) > ' ') && (look != '=')) {
		    sb.append (Character.toLowerCase((char) look)) ;
		    start++ ;
		}
		String name = sb.toString() ;
		// get the value:
		while ((start < strl) && (spec.charAt(start) <= ' ')) start++ ;
		if (spec.charAt(start) != '=') 
		    throw new MimeTypeFormatException (spec) ;
		start++ ;
		while ((start < strl) && (spec.charAt(start) <= ' ')) start++ ;
		sb.setLength(0) ;
		while ((start < strl) 
		       && ((look=spec.charAt(start)) > ' ') && (look != ';')) {
		    sb.append ((char) look) ;
		    start++ ;
		}
		while ((start < strl) && (spec.charAt(start) != ';')) start++ ;
		start++ ; 
		String value = sb.toString() ;
		vp.addElement(name);
		vv.addElement(value);
	    }
	    this.pnames = new String[vp.size()];
	    vp.copyInto(pnames);
	    this.pvalues = new String[vv.size()];
	    vv.copyInto(pvalues);
	}
    }
    
    public MimeType (String type, String subtype
		     , String pnames[], String pvalues[]) {
	this.type    = type ;
	this.subtype = subtype ;
	this.pnames  = pnames;
	this.pvalues = pvalues;
    }

    public MimeType (String type, String subtype) {
	this.type    = type;
	this.subtype = subtype;
    }

    public static void main (String args[]) {
	if ( args.length == 1) {
	    MimeType type = null ;
	    try {
		type = new MimeType (args[0]) ;
	    } catch (MimeTypeFormatException e) {
	    }
	    if ( type != null )
		System.out.println (type) ;
	    else
		System.out.println ("Invalid mime type specification.") ;
	} else {
	    System.out.println ("Usage: java MimeType <type-to-parse>") ;
	}
    }

}
