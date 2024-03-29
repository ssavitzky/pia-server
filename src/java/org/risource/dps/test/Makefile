###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id: Makefile,v 1.6 2001-04-03 00:04:51 steve Exp $

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


PACKAGE=org.risource.interform.test
TOPDIR=../../../..

FILES = \
	t1.xh \
	t2.html \
	t6.xh

.SUFFIXES: .txt .html .if .xh .xx .out .ok .diff .run

DEBUG =
CLASS = org.risource.dps.Filter
RUN   =	$(BINDIR)/pia_wrapper

.txt.out:
	$(RUN) $(CLASS) $(DEBUG) -t basic $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.html.out:
	$(RUN) $(CLASS) $(DEBUG) -t HTML -e $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

# ./$< is used to make fileName and filePath different.
.if.out:
	$(RUN) $(CLASS) $(DEBUG) -t legacy ./$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.xh.out:
	$(RUN) $(CLASS) $(DEBUG) -t xhtml ./$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.xx.out:
	$(RUN) $(CLASS) $(DEBUG) -t xxml ./$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

# make foo.run runs the tests with output on STDOUT.
.txt.run:
	$(RUN) $(CLASS) $(DEBUG) -t basic $<  2>$*.log \
	| tee $*.out

.html.run:
	$(RUN) $(CLASS) $(DEBUG) -t HTML -e $< 2>$*.log \
	| tee $*.out

# ./$< is used to make fileName and filePath different.
.if.run:
	$(RUN) $(CLASS) $(DEBUG) -t legacy ./$< 2>$*.log \
	| tee $*.out

.xh.run:
	$(RUN) $(CLASS) $(DEBUG) -t xhtml ./$< 2>$*.log \
	| tee $*.out

.xx.run:
	$(RUN) $(CLASS) $(DEBUG) -t xxml ./$< 2>$*.log \
	| tee $*.out


include $(TOPDIR)/makefiles/file.make

all:: $(FILES:.txt=.out)

all:: $(FILES:.if=.out)

all:: $(FILES:.html=.out)

clean::
	rm -f *.out *.diff *.log *.tmp*


BIG_FILE   =../../../../../Doc/Manuals/InterForm/tag_man.html
SMALL_FILE = t2.html
OCLASS     = org.risource.interform.Filter

doc::	time.log
	@echo "timings complete"

time.log::
	@echo "making timings -- this takes several minutes."
	$(MAKE) time_setup time_copy time_parse &> time.log

time_setup::
	@echo '*************************************************************'
	@echo '* Timings for setup (processing a tiny file with -t HTML):'
	@echo '*************************************************************'
	@echo ' '
	@wc $(SMALL_FILE)
	time cat $(SMALL_FILE) >/dev/null
	@echo "process setup -- small file"
	@time $(RUN) $(CLASS) -t HTML $(SMALL_FILE) >/dev/null
	@echo "filter  setup -- small file"
	@time $(RUN) $(OCLASS) -t HTML $(SMALL_FILE) > /dev/null
	@echo ' '

time_copy::
	@echo '*************************************************************'
	@echo '* Timings for copying (processing with non-active tagset):'
	@echo '*************************************************************'
	@echo ' '
	@wc $(BIG_FILE)
	time cat $(BIG_FILE) >/dev/null
	@echo "process -t HTML -n	(parse and copy)"
	@time $(RUN) $(CLASS) -t HTML $(BIG_FILE) -n >/dev/null
	@echo "process -t HTML   	(process with HTML tagset)"
	@time $(RUN) $(CLASS) -t HTML $(BIG_FILE) >/dev/null
	@echo "filter -t HTML   	(old InterForm processor)"
	@time $(RUN) $(OCLASS) -t HTML $(BIG_FILE) >/dev/null
	@echo ' '

time_parse::
	@echo '*************************************************************'
	@echo '* Timings for parsing (-t HTML to a serialized parse tree):'
	@echo '*************************************************************'
	@echo ' '
	@wc $(BIG_FILE)
	@echo "process -t HTML -n -p	(parse and serialize parse tree)"
	@time $(RUN) $(CLASS) -t HTML $(BIG_FILE) -n -p >/dev/null
	@echo "process -t HTML -p	(process and serialize parse tree)"
	@time $(RUN) $(CLASS) -t HTML $(BIG_FILE) -p >/dev/null
	@echo "process -t HTML  	(process to output stream)"
	@time $(RUN) $(CLASS) -t HTML $(BIG_FILE) >/dev/null
	@echo ' '
