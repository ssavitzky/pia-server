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
        wrapper="-DOCUMENT-" >

<cvs-id>$Id: src-html.ts,v 1.1 2000-06-02 23:48:34 steve Exp $</cvs-id>

<h1>WOAD Source-listing for HTML</h1>

<doc> This tagset is used for listing HTML files.
</doc>

<define element="html" quoted="yes" >
  <doc> This is the outside wrapper -- needs to be renamed -DOCUMENT- after we
	get wrappers working in the parser.
  </doc>
  <action>
<html>
  <set name="format"><if> &FORM:pretty;
    <then>pretty</then>
    <else-if>&FORM:raw;<then>raw</then></else-if>
    <else></else>
  </if></set>
  <head><title>&DOC:path; -- WOAD listing</title>
  </head>
  <body bgcolor="#ffffff">
    <h1><code>&DOC:path;</code></h1>

    <if> &FORM:probe;
	 <then> <a href="#entities"><b>entities</b></a>
	 </then>
    </if>

    <h3><ss>WOAD &format; source listing</ss></h3>    

<if> &FORM:pretty;
  <then>
<blockquote><em> This <tt>&format;</tt> listing is color-coded and indented to
show the nesting level of the tags. 
</em></blockquote>
<hr />
<pretty>&content;</pretty>
  </then>
<else-if> &FORM:raw;
  <then>
<blockquote><em> This <tt>&format;</tt> listing shows the full contents of the
  file with no alteration.
</em></blockquote>
<hr />
<pre><protect result="result" markup="markup">
<include src="&DOC:path;" quoted="true" /></protect></pre>
  </then></else-if>
  <else>
<blockquote><em>
  This <tt>&format;</tt> listing is color-coded and font-coded according to
  syntax.  For a complete list of HTML tags and their attributes, see <a
  href="http://werbach.com/barebones/barebones.html">The Bare Bones Guide to
  HTML</a>.  Note that because of current limitations entities are expanded in
  place, omitted end tags are shown, and linebreaks inside of tags are
  eliminated.  Missing end tags are not handled correctly in many cases.
</em></blockquote>
<hr />
<doc> Wrap doesn't work well at this point </doc>
<if>&FORM:wrap;
    <then><expand>&content;</expand></then>
    <else><pre><tag>html</tag><expand>&content;</expand><tag>/html</tag></pre></else>
</if>
  </else>
</if>
<hr />

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
</define>

<h3>Structural elements</h3>

<define entity="sc"><value>#ff9933</value></define>

<define element="head" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="head" tc="&sc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>

<define element="title" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="title"><b><expand>&content;</expand></b></elt></action>
</define>

<define element="link" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="link" tc="&lc;" /></action>
</define>
<define element="meta" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="meta" /></action>
</define>
<define element="base" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
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
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="frame" /></action>
</define>
<define element="noframes" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="noframes" tc="&sc;"
           ><expand>&content;</expand></elt-b></action>
</define>

<h3>Headings</h3>

<define element="h1" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h1" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
</define>
<define element="h2" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h2" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
</define>
<define element="h3" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h3" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
</define>
<define element="h4" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h4" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
</define>
<define element="h5" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h5" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
</define>
<define element="h6" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="h6" tc="&sc;"><b><expand>&content;</expand></b><hide>
    </hide></elt></action>
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
    </hide>elt-b tag="dl"><expand>&content;</expand></elt-b></action>
</define>
<define element="menu" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="menu"><expand>&content;</expand></elt-b></action>
</define>
<define element="dir" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="dir"><expand>&content;</expand></elt-b></action>
</define>

<define element="li" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="li"><expand>&content;</expand></elt></action>
</define>
<define element="dt" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dt"><expand>&content;</expand></elt></action>
</define>
<define element="dd" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="dd"><expand>&content;</expand></elt></action>
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
    </hide><elt tag="thead"><expand>&content;</expand></elt></action>
</define>
<define element="tfoot" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="tfoot"><expand>&content;</expand></elt></action>
</define>
<define element="caption" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="caption"><expand>&content;</expand></elt></action>
</define>


<h3>Paragraph-level Elements</h3>

<define element="p" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="p"><expand>&content;</expand></elt></action>
</define>

<define element="div" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="div"><expand>&content;</expand></elt-b></action>
</define>

<define element="blockquote" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="blockquote"><expand>&content;</expand></elt></action>
</define>

<define element="center" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="center"><expand>&content;</expand></elt></action>
</define>

<define element="br" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="br" /></action>
</define>
<define element="hr" syntax="empty">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="hr" /></action>
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
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="pre"><expand>&content;</expand><hide>
    </hide></elt></action>
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

</tagset>

