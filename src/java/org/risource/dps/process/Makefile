###### dps/handle/Makefile:	Active document tag handlers
# 	$Id: Makefile,v 1.7 2001-04-03 00:04:47 steve Exp $

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
 # This code was initially developed by Ricoh Innovations, Inc.  Portions
 # created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 


PACKAGE=org.risource.dps.process
TOPDIR=../../../..


FILES = \
	BasicProcessor.java \
	TopProcessor.java 

include $(TOPDIR)/makefiles/file.make


### Dependencies:

BasicProcessor.class: ../Processor.java ../Context.java \
	../util/ContextStack.java
TopProcessor.class: BasicProcessor.java ../TopContext.java \
	../Processor.java ../Context.java ../util/ContextStack.java

ActiveDoc.class: BasicProcessor.java  TopProcessor.java ../TopContext.java\
	../Processor.java ../Context.java ../util/ContextStack.java \
	../../pia/Agent.java ../../pia/Pia.java ../../pia/Transaction.java \
	../../pia/Resolver.java
