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
<!-- Contributor(s):   bill@rii.ricoh.com                                   -->
<!--                                                                        -->
<!-- ---------------------------------------------------------------------- -->

<tagset name="LiquidDoc-agent" parent="/Tagsets/pia-xhtml" recursive="yes">
<cvs-id>$Id: LiquidDoc-xhtml.ts,v 1.3 2001-01-11 23:36:57 steve Exp $</cvs-id>

<define element="split-query">
   <doc> Take a query and chops it into key=val pairs with spaces </doc>
   <action><parse>
     <text split="yes" sep="&">
	 <subst match="\?" result="">
             <subst match="&" result=" "><subst match="amp;" result=" ">
	     &content;
	 </subst></subst></subst>
     </text>&</parse>
   </action>
</define>

<define element="prune-query">
   <doc> Tag for removing one kind of entry from a query string </doc>

   <action><parse><text trim="yes"><subst match="&attributes:swapname;=.*\&"
               result="">&content;</subst></text></parse></action>

</define>


<define element="fontwrap">
   <doc>Changes the appearance  of a font according to local
        variables </doc>
   <action>
      <if>&fonttype;
         <then>
            <font color="&myfont;" face="sans-serif">&content;</font>
         </then>
         <else>
            <font color="&myfont;" face="serif">&content;</font>
         </else>
      </if>
   </action>
</define>

<define element="make-state">
   <doc> Constructs a new "now-state" entity from the incoming URL (composed
	 of  old "now-state" and  "change" entities). </doc>

   <action>

<?-- initialize state variables --?>
      <set name="VAR:overcount">X</set>
      <set name="VAR:count">-1</set>
      <set name="VAR:fontcolor">red green  purple black red green purple</set>
      <set name="VAR:fonttype"></set>
      <set name="VAR:keyvals"><split-query>&urlquery;</split-query></set>
      <set name="VAR:nowstate"></set>

      <if> <test not="yes">&FORM:s;</test>
         <then>
            <set name="FORM:s"></set>
         </then>
      </if>
      <if> <test not="yes">&FORM:c;</test>
         <then>
            <set name="FORM:c"></set>
         </then>
      </if>

      <set name="statelist"><subst match="X" result=" ">&FORM:s;</subst></set>
      <br/>

    <?-- If change is an open link, close it by removing from list --?>
    <set name="matched"></set>
    <set name="nowstate">
      <repeat>
        <foreach>&statelist;</foreach>
        <if><test match="&FORM:c;" exact="yes">X&li;</test>
           <then>
              <set name="matched">yes</set>
           </then>
	   <else>
	      &li;
              <set name="VAR:X&li;">open</set>
	   </else>
         </if>
      </repeat>
     </set>

     <set name="nowstate">X<text join="yes" sep="X">&nowstate;</text></set>

     <?-- if link was absent (closed), open it by adding --?>
     <if>        &matched;
        <else>
           <if><get name="FORM:c"/>
              <then>
                 <set name="nowstate">&nowstate;&FORM:c;</set>
                 <set name="VAR:&FORM:c;">open</set>
              </then>
           </if>
        </else>
     </if>

   </action>
</define>

<?-- liquid anchor --?>
<define element="la">
   <doc> Liquid-document reference (analagous to &lt;a&gt;)
         Attribute "lref" gives doc name; attribute "flag" used as name of
         attribute to test for open/closed state.</doc>

   <action>
      <?-- increment count for each tag encountered at this level --?>
      <set name="count"><numeric sum="yes">&count; 1</numeric></set>

      <let name="myfont"><extract>
               <from>&fontcolor;</from>
               0
      </extract></let>
      <let name="fontcolor"><subst match="&myfont;" result="">&fontcolor;
           </subst></let>
      <if>&fonttype;
           <then><let name="fonttype"></let></then>
           <else><let name="fonttype">ital</let></else>
      </if>

      <set name="flagname">&overcount;&count;x</set>
      <set name="flagval"><get name="VAR:&flagname;" /></set>

      <if><test match="open">&flagval;</test>
         <?-- if open, show href to close, plus include content of the ref --?>
         <then>
            <let name="overcount">&overcount;&count;x</let>
            <let name="count">-1</let>
            <a  href="home.xh?s=&nowstate;&c=&flagname;">
                 <font color="&myfont;">&content;</font></a><br/>
            <fontwrap >
            <font color="&myfont;"><blockquote>
               <expand><include src="&attributes:lref;" quoted="yes" /></expand>
            </blockquote></font>
            </fontwrap>
         </then>
         <?-- if closed, just show a regular href to open it--?>
         <else>
            <a href="home.xh?s=&nowstate;&c=&flagname;">&content;</a>
         </else>
       </if>


   </action>
</define>


<?-- liquid comment --?>
<define element="lc" quoted="yes">
   <doc> Liquid-document reference (analagous to &lt;la&gt;)
         Content is document to expand; attribute "flag" used as name of
         attribute to test for open/closed state.</doc>

   <action>
      <?-- increment count for each tag encountered at this level --?>
      <set name="count"><numeric sum="yes">&count; 1</numeric></set>

      <?-- use first color on the list, then shorten the list --?>

      <let name="myfont"><extract>
               <from>&fontcolor;</from>
               0
      </extract></let>
      <let name="fontcolor"><subst match="&myfont;" result="">&fontcolor;
           </subst></let>
      <if>&fonttype;
           <then><let name="fonttype"></let></then>
           <else><let name="fonttype">ital</let></else>
      </if>
      <set name="flagname">&overcount;&count;x</set>
      <set name="flagval"><get name="VAR:&flagname;" /></set>

      <if><test match="open">&flagval;</test>

         <?-- if open, show href to close, plus include content of the ref --?>
         <then>
            
            <let name="overcount">&overcount;&count;x</let>
            <let name="count">-1</let>
            <a   href="home.xh?s=&nowstate;&c=&flagname;">
                 <font color="&myfont;">&attributes:atext;</font></a><br/>
            <fontwrap >
            <font color="&myfont;"><blockquote>
               <expand>&content;</expand>
            </blockquote></font>
            </fontwrap>
         </then>
         <?-- if closed, just show a regular href to open it--?>
         <else>
            <a href="home.xh?s=&nowstate;&c=&flagname;">&attributes:atext;</a>
         </else>
       </if>


   </action>
</define>


</tagset>






