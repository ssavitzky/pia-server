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
<!-- Contributor(s): steve@rii.ricoh.com                                    -->
<!-- ---------------------------------------------------------------------- -->

<tagset name="pia-config" parent="xxml" include="pia-tags" recursive="yes">
<cvs-id>$Id: pia-config.ts,v 1.3 2001-01-11 23:37:01 steve Exp $</cvs-id>

<h1>PIA Configuration-file Tagset</h1>

<doc> This tagset contains the tags that are permitted inside PIA
	configuration files.
</doc>

<define element="AGENT" handler="org.risource.pia.agent.AgentBuilder">
  <doc> This element builds and installs an Agent object.
  </doc>
</define>

<define element="DOCUMENT" quoted="yes">
  <doc> This element builds a virtual document inside a Resource element.
  </doc>
</define>

</tagset>

