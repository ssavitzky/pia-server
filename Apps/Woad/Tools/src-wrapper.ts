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

<tagset name="src-wrapper" tagset="woad-xhtml" >

<cvs-id>$Id: src-wrapper.ts,v 1.3 2000-07-28 22:30:08 steve Exp $</cvs-id>

<h1>WOAD source-file document wrapper</h1>

<doc> This tagset contains nothing but the <code>-document-</code> tag, used
      for wrapping re-tagged source documents.
</doc>

<h2>Document Wrapper</h2>

<define element="-document-" quoted="yes" >
  <doc> This is the outside wrapper.  Note that the tagname must be lowercase
	because some tagsets (e.g. HTML) are case-insensitive, so every tag
	defined here may get case-smashed.
  </doc>
  <action>
<html><hide>
  <if>&FORM:code;<then><set name="FORM:tsdoc">tsdoc</set></then></if>
  <set name="VAR:format"><if> &FORM:nested; <then>nested</then>
    <else-if>&FORM:raw;<then>raw</then></else-if>
    <else-if>&FORM:retag;<then>retagged</then></else-if>
    <else-if>&FORM:wrap;<then>wrapped</then></else-if>
    <else-if>&FORM:tsdoc;<then>tsdoc</then></else-if>
    <else>processed</else>
  </if></set>
  <set name="VAR:wrap">&FORM:wrap;</set>
  <set name="tpath"><mapSrcToTarget>&DOC:path;</mapSrcToTarget></set>
  <set name="npath"><mapSourceToNote>&DOC:path;</mapSourceToNote></set>
  <set name="spath">
    <if>&SITE:sourcePrefix;
	<then><subst match="^&SITE:sourcePrefix;"
	             result="">&DOC:path;</subst></then>
	<else>&DOC:path;</else>
    </if></set>
  <set name="slpath">
    <if>&SITE:sourcePrefix;
	<then><subst match="^&SITE:sourcePrefix;"
	             result="">&LOC:path;</subst></then>
	<else>&LOC:path;</else>
    </if></set>
  <set name="sroot">
    <if>&SITE:sourcePrefix;
	<then>&SITE:sourcePrefix;</then>
	<else></else>
    </if></set>
  <!-- === don't decorate the final component, which is the file being listed. -->
  <set name="decoratedPath">
	<decoratePath base="&sroot;">[SRC]&spath;</decoratePath></set>

  <set name="note-tail">
	&SITE:sourceSuffix;/&DOC:name;/
  </set>
  </hide>

  <head><title>&spath; -- WOAD listing</title>
  </head>
  <body bgcolor="#ffffff">

    <table bgcolor="#99ccff" cellspacing="0" border="0" width="100%">
      <tr> <td>
	     <table><!-- the nested table shrink-wraps its contents     -->
	            <!-- so it doesn't get spread out by the width=100% -->
	       <tr><th align="left">
		      <big> &nbsp;<ss><a href="/.Woad/">WOAD</a></ss></big>
		   </th>
		   <th align=left>
		      <big> <i>source listing:</i>
			    <code>&decoratedPath;</code>
		      </big>
		   </th>
	       </tr>
	       <tr>
		 <td align="right">-&gt;&nbsp;</td>
		 <td>
		      <xf fmt="processed" href="&DOC:path;">processed</xf>
		      <xf fmt="wrapped" href="&DOC:path;?wrap">wrapped</xf>
		      <xf fmt="nested" href="&DOC:path;?nested">nested</xf>
		      <xf fmt="raw" href="&DOC:path;?raw">raw</xf>
		      <if><test match=".ts$">&DOC:name;</test>
		          <then>
		      	    <xf fmt="tsdoc"href="&DOC:path;?tsdoc">tsdoc</xf>
		          </then>
		      </if>
		      <if><get name="tpath" />
		          <then>
		      	    <a href="&tpath;">&lt;server&gt;</a>
		      	    <a href="&tpath;" target="server">&lt;!&gt;</a>
		          </then>
		      </if>
		      <if><get name="npath" />
		          <then>
		      	    <a href="&npath;">[URL annotations]</a>
		          </then>
		      </if>
		      <if> &FORM:entities;<!-- this is wrong now. -->
			   <then> <a href="#entities">[info]</a>
			   </then>
			   <else> <a href="&docPath;?entities#entities">
				  <b>[info]</b></a>
			   </else>
		      </if>
		      <a href="/.Woad/help.xh#views">[help]</a>
		 </td>
		</tr>
	     </table>
	   </td>
	   <td>&nbsp;</td>
      </tr>
    </table>
    

<hide>
    <!-- Create a new note:  have to do this before listing the directory -->
    <handleNoteCreation>&LOC:path;&note-tail;</handleNoteCreation>
    <!-- List the note files -->
    <listNoteFiles>&LOC:path;&note-tail;</listNoteFiles>
</hide>

<!-- at this point, include content of HEADER.ww if there is one. -->
<displayHeaderIfPresent>&notePath;HEADER.ww</displayHeaderIfPresent>

<index-bar name="Notes">Notes Views Listing</index-bar>

<form action="&DOC:path;" method="GET"><!-- === PIA isn't passing POST again === -->
  <input type="hidden" name="path" value="&LOC:path;&note-tail;" />
