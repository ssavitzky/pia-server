###### dps/namespace/Makefile:	DPS namespace classes
# 	$Id: Makefile,v 1.3 1999-10-04 17:35:05 steve Exp $

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


PACKAGE=org.risource.dps.namespace
TOPDIR=../../../..

CLASSES = \
	AbstractNamespace.java \
	BasicNamespace.java \
	BasicEntityTable.java \
	PropertyTable.java \
	AttrIndirect.java \
	EntityIndirect.java \
	EntityWrap.java \
	NamespaceWrap.java

FILES = $(CLASSES)

include $(TOPDIR)/makefiles/file.make

### Dependencies

AbstractNamespace.class: ../Namespace.java ../tree/TreeGeneric.java
BasicNamespace.class: AbstractNamespace.java ../Namespace.java 
BasicEntityTable.class: AbstractNamespace.java BasicNamespace.java \
	../Namespace.java ../EntityTable.java
PropertyTable.class: AbstractNamespace.java BasicNamespace.java \
	../Namespace.java ../PropertyMap.java
NamespaceWrap.class: AbstractNamespace.java ../Namespace.java \
	../../ds/Tabular.java
