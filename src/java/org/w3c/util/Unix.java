// Unix.java
// $Id: Unix.java,v 1.1 1999-03-12 19:50:23 pgage Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html


package org.w3c.util;

/**
 * Native methods to do some UNIX specific system calls.
 * This class can be used on UNIX variants to access some specific system
 * calls.
 */

public class Unix {
    private static final String  NATIVE_LIBRARY = "Unix";
    /**
     * Are the calls we support really availables ?
     */
    private static boolean haslibrary = false;
    private static Unix    that       = null;

    private native int getUID0(String name);
    private native int getGID0(String name);
    private native boolean setUID0(int uid);
    private native boolean setGID0(int gid);
    private native boolean chroot0(String root);
    
    /**
     * Get the UNIX system call manger.
     * @return An instance of this class, suitable to call UNIX system
     * calls.
     */

    public static synchronized Unix getUnix() {
	if ( that == null ) {
	    // Load the library:
	    try {
		Runtime.getRuntime().loadLibrary(NATIVE_LIBRARY);
		haslibrary = true;
	    } catch (UnsatisfiedLinkError ex) {
		haslibrary = false;
	    } catch (Exception ex) {
		haslibrary = false;
	    }
	    // Create the only instance:
	    that = new Unix();
	}
	return that;
    }

    /**
     * Can I perform UNIX system calls through that instance ?
     * @return A boolean, <strong>true</strong> if these system calls are
     * allowed, <strong>false</strong> otherwise.
     */

    public boolean isResolved() {
	return haslibrary;
    }

    /**
     * Get the identifier for that user.
     * @return The user's identifier, or <strong>-1</strong> if user was not
     * found.
     */

    public int getUID(String uname) {
	// FIXME: Security check needed here
	if ( uname == null )
	    return -1;
	return getUID0(uname);
    }

    /**
     * Get the identifier for that group.
     * @return The group identifier, or <strong>-1</strong> if not found.
     */

    public int getGID(String gname) {
	// FIXME: Security check needed
	if ( gname == null )
	    return -1;
	return getGID0(gname);
    }

    /**
     * Set the user id for the running process.
     * @param uid The new user identifier for the process.
     * @exception UnixException If failed.
     */

    public void setUID(int uid)
	throws UnixException
    {
	// FIXME: Security check needed
	if ( ! setUID0(uid) )
	    throw new UnixException("setuid failed");
    }

    /**
     * Set the group id for the running process.
     * @param gid The new user identifier for the process.
     * @exception UnixException If failed.
     */

    public void setGID(int gid)
	throws UnixException
    {
	// FIXME: Security check needed
	if ( ! setGID0(gid) )
	    throw new UnixException("setgid failed");
    }
    
    /**
     * Change the process root, using <code>chroot</code> system call.
     * @param root The new root for the process.
     * @exception UnixException If failed.
     */

    public void chroot(String root)
	throws UnixException
    {
	if ( root == null )
	    throw new NullPointerException();
	if ( ! chroot0(root) )
	    throw new UnixException("chroot failed");
    }
    

}
