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

<tagset name="src-file" tagset="woad-xhtml" parser="TextParser"
	include="src-wrapper" documentWrapper="-document-" >

<cvs-id>$Id: src-file.ts,v 1.2 2000-09-23 00:50:48 steve Exp $</cvs-id>

<h1>WOAD source-file listing for generic files</h1>

<doc> This tagset is used for listing arbitrary files.
</doc>

<!-- ============ CHEATING! ========================= -->
<doc> ... or maybe <em>not</em> cheating.  Associating these tags with, e.g.,
      a <code>.wl</code> (<ss>WOAD</ss> <em>listing</em>) extension would
      allow developers to customize the markup of individual files for maximum
      clarity or emphasis.
</doc>

<define element="tag">
  <doc> Used for tags <em>or things that look like tags</em> in source
	listings.  Things that look like tags include, for example, PERL
	filehandles.  When rendering code that outputs HTML (a good bet in web
	development!) it may be better to leave intact those tags that are
	properly nested, and to insert an "empty" flag in isolated start
	tags.  On the other hand, it may be cleaner to just look up the
	appropriate color. 
  </doc>
  <action><tag>&content;</tag></action>
</define>

<define element="qs">
  <doc> Quoted string (<em>double</em> quotes) </doc>
  <action><font color="#999999">"&content;"</font></action>
</define>

<define element="es">
  <doc> <em>Emphasized </em>Quoted string (<em>double</em> quotes).  This is
	meant to be used after keywords such as <code>print</code>.  An added
	refinement would be to only emphasize strings above a certain length.
  </doc>
  <action><font color="red"><b>"&content;"</b></font></action>
</define>

<define element="kw">
  <doc> Keyword. </doc>
  <action><font color="blue"><b>&content;</b></font></action>
</define>

<define element="id" syntax="quoted">
  <doc> identifier. </doc>
  <action>&content;</action>
</define>

<define element="fn">
  <doc> Filename <em>(or URL)</em> enclosed in double quotes.
  </doc>
  <action><a href="&content;">"&content;"</a></action>
</define>

<define element="line" syntax="empty">
  <doc> Marks the start of a source line.  Note that both
	``<code>&lt;line&nbsp;/&gt;</code>'' and its expansion occupy exactly
	eight characters, so they don't mess up tabs if you're looking at the
	marked-up source. <red> Eventually we want a variable that turns line
	numbers on and off. </red>
  </doc>
  <note> <tag>line</tag> <em>must be empty</em> -- if it wraps the line in its
	 content, it will foul up markup that straddles a line boundary.
  </note>
  <action><hide><set name="VAR:line">
		     <numeric op="sum" pad="6">1 <get name="VAR:line" />
	             </numeric></set>
	  </hide><font color="#666699"><get name="VAR:line" /></font>  <hide>
  </hide></action>
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

<define element="rem">
  <action><font color="red">&content;</font><hide>
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

