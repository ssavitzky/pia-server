### file.make -- makefile template for ordinary files
#   $Id: file.make,v 1.9 2001-04-03 00:03:54 steve Exp $

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
 # This code was initially developed by Ricoh Innovations, Inc.  Portions
 # created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s): steve@rii.ricoh.com
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

ifndef TAGSET
  TAGSET=xhtml
endif

### Files:
###	These macros can be used in conjunction with the .xh.html rule
###	to generate HTML files offline.

XH_FILES= $(wildcard *.xh)
XH_HTML= $(XH_FILES:.xh=.html)

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
	echo "<h1><a href=\"$(PIADIR)\">PIA</a> / $(MYPATH)</h1>"	 > $@
	echo "<p>"							>> $@
	echo "    <a href=\"../\">[..]</a>"				>> $@
	echo "    <a href=\"#files\">[files]</a>"			>> $@
	echo "</p>"							>> $@
	echo "<!-- brief description goes here -->"			>> $@
	echo "<h2>Annotated Contents</h2>"				>> $@
	echo "<h3>Directories</h3>"					>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a> </dt>"				>> $@
	echo "  <dd> "							>> $@
	echo "  </dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<h3>Files</h3>"						>> $@
	echo "<dl>"							>> $@
	echo "  <dt> <a href=\"\"></a> </dt>"				>> $@
	echo "  <dd> "							>> $@
	echo "  </dd> "							>> $@
	echo "</dl>"							>> $@
	echo "<hr>"							>> $@
	echo "<b>Copyright &copy; 2001 Ricoh Innovations, Inc.</b><br />" >> $@
	echo "<b>$$"Id"$$</b>"						>> $@
	echo "<a name=\"files\"><hr></a>"				>> $@

### Rules:

.SUFFIXES: .ts .html .tso .tss .xh

### Convert tagsets to ``stripped'' (.tss) files by removing documentation.
#
.ts.tss:
	$(PROCESS) -t tsstrip -q $< > $@.tmp
	rm -f $@; mv $@.tmp $@

### Convert tagsets to documentation
#
.ts.html:
	$(PROCESS) -t tsdoc -q $<  > $@

### Convert .xh files to HTML:
#
.xh.html:
	$(PROCESS) -t $(TAGSET) $< > $@
	{ grep -s $@ .cvsignore } || echo $@ >> .cvsignore

ts-clean::
	rm -f *.tss *.tso *.obj
