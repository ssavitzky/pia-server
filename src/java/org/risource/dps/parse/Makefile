###### dps/parse/Makefile:	Active document parsers
# 	$Id: Makefile,v 1.4 1999-03-23 23:31:58 steve Exp $

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


PACKAGE=org.risource.dps.parse
TOPDIR=../../../..

FILES = \
	AbstractParser.java \
	BasicParser.java \
	HTMLParser.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

AbstractParser.class: ../Tagset.java ../Parser.java \
	../EntityTable.java 
BasicParser.class: AbstractParser.java ../Tagset.java \
	../Parser.java ../EntityTable.java 
HTMLParser.class: BasicParser.java AbstractParser.java \
	../Tagset.java \
	../Parser.java ../EntityTable.java 
