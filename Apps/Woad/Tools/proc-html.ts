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
        include="src-wrapper src-file src-html" documentWrapper="-document-" >

<cvs-id>$Id: proc-html.ts,v 1.1 2000-10-06 17:40:52 steve Exp $</cvs-id>

<h1>WOAD Source-listing for HTML</h1>

<doc> This tagset is used for listing HTML files in "processed" format.
</doc>

<define element="-document-" quoted="yes" >
  <doc> This is the outside wrapper.  Note that the tagname must be lowercase
	because some tagsets (e.g. HTML) are case-insensitive, so every tag
	defined here may get case-smashed.
  </doc>
  <action mode="defer-content"><expand>&content;</expand></action>
</define>

<h3>Structural elements</h3>

<define entity="sc"><value>#ff9933</value></define>

<define element="html" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="html" tc="&sc;"><expand><get name="content" /><hide>
    </hide></expand></elt-b></action>
</define>

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

