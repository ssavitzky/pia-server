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
<!-- Contributor(s): paskin@rsv.ricoh.com                                   -->
<!-- ---------------------------------------------------------------------- -->


<tagset name=Tutorial-agent parent=xhtml recursive>
<cvs-id>$Id: Tutorial-xhtml.ts,v 1.1 1999-07-13 22:59:17 bill Exp $</cvs-id>

<define entity="mybgcolor">
   <value>white</value>
</define>

<define element="output" >
  <action>
     <br><font color="crimson">
     Sorry, the "output" element has been disabled in the demos for
     security reasons.  To use it, please download and install the PIA
     on your own host. </font><br>
  </action>
</define>

<define element="connect" >
  <action>
     <br><font color="crimson">
     Sorry, the "connect" element has been disabled in the demos for
     security reasons.  To use it, please download and install the PIA
     on your own host. </font><br>
  </action>
</define>



<define element='display'>
  <action>
    <if><get name='content'/><then>
    <html>
      <head><title>Instantiation (full)</title></head>
      <body >
        <table width='100%'>
          <tr><td><expand><parse><get name='content'/></parse></expand></td></tr>
        </table>
        <show-errors/>
      </body>

    </html>
    </then><else>
    <html>
      <head><title>Instantiation (empty)</title></head>
      <body >
      </body>
    </html>
    </else></if>
  </action>
</define>

<define element='edit'>

   <action>
    <!DOCTYPE  html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <html>
      <head><title>Editor</title></head>
      <body bgcolor='#ffffff' link='blue' vlink='blue'>

        <form action='instantiate.xh' method='get' target='instantiation'>
           <table width='100%'>

            <tr>
            <td>
            <table align="center" cellspacing="7">
              <td><a href='intro.xh' target="_top"> Intro</a></td>
              <td><a  href='http://www.risource.org/PIA/Doc/Tagsets/basic.html'
                    target="_top"> Manual</a></td>
              <td><a
                    href='http://www.risource.org/PIA/Doc/Tagsets/basic.html#quick'
                    target="_top"> Active-tag table</a></td>
              <td><a href='home.xh' target="_top">  Demo menu</a></td>
            </table>
            </tr>

            <tr><td>Click  <b>Process</b> to see the PIA server process the
            code below and display the results in 
              the frame above. Try out some modifications, or undo them with
              <b>Restore</b>.</td></tr>

             <tr>
               <td align='center'>
                <table cellspacing="5">
                  <tr>
                    <td><input type='submit' value='Process' /></td>
                   <td><a href='&urlPath;'>Restore</a></td>
                    <td><a href='&attributes:demo-name;' target="_top">
                         About this demo</a></td>
                  </tr>
                 </table> 
                </td>
              </tr>
              <tr valign="bottom"><td align='center'>
              <textarea name='code-text' rows='15' cols='80' wrap='off'>
	        <protect markup='markup' result="yes" >&content;</protect>
              </textarea>
              <input name="whichDemo" type="hidden" value="&;"/>
            </td></tr>
          
          </table>
        </form>  
      </body>
    </html> 
  </action>

</define>



</tagset>
