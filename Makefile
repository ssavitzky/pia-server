###### Makefile for pia
#	$Id: Makefile,v 1.27 1999-06-09 18:33:24 steve Exp $

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
 # Contributor(s): steve@rsv.ricoh.com, pgage@rsv.ricoh.com,
 #		   wolff@rsv.ricoh.com
 #
############################################################################## 

PIADIR=.
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=pia
MYPATH=

SUBDIRS= src bin lib Doc

### Standard includes.  Targets "all", "doc", "clean", etc.

include $(MF_DIR)/file.make
include $(MF_DIR)/subdir.make

### The following is unique to the top level ###############################

### Version information:

VENDOR_TAG  = PIA
RELEASE     = 2
MAJOR       = 0
MINOR       = 5
SUFFIX      = a

VERSION_ID = $(VENDOR_TAG)$(RELEASE)_$(MAJOR)_$(MINOR)$(SUFFIX)
VERSION    = $(RELEASE).$(MAJOR).$(MINOR)$(SUFFIX)

### Release directories and file names: 

REL_DIR	    = $(HOME)/src_release
DEST_DIR    = /pia1/pia
TAR_NAME    = pia_src$(VERSION)
CREATE_CVS_TAG = 1

### Remote host and directories: 
###	Note that WEB_RMT is the _parent_ of a PIA subdirectory;
###	you have to be in the parent to untar PIA/Doc (for instance).
RMT_HOST = www.risource.org
WEB_RMT  = /home/web/risource-htdocs
FTP_RMT  = /home/ftp/PIA
CVS_RMT  = /home/cvsroot

### How to make a source release:
###
#   1. Set make macros RELEASE, MAJOR, MINOR, and SUFFIX as appropriate.
#   2. If the version number has changed, commit the Makefile NOW.
#	Building includes a "cvs checkout"
#   2. "make prep-checkout" : this updates _and_commits_ Version.java.
#   3. "make rsync-cvs" (if running under ssh-agent) or DO THIS MANUALLY:  
#	cd /pia1/CvsRoot ; rsync -e ssh -a --numeric-ids --delete -v PIA cvs.risource.org:/home/cvsroot/
#   4. "make src-release"
#	If part of this fails, it breaks down into these substeps:
#	make do_checkout build_release tar_file copy_src_dir
#   5. upload tar file: "make upload" works if running under ssh-agent. 
#   6. Fix RiSource.org/PIA/{latest.html, downloading.html}

### src-release:
###   1. do_checkout	A complete "cvs checkout" from the PUBLIC SERVER.
###   2. build_release 	The resulting directory is built
###   3. tar_file	The entire directory is tarred up.
###   4. copy_src_dir	The directory is copied under /pia1/pia

src-release:: do_checkout build_release tar_file copy_src_dir
	echo 'Build complete.  Your next step is "make upload"'

### Commands:

RSYNC = rsync -a --numeric-ids --delete -v

### Operations involving version number:
###
###	update-version		updates files that depend on the version #

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

# make a version ID file.  copy it to Doc as well.
version_id:: Makefile
	echo $(VERSION) built `date` by `whoami` > version_id
	cp version_id Doc

### Common cvs operations 

cvs_tag::
	cvs tag $(VERSION_ID)

cvs_rtag::
	cvs rtag $(VERSION_ID) PIA

cvs_export::
	cvs export -r $(VERSION_ID)

### Release Components

build_release::
	cd $(REL_DIR)/PIA; $(MAKE) all version_id doc

prep_rel_dir::
	rm -rf $(REL_DIR); mkdir $(REL_DIR)

prep-checkout::
	$(MAKE) update-version
	$(MAKE) prep_rel_dir
	if [ $(CREATE_CVS_TAG) -gt 0 ]; then $(MAKE) cvs_rtag; fi

do_checkout::
	cd $(REL_DIR) ; \
	  cvs -d :pserver:anonymous@cvs.risource.org:/home/cvsroot checkout PIA

copy_src_dir::
	-mkdir $(DEST_DIR)src_release$(VERSION)
	$(RSYNC) $(REL_DIR)/PIA $(DEST_DIR)/src_release$(VERSION)
# 	No trailing slash on src means delete everything but PIA in dest.

# Make a tar file.  >>> REQUIRES GNU tar <<<
tar_file::
	tar --version || (echo "You don't have GNU tar"; false)
	cd $(REL_DIR)  ; tar czf $(DEST_DIR)/$(TAR_NAME).tgz PIA
	cd $(DEST_DIR) ; rm -f pia_src.tgz ; ln -s $(TAR_NAME).tgz pia_src.tgz


### Uploading:
### 	These make targets require you to be running "ssh-agent"
###	Otherwise you must issue the commands manually

# rsync-cvs: synchronize the local and remote CVS trees.
rsync-cvs::
	$(RSYNC) -e ssh /pia1/CvsRoot/PIA/ $(RMT_HOST):$(CVS_RMT)/PIA
# 	Trailing slash on src means other CVS modules on remote are allowed.
#	Without the trailing slash on src and /PIA on dst, anything else
#	in the destination directory would be deleted by rsync.

# upload: upload the tar file, 
#	  make a new symlink in the FTP directory
#	  untar the PIA/Doc subtree into the web directory.
upload::
	scp $(DEST_DIR)/$(TAR_NAME).tgz $(RMT_HOST):$(FTP_RMT)
	ssh $(RMT_HOST) rm -f $(FTP_RMT)/pia_src.tgz
	ssh $(RMT_HOST) cd $(FTP_RMT) \; ln -s $(TAR_NAME).tgz pia_src.tgz
	ssh $(RMT_HOST) cd $(WEB_RMT) \; tar xzf $(FTP_RMT)/pia_src.tgz PIA/Doc
	echo 'Now fix RiSource.org/PIA/{latest.html, downloading.html}'
	echo 'They then need to be uploaded to $(WEB_RMT)/PIA'

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


