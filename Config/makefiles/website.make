### website.make
# $Id: website.make,v 1.2 1999-07-14 22:26:28 steve Exp $

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
 # This code was initially developed by Stephen R. Savitzky.  Portions
 # created by Stephen R. Savitzky are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s): steve@rsv.ricoh.com steve@theStarport.org
 #
############################################################################## 

########################################################################
#
# This makefile contains rules for maintaining web sites
#
########################################################################

### Commands:

PROCESS = $(PIADIR)/bin/process

### Rules:
#
#	Can make .html files from corresponding .xh files. 
#	Use XH_HTML to specify them all.
#	Set TAGSET if necessary before "include"ing this file.
#

ifndef TAGSET
  TAGSET=xhtml
endif

.SUFFIXES: .html .xh
.xh.html:
	$(PROCESS) -t $(TAGSET) $< > $@
	{ grep -s $@ .cvsignore } || echo $@ >> .cvsignore

XH_FILES= $(wildcard *.xh)
XH_HTML= $(XH_FILES:.xh=.html)

### Targets:
#	ftp	transfer modified files to the website using ftp
#	rcp	transfer modified files to the website using rcp/scp etc
#
#   NOTE:   You need the following definitions:
#	HOST	the name of the host to transfer to.
#		For rcp this may have to be user@host
#	DOTDOT	the parent of the directory to transfer to (on HOST)
#	

FTP= ftp -i
RCP= scp
RSH= ssh

ftp:: $(FILES) $(IMAGES)
	{ grep -s ftp .cvsignore } || echo ftp >> .cvsignore
	@echo cd $(DOTDOT)				 > ftp
	@[ "$(MYNAME)x" = "x" ] || echo mkdir $(MYNAME)	>> ftp
	@[ "$(MYNAME)x" = "x" ] || echo cd $(MYNAME) 	>> ftp
	@echo binary 					>> ftp
	for f in $? ; do echo ftp $$f 			>> ftp ; done
	@echo bye 					>> ftp
	[ "$(HOST)x" = "x" ] || $(FTP) $(HOST) < ftp > ftp.log

rcp:: $(FILES) $(IMAGES)
	{ grep -s rcp .cvsignore } || echo rcp >> .cvsignore
	-$(RSH) $(HOST) mkdir $(DOTDOT)/$(MYNAME)
	$(RCP) $? $(HOST):$(DOTDOT)/$(MYNAME)
	echo $? > rcp
