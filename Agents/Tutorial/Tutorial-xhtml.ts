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
<!-- This code was initially developed by Ricoh Innovations, Inc.        -->
<!-- Portions created by Ricoh Innovations, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s): paskin@rii.ricoh.com                                   -->
<!--                 bill@rii.ricoh.com                                     -->
<!--                                                                        -->
<!-- ---------------------------------------------------------------------- -->

<tagset name="Tutorial-agent" parent="xhtml" recursive="yes">
<cvs-id>$Id: Tutorial-xhtml.ts,v 1.11 2001-04-03 00:03:50 steve Exp $</cvs-id>

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
      <body>
          <tr><td><expand><parse><get name='content'/></parse></expand></td></tr>
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
            </table></td>
            </tr>

            <tr><td>Click  <b>Process</b> to see the PIA server process the
            code below and display the results in 
              the frame above. Try out some modifications, or 
              <b>restore</b> the original text below.</td></tr>

             <tr>
               <td align='center'>
                <table cellspacing="5">
                  <tr>
                    <td><input type='submit' value='Process' /></td>
                   <td>
		      <make name="a">
			 <let name="href"><get name="urlPath"/></let>
			 <let name="...">Restore</let>
		      </make>
                    </td>
                    <td>
		      <make name="a">
			 <let name="href"><get name="attributes:demo-name"/></let>
			 <let name="...">About this demo</let>
			 <let name="target">_top</let>
		      </make>
                    </td>
                  </tr>
                 </table> 
                </td>
              </tr>
              <tr valign="bottom"><td align='center'>
              <textarea name='code-text' rows='15' cols='80' wrap='off'>
	        <protect markup='markup' result="yes" >&content;</protect>
              </textarea>
              <input name="whichDemo" type="hidden" />
            </td></tr>
          
          </table>
        </form>  
      </body>
    </html> 
  </action>

</define>

<define element="widgetJavaScript">
  <action>

     <SCRIPT LANGUAGE="JavaScript"> <!--  hide this from browser display 

     function isaPosNum(s) {
	return (parseInt(s) > 0)
     }

     function qty_check(item, min, max) {
	var returnVal = false
	if (!isaPosNum(item.value)) 
	   alert("Please enter a positive number")
	else if (parseInt(item.value) < min) 
	   alert("Please enter a " + item.name + " greater than " + min)
	else if (parseInt(item.value) > max) 
	   alert("Please enter a " + item.name + " less than " + max)
	else 
	   returnVal = true
	return returnVal
     }

     function validateAndSubmit(theform, min, max) {
	if (qty_check(theform.quantity, min, max)) {
	   alert("Order submitted successfully")
	   return true
	}
	else {
	   alert("Sorry, order invalid... try again.")
	   return false
	}
     }
        // end hiding -->
     </SCRIPT>

  </action>
</define>

<define element="widgetForm">
  <action>
     <FORM NAME="widget_order" ACTION="test.xh" METHOD="post">
     How many widgets do you want today?
       (min=&attributes:min; max=&attributes:max;) 
     <INPUT TYPE="text" NAME="quantity"  />
     <BR/>
       <make name="input">
	  <let name="onClick">
            validateAndSubmit(this.form, <get name="attributes:min"/>,
            <get name="attributes:max"/> )
            <get name="attributes:demo-name"/></let>
	  <let name="type">button</let>
	  <let name="value">Enter Order</let>
       </make>

     </FORM>
  </action>
</define>


</tagset>






