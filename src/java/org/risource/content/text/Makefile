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


PACKAGE=org.risource.content.text
TOPDIR=../../../..


FILES = \
	Default.java \
	html.java \
	ProcessedContent.java \
	StringContent.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

Default.class:	../StreamingContent.java

html.class:  Default.java 
annotatedhtml.class:  html.java
ProcessedContent.class: ../../pia/site/SiteDoc.java
