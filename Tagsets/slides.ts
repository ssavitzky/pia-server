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
<!-- Contributor(s):                                                        -->
<!-- ====================================================================== -->

<tagset name="slides" parent="xhtml" recursive="yes">
<doc>
<p> This tagset is used for generating ``slide'' presentations from ordinary
    HTML documents.  The original document contains &lt;slide&gt; elements,
    each of which may contain an &lt;h2&gt; element as its caption along with
    some text.  The document is semi-readable as-is.
</p>
<p> This tagset reformats each ``slide'' as a table with suitable decoration,
    making it look like a slide in a PowerPoint presentation.  Each slide
    contains forward, backward, and table-of-contents links. 
</p>
<p> This tagset accepts a query string with
    <code>slide=<em>starting-slide</em></code> and
    <code>n=<em>number-of-slides</em></code> to show.  Forward and backward
    links work properly in this case, but links in (and to) the TOC are
    currently broken.  The main use of the query is for printing, in order to
    force an integral number of slides on a page.
</p>
<p> Tags Defined: </p>
<ul>
  <li> &lt;slide&gt;&lt;h2&gt;slide caption&lt;/h2&gt; content &lt;/slide&gt;
	<br />	The slide tag is designed so that the ``rough draft'' of
		a presentation is still a valid, readable HTML file.
  </li>
  <li> &lt;start&gt;text&lt;/start&gt;
	<br />	A link to the first slide.  Use this to skip any unwanted
		front matter and get things lined up right.
  </li>
  <li> &lt;end&gt;text&lt;/end&gt;
	<br />	An anchor for the last slide's ``next'' link.
  </li>
  <li> &lt;toc&gt;text&lt;/toc&gt;
	<br />	Table of contents
  </li>
</ul>
</doc>
<note author=steve>
<p> Things to do:
<ul>
  <li> &lt;slide&gt; needs an optional ID attribute.
  </li>
  <li> Next/Prev links would probably work better if they referenced the table
       by a 1-pixel-high top row.  ID attr on the table doesn't work.
  </li>
</ul>
</note>

<h2>Entity Definitions</h2>

<doc> These are easily overridden in the document itself. </doc>

<h3>Dimensions</h3>
<define entity=hh>
  <doc> The height of the main portion of a table.  This needs to be tweeked
	for the screen size of the presentation device; the default is 300,
	which is correct for a large-screen TV pretending to be a 640x480
	monitor, and a browser running in fullscreen mode.
    <p> For 800x600, e.g. a Magio laptop, use <br />
	<code>&lt;set name=VAR:hh&gt;445&lt;/set&gt;</code>
    </p>
  </doc>
  <value>370</value>
</define>

<define entity="slideH">
  <doc> The height of a slide.  This needs to be tweeked for the screen size
	of the presentation device; the default is correct for a large-screen
	TV pretending to be a 640x480 monitor.
    <p> For 800x600, e.g. a Magio laptop, use <br />
	<code>&lt;set name=VAR:slideH&gt;445&lt;/set&gt;</code>
    </p>
  </doc>
  <value>450</value>
</define>

<h3>Colors</h3>
<doc> These define the colors of various portions in the table that represents
      a slide.
</doc>

<define entity=topBg><value>#669966</value></define>
<define entity=topFg><value>black</value></define>
<define entity=leftBg><value>#669966</value></define><!-- #c40026 -->
<define entity=leftFg><value>blue</value></define>
<define entity=leftW><value>100</value></define>
<define entity=ulBg><value>lightblue</value></define>
<define entity=ulFg><value>#c40026</value></define>
<define entity=mainBg><value>white</value></define>
<define entity=mainFg><value>black</value></define>

<h3>Logos and Buttons</h3>

<define entity="icons">
  <doc> This is the leading path for graphics.  It should begin and end with
	a slash if necessary.
  </doc>
  <value>/Icon/</value>
</define>

<define entity="logo">
  <doc> This appears in the upper-left-corner of each slide.  It needs to be
	almost exactly the same height as the text, because it is used as the
	anchor for the ``next slide'' button.
    <p> This presently assumes you're presenting via a PIA.  If you want to
	produce stand-alone HTML you'll have to move it into the same
	directory as your slides. 
    </p>
  </doc>
  <value><img src="&icons;logo16.png" height=16 width=16 alt="&nbsp;"></value>
