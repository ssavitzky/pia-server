<!doctype  tagset system "tagset.dtd">
<tagset name='BugReport-xhtml' parent='/Tagsets/pia-xhtml' recursive='recursive'>

<define element='bug'>
<doc>Top-level element for describing a bug.  The actions for this tag
depend on whether it is being edited (edit=true in the query string) or
viewed (no query string).  The content contains
	all the individual bug report fields.
</doc>
<action>
<html><head>
<!-- Generic html header for a bug report -- this could go in the individual
files instead of this tagset. -->
<title>Bug Report</title>
</head>
<body bgcolor='#ffffff' link='#c40026' vlink='#e40026'>
<header>Bug Report</header>
	<if><get name='FORM:submitted' /><then>
	    <sub-head page="Bug edit response page" />
		<if><get name="FORM:location" /><else>
		 <set name='FORM:location'>~/&year;/&month;/&day;&hour;&minute;&second;.xh</set>
            	</else>
		</if>
		<?-- We want to create an exact copy this bug element with the
		filled in form variables and output to a new file.  In order
		to do so we must construct a version of the element including
		all its contents.  --?>
		<set name='expanded-element'>
			&element;
		</set>
		<hide> <!-- hide results of extract from browser -->
		<extract> <from>&expanded-element;</from>
			<append children>&content;</append>
		</extract>
		<?-- now save this element to the proper location --?>
		<output dst=&FORM:location;>
		&expanded-element;
		</output>
		</hide>
		<!-- The new element can be found at this url -->
		Thanks for your bug submission.  <br />
		The updated report can be accessed through this link: <a href='&FORM:location;'>&FORM:location;</a>
	</then>
	<elsf><get name='FORM:edit' /><then>
	     <sub-head page="Editing Bug report" />
	      <?-- handle the case when we want to edit the element --?>
	       <if><test match='&urlPath;'>/BugReport/BugTemplate</test><then>
	       <set name="buglocation">~/&year;/&month;/&day;&hour;&minute;&second;.xh</set></then>
	       <else>
	         <set name="buglocation">&urlPath;</set>
	       </else></if>
		<FORM method='POST' action='&url;'>
		<input type='hidden' name='location' value='&buglocation;' />
		<TABLE cols='2'>
		<TR>
		<TD align='right' width='60'>
		</TR>
		&content;
		</TABLE>
		<CENTER>
		<input type='submit' name='submitted' value='BugSubmit' size='40' />
		</CENTER>
	</FORM>	
	</then>
	</elsf>
	<else>
	  <sub-head page="Viewing bug report" />
		<!-- Just view the bug as HTML -->
		<TABLE cols='2'>
			&content;
		</TABLE>
		<table> 
		<tr> <th> <a href='&url;?edit=true'> Edit this Bug </a> </th>
		     <th> <a href='&url;?updateStatus=true'> Update status for
			  this Bug </a> </th>
		     <th> <a href='&url;?assign=true'> Assign
			  this Bug </a> </th>
			  </tr>
	</else>
	</if>
<footer> </footer>
</body></html>

</action>
</define>

<define element='bug-name'>
<doc>Bug title/subject field.
</doc>
<!-- use replace-content as the mode for the action so that the element itself
is available in the output.  If this is a form submission, content taken from
appropriate field, otherwise content is displayed as HTML. -->
<action mode=replace-content>
	<if><get name='FORM:b-title-input' />
	  <then><get name='FORM:b-title-input' /><then>
	  <else>			
	    <if><get name='FORM:edit' /><then>
			<?-- View when in editing mode --?>
			<TR><TD align='right' width='60'>Title/Subject:</TD><TD> <input type='text' name='b-title-input' size='60' value='&content;' /></TD></TR>
		</then>
		<else>	
			<?-- Normal View when looking at bug --?>
			<TR><TD align='right' width='60'>Title/Subject:</TD><TD> &content;</TD></TR>
		</else>
		</if>
	</else>
	</if>
</action>
</define>

<define element='bug-submitter'>
<doc>Name and email address of person submitting the bug.
</doc>
<action mode=replace-content>
	<if><get name='FORM:b-submit-input' />
	    <then><get name='FORM:b-submit-input' /></then>
	    <else>
		<if><get name='FORM:edit'><then>
		    <TR><TD align='right' width='60'>Submitted by:</TD><TD> <input type='text' name='b-submit-input' size='60' value='&content;' /></TD></TR>
		 </then>
		 <else>	
		  <TR><TD align='right' width='60'>Submitted by:</TD><TD> &content;</TD></TR>
		  </else>
		</if>
	     </else>
	 </if>
	</action>
