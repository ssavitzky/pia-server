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
<!-- Contributor(s): steve@rsv.ricoh.com                                    -->
<!-- ---------------------------------------------------------------------- -->

<tagset name=ehtml parent=HTML include="basic debug" recursive>
<cvs-id>$Id: ehtml.ts,v 1.2 1999-12-17 23:59:19 bill Exp $</cvs-id>

<h1>EHTML Tagset</h1>

<doc> This tagset consists of the primitive operations from the <a
      href="basic.html">basic</a> tagset, the additional non-primitive
      convenience functions of <a href="xxml.html">xxml</a>, and
      HTML syntax.  It is intended for use in Extended HTML documents.

  <p> Unlike the <a href="xhtml.html">xhtml</a> tagset, <code>ehtml</code>
      uses HTML syntax rather than XML.  Attributes may be minimized, quotes
      are optional, and end tags may be omitted in some cases.  This allows
      you to process HTML files prepared using standard methods, without
      suffering through a huge pile of undesirable warning messages.  It's
      especially useful for HTML coming from outside the PIA, for ``legacy''
      documents, and for HTML prepared in the usual way using a text editor.
  </p>
</doc>

<h2>Additional tags for HTML</h2>


<h3>Form Elements</h3>

<doc> The following elements are used for simplifying the construction of
      forms.
</doc>

<define element="checkbox" empty>
  <doc> This expands into an <tag>input type="checkbox"</tag> element; the
	advantage is that whether it is checked by default can depend on the
	value of an attribute.
  </doc>
  <define attribute="value" optional>
    <doc> If this attribute is present and has a true value, the checkbox will
	  be checked.
    </doc>
  </define>
  <action>
    <if>&attributes:value;
	<then><input type="checkbox" name="&attributes:name;" checked /></then>
	<else><input type="checkbox" name="&attributes:name;" /></else>
    </if>
  </action>
</define>



<define element="radio" >
  <doc> This expands into an <tag>input type="radio"</tag> element; the
	advantage is that whether it is checked by default can depend on the
	value of the content.
  </doc>

  <action>
    <if>&content;
	<then><input type="radio" name="&attributes:name;"
                  value="&attributes:value;" checked /></then>
	<else><input type="radio" name="&attributes:name;"
                  value="&attributes:value;"  /></else>
    </if>
  </action>
</define>



<define element="option" >
  <doc> This expands into an <tag>input type="option"</tag> element; the
	advantage is that whether it exists can depend on the content.
  </doc>

  <action><text trim="yes">
    <if>&content;
	<then>
           <option>&content;</option>
        </then>
    </if>
  </text></action>
</define>

<define element="weekday" handler="handler">
   <doc> If given a legal date, this expands into the weekday of that date,
	 unless other attributes specify other behavior, such as the name of
	 the month, the last date in the month, or the date (possibly
	 negative!) on which to start printing a calendar sunday. There must
	 be either of the following subelement sets contained:
	 <tag>unixdate</tag>, containing a unix-style long int in seconds
	 since 1970, or a set of three <tag>month</tag>, <tag> date</tag>, and
	 <tag>year</tag>.
   </doc>
  <define attribute="monthname" optional>
    <doc> If present and true, the tag expands to the month name.
    </doc>
  </define>
  <define attribute="startsun" optional>
    <doc> If present and true, the tag expands to the date (possibly negative)
	  on which to layout a sunday so that the actual first day of the
	  month will appear on its proper weekday.
    </doc>
  </define>
  <define attribute="lastdate" optional>
    <doc> If present and true, the tag expands to the last date of the given
	  month (e.g. 28, 29, 30, or 31).
    </doc>
  </define>

</define>

</tagset>