</define>
<define entity="trans1x1">
  <doc> <p> A transparent 1x1 pixmap for use as a spacer or placeholder.
    </p>
  </doc>
  <value><img src="/Icon/trans1x1.png"/></value>
</define>
<define entity=toPrev><value>&lt;&lt;</value></define>
<define entity=toNext><value>&gt;&gt;</value></define>
<define entity=noPrev><value>&nbsp;&nbsp;</value></define>
<define entity=noNext><value>&nbsp;&nbsp;</value></define>
<define entity=toToc><value>^^</value></define>

<h3>Default text</h3>
<define entity="subCaption">
  <doc> the ``subCaption'' is the text along the <em>bottom</em> line of each
	slide.  If your presentation is long, you may want to put your section
	caption in here. 
    <p> Usage: <br />
	<code>&lt;set name=subCaption&gt;text for bottom line&lt;/set&gt;</code>
    </p>
  </doc>
  <value>Ricoh Innovations, Inc.</value>
</define>

<h2>&lt;Slide&gt;</h2>

<define element="xxx">
  <doc> This is a sample row using a 1x1 transparent gif.  Unfortunately it
	works correctly only if cellpadding=0, which introduces other problems.
  </doc>
<action>
<tr height=1 nowrap nobr cellpadding=0 bgcolor="&topBg;" 
       ><td colspan=3><a
        name="&slide;"><img src="&icons;/trans1x1.gif"></a></td></tr>
</action>
</define>

<define element="slide" parent="body">
  <doc>	This is the element that defines a ``slide''.   Usage is something
	like:
	<pre>
	&lt;slide&gt; &lt;h2&gt;Document Processing&lt;/h2&gt;
	   &lt;ul&gt;
	      &lt;li&gt; Input: a document
	   &lt;/ul&gt;
	&lt;/slide&gt;
	</pre>
	The &lt;h2&gt; element is <em>required</em>, since it provides the
	caption for the slide.  
  </doc>
  <note> This represents a major change from the way slides used to be done!
	 We used to have a table with cells for top left, top, left side,
	 bottom left, and bottom.  We now have a single left edge, which
	 allows the use of a "binding"-type background that tiles vertically.
  </note>	
<action>
<hide><!-- first time through we initialize the variables -->
  <if>&VAR:slide;<else><set name="VAR:slide">0</set></else></if>
  <if>&VAR:next;
      <else><set name="VAR:next"><numeric sum>1 &slide;</numeric></set></else>
  </if>
  <if>&VAR:prev;<else><set name="VAR:prev"> </set></else></if>
  <if>&VAR:slidelist;<else><set name="VAR:slidelist"> </set></else></if>
  <if>&FORM:slide;
      <then>
	<set name="VAR:start">&FORM:slide;</set>
	<set name="VAR:stop">
	     <numeric op="sum">&FORM:slide;
		<get name="FORM:n">1</get> -1 </numeric>
	</set>
	<set name="VAR:nextQ">&DOC:name;?slide=&next;</set>
	<set name="VAR:prevQ">&DOC:name;?slide=&prev;</set>
	<if>&FORM:n;
	  <then>
	    <set name="VAR:nextQ">&DOC:name;?slide=&next;&amp;n=&FORM:n;</set>
	    <set name="VAR:prevQ">&DOC:name;?slide=&prev;&amp;n=&FORM:n;</set>
	  </then>
	</if>
      </then>
      <else>
	<set name="VAR:nextQ"></set>
	<set name="VAR:prevQ"></set>
      </else>
  </if>
</hide>
<if><logical op="and">
	<get name="FORM:slide" />
	<logical op="or">
	  <test negative><numeric op="diff">&slide; &start;</numeric></test>
	  <test negative><numeric op="diff">&stop; &slide;</numeric></test>
	</logical>
    </logical>
    <then><make type="comment">slide &slide; omitted</make></then>
    <else><make type="comment">begin slide &slide;</make>
