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
    <td bgcolor="#99ccff"> <a href="about#parameters">[help]</a> </td>
  </tr>
  <tr>
    <th bgcolor="silver"> <em>parameter</em> </th>
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

<p></p>
<hr />

<table bgcolor="ffffff" cellpadding="2">
  <tr>
    <td bgcolor="#99ccff"> &nbsp; </td>
    <th colspan="2" align="left" bgcolor="#99ccff">
        <ss>WOAD</ss> Tree Structure Parameters
    </th>
    <td bgcolor="#99ccff"> <a href="about#parameters">[help]</a> </td>
  </tr>
  <tr>
    <th bgcolor="silver"> <em>parameter</em> </th>
    <th bgcolor="silver"> value </th>
    <th bgcolor="silver"> description </th>
  </tr>
  <tr>
    <th bgcolor="silver"> annotation Prefix </th>
    <td>  <get name="SITE:notesPrefix">&nbsp;</get>
    </td>
    <td> Location of annotation tree on this server.
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> keyword Prefix </th>
    <td>  <get name="SITE:wordPrefix">&nbsp;</get>
    </td>
    <td> Location of keyword index on this server.
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> source Prefix </th>
    <td>  <get name="SITE:sourcePrefix">&nbsp;</get>
    </td>
    <td> Location of source tree on this server.
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> source Suffix </th>
    <td>  <get name="SITE:sourceSuffix">&nbsp;</get>
    </td>
    <td> Location of notes relative to a source directory
    </td>
  </tr>
</table>


</blockquote>

<inc-footer cvsid="$Id: app-info.inc,v 1.4 2000-06-21 01:11:42 steve Exp $">
