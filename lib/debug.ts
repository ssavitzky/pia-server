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
<!--                 bill@rsv.ricoh.com                                    -->
<!-- ---------------------------------------------------------------------- -->

<tagset name=debug parent=HTML include=basic recursive>
<cvs-id>$Id: debug.ts,v 1.1 1999-12-14 18:28:32 steve Exp $</cvs-id>

<h1>DEBUG Tagset</h1>

<doc> This tagset contains tags which are useful in debugging active pages,
      but which inforware creators might want to remove from finished pages.
</doc>


<define element='show-errors'>
  <doc> This element expands to show (in crimson) the list of all accumulated
        errors in processing the document up to this point.
  </doc>
  <action>
   <if>&ERROR;
    <then>
    <font color="crimson">
    <h2>Errors:</h2> <br>
      &ERROR; </font>
    </then>
    </if>
  </action>
</define>

<define element="pretty" handler>
   <doc> This tag expands to pretty-print its contents with depth-dependent
         indentation and text color, with each element on a separate line.  If
         the contents are enclosed in a <code>&lt;protect></code> tag, the
         display of those contents is effectively the same XHMTL as the
         original document, and thus can replace the original document
         (e.g. by cut-and-paste)... except for tags or substitutions inside
         <code>&lt;pre></code> tags, which will appear with possibly undesired
         new lines and indentations.
   </doc>

   <define attribute="hide-above-depth">
     <doc>If the value of this attribute is an integer, tags and contents with
            depth less than the value will not be shown or processed (except
            for the  <code>&lt;protect></code> tag).
     </doc>
   </define>

   <define attribute="hide-below-depth">
     <doc>  If the value of this attribute is an integer, tags and contents
            with depth greater than the value will not be shown or processed.
     </doc>
   </define>

   <define attribute="hide-below-tag">
     <doc> If the value of this attribute is a tag-name, the contents of such
	   tags will not be shown or processed.
     </doc>
   </define>

   <define attribute="white-tag">
     <doc> If this attribute is present, any start/end tags whose name matches
	    the attribute's value will be shown in white text.
     </doc>
   </define>

   <define attribute="yellow-tag">
     <doc>  Like white-tag, but with yellow text.
     </doc>
   </define>

   <define attribute="nomarkup">
     <doc> If this attribute is present, the output of the pia process will be
	   exactly the same as the input, except with added
	   indentation....what appears in the browswer window will be the same
	   as without the <code>&lt;pretty></code> tag, and the effect of
	   <code>pretty</code> will only appear in the page source.  This mode
	   is ideal for piping pretty'd documents directly into editors or
	   files, without cut-and-paste.
     </doc>
   </define>
</define>


</tagset>









