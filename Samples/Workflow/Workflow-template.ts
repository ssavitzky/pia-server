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
<!-- This code was initially developed by Ricoh Innovations, Inc.        -->
<!-- Portions created by Ricoh Innovations, Inc. are                     -->
<!-- Copyright (C) 1995-1999.  All Rights Reserved.                         -->
<!--                                                                        -->
<!-- Contributor(s):   bill softky bill@rii.ricoh.com                       -->
<!-- ---------------------------------------------------------------------- -->

<!-- create db of signatures by 
mysql> create table signatures ( owner VARCHAR(30), url VARCHAR(150),
 signed_by VARCHAR(30), to_be_signed_by VARCHAR(30), idx INT NOT NULL
 AUTO_INCREMENT, PRIMARY KEY (idx) ); 

mysql> insert into signatures values ("gene", "http://foo", "jane",
   "bcheese", NULL);   
 -->


<!-- create db of notifications by 
mysql> create table notifications (signer VARCHAR(30) NOT NULL,
 when_notified DATETIME NOT NULL,  PRIMARY KEY (signer) ); 

mysql> insert into notifications values ("gene", now() ); 

(this is the necessary initialization for each member; subsequent updates
 are updates, not inserts)  

and tested by 

mysql> select minute(now() ) < minute( when_notified) + 25 from notifications where signer="wolff";   
 -->


<tagset name="Workflow-template" parent="/Tagsets/pia-xhtml" recursive="recursive">
<?--
<!-- tagset name="Workflow-template" parent="/Agents/Workflow/Workflow-test.ts"  parent="Workflow-xhtml" recursive="recursive" -->
--?>
<cvs-id>$Id: Workflow-template.ts,v 1.3 2001-04-03 00:03:58 steve Exp $</cvs-id>

<define element="sql" handler="handler">

</define>



<define element="mailnow" handler="handler">

</define>


