<!DOCTYPE tagset SYSTEM "tagset.dtd">
<!-- ---------------------------------------------------------------------- -->
<!-- The contents of this file are subject to the Ricoh Source Code Public  -->
<!-- License Version 1.0 (the "License"); you may not use this file except  -->
<!-- in compliance with the License.  You may obtain a copy of the License  -->
<!-- at http://www.risource.org/RPL                                         -->
<!--                                                                        -->
<!-- Software distributed under the License is distributed on an "AS IS"    -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See   -->
<!-- the License for the specific language governing rights and limitations -->
<!-- under the License.                                                     -->
<!--                                                                        -->
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.        -->
<!-- Portions created by Ricoh Silicon Valley, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s):  steve@rsv.ricoh.com                                   -->
<!-- ---------------------------------------------------------------------- -->

<tagset name=Admin-xhtml parent=pia-xhtml recursive>
<cvs-id>$Id: Admin-xhtml.ts,v 1.14 1999-07-09 20:54:35 steve Exp $</cvs-id>

<h1>Admin-XHTML Tagset</h1>

<doc> This tagset is local to the Admin agent.  In addition to defining local
      headers and logos, it defines a number of agent-specific tags with
      handlers that require the presence of the Admin agent class.
</doc>

<h2>Agent-specific tags</h2>

<note author=steve> Note the use of
   <code>handler=org.risource.pia.agent.xxx</code> in 
   the following definitions.  This puts agent-specific handlers where they
   belong, in the <code>org.risource.pia.agent</code> package.
</note>


<define element=AGENT handler='org.risource.pia.agent.AgentBuilder'
        quoted='quoted'>
  <doc> This element builds and installs an Agent object.
  </doc>
</define>

<define element=agent-install handler=org.risource.pia.agent.agentInstall>
</define>

<define element=agent-remove empty handler=org.risource.pia.agent.agentRemove>
   <define attribute=agent required>
      <doc> specifies the name of the agent to be removed
      </doc>
   </define>
</define>


<define element='register-agent'>
  <doc> Register an agent's pathName and load file (content). 
  </doc>
  <define attribute="id" required>
    <doc> The agent's <code>pathName</code> </doc>
  </define>
  <define attribute="on-start" optional>
    <doc> If set, load the agent automatically on startup.  </doc>
  </define>
  <action>
    <set name="new"><do name="agent-file">
			<set name="id"><get name="attributes:id"/></set>
			<set name="on-start"><get name="attributes:on-start"/></set>
		       <get name="content"/></do></set>
    <hide>
      <if><extract><from><get name="AGENT:agent-files"/></from>
		   <id recursive><get name="attributes:id"/></id></extract>
	  <then>
	    <extract><from><get name="AGENT:agent-files"/></from>
		     <id recursive><get name="attributes:id"/></id>
		     <replace node><get name="new"/></replace>
	    </extract>
	  </then>
	  <else>
	    <extract><from><get name="AGENT:agent-files"/></from>
		     agent-files
		     <insert>  <get name="new"/>
</insert>
	    </extract>
	  </else>
      </if>
    </hide>
  </action>
</define>

<define element="agent-load">
  <doc> Load agents from the file specified in the content, using the
	Admin-agent tagset.  Return the pathName of each agent loaded.
  </doc>
   <define attribute="register" optional>
      <doc> If present, register the agent.
      </doc>
   </define>
  <action><text trim>
    <set name='loaded'><do name="include">
			   <set name="src"><get name="content"/></set>
			   <bind name="tagset">Admin-agent</bind></do></set>
    <if><get name="attributes:register"/>
	<then><hide>
	   <set name="agents"><extract><from><get name="loaded"/></from>
					<name recursive>AGENT</name> #element
			      </extract></set>
	   <repeat><foreach><get name="agents"/></foreach>
		   <set name="id"><extract><from><get name="li"/></from>
					   <name>pathName</name>
					   <eval/></extract></set>
		   <set name="st"><extract><from><get name="li"/></from>
					   <name>onStart</name>
					   <eval/></extract></set>
		   <if><get name="id"/>
		       <then><do name="register-agent">
				<set name="id"><get name="id"/></set>
				<set name="on-start"><get name="st"/></set>
				<get name="content"/></do>
                       </then></if>
	  </repeat>
	</hide></then>
    </if>
    <extract sep=' '>
	<from><get name="loaded"/></from>
	<name recursive>AGENT</name>
	<attr>pathName</attr>
	<eval />
    </extract>
  </text></action>
