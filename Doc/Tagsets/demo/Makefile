###### Doc/Tagset/demo Makefile:  Makes documentation pages from Demo agent.
# 	$Id: Makefile,v 1.5 1999-12-14 18:58:27 steve Exp $

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

PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles

DEMO_AGENT_DIR=$(PIADIR)/Agents/Demo

### Directories:
XHDOCDIR = $(PIADIR)/Doc/Tagsets/demo

### The agent .xh files to be converted to documentation
XHTMLS = \
	$(DEMO_AGENT_DIR)/demo_debug.xh \
	$(DEMO_AGENT_DIR)/demo_define.xh \
	$(DEMO_AGENT_DIR)/demo_getset.xh \
	$(DEMO_AGENT_DIR)/demo_if.xh \
	$(DEMO_AGENT_DIR)/demo_logic.xh \
	$(DEMO_AGENT_DIR)/demo_num.xh \
	$(DEMO_AGENT_DIR)/demo_parse.xh \
	$(DEMO_AGENT_DIR)/demo_protect.xh \
	$(DEMO_AGENT_DIR)/demo_rep.xh \
	$(DEMO_AGENT_DIR)/demo_sel.xh \
	$(DEMO_AGENT_DIR)/demo_subst.xh \
	$(DEMO_AGENT_DIR)/demo_test.xh \
	$(DEMO_AGENT_DIR)/demo_output.xh \
	$(DEMO_AGENT_DIR)/demo_text.xh

XHTML_DOCS = $(XHTMLS:.xh=.html)

include $(MF_DIR)/file.make

.SUFFIXES: .xh .html

### Converting xhtml to html documentation:

DOC_TS = tsdoc.ts

.xh.html:
	process -t $(PIA_DIR)/Tagsets/pia-xhtml $< > $@

doc:: xh-doc

xh-doc:: $(XHTML_DOCS)
	cp $? $(XHDOCDIR)
	rm $(DEMO_AGENT_DIR)/demo*.html

# Do not remove index.html--is not auto-generated
clean::
	rm -f demo*.html hello*.html

### Documentation dependencies:

demo_debug.html: $(DEMO_AGENT_DIR)/demo_debug.xh
demo_define.html: $(DEMO_AGENT_DIR)/demo_define.xh
demo_getset.html: $(DEMO_AGENT_DIR)/demo_getset.xh
demo_if.html: $(DEMO_AGENT_DIR)/demo_if.xh
demo_logic.html: $(DEMO_AGENT_DIR)/demo_logic.xh
demo_num.html: $(DEMO_AGENT_DIR)/demo_num.xh
demo_parse.html: $(DEMO_AGENT_DIR)/demo_parse.xh
demo_protect.html: $(DEMO_AGENT_DIR)/demo_protect.xh
demo_rep.html: $(DEMO_AGENT_DIR)/demo_rep.xh
demo_sel.html: $(DEMO_AGENT_DIR)/demo_sel.xh
demo_subst.html: $(DEMO_AGENT_DIR)/demo_subst.xh
demo_test.html:	$(DEMO_AGENT_DIR)/demo_test.xh
demo_text.html:	$(DEMO_AGENT_DIR)/demo_text.xh
hello-world.html:$(DEMO_AGENT_DIR)/hello-world.xh

