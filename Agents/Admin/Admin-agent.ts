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
<!-- Contributor(s): steve@rsv.ricoh.com                                    -->
<!-- ---------------------------------------------------------------------- -->

<tagset name=Admin-agent parent=pia-xhtml recursive>
<cvs-id>$Id: Admin-agent.ts,v 1.2 1999-05-06 20:36:26 steve Exp $</cvs-id>

<h1>PIA AGENT Tagset</h1>

This tagset builds an Agent from an XML document.

<define element=AGENT handler=org.risource.pia.agent.AgentBuilder>
  <doc> This element builds and installs an Agent object.
  </doc>
</define>

</tagset>

