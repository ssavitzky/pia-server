### Makefile for bin
#	$Id: Makefile,v 1.5 2001-04-03 00:04:00 steve Exp $

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


PIADIR=..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=bin
MYPATH=bin

include $(MF_DIR)/file.make

all:: 
	@echo "No scripts to make"

### Executables 
###	These are obsolete and unnecessary.  Retained for reference.

clean::
	rm -f jpia ppia

# This constructs a shell script to run the Java PIA in this directory. 
#	If there is no PIA_HOME in the environment, it overrides it.

jpia: Makefile 
	echo '#!/bin/sh' 					     > jpia
	echo test '"x$$PIA_HOME" != "x" ||' PIA_HOME=`(cd ..; pwd)` >> jpia
	echo export PIA_HOME 					    >> jpia
	echo exec '$$'PIA_HOME/bin/pia '$$*' 	  		    >> jpia
	chmod +x jpia

## This actually constructs a shell script to run the PERL PIA. 
##	If there is no PIA_HOME in the environment, it overrides it.

ppia: Makefile 
	echo '#!/bin/sh' 					     > ppia
	echo test '"x$$PIA_HOME" != "x" ||' PIA_HOME=`(cd ..; pwd)` >> ppia
	echo export PIA_HOME 					    >> ppia
	echo exec perl '$$'PIA_HOME/lib/perl/pia.pl '$$*' 	    >> ppia
	chmod +x ppia
