###### Makefile for pia
#	$Id: Makefile,v 1.23 1999-06-02 21:49:05 steve Exp $

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

PIADIR=.
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=pia
MYPATH=

SUBDIRS= src bin lib Doc

include $(MF_DIR)/file.make
include $(MF_DIR)/subdir.make

### The following is unique to the top level ###
###
###   Make targets:
###	update-version		patch files that depend on version number
###	cvs_rtag		tag repository with version number

### Version information:

VENDOR_TAG  = PIA
RELEASE     = 2
MAJOR       = 0
MINOR       = 5
SUFFIX      = 

### Release directories and file names: 

REL_DIR	    = $(HOME)/src_release
DEST_DIR    = /pia1/pia
TAR_NAME    = pia_src$(VERSION)
CREATE_CVS_TAG = 1

### How to make a source release:
###
#   1. Set make macros RELEASE, MAJOR, MINOR, and SUFFIX as appropriate.
#   2. If the version number has changed, commit the Makefile NOW.
#	Building includes a "cvs checkout"
#   2. "make prep_checkout" : this updates _and_commits_ Version.java.
#   3. DO THIS MANUALLY:  
#	cd /pia1/CvsRoot ; rsync -e ssh -a --numeric-ids --delete -v PIA cvs.risource.org:/home/cvsroot/
#   4. "make src.tar"

### Commands:

### Operations involving version number:
###
###	update-version		updates files that depend on the version #
VERSION_ID = $(VENDOR_TAG)$(RELEASE)_$(MAJOR)_$(MINOR)$(SUFFIX)
VERSION    = $(RELEASE).$(MAJOR).$(MINOR)$(SUFFIX)

RISOURCE=src/java/org/risource
RELNOTES=Doc/Release

report-version::
	echo $(VERSION_ID)

update-version:: $(RISOURCE)/Version.java
	echo version updated to $(VERSION_ID)

$(RISOURCE)/Version.java:: Makefile
	perl -p -i -e 's/[0-9]+;/$(RELEASE);/ if /RELEASE/;' \
		 -e 's/[0-9]+;/$(MAJOR);/ if /MAJOR/;' \
		 -e 's/[0-9]+;/$(MINOR);/ if /MINOR/;' \
		 -e 's/\"[^"]*\"/\"$(SUFFIX)\"/ if /SUFFIX/;' \
		$(RISOURCE)/Version.java
	cvs commit -m 'Update to version $(VERSION)' $@

# Hack the release notes file.  Does not appear to be needed.
$(RELNOTES)/r$(RELEASE).$(MAJOR).html:: Makefile
	perl -p -i -e 's/[0-9.a-zA-Z]+\</$(VERSION)\</ if /\<h1\>/;' \
		$(RELNOTES)/r$(RELEASE).$(MAJOR).html
	cvs commit -m 'Update to version $(VERSION)' $@

version_id::
	echo $(VERSION) `date` > version_id

### Common cvs operations 

cvs_tag::
	cvs tag $(VERSION_ID)

cvs_rtag::
	cvs rtag $(VERSION_ID) PIA

export::
	cvs export -r $(VERSION_ID)


### Release preparation

prep_src_rel::
	make clean ; make all version_id doc

prep_rel_dir::
	rm -rf $(REL_DIR); mkdir $(REL_DIR)

prep_checkout::
	$(MAKE) update-version
	$(MAKE) prep_rel_dir
	if [ $(CREATE_CVS_TAG) -gt 0 ]; then $(MAKE) cvs_rtag; fi

### Make a tar file.  >>> REQUIRES GNU tar <<<
###
###	A complete "cvs checkout" is done from the PUBLIC SERVER.
###	Then the entire directory is tarred up.
###
#   Notes by steve@rsv.ricoh.com 1999-06-02
#	1. removed "make cvs_rtag" because prep_checkout does it.
#	   This means that we can leave CREATE_CVS_TAG = 1
#	2. create tarfile directly in $(DST_DIR) instead of moving it; 
#	   moving will be very expensive if $(HOME) and $(DST_DIR) are in 
#	   different filesystems. 
src.tar:	
	tar --version || (echo "You don't have GNU tar"; false)
	cd $(REL_DIR) ; \
	  cvs -d :pserver:anonymous@cvs.risource.org:/home/cvsroot checkout PIA
	cd $(REL_DIR)/PIA ;  make prep_src_rel
	cd $(REL_DIR) ; tar czf $(DEST_DIR)/$(TAR_NAME).tgz PIA
	cd $(DEST_DIR) ;  rm pia_src.tgz ; ln -s $(TAR_NAME).tgz pia_src.tgz
	cd $(DEST_DIR) ; mkdir src_release$(VERSION) ; cp -r $(REL_DIR)/PIA src_release$(VERSION)



# test making parts of a release
test:	

