###### site/Makefile:	Site Structure package.
# 	$Id: Makefile,v 1.2 1999-08-20 00:03:26 steve Exp $

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


PACKAGE=org.risource.site
TOPDIR=../../..

PACKAGES = \
	util

FILES = \
	Resource.java \
	Document.java \
	Root.java \
	AbstractResource.java \
	FileResource.java \
	FileDocument.java \
	ConfiguredResource.java \
	LocalDocument.java \
	Subsite.java \
	Site.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies

AbstractResource.class: Resource.java
Document.class: Resource.java
Root.class: Resource.java
ConfiguredResource.class: AbstractResource.java Resource.java
LocalDocument.class: ConfiguredResource.class Document.class \
	AbstractResource.java Resource.java
Subsite.class: ConfiguredResource.java AbstractResource.java Resource.java
Site.class: Subsite.java Root.java \
	 ConfiguredResource.java AbstractResource.java Resource.java
FileResource.class: AbstractResource.java Resource.java
FileDocument.class: FileResource.class Document.class \
	AbstractResource.java Resource.java
