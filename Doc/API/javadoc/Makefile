### Makefile for Doc/API/javadoc
#	$Id: Makefile,v 1.1 2000-02-25 23:32:33 steve Exp $	

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
 # Contributor(s):
 #
############################################################################## 

PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=javadoc
MYPATH=Doc/API/javadoc
include $(MF_DIR)/file.make
#include $(MF_DIR)/graphics.make
#include $(MF_DIR)/text.make

### Cleanup: remove all HTML files.  If any are NOT automatically generated
###	by Javadoc, this will cause havoc.  Beware!
clean:: 
	@echo removing generated HTML files.
	rm -f *.html
