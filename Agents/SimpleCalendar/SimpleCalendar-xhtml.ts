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

<tagset name=SimpleCalendar-agent parent=pia-xhtml recursive>
<cvs-id>$Id: SimpleCalendar-xhtml.ts,v 1.12 1999-12-03 19:47:46 steve Exp $</cvs-id>

<define element="soft-include">
   <doc> Check to see if an included file exists; if not, expands to nothing
         rather than to an error message.
   </doc>
   <action>
      <if> <status src="&attributes:src;" item="exists" />
           <then>
              <include src="&attributes:src;" />
           </then>
      </if>
   </action>
</define>




<define element="numpad">
  <doc> Takes a number and left-pads it with a zero if it's just one digit.
  </doc>
  <action><if><test match=".." >&content;</test>
	      <then>&content;</then>
	      <else>0&content;</else></if></action>
</define>


     <!-- strip off leading zeros from single-digit positive numbers -->
<define element="numstrip">
  <doc> Strips leading zeros from single-digit positive numbers
  </doc>
  <action><text trim="yes">
            <if><test greater="9">&content;</test>
               <then>&content;</then>
               <else>
		   <repeat start="1" stop="9">
		      <if><test equals="&n;">&content;</test>
			 <then>&n;</then>
		      </if>
		   </repeat>
               </else>
            </if>
         </text></action>
  </define>


<define element="readoneevent">
  <doc> This is used inside the <tag>eventtable</tag> tag's definition to set
	some local variables (<code>myTime</code>, etc.) from the current
	event, which is passed in the loop variable <code>&amp;li;</code>.
  </doc>
  <action>
       <!-- assign names "my[---]" to values for easy testing -->
       <set name="myTime"><text trim="yes">
	   <extract><from>&li;</from>
	       <name all="all">start-time</name>
	       <content />
	   </extract>
       </text></set>
       <set name="myPlace"><text trim="yes">
	   <extract><from>&li;</from>
	       <name all="all">place</name>
	       <content />
	   </extract>
       </text></set>
       <set name="myName"><text trim="yes">
	   <extract><from>&li;</from>
	       <name all="all">event-name</name>
	       <content />
	   </extract>
       </text></set>
       <set name="myDesc"><text trim="yes">
	   <extract><from>&li;</from>
	       <name all="all">description</name>
	       <content />
	   </extract>
       </text></set>

       <set name="longURL">home.xh?newDay=<text
       encode="url">&myDay;</text>&newMonth=<text
       encode="url">&myMonth;</text>&newYear=<text
       encode="url">&myYear;</text>&newTime=<text
       encode="url">&myTime;</text>&newPlace=<text
       encode="url">&myPlace;</text>&newName=<text
       encode="url">&myName;</text>&Edit=submit</set>
   </action>
</define>



<define element="pluckintervals">
   <action>
     <hide>
	<!-- set FORM:[-]Interval attributes from file, for edit boxes-->
	<set name="FORM:dayInterval"><text trim="yes">
	    <extract><from>&li;</from>
		<name all="all">dayinterval</name>
		<content />
	    </extract>
	</text></set>
	<set name="FORM:weekInterval"><text trim="yes">
	    <extract><from>&li;</from>
		<name all="all">weekinterval</name>
		<content />
	    </extract>
	</text></set>
	<set name="FORM:monthInterval"><text trim="yes">
	    <extract><from>&li;</from>
		<name all="all">monthinterval</name>
		<content />
	    </extract>
	</text></set>
	<set name="FORM:startDate"><text trim="yes">
	    <extract><from>&li;</from>
		<name all="all">startday</name>
		<content />
	    </extract>
	</text></set>

        <!-- set the radio-selection choice from which tag is present -->
        <if><get name="FORM:dayInterval" />
           <then><set name="FORM:interval">Days</set></then>
        </if>
        <if><get name="FORM:monthInterval" />
           <then><set name="FORM:interval">Months</set></then>
        </if>
        <if><get name="FORM:weekInterval" />
           <then><set name="FORM:interval">Weeks</set></then>
        </if>

        <!-- if radio "interval" still not filled up, set it to "None" -->
        <if><get name="FORM:interval" />
           <else><set name="FORM:interval">None</set></else>
        </if>
     </hide>
   </action>
</define>

