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

<tagset name="src-html" parent="HTML" tagset="woad-xhtml"
        include="src-wrapper" documentWrapper="-document-" >

<cvs-id>$Id: src-html.ts,v 1.3 2000-08-24 22:55:59 steve Exp $</cvs-id>

<h1>WOAD Source-listing for HTML</h1>

<doc> This tagset is used for listing HTML files.
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
    <else-if>&FORM:direct;<then>direct</then></else-if>
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
		      <xf fmt="direct" href="&DOC:path;?direct">direct</xf>
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

    <if> <get name="FORM:create" />
	 <then>
	   <if> <status item="exists"
    		        src="&LOC:path;&note-tail;&FORM:label;.ww" />
	        <else><!-- create new note -->
	    <output dst="&LOC:path;&note-tail;&FORM:label;.ww"><make name="note">
		<make name="title"><get name="FORM:title" /></make>
		<make name="created">&dateString;</make>
		<make name="summary">
		    <parse tagset="HTML"><get name="FORM:summary" /></parse>
		</make>
		<make name="content">
		    <parse tagset="HTML"><get name="FORM:content" /></parse>
		</make>
</make><!-- note created using quick form -->
</output>
		</else>
	   </if>
	 </then>
    </if>
    <doc> Now locate the files.  Note that we don't need <code>&amp;tail;</code>
	  here because we're browsing around in the source directories, which
	  are all perfectly real (though referred to as ``virtual'').
    </doc>
    <listNoteFiles>&LOC:path;&note-tail;</listNoteFiles>
</hide>

<if> <status item="exists" src="HEADER.ww" />
     <then>
	<set name="haveHeader">yes</set>
	<load-note>HEADER.ww</load-note>
	<hr />
	<displayNoteAsHeader />
     </then>
</if>

<index-bar name="Notes">Notes Views Listing</index-bar>

<form action="&DOC:path;" method="GET"><!-- === PIA isn't passing POST again === -->
  <input type="hidden" name="path" value="&LOC:path;&note-tail;" />
<table bgcolor="white" border="2">
  <!-- First list the indices -->
<if>&indexFiles;<then>
  <tr> <th bgcolor="#cccccc" width="150"> indices </th>
       <th bgcolor="#cccccc" colspan="2" align="left"> description / link to
       help </th> 
  </tr>
</then></if>  
<repeat>
  <foreach entity="f">&indexFiles;</foreach>
  <let name="label"><subst match="\.[^.]*$" result="">&f;</subst></let>
  <tr> <td align="left" valign="top">
		<a href="&f;"><code>&label;</code></a>
       </td>
       <td colspan="2" valign="top">
		 <describeIndex>&f;</describeIndex>
       </td>
  </tr>
</repeat>

  <!-- Next, list the notes -->
  <tr> <th bgcolor="#cccccc" width="150"> source notes </th>
       <th bgcolor="#cccccc" colspan="2" align="left"> title / summary </th>
  </tr>
<repeat>
  <foreach entity="f">&noteFiles;</foreach>
  <let name="label"><subst match="\.[^.]*$" result="">&f;</subst></let>
  <tr> <td align="left" valign="top" bgcolor="yellow">
		<a href="&LOC:path;&note-tail;/&f;"><code>&label;</code></a>
       </td>
       <td colspan="2" valign="top" bgcolor="yellow">
		 <describeNote>&LOC:path;&note-tail;/&f;</describeNote>
       </td>
  </tr>
</repeat>
	

<if>&npath;
  <then>
    <hide>
      <set name="VAR:nfiles">
        <text sort><status item="files" src="&npath;" /></text>
      </set>
      <set name="VAR:nnoteFiles">
        <repeat><foreach entity="f">&nfiles;</foreach>
	   <if><rejectNote>&f;</rejectNote>
	       <else>&f;</else>
	   </if>
        </repeat>
     </set>
    </hide>
  <!-- list the corresponding URL annotations, if any -->
  <if>&nnoteFiles; <then>
    <tr> <th bgcolor="#cccccc" width="150"> page notes </th>
	 <th bgcolor="#cccccc" colspan="2" align="left"> <em>(From the URL
	 annotations for <a href="&npath;">&npath;</a>)</em></th>
    </tr>
  </then></if>
  <repeat>
    <foreach entity="f">&nnoteFiles;</foreach>
    <let name="label"><subst match="\.[^.]*$" result="">&f;</subst></let>
    <tr> <td align="left" valign="top" bgcolor="#99ccff">
		  <a href="&npath;/&f;"><code>&label;</code></a>
	 </td>
	 <td colspan="2" valign="top" bgcolor="#99ccff">
		   <describeNote>&npath;/&f;</describeNote>
	 </td>
    </tr>
  </repeat>
  </then>
