<!doctype tagset system "tagset.dtd">
<!-- -------------------------------------------------------------------------- -->
<!-- The contents of this file are subject to the Ricoh Source Code Public      -->
<!-- License Version 1.0 (the "License"); you may not use this file except in   -->
<!-- compliance with the License.  You may obtain a copy of the License at      -->
<!-- http://www.risource.org/RPL                                                -->
<!--                                                                            -->
<!-- Software distributed under the License is distributed on an "AS IS" basis, -->
<!-- WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License  -->
<!-- for the specific language governing rights and limitations under the       -->
<!-- License.                                                                   -->
<!--                                                                            -->
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.  Portions  -->
<!-- created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All    -->
<!-- Rights Reserved.                                                           -->
<!--                                                                            -->
<!-- Contributor(s):                                                            -->
<!-- -------------------------------------------------------------------------- -->

<tagset name=Admin-xhtml parent=pia-xhtml recursive>

<h1>Admin-XHTML Tagset</h1>

<doc> This tagset is local to the Admin agent.  It is worth noting that the
      Admin agent also handles pages in the ``root'' directory, with no
      agent; these are kept in the <code>Admin/ROOT</code> directory,
</doc>

<h2>Legacy operations</h2>

<note author=steve> Note the use of <code>handler=crc.pia.agent.xxx</code> in
   the following definitions.  This puts agent-specific handlers where they
   belong, in the <code>crc.pia.agent</code> package.
</note>

<define element=agent-home empty handler=crc.pia.agent.agentHome>
   <doc> Determine the home directory of an agent.  Prefixes the agent's name
	 with its type, if necessary, to produce a complete path.
   </doc>
   <define attribute=agent optional>
      <doc> specifies the name of the agent being queried.  Defaults to the
	    name of the current agent.
      </doc>
   </define>
   <define attribute=link optional>
      <doc> If present, the result is a link to the agent's home. 
      </doc>
   </define>
</define>

<define element=agent-restore empty handler=crc.pia.agent.agentRestore>
   <define attribute=file required>
      <doc> specifies the name of the file to be restored from.
      </doc>
   </define>
</define>

<define element=agent-remove empty handler=crc.pia.agent.agentRemove>
   <define attribute=agent required>
      <doc> specifies the name of the agent to be removed
      </doc>
   </define>
</define>

<define element=agent-save empty handler=crc.pia.agent.agentSave>
   <define attribute=file required>
      <doc> specifies the name of the file to be saved into.
      </doc>
   </define>
   <define attribute=agent optional>
      <doc> specifies the name of the agent to be saved.  Defaults to the
	    name of the current agent.
      </doc>
   </define>
   <define attribute=list optional>
      <doc> specifies a list of  names of agents to be saved.
      </doc>
   </define>
   <define attribute=append optional>
      <doc> specifies that the agent is to be appended to an existing file.
      </doc>
   </define>
</define>

<define element=agent-install handler=crc.pia.agent.agentInstall>
</define>

<h2>Page Components</h2>

<h3>Graphics</h3>

<define entity=A100>
  <doc> Large pentagonal A, which serves as an identifying logo for the 
	Admin agent.
  </doc>
  <value><img src="Logo/A100.gif" height=100 width=111 
		alt="ADMIN"></value>
</define>
<!-- these two aren't being picked up from pia-xhtml; check namespace stuff
     in TopProcessor and BasicTagset. -->
<define entity=blank-170x1>
  <value><img src="/Icon/white170x1.gif" width=170 height=1
		alt=" "></value>
</define>
<define entity=blue-dot>
  <value><img src="/Icon/dot-blue.gif"
		height=20 width=20 alt="*"></value>
</define>


<h3>Miscellaneous tables and lists:</h3>

<define element=agent-index empty>
  <doc> Put out an index of agents. </doc>
  <action>
    <table cellspacing=0 cellpadding=0 border=0>
      <tr><th width=170 valign=top><i>Agent Index:</i>
	    </th>
	  <td valign=center>
	    <table cellspacing=0 cellpadding=0 align=center border=0>
	      <tr><th width=170 align=right><i>Index</i>&nbsp;
		  <th align=left><i>Home</i></tr>
	      <repeat list="&agentNames;" entity="li"><tr>
		  <th align=right><a href="/&li;/">&li;/</a>&nbsp;
		  <td><agent-home link agent="&li;"></tr>
	      </repeat>
	    </table>
    </table>
  </action>
