### Makefile for pia
#	Makefile,v 1.16 1999/03/02 20:24:11 pgage Exp

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

### Commands:

# === GNU tar required ===
TAR=/usr/local/bin/tar

### Common cvs operations 

cvs_tag::
	cvs tag $(VERSION_ID)

cvs_rtag::
	cvs rtag $(VERSION_ID) PIA

foobar::
	echo $(VERSION_ID)


### Stuff for making a CD-ROM

CD_ROM_SRC_DIR	= cd_rom_src_dir
CD_ROM_DEST_DIR	= cd_rom_dest_dir
CD_ROM_PUB      = Ricoh Silicon Valley

VENDOR_TAG  = PIA
RELEASE         = 1
MAJOR           = 1
MINOR           = 0
SUFFIX          = 
VERSION_ID = $(VENDOR_TAG)$(RELEASE)_$(MAJOR)_$(MINOR)$(SUFFIX)
VERSION    = $(VENDOR_TAG)$(RELEASE).$(MAJOR).$(MINOR)$(SUFFIX)

version_id::
	echo $(VERSION) `date` > version_id

cd_rom:		$(CD_ROM_SRC_DIR) $(CD_ROM_DEST_DIR) version_id
	mkisofs -f -R -T \
	    -P "$(CD_ROM_PUB)" -A $(VERSION_ID) -V $(VERSION_ID) \
	    -o $(CD_ROM_DEST_DIR)/pia.iso $(CD_ROM_SRC_DIR)



### Put all CVS files into a tar file for safekeeping.

SRCDIR=src/java/crc
CLASSDIR=src/java
PIALIBDIR=lib/java
PIABINDIR=bin
INTERFORM=
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
	cd $(CLASSDIR);make pia.zip; make alldoc
	cd $(CLASSDIR);rm -fr java

cp_build_noa::
	cp /pia1/home/wolff/pia/Doc/Papers/BuildingNOA/BuildingNOA.ps $(DOCPAPER)/BuildingNOA
	cp -R /pia1/home/wolff/pia/Doc/Papers/BuildingNOA/BuildingNOA $(DOCPAPER)/BuildingNOA

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

pia_bin.tar:	rm_bin_tar prep_rel crfixbat cp_build_noa pia_bin.toc
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

pia.tar:	rm_pia_tar prep_rel crfixbat cp_build_noa pia.toc
	cd ..; $(TAR) cfT PIA/pia_src PIA/pia.toc ;	/bin/gzip -S .tgz PIA/pia_src

### Binary and source release
pia_bin_src: 	rm_bin_tar rm_pia_tar prep_rel crfixbat cp_build_noa pia_bin.toc pia.toc
	cd ..; $(TAR) cfT PIA/pia_bin PIA/pia_bin.toc ;  /bin/gzip -S .tgz PIA/pia_bin
	cd ..; $(TAR) cfT PIA/pia_src PIA/pia.toc ;	/bin/gzip -S .tgz PIA/pia_src

pia_cdrom::	pia_bin_src
	cp pia_bin.tgz $(CDROMDIR); cp pia_src.tgz $(CDROMDIR)
	cp Doc/Release/readme.txt $(CDROMDIR)
	cp bin/autorun.inf $(CDROMDIR)
	cp bin/pia.pif $(CDROMDIR)