</define>


<define element='bug-package'>
<doc>Name of the package in which the bug appeared.
</doc>
<action mode='replace-content'>
	<if><get name='FORM:b-pkg-input' />
	    <then><get name='FORM:b-pkg-input' /></then>
		<else>
			<if><get name='FORM:edit'><then>
				<TR><TD align='right' width='60'>Package:</TD><TD> <input type='text' name='b-pkg-input' size='60' value='&content;' /></TD></TR>
			</then>
			<else>	
				<TR><TD align='right' width='60'>Package:</TD><TD> &content;</TD></TR>
			</else>
			</if>
		</else>
		</if>
	</action>
</define>

<define element='bug-agent'>
<doc>Name of the agent in which the bug appeared.
</doc>
<action mode='replace-content'>
	<if><get name='FORM:b-agent-input' />
	    <then><get name='FORM:b-agent-input' /></then>
		<else>
			<if><get name='FORM:edit'><then>
				<TR><TD align='right' width='60'>Agent:</TD><TD> <input type='text' name='b-agent-input' size='60' value='&content;' /></TD></TR>
			</then>
			<else>	
				<TR><TD align='right' width='60'>Agent:</TD><TD> &content;</TD></TR>
			</else>
			</if>
		</else>
		</if>
	</action>
</define>

<define element='bug-date'>
<doc>The date that the bug was submitted.
</doc>
<action mode='replace-content'>
	<if><get name='FORM:b-date-input' />
	   <then><get name='FORM:b-date-input' /></then>
		<else>
			<if><get name='FORM:edit'><then>
				<TR><TD align='right' width='60'>Date:</TD><TD> <input type='text' name='b-date-input' size='60' value='&content;' /></TD></TR>
			</then>
			<else>	
				<TR><TD align='right' width='60'>Date:</TD><TD> &content;</TD></TR>
			</else>
			</if>
		</else>
		</if>
	</action>
</define>

<define element='bug-version'>
<doc>The version of the software in which the bug occurred.
</doc>
<action mode=replace-content>
	<if><get name='FORM:b-version-input' />
	   <then><get name='FORM:b-version-input' /></then>
		<else>
			<if><get name='FORM:edit'><then>
				<TR><TD align='right' width='60'>Version:</TD><TD> <input type='text' name='b-version-input' size='60' value='&content;' /></TD></TR>
			</then>
			<else>	
				<TR><TD align='right' width='60'>Version:</TD><TD> &content;</TD></TR>
			</else>
			</if>
		</else>
		</if>
	</action>
</define>

<define element='bug-desc'>
<doc>A detailed description of the bug.
</doc>
<action mode='replace-content'>
	<if><get name='FORM:b-desc-text' />
	    <then><get name='FORM:b-desc-text' /></then>
		<else>
			<if><get name='FORM:edit'><then>
				<TR><TD align='right' width='60'>Description:</TD><TD><TEXTAREA ROWS='7' name='b-desc-text' COLS='56'>&content;</TEXTAREA></TD></TR>
			</then>
			<else>	
				<TR><TD align='right' width='60'>Description:</TD><TD>&content;</TD></TR>
			</else>
			</if>
		</else>
		</if>
	</action>
</define>

<define element='bug-id'>
<doc>A serial number a bug. This number is automatically generated for new
	bugs from a number stored in an agent bugid entity
	(which is associated with the data file .pia/BugReport/bug_id.txt by initialize.xh)
</doc>
<action mode='replace-content'>
	<if><get name='FORM:b-id' />
	  <then><get name='FORM:b-id' /></then>
	<else>
		<if><get name='FORM:edit'><then>
		<?-- hidden field for ID, if not already assigned, increment
		counter --?>
		
		 <if><get name='content' />
		    <then>
		     <input name='b-id' type=hidden value=&content;>
		    <else><set name="AGENT:bugid"><numeric sum digits=0>
		     &AGENT:bugid; 1 </numeric></set>
		     <input name='b-id' type=hidden value=&AGENT:bugid;>
		    </else>
		 </if>
		</then>
		<else>	
		 <TR><TH align='right' width='60'>Bug ID:</TH><TD> &content; </TD></TR>
		</else>
		</if>
		
	</else>
	</if>
</action>	
</define>


</tagset>