<define element=sign handler=sign >
  <doc> Creates and verifies digital signatures.<br />
  &lt;sign user=user [[password=password [create dname=distinguishedName]] |
  [verify=signature] | [exists]] [keyfile=filename
  [keypass=keypass]]&gt;content&lt;/sign&gt; <br />
 Default action is to sign the content using the specified users private
  key.  The Base64 encoded signature is returned.<br />
  If verify is specified, the provided signature (should also be Base64) for
  the contents is checked with the users public key.<br />
  If exists is specified, "True" is returned if and only if a key is stored
  for this user.<br />
  If create a specified, a new public/private key pair is generated and
  stored for this user.  (Any existing public key for this user will be stored under an
  alias of the form user-#.)  Currently uses Sun's KeyTool to create and
  store the public/private keys.<br />
  If newpasswd is specified, changes the old password into the new one. <br/>
     <ul> 
      <li>user specifies the alias for the new key</li>
   <li> file is the name of the file the keys are stored in</li>
   <li> keypass is the password for the private key</li>
   <li> storepass is the password for the key storage</li>
   <li> create specifies that a new key pair should be generated </li>
   <li> distinguishedName specifies the values that should be put into 
   (self signed) certificate -- should be of the form
   "CN=Common Name, OU=Organization Unit, O=Organization, L=Location, S=State, C=Country"</li>

   <li> password is the password for the private key</li>
   <li> validity is number of days newly created keys should be valid
   (default 3660=ten years)</li>
   </ul>

   To create the initial keystore, use 
      keytool -genkey -keystore [keyfile] -keypass [keypassword] -storepass
      [storepassword] -alias [user]

   (the file [keyfile] should reside in the directory where the pia runs)

   To verify the keystore's contents, use 
      keytool -list -keystore [keyfile] -storepass [storepassword] 

      REMEMBER to restart the PIA after any changes to the keystore, such as
      creating keys, changing passwords, etc.
  </doc>
</define>

<!-- tags which should be in the parent workflow tagset -->


<define element="test-viewing">
  <action>
    <test not="yes">
        <logical or="yes">
           <get name="FORM:edit"/>
           <get name="FORM:sign"/>
           <get name="FORM:submit"/> 
           <get name="FORM:save"/> 
           <get name="FORM:sign"/> 
           <get name="FORM:signed"/> 
           <get name="FORM:sig_verify"/> 
           <get name="FORM:verify"/> 
        </logical>
    </test>
  </action>
</define>

<define element="new-filename">
 <doc>Creates a new filename based on the type and date of the form</doc>
 <define attribute="type"><doc>the type of the form </doc> </define>
 <action><text trim="yes">
    <if><test not="yes" match="forms">&url;</test>
      <then><set name="prefix">../forms/&attributes:type;/</set></then>
      <else><set name="prefix">../../../../../forms/&attributes:type;/</set>
      </else>
    </if>
 <hide>
   <set name="postfix">&year;/&month;/&day;/&hour;.&minute;.&second;.form</set>
<!-- need to test for existence here -->
 </hide>
  <if><get name="FORM:signed">
         <then><get name="DOC:name"/></then>
         <else><get name="prefix"/><get name="postfix" /></else>
  </if>

  </text></action>
</define>

<define element="save-form">
 <doc>Saves a form into a file</doc>
 <define attribute="filename" quoted="yes"><doc>filename to save form into </doc> </define>
 <action>
  <set name="VAR:saving">saving file</set>
  <!-- should do file locking here -->
  <user-message > saving form &attributes:filename; </user-message>
  <output dst="&attributes:filename;"><expand>&content;</expand></output>
  <set name="VAR:saving"></set>
 </action>
</define>


<define element="get-signer-name">
<doc> Given a signer type and memberid, find the name of the signer </doc>
  <action><text trim="yes">
     <set name="authtype">&attributes:type;_auth</set>
     <extract>
       <from><sql database="jdbc:mysql:///orgdb"
	    user="root" password="root">SELECT &authtype;
	    from members  WHERE memberid="&attributes:memberid;"</sql></from>
       <name all="yes">&authtype;</name>
       <content />
      </extract>  
   </text></action>
</define>




<define element="add-db-signers">
  <doc> take the content (a list of signer types) and create rows in the DB's
        "signatures" table corresponding to it </doc>
  <action>
       <?-- need to create a dummy file so that "status" can return its
            absolute pathname for DB storage.  Use the VAR:filename
	    from earlier assignment to ensure the same name used
	    for saving and for DB storage.  --?>
       <set name="theFilename">&VAR:filename;</set>

     <?-- touch to make file for status --?>
      <if> <test not="yes"><status src="&theFilename;" item="exists" /></test>
          <then>
            <output dst="&theFilename;">dummy</output>
          </then>
      </if>

       <set name="theFilename"><status src="&theFilename;"
       item="path"/></set>
	
       <hide>
         <repeat><foreach>&content;</foreach>
            <set name="realName"><get-signer-name 
                memberid="&FORM:owner-user;"   type="&li;" /></set>
           <sql database="jdbc:mysql:///orgdb"
                user="root" password="root">INSERT INTO signatures VALUES
                ("&attributes:owner;", "&theFilename;", NULL, "&realName;",
		 "&li;", NULL)</sql>
         </repeat>
       </hide>
  </action>
</define>

<!-- tags specific to form templates and form completion -->

<define element="form-template" quoted="yes">
 <doc>This tag specifies a template form.  The action
 associated with this tag either populate the form with the submitted values
 or returns an appropriate HTML form element.</doc>
 <define attribute="type">
   <doc>the type of form </doc>
 </define>

 <action ><text trim="yes">

   <?-- if viewing/signing/sig_verifying, or if editing without having
   previously saved fields in FORM: fields, then load contents into FORM:
   fields for calculating subtotals --?>

  <?--<set name="thisFile"><include src="&DOC:name;"
  quoted="yes"/></set>--?>
   <set name="VAR:first_on_list"></set>

  <set name="thisFile"><include src="&DOC:name;" quoted="yes"/></set>
              
  <if><test-viewing/><get name="FORM:sig_verify"/><get name="FORM:sign"/>
       <then>
           <set name="numericlist">
              <extract><from>&thisFile;</from>
                <name all="yes">numeric-input</name>
              </extract>
           </set>
	   <repeat ><foreach>&numericlist;</foreach>
	       <set   name="attname"><extract><from>&li;</from>
		  <attr>name</attr><eval/></extract></set>
	       <set   name="val"><extract><from>&li;</from>
		  <content /></extract></set> 
               <set name="FORM:&attname;">&val;</set>
	   </repeat> 
        </then>
    </if>

   <?-- if purportedly signed, check that user/password was valid before
        committing to write a new file... a bad password changed "FORM:signed"
        back to "FORM:sign" to re-try password entry --?>
   <if> <get name="FORM:signed">
        <then>
          <set name="sigtest"><text trim="yes">
          <sign  user="&FORM:signed;"
	    keyfile=".keys.private"	    filepass="thespp"
	    password="&FORM:password;">dummy content</sign></text></set>
          <if><test match="Invalid">&sigtest;</test>
              <then>
                 <set name="FORM:sign"><get name="FORM:signed"/></set>
                 <set name="FORM:signed"></set>
              </then>
          </if>
        </then>
   </if>


<?--- assign filename here, so DB knows it before saving  --?>

    <if> <get name="FORM:submit" /> <get name="FORM:signed" />
	   <then>
       	<set name="VAR:filename"><new-filename 	type="&FORM:formtype;"/></set>
           </then>
    </if>

   <?-- now, if *still* signed, update the db entries to reflect new 
        signatures --?>

   <if> <get name="FORM:signed"/>
       <then>
	  <set name="fname"><status src="&VAR:filename;" item="path"/></set>

          <hide>
	  <set name="query">UPDATE signatures SET
                to_be_signed_by=NULL,   signed_by="&FORM:signed;"
                WHERE url="&fname;" 
		AND to_be_signed_by="&FORM:signed;"</set>
          <sql database="jdbc:mysql:///orgdb"
                user="root" password="root">&query;</sql></hide>

 <?-- find remaining signers after this one --?>
           <set name="query">SELECT to_be_signed_by FROM 
		signatures  WHERE url="&name;"
		 AND to_be_signed_by IS NOT NULL
		 ORDER BY idx LIMIT 1</set>
           <set name="dbresult"><sql database="jdbc:mysql:///orgdb"
                user="root" password="root">&query;</sql></set>
   	   <set name="VAR:first_on_list"><extract>
              <from>&dbresult;</from>
              <name all="yes">to_be_signed_by</name>
              <content/>
           </extract></set>

<?-- find remaining forms for this signer after this one --?>
           <set name="query">SELECT url FROM 
		signatures  WHERE to_be_signed_by="&FORM:signed;"</set>
           <set name="dbresult"><sql database="jdbc:mysql:///orgdb"
                user="root" password="root">&query;</sql></set>
   	   <set name="VAR:to-be-signed-urls"><extract>
              <from>&dbresult;</from>
              <name all="yes">url</name>
              <content/>
           </extract></set>

       </then>
   </if>

   <?-- if submitted, make a list of needed signatures and store into DB.
        We need to read in the file again, this time evaluating the 
	"conditional" tags before finding the signatures....and that
	evaluation requires using a different tagset to avoid getting in
	an infinitely recursive loop of "includes" --?>

   <set name="signerFile"></set>
<if><logical and="yes">&FORM:submit; <test not="yes">&VAR:included;</test>
         </logical>
     <then>
      <?-- first pass is to calcuate subtotals; second pass is to evaluate
           conditionals using those subtotals; only that pass contains the
	   right signatures from which to match signer_type with names--?>
         <set name="VAR:included">yes</set>
         <let name="dontsave">yes</let>
         <hide><expand><include src="&DOC:name;"
         quoted="yes"/></expand></hide>
         <set name="signerFile"><include src="&DOC:name;" 
	   tagset="nonRecursive"   /></set>
     </then>
</if>



   <set name="signerlist"></set>
  <if> <logical and="yes"><get name="FORM:submit"> 
        <test not="yes">&dontsave;</test></logical>
        <then> 
           <set name="signerTypelist"><text split="yes"  sep=" "><extract>
	            <from>&signerFile;</from>
		    <name all="yes">signature</name>
		    <attr>signer_type</attr><eval/>
	   </extract></text></set>
  
           <?-- convert the list of signer types into a list of signers--?>
	   <set name="signerList"> </set>
           <repeat><foreach>&signerTypeList;</foreach><text trim="yes">
                 <set name="signerList">&signerList;  <get-signer-name 
                          memberid="&FORM:owner-user;"   type="&li;"
                          /></set>
                 <set name="VAR:&li;"><get-signer-name 
                          memberid="&FORM:owner-user;"   type="&li;" /></set>
           </text></repeat>



	   <set name="VAR:first_on_list"><extract> <from>&signerlist;</from>
                 0
           </extract></set>

           <add-db-signers owner="&FORM:owner-user;">&signerTypeList;</add-db-signers>
       </then>
   </if> 

   <?-- actual saving and printout starts here --?>

  <if><get name="FORM:save" />
      <then>
	   <set name="VAR:filename"><new-filename type="&FORM:formtype;"
	   /></set>

	   <save-form type="&FORM:formtype;" filename="&filename;">
	       <form-template
       type="&FORM:formtype;"><expand>&content;</expand></form-template>
	   </save-form>
	   click <a href="&filename;"> here to view this form: &filename;</a>
	   <hr />
	  <expand>&content;</expand>
      </then>
 
      <else-if> <get name="FORM:submit" /> <get name="FORM:signed" />
       <!-- should check for match with type attribute -->
	   <then>
		<!-- populate the form, save it, and update the signature box -->


<?-- already assigned above		<set
<name="VAR:filename"><new-filename type="&FORM:formtype;" /></set> --?>



		<save-form type="&FORM:formtype;" filename="&filename;"><form-template type="&FORM:formtype;"><expand>&content;</expand></form-template></save-form>

	       <expand>&content;</expand>

	   </then>
        </else-if>
	<else> 
	     <form action="&url;" method="GET"> 
	     <input type="hidden" name="formtype" value="&attributes:type;" />
	     <expand>&content;</expand>
	     <if>&FORM:verify; &FORM:edit;  &FORM:submit;<test-viewing/>
		<then><input type="submit" name="verify" value="Verify" /></then>
	     </if>
	    <if> <test-viewing/> &FORM:verify;
	       <then><input type="submit" name="edit" value="Edit" /></then>
	    </if>
	    <if>&FORM:verify; &FORM:edit;  &FORM:submit;
	       <then><input type="submit" name="save" value="Save" /></then>
	    </if>
	    <if>&FORM:verify; &FORM:edit; &FORM:Save; 
	       <then><input type="submit" name="submit" value="Save &amp;
	       Process" /></then>
	    </if>
	    </form>
	</else>
  </if>




<hr />
<?-- list-making and db-storing used to be here --?>

   <if> <get name="FORM:signed"/> <get name="FORM:submit"/> 
      <then>
<?-- get last time this signer looked at forms, to decide whether to mail 
     them a notification. The result has a weird column name, so extract
     it by depth. --?>

          <hide> <?-- set mail delay here --?>
	     <set name="query">SELECT ( now()  - 
	     when_notified) > 1000000000 FROM notifications WHERE 
	     signer="&VAR:first_on_list;"</set>

	     <set name="dbresult">
             <sql database="jdbc:mysql:///orgdb"
                user="root" password="root">&query;</sql></set></hide>
             <set name="VAR:yesmail"><extract>
                  <from>&dbresult;</from>
		  <name all="yes">SQL:row</name>
		  <content/>
		  <name all="yes">#element</name>
		  <content/>
              </extract></set>

<?-- If last notification stale enough to send mail, reset it now --?>
             <if><test match="1">&VAR:yesmail;</test>
                <then> 
		   <set name="query">UPDATE notifications SET
		     when_notified=NOW() WHERE
		      signer="&VAR:first_on_list;"</set>
		   <hide><sql database="jdbc:mysql:///orgdb"
		     user="root" password="root">&query;</sql></hide>
                 </then>
              </if>

       </then>
   </if>

  <?-- FINALLY, mail out and show who needs to sign --?>
  <if> <get name="FORM:signed" /><get name="FORM:submit"/><then>
      <if><get name="VAR:first_on_list" />
	   <then>
		<set name="theFilename"><status src="&filename;"
		item="path"/></set>
		<if><test match="1">&VAR:yesmail;</test><then>
                    Signer notified; 
		    <mailnow to="&VAR:first_on_list;"
			 subject="Form to sign"  >
			 &VAR:first_on_list;, sign this form:
			 &PIA:url;&theFilename;?sign=&VAR:first_on_list;
		    </mailnow></then>
		    <else> Signer will be notified </else>
                </if>
		<br/>Saved as: <a href="&PIA:url;&theFilename;">
		&PIA:url;&theFilename; </a>
	   </then>
	   <else>
		VERIFY this form at  
                <a href="&filename;?sig_verify=yes">&filename;</a>  
		<br />		<br />
		&FORM:signed;, you can also sign the following forms: <br/>

		<repeat><foreach>&VAR:to-be-signed-urls;</foreach>
                     <a href="&li;?sign=&FORM:signed;">&li;</a>  <br/>
                </repeat>
	   </else>
      </if>
  </then></if>
  <hr />
 </text></action>
</define>  <?-- end of form-template definition --?>


<doc>Form elements and sections </doc>

<define element="subtotal"> 
  <doc>A main section of a form -- numeric inputs will be subtotaled in each section</doc>
  <action mode="replace-content"><text trim="yes">

        <if><test-viewing/>     <get name="FORM:sign"/> 
	         <get name="FORM:signed"/>    <get name="FORM:sig_verify"/> 
           <then>
	      <get name="content"/>
           </then>
           <else>
	       <set name="mylist"><text
	       split="yes">&attributes:src;</text></set>
               <?-- look for variables both in FORM: and in VAR: --?>
	       <set name="tempSum"><numeric sum="yes"><repeat>
		  <foreach>&mylist;</foreach> 
		     <get name="FORM:&li;"/>
		     <get name="VAR:&li;"/>
		</repeat></numeric></set>
                &tempSum;

<?--
	       <repeat><foreach>&mylist;</foreach><if><test match="oops"><get
	       name="FORM:&li;" default="oops">oops</get></test><then><font
	       color="red">Subtotal field &li; can't be
	       found</font></then></if></repeat>
--?>
	       <if>  <get name="attributes:name" />
                 <then><set name="VAR:&attributes:name;">&tempSum;</set></then>
               </if>
           </else>
         </if>
  </text></action>
</define>



<define element="multiply"> 
  <doc>Multiplies two numbers and keeps result in a variable</doc>
  <action mode="replace-content"><text trim="yes">

        <if><test-viewing/>     <get name="FORM:sign"/> 
	                <get name="FORM:signed"/>    <get name="FORM:sig_verify"/> 
           <then>
	      <get name="content"/>
           </then>
           <else>
               <set name="dflt1">0</set>
               <set name="dflt2">0</set>

               <if> <get name="attributes:dflt1"/>
                   <then><set name="dflt1"><get name="attributes:dflt1"/></set>
		   </then>
               </if>
               <if> <get name="attributes:dflt2"/>
                   <then><set name="dflt2"><get name="attributes:dflt2"/></set>
		   </then>
               </if>

               <if> <get name="FORM:&attributes:src1;"/>
                   <else><set name="FORM:&attributes:src1;">&dflt1;</set>
		   </else>
               </if>
               <if> <get name="FORM:&attributes:src2;"/>
                   <else><set name="FORM:&attributes:src2;">&dflt2;</set>
		   </else>
               </if>
                    
	       <set name="tempProd"><numeric product="yes">
	            <get name="FORM:&attributes:src1;"/>
	            <get name="FORM:&attributes:src2;"/>
		</numeric></set>
                &tempProd;

	       <if>  <get name="attributes:name" />
                 <then><set name="VAR:&attributes:name;">&tempProd;</set></then>
               </if>
           </else>
         </if>
  </text></action>
</define>


<define element="option-multiply"> 
  <doc>Multiplies a given number by one assigned by text; stores result</doc>
  <action mode="replace-content">

        <if><test-viewing/>    <get name="FORM:sign"/> 
	         <get name="FORM:signed"/>    <get name="FORM:sig_verify"/> 
           <then>
	      <get name="content"/>
           </then>
           <else>
             <?-- see if there is an input by this name somewhere --?>
               <if> <get name="FORM:&attributes:src;"/>
                   <else><set name="FORM:&attributes:src;">0</set>
		   </else>
               </if>
               <?-- the number is the value of the attribute with
                    the option's name, e.g. Japan="37" in tag --?>
              <set name="optionname">FORM:&attributes:optsrc;</set>
	       <set name="whichop"><get name="&optionname;"/></set>
	       <if>&whichop;<then>
		  <set name="option-val">
		      <get name="attributes:&whichop;" /> 
		  </set></then>
                  <else><set name="option-val">0</set></else>
               </if>

	       <set name="tempProd"><numeric product="yes">
	            <get name="FORM:&attributes:src;"/>
	            <get name="option-val"/>
		</numeric></set>
                &tempProd;

	       <if>  <get name="attributes:name" />
                 <then><set name="VAR:&attributes:name;">&tempProd;</set></then>
               </if>
           </else>
         </if>
  </action>
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


<define element="daycheck" >
  <action>

         <set name="firstday">1</set>
         <if><get name="attributes:firstday" />
             <then><set name="firstday">&attributes:firstday;</set></then>
         </if>
         <logical and="yes">
            <test not="yes" match="Saturday">&attributes:weekday;</test>
            <test not="yes" match="Sunday">&attributes:weekday;</test>
            <test greater="&firstday;"><numeric
            sum="yes">&attributes:testday; 1</numeric></test>
            <test less="&attributes:lastday;"><numeric difference="yes"> &attributes:testday; 1</numeric></test>
         </logical>
  </action>
</define>      

<define element="calendar" quoted="yes">
   <doc> This tag's content is repeated on each day if a tabular calendar
   </doc>

   <action>
      <set name="startday">1</set>
      <if><get name="attributes:startday" />
             <then><set name="startday">&attributes:startday;</set></then>
      </if>

      <table cellspacing="1"  cellpadding="2" border="0" width="100%">

            <tr>
               <td align="center"><tt>--Sunday---</tt></td>
               <td align="center"><tt>--Monday---</tt></td>
               <td align="center"><tt>--Tuesday--</tt></td>
               <td align="center"><tt>-Wednesday-</tt></td>
               <td align="center"><tt>--Thursday-</tt></td>
               <td align="center"><tt>--Friday---</tt></td>
               <td align="center"><tt>-Saturday--</tt></td>
            </tr>
                     

            <set name="thisMonth">&startmonth;</set>

            <set name="offset"><weekday startsun="yes">
                   <day>15</day><month>&attributes:startmonth;</month>
                   <year>&attributes:startyear;</year>
            </weekday></set>
            <set name="lastday"><weekday lastday="yes">
                 <day>15</day><month>&attributes:startmonth;</month>
                 <year>&attributes:startyear;</year>
            </weekday></set>
	    <if><get name="attributes:weeks" />
		   <then><set name="lastday"><numeric sum="yes"><numeric
		   product="yes">&attributes:weeks; 7</numeric> &startday;</numeric></set></then>
	    </if>

            <?-- week loop --?>
            <repeat start="&offset;" entity="m" stop="&lastday;" step="7">

               <set name="stopDay"><numeric sum="sum">&m; 6</numeric></set>
               <tr>
                 <?--  day loop --?>
                   <repeat start="&m;" stop="&stopDay;"><text trim="yes">
                       <set name="myDay">&n;</set>
                       <set name="daycolor">azure</set>
		       <set name="weekday"><weekday><day>&n;</day>
				<month>&attributes:startmonth;</month>
				 <year>&attributes:startyear;</year> </weekday>
		       </set>

                       <if><daycheck testday="&myDay;" lastday="&lastday;" 
                              firstday="&startday;" weekday="&weekday;"/>
                           <then><text trim="yes">
                              <td valign="top"  bgcolor="&daycolor;">
                                       &myDay;
                              <expand><get name="content" /></expand></td>

                           </text></then>
                           <else><td /></else>
                       </if>
                   </text></repeat>
               </tr>
            </repeat>

         </table>     
   </action>
</define>


<define element="form-section"> 
  <doc>A main section of a form -- numeric inputs will be subtotaled in each section</doc>
  <action mode="replace-content"><text trim="yes">

  <?-- re-read the document to get all form "contents", to sign below --?>
   <if><get name="FORM:signed"/><get name="FORM:sig_verify"/>
      <then>
         <set name="VAR:to-be-signed">
	   <extract> <from><include src="&DOC:name;" quoted="yes">oops!</include></from>
              <name all="yes">form-section</name>
            </extract> 
         </set>
      </then>
   </if>
   &content;
  </text></action>
</define>

<!---- userinput -->

<define element="load-user-from-file">
   <doc> Creates hidden input elements from fields of user-input </doc>
   <action><text trim="yes">
             <set name="user-name">
             <extract><from>&content;</from>
                <name all="yes">user</name>
                <content />
             </extract>
          </set>
          <?-- if not content, grab current user from authentication --?>
          <if><get name="user-name"/>
             <else>
	         <set name="user-name">&REQ:AuthenticatedUser;</set>
             </else>
          </if>
	  <!-- read from xml file -->
	  <br />
	  User: &user-name; 
<?--	  <input type="hidden" name="&attributes:name;-user" --?>
	  <input type="hidden" name="owner-user" value="&user-name;" /> 
	  <set name="user-auth">
	     <extract><from>&content;</from>
		<name all="yes">authorizer</name>
		<content />
	     </extract>
	  </set>
	  Authorizer: &user-auth;
<?--	  <input type="hidden" name="&attributes:name;-auth" --?>
	  <input type="hidden" name="owner-auth" value="&user-auth;" /> 
	  <set name="user-dept">
	     <extract><from>&content;</from>
		<name all="yes">dept</name>
		<content />
	     </extract>
	  </set>
	  Dept: &user-dept;
<?--	  <input type="hidden" name="&attributes:name;-dept" --?>
	  <input type="hidden" name="owner-dept" value="&user-dept;" /> 
 
   </text></action>
</define>

<?-- verify-user takes the user name and pulls everthing else out of the DB,
     stuffing it into FORM: fields and showing on the screen --?>

<define element="verify-user">
   <doc> Checks user ID and loads from DB if necessary </doc>
   <action mode="replace-content"><text trim="yes">
<?-- 	 <set name="userid"><get name="FORM:&attributes:name;-user" /></set>
--?>
 	 <set name="userid"><get name="FORM:owner-user" /></set>

         <set name="dbresult"> 
            <extract><from><sql database="jdbc:mysql:///orgdb"
                user="root" password="root">SELECT * FROM
                    members WHERE memberid="&userid;"</sql></from>
                <name>SQL:result</name>
                <content/>
                <name>SQL:row</name>
                <content/> 
            </extract>
         </set>

         <if><get name="dbresult"/>
              <else><font color="red">User &userid; not in
              database</font></else>
         </if>
         <if><get name="FORM:edit"/> <test not="yes"><get name="dbresult"/></test>
<?--            <then><input name="&attributes:name;-user" --?>
              <then><input name="owner-user"  value="&userid;"/></then>
      <?--       <else><input name="&attributes:name;-user"  --?>
              <else><input name="owner-user" type="hidden" value="&userid;"/>
                <br />
                <extract><from>&dbresult;</from>
                        <name all="yes">given_name</name>
                </extract> <nbsp/>
                <extract><from>&dbresult;</from>
                        <name all="yes">surname</name>
                </extract>  <nbsp />
                ( <nbsp/> <extract><from>&dbresult;</from>
                        <name all="yes">memberid</name>
                </extract> <nbsp/>) 
		<br />
                Dept: <extract><from>&dbresult;</from>
                        <name all="yes">deptid</name>
                </extract> <nbsp />
              </else>
         </if>


         <!-- copy form value into new inputs if they exist -->
<?--         <if><get name="FORM:&attributes:name;-dept"/> --?>
         <if><get name="FORM:owner-dept"/>
            <then>
<?--               <set name="newdept"><get
<name="FORM:&attributes:name;-dept"/></set> --?>
               <set name="newdept"><get name="FORM:owner-dept"/></set>
            </then>
            <else>
               <set name="newdept"><extract><from>&dbresult;</from><name
               all="yes">deptid</name><content/></extract></set>
            </else>
          </if>
<?--          <input type="hidden" name="&attributes:name;-dept" --?>
          <input type="hidden" name="owner-dept"
          value="&newdept;" />

<?--         <if><get name="FORM:&attributes:name;-auth"/> --?>
         <if><get name="FORM:owner-auth"/>
            <then>
<?--               <set name="newauth"><get
<name="FORM:&attributes:name;-auth"/></set> --?>
               <set name="newauth"><get name="FORM:owner-auth"/></set>
            </then>
            <else>
               <set name="newauth"><extract><from><sql 
	       database="jdbc:mysql:///orgdb"
                user="root" password="root">SELECT authid FROM
                    authorizers WHERE dept="&newdept;"</sql></from><name
               all="yes">authid</name><content/></extract></set>
            </else>
          </if>
<?--          <input type="hidden" name="&attributes:name;-auth"
<value="&newauth;" /> --?>
          <input type="hidden" name="owner-auth" value="&newauth;" />

   </text></action>
</define>

<define element="user-input">
 <doc>Collects the name of a user, and fills in other info from database</doc> 
 <define attribute="name"> <doc>the variable name </doc> </define>
     <action mode="replace-content"><text trim="yes">
        <if><test-viewing /> <get name="FORM:sign"/> 

           <then>
              <load-user-from-file name="&attributes:name;" >&content;</load-user-from-file>
           </then>
        </if>

<?-- piece patched in --?>
        <if><logical and="yes">
             <get name="FORM:edit"/>
             <test not="yes"><get name="FORM:owner-user" /></test>
            </logical>
            <then>  
               <set name="FORM:owner-user">&REQ:AuthenticatedUser;</set>
            </then>
        </if>

        <if><get name="FORM:verify"/> <get name="FORM:edit"/>
          <then><verify-user name="&attributes:name;"/></then>
        </if>

        <if><get name="FORM:submit"/> <get name="FORM:save" />
          <then>
<?--               <user><get  name="FORM:&attributes:name;-user"/></user>
<--?>
               <user><get  name="FORM:owner-user"/></user>
<?--               <authorizer><get
<name="FORM:&attributes:name;-auth"/></authorizer>
--?>
               <authorizer><get  name="FORM:owner-auth"/></authorizer>
<?--               <dept><get  name="FORM:&attributes:name;-dept"/></dept>
<--?>
               <dept><get  name="FORM:owner-dept"/></dept>
          </then>
        </if>

        <if> <get name="FORM:signed"/> <get name="FORM:sig_verify"/> 
          <then>&content;</then>
        </if>

     </text></action>
</define>

<!-- end user-input -->



<!---- numeric input -->

<define element="verify-numeric">
    <action>

          <set name="temp"><get name="FORM:&attributes:name;"/></set>
          <if><test numeric="yes">&temp;</test>
            <then>
                 <if>
                    <test greater="&attributes:max;">&temp;</test>
                    <test less="&attributes:min;">&temp;</test>
                    <then>
		       <font color="red">Allowed range 
                           &attributes:min; to &attributes:max; </font>       
		       <input name="&attributes:name;" value="&temp;" /> 
		    </then>
                    <else>
			&temp;
			<input type="hidden" name="&attributes:name;"
			value="&temp;" /> 
                    </else>
                  </if>
            </then>
            <else-if>&temp;
               <then>
		   <font color="red"> Not a number</font>            
		   <input name="&attributes:name;" value="&temp;" /> 
               </then>
            </else-if>
	    <else-if>  &attributes:required;
               <then>
		   <font color="red">Required</font>            
		   <input name="&attributes:name;" value="&temp;"  /> 
               </then>
	    </else-if>
          </if>
    </action>
</define>

<define element="numeric-input">
 <doc>field denoting numeric input -- show input field or set value as entered </doc> 
 <define attribute="name"> <doc>the variable name </doc> </define>
 <define attribute="min"> <doc>minimum allowed value </doc> </define>
 <define attribute="max"> <doc> maximum allowed value </doc> </define>
     <action mode="replace-content"><text trim="yes">
        <if><test-viewing />  <get name="FORM:sign"/> 

           <then><get name="content" />
	      <input type="hidden" name="&attributes:name;"
	      value="&content;" /> 
           </then>
        </if>

        <if><get name="FORM:verify"/>
          <then>
             <verify-numeric name="&attributes:name;"
			 max="&attributes:max;"
			 min="&attributes:min;"
			 required="&attributes:required;" 
			  />
          </then>
        </if>

       <if><get name="FORM:edit"/>
           <then> 
              <if>&attributes:required;
                  <then>
                     <font color="red">*</font>
                  </then>
              </if>


<?--              <set name="temp"><get name="FORM:&attributes:name;" /> </set>--?>

<?-- piece patched in --?>
    <set name="temp"><if><get name="FORM:&attributes:name;" /> 
                  <then><get name="FORM:&attributes:name;" /> </then>
                  <else><get name="content" /> </else></if></set> 

	      <set name="size"><get name="attributes:size" /></set>
	      <if> <get name="size" /><else><set name="size">2</set></else></if>
              <input name="&attributes:name;" size="&size;"
                             value="&temp;" /> 
            </then>
       </if>

        <if><get name="FORM:submit"/> <get name="FORM:save" />
          <then>
               <get  name="FORM:&attributes:name;"/>
          </then>
        </if>
        <if> <get name="FORM:signed" /><get name="FORM:sig_verify"/>
          <then><get name="content" /></then>
        </if>

     </text></action>
</define>

<!-- end numeric input -->

<!-- single-line text input -->

<define element="verify-text">
    <action><text trim="yes">
          <set name="temp"><get name="FORM:&attributes:name;"/></set>
          <set name="toShow">"<get name="attributes:required"/>"</set>
	  <if><test match="." >&toShow;</test>
              <then><set name="toShow"> </set></then>
          </if>

          <if><logical and="yes">&attributes:reject; <test match="&attributes:reject;">&temp;</test></logical>
            <then>
		<font color="red">Reject "&attributes:reject;"</font>
		<input name="&attributes:name;" value="&temp;"
                       size="&attributes:size;" /> 
            </then>
            <else-if><logical and="yes">
                        <get name="attributes:required"/>
                        <test not="yes" 
                          match="&attributes:required;">&temp;</test>
                     </logical>
		<then>
                     <font color="red">&toShow; Required</font>
		     <input name="&attributes:name;" value="&temp;"
                            size="&attributes:size;" /> 
                </then>
            </else-if>
	    <else>
		&temp;
		<input type="hidden" name="&attributes:name;"
		value="&temp;" /> 
	    </else>
          </if>
    </text></action>
</define>

<define element="text-input">
 <doc>field denoting text input -- show input field or set as entered </doc> 
 <define attribute="name"> <doc>the variable name </doc> </define>
 <define attribute="type"> <doc>what kind of text field </doc> </define>
 <action mode="replace-content"><text trim="yes">

    <set name="size"><get name="attributes:size" /></set>
    <if> <get name="size" /><else><set name="size">20</set></else></if>

  <if><get name="FORM:edit" />
      <then>
        <set name="temp"><protect markup="yes" result="yes"><get
        name="FORM:&attributes:name;"/></protect></set>
	<if>&attributes:required;<then><font color="red">*</font></then></if>
        <input name="&attributes:name;" value="&temp;" 
	        size="&size;" />
      </then>
  </if>

   <if><test-viewing /> <get name="FORM:sign"/> 

        <then><get name="content" />
             <input type="hidden" name="&attributes:name;"
             value="&content;" />
        </then>
   </if>

   <if><get name="FORM:verify"/> 
        <then>
          <set name="req"><get name="attributes:required"/></set>
          <set name="rej"><get name="attributes:reject"/></set>
	  <verify-text  name="&attributes:name;"
                            required="&req;"
			    size="&size;"
                            reject="&rej;"><get name="content"/></verify-text></then>
   </if>

   <if><get name="FORM:submit"/><get name="FORM:save"/>
         <then><get name="FORM:&attributes:name;"/> </then>
   </if>
   <if> <get name="FORM:signed"/> <get name="FORM:sig_verify"/> 
         <then><get name="content" /></then>
   </if>
 </text></action>
</define>

<!-- end single-line text input -->


<!-- text-area input -->

<define element="verify-textarea">
    <action><text trim="yes">
          <set name="temp"><get name="FORM:&attributes:name;"/></set>
          <set name="toShow">"<get name="attributes:required"/>"</set>
	  <if><test match="." >&toShow;</test>
              <then><set name="toShow"> </set></then>
          </if>
          <if><logical and="yes">&attributes:reject; <test match="&attributes:reject;">&temp;</test></logical>
            <then>
		<font color="red">Reject "&attributes:reject;"</font>
                <textarea rows="&attributes:rows;" cols="&attributes:cols;" name="&attributes:name;">&temp;</textarea>
            </then>
            <else-if><logical and="yes">
                        <get name="attributes:required"/>
                        <test not="yes" 
                          match="&attributes:required;">&temp;</test>
                     </logical>
		<then>
                     <font color="red">&toShow; Requiredd</font>
                     <textarea rows="&attributes:rows;" cols="&attributes:cols;" name="&attributes:name;">&temp;</textarea>
                </then>
            </else-if>
	    <else>
		&temp;
		<input type="hidden" name="&attributes:name;"
		value="&temp;" /> 
	    </else>
          </if>
    </text></action>
</define>

<define element="textarea-input">
 <doc>field denoting text input -- show input field or set as entered </doc> 
 <define attribute="name"> <doc>the variable name </doc> </define>
 <define attribute="type"> <doc>what kind of text field </doc> </define>
 <action mode="replace-content"><text trim="yes">
  <if><get name="FORM:edit"/>  
      <then><textarea name="&attributes:name;" rows="&attributes:rows;"
              cols="&attributes:cols;"><protect markup="yes" result="yes"><get
      name="FORM:&attributes:name;"/></protect></textarea></then>
  </if>

   <if><test-viewing /> <get name="FORM:sign"/> 

        <then><get name="content" />
             <input type="hidden" name="&attributes:name;"
             value="&content;"/>
        </then>
   </if>

   <if><get name="FORM:verify"/> <then>
          <set name="req"><get name="attributes:required"/></set>
          <set name="rej"><get name="attributes:reject"/></set>
	  <verify-textarea  name="&attributes:name;"
	                    rows="&attributes:rows;"
	                    cols="&attributes:cols;"
                            required="&req;"
                            reject="&rej;">&content;</verify-textarea></then></if>

   <if><get name="FORM:submit"/> <get name="FORM:save"/>
         <then><get name="FORM:&attributes:name;" /> </then>
   </if>
   <if> <get name="FORM:signed"/><get name="FORM:sig_verify"/> 
         <then><get name="content" /></then>
   </if>

 </text></action>
</define>

<!-- end text-area input -->

<!-- text-options input -->

<define element="verify-textoptions">
    <action><text trim="yes">
          <set name="temp"><get name="FORM:&attributes:name;"/></set>
          <if><extract><from>&temp;</from>1</extract>
            <then>
		<font color="red">Select one</font>
                <select
                    name="&attributes:name;"><repeat><foreach>&temp;</foreach><option>&li;</option></repeat></select>

            </then>
	    <else>
		&temp;
		<input type="hidden" name="&attributes:name;"
		value="&temp;" /> 
	    </else>
          </if>
    </text></action>
</define>

<define element="textoptions-input">
 <doc>field denoting text input -- show input field or set as entered </doc> 
 <define attribute="name"> <doc>the variable name </doc> </define>
 <define attribute="type"> <doc>what kind of text field </doc> </define>
 <action mode="replace-content"><text trim="yes">

  <set name="selected"> </set>
  <if><get name="FORM:edit"/> <then><select
     name="&attributes:name;"><repeat><foreach><protect markup="yes"
     result="yes"><get
     name="attributes:options"/></protect></foreach>
     <if><test match="&li;" exact="yes"><get name="FORM:&attributes:name;"/></test>
          <then><set name="selected">selected</set></then>
          <else><set name="selected"> </set></else></if><option selected="&selected;" >&li;</option></repeat></select></then></if>

   <if><test-viewing />  <get name="FORM:sign"/> 
        <then><get name="content"/>
             <input type="hidden" name="&attributes:name;"
             value="&content;"/>
        </then>
   </if>


   <if><get name="FORM:verify"/> <then><verify-textoptions
                            name="&attributes:name;">&content;</verify-textoptions></then></if>

   <if><get name="FORM:submit"/> <get name="FORM:save"/>
         <then><get name="FORM:&attributes:name;" /> </then>
   </if>
   <if> <get name="FORM:signed"/><get name="FORM:sig_verify"/> 
         <then><get name="content" /></then>
   </if>

 </text></action>
</define>

<!-- end text-options input -->

<!-- signature input -->

<define element="signature"><doc>place holder for a signature on this
document, may include tests for relevant conditions </doc> 
 <action>
     <make name="signature">
        <let name="signer_type"><get name="attributes:signer_type"/></let>
	<if><get name="FORM:submit"/>
          <then><let name="signer"><get
          name="VAR:&attributes:signer_type;"/></let>
          </then>
	  <else>
             <let name="signer"><get
             name="attributes:signer"/></let>
          </else>
         </if>
        <let name="..."><text trim="yes">

 <if><test-viewing/>
     <get name="FORM:verify" />
     <get name="FORM:edit" />
       <then><tr><th>Signature: </th><td><font color="green">&content;</font></td></tr><tr><td />
       <td>
         <if><get name="attributes:signer" />
             <then><get name="attributes:signer" /></then>
             <else><get name="attributes:signer_type" /></else>
         </if></td></tr>
    </then>
 </if>

 <if><get name="FORM:submit"/><get name="FORM:save"/> 
    <then><get name="content" /></then>
 </if>


 <if><get name="FORM:signed"/>
    <then>
      <if><test match="&FORM:signed;">&attributes:signer;</test>
          <then>
            <set name="sigcode"><text trim="yes">
	    <sign  user="&FORM:signed;"
	    keyfile=".keys.private"	    filepass="thespp"
	    password="&FORM:password;"><get name="VAR:to-be-signed"
	    quoted="yes" /></sign></text></set>
            <if><test match="Invalid">&sigcode;</test>
               <?-- this should never happen --?>
               <then><font color="red">Retype password for &FORM:signed; <input
          type="password" name="password"/></font>
	       <input type="submit" value="Click here to submit"/>
	       <input type="hidden" name="signed" value="&FORM:signed;" />
               </then>
               <else>
                  &sigcode;
               </else>
            </if>
          </then>

          <else><get name="content"/></else>
       </if>
    </then>
 </if> 


 <if><get name="FORM:sign"/>
    <then>
      <if><test match="&FORM:sign;">&attributes:signer;</test>
          <then><br/><font color="red">Signature for &FORM:sign; <input
          type="password" name="password"/></font>
	  <input type="submit" value="Click here to sign"/>
	  <input type="hidden" name="signed" value="&FORM:sign;" />
	  </then>

          <else><br/><font color="green">Signature for &FORM:sign; : &content;</font></else>
       </if>
    </then>
 </if> 


 <if><get name="FORM:sig_verify"/>
    <then>
       <set name="confirm"><sign  user="&attributes:signer;"
	    keyfile=".keys.private"  filepass="thespp"
	    verify="&content;"><get name="VAR:to-be-signed" 
	    quoted="yes"/></sign></set>
       <if ><test match="True">&confirm;</test>
          <then><font color="green">Confirmed signature for
          &attributes:signer;  </font></then>
          <else><font color="red">NO confirmed signature for
          &attributes:signer;  </font></else>
       </if>
    </then>
 </if> 
 

 </text></let></make></action>
</define>

<!-- end signature input -->


<define element="change-passwd">

<doc> This places password entry boxes, and upon processing verifies the
      old password and changes it to the new one. This is NOT meant to
      be used inside a form-template tag, but free-standing.</doc>

<action>
<b> Change password </b> <br/>
<?----  change password here: first test if already signed successfully;
	if not, just ask again --?>
<set name="sigtest"> </set>

<if> <logical and="yes">&FORM:submitPasswd;
     <test match="&FORM:newpasswd;" exact="yes">&FORM:newpasswd2;</test>
     </logical>
   <then>
      <set name="sigtest"><text trim="yes">
      <sign  user="&attributes:user;"
	keyfile=".keys.private"	    filepass="thespp"
	password="&FORM:oldpasswd;" >dummy content</sign></text></set>
      <if><test match="Invalid">&sigtest;</test>
	  <then>
	     <set name="FORM:submitPasswd"> </set>
	  </then>
      </if>
   </then>
   <else> <set name="FORM:submitPasswd"> </set></else>
</if>

<br/>

<if> <test not="yes">&FORM:submitPasswd;</test>
   <then>
      <form action="&url;" method="get">
         Old password:	 <input name="oldpasswd" type="password" /> 
         New password:	 <input name="newpasswd" type="password" /> 
         (again)     :	 <input name="newpasswd2" type="password" /> <br/>
	 <input name="submitPasswd" type="submit" value="Change my password" />
      </form>
   </then>
   <else>
    <set name="sigtest">
      <sign  user="&attributes:user;"
	keyfile=".keys.private"	    filepass="thespp"
	password="&FORM:oldpasswd;" 
	newpasswd="&FORM:newpasswd;" > </sign></set>
 
     <if><test match="Invalid">&sigtest;</test>
          <then>&sigtest;</then>
	  <else>
      Congratutulations.... your new password will become effective as soon
      as the PIA server is restarted.
          </else>
      </if>
   </else>
 </if>
</action>
</define>

</tagset>

