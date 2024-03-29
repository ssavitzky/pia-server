### file.make
# $Id: file.make,v 1.12 2001-01-11 23:37:03 steve Exp $
# COPYRIGHT 1997, Ricoh California Research Center
# Portions COPYRIGHT 1997, Sun Microsystems

# This makefile should be included in the Makefile of each package directory
# For example, if you have a package 'foo' containing 'a.java' and 'b.java'
# your Makefile should look like this:
# ----------
# PACKAGE=foo
# FILES=a.java b.java
# TOPDIR=../../..
# include $(TOPDIR)/makefiles/files.make
# ----------
#
# This file defines the following targets:
# all:	 to build the class files from the java files.
# clean: to clean all sub packages
# doc:   to build the appropriate documentation files from the source
#   	 Normally does nothing, since javadoc is called non-recursively
#	 at the top level.  May be used for auxiliary documentation.
# jdoc:	 to invoke javadoc in this directory.  Does not make package index
#	 or class hierarchy, since these are shared by all packages, but can
#	 be used to quickly update the documentation for a changed package.

# note:
#	The Sun originals required MAKEDIR and DESTDIR to be absolute paths.
#	This has serious problems when you're trying to use source control.

CLASSDIR= $(TOPDIR)

PIADIR=$(TOPDIR)/../..
LIBDIR=$(PIADIR)/lib/java
BINDIR=$(PIADIR)/bin
DOCDIR=$(PIADIR)/Doc/API/javadoc

# S is the path separator: it needs to be ";" on Windows machines.
# F is the file separator: it needs to be "\" on Windows machines.
S=:
F=/
PKGPATH=$(subst .,$F,$(PACKAGE))
ifeq ($F,/)
  CDFIX=
else
  # On windows, cd is sticky.  This is what we need to fix it.
  CDFIX=cd $(PKGPATH)
endif

## Set this on the command line to see warnings about deprecated API's
# JAVAFLAGS=-deprecation

.SUFFIXES: .java .class

## Rule to compile Java class files.  
#  Use one of the two techniques below, depending on $(CLASSPATH):  
#  1. adding $(CLASSDIR) to $(CLASSPATH)
#	This works well if $(CLASSPATH) is already set, 
#	AND it contains the current Java class library.
#  2. cd'ing to $(CLASSDIR) and not setting $(CLASSPATH). 
#	This works only if $(CLASSPATH) is not set, OR it contains "."
#
#	Cleverness involving $(PKGPATH) messes up error parsing because
#	of the relative path.  

ifeq ($(CLASSPATH),)
.java.class:
	wd=`pwd`; export wd; cd $(CLASSDIR); javac -g $(JAVAFLAGS) $$wd/$<
#	cd $(CLASSDIR); javac -g $(JAVAFLAGS) $(PKGPATH)/$<; $(CDFIX)
else
BUILDCLASSES=$(CLASSDIR)$S$(CLASSPATH)
.java.class:
	javac -d $(CLASSDIR) -classpath $(BUILDCLASSES) -g $(JAVAFLAGS) $<;
endif

### Beginning of make targets:

all:: $(FILES:.java=.class)

all-l:: $(FILES:.java=.class)

doc:: holes.log lines.log
	@echo Documenting.

### Javadoc of this package only.  Don't smash existing index files.
###	Note the awkward $(PKGPATH)/$(DOCDIR) construct due to the fact
###	that $(DOCDIR) is relative to the current directory.  If it 
###	turns out not to be (i.e. it was set on the command line) there
###	will be problems.  Don't do that.
###
ifeq ($(CLASSPATH),)
jdoc::
	cd $(CLASSDIR); javadoc -d $(PKGPATH)/$(DOCDIR) -noindex -notree $(PACKAGE); $(CDFIX)
else
jdoc::
	javadoc -d $(DOCDIR) -noindex -notree -classpath $(BUILDCLASSES) $(PACKAGE)
endif

###
### Holes.log: a list of everything that's going to need attention.
###	It comes out in a form that allows emacs to find each hole 
###	using the M-x next-error (C-x `) command

HOLE_REGEXP='[^=]===[^=]|unimplemented|eventually}'
HOLE_FILES= Makefile *.java *htm? *.if *.tex *.p[lm] *.inc

holes.log::
	-egrep -i -n -s $(HOLE_REGEXP) $(HOLE_FILES) /dev/null | tee holes.log

lines.log::
	-wc *.java /dev/null | grep -v /dev/null > lines.log

clean::
	@@rm -rf *~ *.class

clean-l::
	@@rm -rf *~ *.class



