// ArrayEnumeration.java
// $Id: ArrayEnumeration.java,v 1.1 1999-03-12 19:50:10 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.w3c.util ;

import java.util.* ;

/** Iterates through array skipping nulls. */
public class ArrayEnumeration implements Enumeration {
    private int nelems ;
    private int elemCount ;
    private int arrayIdx ;
    private Object[] array ;

    public ArrayEnumeration(Object[] array) {
	this(array,array.length) ;
    }

    public ArrayEnumeration(Object[] array,int nelems) {
	arrayIdx = elemCount = 0 ;
	this.nelems = nelems ;
	this.array = array ;
    }

    public final boolean hasMoreElements() {
	return elemCount<nelems ;
    }

    public final Object nextElement() {
	while(array[arrayIdx]==null && arrayIdx<array.length)
	    arrayIdx++ ;

	if(arrayIdx>=array.length)
	    throw new NoSuchElementException() ;

	elemCount++; 
	return array[arrayIdx++] ;
    }
}
