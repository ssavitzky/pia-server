###### pia/Makefile:	Platform for Information Appliances
#	$Id: Makefile,v 1.8 1999-10-27 16:33:57 steve Exp $

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
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 


PACKAGE=org.risource.pia
TOPDIR=../../..
REL_DIR = $(PIADIR)/Doc/Release

PACKAGES = \
	agent \
	handle \
	site

FILES = \
	Athread.java \
	HttpBuffer.java \
	Accepter.java \
	Resolver.java \
	Transaction.java \
	Machine.java \
	Configuration.java \
	Content.java \
	InputContent.java \
	ContentOperationUnavailable.java \
	Crontab.java \
	CrontabEntry.java \
	Agent.java \
	ContentFactory.java \
	Logger.java \
	Pia.java \
	PiaInitException.java \
	PiaRuntimeException.java \
	FormContent.java \
	MultipartFormContent.java \
	HeaderFactory.java \
	Headers.java \
	ThreadPool.java \
	HTTPRequest.java \
	HTTPResponse.java \
	Authenticator.java 

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

# Documentation: copy to-do file to the release directory
doc:: $(REL_DIR)/to-do.pia.html

$(REL_DIR)/to-do.pia.html: to-do.html
	cp $? $@

### Dependencies:

Pia.class: ../Version.java \
	../site/Root.java ../site/Site.java ../site/Resource.java
