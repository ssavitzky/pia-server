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

<cvs-id>$Id: woad-web.ts,v 1.3 2000-06-28 01:49:18 steve Exp $</cvs-id>

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
<p> There are two possible queries:  <code>?edit</code>, which puts the note
    into ''edit mode'', and <code>update&amp;...</code> which actually
    performs the update (and is normally passed via POST). 
</p>
</doc>

<define element="summary">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <table  width="95%">
    <if> <get name="FORM:edit" />
	 <then>
      	    <tr> 
	      <th bgcolor="#99ccff" align="left" valign="top" width="100">
	         Summary:
	      </th>
	      <td>
		 <textarea name="summary"
      			   cols="60" rows="4"><protect result markup><get
      		 	   name="content"/></protect>
		 </textarea>
	      </td>
	    </tr>
	    <tr> <th> &nbsp; </th>
		 <td> The summary appears in expanded listings underneath the
		      title.  HTML markup is permitted.  Try to keep the
		      summary short. 
		 </td>
	 </then>
	 <else>
            <tr>
	      <th bgcolor="#99ccff" align="left" valign="top" width="100">
	          Summary:
	      </th>
	      <td bgcolor="#ffffff"> <get name="content"/> </td>
	    </tr>
	 </else>
    </if>
	    <tr> <th  bgcolor="#99ccff"> &nbsp; </th> </tr>
    </table>
  </action>
</define>

<define element="title">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <table>		
    <if> <get name="FORM:edit" />
	 <then>
      	    <tr> <th valign="top" align="left" width="100"> Title: </th>
		 <td> <input name="title" value="&content;" size="40"/> </td>
	    </tr>
	    <tr> <th> &nbsp; </th>
		 <td> The title appears in listings as a one-line summary.  No
		      HTML markup is permitted in the title.
		 </td>
	 </then>
	 <else>
            <tr> <th bgcolor="#ffffff"><big><get name="content"/></big> </th>
	    </tr>
	 </else>
    </if>
	    <tr> <th  bgcolor="#99ccff"> &nbsp; </th> </tr>
    </table>
  </action>
</define>

<define element="content">
  <doc> This element delimits the main content of the note.
  </doc>
  <action>
    <table width="95%">		
    <if> <get name="FORM:edit" />
	 <then>
	    <tr>
	      <td> The following field contains the main content of the note.
		   It can be arbitrarily long; HTML markup is permitted. 
	      </td>
      	    <tr> <td>
		 <textarea name="content"
      			   cols="72" rows="20"><protect result markup><get
      		 	   name="content"/></protect>
		 </textarea>
		 </td>
	    </tr>
	 </then>
	 <else> <tr> <td bgcolor="white" ><get name="content"/> </td> </tr>
	 </else>
    </if>
    </table>
  </action>
</define>
<hide><ul></ul></hide>

<define element="note" syntax="quoted">
  <doc> This element delimits the entire note.
  </doc>
  <action>
    <if> <get name="FORM:update" />
	 <then> <hide><get name="content"/></hide><!-- discard old content -->
<set name="content">
  <make name="title"><get name="FORM:title" /></make>

  <make name="summary"><parse><get name="FORM:summary" /></parse></make>

  <make name="content"><parse><get name="FORM:content" /></parse></make>

</set>
		<output dst="&DOC:path;"><make name="note">
  <get name="content"/>
</make>
		</output>
	 </then>
    </if>
    <set name="title"><extract><from><get name="content"/></from>
			       title <content />
       </extract></set>
		
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html><head>
<title>&title;</title>
</head><body bgcolor="99ccff">
<set name="ltitle"><ss>WOAD</ss> note:</set>
<header>&title;</header>
<hr />
<if> <get name="FORM:edit"/>
     <then><!-- === PIA is barfing on POST again... === -->
	<form method="GET" action="&DOC:path;">
	  <table cellpadding="0" border="0">
	    <tr>
	      <th bgcolor="#ffffff">
	        <input type="submit" name="update" value="update"/>
	      </th>
	      <td valign="top"> Click here to update the note with your edits.
	          <br /> Go ``back'' in your browser if you really didn't mean
	          it. 
	      </td>
	    </tr>
          </table>
<hr />
	  <expand><get name="content"/></expand>
	</form>
	<hr />
     </then>
     <else>
	<expand><get name="content"/></expand>
	<hr />
	<table width="100%" border="2" cellpadding="3" cellspacing="0"
	       bgcolor="99ccff">
	  <tr>
	    <td> <a href="&DOC:path;?edit">[edit]</a>
	    </td>
	  </tr>
	</table>
     </else>
</if>
</body></html>
  </action>
</define>

</tagset>

