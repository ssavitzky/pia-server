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
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.        -->
<!-- Portions created by Ricoh Silicon Valley, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s): steve@rsv.ricoh.com pgage@rsv.ricoh.com                -->
<!-- ====================================================================== -->

<tagset name="woad-web" parent="woad-xhtml" tagset="woad-xhtml" >

<cvs-id>$Id: woad-web.ts,v 1.2 2000-06-23 17:31:21 steve Exp $</cvs-id>

<h1>Tagset for WOAD Annotations</h1>

<doc>
<p> This tagset is used for displaying and editing WOAD annotation files.  An
    annotation file has the following structure:
</p>
<pre>
    <tag>note</tag>
	<tag>title</tag><em>title (one line, text only)</em><tag>/title</tag>
	<tag>summary</tag>
	     <em>A brief summary.  Arbitrary HTML permitted.</em>
	<tag>/summary</tag>
	<tag>content</tag>
	     <em>The actual content.  Arbitrary HTML permitted.</em>
	<tag>/content</tag>
    <tag>/note</tag>
</pre>
</doc>

<define element="summary">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <if> <get name="FORM:edit" />
	 <then>
	 </then>
	 <else> <table bgcolor="white" align="center" width="95%">
	   	   <tr> <th bgcolor="#99ccff" align="left" valign="top">
		     	     Summary: </th>
			<td> <get name="content"/> </td>
		   </tr>
		</table>
	 </else>
    </if>
  </action>
</define>

<define element="title">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <if> <get name="FORM:edit" />
	 <then>
	 </then>
	 <else> <table bgcolor="white">
	   	   <tr> <th><big><get name="content"/></big> </th> </tr>
		   <tr> <th  bgcolor="#99ccff"> &nbsp; </th> </tr>
		</table>
	 </else>
    </if>
  </action>
</define>

<define element="content">
  <doc> This element delimits the main content of the note.
  </doc>
  <action>
    <if> <get name="FORM:edit" />
	 <then>
	 </then>
	 <else> <p>&nbsp;</p>
		<table bgcolor="white" align="center" width="95%" border="2">
	   	   <tr> <td><get name="content"/> </td> </tr>
		</table>
	 </else>
    </if>
  </action>
</define>

<define element="note" syntax="quoted">
  <doc> This element delimits the entire note.
  </doc>
  <action>
    <if> <get name="FORM:edit" />
	 <then> 
	 </then>
	 <else> 
	 </else>
    </if>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html><head>
<title><extract><from><get name="content"/></from>
		note title <content />
       </extract></title>
</head><body bgcolor="99ccff">
<expand><get name="content"/></expand>
</body></html>
  </action>
</define>

</tagset>