###
### Old stuff.
###
###	Everything after this point is _probably_ obsolete; it is being kept
###	around because at some point we may go back and revise our procedures
###	to use it again.
###

### Stuff for making a CD-ROM

CD_ROM_SRC_DIR	= cd_rom_src_dir
CD_ROM_DEST_DIR	= cd_rom_dest_dir
CD_ROM_PUB      = Ricoh Silicon Valley

cd_rom:		$(CD_ROM_SRC_DIR) $(CD_ROM_DEST_DIR) version_id
	mkisofs -f -R -T \
	    -P "$(CD_ROM_PUB)" -A $(VERSION_ID) -V $(VERSION_ID) \
	    -o $(CD_ROM_DEST_DIR)/pia.iso $(CD_ROM_SRC_DIR)



### Put all CVS files into a tar file for safekeeping.

SRCDIR=src/java/org/risource
CLASSDIR=src/java
PIALIBDIR=lib/java
PIABINDIR=bin
DOCRELEASE=Doc/Release
DOCPAPER=Doc/Papers
CDROMDIR=../cdrom

cvs.tar::
	find $(SUBDIRS) -name CVS -print | tar -cT - -f cvs.tar

### Prepare release

rm_bin_tar::
	 rm -f pia_bin.toc 
	 rm -f pia_bin.tgz
	 rm -f $(PIALIBDIR)/pia.zip
rm_pia_tar::
	rm -f pia.toc
	rm -f pia_src.tgz
	rm -f $(PIALIBDIR)/pia.zip

prep_rel::
	cd $(CLASSDIR); make clean ; make
	cd $(CLASSDIR); make doc
	cd $(CLASSDIR);

### add crln
crfixbat:: 
	cd $(PIABINDIR); cp pia.bat pia.bak; cp_ascii < pia.bak > pia.bat
	cd $(PIABINDIR); cp piajdk.bat piajdk.bak; cp_ascii < piajdk.bak > piajdk.bat
	cd $(PIABINDIR); cp autorun.inf autorun.bak; cp_ascii < autorun.bak > autorun.inf
	cd $(PIABINDIR); rm -f *.bak
	cp README README.bak; cp_ascii < README.bak > README.txt
	cp INSTALL INSTALL.bak; cp_ascii < INSTALL.bak > INSTALL.txt
	rm README.bak INSTALL.bak
	cd $(DOCRELEASE); cp readme readme.bak; cp_ascii < readme.bak > readme.txt; rm readme.bak


### Binary release

pia_bin.toc:: 
	cd ..; find PIA \! -type d -print \
	    | grep -v CVS \
	    | grep -v Config/CDRW | grep -v Config/Demos \
	    | grep -v Config/MB3 \
	    | grep -v Config/Photo | grep -v Config/Printer \
	    | grep -v Config/WebWort | grep -v Config/cdrom \
	    | grep -v pia_src.tgz | grep -v pia.toc \
	    | grep -v src > PIA/pia_bin.toc 

pia_bin.tar:	rm_bin_tar prep_rel crfixbat pia_bin.toc
	cd ..; $(TAR) cfT PIA/pia_bin PIA/pia_bin.toc ;  /bin/gzip -S .tgz PIA/pia_bin

### Source release

pia.toc:: 
	cd ..;	find PIA \! -type d -print \
	    | grep -v CVS \
	    | grep -v src/app/webfax | grep -v src/perl \
	    | grep -v src/tex \
	    | grep -v Config/CDRW | grep -v Config/MB3 \
	    | grep -v Config/Photo | grep -v Config/Printer \
	    | grep -v Config/WebWort | grep -v Config/cdrom \
	    | grep -v Config/Demos | grep -v Doc/Papers \
	    | grep -v pia_bin.tgz | grep -v pia_bin.toc > PIA/pia.toc 

pia.tar:	rm_pia_tar prep_rel crfixbat pia.toc
	cd ..; $(TAR) cfT PIA/pia_src PIA/pia.toc ;	/bin/gzip -S .tgz PIA/pia_src



### Binary and source release
pia_bin_src: 	rm_bin_tar rm_pia_tar prep_rel crfixbat pia_bin.toc pia.toc
	cd ..; $(TAR) cfT PIA/pia_bin PIA/pia_bin.toc ;  /bin/gzip -S .tgz PIA/pia_bin
	cd ..; $(TAR) cfT PIA/pia_src PIA/pia.toc ;	/bin/gzip -S .tgz PIA/pia_src

pia_cdrom::	pia_bin_src
	cp pia_bin.tgz $(CDROMDIR); cp pia_src.tgz $(CDROMDIR)
	cp Doc/Release/readme.txt $(CDROMDIR)
	cp bin/autorun.inf $(CDROMDIR)
	cp bin/pia.pif $(CDROMDIR)


