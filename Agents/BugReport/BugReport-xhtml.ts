<!doctype  tagset system "tagset.dtd">
<tagset name='BugReport-xhtml' parent='pia-xhtml' recursive='recursive'>

<define element=bug>
<doc>Presents the bug report in different formats according
	to whether it is being edited or viewed.  Also contains
	all individual bug report fields.
</doc>
<action>
	<if><get name='FORM:submitted'><then>
		<if><test match=&urlPath;>/BugReport/BugTemplate</test><then>
			<set name=FORM:location>~/&year;/&month;/&day;&hour;&minute;&second;.xh</set>
            	</then>
		<else>
			<set name=FORM:location>&urlPath;</set>
		</else>
		</if>
		<set name=expanded-element>
			&element;
		</set>
		<extract> <from>&expanded-element;</from>
			<append children>&content;</append>
		</extract>
		<output dst=&FORM:location;>
<html><head>
<title>Bug Report</title>
</head>
<body bgcolor='#ffffff' link='#c40026' vlink='#e40026'>
<h1>Bug Report</h1>
&expanded-element;
</body></html>
</output>
<a href=&FORM:location;>&FORM:location;</a>
	</then>
	<elsf><get name='FORM:edit'><then>
		<FORM method='POST' action='&url;'>
		<input type='hidden' name='submitted' value='&FORM:submitted;' />
		<input type='hidden' name='location' value='&FORM:location;' />
		<input type='hidden' name='id' value='&FORM:id;' />
		<TABLE cols='2'>
		<TR>
		<TD align='right' width='60'>
		</TR>
		&content;
		</TABLE>
		<CENTER>
		<input type='submit' name='submitted' value='Submit Report' size='40' />
		</CENTER>
	</FORM>	
	</then>
	</elsf>
	<else>
		<!-- View file that has been saved -->
		<FORM method='POST' action='&url;'>
		<TABLE cols='2'>
		<TR>
		<TD align='right' width='60'>
		</TR>
			&content;
		</TABLE>
	</else>
	</if>
</action>
</define>

<define element=bug-name>
<doc>Bug title/subject field.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-title-input;<then>
			<!-- Page that is returned following a editing submit -->
			<get name='FORM:b-title-input' />
		</then>
		<else>
			&content;
		</else>
		</if>
	</then>
	<else>			
		<if><get name='FORM:edit'><then>
			<!-- View when in editing mode -->
			<TR><TD align='right' width='60'>Title/Subject:</TD><TD> <input type='text' name='b-title-input' size='60' value='&content;' /></TD></TR>
		</then>
		<else>	
			<!-- View when looking at saved-out file -->
			<TR><TD align='right' width='60'>Title/Subject:</TD><TD> &content;</TD></TR>
		</else>
		</if>
	</else>
	</if>
</action>
</define>

<define element=bug-submitter>
<doc>Name and email address of person submitting the bug.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-submit-input;<then>
				<get name='FORM:b-submit-input' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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


<define element=bug-package>
<doc>Name of the package in which the bug appeared.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-pkg-input;<then>
				<get name='FORM:b-pkg-input' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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

<define element=bug-agent>
<doc>Name of the agent in which the bug appeared.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-agent-input;<then>
				<get name='FORM:b-agent-input' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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

<define element=bug-date>
<doc>The date that the bug was submitted.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-date-input;<then>
				<get name='FORM:b-date-input' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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

<define element=bug-version>
<doc>The version of the software in which the bug occurred.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-version-input;<then>
				<get name='FORM:b-version-input' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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

<define element=bug-desc>
<doc>A detailed description of the bug.
</doc>
<action mode=replace-content>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:b-desc-text;<then>
				<get name='FORM:b-desc-text' />
			</then>
			<else>
				&content;
			</else>
			</if>
		</then>
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

<define element=bug-id>
<doc>Creates an id for a bug from a number saved to
	file .pia/BugReport/bug_id.txt.
</doc>
<action>
	<if><get name='FORM:submitted'><then>
		<if>&FORM:id;<then>
			<h2>Bug ID: &FORM:id;</h2>
		</then>
		<else>
			 <h2>BUG ID: &content;</h2>
		</else>
		</if>
	</then>
	<else>
		<if><get name='FORM:edit'><then>
			<h2>Bug ID: &FORM:id;</h2>
		</then>
		<else>	
			<h2>Bug ID: &content;</h2>
		</else>
		</if>
		
	</else>
	</if>
</action>	
</define>

<define element=increment-bug-id>
<doc>
	Takes the current bug id and increments it by 1.
	Saves the result to the .pia/BugReport/bug_id.txt file.
	Then gets the new result from the file.  Assumes that
	the file originally contains 0 to start numbers from 1.
</doc>
<action>
	<set name='fileid'>
		<connect src='~/bug_id.txt' entity=b-id></connect>
	</set>
	<set name=result><numeric sum>&fileid; 1</numeric></set>
	<output dst='~/bug_id.txt'>&result;</output>
	<set name='FORM:id'>
		&result;
	</set>
	&FORM:id;
</action>
</define>


</tagset>
