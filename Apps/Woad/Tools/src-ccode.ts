<!DOCTYPE tagset SYSTEM "tagset.dtd">
<!-- ====================================================================== -->
<!-- The contents of this file are subject to the Ricoh Source Code Public  -->
<!-- License Version 1.0 (the "License"); you may not use this file except  -->
<!-- in compliance with the License.  You may obtain a copy of the License  -->
<!-- at http://www.risource.org/RPL                                         -->
<!--                                                                        -->
<!-- Software distributed under the License is distributed on an "AS IS"    -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See   -->
<!-- the License for the specific language governing rights and limitations -->
<!-- under the License.                                                     -->
<!--                                                                        -->
<!-- This code was initially developed by Ricoh Innovations, Inc.        -->
<!-- Portions created by Ricoh Innovations, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s): steve@rii.ricoh.com pgage@rii.ricoh.com                -->
<!-- ====================================================================== -->

<tagset name="src-ccode" parent="src-file" tagset="woad-xhtml"
	include="src-wrapper" documentWrapper="-document-"
	parser="CodeParser" comment="//" cbegin="/*" cend="*/"
	xrefs="SITE:xref" xprefix="/.words/xref/"
	keywords="if else while until for switch case
		  try catch throws implements instanceof new this super
		  class interface static public private protected final 
		  int long short byte float char void boolean true false null  
		  String return import export package include define"
>

<cvs-id>$Id: src-ccode.ts,v 1.7 2001-04-03 00:03:53 steve Exp $</cvs-id>

<h1>WOAD source-file listing for Java, C and C-like languages.</h1>

<doc> This tagset is used for listing arbitrary files.
</doc>

<define element="" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="&tagname;" tc="#666666"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="#comment" syntax="quoted">
  <action><font color="red">&lt;!--&value;--&gt;</font><hide>
    </hide></action>
</define>

<define element="#pi" syntax="quoted">
  <action><font color="red">&lt;?&name; &value;?&gt;</font><hide>
    </hide></action>
</define>

<define element="#reference" syntax="quoted">
  <action><font color="#999999">&amp;&name;;</font><hide>
    </hide></action>
</define>

<define element="#doctype" syntax="quoted">
  <action><font color="red">&lt;!&name; &value;&gt;</font><hide>
    </hide></action>
</define>

</tagset>

