######  content/Makefile:	Content package.
# 	$Id: Makefile,v 1.4 2001-04-03 00:04:08 steve Exp $

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


PACKAGE=org.risource.content
TOPDIR=../../..

PACKAGES = \
	text

FILES = \
	GenericContent.java \
	StreamingContent.java \
	ByteStreamContent.java 

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies

Text.class: GenericContent.java

ByteStreamContent.class:  GenericContent.java

GenericContent.class: ../pia/Content.java



