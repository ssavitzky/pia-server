###### dom/tree/Makefile:	DOM implementation classes
# 	$Id: Makefile,v 1.3 1999-06-25 00:17:59 steve Exp $

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


PACKAGE=org.risource.dps.tree
TOPDIR=../../../..

INTERFACES = 

BASE_CLASSES = \
	TreeNode.java \
	TreeCharData.java \
	TreeValue.java

NODE_CLASSES = \
	TreeAttr.java \
	TreeComment.java \
	TreeDecl.java \
	TreeDocType.java \
	TreeFragment.java \
	TreeDocument.java \
	TreeElement.java \
	TreeEntity.java \
	TreeEntityRef.java \
	TreeExternal.java \
	TreeGeneric.java \
	TreePI.java \
	TreeText.java \
	TreeCDATA.java

OTHER_CLASSES = \
	TreeAttrList.java \
	TreeNodeArray.java \
	TreeNodeTable.java \
	TreeNodeMap.java \
	TreeNodeList.java \
	TreeChildList.java 

CLASSES = $(BASE_CLASSES) $(OTHER_CLASSES) $(NODE_CLASSES) 

FILES = $(INTERFACES) $(CLASSES)

include $(TOPDIR)/makefiles/file.make

### Dependencies

TreeAttr.class: TreeNode.java TreeValue.java
TreeEntity.class: TreeNode.java TreeValue.java
TreeEntityRef.class: TreeNode.java TreeValue.java
TreePI.class: TreeNode.java TreeValue.java

TreeElement.class: TreeNode.java
TreeGeneric.class: TreeNode.java TreeElement.java

TreeText.class: TreeNode.java TreeCharData.java
TreeComment.class: TreeNode.java TreeCharData.java
TreeCDATA.class: TreeNode.java TreeCharData.java TreeText.java

TreeNodeMap.class: TreeNodeArray.java

TreeNodeList.class: TreeNodeArray.java

