###### Top-level Makefile for PIA
#	$Id: Makefile,v 1.49 2001-04-03 00:25:38 steve Exp $

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
 # Contributor(s): steve@rii.ricoh.com, pgage@rii.ricoh.com,
 #		   wolff@rii.ricoh.com
 #
############################################################################## 

PIADIR=.
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=pia
MYPATH=

### Subdirectories.  
#	Order is important -- code built in src needs to be in place
#	before we can use the DPS to process documentation, for example.

SUBDIRS= src bin lib Tagsets Doc

### Standard includes.  Targets "all", "doc", "clean", etc.

include $(MF_DIR)/file.make
include $(MF_DIR)/subdir.make

###### The following is unique to the top level ###############################

### Version information:

VENDOR_TAG  = PIA
RELEASE     = 2
MAJOR       = 1
MINOR       = 6
SUFFIX      = 

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

### PIA setup information

PIA_ROOT = $$HOME/.pia

############################################################################# 
###
### How to make a source release:
###
###   1. Set make macros RELEASE, MAJOR, MINOR, and SUFFIX as appropriate.
###   2. If the version number has changed, commit the Makefile NOW.
###	 Building includes a "cvs checkout"; everything in the release
###	 will come out of the CVS tree, not your working directory.
###   2. "make prep-checkout" : this updates _and_commits_ Version.java.
###	 It then tags the CVS tree and creates a clean build directory.
###   3. "make rsync-cvs" (running under ssh-agent -- otherwise do it manually)
###   4. "make src-release"
###	 If part of this fails, it breaks down into these substeps:
###	 make do_checkout build_release tar_file
###   5. upload tar file: "make upload" works if running under ssh-agent. 
###   6. Fix RiSource.org/{News/news.html, PIA/latest.html}

### src-release:
###   1. do_checkout	A complete "cvs checkout" from the PUBLIC SERVER.
###   2. build_release 	The resulting directory is built
###   3. tar_file	The entire directory is tarred up.

src-release:: do_checkout build_release tar_file
	@echo 'Build complete.  Your next step is "make upload".'
	@echo '"make copy_src_dir" is now optional'

### Commands:

RSYNC = rsync -a --numeric-ids --delete -v
RSYNC_ND = rsync -a --numeric-ids -v

### Operations involving version number:
###
###	update-version		updates files that depend on the version #

RISOURCE=src/java/org/risource
RELNOTES=Doc/Release

report-version::
	@echo $(VERSION_ID)

update-version:: $(RISOURCE)/Version.java
	@echo version updated to $(VERSION_ID)

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
	@echo $(VERSION) built `date` by `whoami` > version_id
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
	cd $(REL_DIR)/PIA; $(MAKE) -k all version_id doc

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
#  !!!	No trailing slash on src means delete everything but PIA in dest !!!

# Make a tar file.  >>> REQUIRES GNU tar <<<
tar_file::
	@tar --version || (echo "You don't have GNU tar"; false)
	cd $(REL_DIR)  ; tar czf $(DEST_DIR)/$(TAR_NAME).tgz PIA
	cd $(DEST_DIR) ; rm -f pia_src.tgz ; ln -s $(TAR_NAME).tgz pia_src.tgz


### Uploading:
### 	These make targets require you to be running "ssh-agent"
###	Otherwise you must issue the commands manually

# rsync-cvs: synchronize the local and remote CVS trees.
rsync-cvs::
	$(RSYNC) -e ssh /pia1/CvsRoot/PIA/ $(RMT_HOST):$(CVS_RMT)/PIA
#  !!!	Trailing slash on src means other CVS modules on remote are allowed.
#	Without the trailing slash on src and /PIA on dst, anything else
#	in the destination directory would be deleted by rsync.

# rsync-cvs: synchronize selected files between the local and remote CVSROOT 
# 	directories.  This should not have to be done very often.  Possibly,
#	never again.
rsync-cvs-root::
	$(RSYNC_ND) -e ssh /pia1/CvsRoot/CVSROOT/cvsignore* \
	            $(RMT_HOST):$(CVS_RMT)/CVSROOT

# upload: upload the tar file, 
#	  make a new symlink in the FTP directory
#	  untar the PIA/Doc subtree into the web directory.
upload::
	scp $(DEST_DIR)/$(TAR_NAME).tgz $(RMT_HOST):$(FTP_RMT)
	ssh $(RMT_HOST) rm -f $(FTP_RMT)/pia_src.tgz
	ssh $(RMT_HOST) cd $(FTP_RMT) \; ln -s $(TAR_NAME).tgz pia_src.tgz
	ssh $(RMT_HOST) cd $(WEB_RMT) \; tar xzf $(FTP_RMT)/pia_src.tgz PIA/Doc
	@echo 'Now fix RiSource.org/PIA/{latest.html, downloading.html}'
	@echo 'They then need to be uploaded to $(WEB_RMT)/PIA'

############################################################################# 
###
### Directory index (Done as part of "make doc")
###
###	First, make a list of all the directories, in all-dirs.log
###	Then, make it into an HTML file that we can use as an index. 
###

doc:: all-dirs.html sidebar.html

index::  all-dirs.html sidebar.html

all-dirs.html: all-dirs.log
	perl src/app/tools/index-dirs.pl < all-dirs.log > all-dirs.html

sidebar.html: all-dirs.log
	COMPACT=1 perl src/app/tools/index-dirs.pl < all-dirs.log > sidebar.html

all-dirs.log::
	find . -type d \! -name CVS -print  | sort > all-dirs.log


############################################################################# 
###
### WOAD indices:
###
###	my-woad-index 	constructed in $HOME/.pia  just the indices
###	
###	  One would like to ship a WOAD index with the PIA, but it's almost
###	  impossible without forcing the use of the pre-built index as the
###	  WOAD root.  Also, it requires the use of subdirectories that start
###	  with ".", which some OS's may not support.  Bad idea.
###
###	my-woad-xref 	constructed in $HOME/.pia  index plus cross-references
###
###	  WARNING! At the moment, this requires about 80Mb of RAM!
###		 The resulting cross-reference index eats 43Mb of disk.
###		 It was much worse, but compactifying <Ref>'s fixed it.
###
###	my-woad-yref 	constructed in $HOME/.pia  index plus cross-references
###
###	  Just indexes the definitions.  This saves an enormous amount of 
###	  both space and time. 

my-woad-index::
	time perl src/app/tools/woad-index.pl source=. root=$(PIA_ROOT) \
	| tee woad-index.log

my-woad-xref::
	time perl src/app/tools/woad-index.pl source=. root=$(PIA_ROOT) -x \
	| tee woad-index.log

my-woad-yref::
	time perl src/app/tools/woad-index.pl source=. root=$(PIA_ROOT) -y \
	| tee woad-index.log

############################################################################# 
###
### Old stuff.
###
###	We used to have make targets for building things like a CD-ROM and 
###	separate source and binary releases.  The Makefile that contained
###	all that has been renamed Makefile.cdrom, checked in, and then removed.
###	If you want it, you can retrieve it from the Attic using CVS.
###
############################################################################# 

###### End of Makefile #####################################################