</define>

<define element='load-agent-files'>
  <doc> Load agents from the list of files in the content, which must consist
	of (expand to) an <tag>agent-files</tag> element that in turn contains
	<tag>agent-file</tag> elements.  Reports progress using
	&lt;user-message&gt;.  Only elements with the <code>onStart</code>
	attribute are loaded.  The content is saved in
	<code>&amp;AGENT:agent-files;</code> in order to track new or updated
	agents. 
  </doc>
  <action>
    <set name="AGENT:agent-files"><extract><from><get name="content"/></from>
			      		   agent-files
				  </extract></set>
    <set name="list"><extract><from><get name="AGENT:agent-files"/></from>
			      <name recursive>agent-file</name>
			      <has-attr>on-start</has-attr>
			      <content />
		     </extract></set>

    <repeat><foreach><get name="list"/></foreach>
	    <user-message>  loaded <hide>
		    </hide><agent-load><get name="li"/></agent-load><hide>
		    </hide> from <status src='&li;' item='path'/></user-message>
    </repeat>
    <user-message>Loading complete.</user-message>
  </action>
</define>

<define element='agent-file'>
  <doc> Specifies an agent to load.  The content is the pathname of the file
	to load from.  This is <em>not</em> an active element; this makes it
	easier to construct file lists.
  </doc>
  <define attribute="id" required>
    <doc> The agent's <code>pathName</code> </doc>
  </define>
  <define attribute="on-start" optional>
    <doc> If set, load the agent automatically on startup. </doc>
  </define>
  <define attribute="src" optional>
    <doc> The <em>original</em> path to the agent. </doc>
  </define>
</define>

<h3>Legacy operations</h3>


<define element="agent-home" empty handler="org.risource.pia.agent.agentHome">
   <doc> Determine the home directory of an agent.  Prefixes the agent's name
	 with its type, if necessary, to produce a complete path.
   </doc>
   <define attribute="agent" optional>
      <doc> specifies the name of the agent being queried.  Defaults to the
	    name of the current agent.
      </doc>
   </define>
   <define attribute="link" optional>
      <doc> If present, the result is a link to the agent's home. 
      </doc>
   </define>
</define>


<define element="pia-exit" handler="org.risource.pia.agent.piaExit">
   <doc> Exit the PIA. 
   </doc>
   <define attribute="status" optional>
      <doc> Specifies the ``status'' returned to the OS when the program
	    exits. 
      </doc>
   </define>
</define>

<define element="agent-restore" empty
	handler="org.risource.pia.agent.agentRestore">
   <define attribute=file required>
      <doc> specifies the name of the file to be restored from.
      </doc>
   </define>
</define>

<define element="agent-save" empty handler="org.risource.pia.agent.agentSave">
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

<h2>Page Components</h2>

<h3>Graphics</h3>

<define entity=A100>
  <doc> Large logo.  
  </doc>
  <value><img src="/Icon/logo100.gif" height=100 width=111 
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
    <table border=1>
      <tr><th align=left> /path/name  </th>
	  <th> home  </th>
	  <th> index </th>
	  <th> data  </th>
	  <th> type  </th>
      </tr>
      <repeat>
        <foreach entity="li"><get name="agentNames"/></foreach>
        <tr>
	  <set name=pname><agent-home agent="&li;"/></set>
	  <set name=type><agent-home type agent="&li;"/></set>
	  <th align=left><a href="&pname;"><get name="pname"/></a> </th>
	  <td> <a href="&pname;~"><em>path</em>~</a> </td>
	  <td> <a href="&pname;~/"><em>path</em>~/</a> </td>
	  <td>
		<if><status item=exists src="&pname;~/~/" />
		    <then><a href="&pname;~/~/"><em>path</em>~/~/</a></then>
		    <else>&nbsp;</else>
		</if></td>
	  <td>
		<if><test exact match="/"><get name="type"/></test>
		    <then>&nbsp;</then>
		    <else><a href="&type;"><get name="type"/></a></else>
		</if></td>
	</tr>
      </repeat>
    </table>
  </action>
</define>

