<!DOCTYPE tagset SYSTEM "tagset.dtd">
<!-- ---------------------------------------------------------------------- -->
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
<!-- ---------------------------------------------------------------------- -->

<tagset name="pia-xhtml" parent="xhtml" include="pia-tags" recursive="yes">
<cvs-id>$Id: pia-xhtml.ts,v 1.2 1999-12-16 21:12:53 steve Exp $</cvs-id>

<h1>PIA XHTML Tagset</h1>

<doc> This the version of the XXML tagset used by default in the PIA.  Active
      tags unique to the PIA are actually defined in <code>pia-tags</code>,
      and are shared between <code>pia-xxml</code> and <code>pia-xhtml</code>.
</doc>


<h2>Form-Processing Tags</h2>

Note that we only need these inside the PIA.

<h3>Submit-Forms</h3>
<define element=submit-forms handler="org.risource.pia.handle.submitHandler" >
  <doc> Submit each form in the content to its target.  Results, if any,
	are discarded.
  </doc>
</define>

<h3>Form and its components</h3>

<h4>Sub-elements of <tag>form</tag></h4>
<ul>
  <li> <define element=process parent=form quoted handler>
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

<define entity="blank-170x1">
  <value><img src="/Icon/white170x1.png" width=170 height=1
		alt=" "></value>
</define>
<define entity="blue-dot">
  <value><img src="/Icon/dot-blue.png"
		height=20 width=20 alt="*"></value>
</define>

<define entity="RiSource.org">
  <value><a href="http://www.RiSource.org/"
            ><font color="#c40026" face="Verdana, Arial, Helvetica, sans-serif"
                   >Ri</font><font color="black"
	                           face="Verdana, Arial, Helvetica, sans-serif"
                   ><i>Source.org</i></font></a></value>
</define>

<define entity="RiSource.org.pia">
  <value><a href="http://www.RiSource.org/PIA"
            ><font color="#c40026" face="Verdana, Arial, Helvetica, sans-serif"
                   ><i>PIA</i></font></a></value>
</define>


<h2>Page Components</h2>

<h3>Utility tags for use in page components</h3>
<define element=xa>
  <doc> Either an anchor link or a bold name.  Used on lines that
	contain links to any of several different pages.
  <doc>
  <define attribute=page>
    <doc> The base name of the current page, matched against the URL (href
	  attribute).  If missing, the current binding of "page" is used.
    </doc>
  </define>
  <define attribute=href>
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

<define element="xopt">
  <doc> Output the content if the page attribute matches one of the
	names listed in the pages attribute.  Used for an indicator
	(e.g, a blue dot) on lines that contain links to several
	different pages.
  <doc>
  <define attribute="page">
    <doc> the base name of the current page, matched against the
	  <code>pages</code> attribute.  If missing, the current binding of
	  "page" is used. 
    </doc>
  </define>
  <define attribute=pages required>
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
	<then><get name="content"/></then>
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

<define element="verboseForm" quoted>
  <doc> Expand the content if the AGENT:verboseForms option is set.
  </doc>
  <action><if><get name="AGENT:verboseForms"/>
	      <then><expand><get name="content"/></expand></then></if>
  </action>
</define>

<define element="verbosePage" quoted>
  <doc> Expand the content if the AGENT:verbosePage option is set.
  </doc>
  <action><if><get name="AGENT:verbosePages"/>
	      <then><expand><get name="content"/></expand></then></if>
  </action>
</define>

<define element="agentOption" empty>
  <doc> Set an agent option from a form.
  </doc>
  <define attribute="name" required>
    <doc> The name of the option.  Must be the same as the name of the
	  entity in the AGENT namespace and the <tag>input</tag> element's
	  <code>name</code> attribute.
    </doc>
    <note author="steve">
	The action for this element exhibits the <tag>do</tag> in its most
	perverse form, computing the <code>name</code> attributes for both
	<tag>get</tag> and <tag>set</tag>.  Note the use of <tag>let</tag>
	in the inner <tag>do</tag>, absolutely essential in order to ensure
	that the assignment happens locally.
    </note>
  </define>
  <action>
    <if><get name="FORM:"/>
	<then><do name="set">
		  <let name="name">AGENT:<get name="attributes:name"/></let>
	          <do name="get">
		      <let name="name">FORM:<get name="attributes:name"/></let>
		  </do>
	      </do>
        </then>
    </if>
  </action>
</define>

<define element="agentLock" empty>
  <doc> Set an agent lock from a form.  Locks can be set, but cannot be
	cleared.
  </doc>
  <define attribute="name" required>
    <doc> The name of the option.  Must be the same as the name of the
	  entity in the AGENT namespace and the <tag>input</tag> element's
	  <code>name</code> attribute.
    </doc>
  </define>
  <action>
    <if><do name="get">
	    <set name="name">FORM:<get name="attributes:name"/></set>
	</do>
	<then><do name="set">
		  <set name="name">AGENT:<get name="attributes:name"/></set>
	          LOCKED
	      </do>
        </then>
    </if>
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
<tr><th><a href="/"><img src="/Icon/pia45.png"
			 border=0 width=85 height=45 alt="P I A"></a></th>
    <td valign=bottom>
      <table>
	<tr><th valign=bottom align=left>
	       &RiSource.org;'s Platform for Information Applications</th></tr>
	<tr height=6><td><img src="/Icon/rule.png" height=6 width=469></td></tr>
      </table>
    </td>
