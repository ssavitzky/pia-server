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

<tagset name="SQL-xhtml" parent="/Tagsets/pia-xhtml" recursive="recursive">

<define element="sql" handler="handler">

</define>

<define element="unpack-depts">
  <action>
      Unpacking depts from &attributes:deptname; (&attributes:deptid;)<br />
       <set name="deptlist">
	   <extract>
	     <from>&content;</from>
	     <content />
	     <name>department</name>
	   </extract>
       </set>

       <repeat>
          <foreach> &deptlist;</foreach>
          <set name="subname"><text trim="yes">
	      <extract>
		 <from> &li; </from>
		 <content />
		 <name>description</name>
                 <content />
	      </extract>
          </text></set>

          <set name="subid"><text trim="yes">
	      <extract>
		 <from> &li; </from>
		 <attr>id</attr>
                 <eval />
	      </extract>
          </text></set>

	  <!-- make the sql insert-query from extracted data -->
	  <sql database="jdbc:mysql:///orgdb"
	      user="root" password="root">
                      insert into depts VALUES ("&subname;",
                     "&attributes:deptid;", "&subid;") </sql>

        </repeat>
        <br />

  </action>
</define>

<define element="get-authid">
  <doc> Given a department and auth-type, return the authorizer's name 
        (or nothing if no match)</doc>

  <action><text trim="yes">

       <?-- get the authorizer for that dept --?>
       <set name="dbresult"><text trim="yes">
              <sql database="jdbc:mysql:///orgdb"
	      user="root" password="root">
                      SELECT auth_type FROM authorizers WHERE 
		      dept="&attributes:dept;"</sql>
       </text></set>

       <set name="nextauthtype"><text trim="yes">
           <extract>
              <from>&dbresult;</from>
              <name all="yes">auth_type</name>
              <content />
           </extract>
       </text></set>

       <repeat> <foreach>&nextauthtype;</foreach>
          <if ><test match="&attributes:type;">&li;</test>
		<then>
		   <set name="dbresult"><text trim="yes">
			  <sql database="jdbc:mysql:///orgdb"
			  user="root" password="root">
				  SELECT authid FROM authorizers WHERE 
				  auth_type="&li;" AND
                                  dept="&attributes:dept;"</sql>
		   </text></set>

		   <extract>
		      <from>&dbresult;</from>
		      <name all="yes">authid</name>
		      <content />
		   </extract>
		</then>
		<else></else>
	   </if>
       </repeat>


  </text></action>
</define>

<define element="get-next-auth">
   <doc> Finds the lowest department above the present one which contains
         an authorizer of this type  </doc>
   <action><text trim="yes">
       <set name="nextdept">&attributes:dept;</set>
       <set name="authid"> </set>
       <?-- try this dept, and go up the chain until successful --?>
       <repeat>
          <set name="authid"><get-authid dept="&nextdept;" 
              type="&attributes:type;"/></set>
          <while><logical and="yes">
                   <test not="yes">&authid;</test>
                   <test not="yes" match="NULL">&nextdept;</test></logical></while>
          
	   <set name="nextdept"><text trim="yes">
	       <extract>
		  <from><sql database="jdbc:mysql:///orgdb"
		  user="root" password="root">
			  SELECT parent_dept FROM depts WHERE 
			  deptid="&nextdept;"</sql></from>
		  <name all="yes">parent_dept</name>
		  <content />
	       </extract>
	   </text></set>
        </repeat>       

<?--	FINALLY GOT authid=&authid; for dept=&attributes:dept; 
         and type=&attributes:type; <br> --?>

   <if>&authid;
        <then>&authid;</then>
        <else>NULL</else>
   </if>

   </text></action>
</define>


<define element="unpack-members">
  <action>

<?-- USING VAR:uniqAuth[&VAR:uniqueAuths;] --?>

      Unpacking members from &attributes:deptname; (&attributes:deptid;) <br />
       <set name="memberlist">
	   <extract>
	     <from>&content;</from>
	     <content />
	     <name>member</name>
	   </extract>
       </set>

       <repeat>
          <foreach> &memberlist;</foreach>

          <set name="givenname"><text trim="yes">
	     <extract>
		<from> &li; </from>
		<content />
		<name>givenname</name>
                <content/>
	     </extract></text>
          </set>
	  <set name="surname"><text trim="yes">
	     <extract>
		<from> &li; </from>
		<content />
		<name>surname</name>
                <content/>
	     </extract></text>
          </set>
	  <set name="id"><text trim="yes">
	     <extract>
	       <from>&li;</from>
	       <attr>id</attr>
	       <eval />
	     </extract></text>
          </set>

	  <?-- create query string, and append the various auths --?>
	  <set name="sqlquery">INSERT INTO members VALUES ("&surname;",
                     "&givenname;", "&attributes:deptid;", "&id;" </set>

	  <?-- try looking for lowermost auth-type (e.g. manager) for 
               this person, using the just-created db tables  --?>
	  
          <repeat> <foreach>&VAR:uniqueAuths;</foreach>
               <set name="authid"><get-next-auth type="&li;"
               dept="&attributes:deptid;" /></set>
<?--	       READY: type=&li; and authid=&authid; <br /> --?>
    
               <set name="sqlquery">&sqlquery;, "&authid;"</set>
          </repeat>
          <set name="sqlquery">&sqlquery; )  </set>

        LAUNCHING query(&sqlquery;)<br/>


	  <!-- insert member data -->
	  <sql database="jdbc:mysql:///orgdb"
	      user="root" password="root">&sqlquery;</sql>


	  <!-- create a first notification -->
	  <sql database="jdbc:mysql:///orgdb"
	      user="root" password="root">
                      insert into notifications VALUES ("&id;", now() ) </sql>

        </repeat>
        <br />

  </action>
</define>

<define element="unpack-authorizers">
  <action>
      Unpacking authorizers from &attributes:deptname; (&attributes:deptid;)
      <br />

	<set name="authlist">
	    <extract>
	      <from>&content;</from>
	      <content />
	      <name>authorizer</name>
	    </extract>
	</set>

        <repeat>
          <foreach>&authlist;</foreach>
	   <set name="authtype"><text trim="yes">
	       <extract>
		 <from>&li;</from>
		 <attr>type</attr>
		 <eval />
	       </extract>
           </text></set>

	   <set name="memberid"><text trim="yes">
	       <extract>
		 <from>&li;</from>
		 <attr>memberid</attr>
		 <eval />
	       </extract>
           </text></set>

	  <!-- make the sql insert-query from extracted data -->
	  <sql database="jdbc:mysql:///orgdb"
	      user="root" password="root">
                      insert into authorizers VALUES ("&authtype;",
                      "&attributes:deptid;", "&memberid;", NULL) </sql>

        </repeat>

  </action>
</define>


</tagset>