<table bgcolor="white" border="2">
  <!-- First list the indices, if any (they're unlikely, and optional) -->
  <if>&indexFiles;
      <then><indexTableRows>&indexFiles;</indexTableRows></then>
  </if>

  <!-- Next, list the notes (including page notes)-->
  <noteTableRows heading="source notes">&noteFiles;</noteTableRows>
  <pageNoteRows>&npath;</pageNoteRows>

  <!-- Finally, the form for creating a new note (the easy way) -->
  <noteCreationForm> 
    <option selected="selected"><uniquify>note</uniquify></option>
    <option><uniquify>bug</uniquify></option>
    <option><uniquify>wish</uniquify></option>
    <option><uniquify>see-also</uniquify></option>
    <option><uniquifyIfNeeded>&date;</uniquifyIfNeeded></option>
    <option>&date;-&hour;&minute;</option>
    <if>&haveHeader;<else><option>HEADER</option></else></if>
  </noteCreationForm> 
</table>
</form>

<!-- ==================================================================== -->
<!-- === actually this wants to be a separate section, on the index bar. -->
<tool-bar />

<!-- ==================================================================== -->
<index-bar name="Views">Notes Views Listing</index-bar>

<if> &tpath;
     <then>
	<p> This page is a <ss>WOAD</ss> listing of the application source
	    document <code>&spath;</code>
	    <br />
    	    It corresponds to the application URL <code>&tpath;</code>
	</p>
	<p> There are two options for viewing the associated application
	    document: 
	</p>
	<table bgcolor="white" width="90%" cellpadding="5" border="2"
	       align="center" >
	  <tr>
	    <td valign="top"> 
		 <a href="&npath;">View&nbsp;[URL&nbsp;annotations].</a>
	    </td>
	    <td> This will show you the <ss>WOAD</ss> annotations associated
		 with this URL.  It will also show you a listing, similar to
		 this one, of the page <em>as it came from the server.</em>
		 This can be useful for debugging an active document.
	    </td>
	  </tr>
	  <tr>
	    <td valign="top" align="right"> 
		 <a href="&tpath;">View page&nbsp;on&nbsp;&lt;server&gt;.</a>
	         <br />
	    	 <a href="&tpath;"
    	    	    target="server">...&nbsp;in&nbsp;&lt;!new&nbsp;window&gt;</a>
	    </td>
	    <td> This is a link to this page <em>on the server</em> -- in
		 other words, it's what a user will see if they browse to the
		 url that corresponds to this document.  
	    </td>
	  </tr>
	</table>	  
     </then>
     <else>
	<p> This page is a <ss>WOAD</ss> source listing of
	    <code>&spath;</code>. 
	</p>
	<yellow-note>
	  There is no URL on the server that corresponds to this file. 
	</yellow-note>
     </else>
</if>
<p> 
</p>


<index-bar name="Listing">Notes Views Listing</index-bar>
<hr />

<!-- ===================================================================== -->
<if> &FORM:nested;	<!-- ====== nested =============================== -->
  <then>
<yellow-note><em>
      This <tt>&VAR:format;</tt> listing is color-coded and indented to show
      the nesting level of the tags.  
       <br />
      <red>Note that because of current limitations omitted end tags are
      shown, and linebreaks inside of tags are eliminated.  Omitted end tags
      may be shown in the wrong place.</red> </em>
</yellow-note>
<hr />
<pretty>&content;</pretty>
  </then>
<else-if> &FORM:raw;	<!-- ====== raw ================================== -->
  <then>
<yellow-note><em> This <tt>&VAR:format;</tt> listing shows the full contents of
  the file with no alteration.</em>
</yellow-note>

<hr />
<pre><include src="&DOC:path;" quoted="true" tagset="" /></pre>
  </then></else-if>
<else-if> &FORM:tsdoc;	<!-- ====== tagset =============================== -->
  <then>
<yellow-note><em> This <tt>&VAR:format;</tt> listing shows tagset
       documentation. </em>
</yellow-note>

<hr />
<include src="&DOC:path;" tagset="tsdoc" />
  </then></else-if>
<else>			<!-- ====== processed ============================ -->
<yellow-note><em>
      This <tt>&VAR:format;</tt> listing is color-coded and font-coded
      according to syntax.  The <tt><a href="&DOC:path;?wrap">wrapped</a></tt>
      format tends to be more compact than the normal <tt><a
      href="&DOC:path;">processed</a></tt> format, and is more readable in
      some cases.<br />

      <red>Because of current limitations, omitted end tags are shown and
      linebreaks inside of tags are removed.  Missing end tags may be shown in
      the wrong place.</red> </em>
</yellow-note>

<hr />
<!-- not clear whether we always want to make the listing <small>. -->
<if>&wrap;
    <then><expand>&content;</expand></then>
    <else><small><pre><expand>&content;</expand></pre></small></else>
</if>
  </else>
</if>

<hr /><!-- ====== End of Listing ===================================== -->
<if> &FORM:entities;
  <then>
<hr />
<table bgcolor="#99ccff" cellspacing="0" border="0" width="100%">
  <tr> <th align="left" colspan="2">
        <h3><a name="entities">WOAD server info</a></h3>
       </th>
  </tr>
  <tr> <td width="50">&nbsp;</td>
       <td>
            <p> This is a complete dump of the information the WOAD server is
		keeping track of for this source listing and its parent
		directories.  It is presently most useful for debugging WOAD.
            </p>
       </td>
  </tr>
  <tr> <td colspan="2">&nbsp;</td></tr>
</table>

<ul>
<repeat><foreach><get name="VAR:"/></foreach>
  <if><get name="FORM:expanded">
      <then>
      <li> &li; = <pretty hide-below-tag="action" yellow-tag="Properties">
	       <protect result markup><get name="VAR:&li;"/>
	   </protect></pretty></li>
      </then>
      <else>
      <li> &li; = <protect result markup><get name="VAR:&li;"/>
	   </protect></li>
      </else>
  </if>
</repeat>
</ul>

  </then>
</if>

  </body>
</html>
  </action>
</define><!-- ====== end of wrapper =================================== -->

</tagset>

