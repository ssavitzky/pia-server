### Makefile for lib
#	$Id: Makefile,v 1.4 1999-12-14 18:26:57 steve Exp $

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
MYNAME=lib
MYPATH=lib

include $(MF_DIR)/file.make

### Directories:
TSDOCDIR = $(PIADIR)/Doc/Tagsets/tsdoc

### Main tagsets: the ones people will actually use.
MAIN_TAGSETS = \
	basic.ts \
	xxml.ts \
	xhtml.ts

### Auxiliary tagsets: used internally..
AUX_TAGSETS = \
	tagset.ts \
	tsdoc.ts \
	tsstrip.ts

TAGSETS = $(MAIN_TAGSETS) $(AUX_TAGSETS) $(SPECIAL_TAGSETS)


TAGSET_DOCS = $(TAGSETS:.ts=.html)
TAGSET_TSSS = $(TAGSETS:.ts=.tss)

all:: 	basic.tss tsstrip.tss tagset.tss tsdoc.tss
	@echo "done"

clean::
	rm *.tss

doc::	ts-doc

ts-doc:: $(TAGSET_DOCS)
	cp $? $(TSDOCDIR)

all::	.cvsignore

.cvsignore:: Makefile
	for f in $(TAGSET_DOCS); do \
	    grep -s -q $$f .cvsignore /dev/null || echo $$f >> .cvsignore ; \
	done

### Tagset dependencies:

basic.tss:  tsstrip.ts basic.ts tagset.ts
tagset.tss:  tsstrip.ts basic.ts tagset.ts
tsstrip.tss:  tsstrip.ts basic.ts tagset.ts
tsdoc.tss:  tsstrip.ts basic.ts tagset.ts

