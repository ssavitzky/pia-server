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

<cvs-id>$Id: woad-web.ts,v 1.13 2000-12-15 19:55:48 steve Exp $</cvs-id>

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
    <table>
    <if> <get name="FORM:edit" />
	 <then>
      	    <tr> 
	      <th bgcolor="#99ccff" align="left" valign="top" width="100">
	         Summary:
	      </th>
	      <td>
		 <textarea name="summary" wrap="wrap"
      			   cols="60" rows="4"><protect result markup><get
      		 	   name="content"/></protect></textarea>
	      </td>
	    </tr>
	    <tr> <th> &nbsp; </th>
		 <td> The summary appears in expanded listings underneath the
		      title.  HTML markup is permitted.  Try to keep the
		      summary short. 
		 </td>
	    </tr>
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

<define element="created">
  <doc> This element delimits a brief summary of the note that appears on the
	parent directory's listing page.
  </doc>
  <action>
    <table>
            <tr>
	      <th bgcolor="#99ccff" align="left" valign="top" width="100">
	          created:
	      </th>
	      <td bgcolor="#ffffff"> <get name="content"/> </td>
	    </tr>
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
      	    <if> &content; <else><let name="content"> </let></else></if>
      	    <tr> <th valign="top" align="left" width="100"> Title: </th>
		 <td> <input name="title" value="&content;" size="40"/> </td>
	    </tr>
	    <tr> <th> &nbsp; </th>
		 <td> The title appears in listings as a one-line summary; it
		      also shows up in the browser's title bar when you view a
		      note as a separate web page.  Because of this, no
		      HTML markup is permitted in the title.
		 </td>
	    </tr>
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
		 <textarea name="content" wrap="wrap"
      			   cols="72" rows="20"><protect result markup><get
      		 	   name="content"/></protect></textarea>
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
  <action><hide>
    <set name="annotates">
	<if> <test match="^&SITE:sourcePrefix;">&LOC:path;</test>
	     <then><subst match="&SITE:sourceSuffix;"
			  result="">&LOC:path;</subst>
	     <then>
	     <else>&LOC:path;</else>
	</if>
    </set>
    <if> <get name="FORM:update" />
	 <then>
	    <doc> <p> Rewrite the note.  It is believed that this is the only
		      place in Woad where a note is rewritten (as opposed to
		      created, which happens in several places).  Therefore
		      this is the right place to create a backup file.
		  </p>
		  <p> Note that we actually have to copy the original file
		      rather than rename it; this gets around a bug in the
		      implementation of SiteDocument (which is where the
		      backup file <em>really</em> needs to be written).
		  </p>
	    </doc>
	    <hide><get name="content"/></hide><!-- discard old content -->
<filter cmd="cp -fp &SITE:file;&DOC:path; &SITE:file;&DOC:path;~" />
<set name="content">
  <make name="title"><get name="FORM:title" /></make>

  <make name="summary"><parse tagset="HTML"><get name="FORM:summary" /></parse></make>

  <make name="content"><parse tagset="HTML"><get name="FORM:content" /></parse></make>

</set>
<output dst="&DOC:path;"><make name="note">
    <get name="content"/>
</make>
</output>
<!-- === AllNotesByTime needs to go under &project; or something eventually -->
<output dst="/AllNotesByTime.wi" append="yes">
<make name="Wfile"><hide>
   <let name="name">&DOC:name;</let>
   <let name="path">&DOC:path;</let>
   <let name="type">note</let>
   <!-- let name="size"><status src="&path;" item="length" /></let -->
   <let name="mtime"><status src="&path;" item="last-modified" /></let>
   <if>&FORM:title;<then><let name="title">&FORM:title;</let></then></if>
   </hide><parse tagset="HTML"><get name="FORM:summary" /></parse>
</make>
</output>
	 </then>
    </if>
    <set name="title"><extract><from><get name="content"/></from>
			       title <content />
       		      </extract>
    </set>
    <if><get name="title"/>
	<else>
	  <set name="title">
		<subst match="\.ww" result="">&DOC:name;</subst>
	  </set>
	</else>
    </if>
</hide><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html><head>
<title>&title;</title>
</head><body bgcolor="99ccff">
<set name="ltitle"><ss>annotation:</ss></set>
<header>&title;</header>
<hr />
<if> <get name="FORM:edit"/>
     <then><!-- === PIA is barfing on POST again... === -->
	<form method="POST" action="&DOC:path;">
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
	    <td> <a href="&DOC:path;?edit">[edit this note]</a>
		 <a href="&annotates;">[go to `&annotates;' (1)]</a>
	    </td>
	    <td align="right">
		 <code>&DOC:path;</code>
	    </td>
	  </tr>
	</table>
	<p> (1) `&annotates;' is the WOAD page for the file, directory, or URL
	    that this note annotates.
	</p>
     </else>
</if>
<hr />
<!-- for some reason the old content appears here after an update. -->
</body></html>
  </action>
</define>

</tagset>

