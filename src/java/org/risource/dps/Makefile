###### dps/Makefile:	Document Processing System package.
# 	$Id: Makefile,v 1.5 1999-04-14 18:59:03 steve Exp $

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


PACKAGE=org.risource.dps
TOPDIR=../../..

PACKAGES = \
	active \
	input \
	tree \
	output \
	util \
	handle \
	parse \
	process \
	tagset

FILES = \
	Action.java \
	Active.java \
	Context.java \
	Cursor.java \
	EntityTable.java \
	Filter.java \
	Handler.java \
	Input.java \
	Namespace.java \
	Output.java \
	Parser.java \
	ProcessException.java \
	Processor.java \
	ProcessorInput.java \
	Syntax.java \
	Tagset.java \
	TopContext.java

# === Potential renames:
#	ParseStack -> BasicContext
#	Filter -> Process

include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Handler.class: Syntax.java Action.java