<define element="month-name">
  <doc> Convert the content, a number between 1 and 12 inclusive, into the
	name of the corresponding month.
  </doc>
  <action><weekday monthname="yes">
		  <day>15</day><month>&content;</month><year>&myYear;</year>
  </weekday></action>
</define>


<define element="show-today" >
  <doc> Show today's date as a link to editing that day </doc>
  <action>
     <!-- encapsulate whole date into a smaller package -->
     <set name="todaypack">
              <day>&day;</day><month>&month;</month><year>&year;</year>
     </set>
    <!-- show today's date as link to the day -->
<h2> Today is  
          <weekday>&todaypack;</weekday>,
                 <a href="home.xh?newDay=&myDay;&newMonth=&myMonth;&newYear=&year;&Add=submit"><weekday monthname="yes">&todaypack;</weekday>
            &myDay;, &year; </a>  </h2> 

  </action>
</define>

<define entity="month-name-list">
  <value>
    <li>January</li>
    <li>February</li>
    <li>March</li>
    <li>April</li>
    <li>May</li>
    <li>June</li>
    <li>July</li>
    <li>August</li>
    <li>September</li>
    <li>October</li>
    <li>November</li>
    <li>December</li>
  </value>
</define>

<define element="monthRow">
  <doc> Expand into a row of month names, each a link to a month in the
	current year (<code>&amp;myYear;</code>).
  </doc>
  <define attribute="mpage">
    <doc> If present, use this as the page to use for the month display.
    </doc>
  </define>	 
  <define attribute="mlink">
    <doc> If present, make this month a link too.
    </doc>
  </define>	 
  <action>
    <set name="mpage"><get name="attributes:mpage"/></set>
    <set name="mlink"><get name="attributes:mlink"/></set>
    <if><get name="mpage"/><else><set name=mpage>month</set></else></if>
    <tr>
       <repeat entity="mth" start="1" stop="12"><text trim="yes">
	 <hide>
           <set name="align"><if><test equals="1">&mth;</test>
				 <then>left</then>
			     <else-if><test equals="12">&mth;</test>
				 <then>right</then>
			         <else>center</else></if></set>
	   <set name="mthname"><month-name>&mth;</month-name></set>
	 </hide>
	   <if><test equals="&myMonth;">&mth;</test>
	     <then>
	       <th align="&align;"><font size="+2"><if>&mlink;
	 	  <then><a  href="&mpage;.xh?newDay=15&newMonth=&mth;&newYear=&myYear;">&mthname;</a></then>
	          <else>&mthname;</else>
	       </if></font></th>
	     </then>
	     <else>
	         <td align="&align;"><a  href="&mpage;.xh?newDay=15&newMonth=&mth;&newYear=&myYear;">&mthname;</a> </td>
	     </else>
	   </if>
       </text></repeat>
    </tr>
  </action>
</define>

<define element="yearRow">
  <doc> Expand into a row of year names, each a link to the full-page display
	of the year in question. 
  </doc>
  <define attribute="mpage">
    <doc> If present, use this as the page to use for the month display.
    </doc>
  </define>	 
  <action><hide>	 
    <set name="mpage"><get name="attributes:mpage"/></set>
    <if><get name="mpage"/><else><set name=mpage>month</set></else></if>
    <set name="lastYear"><numeric op="diff">&myYear; 1</numeric></set>
    <set name="llastYear"><numeric op="diff">&myYear; 2</numeric></set>
    <set name="nextYear"><numeric op="sum" >&myYear; 1</numeric></set>
    <set name="nnextYear"><numeric op="sum" >&myYear; 2</numeric></set></hide>
    <tr><td align="left" >
          <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&llastYear;&mpage=&mpage;">
		&llastYear;</a>
        </td>
        <td align="left">
          <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&lastYear;&mpage=&mpage;">
		&lastYear;</a>
        </td>
        <th align="center" colspan=10>
		<font size="+2">
          <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&myYear;&mpage=&mpage;">
		&myYear;</a>
	  </font>
	</th>
        <td align="right">
          <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&nextYear;&mpage=&mpage;">
		&nextYear;</a>
        </td>
        <td align="right">
          <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&nnextYear;&mpage=&mpage;">
		&nnextYear;</a>
        </td>
    </tr>
  </action>
</define>

