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

<tagset name="woad-xhtml" parent="xhtml" include="pia-tags" recursive="yes">
<cvs-id>$Id: woad-xhtml.ts,v 1.31 2000-12-06 02:11:35 steve Exp $</cvs-id>

<h1>WOAD XHTML Tagset</h1>

<doc> This the version of the XXML tagset used by default in WOAD.  Active
      tags unique to the PIA are actually defined in <code>pia-tags</code>,
      and are shared between <code>pia-xxml</code> and <code>pia-xhtml</code>.
</doc>

<note author="steve">
<p> Two further bits of refactoring are possible at this point:
</p>
<ul>
  <li> moving the pretty-printing tags (e.g. <tag>elt-b</tag>) into a
       separate tagset, e.g. <code>Tools/listing.ts</code>
  </li>
  <li> moving the listing-page-specific tags (e.g. <tag>rejectNote</tag>)
       into, e.g., <code>Tools/tools-xhtml.ts</code>. 
  </li>
</ul>  
</note>

<h2>Utilities</h2>
<define element="filter" handler="filterHandler" />

<h2>Form-Processing Tags</h2>

Note that we only need these inside the PIA.

<h3>Submit-Forms</h3>
<define element="submit-forms" handler="org.risource.pia.handle.submitHandler" >
  <doc> Submit each form in the content to its target.  Results, if any,
	are discarded.
  </doc>
</define>

<h3>Form and its components</h3>

<h4>Sub-elements of <tag>form</tag></h4>
<ul>
  <li> <define element="process" parent="form" syntax="quoted" handler>
         <doc> The content of a <tag>process</tag> element is expanded only
	       when the <tag>form</tag> that contains it is being processed as
	       a result of a <code>post</code> or <code>query</code>
	       submission. 
         </doc>
       </define>
  </li>
</ul>
<h2>Page Components</h2>

<h3>Graphics and pseudo-graphics</h3>

<define element="ss">
  <action><font face="Verdana, Arial, Helvetica, sans-serif"
	   >&content;</font></action>
</define>

<define element="red">
  <action><font color="#cc0000">&content;</font>
  </action>
</define>

<define entity="blank-170x1">
  <value><img src="&LOC:path;/Icon/white170x1.png" width="170" height="1" alt=" "></value>
</define>
<define entity="blue-dot">
  <value><img src="&LOC:path;/Icon/dot-blue.png"
		height="20" width="20" alt="*"></value>
</define>

<define entity="RiSource.org">
  <value><a href="http://www.RiSource.org/"
            ><font color="#c40026" 
                   face="Verdana, Arial, Helvetica, sans-serif"
                   >Ri</font><font color="black"
	                           face="Verdana, Arial, Helvetica, sans-serif"
                   ><i>Source.org</i></font></a></value>
</define>

<define entity="RiSource.org.pia">
  <value><a href="http://www.RiSource.org/PIA"
            ><font color="#c40026" 
                   face="Verdana, Arial, Helvetica, sans-serif"
                   ><i>PIA</i></font></a></value>
</define>

<define entity="WOADroot">
  <value>&LOC:path;</value>
</define>

<define entity="WOAD-Logo">
  <value><a href="/.Woad/"
            ><font color="blue" face="Verdana, Arial, Helvetica, sans-serif"
                   >Woad</font></a></value>
</define>

<define entity="WOAD-LOGO">
  <value><a href="/.Woad/"
            ><font color="blue" face="Verdana, Arial, Helvetica, sans-serif"
                   >WOAD</font></a></value>
</define>

<h2>Path and Identifier Mapping</h2>

<define element="removePrefix">
  <doc> Remove a prefix from the content.
  </doc>
  <define attribute="prefix">
    <doc> The prefix to be removed.
    </doc>
  </define>
  <action><subst match="^&attributes:prefix;" result="">&content;</subst><hide>
  </hide></action>
</define>

<define element="removePathPrefix">
  <doc> Remove a prefix from a path.  If the result would be null,
	return a single slash.
  </doc>
  <define attribute="prefix">
    <doc> The prefix to be removed.
    </doc>
  </define>
  <action><if>  <test case="case" match="^&sprefix;$">&path;</test>
		<then>/</then>
		<else><subst match="^&sprefix;" 
			     result="">&path;</subst></else>
  </if></action>
</define>

<define element="replacePrefix">
  <doc> The content is a path.  If it starts with <code>old</code>, replace
	<code>old</code> with <code>new</code>.  Otherwise, return null.
  </doc>
  <note> === check explicitly for exact match to work around a subst bug.
  </note>
  <define attribute="old">
    <doc> The old prefix to be removed.
    </doc>
  </define>
  <define attribute="new">
    <doc> The new prefix to be prepended.
    </doc>
  </define>
  <action><if>  <test case="yes" match="^&attributes:old;$">&content;</test>
		<then>&attributes:new;</then>
	  <else-if>
		<test case="yes" match="^&attributes:old;">&content;</test>
		<then><subst match="^&attributes:old;"
			     result="&attributes:new;">&content;</subst></then>
	  </else-if>
  </if></action>
</define>

<define element="forceTrailingSlash">
  <doc> Force the content, a path, to end in a slash so that it can be
	prefixed to a filename.  An empty path is not affected.
  </doc>
  <action><if>&content;
	      <then><if><test match="/$">&content;</test>
		        <then>&content;</then>
		        <else>&content;/</else>
  	      </if></then>
  </if></action>
</define>

