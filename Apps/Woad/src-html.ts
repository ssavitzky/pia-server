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
        documentWrapper="-document-" >

<cvs-id>$Id: src-html.ts,v 1.5 2000-06-14 17:07:16 steve Exp $</cvs-id>

<h1>WOAD Source-listing for HTML</h1>

<doc> This tagset is used for listing HTML files.
</doc>

<h2>Document Wrapper</h2>

<define element="-document-" quoted="yes" >
  <doc> This is the outside wrapper.  Note that it has to be lowercase because
	some including tagsets might not be case-insensitive, and every tag
	defined here in HTML gets case-smashed.
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
  <set name="tpath"><mapToTarget>&DOC:path;</mapToTarget></set>
  <set name="spath">
    <if>&SITE:sourceOffset;
	<then><subst match="^&SITE:sourceOffset;"
	             result="">&DOC:path;</subst></then>
	<else>&DOC:path;</else>
    </if></set>
  <set name="slpath">
    <if>&SITE:sourceOffset;
	<then><subst match="^&SITE:sourceOffset;"
	             result="">&LOC:path;</subst></then>
	<else>&LOC:path;</else>
    </if></set>

  <doc> The following crockery is used to compute &amp;decoratedPath;,
	which is the path of the current directory ``decorated'' with
	".." links to the directories above "."
  </doc>
  <set name="depth">0</set>
  <set name="split"><subst match="/" result=" ">TOP&slpath;</subst></set>
  <repeat>
    <foreach><text split>&split;</text></foreach>
    <set name="depth"><numeric op="sum">1 <get name="depth"/></numeric></set>
  </repeat>
  <set name="d">2</set>
  <set name="decoratedPath"><repeat><hide>
    </hide><foreach entity="f"><text split>&split;</text></foreach><hide>
    <set name="dots"><repeat><for start="&d;" stop="&depth;"/>../</repeat></set>
    <set name="d"><numeric op="sum">1 <get name="d"/></numeric></set>
    </hide><if> &dots;
	  <then><a href="&dots;">&f;</a>/</then>
	  <else><a href="../&f;">&f;</a>/</else>
  </if></repeat>&DOC:name;</set>

  </hide>
  <head><title>&spath; -- WOAD listing</title>
  </head>
  <body bgcolor="#ffffff">
    <table bgcolor="#99ccff" cellspacing="0" border="0" width="100%">
      <tr><th align="left">
          <h1><code>&decoratedPath;</code></h1>
          </th>
      </tr>
    </table>
    <if> &FORM:probe;<!-- this is wrong now. -->
	 <then> <a href="#entities"><b>entities</b></a>
	 </then>
    </if>

    <table bgcolor="#99ccff" cellspacing="0" border="0" width="100%">
      <tr> <td>
	     <table><!-- the nested table shrink-wraps its contents     -->
	            <!-- so it doesn't get spread out by the width=100% -->
	       <tr><th align="left">
		      <big> &nbsp;<ss><a href="/.Woad/">WOAD</a></ss></big>
		   </th>
		   <th align=left>
		      <big> &VAR:format; source listing</big>
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
		          </then>
		      </if>
		      <a href="/.Woad/help.xh#views">[help]</a>
		 </td>
		</tr>
	     </table>
	   </td>
	   <td>&nbsp;</td>
      </tr>
    </table>
<!-- ===================================================================== -->
<if> &FORM:nested;	<!-- ====== nested =============================== -->
  <then>
    <blockquote><em>
      This <tt>&VAR:format;</tt> listing is color-coded and indented to show
      the nesting level of the tags.  

      <red>Note that because of current limitations omitted end tags are
      shown, and linebreaks inside of tags are eliminated.  Missing end tags
      may not be handled correctly.</red>
</em></blockquote>
<hr />
<pretty>&content;</pretty>
  </then>
<else-if> &FORM:raw;	<!-- ====== raw ================================== -->
  <then>
<blockquote><em> This <tt>&VAR:format;</tt> listing shows the full contents of
  the file with no alteration.
</em></blockquote>
<hr />
<pre><include src="&DOC:path;" quoted="true" tagset="" /></pre>
  </then></else-if>
<else-if> &FORM:tsdoc;	<!-- ====== tagset =============================== -->
  <then>
<blockquote><em> This <tt>&VAR:format;</tt> listing shows tagset
  documentation. 
</em></blockquote>
<hr />
<include src="&DOC:path;" tagset="tsdoc" />
  </then></else-if>
<else>			<!-- ====== processed ============================ -->
    <blockquote><em>

      This <tt>&VAR:format;</tt> listing is color-coded and font-coded
      according to syntax.  The <tt><a href="&DOC:path;?wrap">wrapped</a></tt>
      format tends to be more compact than the normal <tt><a
      href="&DOC:path;">processed</a></tt> format, and is more readable in
      some cases.

      <red>Note that because of current limitations omitted end tags are
      shown, and linebreaks inside of tags are eliminated.  Missing end tags
      may not be handled correctly.</red>
    </em></blockquote>
<hr />
<if>&wrap;
    <then><expand>&content;</expand></then>
    <else><pre><expand>&content;</expand></pre></else>
</if>
  </else>
</if>

<hr /><!-- ====== End of Listing ===================================== -->
<if> &FORM:probe;
  <then>
<hr />
<h3><a name="entities">Entities</a></h3>
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
    </hide><elt-b tag="head" tc="&sc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>

<define element="title" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="title"><b><expand>&content;</expand></b><hide>
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
    </hide><elt-b tag="body" tc="&sc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>

<define element="frameset" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="frameset" tc="&sc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="frame" syntax="empty">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="frame" /></action>
</define>
<define element="noframes" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="noframes" tc="&sc;"
           ><expand>&content;</expand></elt-b></action>
