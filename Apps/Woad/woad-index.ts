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

<tagset name="woad-index" parent="woad-web" tagset="woad-xhtml"
        documentWrapper="index" >

<cvs-id>$Id: woad-index.ts,v 1.3 2000-08-10 17:30:28 steve Exp $</cvs-id>

<h1>Tagset for WOAD Indices</h1>

<doc>
<p> This tagset is used for displaying and editing WOAD index files.  An
    index file is a parsed entity (i.e. it has no top-level document element)
    in order to make it easy to append to.  The documentWrapper supplies
    the necessary outer element.
</p>
</doc>

<define element="title">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <table bgcolor="white">
      <tr> <th><big><get name="content"/></big> </th> </tr>
      <tr> <th  bgcolor="#99ccff"> &nbsp; </th> </tr>
    </table>
  </action>
</define>

<define element="Page">
  <doc> Web Page reference.   Path in content. 
  </doc>
  <action>
    <let name="name">
       <logical op="or">
    	 <get name="attributes:name" />
    	 <get name="content" />
       </logical>
    </let>
    <let name="path"><get name="notesPrefix"/>&content;</let>
    <tr> <td> <a href="&path;">&content;</a>
	 </td>
	 <td> &nbsp;
	      <logical op="or">
	        <get name="attributes:title"/>
	        <if> <get name="attributes:dscr"/>
	      	     <then><em><get name="attributes:dscr"/></em></then>
	        </if>
	        <em><if> <get name="attributes:tdscr" />
	      	    <then> <get name="attributes:tdscr" />
	      	 	   <if> <test match="file">
	      			  <get name="attributes:tdscr" /></test>
	      			<test match="directory">
	      			  <get name="attributes:tdscr" /></test>
	      			<else>file</else>
	      		   </if>
	      	    </then>
	      	    <else>(title/description not in this index)</else>
	        </if></em>
	      </logical>
	 </td>
    </tr>
  </action>
</define>

<define element="File">
  <doc> Source File reference.  Path in content.
  </doc>
  <action>
    <let name="path"><get name="sourcePrefix"/>&content;</let>
    <tr> <td> <a href="&path;">&content;</a>
	 </td>
	 <td> &nbsp;
	      <logical op="or">
	        <get name="attributes:title"/>
	        <if> <get name="attributes:dscr"/>
	      	     <then><em><get name="attributes:dscr"/></em></then>
	        </if>
	        <em><if> <get name="attributes:tdscr" />
	      	    <then> <get name="attributes:tdscr" />
	      	 	   <if> <test match="file">
	      			  <get name="attributes:tdscr" /></test>
	      			<test match="directory">
	      			  <get name="attributes:tdscr" /></test>
	      			<else>file</else>
	      		   </if>
	      	    </then>
	      	    <else>(unknown; probably text)</else>
	        </if></em>
	      </logical>
	 </td>
    </tr>
  </action>
</define>

<define element="Def">
  <doc> Word definition
  </doc>
  <action><hide>
    <let name="cxt"><get name="attributes:context"/></let>
    <let name="word"><get name="attributes:word"/></let>
    <let name="path"><get name="attributes:path"/></let></hide>
    <tr> <td rowspan="2"> <a href="&path;">&word;</a>
	 </td>
	 <td> <a href="&path;">&path;</a>
	 </td>
    </tr>
    <tr> <td> &nbsp;&nbsp;&nbsp; &content;
	 </td>
    </tr>
  </action>
</define>


<define element="index" syntax="quoted">
  <doc> This element wraps the index
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
<title>&DOC:path;</title>
</head><body bgcolor="99ccff">
<header>&DOC:name; Index file</header>
<set name="notesPrefix">
    <if> <test exact="yes" match="/"><get name="SITE:notesPrefix"/></test>
         <else><get name="SITE:notesPrefix"/></else>
    </if>
</set>
<set name="sourcePrefix">
    <if> <test exact="yes" match="/"><get name="SITE:sourcePrefix"/></test>
         <else><get name="SITE:sourcePrefix"/></else>
    </if>
</set>
<hr />

<table bgcolor="white" border="2">    
<expand><get name="content"/></expand>
</table>
<hr />
<h5><em>This file is automatically maintained and cannot be edited.</em></h5>

</body></html>
  </action>
</define>

</tagset>

