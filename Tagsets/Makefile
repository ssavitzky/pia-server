### Makefile for Tagsets
#	$Id: Makefile,v 1.2 2000-04-20 22:59:19 steve Exp $

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

PIADIR=..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=Tagsets
MYPATH=Tagsets

TSDOCDIR = $(PIADIR)/Doc/Tagsets/tsdoc

include $(MF_DIR)/file.make

### Tagsets

TAGSETS = \
	pia-xhtml.ts \
	pslides.ts \
	slides.ts

TAGSET_DOCS = $(TAGSETS:.ts=.html)
TAGSET_TSSS = $(TAGSETS:.ts=.tss)

### Targets

all:: 
	@echo "done"

doc:: $(TAGSET_DOCS)

ts-doc:: $(TAGSET_DOCS)
	cp $? $(TSDOCDIR)


clean::
	rm *.tss

all::	.cvsignore

.cvsignore:: Makefile
	for f in $(TAGSET_DOCS); do \
	    grep -s -q $$f .cvsignore /dev/null || echo $$f >> .cvsignore ; \
	done

