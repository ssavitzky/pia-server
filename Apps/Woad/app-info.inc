<blockquote>
<!-- This really wants to be a surrounding table with the app name,
     description, and summary in it
 -->
<table bgcolor="ffffff" cellpadding="2">
  <tr>
    <td bgcolor="#99ccff"> &nbsp; </td>
    <th colspan="2" align="left" bgcolor="#99ccff">
        <ss>WOAD</ss> Application Information
    </th>
  </tr>
  <tr>
    <th bgcolor="silver"> parameter </th>
    <th bgcolor="silver"> value </th>
    <th bgcolor="silver"> command-line option </th>
  </tr>
  <tr>
    <th bgcolor="silver"> target Server </th>
    <td> <get name="SITE:targetServer">&nbsp;</get>
    </td>
    <td> <code>target=<em><get name="SITE:targetServer">[host:port]</get></em>
	 </code> 
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> annotation Prefix </th>
    <td>  <get name="SITE:notesPrefix">&nbsp;</get>
    </td>
    <td> <em>set in site config file</em>
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> source Prefix </th>
    <td>  <get name="SITE:sourcePrefix">&nbsp;</get>
    </td>
    <td> <code>prefix=<em><get name="SITE:sourcePrefix">/path</get></em></code>
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> source Root </th>
    <td> &SITE:sourceRoot;
    </td>
    <td> <code>sources=<em>&SITE:sourceRoot;</em></code>
	 <!-- should probably do some configuration checking  -->
	 <!-- to see exactly where this parameter was set     -->
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> document Offset </th>
    <td>  <get name="SITE:docOffset">&nbsp;</get>
    </td>
    <td> <code>offset=<em><get name="SITE:docOffset">/path</get></em>
	 </code>
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> Annotation root </th>
    <td>  &SITE:file;
    </td>
    <td> <code>-root <em>&SITE:file;</em></code> </td>
  </tr>
</table>
</blockquote>

<inc-footer cvsid="$Id: app-info.inc,v 1.3 2000-06-18 17:40:50 steve Exp $">
