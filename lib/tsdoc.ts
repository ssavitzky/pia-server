<!doctype tagset system "tagset.dtd">
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
<!-- Contributor(s): steve@rii.ricoh.com                                    -->
<!-- ====================================================================== -->

<tagset name="tsdoc" parent="HTML" tagset="xhtml">

<h1>Tagset Documentation Tagset</h1>

<doc> This file contains the XML definition for a Tagset that can be used
      for formatting Tagset files.
</doc>

<doc> Tagset definition files, their syntax, and their usual representation,
      are documented in <a href="tagset.html">tagset.html</a>.
</doc>

<h2>Definition Elements</h2>

<define element="tagset">
  <doc> &lt;tagset&gt; is the top-level element.  We have to transform it into
	an &lt;html&gt; element with appropriate head and body content. 
  </doc>
  <action><html><head>
    <title>Tagset documentation for ''&attributes:name;''</title>
    </head>
    <body bgcolor="white"><!-- AUTOMATICALLY GENERATED -- DO NOT EDIT!!! -->
      <h1>Tagset documentation for <code>&attributes:name;</code></h1>
      <h3>&lt;tagset <repeat list="&attributes;"> &li; </repeat>&gt;</h3>
	<blockquote>
	  <em> This documentation was automatically generated
	       <if><get name="PIA:root"/>
		   <then> on-the-fly by a PIA server using the
			  <code>tsdoc</code> tagset.  Don't forget to reload
			  if you change the tagset.
		   </then>
		   <else> by the <code>tsdoc</code> tagset, running offline
			  under the <code>process</code> command.
		   </else>
	       </if>
	  </em>
	</blockquote>
	<?-- Because this tag is not quoted, we have already processed the
	     content by now.  That means we can put in a table of contents at
	     this point.
          --?>
<dl>
  <dt> <b>Contents:</b> (parentheses delimit sub-elements) </dt>
  <dd> <repeat><foreach><get name="VAR:elements"/></foreach>&li; </repeat>
  </dd>
  <dt> <b>Element Index:</b> </dt>
  <dd> <repeat>
         <foreach><text op="sort"><get name="VAR:elements"/></text></foreach>
         <if><test match="[()]">&li;</test><else> &li; </else></if>
       </repeat>
  </dd>
  <dt> <b><a href="&DOC:name;?code=y">[view code]</a></b>
  </dt>
  <dd> The link above will show the contents of &lt;action&gt; and
       &lt;value&gt; elements if they exist.  The original formatting may not
       be preserved precisely.
  </dd>
</dl>

      <get name="content"><font color="red">This tagset is empty!</font></get>
      <h2>&lt;/tagset&gt;</h2>
    </body></html>
  </action>
</define>

<define element="define" quoted="quoted">
  <action> <if>&attributes:element;
	       <then><h3><a name="&attributes:element;">
		      <font color="blue"><code>&lt;&attributes:element;&gt;
		      </code></font></a></h3>
		     &nbsp;&nbsp;<font color="blue">
	             <strong>&lt;define</strong>
	             <repeat list="&attributes;"> &li; </repeat><hide>
		     <set name="VAR:current-element">&attributes:element;</set>
		     </hide><strong>&gt;</strong></font><br/>
		     <set name="VAR:elements"><get name="VAR:elements"/>
<if>&VAR:sub-element;
    <then><small><a href="#&attributes:element;">&attributes:element;</a></small></then>
    <else><a href="#&attributes:element;">&attributes:element;</a></else>
</if></set>
	       </then>
           <elsf>&attributes:attribute;
	       <then><a name="&VAR:current-element;.&attributes:attribute;"
		      ><strong>&lt;define</strong></a>
	             <repeat list="&attributes;"> &li; </repeat><hide>
		     </hide><strong>&gt;</strong> <br/>
	       </then></elsf>
           <elsf>&attributes:entity;
	       <then><a name="&attributes:entity;"
		      ><strong>&lt;define</strong></a>
	             <repeat list="&attributes;"> &li; </repeat><hide>
		     </hide><strong>&gt;</strong> <br/>
	       </then></elsf>
	   <else><strong>&lt;define</strong>
	             <repeat list="&attributes;"> &li; </repeat><hide>
		     </hide><strong>&gt;</strong> <br/>
           </else>
           </if>
	   <if>&content;
	       <then><blockquote><expand>&content;</expand></blockquote></then>
	   </if>

  </action>
  <doc> Basically we're turning the declaration itself into a sort of header,
	except that we're assuming that headers already exist, so we just
	emphasize it.
  </doc>
</define>

<define element="undefine">
  <action>
    <p> <em>(unimplemented: <repeat list="&attributes;"> &li; </repeat>) </em>
    </p>
  </action>
  <doc> Just by changing a <tag>define</tag> to <tag>undefine</tag> one can
	make it disappear from the documentation.
  </doc>
</define>

<define element="action" quoted="quoted">
  <action>
    <if>&FORM:code;
	<then><pre> &lt;action&gt;<protect result
	       markup>&content;</protect> &lt;/action&gt; </pre></then>
	<else><p>
		<if>&DOC:name;
		    <then><a href="&DOC:name;?code=y#&VAR:current-element;">
		            [action]</a> 
		    </then>
		</if></p>
	</else>
    </if>
  </action>
</define>

<define element="value" quoted="quoted">
  <action>
    <if>&FORM:code;
	<then><pre> &lt;value&gt;<protect result markup>&content;</protect> &lt;/value&gt; </pre></then>
	<else><p>
		<if>&DOC:name;
		    <then><a href="&DOC:name;?code=y#&VAR:current-element;">
		      [value]</a>
		    </then>
		</if></p>
	</else>
    </if>
  </action>
</define>

<define element="doc">
  <action>  &content;  </action>
</define>

<define element="note">
  <action>
  <dl>
    <dt> <strong>Note: </strong> <if>&attributes;
	 <then>(&attributes;)</then></if>
    </dt>
    <dd> <em>&content;</em></dd>
  </dl></action>
</define>

<h2>Additional Constructs:</h2>

<define element="cvs-id">
  <action><h5>&content;</h5></action>
</define>

<define element="tag">
  <action><code>&lt;&content;&gt;</code></action>
</define>

<define element="ul" quoted="quoted">
  <action><set name="VAR:elements"><get name="VAR:elements"/> ( </set>
  	  <ul><expand>&content;</expand></ul>
  	  <set name="VAR:elements"><get name="VAR:elements"/> ) </set>
  </action>
</define>

<define element="li" quoted="quoted">
  <action><set name="VAR:sub-element">yes</set>
  	  <li><expand>&content;</expand></li>
  	  <set name="VAR:sub-element"> </set>
  </action>
</define>


<hr>
<b>Copyright &copy; 1998 Ricoh Innovations, Inc.</b><br>
<b>$Id: tsdoc.ts,v 1.5 2001-01-11 23:37:02 steve Exp $</b><br>
</tagset>

