<!DOCTYPE tagset SYSTEM "tagset.dtd" >
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
<!-- Contributor(s):   bill@rsv.ricoh.com                                   -->
<!--                                                                        -->
<!-- ---------------------------------------------------------------------- -->

<tagset name=ExtensionDemo-agent parent=pia-xhtml recursive>
<cvs-id>$Id: ExtensionDemo-xhtml.ts,v 1.1 1999-10-14 22:29:06 bill Exp $</cvs-id>


<define element="todaytag" >
  <doc> This sample tag expands to show today's date </doc>
  <action>
     Today is &dayname; , &monthname; &day; &year;
  </action>
</define>

<define element="myfont">
   <doc>Wraps a user-selected font around text. </doc>
   <action>
      <font size="+4" color="green"><b> &content; </b></font>
    </action>
</define>



</tagset>