</define>

<h3>Headings</h3>

<define element="h1" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h1" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h2" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h2" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h3" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h3" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h4" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h4" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h5" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h5" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>
<define element="h6" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h6" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></wrap></action>
</define>

<h3>Lists</h3>

<define element="ul" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="ul"><expand>&content;</expand></elt-b></action>
</define>
<define element="ol" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="ol"><expand>&content;</expand></elt-b></action>
</define>
<define element="dl" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="dl"><expand>&content;</expand></elt-b></action>
</define>
<define element="menu" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="menu"><expand>&content;</expand></elt-b></action>
</define>
<define element="dir" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="dir"><expand>&content;</expand></elt-b></action>
</define>

<define element="toc" syntax="quoted">
  <doc> This tag is not official HTML, but is used in the PIA to delimit the
	table of contents.
  </doc>
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="toc"><expand>&content;</expand></elt-b></action>
</define>

<define element="li" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="li"><expand>&content;</expand></elt><wrap /></action>
</define>
<define element="dt" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dt"><expand>&content;</expand></elt><wrap /></action>
</define>
<define element="dd" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dd"><expand>&content;</expand></elt><wrap /></action>
</define>

<h3>Tables</h3>

<define element="table" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="table"><expand>&content;</expand></elt-b></action>
</define>
<define element="tr" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tr"><expand>&content;</expand></elt-b></action>
</define>
<define element="td" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="td"><expand>&content;</expand></elt></action>
</define>
<define element="th" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="th"><expand>&content;</expand></elt></action>
</define>
<define element="thead" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="thead"><expand>&content;</expand></elt-b></action>
</define>
<define element="tfoot" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tfoot"><expand>&content;</expand></elt-b></action>
</define>
<define element="tbody" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tbody"><expand>&content;</expand></elt-b></action>
</define>
<define element="caption" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="caption"><expand>&content;</expand></elt></action>
</define>


<h3>Paragraph-level Elements</h3>

<define element="p" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="p"><expand>&content;</expand></elt><wrap /></action>
</define>

<define element="div" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="div"><expand>&content;</expand></elt-b></action>
</define>

<define element="blockquote" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="blockquote"><expand>&content;</expand></elt-b></action>
</define>

<define element="center" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="center"><expand>&content;</expand><wrap /></elt></action>
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
    </hide><elt tag="b"><b><expand>&content;</expand></b></elt></action>
</define>
<define element="i" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="i"><i><expand>&content;</expand></i></elt></action>
</define>
<define element="u" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="u"><u><expand>&content;</expand></u></elt></action>
</define>
<define element="tt" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="tt"><tt><expand>&content;</expand></tt></elt></action>
</define>
<define element="em" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="em"><em><expand>&content;</expand></em></elt></action>
</define>
<define element="strong" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="strong"><strong><expand>&content;</expand></strong><hide>
    </hide></elt></action>
</define>

<define element="q" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="q"><em><expand>&content;</expand></em></elt></action>
</define>
<define element="cite" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="cite"><em><expand>&content;</expand></em></elt></action>
</define>
<define element="address" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="address"><em><expand>&content;</expand></em><hide>
    </hide></elt></action>
</define>
<define element="code" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="code"><code><expand>&content;</expand></code><hide>
    </hide></elt></action>
</define>

<define element="pre" syntax="quoted">
  <action><wrap><hide><let name="atts">&attributes;</let>
    </hide><elt tag="pre"><tt><expand>&content;</expand></tt><hide>
    </hide></elt></wrap></action>
</define>

<define element="font" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="font"><expand>&content;</expand></elt></action>
</define>
<define element="big" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="big"><expand>&content;</expand></elt></action>
</define>
<define element="small" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="small"><expand>&content;</expand></elt></action>
</define>

<define element="sub" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sub"><expand>&content;</expand></elt></action>
</define>
<define element="sup" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sup"><expand>&content;</expand></elt></action>
</define>

<define element="var" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="var"><expand>&content;</expand></elt></action>
</define>
<define element="dfn" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dfn"><expand>&content;</expand></elt></action>
</define>
<define element="samp" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="samp"><expand>&content;</expand></elt></action>
</define>
<define element="kbd" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="kbd"><expand>&content;</expand></elt></action>
</define>

<define element="ins" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="ins"><expand>&content;</expand></elt></action>
</define>
<define element="del" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="del"><expand>&content;</expand></elt></action>
</define>
<define element="acronym" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="acronym"><expand>&content;</expand></elt></action>
</define>


<h3>Links</h3>

<define entity="lc"><value>#cc00cc</value></define><!-- link color -->

<define element="a" syntax="quoted">
  <doc> If <code>href</code> attributes are doubled, it means you're inside a
	tag that should have <code>syntax="quoted"</code> but doesn't.
  </doc>
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="a" tc="&lc;"><hide>
      </hide><if>&attributes:name;
	<then><a name="&attributes:name;"><expand>&content;</expand></a></then>
	<else><a href="&attributes:href;"><expand>&content;</expand></a></else>
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
    </hide><elt-b tag="form"><expand>&content;</expand></elt-b></action>
</define>

<define element="input" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="input" /></action>
</define>

<define element="select" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="select"><expand>&content;</expand></elt-b></action>
</define>
<define element="option" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="option"><expand>&content;</expand></elt></action>
</define>
<define element="textarea" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="textarea"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="button" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="button"><expand>&content;</expand></elt></action>
</define>
<define element="label" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="label"><expand>&content;</expand></elt></action>
</define>
<define element="fieldset" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="fieldset"><expand>&content;</expand></elt></action>
</define>
<define element="legend" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="legend"><expand>&content;</expand></elt></action>
</define>

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