<define element=agent-index-pre empty>
  <doc> Put out an index of agents using <tag>pre</tag>.  Might be useful
	with browsers that lack table support, if there are any left.
  </doc>
  <action>
    <table cellspacing=0 cellpadding=0 align=center border=0>
    <tr><th width=170 valign=center><get name="A100"/></th>
      <td valign=center>
	<table cellspacing=0 cellpadding=0 align=center border=0>
	<tr> <!-- need an image to fix the size -->
      <pre><i><text pad align=right width=15>Index</text>	Home</i>
      <repeat list="&agentNames;" entity="li"
	     ><text pad align=right width=15><a href="/&li;/"><b>&li;/</b></a
	     ></text>	<agent-home link agent="&li;"/>
      </repeat></pre></tr>
    </table></td></tr>
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
  <action><set name="page"><get name="attributes:page"/></set>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170><get name="A100"/>
    <td>
    <table cellspacing=0 cellpadding=0 border=0>
    <tr><th align=left nowrap width=170><get name="blank-170x1"/></th>
	<td></td></tr>
    <tr><th align=right>
	     <xopt pages="home index help options"><get name="blue-dot"/></xopt>
	</th>
	<td> <xa href="home">Home</xa>
    	     <xa href="index">Index</xa>
    	     <xa href="help">Help</xa>
	     <xa href="options">Options</xa>
	</td></tr>
    <tr><th align=right>
	     <xopt pages="config control"><get name="blue-dot"/></xopt> </th>
	<td> <xa href="config">Configure</xa> /
	     <xa href="control">Control</xa> PIA
	</td></tr>
    <tr><th valign=top align=right>
	     <xopt page="&attributes:page;"
		   pages="agents installers load-agent remove-agent"
		  ><get name="blue-dot"/></xopt>
	     &nbsp; </th>
	<td valign=top>
	    <xa href="list-agents">list</xa> / 
	    <xa href="installers">install</xa> / 
	    <xa href="load-agent">load</xa> / 
 	    <xa href="remove-agent">remove</xa>
	    agents
	</td></tr>
    <tr><th valign=top align=right>Files: &nbsp; </th>
	<td><a href="/Doc/"><b>Docs</b></a>
	    <a href="/~/">Data</a>
	    <a href="/PIA/src/">Sources</a>
	    <a href="/PIA/Agents/">Agents</a>
	    <a href="/~/Agents/">(Customized)</a>
	</td></tr>
    <tr><th valign=top align=right><b>Agents:</b> &nbsp;
        <td valign=top>
            <repeat><foreach entity="foo"><get name="agentNames"/></foreach>
              <do name="a"><set name="href">/<get name="foo"/></set>
		  <get name="foo"/></do>
	      <do name="a"><set name="href">/<get name="foo"/>~/</set>
		  <b> / </b></do>
            </repeat><br>
	</td></tr>
   <get name=content />
  </table></td></tr>
</table>
  </action>
</define>


<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
	<a href="/">PIA</a> ||
          <make name="a">
	    <set name="href"><get name="AGENT:pathName"/></set>
	    <get name="AGENT:name"/></make>:
          <xlink text="home"><get name="AGENT:pathName"/>~</xlink>
          <xlink text="/index/"><get name="AGENT:pathName"/>~/</xlink>
	  <xlink text="agents"><get name="AGENT:pathName"/>/list-agents</xlink>
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
<hr/>
<nav-bar/>
<hr/>
<a href="/&AGENT:name;"><get name="AGENT:name"/></a> agent on
<if><test exact  match='pia'><get name="piaUSER"/></test>
    <then> the </then>
    <else> <get name="piaUSER"/>'s</else></if>
<if><test exact match='pia'><get name="piaUSER"/></test>
    <then> information appliance </then>
    <else>Personal Information Agency</else></if><br>
<b>URL:</b> &lt;<xlink>&url;</xlink>&gt;
<hr>
<set name=myear><subst match="/.* " result=", "><extract>
    &attributes;<name>cvsid<eval/><text split>&list;</text> 3
    </extract> </subst></set>
<b>Copyright &copy; <get name="myear"/> Ricoh Silicon Valley</b>.
   Open Source at &lt;<b><get name="RiSource.org"/>/<get name="RiSource.org.pia"/></b>&gt;.<br>
<em><extract><get name="attributes"/><name>cvsid<eval/></extract></em>

  </action>
</define>

<hr />
<b>Copyright &copy; 1995-1999 Ricoh Silicon Valley</b><br />
<b>$Id: Admin-xhtml.ts,v 1.14 1999-07-09 20:54:35 steve Exp $</b><br />
</tagset>

