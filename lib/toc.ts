<!DOCTYPE tagset SYSTEM "tagset.dtd">
<!-- ====================================================================== -->
<!-- The contents of this file are subject to the Ricoh Source Code Public  -->
<!-- License Version 1.0 (the "License"); you may not use this file except  -->
<!-- in compliance with the License.  You may obtain a copy of the License  -->
<!-- at http://www.risource.org/RPL                                         -->
<!--                                                                        -->
<!-- Software distributed under the License is distributed on an "AS IS"    -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.        -->
<!-- See the License for the specific language governing rights and         -->
<!-- limitations under the License.                                         -->
<!--                                                                        -->
<!-- This code was initially developed by Ricoh Silicon Valley, Inc.	    -->
<!-- Portions created by Ricoh Silicon Valley, Inc. are Copyright (C)       -->
<!-- 1995-2000.  All Rights Reserved.                                       -->
<!--                                                                        -->
<!-- Contributor(s):  steve@rsv.ricoh.com                                   -->
<!-- ====================================================================== -->

<tagset name="toc" parent="HTML" tagset="xhtml">
<doc>
<p> This tagset is used for attaching a table of contents to an HTML document.
    The body is processed without ever expanding it, so that an active
    document can be given a TOC without mangling it.
</p>
<p> Tags Defined: </p>
<ul>
  <li> &lt;body&gt;
  </li>
  <li> &lt;h2&gt;, &lt;h3&gt;<br />
       Only the &lt;h2&gt; and &lt;h3&gt; elements are indexed.
  </li>
  <li> &lt;toc&gt;<br />
       Table of contents.  The content of the element is replaced by the new
       table of contents.  NOTE: The toc tag must occur before the first
       indexable &lt;h2&gt; element.  This should really be a &lt;div&gt;
       tag. 
  </li>
  <li> &lt;a&gt; <br />
       defined so that the section-number anchors needed for the
       table of contents can be renumbered when rebuilding the TOC.
  </li>
</ul>
</doc>

<h2>Entity Definitions</h2>

<doc> These are easily overridden in the document itself. </doc>

<h2>&lt;body&gt;</h2>

<define element="body" syntax="quoted">
  <doc> Insert numbers into the <code>h2</code> and <code>h3</code> headings;
	replace any <code>toc</code> element with the new table of contents.
  </doc>
  <action mode="replace-content"><hide>
    <set name="VAR:toc"> </set>
    <set name="VAR:h2">0</set>
    <set name="VAR:h3">0</set>
    <expand>&content;</expand><!-- compute the table of contents -->
    <set name="VAR:h2">0</set>
    <set name="VAR:h3">0</set>
    </hide><expand>&content;</expand><hide>
  </hide></action>
</define>

<define element="h2">
  <doc> Handle an <code>h2</code> heading.
  </doc>
  <action mode="replace-content"><hide>
    <set name="VAR:h2"><numeric op="sum">1 <get name="VAR:h2"/></numeric></set>
    <set name="VAR:h3">0</set>
    <let name="delinked">
      <repeat><foreach>&content;</foreach><text op="trim">
	 <logical op="or">
	   <extract><from>&li;</from> a <content /></extract>
	   &li;
	 </logical>
      </text> </repeat>
    </let>
    <set name="VAR:toc"><get name="VAR:toc"/>
 <br />&nbsp;&nbsp;&nbsp;&nbsp;<a href="#section-&h2;">&h2;: <text op="trim">
	&delinked;
	</text></a>
    </set>
    </hide> <a name="section-&h2;">&h2;:</a> <text op="trim">&content;</text><hide>
  </hide></action>
</define>

<define element="h3">
  <doc> Handle an <code>h3</code> heading.
  </doc>
  <action mode="replace-content"><if>
	<test op="zero">&VAR:h2;</test>
	<then>&content;</then>
	<else><hide>
	      <let name="delinked">
		<repeat><foreach>&content;</foreach><text op="trim">
		   <logical op="or">
		     <extract><from>&li;</from> a <content /></extract>
		     &li;
		   </logical>
		</text> </repeat>
	      </let>
	      <set name="VAR:h3"><numeric op="sum">1 <get name="VAR:h3"/></numeric></set>
	      <set name="VAR:toc"><get name="VAR:toc"/>
  <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size="-1"><a href="#section-&h2;.&h3;">&h2;.&h3;: <text op="trim">&delinked;</text></a></font>
	      </set>
	      </hide><a name="section-&h2;.&h3;">&h2;.&h3;:</a> <text op="trim">&content;</text></else>
  </if></action>
</define>

<define element="toc" syntax="quoted">
  <doc> Output a table of contents.
  </doc>
  <action mode="replace-content">
&VAR:toc;
  </action>
</define>

<define element="a">
  <doc> If this is a name anchor for a section number, delete it.
  </doc>
  <action><if><test match="section-">&attributes:name;</test>
    <else><if>&attributes:name;
	<then><a name="&attributes:name;">&content;</a></then>
	<else><a href="&attributes:href;">&content;</a></else>
        </if></else>
    </if></action>
</define>

<em>$Id: toc.ts,v 1.4 2000-07-21 20:22:59 steve Exp $</em>
</tagset>