</define>

<define element=agent-index-pre empty>
  <doc> Put out an index of agents using <tag>pre</tag>.  Might be useful
	with browsers that lack table support, if there are any left.
  </doc>
  <action>
    <table cellspacing=0 cellpadding=0 align=center border=0>
    <tr><th width=170 valign=center>&A100;</th>
    <td valign=center>
      <table cellspacing=0 cellpadding=0 align=center border=0>
      <tr> <!-- need an image to fix the size -->
    <pre><i><text pad align=right width=15>Index</text>	Home</i>
    <repeat list="&agentNames;" entity="li"
	   ><text pad align=right width=15><a href="/&li;/"><b>&li;/</b></a
	   ></text>	<agent-home link agent="&li;">
    </repeat></pre>
    </table>
    </table>
  </action>
</define>

<h3>Headers and Footers</h3>

<define element=sub-head>
  <doc> A secondary table located immediately under the header.
	Content should consist of additional table rows.
  </doc>
  <define attribute=page>
    <doc> the base name of the page, e.g. <code>index</code> or
	  <code>home</code>.
    </doc>
  </define>
  <action>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170>&A100;
    <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th align=left nowrap width=170>&blank-170x1;<td><br>
    <tr><th align=right><xopt page="&attributes:page;"
			      pages="home index help options">&blue-dot;</xopt>
	<td> <xa href="home" page="&attributes:page;">Home</xa>
    	     <xa href="index" page="&attributes:page;">Index</xa>
    	     <xa href="help" page="&attributes:page;">Help</xa>
	     <xa href="options" page="&attributes:page;">Options</xa>
    <tr><th align=right>
	     <xopt page="&attributes:page;"
		   pages="checkpoint restore shutdown">&blue-dot;</xopt>
	<td> <xa href="checkpoint" page="&attributes:page;">checkpoint</xa> /
	     <xa href="restore" page="&attributes:page;">restore</xa> state
	     <if><test exact match='pia'>&piaUSER;</test>
	         <then><a href="shutdown">shut down</a> appliance<br>
	     </if>
    <tr><th valign=top align=right>
	     <xopt page="&attributes:page;"
		   pages="agents installers remove-agent">&blue-dot;</xopt>
	     &nbsp;
	<td valign=top>
	    <xa href="list-agents" page="&attributes:page;">list</xa> / 
	    <xa href="installers" page="&attributes:page;">install</xa> / 
 	    <xa href="remove-agent" page="&attributes:page;">remove</xa>
	     agents<br>
    <tr><th valign=top align=right>Files: &nbsp;
	<td><a href="/PIA/Doc/"><b>Docs</b></a>
	    <a href="/~/">Data</a>
	    <a href="/PIA/src/">Sources</a>
	    <a href="/PIA/Agents/">Agents</a>
	    <a href="/~/Agents/">(Customized)</a>
    <tr><th valign=top align=right><b>Agents:</b> &nbsp;
        <td valign=top> <repeat list="&agentNames;" entity="foo">
            <a href="/&foo;">&foo;</a> <a href="/&foo;/"><b> / </b></a>
            </repeat><br>
   <get name=content>
  </table>
</table>
  </action>
</define>


<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="/">PIA</a> || <a href="/&AGENT:name;">&AGENT:name;</a>:
<a href="/&AGENT:name;/">/index/</a>
<a href="/&AGENT:name;/agents">agents</a>
  </action>
</define>

<define element=footer empty>
  <doc> This expands into a standard footer.  Go to some lengths to extract
	the year the file was modified from the cvs id. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<nav-bar/>
<hr>
<a href="/&AGENT:name;">&AGENT:name;</a> agent on
<if><test exact  match='pia'>&piaUSER;</test>
    <then> the </then>
    <else> &piaUSER;'s</else></if>
<if><test exact match='pia'>&piaUSER;</test>
    <then> information appliance </then>
    <else>Personal Information Agency</else></if><br>
<b>URL:</b> &lt;<a href="&url;">&url;</a>&gt;
<hr>
<set name=myear><subst match="/.* " result=", "><extract>
    &attributes;<name>cvsid<eval/><text split>&list;</text> 3
    </extract> </set>
<b>Copyright &copy; &myear; Ricoh Silicon Valley</b><br>
<em><extract>&attributes;<name>cvsid<eval/></extract></em>

  </action>
</define>

</tagset>

