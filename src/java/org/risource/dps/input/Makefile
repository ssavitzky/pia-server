###### dps/input/Makefile:	Implementations of the Input interface
# 	$Id: Makefile,v 1.6 1999-04-23 00:22:01 steve Exp $

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


PACKAGE=org.risource.dps.input
TOPDIR=../../../..

FILES = \
	ActiveInput.java \
	FromNodeList.java \
	FromEnumeration.java \
	FromNamespace.java \
	FromParseTree.java 


include $(TOPDIR)/makefiles/file.make

### Dependencies:

ActiveInput.class: ../Input.java ../util/CurrentActive.java
FromParseTree.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java
FromNodeList.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java\
	../active/ActiveNodeList.java
FromEnumerator.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java
FromNamespace.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java\
	../Namespace.java
