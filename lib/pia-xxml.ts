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

<tagset name="pia-xxml" parent="xxml" recursive="yes">
<cvs-id>$Id: pia-xxml.ts,v 1.1 1999-12-14 18:28:33 steve Exp $</cvs-id>

<h1>PIA XXML Tagset</h1>


This the version of the XXML tagset used by default in the PIA.


<h2>Legacy Tags</h2>

<define element=agent-list handler=org.risource.pia.handle.agentList empty>
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

<define element=agent-running empty handler=org.risource.pia.handle.agentRunning>
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

<define element=user-message handler=org.risource.pia.handle.userMessage>
  <doc> Output a message to the user.
  </doc>
</define>

<define element=trans-control handler=org.risource.pia.handle.transControl>
  <doc> Make content into a transaction control.
  </doc>
</define>

</tagset>

