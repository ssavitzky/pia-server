<!-- This file is included in the root home page for personal PIA's -->
<h3>Welcome to the PIA</h3>


<p> This is the home page of your personal PIA.  It is running as a process
    under your own user name (if you're on Unix), and is accessible through
    the URL &lt;<a href="&PIA:url;">&PIA:url;/</a>&gt;.
    Of course, if you're looking at this page in a web browser you already
    know that.
    <if><get name="PIA:servlet"/>
        <then> The PIA is currently running as a servlet, which is ``mounted
	       on'' (accessible through) the URL path
	       ``<a href="&PIA:rootPath;">&PIA:rootPath;</a>''.  There may be
	       other web pages and applications on this server outside the PIA.
        </then>
    </if>
</p>

<p> You can find out more about this <em>particular</em> PIA server from the
    <a href="Agents/Admin">Admin</a> agent, which is used to perform server
    management functions.  You can find out about its implementation from the
    <a href="&PIA:rootPath;Doc/API/internals.html">API</a> (Application
    Programming Interface) Manual, and by directly studying the <a
    href="&PIA:rootPath;src/java/org/risource">source code</a>, all of which
    is online.
</p>

<p> You can find out more about the PIA framework and its role in information
    applications from the online documentation described in the
    <strong>Documentation Overview</strong> below, and from the PIA
    Developer's Website at <a
    href="http://RiSource.org/PIA"><code>RiSource.org/PIA</code></a>.
    You can obtain updates and additional open-source software from <a
    href="http://RiSource.org/"><code>RiSource.org</code></a>.  You
    can find a running PIA demonstration server at <a
    href="http://pia.rsv.ricoh.com"><code>pia.rsv.ricoh.com</code></a>.
</p>

<dl>
  <dt> Note: </dt>
  <dd> If you were using the PIA before
       <a href="&PIA:rootPath;Doc/Release/r2.1.html">Release 2.1</a>, it is
       <em>strongly</em> suggested that you look at the following documents:

       <a href="&PIA:rootPath;src/java/org/risource/site/about">About the Site
       Resource 
       Package</a>,
       <a href="&PIA:rootPath;src/java/org/risource/pia/about">About the PIA</a>,
       and <a href="&PIA:rootPath;Doc/Notes/steve/naming">Steve's notes</a>.
  </dd>
<if><test exact="yes" match="pia"><get name="ENV:user.name"/></test>
  <else>       
  <dt> <strong>Warning:</strong>
  </dt>
  <dd> If this server is accessible from the Internet, you should take steps
       to protect it.  Running a PIA under your own user ID represents a
       potential security problem.
  </dd>
  </else>       
</if>
</dl>
<inc-footer cvsid="$Id: welcome.inc,v 1.18 2000-06-22 07:02:09 steve Exp $" />
