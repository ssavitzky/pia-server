// EmptyEnumeration.java
// $Id: EmptyEnumeration.java,v 1.1 1999-03-12 19:50:12 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.util;

import java.util.*;

/**
 * An empty enumeration.
 */

public class EmptyEnumeration implements Enumeration {

    public final boolean hasMoreElements() {
	return false;
    }

    public final Object nextElement() {
	throw new NoSuchElementException("empty enumeration");
    }

}