<define element="mapSourceToNote">
  <doc> Map the annotation path in <code>&amp;content;</code> to the
	corresponding <em>source</em> path.  The content needs to be a
	full path, typically the one returned by <code>&amp;DOC:path;</code>.

	<p> The following global variables are used:
        </p>
	<table>
	  <tr> <td> <code>SITE:targetServer</code></td>
	       <td> <code><em>host</em>:<em>port</em></code> for the target
		    server.  
	       </td>
	  </tr>
	  <tr> <td> <code>SITE:docOffset</code></td>
	       <td> The path from the root of the Woad source tree to the
		    target server's <code>DocumentRoot</code>.  Should start
		    with a slash, but not end with one.
	       </td>
	  </tr>
	  <tr> <td> <code>SITE:sourcePrefix</code></td>
	       <td> The path from the root of the Woad <em>server</em> to the
		    root of the source listings.  Normally
		    ``<code>.source</code>''.
	       </td>
	  </tr>
	  <tr> <td> <code>SITE:aliases</code></td>
	       <td> A space-separated list of aliased top-level directories.
		    Each should start with a slash, but not end with one.
	       </td>
	  </tr>
	  <tr> <td> <code>SITE:offsets</code></td>
	       <td> A space-separated list of offsets corresponding to the
		    aliased directories in <code>SITE:aliases</code>. 
		    Each should start with a slash, but not end with one.
	       </td>
	  </tr>
	</table>

  </doc>
  <action><hide>
	<let name="nprefix"><get name="SITE:notesPrefix"/></let>
	<let name="sprefix"><get name="SITE:sourcePrefix"/></let>
	<let name="offset"><get name="SITE:docOffset"/></let>
	<let name="path"><get name="content"/></let>
	<if>&sprefix;
	    <then>
	      <let name="path">
		 <removePathPrefix prefix="&sprefix;">&path;</removePathPrefix>
	      </let>
	    </then>
	</if>
	<doc> The path is now cleaned up -- it starts with the source root.
	      The next task is to find a suitable offset.  Iterate in parallel
	      through SITE:offsets and SITE:aliases.  If an offset matches,
	      remove it and replace it with the corresponding aliased prefix.
	      Otherwise, check for SITE:docOffset, which is aliased to /.
	</doc>
	<let name="npath"></let>
	<repeat><foreach entity="o"><get name="SITE:offsets" /></foreach>
		<foreach entity="n"><get name="SITE:aliases" /></foreach>
		<set name="npath">
		  <replacePrefix old="&o;" new="&n;">&path;</replacePrefix>
		</set>
		<until><get name="npath"/></until>
	</repeat>
	<if>&npath;<then><let name="path">&npath;</let></then>
	<else-if>&offset;
	    <then><let name="path">
		    <replacePrefix old="&offset;" new="/">&path;</replacePrefix>
		  </let>
	    </then>
	</else-if>
	</if>
     </hide><text op="trim">
	<if>&path; <then>&nprefix;&path;</then></if>
  </text></action>
</define>

<define element="mapNoteToTarget">
  <doc> Map the annotation path in <code>&amp;content;</code> to the
	corresponding URL on the target server.  The content needs to be a
	full path, typically the one returned by <code>&amp;DOC:path;</code>.

	<p> The following global variables are used:
        </p>
	<table>
	  <tr> <td> <code>SITE:targetServer</code></td>
	       <td> <code><em>host</em>:<em>port</em></code> for the target
		    server. 
	       </td>
	  </tr>
	  <tr> <td> <code>SITE:notesPrefix</code></td>
	       <td> The path from the root of the Woad <em>server</em> to the
		    root of the annotations.  Normally null.
	       </td>
	  </tr>
	</table>

  </doc>
  <action><hide>
	<let name="server"><get name="SITE:targetServer"/></let>
	<let name="prefix"><get name="SITE:notesPrefix"/></let>
	<let name="path"><get name="content"/></let>
	<if>&prefix;
	    <then>
	      <let name="path">
		 <removePathPrefix prefix="&prefix;">&path;</removePathPrefix>
	      </let>
	    </then>
	</if>
     </hide><if><get name="server" />
	        <then>http://&server;&path;</then>
	        <else>&path;</else>
  </if></action>
</define>

<define element="mapNoteToSource">
  <doc> Map the annotation path in <code>&amp;content;</code> to the
	corresponding <em>source</em> path.  The content needs to be a
	full path, typically the one returned by <code>&amp;DOC:path;</code>.
  </doc>
  <action><hide>
	<let name="nprefix"><get name="SITE:notesPrefix"/></let>
	<let name="sprefix"><get name="SITE:sourcePrefix"/></let>
	<if> <get name="sprefix"/>
	     <then><if> <test match="^\/">&sprefix;</test>
			<else><let name="sprefix">/&sprefix;</let></else>
	     </if></then>
	</if>
	<let name="offset"><get name="SITE:docOffset"/></let>
	<let name="path">
	     <subst match="//" result="/"><get name="content"/></subst>
	</let>
	<if>&nprefix;
	    <then>
	      <let name="path">
		 <removePathPrefix prefix="&nprefix;">&path;</removePathPrefix>
	      </let>
	    </then>
	</if>
	<let name="npath"></let>
	<repeat><foreach entity="n"><get name="SITE:offsets" /></foreach>
		<foreach entity="o"><get name="SITE:aliases" /></foreach>
		<set name="npath">
		  <replacePrefix old="&o;" new="&n;">&path;</replacePrefix>
		</set>
		<until><get name="npath"/></until>
	</repeat>
	<let name="path">
	    <if>&npath;
		<then>&npath;</then>
		<else>&offset;&path;</else>
	    </if>
	</let>
     </hide><text op="trim">
	&sprefix;&path;
  </text></action>
