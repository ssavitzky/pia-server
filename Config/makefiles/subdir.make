### subdir.make -- makefile template for subdirectories
#   $Id: subdir.make,v 1.5 1999-12-14 23:12:11 steve Exp $

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
# This makefile contains rules for subdirectories
#
########################################################################

### Usage:
#	PIADIR=../../....
#	MF_DIR=$(PIADIR)/Config/makefiles
#	MYNAME=<name of this directory>
#	MYPATH=<path from PIADIR to this directory>
#	include $(MF_DIR)/file.make
#  (up to this point it's standard boilerplate)
#	SUBDIRS = x y z
#	include $(MF_DIR)/subdir.make
#

### Targets:
#	all	build executables, etc.
#	doc	build documentation
#	clean	remove trash
#	setup	initialize Makefile and other essential files

all::
	for p in $(SUBDIRS); do test -d $$p && ( cd $$p; if test -f Makefile; \
		then $(MAKE) PIADIR=$(SUBPIADIR) VPATH=$(VPATH)/$$p; fi ); \
	done

doc::
	@@for p in $(SUBDIRS); do test -d $$p && ( cd $$p; if test -f Makefile;\
		then $(MAKE) PIADIR=$(SUBPIADIR) doc; fi); \
	done

clean::
	@@for p in $(SUBDIRS); do test -d $$p && ( cd $$p; if test -f Makefile;\
		then $(MAKE) PIADIR=$(SUBPIADIR) clean; fi); \
	done

setup::
	@@for p in $(SUBDIRS); do \
		echo 'setting up ' $$p; \
		test -d $$p || mkdir $$p; \
		test -f $$p/Makefile || (cd $$p; \
		  make -f ../Makefile PIADIR=$(SUBPIADIR) \
			MYPATH=$(MYPATH)/$$p MYNAME=$$p setupSub); \
		(cd $$p; $(MAKE) PIADIR=$(SUBPIADIR) setup || true); \
	done

setupSub: 
	echo   '### Makefile for' $(MYPATH) 			 > Makefile
	echo   '#	$$Id: subdir.make,v 1.5 1999-12-14 23:12:11 steve Exp $$	'				>> Makefile
	echo   '# 	COPYRIGHT 1999, Ricoh Silicon Valley' 	>> Makefile
	echo    						>> Makefile
	[ -z $(ABSPIA) ] || echo 'ABSPIA=$(ABSPIA)'		>> Makefile
	echo   'PIADIR=$(PIADIR)'				>> Makefile
	echo   'MF_DIR=$$(PIADIR)/Config/makefiles'		>> Makefile
	echo   'MYNAME=$(MYNAME)'				>> Makefile
	echo   'MYPATH=$(MYPATH)'				>> Makefile
	echo   'include $$(MF_DIR)/file.make'			>> Makefile
	-grep '^include ' ../Makefile \
		| grep -v file.make | grep -v subdir.make 	>> Makefile
	echo    						>> Makefile

### Define ABSPIA if the path to PIADIR is absolute.

ifndef ABSPIA
SUBPIADIR = ../$(PIADIR)
else
SUBPIADIR = $(PIADIR)
endif
