###### org/risource/Makefile:	RiSource.org's Java classes.
#	$Id: Makefile,v 1.15 2001-04-03 00:04:06 steve Exp $

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
 # This code was initially developed by Ricoh Innovations, Inc.  Portions
 # created by Ricoh Innovations, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 


TOPDIR=.

ADOCDIR = $(PIADIR)/Doc/API/javadoc
CLASSDIR= .

PACKAGES= \
	misc \
	org

REGEXPACKAGE= \
	JP

JIGSAWPACKAGE= \
	org/w3c/tools \
	org/w3c/util \
	org/w3c/www \
	org/w3c/dom

### These are the names of all the packages in the PIA.
###	We need these because javadoc does _not_ operate recursively.

PIAPACKAGENAMES= \
	org.risource.content org.risource.content.text \
	org.risource.dom \
	org.risource.ds \
	org.risource.dps \
	org.risource.dps.active \
	org.risource.dps.handle \
	org.risource.dps.input \
	org.risource.dps.namespace \
	org.risource.dps.output \
	org.risource.dps.parse \
	org.risource.dps.process \
	org.risource.dps.tagset \
	org.risource.dps.tree \
	org.risource.dps.util \
	org.risource.site \
	org.risource.site.util \
	org.risource.pia \
	org.risource.pia.agent \
	org.risource.pia.handle \
	org.risource.pia.site \
	org.risource.tf \
	org.risource.util \
	misc

#	org.risource.sax \

### These are the names of the classes in the JDK that the PIA uses.  
###	Assuming javadoc can find the source code, we should be able to
###	include them in the API documentation.

SUNPACKAGENAMES= \
	java.lang \
	java.io \
	java.net \
	java.text \
	java.util

MISCPACKAGENAMES= \
	org.w3c.tools.timers \
	org.w3c.util \
	org.w3c.www.http \
	org.w3c.www.mime \
	org.w3c.dom \
	org.w3c.dom.html \
	org.xml.sax

REGEXPACKAGENAMES= \
	JP.ac.osaka_u.ender.util.regex

# Packages to document.
#	We don't include $(SUNPACKAGENAMES) because we can't guarantee
#	that the sources are accessible.
PACKAGENAMES= $(PIAPACKAGENAMES) $(MISCPACKAGENAMES) $(REGEXPACKAGENAMES)


# makefiles/file.make defines PIADIR, LIBDIR, BINDIR, DOCDIR
include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

###
### Stuff for jarring class files.
###
###	We used to jar each group of classes separately.  This is neither
###	necessary nor desirable: it prevents running the PIA with a simple:
###	   java -cp /pia.jar org.risource.pia.Pia [options]
###
all:: all_classes.toc

all_classes.toc::
	find . -name '*.class' -print > $@

jarfile::	$(PIADIR)/lib/pia.jar
	@echo jar file written to $(PIADIR)/lib/pia.jar

$(PIADIR)/lib/pia.jar: all_classes.toc
	jar cf $@ `cat all_classes.toc`

### Recursive documentation:

doc:: alldoc
	@echo Java documentation complete

doc:: all_source.log

### "make logs" does "make clean", "make all", and "make doc".
###	Each operation is logged and timed.  

logs::  report start.logs clean.log make.log doc.log 
	@date						>> summary.log
	@echo make logs complete.  Summary:
	cat summary.log

start.logs::
	@echo Doing clean makes to produce log files.
	@echo Go get some coffee -- this takes about 20 minutes

clean.log::
	(time make -k clean				2>&1) | tee clean.log

make.log::
	(time make -k JAVAFLAGS=-deprecation all	2>&1) | tee make.log
	@echo Times:  make				>> summary.log
	@tail -2 make.log				>> summary.log

doc.log::
	(time make -k doc				2>&1) | tee doc.log
	@echo Times:  doc				>> summary.log
	@tail -2 doc.log				>> summary.log

report:: all_holes.log all_lines.log
	@date 						| tee	   summary.log
	@echo FILES: `wc -l all_lines.log`		| tee -a   summary.log
	@echo HOLES: `wc -l all_holes.log`		| tee -a   summary.log

### Recursive hole report.
all_holes.log::
	$(PIADIR)/src/app/tools/hole-report > all_holes.log

### Recursive line count.  
###	Suitable for use with "xdu all_lines.log"
all_lines.log::
	find ${*-.} -type f -name '*.java' -exec wc -l {} \; > all_lines.log

### Recursive Java source-file enumeration.  
all_source.log::
	find ${*-.} -type f -name '*.java' -print | sort > all_files.log

### JavaDoc documentation -- non-recursive.
###	We do this because javadoc doesn't append to existing index files,
###	it smashes them.  This makes it useless when run recursively.

JAVADOC_CLASSES = $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH)
JAVADOC_SOURCES = $(CLASSDIR):$(CLASSPATH):$(JAVASOURCE)
JAVADOC_CMD = javadoc -J-mx64m -J-ms64m -d $(ADOCDIR) -package \
	-sourcepath $(JAVADOC_SOURCES)

alldoc::
	-$(JAVADOC_CMD) $(PACKAGENAMES)

piadoc::
	-$(JAVADOC_CMD) $(PIAPACKAGENAMES)

sundoc::
	-$(JAVADOC_CMD) $(SUNPACKAGENAMES)