<define element="header">
  <action>
    <table border="0" cellpadding="0" cellspacing="0"
           align="center" width="100%">
      <tr><th align=left><font size="+2">&content;</font>
	  </th>
      <tr height=6><td><img src="/Icon/rule.png" height=6 width=469></td></tr>
    </table>
    <table border="0" cellpadding="0" cellspacing="0" 
           align="center" width="100%">
      <tr><th align=left>
	    <table>  <!-- bar of links to nearby years -->
	     <yearRow>&myYear;</yearRow>
	    </table>
	  </th>
	  <td align="right">
	    <a href="home">Today</a> is 
	      <weekday>&todaypack;</weekday>,
	    <a href="month"><weekday monthname="yes">&todaypack;</weekday>
	      </a> &day;, &year;
	  </td>
      </tr>
      <tr><td colspan=2>&nbsp;</td></tr>
    </table>
  </action>
</define>

<define element="navbar">
  <define attribute="mpage">
    <doc> If present, use this as the page to use for the month display.
    </doc>
  </define>	 
  <define attribute="mlink">
    <doc> If present, make this month a link too.
    </doc>
  </define>	 
  <action>
     <set name="mlink"><get name="attributes:mlink"/></set>
     <set name="mpage"><get name="attributes:mpage"/></set>
     <if><get name="mpage"/><else><set name=mpage>month</set></else></if>
     <table border="0" cellpadding="0" cellspacing="0" width="100%">
	<monthRow mlink="&mlink;" mpage="&mpage;"/>
       <hide>  
	   <set name="lastYear"><numeric op="diff">&myYear; 1</numeric></set>
	   <set name="nextYear"><numeric op="sum" >&myYear; 1</numeric></set>
       </hide>
       <tr><td align="left">
	     <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&lastYear;&mpage=&mpage;">
		   &lastYear;</a>
	   </td>
	   <th align="center" colspan=10>
		   <font size="+2">
	     <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&myYear;&mpage=&mpage;">
		   &myYear;</a>
	     </font>
	   </th>
	   <td align="right">
	     <a href="year.xh?newDay=15&newMonth=&myMonth;&newYear=&nextYear;&mpage=&mpage;">
		   &nextYear;</a>
	   </td>
       </tr>
    </table>
  </action>
</define>

<define element="event-table">
  <doc> takes an event file and prints it in table form </doc>

  <!-- sort the events for this day by start-time -->
  <action><text trim="yes">
	     <set name="sortedlist">
	       <extract>
		  <from>&content;</from>
		   <sort numeric="yes">
		      <name all="yes"> start-time </name>
		   </sort>
	       </extract>
	     </set>
	     <table >
		 <tr>
		    <td>
		      <a
		      href="home.xh?newDay=&myDay;&newMonth=&thisMonth;&newYear=&myYear;&Add=submit">&myDay; </a>
		    </td>
		 </tr>
		 <repeat><foreach>&sortedlist;</foreach>
		   <set name="myTime"/>
		   <set name="myPlace"/>
		   <set name="myName"/>
		   <set name="myDesc"/>
		   <set name="longURL" />
		   <readoneevent />

		   <tr>
		       <td valign="top">
	                 <a  href="&longURL;">
                            <b><small>&myTime;</small></b>
			     <i><small>&myName;</small></i>
                         </a>
                       </td>
		    </tr>
		  </repeat>
	     </table>
  </text></action>
</define>



<define element="daycheck">
  <action>
	 <logical and="yes">
	    <test greater="0">&attributes:testday;</test>
	    <test less="&attributes:lastday;"><numeric difference="yes"> &attributes:testday; 1</numeric></test>
	 </logical>
  </action>
</define>



