###### pia/agent/Makefile:	Agent handlers and related classes
#	$Id: Makefile,v 1.8 1999-12-14 18:37:46 steve Exp $

############################################################################### 
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 


PACKAGE=org.risource.pia.agent
TOPDIR=../../../..


OTHER = \
	AgentMachine.java \
	AgentInstallException.java

BOGUS = \
	agentRestore.java \
	agentSave.java \
	ToAgent.java

AGENTS = \
	Generic.java \
	Admin.java 

HANDLERS = \
	AgentBuilder.java \
	agentHome.java \
	agentInstall.java \
	agentRemove.java \
	piaExit.java

FILES= $(OTHER) $(AGENTS) $(HANDLERS)

include $(TOPDIR)/makefiles/file.make

### Dependencies:

AgentBuilder.class: Generic.java ../Agent.java  \
	../../dps/tree/TreeElement.java ../../dps/active/ActiveNode.java

Generic.class: ../Agent.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java \
	../../dps/tree/TreeElement.java ../../dps/active/ActiveNode.java

Agency.class: ../Agent.java Generic.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

Admin.class: ../Agent.java Generic.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

Root.class: ../Agent.java Generic.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPRequest.java

AgentMachine.java:  ../Agent.java Generic.java ../Resolver.java \
	 ../Pia.java ../Content.java  ../../content/ByteStreamContent.java  \
	../Transaction.java ../Machine.java ../HTTPResponse.java

Dofs.java: ../Agent.java Generic.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java 

Logo.java: ../Agent.java Generic.java ../Resolver.java ../Pia.java \
	../Transaction.java ../Machine.java ../HTTPResponse.java