</if>

  <!-- Finally, the form for creating a new note (the easy way) -->
  <tr> <th bgcolor="#cccccc" width="150"> new note </th>
       <th bgcolor="#cccccc" colspan="2" align="left">
    	    Enter note text or use &nbsp;&nbsp;&nbsp;
    	    <a href="/.Woad/Tools/new-note?path=&xloc;">[advanced form]</a> 
       </th>
  </tr>
  <tr> <td valign="top">
    	  <select name="label"> 
	       	   <option selected="selected"><uniquify>note</uniquify></option>
	       	   <option><uniquify>bug</uniquify></option>
	       	   <option><uniquify>wish</uniquify></option>
	       	   <option><uniquify>see-also</uniquify></option>
	       	   <option>&date;-&hour;&minute;</option>
	       	   <option>&date;</option>
	       	   <if>&haveHeader;<else><option>HEADER</option></else></if>
	  </select><br />
 	  <input name="create" value="Create Note" type="submit" />
       </td>
       <td valign="top" colspan="2">
	  <textarea name="summary" cols="60" rows="4" wrap="wrap"></textarea>
       </td>
  </tr>
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
  the file as ascii text with no alteration.</em>
</yellow-note>

<hr />
<pre><include src="&DOC:path;" quoted="true" tagset="" /></pre>
  </then></else-if>

<else-if> &FORM:direct;	<!-- ====== direct =============================== -->
  <then>
<yellow-note><em> This <tt>&VAR:format;</tt> listing passes the 
	contents of the file to the browser as HTML.  If it contains active
	content the results will be somewhat bizarre.</em>
</yellow-note>

<hr />
<include src="&DOC:path;" quoted="true" tagset="HTML" />
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

<h3>Structural elements</h3>

<define entity="sc"><value>#ff9933</value></define>

<define element="head" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="head" tc="&sc;"><expand><get name="content" /><hide>
    </hide></expand></elt-b></action>
</define>

<define element="title" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="title"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt><wrap /></action>
</define>

<define element="link" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="link" tc="&lc;" /></action>
</define>
<define element="meta" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="meta" /></action>
</define>
<define element="base" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="link" tc="&lc;" /></action>
</define>

<define element="body" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="body" tc="&sc;"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>

<define element="frameset" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="frameset" tc="&sc;"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="frame" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="frame" /></action>
</define>
<define element="noframes" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="noframes" tc="&sc;"
           ><expand><get name="content" /></expand></elt-b></action>
</define>

<h3>Headings</h3>

<define element="h1" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h1" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h2" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h2" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h3" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h3" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h4" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h4" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h5" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h5" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h6" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h6" tc="&sc;"><b><expand><get name="content" /></expand></b><hide>
    </hide></elt></wrap></action>
</define>

<h3>Lists</h3>

<define element="ul" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="ul"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="ol" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="ol"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="dl" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="dl"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="menu" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="menu"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="dir" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="dir"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>

<define element="toc" syntax="quoted">
  <doc> This tag is not official HTML, but is used in the PIA to delimit the
	table of contents.
  </doc>
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="toc"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>

<define element="li" syntax="quoted" implicitly-ends="li">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="li"><expand><get name="content" /></expand></elt><wrap /></action>
</define>
<define element="dt" syntax="quoted" implicitly-ends="dt dd">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dt"><expand><get name="content" /></expand></elt><wrap /></action>
</define>
<define element="dd" syntax="quoted" implicitly-ends="dt dd">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dd"><expand><get name="content" /></expand></elt><wrap /></action>
</define>

<h3>Tables</h3>

<define element="table" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="table"><expand><get name="content" /></expand></elt-b></action>
</define>
<define element="tr" syntax="quoted" implicitly-ends="tr">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tr"><expand><get name="content" /></expand></elt-b></action>
</define>
<define element="td" syntax="quoted" implicitly-ends="th td">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="td"><expand><get name="content" /></expand></elt></action>
</define>
<define element="th" syntax="quoted" implicitly-ends="th td">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="th"><expand><get name="content" /></expand></elt></action>
</define>
<define element="thead" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="thead"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="tfoot" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tfoot"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="tbody" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tbody"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="caption" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="caption"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>


