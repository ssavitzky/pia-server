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
<!-- Contributor(s):   bill softky bill@rsv.ricoh.com                       -->
<!-- ---------------------------------------------------------------------- -->


<tagset name="nonRecursive" parent="/Tagsets/pia-xhtml" recursive="recursive">


<cvs-id>$Id: nonRecursive.ts,v 1.1 2000-02-29 01:12:54 bill Exp $</cvs-id>

<define element="include">
   <doc> Include does nothing, to prevent recursion</doc>
   <action><user-message>USING nonrecursive </user-message></action>
</define>



<define element="expand">
   <doc> Expand does nothing, to prevent recursion</doc>
   <action></action>
</define>

<?-- this needs to be defined in BOTH tagsets, Workflow and nonRecursive --?>
<define element="conditional">
    <doc> A wrapper for an "if" tag which tests whether one attribute
          is greater than another </doc>
    <action>
         <if><test greater="&attributes:threshold;" >
                 <get name="&attributes:input;" /> </test>
            <then>&content;</then>
         </if>
    </action>
</define>


</tagset>
