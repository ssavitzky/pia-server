### file.make -- makefile template for ordinary files
#   $Id: file.make,v 1.4 1999-10-14 18:05:45 steve Exp $

############################################################################## 
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
 # Contributor(s): steve@rsv.ricoh.com
 #
############################################################################## 

########################################################################
#
# This makefile contains rules for ordinary files 
#
########################################################################

### Usage:
#	PIADIR=../../....
#	MF_DIR=$(PIADIR)/Config/makefiles
#	MYNAME=<name of this directory>
#	MYPATH=<path from PIADIR to this directory>
#	include $(MF_DIR)/file.make
#


### Commands:

PROCESS = $(PIADIR)/bin/process

### Targets:
#	all	build executables, etc.
#	doc	build documentation
#	clean	remove trash
#	setup	initialize Makefile and other essential files 

all::
	@echo "Building in" $(MYNAME)

doc::
	@echo "Documenting in" $(MYNAME)

clean::
	@echo "Cleaning in" $(MYNAME)
	rm -f *~ *.bak *.log *.o *.obj

setup:: HEADER.html
	@echo "Setup in" $(MYNAME)
	@echo "     You will probably need to edit the Makefile"

HEADER.html:
	echo "<h1>$(MYPATH)</h1>"					 > $@
	echo "<a href=\"../\">[..]</a>"					>> $@
	echo "<a href=\"#files\">[files]</a><p>"			>> $@
	echo "<!-- brief description goes here -->"			>> $@
	echo "<h2>Annotated Contents</h2>"				>> $@
	echo "<h3>Directories</h3>"					>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a>"					>> $@
	echo "  <dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<h3>Files</h3>"						>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a>"					>> $@
	echo "  <dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<hr>"							>> $@
	echo "<b>Copyright &copy; 1999 Ricoh Silicon Valley</b><br>"	>> $@
	echo "<b>$$"Id"$$</b>"						>> $@
	echo "<a name=\"files\"><hr></a>"				>> $@

### Rules:

.SUFFIXES: .ts .html .tso .tss

### Convert tagsets to ``stripped'' (.tss) files by removing documentation.
###
.ts.tss:
	$(PROCESS) -t tsstrip -q $< > $@.tmp
	rm -f $@; mv $@.tmp $@

.ts.html:
	$(PROCESS) -t tsdoc -q $<  > $@