<h3>Paragraph-level Elements</h3>

<define element="p" syntax="quoted" implicitly-ends="p">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="p"><expand><get name="content" /></expand><hide>
    </hide></elt><wrap /></action>
</define>

<define element="div" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="div"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>

<define element="blockquote" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="blockquote"><expand><get name="content" /></expand></elt-b></action>
</define>

<define element="center" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="center"><expand><get name="content" /></expand><wrap /></elt></action>
</define>

<define element="br" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="br" /><if>&wrap;<then><br /></then></if></action>
</define>
<define element="hr" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="hr" /><if>&wrap;<then><br /></then></if></action>
</define>

<h3>Flow Elements</h3>

<define element="b" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="b"><b><expand><get name="content" /></expand></b></elt></action>
</define>
<define element="i" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="i"><i><expand><get name="content" /></expand></i></elt></action>
</define>
<define element="u" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="u"><u><expand><get name="content" /></expand></u></elt></action>
</define>
<define element="tt" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="tt"><tt><expand><get name="content" /></expand></tt></elt></action>
</define>
<define element="em" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="em"><em><expand><get name="content" /></expand></em></elt></action>
</define>
<define element="strong" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="strong"><strong><expand><get name="content" /></expand></strong><hide>
    </hide></elt></action>
</define>

<define element="q" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="q"><em><expand><get name="content" /></expand></em></elt></action>
</define>
<define element="cite" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="cite"><em><expand><get name="content" /></expand></em></elt></action>
</define>
<define element="address" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="address"><em><expand><get name="content" /></expand></em><hide>
    </hide></elt></action>
</define>
<define element="code" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="code"><code><expand><get name="content" /></expand></code><hide>
    </hide></elt></action>
</define>

<define element="pre" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="pre"><tt><expand><get name="content" /></expand></tt><hide>
    </hide></elt></wrap></action>
</define>

<define element="font" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="font"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="big" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="big"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="small" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="small"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="sub" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sub"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="sup" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sup"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="var" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="var"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="dfn" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dfn"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="samp" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="samp"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="kbd" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="kbd"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="ins" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="ins"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="del" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="del"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="acronym" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="acronym"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>


<h3>Links</h3>

<define entity="lc"><value>#cc00cc</value></define><!-- link color -->

<define element="a" syntax="quoted">
  <doc> If <code>href</code> attributes are doubled, it means you're inside a
	tag that should have <code>syntax="quoted"</code> but doesn't.  Note
	the special handling of the case where both <code>name</code> and
	<code>href</code> attributes are present.
  </doc>
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="a" tc="&lc;"><hide>
      </hide><if>&attributes:href;
	<then><if>&attributes:name;
		  <then><a name="&attributes:name;" href="&attributes:href;"
			><expand><get name="content" /></expand></a></then>
		  <else><a href="&attributes:href;"
			><expand><get name="content" /></expand></a></else>
	      </if></then>
	<else><a name="&attributes:name;"><expand><get name="content" /></expand></a></else>
    </if><hide>
    </hide></elt></action>
</define>

<define element="img" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="img" tc="&lc;" /></action>
</define>

<h3>Forms</h3>

<define element="form" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
	        <let name="econtent"><expand><get name="content" /></expand></let>
    </hide><elt-b tag="form" tc="#cc00cc">&econtent;</elt-b></action>
</define>

<define element="input" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="input" /></action>
</define>

<define element="select" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="select"><expand><get name="content" /></expand><hide>
    </hide></elt-b></action>
</define>
<define element="option" syntax="quoted" implicitly-ends="option">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="option"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="textarea" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="textarea"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="button" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="button"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="label" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="label"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="fieldset" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="fieldset"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="legend" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="&tagname;"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>
<define element="script" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="&tagname;"><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="&tagname;" tc="#666666"><hide>
    </hide><expand><get name="content" /></expand><hide>
    </hide></elt></action>
</define>

<define element="#comment" syntax="quoted">
  <action><font color="red">&lt;!--&value;--&gt;</font><hide>
    </hide></action>
</define>

<define element="#pi" syntax="quoted">
  <action><font color="red">&lt;?<get name="name"/>&value;?&gt;</font><hide>
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

