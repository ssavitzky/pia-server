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
<!-- Contributor(s): steve@rsv.ricoh.com pgage@rsv.ricoh.com                -->
<!-- ---------------------------------------------------------------------- -->

<tagset name="pia-tags" parent="xxml" recursive="yes">
<cvs-id>$Id: pia-tags.ts,v 1.1 1999-12-16 21:12:57 steve Exp $</cvs-id>

<h1>PIA Tags</h1>


<doc> This tagset contains the tags unique to the PIA server environment that
      are unlikely to be customized by an application designer.  Tags that are
      meant to be customized, such as headers and footers, are in
      <code>Tagsets/pia-xhtml.ts</code>.
</doc>


<h2>Legacy Tags</h2>

<define element="agent-list" handler="org.risource.pia.handle.agentList"
        empty="yes">
  <doc> List agents, possibly those with a given type.
  </doc>
   <define attribute=type optional>
      <doc> specifies the type of the agents to be listed.
      </doc>
   </define>
   <define attribute=subs boolean optional>
      <doc> If present, specifies that only sub-agents of the given type will
	    be listed. 
      </doc>
   </define>
</define>

<define element="agent-running" handler="org.risource.pia.handle.agentRunning"
	empty="yes" >
   <doc> Determine whether a given agent is currently running (installed in
	 the PIA).
   </doc>
   <define attribute=name required>
      <doc> specifies the name of the agent being queried.
      </doc>
      <note author=steve> It was once thought that this should be renamed
	``agent'', but <em>nothing</em> else uses that, so it would be a
	mistake. 
      </note>
   </define>
</define>

<define element="user-message" handler="org.risource.pia.handle.userMessage">
  <doc> Output a message to the user.
  </doc>
</define>

<define element="trans-control" handler="org.risource.pia.handle.transControl">
  <doc> Make content into a transaction control.
  </doc>
</define>

<define element="read-vars" empty="yes">
  <doc> Read the applications's variables from a file.
  </doc>
  <define attribute="src">
    <doc> Specifies the file from which the variables are to be read.
    </doc>
  </define>
  <action>
    <user-message>this is a test</user-message>
    <include tagset="pia-xxml" src="&attributes:src;"> </include>
  </action>
</define>

<define element="write-vars">
  <doc> Write the applications's variables into a file.  The content of the
	tag is written ahead of the variables; this can be used for
	documentation, for example.
  </doc>
  <define attribute="dst">
    <doc> Specifies the file into which the variable bindings are to be
	  written. 
    </doc>
  </define>
  <define attribute="list">
    <doc> Specifies the list of variables to be written.
    </doc>
  </define>
  <action>
    <output dst="&attributes:dst;"><get name="content"/>
	<repeat list="&attributes:list;"><make name="set">
	    <let name="name">&li;</let>
	    <let name="..."><make name="protect"><get name="&li;"/></make></let>
	  </make>
	</repeat>
	<make type="comment"> Written &date; &time; </make>
    </output>
  </action>
</define>

</tagset>