</define>


<define element="mapSrcToTarget">
  <doc> Map the source file path in <code>&amp;content;</code> to the
	corresponding URL on the target server.  The content needs to be a
	full path, typically the one returned by <code>&amp;DOC:path;</code>.
	This operation simply combines <tag>mapNoteToTarget</tag> with
	<tag>mapSourceToNote</tag>, so as not to require duplication of code.
  </doc>
  <action><mapNoteToTarget><mapSourceToNote><get name="content"
          /></mapSourceToNote></mapNoteToTarget></action> 
</define>
	
<hide><dl></dl></hide>

<define element="decoratePath">
  <doc> Turn a path into a sequence of links to its components.
	=== later may want to add trailing "/" where appropriate, and not
	    link the final path component. ===
  </doc>
  <define attribute="base">
    <doc> The base path for the document.
    </doc>
  </define>
  <action><hide>
    <let name="split">
	<subst match="/" result=" "><get name="content"/></subst>
    </let>
    <let name="xpath"><get name="attributes:base" /></let>
    <if>&xpath;<else><let name="xpath">/</let></else></if>
    <let name="sep"></let></hide>
    <repeat><foreach><text op="split">&split;</text></foreach><text op="trim">
	<if><get name="sep" />
	    <then><hide> <let name="xpath"><get name="xpath"/>/&li;</let>
		         <let name="xpath"><subst match="//"
		              result="/">&xpath;</subst></let>
		  </hide><get name="sep" /><a href="&xpath;">&li;</a>
	    </then>
	    <else><a href="&xpath;">&li;</a>
		  <let name="sep">/</let>
	    </else>
        </if>
    </text></repeat>
  </action>
</define>

<define element="fixRelativePath">
  <doc> Turn a relative path into an absolute path starting with
	``<code>/</code>''.  Return null for a non-local URL.
  </doc>
  <define attribute="path">
    <doc> The base path for the document.  If omitted, LOC:path is used.
    </doc>
  </define>
  <action><if>
	<test match="^[a-zA-Z]+[:]">&content;</test>
	<then></then>
      <else-if><test match="^\.[/]?$">&content;</test>
	<then><get name="attributes:path"><get name="LOC:path" /></get>/</then>
      </else-if>
      <else-if><test match="^[/]">&content;</test>
	<then>&content;</then></else-if>
	<else><subst match="[/]+" result="/"><hide>
	  </hide><get name="attributes:path"><get name="LOC:path" /></get><hide>
	  </hide>/&content;</subst></else>
  </if></action>
</define>

<ul></ul><!-- reset === indentation column === -->

<h2>Listing Components</h2>

<define entity="tc">
  <doc> Default tag color -- same as emacs, currently. </doc>
  <value>#009900</value>
</define>

<define element="tag">
  <action><font color="&tc-;">&lt;&content;&gt;</font></action>
</define>

<define element="etag">
  <action><font color="&tc-;">&lt;/&content;&gt;</font></action>
</define>

<define element="atr">
  <action><hide>
      <let name="n"><extract><from>&content;</from><name /></extract></let>
      <let name="v"><extract><from>&content;</from><eval /></extract></let>
      <if><test exact match="src">&n;</test>
	  <test exact match="href">&n;</test>
	  <test exact match="action">&n;</test>
	  <then><let name="v"><a href="&v;">&v;</a></let></then>
      </if>
    </hide>&n;="<i>&v;</i>"</action>
</define>

<define element="atl">
  <action><if>&content;
	      <then><repeat><foreach>&content;</foreach> <atr>&li;</atr></repeat></then>
  </if></action>
</define>

<define element="elt">
  <doc> Display an element in source-listing format.  Handles null content but
	=== presently fails to distinguish empty content from whitespace ===.
	Note the contortions required to prevent spaces from fouling up a
	preformatted listing.
  </doc>
  <action><hide>
    <if>&attributes:tc;
        <then><let name="tc-">&attributes:tc;</let></then>
	<else><let name="tc-">&tc;</let></else>
    </if>
    </hide><tag>&attributes:tag;<atl><get name="atts"/></atl><if>&content;
           <else> /</else></if></tag><hide>
    </hide><if>&content;<then>&content;</then></if><hide>
    <if>&attributes:tc;
        <then><let name="tc-">&attributes:tc;</let></then>
	<else><let name="tc-">&tc;</let></else>
    </if>
    </hide><if>&content;<then><etag>&attributes:tag;</etag></then></if><hide>
  </hide></action>
</define>
<define element="elt-b">
  <doc> Display an element in source-listing format, making the tags bold.
	Unlike <tag>elt</tag>, this does not handle empty tags -- we use
	boldface to indicate tags with defined element content.  If wrapping
	is turned on, the start and end tags are each preceeded by a line
	break.  One should really keep track of nesting level.
  </doc>
  <action><if>&wrap; <then><br /></then></if><hide>
    <if>&attributes:tc;
        <then><let name="tc-">&attributes:tc;</let></then>
	<else><let name="tc-">&tc;</let></else>
    </if>
    </hide><b><tag>&attributes:tag;<atl><get name="atts"/></atl></tag></b><hide>
    </hide><if>&content;
		<then><if>&wrap;
		          <then><table border=0 cellspacing=0 cellpadding=0>
			           <tr><td>&nbsp;&nbsp;&nbsp;</td>
				        <td>&content;</td></tr></table></then>
		          <else>&content;</else>
		</if></then></if><hide>
    <if>&attributes:tc;
        <then><let name="tc-">&attributes:tc;</let></then>
	<else><let name="tc-">&tc;</let></else>
    </if>
    </hide><b><etag>&attributes:tag;</etag></b><hide>
  </hide><if>&wrap; <then><br /></then></if><hide>
  </hide></action>
