###### dps/Makefile:	Document Processing System package.
# 	$Id: Makefile,v 1.9 1999-05-21 00:58:14 steve Exp $

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
	Syntax.java \
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

# Make the ``stripped'' version of the basic tagset, for a 2x speedup.
#	It's probably not worthwhile to strip any of the others;
#	even stripping pia-xhtml saves only about .5 second.
all::	tagset/basic.tss 

# Note that $(MAKE) works wrong in these rules when working recursively,
#	because the recursion defines TOPDIR on the command line. 
tagset/basic.tss: tagset/basic.ts tagset/tsstrip.ts
	cd tagset; make basic.tss

tagset/pia-xhtml.tss: tagset/pia-xhtml.ts tagset/basic.ts tagset/tsstrip.ts
	cd tagset; make pia-xhtml.tss

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Handler.class: Syntax.java Action.java

