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

<tagset name=xhtml parent=basic include="debug HTML" recursive>
<cvs-id>$Id: xhtml.ts,v 1.2 1999-12-17 23:59:20 bill Exp $</cvs-id>

<h1>XHTML Tagset</h1>

<doc> This tagset consists of the primitive operations from the <a
      href="basic.html">basic</a> tagset, the additional non-primitive
      convenience functions of <a href="xxml.html">xxml</a>, and
      HTML syntax.  It is intended for use in eXtended HTML documents.
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

<define element="date" handler="date">
   <doc> <p>If given a legal date, this expands into a Unix-like date string,
	 unless other attributes specify other behavior, such as the name of
	 the month, the last date in the month, or the date (possibly
	 negative!) on which to start printing a calendar sunday.  The
	 content, if present, must be one of the following:
	 <tag>seconds</tag>, containing a unix-style time in seconds since
	 1970, <tag>mstime</tag>, containing a Java-style time in
	 milliseconds, <tag>julian</tag> containing an astronomer's Julian
	 day, or a set of three <tag>month</tag>, <tag>day</tag>, and
	 <tag>year</tag>.  If no content is present, the current date and time
	 are assumed.</p>

	 <p>The content sub-elements <tag>hour</tag>, <tag>minute</tag>, and
	 <tag>second</tag> are optional.  Do not confuse <tag>second</tag>
	 with  <tag>seconds</tag>!</p>
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
  <define attribute="lastday" optional>
    <doc> If present and true, the tag expands to the last date of the given
	  month (e.g. 28, 29, 30, or 31).
    </doc>
  </define>
  <define attribute="format" optional>
    <doc> If present, the value is a format (as defined by
	  <code>java.text.SimpleDateFormat</code>). 
    </doc>
  </define>

</define>

<define element="weekday" handler="date">
  <doc> Identical to <tag>date</tag> except that the default, if no attributes
	are given that specify the output format, the result is the name of
	the day of the week.
  </doc>
</define>

</tagset>









