<h2>About ``&AGENT:name;''</h2> <!-- -*-html-helper-*- -->
<!-- Copyright 1997 Ricoh Innovations, Inc. -->

<p> The ``&AGENT:name;'' agent ``busts'' (blocks) requests that match one of a
    set of patterns, by redirecting them to a transparent 1x1 image stored
    locally on the PIA.  It is particularly useful for removing advertising
    banners.
</p>

<h3>Patterns:</h3>

<if><get name="FORM:pattern"/>
    <then> <set name="AGENT:pattern">
              <subst match="\r" result=""><subst match="[ \n\r]+"
              result="|"><text trim><get name="FORM:pattern"/></text></subst></subst>
	   </set>
	   <output dst="pattern.txt"><text trim><get name="FORM:pattern"/></text>
	   </output>
    </then>
</if>

<p>
<table width="100%">
<tr><td>
	<form action="home.xh" method="POST">
	  <textarea rows=10 cols=35 name=pattern><subst match="\|" result="\n"
	   >&AGENT:pattern;</subst></textarea><br>
	  <input type=submit value="Change Pattern" />
	</form>
    </td>
    <td valign="top">
	<dl>
	  <dt> <b>Note:</b> </dt>
	  <dd> Whitespace in the specified pattern list is mapped onto
	       ``<code>|</code>'' characters, which separate alternatives in a
	       regular expression.
	  </dd>
	 <if><get name="AGENT:enabled"/> <else>
	  <dt> <b>Note:</b> </dt>
	  <dd> The &AGENT:name; agent is currently <b>disabled!</b>
	       <form action='disable'>
	         <input type='hidden' name='enable' value='true' />
	         <input type='submit' value='click here to enable' />
	       </form>
	  </dd>
	 </else></if>
	</dl>
    </td>
</tr>	
</table>
</p>


<p>Actual pattern (for debugging):</p>
<font size=small>
<pre>  <get name="AGENT:pattern"/></pre>
</font>

<inc-footer cvsid="$Id: about.inc,v 1.2 2001-01-11 23:36:31 steve Exp $">
