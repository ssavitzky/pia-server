###### pia/agent/Makefile:	Agent handlers and related classes
#	$Id: Makefile,v 1.1 1999-09-09 21:54:44 steve Exp $

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


PACKAGE=org.risource.pia.site
TOPDIR=../../../..


OTHER = \
	SiteMachine.java \
	SiteDoc.java

HANDLERS = 

FILES= $(OTHER) $(HANDLERS)

include $(TOPDIR)/makefiles/file.make

### Dependencies:

DocMachine.java:  ../Machine.java ../Resolver.java \
	 ../Pia.java ../Content.java  ../../content/ByteStreamContent.java  \
	../Transaction.java ../HTTPResponse.java