</define>

<define element="wrap">
  <doc> Insert a line break if wrapping is turned on.  If content is present,
	wrap the content in a table so as to force linebreaks around it.
  </doc>
  <action><if>&wrap;
	<then><if>&content;
		  <then><table border=0 cellspacing=0 cellpadding=0>
			     <tr><td>&content;</td></tr></table></then>
		  <else><br /></else>
	      </if></then>
	<else><if>&content;<then>&content;</then></if></else>
  </if></action>
</define>

<h2>Note-handling components</h2>

<define element="rejectNote">
  <doc> decide whether to omit a file from the notes listing
  </doc>
  <action>
    <test not="not" match="\.ww$">&content;</test>
  </action>
</define>

<doc> originally we did multiple includes for each note: gross.
      Now we suck in the whole note (it's short) and put it in a global.
     === do NOT do this with index files -- list them separately.
</doc>

<define element="load-note">
  <action>
    <set name="VAR:note">
	<include quoted tagset="/.Woad/woad-web.ts" src="&content;" />
    </set>
  </action>
</define>

<define element="note-title" syntax="empty">
  <action>
    <extract><from><get name="VAR:note" /></from>
	     <name all="all">title</name> <content /></extract>
  </action>
</define>

<define element="note-content" syntax="empty">
  <action>
    <extract><from><get name="VAR:note" /></from>
	     <name all="all">content</name> <content /></extract>
  </action>
</define>

<define element="note-summary" syntax="empty">
  <action>
    <extract><from><get name="VAR:note" /></from>
	     <name all="all">summary</name> <content /></extract>
  </action>
</define>

<define element="describeIndex">
  <doc> An index file's listing entry. The content is dderived from the
	filename of the index.  Note that <strong>we do not read the
	index!</strong> Index files can get big, and we don't want to slow
	things down.  Fortunately, and unlike note files, the set of index
	files is pretty-much under WOAD's control, so we just link to the
	appropriate documentation in the help file. 
  </doc>
  <action><text op="trim">
    <let name="basename"><subst match="\.wi$" result="">&content;</subst></let>
    <em>( <ss>WOAD</ss> <hide>
        </hide><a href="/.Woad/help.xh#&basename;">&basename;</a> index )</em> 
  </text></action>
</define>

<define element="describeNote">
  <doc> A note's listing entry: this contains the title (if any, boldfaced),
	the summary (if any), and a [more] link if the note has additional
	content.
  </doc>
  <action>
    <load-note>&content;</load-note>
    <if> <note-title /> <note-summary />
	 <then> <if> <note-title />
		     <then> <b><note-title /></b><br />
		     </then>
		</if>
		<note-summary />
		<if> <note-content />
		     <then> <a href="&content;">[more]</a>
		     </then>
		</if>
	</then>
	<else> <em>( <ss>WOAD</ss> annotation file: untitled )</em>
	</else>
    </if>
  </action>
</define>

<define element="displayNoteAsHeader">
  <doc> The note whose path is passed in the content is displayed as a page
	header if present.
  </doc>
  <action>
    <if> <note-title />
	 <then> <p align="center"><big><strong><note-title /></strong></big></p>
	 </then>
    </if>
    <blockquote><note-summary /></blockquote>
    <blockquote><note-content /></blockquote>
  </action>
</define>

<define element="displayHeaderIfPresent">
  <action>
    <if> <status item="exists" src="&content;" />
	 <then>
	    <set name="VAR:haveHeader">yes</set>
	    <load-note>&content;</load-note>
	    <hr />
	    <displayNoteAsHeader />
	 </then>
    </if>
  </action>
</define>

<define element="uniquify">
  <doc> Append a numeric suffix to the name in the content to make it unique.
	Takes file list in <code>&amp;files;</code>.
  </doc>
  <action><hide>
    <set name="n">001</set>
    <repeat><while><extract><from>&files;</from>
			    <key>&content;-&n;.ww</key></extract>
	    </while>
	    <set name="n"><numeric op="sum" pad="3">&n; 1</numeric></set>
    </repeat>
    </hide>&content;-&n;<hide>
  </hide></action>
</define>

<define element="uniquifyIfNeeded">
  <doc> Append a numeric suffix to the name in the content to make it unique,
	but only if the name already exists without a suffix.  Takes file list
	in <code>&amp;files;</code>.
  </doc>
  <action><if><extract><from>&files;</from>
		 <key>&content;.ww</key></extract>
	      <then><uniquify>&content;</uniquify></then>
	      <else>&content;</else>
  </if></action>
</define>

<define element="listNoteFiles">
  <doc> This element sets the variables <code>files</code>,
	<code>indexFiles</code>, <code>indexDirs</code>, and
	<code>noteFiles</code>.  The content is the path to the directory
	containing the notes.  It sets the variable <code>notePath</code> to
	the note directory's path, and forces it to end with a slash.
  </doc>
  <action>
    <set name="VAR:notePath">
	 <forceTrailingSlash>&content;</forceTrailingSlash>
    </set>
    <set name="VAR:notePath">
         <subst match="\/\/" result="/">&VAR:notePath;</subst>
    </set>
    <set name="VAR:files">
      <text sort><status item="files" src="&content;" /></text>
    </set>

    <set name="VAR:indexFiles">
      <extract><from>&files;</from><match>\.wi$</match></extract>
    </set>

    <set name="VAR:indexDirs">
      <extract><from>&files;</from><match>^-.-$</match></extract>
    </set>

    <set name="VAR:noteFiles">
      <repeat><foreach entity="f">&files;</foreach>
	 <if><rejectNote>&f;</rejectNote>
	     <else>&f;</else>
	 </if>
      </repeat>
    </set>
  </action>
</define>
		     
<h2>Page Components</h2>

<h3>Annotation Table and its Components</h3>

<doc> The elements in this section are used to build the table of indices and
      notes that appears at the top of every Woad listing page.  In general
      they assume that <code>VAR:xloc</code> is the path of the directory that
      contains the notes.
</doc>

<define element="handleNoteCreation">
  <doc> This expands into the code that <em>creates</em> a note using the note
	creation form.  The content is the path to the directory that will
	contain the new note; it may be empty if we can guarantee that
	<code>LOC:path</code> is correct.  Note that we do <em>nothing</em> if
	the note already exists.  This usually means that the user has used the
	BACK button after perhaps editing the note; we mustn't clobber it.
  </doc>
  <action>
    <if> <get name="FORM:create" />
	 <then>
	   <let name="dir"><get name="content" /></let>
	   <if> <status item="exists" src="&dir;&FORM:label;.ww" />
	        <else><!-- create new note -->
<output dst="&dir;&FORM:label;.ww"><make name="note">
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
<!-- === AllNotesByTime needs to go under &project; or something eventually -->
<output dst="/AllNotesByTime.wi" append="yes">
<make name="Wfile"><hide>
   <let name="name">&FORM:label;.ww</let>
   <let name="path">&dir;&FORM:label;.ww</let>
   <let name="type">note</let>
   <!-- let name="size"><status src="&path;" item="length" /></let -->
   <let name="mtime"><status src="&path;" item="last-modified" /></let>
   <if>&FORM:title;<then><let name="title">&FORM:title;</let></then></if>
   </hide><parse tagset="HTML"><get name="FORM:summary" /></parse>
</make>
</output>
		</else>
	   </if>
	 </then>
    </if>
  </action>
</define>

<define element="indexTableRow">
  <action><hide>
      <let name="f">&attributes:f;</let>
      <let name="label"><subst match="\.[^.]*$" result="">&f;</subst></let>
    </hide>
    <tr><td align="left" valign="top">
            <a href="&f;"><code>&label;</code></a>
	</td>
	<td colspan="2" valign="top">
      	    <if> &content;
      		 <then>
      			<repeat> <foreach entity="g">&content;</foreach>
       			   <let name="lbl">
      				<subst match="\.[^.]*$" result="">&g;</subst>
      			   </let>
       			   <let name="lbl">
      				<subst match="^[^-]*" result="">&lbl;</subst>
      			   </let>
            		   <a href="&g;"><code>&lbl;</code></a>
     			</repeat>
		        <a href="/.Woad/help.xh#&label;">[help]</a>
      		 </then>
      		 <else>
      			<describeIndex>&f;</describeIndex>
      		 </else>
      	    </if>
	</td>
    </tr>
  </action>
</define>

<define element="indexTableRows">
  <doc> This expands into a sequence of table rows listing index
	files, together with their heading row.  The content is the list of
	filenames to tabulate.  Files with names of the form
	<code><em>prefix</em>-<em>X</em>-</code> are listed in a single row.
	<em>At the moment the file <code>prefix.wi</code> must also exist.</em>
  </doc>
  <action>
    <tr> <th bgcolor="#cccccc" width="150"> indices </th>
	 <th bgcolor="#cccccc" colspan="2" align="left"> description / link to
	 help </th> 
    </tr> <let name="index"></let><let name="subs"></let>
    <if> &indexDirs;
         <then> <doc> we have sub-index directories.  Put them out first.
    		</doc>
    		<indexTableRow f="">&indexDirs;</indexTableRow>
         </then>
    </if>
    <repeat>
      <foreach entity="f">&indexFiles;</foreach>
      <if> <test match="-.-">&f;</test>
    	   <then><doc> we have a sub-index file.  Add it to subs.  Note the
	               use of <code>set</code> here -- we're inside a
	               <code>repeat</code>, which has its own context.
    		 </doc>
    		 <set name="subs"><get name="subs"/> &f;</set>
	   </then>
           <else><doc> ok, it's not a sub-index.  Do the row, taking advantage
	               of the fact that ``<code>-</code>'' sorts before
	               ``<code>.</code>'', so if any sub-indices existed we
	               would already have seen them.
    		 </doc>
    		 <indexTableRow f="&f;">&subs;</indexTableRow>
    		 <set name="subs"></set>
           </else>
      </if>
    </repeat>
  </action>
</define>

<define element="noteTableRows">
  <doc> This expands into a sequence of table rows listing notes.  The content
	is the list of filenames to tabulate.  The heading row is <em>not</em>
	displayed, since it may vary considerably depending on context. 
  </doc>
  <define attribute="path">
    <doc> If present, this overrides <code>xloc</code> as the path to the
	  notes.
    </doc>
  </define>
  <define attribute="color">
    <doc> Specifies the background color for the table cells.
    </doc>
  </define>
  <define attribute="heading">
    <doc> If present, this is the content to display in the left column of a
	  heading row.  If absent, no heading row is displayed.
    </doc>
  </define>
  <action><hide>
      <let name="color"><get name="attributes:color">yellow</get></let>
      <let name="path"><get name="attributes:path">&notePath;</get></let>
    </hide>
    <if><get name="attributes:heading"/><then>
      <tr> <th bgcolor="#cccccc" width="150"> <get name="attributes:heading"/>
	   </th>
	   <th bgcolor="#cccccc" colspan="2" align="left"> title / summary </th>
      </tr>
    </then></if>
    <repeat>
      <foreach entity="f">&content;</foreach>
      <let name="label"><subst match="\.[^.]*$" result="">&f;</subst></let>
      <tr> <td align="left" valign="top" bgcolor="&color;">
		    <a href="&path;&f;"><code>&label;</code></a>
	   </td>
	   <td colspan="2" valign="top" bgcolor="&color;">
		     <describeNote>&path;&f;</describeNote>
	   </td>
      </tr>
    </repeat>
  </action>
</define>

<define element="pageNoteRows">
  <doc> Expands into the code that checks for and displays the page (URL)
	notes corresponding to the source page being displayed.  The content
	is the path to the notes. 
  </doc>      
  <action>
    <if>&content;
      <then>
	<hide>
	 <set name="npath">
      	      <forceTrailingSlash>&content;</forceTrailingSlash>
         </set>
	  <set name="VAR:nfiles">
	    <text sort><status item="files" src="&content;" /></text>
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
      <!-- === Link needs to be corrected if npath="/" === -->
      <if>&nnoteFiles; <then>
	<tr> <th bgcolor="#cccccc" width="150"> page notes </th>
	     <th bgcolor="#cccccc" colspan="2" align="left"> <em>(From the URL
	     annotations for <a href="&npath;">&npath;</a>)</em></th>
	</tr>
	<noteTableRows path="&npath;" color="#99ccff">&nnoteFiles;</noteTableRows>
      </then></if>
      </then>
    </if>
  </action>
</define>

<define element="noteCreationForm">
  <doc> This expands into the note creation form and its header line.  The
	content must be the list of &lt;option&gt; elements that specify the
	label.
  </doc>
  <action>
    <tr> <th bgcolor="#cccccc" width="150"> new note </th>
	 <th bgcolor="#cccccc" colspan="2" align="left">
	      Enter note text or use the
	      <a href="/.Woad/Tools/new-note?path=&xloc;">[advanced form]</a> 
	 </th>
    </tr>
    <tr> <td valign="top">
	    <select name="label"> <get name="content" />
	    </select><br />
	    <input name="create" value="Create Note" type="submit" />
	 </td>
	 <td valign="top" colspan="2">
	    <textarea name="summary" cols="60" rows="4" wrap="wrap"></textarea>
	 </td>
    </tr>
  </action>
</define>

<h3>Utility tags for use in page components</h3>

<define element="xf">
  <doc> Either an anchor link or a bold name.  Used on lines that
	contain links to any of several different formats.
  </doc>
  <define attribute="fmt">
    <doc> The "format", attached as a query string to the URL and matched
	  against the current value of <code>&amp;format;</code>.
    </doc>
  </define>
  <define attribute="href">
    <doc> The URL of the page.
    </doc>
  </define>
  <action>
    <if><test exact match="&attributes:fmt;">&VAR:format;</test>
	<then><b><get name="content"/></b></then>
	<else><a href="&attributes:href;">&content;</a></else>
    </if>
  </action>
</define> 

<define element="xa">
  <doc> Either an anchor link or a bold name.  Used on lines that
	contain links to any of several different pages.
  </doc>
  <define attribute="page">
    <doc> The base name of the current page, matched against the URL (href
	  attribute).  If missing, the current binding of "page" is used.
    </doc>
  </define>
  <define attribute="href">
    <doc> The URL of the page.
    </doc>
  </define>
  <action>
    <if><get name="attributes:page"/>
	<then><set name="page"><get name="attributes:page"/></set></then>
    </if>
    <if><do name="test">
	    <set name="match"><get name="page"/></set>
	    <get name="attributes:href"/></do>
	<then><b><get name="content"/></b></then>
	<else><do name="a">
		  <set name="href"><get name="attributes:href"/></set>
		  <get name="content"/></do></else>
    </if>
  </action>
</define> 

<define element="xan">
  <doc> Either an anchor link or a bold name.  Used on lines that contain
	links to any of several different fragment names within a single page.
  </doc>
  <define attribute="name">
    <doc> The name to link to.  Matched against the content. 
    </doc>
  </define>
  <define attribute="text">
    <doc> If present, the text of the element.  If omitted, the name is used. 
    </doc>
  </define>
  <action>
    <let name="name"><get name="attributes:name"/></let>
    <let name="text">
	<get name="attributes:text"><get name="attributes:name"/></get>
    </let>
    <if><test match="&attributes:name;"><get name="content"/></test>
	<then><b><get name="text"/></b></then>
	<else><make name="a">
		  <let name="href">#<get name="name"/></let>
		  <get name="text"/></make></else>
    </if>
  </action>
</define> 

<define element="xopt">
  <doc> Output the content if the page attribute matches one of the
	names listed in the pages attribute.  Used for an indicator
	(e.g, a blue dot) on lines that contain links to several
	different pages.
  </doc>
  <define attribute="page">
    <doc> the base name of the current page, matched against the
	  <code>pages</code> attribute.  If missing, the current binding of
	  "page" is used. 
    </doc>
  </define>
  <define attribute="pages" required="yes">
    <doc> space-separated names of the pages represented on this line.
    </doc>
  </define>
  <action>
    <if><get name="attributes:page"/>
	<then><set name="page"><get name="attributes:page"/></set></then>
    </if>
    <if><do name="test">
	    <set name="match"><get name="page"/></set>
	    <get name="attributes:pages"/></do>
	<then><expand><get name="content"/></expand></then>
    </if>
  </action>
</define> 

<define element="xlink">
  <doc> Construct a link to the content.
  </doc>
  <define attribute="text">
    <doc> If present, this is the text (content) of the link element.
	  Otherwise, the URL is used.
    </doc>
  </define>
  <action><do name="a">
	      <set name="href"><get name="content"/></set>
	      <if><get name="attributes:text"/>
		  <then><get name="attributes:text"/></then>
		  <else><get name="content"/></else>
	      </if>
	   </do>
  </action>
</define> 

<define element="verboseForm" syntax="quoted">
  <doc> Expand the content if the AGENT:verboseForms option is set.
  </doc>
  <action><if><get name="AGENT:verboseForms"/>
	      <then><expand><get name="content"/></expand></then></if>
  </action>
</define>

<define element="verbosePage" syntax="quoted">
  <doc> Expand the content if the AGENT:verbosePage option is set.
  </doc>
  <action><if><get name="AGENT:verbosePages"/>
	      <then><expand><get name="content"/></expand></then></if>
  </action>
</define>

<h3>Annotations</h3>

<define element="yellow-note">
  <doc>
  </doc>
  <action>
       <table bgcolor="yellow" width="100%" cellspacing="0" cellpadding="3"
	      border="0">
	 <tr> <td valign="top"> &nbsp;<b>Note:</b>&nbsp;
	      </td>
	      <td> <get name="content" />
	      </td>
	 </tr>
       </table>
  </action>
</define>

<define element="yellow-box">
  <doc>
  </doc>
  <action>
       <table bgcolor="yellow" width="100%" cellspacing="0" cellpadding="3"
	      border="0">
	 <tr> <td> <get name="content" />
	      </td>
	 </tr>
       </table>
  </action>
</define>

<h3>Headers and Footers</h3>

<define element="header">
  <doc> This expands into a standard header.  The content is the
	title, and ends up assigned to the entity <code>title</code>.  This
	element also initializes some common entities.
  </doc>
  <define attribute="show-date" optional>
    <doc> If present, show the date underneath the title.
    </doc>
  </define>
<action><hide>
    <if><get name="content"/>
	<then><set name=title><get name="content"/></set></then></if>
    <set name="agentNames"><text sort case><get name="agentNames"/></text></set>
    <if><get name="ltitle" /><then> </then>
        <else><set name="ltitle"><do name="a">
		<set name="href"><get name="locPath"/></set>
		<get name="locName"/></do>:</set></else>
    </if>
</hide>
<table cellpadding=0 cellspacing=0>
<tr><th><a href="http://RiSource.org/PIA/"><img src="/.Woad/Icon/pia45.png"
			 border=0 width=85 height=45 alt="P I A"></a></th>
    <td valign=bottom>
      <table>
	<tr><th valign=bottom align=left><expand>&WOAD-LOGO;</expand>: 
	       <ss>Web-Organized Application Development</ss>
	    </th>
	</tr>
	<tr height=6><td><img src="/.Woad/Icon/rule.png" height=6 width=469></td></tr>
      </table>
    </td>
</tr>
</table>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th valign=top width=170>&nbsp;</th>
    <th align=right valign=top width=170><get name="ltitle"/>&nbsp; </th>
    <th align=left valign=top><if><get name="title"/>
	<then><get name="title"/></then>
	<else><get name="fileName"/></else></if></th></tr>
<if><get name="attributes:show-date"/><then>
  <tr nowrap nobr>
      <td colspan=2></td><td><get name="dayName"/>, <get name="year"/>-<get
	name="month"/>-<get name="day"/>, <get name="time"/>:<get
	name="second"/></td></tr>
  </then></if>
</table>
</action>
</define>

<define element="sub-head" syntax="quoted">
  <doc> A secondary table located immediately under the header.
	Content should consist of additional table rows.
  </doc>
  <define attribute="page">
    <doc> the base name of the page, e.g. <code>index</code> or
	  <code>home</code>.
    </doc>
  </define>
  <action><set name=page><get name="attributes:page"/></set>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170
        ><br/><include src="insert"> </include></th>
    <td>
      <table cellspacing=0 cellpadding=0 border=0>
	<tr><th align=left nowrap width=170><get name="blank-170x1"/></th>
	    <td><br /></td>
	</tr>
	<tr><th align=right>
	      <xopt pages="home index help options">
	  	<expand><get name="blue-dot"/></expand></xopt></th>
	    <td><xa href="home">Home</xa>
		<xa href="-">Index</xa>
		<if><status item="exists" src="help.xh" />
		    <then><xa href="help">Help</xa></then></if> 
		<if><status item="exists" src="help.xh" />
		    <then><xa href="options">Options</xa></then></if> 
	    </td>
	</tr>
	<expand><get name="content"/></expand>
      </table>
    </td>
</tr>
</table>
  </action>
</define>

<define element="tool-bar">
  <doc> A toolbar or collection of useful forms and other gizmos.  In WOAD, it
	is currently entirely devoted to <code>/.word</code> and its contents
	and should probably be renamed <tag>word-bar</tag>.
  </doc>
  <action>
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
	<th>keyword</th>
	<td>
	     <a href="/.words/">/contexts/</a> (
	     <a href="/.words/class/">classes</a>
	     <a href="/.words/file/">files</a>
	     <a href="/.words/func/">functions</a>
	     <a href="/.words/title/">titles</a>
	     <a href="/.words/xref/">xrefs</a>
	     ... )
	</td>
    <form action="/.words/">
	<td align="right"> look up: <input name="word" />
	</td>
    </form>
      </tr>
    </table>
  </action>
</define>

<define element="index-bar">
  <doc> A navigation bar used to index sections within a page.  Content is a
	list of section names.  
  </doc>
  <define attribute="name">
    <doc> The name of the current section.
    </doc>
  </define>
  <action><a name="&attributes:name;"><hr /></a>
    <table width="100%" border="2" cellpadding="0" cellspacing="0"
	   bgcolor="99ccff">
      <tr>
	<td> <repeat>
	       <foreach><text op="trim"><get name="content"/></text></foreach>
	       <xan name="&li;"><get name="attributes:name"/></xan>
	     </repeat>
	</td>
      </tr>
    </table>
  </action>
</define>

    

<define element="nav-bar">
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="&PIA:rootPath;">PIA</a> || <a href="/~Admin">Admin</a> ||
          <make name="a">
	    <set name="href"><get name="locPath"/>/</set>
	    <get name="locName"/></make>:
          <xlink text="index"><get name="locPath"/>/-</xlink>
	<if><status src="&locPath;/DATA" item="exists" /><then>
	  <xlink text="help"><get name="locPath"/>/help</xlink> (
	     <xlink text="specific"><get name="locPath"
                    />/help#context-specific</xlink>
	     <xlink text="general"><get name="locPath"
                    />/help#general</xlink> )
	</then></if>
	<if><status src="&locPath;/DATA" item="exists" /><then>
          <xlink text="&PIA:rootPath;data/"><get name="locPath"/>/DATA/</xlink>
	</then></if>
  </action>
</define>

<define element="footer" syntax="empty">
  <doc> This expands into a standard footer, including a ``navigation bar''.
	Go to some lengths to extract the year the file was modified from the
	cvs id.
  </doc>
  <define attribute="cvsid">
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr />
<nav-bar />
<hr />
    <hide>
      <set name="dateIndex">
	<if><test match='Id:'><get name="attributes:cvsid"/></test>
	    <then>3</then><else>2</else>
	</if>
      </set>
      <set name="myear">
	<subst match="/.* " result=", "><extract>
          <from><get name="attributes:cvsid"/></from>
          <text split><get name="list"/></text>
          <nodes><get name="dateIndex"/></nodes>
        </extract> </subst> </set>
    </hide>
<b><ss>WOAD</ss> Copyright &copy; <get name="myear"/> Ricoh Silicon Valley.</b>
   Open Source at &lt;<b>&RiSource.org;/&RiSource.org.pia;</b>&gt;.<br />
<em><get name="attributes:cvsid" /></em>
<a href="&PIA:rootPath;Agents/View/source?url=&docPath;">view source</a>
  </action>
</define>

<define element="short-footer" syntax="empty">
  <doc> This expands into a short-form footer: just the CVS id and copyright
	notice. 
  </doc>
  <define attribute="cvsid">
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr />
    <hide>
      <set name="dateIndex">
	<if><test match="Id:"><get name="attributes:cvsid"/></test>
	    <then>3</then><else>2</else>
	</if>
      </set>
      <set name="myear">
	<subst match="/.* " result=", "><extract>
          <from><get name="attributes:cvsid"/></from>
          <text split><get name="list"/></text>
          <nodes><get name="dateIndex"/></nodes>
        </extract> </subst> </set>
    </hide>
<b><ss>WOAD</ss> Copyright &copy; <get name="myear"/> Ricoh Silicon Valley</b>.
   Open Source at &lt;<b>&RiSource.org;/&RiSource.org.pia;</b>&gt;.<br>
<em><get name="attributes:cvsid" /></em>
<if><status src="&PIA:rootPath;Agents/View/source.xh" item="exists"/>
    <then><a href="&PIA:rootPath;Agents/View/source?url=&docPath;">view source</a></then>
</if>
  </action>
</define>

<define element="inc-footer" syntax="empty">
  <doc> This expands into a tiny footer for include files. 
  </doc>
  <define attribute="cvsid">
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
    <hide>
      <set name="dateIndex">
	<if><test match="Id:"><get name="attributes:cvsid"/></test>
	    <then>3</then><else>2</else>
	</if>
      </set>
      <set name="myear">
	<subst match="/.* " result=", "><extract>
          <from><get name="attributes:cvsid"/></from>
          <text split><get name="list"/></text>
          <nodes><get name="dateIndex"/></nodes>
        </extract> </subst> </set>
      <set name="incfn">
	<extract><from><get name="attributes:cvsid"/></from>
	         <text split><get name="list"/></text> 0
	</extract><extract><from><get name="attributes:cvsid"/></from>
	         <text split><get name="list"/></text>
		 <nodes><numeric difference><get name="dateIndex"/> 2</numeric>
		        <numeric difference><get name="dateIndex"/> 1</numeric>
		 </nodes>
	</extract>
      </set>
    </hide>
<h6 align="right"><get name="incfn"/> &copy; <get name="myear"/>
    Ricoh Silicon Valley</h6>
  </action>
</define>

<!-- $Id: woad-xhtml.ts,v 1.31 2000-12-06 02:11:35 steve Exp $ -->
</tagset>