</tr>
</table>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th valign=top width=170>&nbsp;</th>
    <th align=right valign=top width=170><get name="ltitle"/>&nbsp; </th>
    <th align=left valign=top><if><get name=title />
	<then><get name=title /></then>
	<else><get name="fileName"/></else></if></th></tr>
<if><get name="attributes:show-date"/><then>
  <tr nowrap nobr>
      <td colspan=2><td><get name="dayName"/>, <get name="year"/>-<get
	name="month"/>-<get name="day"/>, <get name="time"/>:<get
	name="second"/></td></tr>
  </then></if>
</table>
</action>
</define>

<define element=sub-head quoted>
  <doc> A secondary table located immediately under the header.
	Content should consist of additional table rows.
  </doc>
  <define attribute=page>
    <doc> the base name of the page, e.g. <code>index</code> or
	  <code>home</code>.
    </doc>
  </define>
  <action><set name=page><get name="attributes:page"/></set>
<table cellspacing=0 cellpadding=0 border=0>
<tr><th align=center valign=center nowrap width=170
        ><br/><include src=insert> </include></th>
    <td>
    <table cellspacing=0 cellpadding=0 border=0>
      <tr><th align=left nowrap width=170><get name="blank-170x1"/></th>
	  <td><br /></td></tr>
      <tr><th align=right>
            <xopt pages="home index help options"><get name="blue-dot"/></xopt>
	  <td><xa href="home">Home</xa>
    	      <xa href="-">Index</xa>
	      <if><status item="exists" src="help.xh" />
    	          <then><xa href="help">Help</xa></then></if> 
	      <if><status item="exists" src="help.xh" />
	          <then><xa href="options">Options</xa></then></if> 
	  </td>
      </tr>
      <expand><get name="content"/></expand>
  </table></td>
</tr>
</table>
  </action>
</define>


<define element=nav-bar>
  <doc> A navigation bar, usually placed just above the copyright notice in
	the footer.  Usually fits in a single line.  Content is whatever you
	want to put after the standard start.
  </doc>
  <action>
<a href="/">PIA</a> || <a href="/~Admin">Admin</a> ||
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
          <xlink text="/data/"><get name="locPath"/>/DATA/</xlink>
	</then></if>
  </action>
</define>

<define element=footer empty>
  <doc> This expands into a standard footer, including a ``navigation bar''.
	Go to some lengths to extract the year the file was modified from the
	cvs id.
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<nav-bar/>
<hr>
<set name=dateIndex><if><test match='Id:'><get name="attributes:cvsid"/></test>
    <then>3</then><else>2</else></if></set>
<set name=myear><subst match="/.* " result=", "><extract>
    <from><get name="attributes"/></from>
    <name>cvsid</name><eval/><text split><get name="list"/></text>
    <nodes><get name="dateIndex"/></nodes>
    </extract></subst> </set>
<b>Copyright &copy; <get name="myear"/> Ricoh Silicon Valley.</b>
   Open Source at &lt;<b>&RiSource.org;/&RiSource.org.pia;</b>&gt;.<br>
<em><get name='attributes:cvsid' /></em>
<a href="/Agents/View/source?url=&docPath;">view source</a>
  </action>
</define>

<define element=short-footer empty>
  <doc> This expands into a short-form footer: just the CVS id and copyright
	notice. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<hr>
<set name=dateIndex><if><test match='Id:'><get name="attributes:cvsid"/></test>
    <then>3</then><else>2</else></if></set>
<set name=myear><subst match="/.* " result=", "><extract>
    <from><get name="attributes"/></from>
    <name>cvsid</name><eval/><text split><get name="list"/></text>
    <nodes><get name="dateIndex"/></nodes>
    </extract></subst> </set>
<b>Copyright &copy; <get name="myear"/> Ricoh Silicon Valley</b>.
   Open Source at &lt;<b>&RiSource.org;/&RiSource.org.pia;</b>&gt;.<br>
<em><get name='attributes:cvsid' /></em>
<if><status src="/Agents/View/source.xh" item="exists"/>
    <then><a href="/Agents/View/source?url=&docPath;">view source</a></then>
</if>
  </action>
</define>

<define element=inc-footer empty>
  <doc> This expands into a tiny footer for include files. 
  </doc>
  <define attribute=cvsid>
    <doc> The CVS id string of the file.
    </doc>
  </define>
  <action>
<set name=dateIndex><if><test match='Id:'><get name="attributes:cvsid"/></test>
    <then>3</then><else>2</else></if></set>
<set name=myear><subst match="/.* " result=", "><extract>
    <from><get name="attributes"/></from>
    <name>cvsid</name><eval/><text split><get name="list"/></text>
    <nodes><get name="dateIndex"/></nodes>
    </extract> </subst></set>
<set name=incfn><extract><from><get name="attributes"/></from>
    <name>cvsid</name><eval/>
    <text split><get name="list"/></text> 0
    </extract><extract><from><get name="attributes"/></from>
    <name>cvsid</name><eval/>
    <text split><get name="list"/></text>
    <nodes><numeric difference><get name="dateIndex"/> 2</numeric>
           <numeric difference><get name="dateIndex"/> 1</numeric>
    </nodes></extract></set>
<h6 align=right><get name="incfn"/> &copy; <get name="myear"/>
    Ricoh Silicon Valley</h6>
  </action>
</define>

</tagset>

