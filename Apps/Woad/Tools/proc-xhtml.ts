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
<!-- Contributor(s): steve@rii.ricoh.com pgage@rii.ricoh.com                -->
<!-- ====================================================================== -->

<tagset name="src-xhtml" include="proc-html" tagset="woad-xhtml"
	documentWrapper="-document-"
>

<cvs-id>$Id: proc-xhtml.ts,v 1.2 2001-01-11 23:36:39 steve Exp $</cvs-id>

<h1>WOAD Source-listing for XHTML</h1>

<doc> This tagset is used for listing XHTML files in "processed" mode.
</doc>


<define element="-document-" quoted="yes" >
  <doc> This is the outside wrapper.  Note that the tagname must be lowercase
	because some tagsets (e.g. HTML) are case-insensitive, so every tag
	defined here may get case-smashed.
  </doc>
  <action mode="defer-content"><expand>&content;</expand></action>
</define>

<h2>Tags defined in <code>basic.ts</code></h2>

<h3>Definition elements</h3>

<define entity="cc"><value>#cc0000</value></define><!-- control color -->
<define entity="dc"><value>#3366ff</value></define><!-- definition color -->

<define element="tagset" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="tagset" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="namespace" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="namespace" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="properties" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="properties" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>

<define element="define" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="define" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="value" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="value" tc="&dc;"><expand>&content;</expand></elt></action>
</define>
<define element="action" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="action" tc="&dc;"><expand>&content;</expand></elt></action>
</define>

<define element="doc" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="doc" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="note" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="note" tc="&dc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>

<define element="do" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="do" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="make" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="make" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>


<h3>Name-binding tags</h3>

<define element="bind">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="bind" tc="&dc;">&content;</elt></action>
</define>
<define element="let">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="let" tc="&dc;">&content;</elt></action>
</define>
<define element="get">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="get" tc="&dc;">&content;</elt></action>
</define>
<define element="set">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="set" tc="&dc;">&content;</elt></action>
</define>

<h3>Control-structure tags</h3>

<define element="if" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="if" tc="&cc;"><expand>&content;</expand></elt-b></action>
</define>
<define element="then" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="then" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="else" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="else" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="else-if" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><wrap /><elt tag="else-if" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="elif" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="elif" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="elsf" syntax="quoted">
  <action><wrap /><hide><let name="atts">&attributes;</let>
    </hide><elt tag="elsf" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="test" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="test" tc="&cc;"><expand>&content;</expand></elt></action>
</define>

<define element="repeat" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="repeat" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="foreach" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="foreach" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="for" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="for" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="start" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="start" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="stop" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="stop" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="step" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="step" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="while" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="while" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="until" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="until" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="first" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="first" tc="&cc;"><expand>&content;</expand></elt></action>
</define>
<define element="finally" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="finally" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>


<h3>Computational Elements</h3>

<define element="logical" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="logical" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="numeric" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="numeric" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="text" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="text" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="subst" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="subst" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="parse" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="parse" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="to-text" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="to-text" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>


<define element="extract" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="extract" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="from" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="from" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="in" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="in" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="id" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="id" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="name" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="name" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="tm_like" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="tm_like" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="any-tag" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="any-tag" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="sort" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sort" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="key" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="key" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="match" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="match" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="child" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="child" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="attr" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="attr" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="has-attr" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="has-attr" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="nodes" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="nodes" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="xptr" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="xptr" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="eval" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="eval" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="content" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="content" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="parent" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="parent" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="next" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="next" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="prev" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="prev" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="replace" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="replace" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="append" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="append" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="insert" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="insert" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="remove" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="remove" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="unique" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="unique" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<h3>Expansion Control Elements</h3>

<define element="expand" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="expand" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="protect" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="protect" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="hide" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="hide" tc="&cc;"><em><expand>&content;</expand></em><hide>
    </hide></elt></action>
</define>


<h3>Link-like tags</h3>

<define element="include" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="include" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="output" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="output" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="connect" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="connect" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="status" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="status" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<h3>Data Structures</h3>

<define element="DOCUMENT" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="DOCUMENT" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="HEADERS" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="HEADERS" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="URL" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="URL" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="QUERY" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="QUERY" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>


<h2>Tags defined in <code>xhtml.ts</code></h2>

<define element="checkbox" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="checkbox" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="radio" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="radio" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="date" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="date" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="weekday" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt-b tag="weekday" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt-b></action>
</define>
<define element="seconds" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="seconds" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="mstime" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="mstime" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="julian" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="julian" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="year" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="year" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="month" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="month" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="day" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="day" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>


<h2>Tags defined in <code>pia-xhtml.ts</code></h2>

<doc> Strictly speaking, we shouldn't be doing this.  But it's expedient to
      define at least the most common ones, because they'll make PIA code much
      easier to read.
</doc>

<define element="xa" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="xa" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="xopt" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="xopt" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="xlink" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="xlink" tc="&lc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="agentRunning" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="agentRunning" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="header" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="header" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="sub-head" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="sub-head" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="nav-bar" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="nav-bar" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="footer" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="footer" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="short-footer" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="short-footer" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>
<define element="inc-footer" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="inc-footer" tc="&cc;"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<!-- === some tags still missing === -->

<define element="" syntax="quoted">
  <action><hide><let name="atts">&attributes;</let>
    </hide><elt tag="&tagname;" tc="#996666"><expand>&content;</expand><hide>
    </hide></elt></action>
</define>

<define element="#comment" syntax="quoted">
  <action><font color="red">&lt;!--&value;--&gt;</font><hide>
    </hide></action>
</define>

<define element="#pi" syntax="quoted">
  <action><font color="red">&lt;?<get name="name"/> &value;?&gt;</font><hide>
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