<define element="lookup">
  <doc> reads out and sorts events for a given date from appropriate
        sources.
  </doc>
  <action><hide>
        <!-- get the file(s) -->
	<set name="filename">&attributes:newyear;/&attributes:newmonth;/&attributes:newday;/Events</set>

	<!-- read out file (possibly empty) -->
	<set name="eventfile" >
	        <extract>
		    <from><soft-include src="&filename;"/></from>
		    <name >event</name>
		</extract>
	</set>

          <!-- look for repeating events -->
	<set name="longdaytoday">
		    <weekday longday="yes">
			  <day>&attributes:newday;
			  </day><month>&attributes:newmonth;</month>
			  <year>&attributes:newyear;</year>
		    </weekday>
	</set>

       <!-- by day, looping over 1-6 day intervals -->
       <set name="moreEvents" >
	 <repeat start="1" stop="6" entity="days">
             <set name="repeatFile">
		<extract>
		    <from>
			<soft-include src="Every/&days;days/Events" />
		    </from>
		    <name>event</name>
		</extract>
             </set>
             <repeat>
                   <foreach>&repeatFile;</foreach>
		      <set name="startday">
			   <extract>
			     <from>&li;</from>
			     <name all="yes">startday</name>
			     <content />
			   </extract>
		      </set>

		      <set name="daydiff">
			  <numeric difference="yes">
			   &longdaytoday;
			   &startday;
			   </numeric>
		      </set>

		      <if> <get name="startday" />
			  <else>
			    <set name="daydiff"> -9999 </set>
			  </else>
		      </if>

                      <if> <logical and="yes">
                             <test greater="-1">&daydiff;</test>
                             <test equals="0">
                              <numeric remainder="yes">
                                &daydiff;
                                &days; 
                               </numeric>
                             </test>
                            </logical>
                          <then>
                             &li;
                          </then>
                      </if>
              </repeat>
          </repeat>
       </set>

       <!-- by week, looping over 1-4 week intervals -->
       <set name="wkdayname"><weekday>
		    <day>&attributes:newday;
                    </day><month>&attributes:newmonth;</month>
                    <year>&attributes:newyear;</year>
       </weekday></set>

       <set name="moreEvents" >
         <get name="moreEvents" />
	 <repeat start="1" stop="4" entity="weeks">
             <set name="repeatFile">
		<extract>
		    <from>
			<soft-include src="Every/&weeks;&wkdayname;/Events" />
		    </from>
		    <name>event</name>
		</extract>
             </set>

             <repeat>
                   <foreach>&repeatFile;</foreach>
		      <set name="startday">
			   <extract>
			     <from>&li;</from>
			     <name all="yes">startday</name>
			     <content />
			   </extract>
		      </set>

		      <set name="daydiff">
			  <numeric difference="yes">
			   &longdaytoday;
			   &startday;
			   </numeric>
		      </set>

		      <if> <get name="startday" />
			  <else>
			    <set name="daydiff"> -9999 </set>
			  </else>
		      </if>

                      <if> <logical and="yes">
                             <test greater="-1">&daydiff;</test>
                             <test equals="0">
                              <numeric remainder="yes">
                                &daydiff;
                                <numeric product="yes"> &weeks; 7 </numeric>
                               </numeric>
                             </test>
                            </logical>
                          <then>
                             &li;
                          </then>
                      </if>
              </repeat>
          </repeat>

       </set>

       <!--- now months -->
       <set name="monthtoday">
              <weekday monthnum="yes">
		    <day>&attributes:newday;
                    </day><month>&attributes:newmonth;</month>
                    <year>&attributes:newyear;</year>
              </weekday>
       </set>
       <set name="moreEvents" >
         <get name="moreEvents" />

	 <repeat start="1" stop="12" entity="months">
             <set name="repeatFile">
		<extract>
		    <from>
			<soft-include src="Every/&months;Months/&attributes:newday;/Events" />
		    </from>
		    <name>event</name>
		</extract>
             </set>

             <repeat>
                   <foreach>&repeatFile;</foreach>
		      <set name="startday">
			   <extract>
			     <from>&li;</from>
			     <name all="yes">startday</name>
			     <content />
			   </extract>
		      </set>
                      <set name="startmonth">
                          <weekday monthnum="yes">
                             <seconds>
                               <numeric product="yes">
                                  &startday;
                                  86410
                               </numeric>
                             </seconds>
                           </weekday>
                      </set>

		      <set name="monthdiff">
			  <numeric difference="yes">
			   &monthtoday;
                           &startmonth;
			   </numeric>
		      </set>

                      <if> <logical and="yes">
                             <test greater="-1">&monthdiff;</test>
                             <test equals="0">
                              <numeric remainder="yes">
                                &monthdiff; &months;
                               </numeric>
                             </test>
                            </logical>
                          <then>
                             &li;
                          </then>
                      </if>
              </repeat>
          </repeat>
      </set>


       <!--- finally by year -->
       <set name="mthname"><text trim="yes">
              <weekday monthname="yes">
		    <day>&attributes:newday;
                    </day><month>&attributes:newmonth;</month>
                    <year>&attributes:newyear;</year>
              </weekday>
       </text></set>
       <set name="moreEvents" >
                <get name="moreEvents" />
		<extract>
		    <from>
			<soft-include src="Every/&mthname;/&attributes:newday;/Events" />
		    </from>
		    <name>event</name>
		</extract>
            
       </set>

          <!-- concatenate all events together, then sort -->
          <set name="eventfile" >
             &eventfile; &moreEvents;
          </set>

          <set name="eventfile" >
             <extract>
                  <from>&eventfile;</from>
                  <sort numeric="yes">
		       <name all="yes"> start-time </name>
		  </sort>
              </extract>
          </set>
       
    </hide>
    <get name="eventfile" />
  </action>
