<!doctype tagset system "tagset.dtd">
<tagset name=tt parent=xhtml recursive>

<define element='yyy'><action>Y&bar;Y<get name='bar'/>Y</action></define>
<define entity='baz'><value>BAZ</value></define>

<define element=duck> </define>
<duck> duck! </duck>
<dog> dog </dog>

<define element='frob'>
  <action>Expanding frob:
	  entity bar: '&bar;' get bar: '<get name='bar'/>'
	  entity baz: '&baz;' get baz: '<get name='baz'/>'
	  attrs: "&attributes;"  content: "&content;"
	  defined element: <yyy/>
  </action> 
</define>

<doc> for some reason, doc kills define</doc>

<define element='fac'>
  <action><if><test zero>&content;</test>
	    <then>1</then>
	    <else><numeric product>&content;
			<fac><numeric difference>&content; 1</fac></else>
	  </if></action>
</define>
</tagset>
