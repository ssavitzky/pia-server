###### dps/Makefile:	Document Processing System package.
# 	$Id: Makefile,v 1.16 2001-04-03 00:04:12 steve Exp $

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


PACKAGE=org.risource.dps
TOPDIR=../../..
REL_DIR = $(PIADIR)/Doc/Release

PACKAGES = \
	active \
	tree \
	namespace \
	input \
	output \
	util \
	handle \
	parse \
	process \
	tagset

FILES = \
	Action.java \
	Syntax.java \
	Active.java \
	Context.java \
	Cursor.java \
	EntityTable.java \
	Handler.java \
	Input.java \
	Namespace.java \
	Output.java \
	Parser.java \
	Processor.java \
	ProcessorInput.java \
	PropertyMap.java \
	Tagset.java \
	TopContext.java

# === Potential renames:
#	ParseStack -> BasicContext
#	Filter -> Process

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

# Make the application, Filter.class, last. 
#	This means that we can make the interfaces first. 
all:: Filter.class

# Documentation: copy to-do file to the release directory
doc:: $(REL_DIR)/to-do.dps.html

$(REL_DIR)/to-do.dps.html: to-do.html
	cp $? $@

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Handler.class: Syntax.java Action.java