</define>

<define element="find-event-file">
  <doc> this does the workhorse lifting by locating the file in which
        a given interval is stored; it expands to the desired filename
  </doc>

  <action><text trim="yes">
    <hide>

        <!-- Everthing tries to fill up fileName -->
        <set name="filedir"> </set>

	 <set name="weeknum"><text trim="yes">
	    <extract>
	       <from>&content;</from>
	       <name all="yes">weekinterval</name>
	       <content />
	    </extract>
	 </text></set>
	 <if>&weeknum;
	    <then>
		 <set name="wkdayname"><text trim="yes">
		    <weekday>
		       <day>&attributes:newday;</day>
		       <month>&attributes:newmonth;</month>
		       <year>&attributes:newyear;</year>
		    </weekday>
		 </text></set>
		 <set name="filedir">Every/&weeknum;&wkdayname;</set>
	    </then>
	 </if>

         <!-- misc day intervals -->
	 <set name="daynum"><text trim="yes">
	    <extract>
	       <from>&content;</from>
	       <name all="yes">dayinterval</name>
	       <content />
	    </extract>
	 </text></set>
	 <if>&daynum;
	    <then>
		 <set name="filedir">Every/&daynum;days</set>
	    </then>
	 </if>

         <!-- months -->
	 <set name="monthnum"><text trim="yes">
	    <extract>
	       <from>&content;</from>
	       <name all="yes">monthinterval</name>
	       <content />
	    </extract>
	 </text></set>
	 <if><test equals="12">&monthnum;</test>
	    <then>
		 <set name="mthname"><text trim="yes">
		    <weekday monthname="yes">
		       <day>&attributes:newday;</day>
		       <month>&attributes:newmonth;</month>
		       <year>&attributes:newyear;</year>
		    </weekday>
		 </text></set>
		 <set name="filedir">Every/&mthname;/&attributes:newday;</set>
	    </then>
            <else-if>&monthnum;
               <then>
		 <set name="filedir">Every/&monthnum;Months/&attributes:newday;</set>
               </then>
            </else-if>
	 </if>
    </hide>
  <get name="filedir" />
  </text></action>
</define>

        
<define element="new-interval-event">
  <doc> Appends the interval in its contents to appropriate file.
  </doc>
  <action>
	<set name="filedir"><text trim="yes">
	     <find-event-file  newday="&attributes:newday;"
		       newmonth="&attributes:newmonth;"
		       newyear="&attributes:newyear;" >
		 &content;
	      </find-event-file>
	 </text></set>

	 <output dst="&filedir;" directory="yes" />
	 <output dst="&filedir;/Events" append="append">
	     &content;
	 </output>
  </action>        
</define>
        