<table width="100%" cellspacing=0 cellpadding=5 border=0>
  <tr><td bgcolor="&leftbg;" width="&leftW;" valign="top" align="center"
          rowspan="4"><br />
        <if>&prev;
  	      <then><a href="&prevQ;#&prev;">&toPrev;</a></then>
  	      <else>&noPrev;</else></if><?--
        --?><if><test exact match=TOC>&label;></test>
	      <else><a href="#TOC">&nbsp;&slide;&nbsp;</a></else></if><?--
        --?><if>&next;
	      <then><a href="&nextQ;#&next;">&toNext;</a></then>
	      <else>&noNext;</else></if><br />
	<a href="#TOC">contents</a><br />
	<a href="#0">start</a><br />
      </td>
      <th align=left bgcolor="&mainBg;" fgcolor="&topFg;">
	    <a name="&slide;">&nbsp;</a><if>
	    <get name=label>
	    <then><a name="&label;">&nbsp;<get name="caption" /></a></then>
	    <else>&nbsp;<get name="caption" /></else></if>
      </th>
  </tr>
  <!-- this green stripe occupies 11 pixels vertically -->
  <tr><td bgcolor="&topBg;" height="3">&trans1x1;</td>
  </tr>
  <tr><!-- this table cell contains the actual content of the slide -->
      <td bgcolor="&mainBg;" valign="top" height="&hh;">
&content;
      </td>
  </tr>  
  <tr><td align=right valign=bottom bgcolor="&mainBg;"
	      width="100%"><em>&subCaption;</em>   <expand>&logo;</expand>
      </td>
  </tr>
</table><!-- end slide -->
<p /> </else>
</if><hide>
    <if><test zero>&slide;</test><then>
        <else><set entity name="slidelist"><get entity name="slidelist" />
<li> <a href="#&slide;">&caption;</a></li></set></else></if>
    <set name=prev>&slide;</set>
    <set name=slide><numeric sum digits=0>1 &slide;</numeric></set>
    <set name=next><numeric sum digits=0>1 &slide;</numeric></set>
    <set name=label> </set>
    <set name=caption> </set>
</hide>
</action>
</define>
     
<dl>
  <dt><!-- this is here just to repair indentation -->
  </dt>
</dl>

<h2>Auxiliary Tags</h2>

<define element=end><action><a name="&slide;">&content;</a></action></define>
<define element=start>
  <action>
    <table width='100%'>
      <tr>
	<td align=left><a href="#TOC">TOC</a></td>
	<td align=right><a href="#0">&content; &toNext;</a></td>
      </tr>
    </table>
  </action>
</define>

<define element="h1">
  <doc> The top-level heading in the file (typically there is only one)
	becomes the default subCaption.  This may not be exactly what you
	want, but is easily overridden.  It also generates a link with the
	text ``Start here'', linked to the top of the first slide.  
  </doc>
<action>
<hide><!-- first time through we initialize the variables -->
  <set name=VAR:subCaption>&content;</set>
  <if>&VAR:slide;
      <else><set name=VAR:slide>0</set></else></if>
  <if>&VAR:next;
      <else><set name=VAR:next><numeric sum>1 &slide;</numeric></set></else>
  </if>
  <if>&VAR:prev;
      <else><set name=VAR:prev> </set></else></if>
  <if>&VAR:slidelist;
      <else><set name=VAR:slidelist> </set></else></if>
</hide>
<if><get name="FORM:slide"/>
    <else>
<h1>&content;</h1>
<p> <start>Start here</start>
</p>
    </else>
  </if>
  </action>
</define>
     
<define element=h2><action><set name=VAR:caption>&content;</set></action>
</define>

<?-- the following causes some bogus errors; probably slide isn't defined 
<define element=toc>
  <action><!-- table of contents: -->
<set name=label>TOC</set>
<slide><!-- not working: tagset doesn't seem to be properly recursive. -->
<h2>&content;</h2>
<ol>
  &slidelist;
</ol>
</slide>
</action>
</define>
--?>

<em>$Id: slides.ts,v 1.4 2001-01-11 23:37:00 steve Exp $</em>
</tagset>
