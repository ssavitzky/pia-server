<make type=DOCTYPE>html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"</make>
<html><head>
<title>Proxie Initialization file</title>
</head><body>
<h1>Proxie Initialization file</h1>

<p> This is run by the top-level initialization file.  A copy should be made
    in every subdirectory of /Agents that contains sub-agents.
</p>

<define element="init-children">
  <doc> Descend into the children of a directory and force each to initialize
	itself. 
  </doc>
  <action>
    <user-message>  Initializing in &content;</user-message>
    <repeat><foreach entity="f"><status item="files" src="&content;"/></foreach>
	    <status item="path" src="&content;/&f;" />
    </repeat>
  </action>
</define>

<init-children>/Agents/Proxie</init-children>

<hr/>
<b>Copyright &copy; 1997-1999 Ricoh Silicon Valley</b><br>
<b>$Id: initialize.inc,v 1.2 1999-11-23 18:40:55 steve Exp $</b><br/>
</body></html>