<define element="search-and-destroy">
 <doc> Finds the interval in its contents in the appropriate file,
       and deletes it.
 </doc>
 <action>
     <set name="filedir"><text trim="yes">
	  <find-event-file  newday="&attributes:newday;"
		    newmonth="&attributes:newmonth;"
		    newyear="&attributes:newyear;" >
	      &content;
	   </find-event-file>
      </text></set>

      <!-- find fields to be matched in the old interval-event -->
      <set name="oldPlace"><text trim="yes">
	 <extract>
	   <from>&content;</from>
	   <name all="yes">place</name>
	   <content />
	 </extract>
      </text></set>
      <set name="oldName"><text trim="yes">
	 <extract>
	   <from>&content;</from>
	   <name all="yes">event-name</name>
	   <content />
	 </extract>
      </text></set>
      <set name="oldTime"><text trim="yes">
	 <extract>
	   <from>&content;</from>
	   <name all="yes">start-time</name>
	   <content />
	 </extract>
      </text></set>
      <set name="oldStartday"><text trim="yes">
	 <extract>
	   <from>&content;</from>
	   <name all="yes">startday</name>
	   <content />
	 </extract>
      </text></set>

       <!-- read out whole day's events prior to overwriting,  -->
       <set name="allEvents">
	   <extract>
	       <from>
		   <include src="&filedir;/Events" />
	       </from>
	       <name>event</name>
	       <!-- go through events to find one to replace -->
	       <!-- match most fields (delete if not replacing) -->
	       <repeat>
		  <foreach>&list;</foreach>
		    <if>
		       <logical and="yes">
			  <test match="&oldName;"><text
			  trim="yes">
			     <extract>
			       <from>&li;</from>
			       <name all="yes">event-name</name>
			       <content />
			     </extract>
			  </text></test>
			  <test match="&oldPlace;"><text
			  trim="yes">
			     <extract>
			       <from>&li;</from>
			       <name all="yes">place</name>
			       <content />
			     </extract>
			  </text></test>
			  <test match="&oldTime;"><text
			  trim="yes">
			     <extract>
			       <from>&li;</from>
			       <name all="yes">start-time</name>
			       <content />
			     </extract>
			  </text></test>
			  <test match="&oldStartday;"><text
			  trim="yes">
			     <extract>
			       <from>&li;</from>
			       <name all="yes">startday</name>
			       <content />
			     </extract>
			  </text></test>
		       </logical>
		       <then> </then>
		       <else>&li;</else>
		    </if>
	       </repeat>
	   </extract>
       </set>

      <!-- finally, overwrite the repeat file with the new copy, in
	   which the undesired event is missing -->
      <output dst="&filedir;/Events" >
	  &allEvents;
      </output>
  </action>        
</define>




<define element="store">
  <doc> reads out and sorts events for a given date from appropriate
        sources.
  </doc>
  <action>
     <set name="newInterval">
	       <extract>
		  <from>&content;</from>
		  <name>interval-evt-to-add</name>
		  <content />
	       </extract>
     </set>
     <set name="oldInterval">
	       <extract>
		  <from>&content;</from>
		  <name>interval-evt-to-delete</name>
		  <content />
	       </extract>
     </set>


     <if><logical or="yes">
	   <get name="newInterval" />
	   <get name="oldInterval" />
	 </logical>
	 <then>
	     <if><get name="attributes:appendevent" />
		<else>
		    <search-and-destroy newday="&attributes:newday;"
		     newmonth="&attributes:newmonth;"
		     newyear="&attributes:newyear;" >
			 &oldInterval;
		    </search-and-destroy>
		</else>
	      </if>
	      <new-interval-event newday="&attributes:newday;"
	       newmonth="&attributes:newmonth;"
	       newyear="&attributes:newyear;" >
		   &newInterval;
	      </new-interval-event>
	 </then>
     </if>


     <!--overwrite nonrepeating file if oldinterval wasn't repeating -->
     <if><test not="yes">
	   <extract>
	      <from><get name="oldInterval" /></from>
	      <name all="yes">interval</name>
	   </extract>
	  </test>
	 <then>
	    <!-- store nonrepeating events in ordinary directories -->
	    <set name="nonrepeating">
	       <repeat>
		  <foreach>&content;</foreach>
		  <if>
		      <extract>
			 <from>&li;</from>
			 <name all="yes">interval</name>
		      </extract>
		      <then> </then>
		     <else> &li; </else>
		  </if>
	       </repeat>
	    </set>


	    <set name="filedir">&attributes:newyear;/&attributes:newmonth;/&attributes:newday;</set>

		 <!-- create output directory just in case -->
		 <output dst="&filedir;" directory="yes" />

		 <!-- creating a whole new event ==> appending file -->
		 <if>	<get name="attributes:appendevent" /> 
		     <then>
			 <!-- append new data to file -->
			 <output dst="&filedir;/Events" append="append">
			    <get name="nonrepeating" />
			 </output>
		     <then />
		 </if>
		 <if>	<get name="attributes:rewriteevent" /> 
		     <then>
			 <!--  overwrite file --> 
			 <output dst="&filedir;/Events" >
			   <get name="nonrepeating" />
			</output>
		    <then />
		</if>
	   </then>
       </if>
  </action>
</define>

</tagset>






